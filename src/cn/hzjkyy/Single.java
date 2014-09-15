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
		boolean isTest = true;
		boolean debug = true;
		
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
		if(plan.getKsdd() == "3301007"){
			explorer.setLimits(10);
		}
		Tab mainTab = explorer.newTab();
		Action action = new Action(mainTab, user, device, plan, isTest);

		Exam exam = null;
		Exam fakeExam = new Exam("51", plan.getKsdd(), "2014-10-11");
		boolean success = false;
	    Random rand = new Random();
	    int randomNum = rand.nextInt(60000);
		long end = action.getTimestamp(13, 40) + randomNum;

		do {
			try{
				//登录
				action.waitUntil(8, 20).login();
				do {
					//获取考试信息
					exam = action.waitUntil(8, 25).detect();
					
					//预约
					if(exam != null){
						applicationLog.record("获取考试成功，预约考试");
						success = action.book(exam);						
					}else if(user.getKskm() != null){
						applicationLog.record("获取考试失败，盲预约");
						success = action.book(fakeExam);
					}
				}while(!success && System.currentTimeMillis() < end);			
			}catch(UnloginException ex){
				applicationLog.record("未登录错误");
			}
		}while(!success && System.currentTimeMillis() < end);
		
		planClient.report(plan, exam == null ? "" : exam.ksrq, success);
		action.close();
		explorer.close();
		applicationLog.close();
	}
	
}
