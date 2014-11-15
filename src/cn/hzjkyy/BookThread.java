package cn.hzjkyy;

import cn.hzjkyy.action.Action;
import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.NextException;
import cn.hzjkyy.agent.PauseException;
import cn.hzjkyy.agent.StopException;
import cn.hzjkyy.agent.PlanClient;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.agent.UnloginException;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.User;
import cn.hzjkyy.tool.Log;

public class BookThread extends Thread {
	private Plan plan;
	private PlanClient planClient;
	private boolean isTest;
	public BookThread(PlanClient planClient, Plan plan, boolean isTest){
		this.planClient = planClient;
		this.plan = plan;
		this.isTest = isTest;
	}
	
	public void run() {
		//创建日志
		Log.init(isTest ? 1 : 5000);
		Log applicationLog = Log.getLog(plan, "application");
		
		//初始化
		User user = new User(plan.getSfzmhm(), plan.getPass());
		Device device = new Device(null);
		Explorer explorer = new Explorer(plan);
		Tab mainTab = explorer.newTab();
		Action action = new Action(mainTab, user, device, plan, isTest);

		Exam exam = null;
		boolean success = false;
		String newPass = "201412";
		
//		Pattern p = Pattern.compile("(\\d{17})");
//		Matcher m = p.matcher(plan.getSfzmhm());
//		if (m.find()) {
//			String value = m.group(1);
//			int first = Integer.parseInt(value.substring(0, 6));
//			int middle = Integer.parseInt(value.substring(6, 12));
//			int last = Integer.parseInt(value.substring(12, 17));
//			int result = (first + middle + 1000000 - last) % 1000000;
//			newPass = "" + result;
//		}
		
		if(newPass != null){
			try {
				explorer.setCheckingMode(false);
				action.login();
				action.changePass(newPass);
			} catch (UnloginException | PauseException | StopException | NextException e) {
			}		
		}
		
		explorer.setCheckingMode(true);
		String ksrq = "";
		do {
			try{
				do {
			    	int status = Single.status();
			    	
			    	if(status == 1 || status == plan.getId()){
			    		break;
			    	}else if(status == 3){
			    		throw new StopException();
			    	}else{
			    		try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
						}   		
			    	}
				}while(true);

				do {
					action.login();
					//获取考试信息
					exam = action.detect();
						
					//预约
					if(exam != null){
						ksrq = exam.ksrq;
						applicationLog.record("获取考试成功，预约考试");
						success = action.book(exam);						
					}else{
						applicationLog.record("获取考试失败");
					}					
				}while(!success);
					
			}catch(UnloginException ex){
				applicationLog.record("未登录错误");
			}catch(PauseException pe){
			}catch(NextException ne){
				Single.setStatus(0);
			}catch(StopException se){
				break;
			}
		}while(!success);
		
//		try {
//			if(!success && newPass != null){
//				explorer.setCheckingMode(false);
//				action.changePass(plan.getPass());				
//			}
//		} catch (UnloginException | PauseException | StopException | NextException e) {
//		}
		
		planClient.report(plan, ksrq, success);
		action.close();
		explorer.close();
		applicationLog.close();
	}
}
