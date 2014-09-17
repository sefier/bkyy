package cn.hzjkyy;

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
			plan.setSfzmhm("332502199204116216");
			plan.setPass("AnLu123");
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
		String newPass = "181745";

		try {
			action.login();
			action.changePass(newPass);
		} catch (UnloginException e1) {
		}

		action.waitUntil(8, 59);
		do {
			try{
				//登录
				action.login();
				action.front();

				//获取考试信息
				exam = action.detect();
					
				//预约
				if(exam != null){
					applicationLog.record("获取考试成功，预约考试");
					success = action.book(exam);						
				}else{
					applicationLog.record("获取考试失败");
				}
				
			}catch(UnloginException ex){
				applicationLog.record("未登录错误");
			}
		}while(!success && !planClient.over());
		
		try {
			action.changePass(plan.getPass());
		} catch (UnloginException e) {
		}
		
		planClient.report(plan, exam == null ? "" : exam.ksrq, success);
		action.close();
		explorer.close();
		applicationLog.close();
	}
	
}
