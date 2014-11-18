package cn.hzjkyy;

import java.util.ArrayList;

import cn.hzjkyy.action.Action;
import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.PauseException;
import cn.hzjkyy.agent.RetryException;
import cn.hzjkyy.agent.StopException;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.agent.UnloginException;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.User;
import cn.hzjkyy.tool.Log;

public class Test {
	public static void main(String[] args){
		boolean isTest = true;
		ArrayList<Plan> plans = new ArrayList<Plan>();
		
		Plan p = new Plan();
		p.setSfzmhm("330123197106195327");
		p.setPass("553280");
		plans.add(p);
		
		Plan p1 = new Plan();
		p1.setSfzmhm("35072319931104171X");
		p1.setPass("814360");
		plans.add(p1);
		
		Plan p2 = new Plan();
		p2.setSfzmhm("330122197608292526");
		p2.setPass("600372");
		plans.add(p2);

		Plan p3 = new Plan();
		p3.setSfzmhm("362323197409292122");
		p3.setPass("497841");
		plans.add(p3);

		for(Plan plan : plans){
			
			//创建日志
			Log.init(isTest ? 1 : 5000);
			
			//初始化
			User user = new User(plan.getSfzmhm(), plan.getPass());
			Device device = new Device(null);
			Explorer explorer = new Explorer(plan);
			Tab mainTab = explorer.newTab();
			Action action = new Action(mainTab, user, device, plan, isTest);

//			explorer.setCheckingMode(true);

			try{
				action.login();
				action.sendYzm();
			} catch (RetryException | StopException | PauseException e) {
			} catch (UnloginException ue){
//				
			}						
		}
	}

}
