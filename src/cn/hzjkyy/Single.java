package cn.hzjkyy;

import java.util.Random;

import cn.hzjkyy.action.Action;
import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.PlanClient;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.agent.UnloginException;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.User;
import cn.hzjkyy.tool.Log;

public class Single {
	public static void main(String[] args){
		//程序运行环境
		boolean isTest = false;
		boolean debug = false;
		
		if(debug){
//		    java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINER);
//		    java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINER);
//
//		    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//		    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//		    System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
//		    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
//		    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
		}

		PlanClient planClient = new PlanClient(isTest);
		Plan plan = new Plan();
		
		if(debug){
			plan.setId(1);
			plan.setKsdd("3301022");
			plan.setSfzmhm("332502199110174570");
			plan.setPass("520555");
			plan.setNumber(0);
			plan.setTotal(15);
		}else{
			plan = planClient.fetch();
		}
		
		//创建日志
		Log.init(plan, isTest ? 1 : 5000);
		Log applicationLog = Log.getLog("application");
		
		//初始化
		User user = new User(plan.getSfzmhm(), plan.getPass());
		Device device = new Device(plan.getToken());
		Explorer explorer = new Explorer();
		Tab mainTab = explorer.newTab();
		Action action = new Action(mainTab, user, device, plan, isTest);

		Exam exam = null;
		boolean success = false;
	    Random rand = new Random();
	    int randomNum = rand.nextInt(60000);
		long end = action.getTimestamp(9, 40) + randomNum;
		int number = plan.getNumber();
		int total = plan.getTotal();
		if(isTest){
			applicationLog.record("number:" + number);
			applicationLog.record("total:" + total);			
		}
		long offset = number * 20000 - Math.min(6, Math.max(number - 3, 0)) * 10000;
		long start = action.getTimestamp(8, 59) + offset;
		long circle = total * 20000 - Math.min(6, Math.max(total - 3, 0)) * 10000;

		do {
			try{
				//登录
				action.waitUntil(8, 55).login();

				//获取考试信息
				exam = action.waitUntil(start).detect();
					
				//预约
				if(exam != null){
					applicationLog.record("获取考试成功，预约考试");
					success = action.book(exam);						
				}else{
					applicationLog.record("获取考试失败");
				}
				
				start += circle;
			}catch(UnloginException ex){
				applicationLog.record("未登录错误");
			}
		}while(!success && start < end);
		
		planClient.report(plan, exam == null ? "" : exam.ksrq, success);
		action.close();
		explorer.close();
		applicationLog.close();
	}
	
}
