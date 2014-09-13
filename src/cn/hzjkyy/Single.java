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
		boolean isTest = false;
		
		//获取预约计划
		PlanClient planClient = new PlanClient(isTest);
		Plan plan = planClient.fetch();
		
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
		long end = action.getTimestamp(9, 40);

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
					success = action.book(exam);
				}while(!success && System.currentTimeMillis() < end);			
			}catch(UnloginException ex){
				applicationLog.record("未登录错误");
			}
		}while(!success && System.currentTimeMillis() < end);
		
		planClient.report(plan, exam.ksrq, success);
		action.close();
		explorer.close();
		applicationLog.close();
	}
	
}
