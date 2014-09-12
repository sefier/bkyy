package cn.hzjkyy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.agent.AdvancedExplorer;
import cn.hzjkyy.agent.PlanClient;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.generator.BookGenerator;
import cn.hzjkyy.generator.ExamGenerator;
import cn.hzjkyy.generator.LoginGenerator;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.model.User;
import cn.hzjkyy.parser.LoginParser;
import cn.hzjkyy.tool.Log;
import cn.hzjkyy.tool.OcsClient;

public class Application {
	public static final boolean isTest = false;
	public static void main(String[] args){
		//获取预约计划
		PlanClient planClient = new PlanClient(isTest);
		Plan plan = planClient.fetch();
		
		//创建日志
		Log.init(plan, isTest ? 1 : 5000);
		Log applicationLog = Log.getLog("application");
		
		//初始化
		User user = new User(plan.getSfzmhm(), plan.getPass());
		user.setKskm(plan.getKskm());
		Device device = new Device();
		AdvancedExplorer explorer = new AdvancedExplorer(300000, 4);
		Tab mainTab = explorer.newTab();
		
		//登录
		applicationLog.record("登录：");
		LoginGenerator loginGenerator = new LoginGenerator(user, device);
		Request loginRequest = loginGenerator.generate();
		LoginParser loginParser = new LoginParser();

		do {
			applicationLog.record("登录中...");
			Response response = mainTab.visit(loginRequest);
			if(response.getStatusPanel().isSuccess()){
				loginParser.parse(response.getResponseBody());					
			}
		} while(!loginParser.getStatusPanel().isSuccess());
		user.setXm(loginParser.getXm());
		user.setToken(loginParser.getToken());
		user.setSfzmmc(loginParser.getSfzmmc());
		applicationLog.record("登录成功，姓名：" + loginParser.getXm() + "，密钥：" + loginParser.getToken());
		
		long start = getStart();
		long end = getEnd();
		int interval = 60000 / plan.getTotal();
		
		while(System.currentTimeMillis() < start){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

		//获取考试日期
		String month = isTest ? "09" : "10";
		Pattern ksrqPattern = Pattern.compile("<ksrq>(2014-" + month + "-.+?)</ksrq>");
		String ksrq = getKsrq(plan.getKskm());
		long current = System.currentTimeMillis();
		long lastPosition = -1;
		List<Tab> examTabs = new ArrayList<Tab>();
		while(ksrq.isEmpty() && current < end){
			long currentPosition = (current - start) / interval;
			if((currentPosition != lastPosition) && (currentPosition % plan.getTotal() == plan.getNumber())){
				lastPosition = currentPosition;
				applicationLog.record("开始获取考试日期：");
				ExamGenerator examGenerator = new ExamGenerator(user);
				Request examRequest = examGenerator.generate();
				examTabs.add(explorer.newTab().visit(examRequest, true));
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			Iterator<Tab> it = examTabs.iterator();
			while (it.hasNext()) {
				Tab tab = it.next();

				if(tab.isOver()){
					Response response = tab.getResponse();
					if(response.getStatusPanel().isSuccess()){
						Matcher m = ksrqPattern.matcher(response.getResponseBody());
						if (m.find()) {
							ksrq = m.group(1);
							applicationLog.record("获取考试日期成功：" + ksrq);
							setKsrq(ksrq, plan.getKskm());
						}
					}
					tab.close();
				    it.remove();
				}
			}
			
			if(ksrq.isEmpty()){
				ksrq = getKsrq(plan.getKskm());
				current = System.currentTimeMillis();
			}
		}
		
		boolean success = false;
		if(!ksrq.isEmpty()){
			//预约考试
			Exam exam = new Exam("51", plan.getKsdd(), ksrq);
			applicationLog.record("开始预约考试：");
			BookGenerator bookGenerator = new BookGenerator(user, plan.getJlc(), exam);
			Request bookRequest = bookGenerator.generate();
			do {
				applicationLog.record("预约中...");
				Response response = mainTab.visit(bookRequest);
				if(response.getStatusPanel().isSuccess() && response.getResponseBody().contains("您已预约成功")){
					applicationLog.record("预约考试成功！");
					success = true;
					break;
				}else{
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}while(System.currentTimeMillis() < end);
		}
		
		planClient.report(plan, ksrq, success);
		explorer.close();
		applicationLog.close();
		OcsClient.close();
	}
	
	//开始检查考试计划的时间
	private static long getStart() {
		Calendar calendar = new GregorianCalendar();
		if(!isTest){
			calendar.set(Calendar.HOUR_OF_DAY, 8);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 50);
			calendar.set(Calendar.MILLISECOND, 0);			
		}
		return calendar.getTimeInMillis();
	}
	
	//结束程序的时间
	private static long getEnd() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 40);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	//查看是否已经开始放号
	private static String getKsrq(String kskm) {
		if(isTest){
			return "";			
		}else{
			Object ksrq = OcsClient.get("km" + kskm);
			if(ksrq != null){
				return ksrq.toString();
			}else{
				return "";
			}					
		}
	}
	
	//将已经开始放的号，推送到服务器上
	private static void setKsrq(String ksrq, String kskm){
		if(!isTest){
			OcsClient.set("km" + kskm, ksrq);			
		}
	}
}
