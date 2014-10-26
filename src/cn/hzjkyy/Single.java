package cn.hzjkyy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cn.hzjkyy.agent.PlanClient;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.tool.Log;

public class Single {
	public static long endTimeStamp = getTimestamp(9, 15);
	public static String programVersion = "1027";
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
		waitUntil(getTimestamp(8, 47) + (serverId % 100) * 1200);
		serverLog("向服务器获取预约计划");
		ArrayList<Plan> plans = new ArrayList<Plan>();
		do {
			serverLog("获取中...");
			plans = planClient.fetch(serverId);
		}while(plans.isEmpty());
		serverLog("我们共获取" + plans.size() + "个计划");
		
		//将计划付诸于实施
		for(Plan plan : plans){
			serverLog("启动计划" + plan.getId());
			(new BookThread(planClient, plan, isTest)).start();
		}
	}
	
	public static void serverLog(String message){		
		System.out.println("[" + Log.dateFormat.format(new Date()) + "]" + message);
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
