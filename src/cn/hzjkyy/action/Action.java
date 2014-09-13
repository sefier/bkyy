package cn.hzjkyy.action;

import java.util.Calendar;
import java.util.GregorianCalendar;

import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.agent.UnloginException;
import cn.hzjkyy.generator.AgreeGenerator;
import cn.hzjkyy.generator.BookGenerator;
import cn.hzjkyy.generator.ExamGenerator;
import cn.hzjkyy.generator.IdentityGenerator;
import cn.hzjkyy.generator.JlcGenerator;
import cn.hzjkyy.generator.LoginGenerator;
import cn.hzjkyy.generator.LoginVerifyGenerator;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.model.User;
import cn.hzjkyy.parser.ExamParser;
import cn.hzjkyy.parser.JlcParser;
import cn.hzjkyy.parser.LoginParser;
import cn.hzjkyy.parser.LoginVerifyParser;
import cn.hzjkyy.tool.Log;

public class Action {
	private Tab tab;
	private User user;
	private Device device;
	private Plan plan;
	private boolean isTest;
	protected Log actionLog = Log.getLog("action"); 
	public void close() {
		actionLog.close();
	}
	public Action waitUntil(int hour, int minute) {
		if(!isTest){
			long prepare = getTimestamp(hour, minute);
			while(System.currentTimeMillis() < prepare){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}			
		}
		
		return this;
	}
	
	public long getTimestamp(int hour, int minute) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	public Action(Tab tab, User user, Device device, Plan plan, boolean isTest){
		this.tab = tab;
		this.user = user;
		this.device = device;
		this.plan = plan;
		this.isTest = isTest;
	}
	
	public void login() throws UnloginException {
		//登录
		actionLog.record("登录：");
		LoginGenerator loginGenerator = new LoginGenerator(user, device);
		Request loginRequest = loginGenerator.generate();
		LoginParser loginParser = new LoginParser();

		do {
			actionLog.record("登录中...");
			Response response = tab.visit(loginRequest);
			if(response.getStatusPanel().isSuccess()){
				loginParser.parse(response.getResponseBody());					
			}
		} while(!loginParser.getStatusPanel().isSuccess());
		actionLog.record("登录成功");
		user.setXm(loginParser.getXm());
		user.setToken(loginParser.getToken());
		user.setSfzmmc(loginParser.getSfzmmc());
		
		//登录验证
		actionLog.record("进行登录验证");
		LoginVerifyGenerator loginVerifyGenerator = new LoginVerifyGenerator(user);
		Request loginVerifyRequest = loginVerifyGenerator.generate();
		LoginVerifyParser loginVerifyParser = new LoginVerifyParser();
		do {
			actionLog.record("登录验证中...");
			Response response = tab.visit(loginVerifyRequest);
			if(response.getStatusPanel().isSuccess()){
				loginVerifyParser.parse(response.getResponseBody());					
			}
		} while(!loginVerifyParser.getStatusPanel().isSuccess());
		actionLog.record("登录验证成功");

		//登录验证
		actionLog.record("进行身份验证");
		IdentityGenerator identityGenerator = new IdentityGenerator(user);
		Request identityRequest = identityGenerator.generate();
		LoginVerifyParser identityParser = new LoginVerifyParser();
		do {
			actionLog.record("身份验证中...");
			Response response = tab.visit(identityRequest);
			if(response.getStatusPanel().isSuccess()){
				identityParser.parse(response.getResponseBody());					
			}
		} while(!identityParser.getStatusPanel().isSuccess());
		actionLog.record("身份验证成功");
	}
	
	public Exam detect() throws UnloginException {
		actionLog.record("进行同意操作");
		AgreeGenerator agreeGenerator = new AgreeGenerator(user);
		Request agreeRequest = agreeGenerator.generate();
		LoginVerifyParser agreeParser = new LoginVerifyParser();
		do {
			actionLog.record("同意中...");
			Response response = tab.visit(agreeRequest);
			if(response.getStatusPanel().isSuccess()){
				agreeParser.parse(response.getResponseBody());					
			}
		} while(!agreeParser.getStatusPanel().isSuccess());
		actionLog.record("同意操作成功");

		//获取考试流水
		actionLog.record("系统开始获取考试流水。");
		JlcGenerator jlkGenerator = new JlcGenerator(user);
		Request jlkRequest = jlkGenerator.generate();
		JlcParser jlcParser = new JlcParser();
		
		do {
			actionLog.record("获取考试流水...");
			Response response = tab.visit(jlkRequest);
			if(response.getStatusPanel().isSuccess()){
				jlcParser.parse(response.getResponseBody());
			}
		}while(!jlcParser.getStatusPanel().isSuccess());
		String jlc = jlcParser.getJlcs()[0];
		String kskm = jlcParser.getKskm();
		user.setKskm(kskm);
		user.setJlc(jlc);
		actionLog.record("获取考试流水成功：");
		
		//获取考试信息
		actionLog.record("系统开始获取考试信息：");
		ExamGenerator examGenerator = new ExamGenerator(user);
		Request examRequest = examGenerator.generate();
		ExamParser examParser = new ExamParser(plan);
		
		actionLog.record("获取考试信息...");
		Response response = tab.visit(examRequest);
		if(response.getStatusPanel().isSuccess()){
			examParser.parse(response.getResponseBody());
		}
		Exam exam = null;
		if(examParser.getStatusPanel().isSuccess()){
			exam = examParser.getExam();
			actionLog.record("获取考试信息成功");
		}else{
			actionLog.record("获取考试信息失败");
		}
		
		return exam;
	}
	
	//预约
	public boolean book(Exam exam) throws UnloginException {
		actionLog.record("开始预约考试：");
		BookGenerator bookGenerator = new BookGenerator(user, exam);
		Request bookRequest = bookGenerator.generate();

		Response response = tab.visit(bookRequest);

		if(response.getStatusPanel().isSuccess() && response.getResponseBody().contains("您已预约成功")){
			actionLog.record("预约考试成功！");
			return true;
		}
		
		return false;
	}
}
