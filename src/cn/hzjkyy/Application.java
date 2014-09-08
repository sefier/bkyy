package cn.hzjkyy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hzjkyy.agent.AdvancedExplorer;
import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.PlanClient;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.User;
import cn.hzjkyy.tool.Log;

public class Application {
	private static Date today = new Date();
	public static void main(String[] args){
		//获取预约计划
		PlanClient planClient = new PlanClient();
		Plan plan = planClient.fetch();
		
		//组建基本环境
		Log applicationLog = Log.getLog(getLogName(plan, "application"));
		User user = new User(plan.getSfzmhm(), plan.getPass());
		Device device = new Device();
		Explorer explorer = new AdvancedExplorer(300000, 4);
		Tab mainTab = explorer.newTab();
	}

	private static DateFormat dayDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static String getLogName(Plan plan, String name) {
		return String.format("%s_%d_%s", dayDateFormat.format(today), plan.getId(), name);
	}
}
