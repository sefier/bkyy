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
		boolean isTest = true;
		Plan plan = new Plan();
		if(isTest){
		    java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINER);
		    java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINER);

		    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
		    System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "debug");
		    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "debug");
		    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "debug");
			plan.setId(1);
			plan.setKsdd("3301007");
			plan.setSfzmhm("330102199308100610");
			plan.setPass("408504");
		}else{
			PlanClient planClient = new PlanClient(isTest);
			plan = planClient.fetch();
		}
		
		//创建日志
		Log.init(plan, isTest ? 1 : 5000);
		Log applicationLog = Log.getLog("application");
		
		//初始化
		User user = new User(plan.getSfzmhm(), plan.getPass());
		Device device = new Device();
		Explorer explorer = new Explorer();
		Tab mainTab = explorer.newTab();
		Action action = new Action(mainTab, user, device, plan, isTest);

		Exam exam = null;
		boolean success = false;
	    Random rand = new Random();
	    int randomNum = rand.nextInt(60000);
		long end = action.getTimestamp(9, 40) + randomNum;

		do {
			try{
				//登录
				action.waitUntil(8, 55).login();
				do {
					do {
						//获取考试信息
						exam = action.waitUntil(8, 58).detect();
					}while(exam == null && System.currentTimeMillis() < end);
					
					//预约
					if(exam != null){
						success = action.book(exam);						
					}
				}while(!success && System.currentTimeMillis() < end);			
			}catch(UnloginException ex){
				applicationLog.record("未登录错误");
			}
		}while(!success && System.currentTimeMillis() < end);
		
		//planClient.report(plan, exam == null ? "" : exam.ksrq, success);
		action.close();
		explorer.close();
		applicationLog.close();
	}
	
}
