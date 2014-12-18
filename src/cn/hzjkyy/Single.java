package cn.hzjkyy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import cn.hzjkyy.agent.PlanClient;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.tool.Log;

public class Single {
	public static String programVersion = "1219";
	public static void main(String[] args){
		//程序运行环境
		boolean isTest = false;
		serverLog("程序启动，版本号：" + programVersion + (isTest ? "测试版" : "正式版"));

		PlanClient planClient = new PlanClient(isTest);
		
		//向服务器报到
		int serverId = -1;
		do {
			try{
				String world = planClient.hello();
				serverLog("向服务器报到，收到编号：" + world);
				serverId = Integer.parseInt(world);			
			}catch(NumberFormatException e){
				serverLog("编号解析异常");
			}			
		}while(serverId <= 0);
		serverLog("服务器报到结束，编号：" + serverId);
		
		//向服务器获取预约计划
		waitUntil(getTimestamp(8, 30) + (serverId % 100) * 1200);
		serverLog("向服务器获取预约计划");
		ArrayList<Plan> plans = new ArrayList<Plan>();
		do {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}

			serverLog("获取中...");
			plans = planClient.fetch(serverId);
		}while(plans.isEmpty());
		serverLog("我们共获取" + plans.size() + "个计划");
		
		for(Plan plan : plans){
			serverLog("启动计划" + plan.getId());
			(new BookThread(planClient, plan, isTest)).start();
		}
		
		int size = plans.size();
		waitUntil(getTimestamp(8, 35) + (serverId % 120) * 2 * 1000);
		do {
			int signal = planClient.over();
			serverLog("获取中心服务器信号：" + signal);
			
			if(signal == 1){//紧急状态
				status = 1;
			}else if(signal == 3){//停止状态
				status = 3;
				break;
			}else if(signal == 2 && status == 1){
				status = 0;
			}
			serverLog("生成服务器指令：" + status);
			
			for(int i = 0; i < 3; i++){
				reAssignStatus(plans, size);
				serverLog("重新生成服务器指令：" + status);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
			}
		}while(true);
	}
	
	public static void reAssignStatus(ArrayList<Plan> plans, int size){
		if(status != 1 && status != 3){
			if(status == 0 || (System.currentTimeMillis() - statusAssignAt > 20 * 60 * 1000) ||  overPlanIds.contains(status)){
				lastIndex = (lastIndex + 1) % size;
				statusAssignAt = System.currentTimeMillis();
				status = plans.get(lastIndex).getId();
			}
		}
	}
	
	public static void serverLog(String message){		
		System.out.println("[" + Log.dateFormat.format(new Date()) + "]" + message);
	}
	
	private static int status = 0;
	private static int lastIndex = -1;
	private static long statusAssignAt;
	private static Set<Integer> overPlanIds = new HashSet<Integer>();
	public static synchronized int status(){
		return status;
	}
	
	public static synchronized void setStatus(int serverStatus){
		 status = serverStatus;
	}
	
	public static synchronized void finishPlan(int planId){
		overPlanIds.add(planId);
	}

	public static void quit(){
		System.exit(0);
	}

	public static void waitUntil(long timestamp){
		while(System.currentTimeMillis() < timestamp){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}

	}

	public static void waitUntil(int hour, int minute) {
		long timestamp = getTimestamp(hour, minute);
		waitUntil(timestamp);
	}
	
	public static long getTimestamp(int hour, int minute) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
}
