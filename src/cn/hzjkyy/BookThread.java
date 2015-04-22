package cn.hzjkyy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.action.Action;
import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.PauseException;
import cn.hzjkyy.agent.RetryException;
import cn.hzjkyy.agent.StopException;
import cn.hzjkyy.agent.PlanClient;
import cn.hzjkyy.agent.SuccessException;
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
		Log.init(isTest ? 1 : 2);
		Log applicationLog = Log.getLog(plan, "application");
		
		//初始化
		User user = new User(plan.getSfzmhm(), plan.getPass());
		Device device = new Device(null);
		Explorer explorer = new Explorer(plan, 60 * 1000);
		Tab mainTab = explorer.newTab();
		Action action = new Action(mainTab, user, device, plan, isTest);

		Exam exam = null;
		boolean success = false;
		String newPass = "201504";

		if(newPass != null){
			try {
				explorer.setCheckingMode(false);
				action.login();
				action.changePass(newPass);
			} catch (UnloginException | RetryException | StopException | PauseException e) {
			}		
		}
		
		explorer.setCheckingMode(true);
		String ksrq = "";
		String reason = "";
		do {
			try{
				do {
			    	int status = Single.status();
			    	applicationLog.record("服务器指令" + status);
			    	
			    	if(status == 1 || status == plan.getId()){
			    		applicationLog.record("开始预约");
			    		break;
			    	}else if(status == 3){
			    		throw new StopException("中心服务器指示：3");
			    	}else{
			    		try {
			    			applicationLog.record("预约等待中");
							Thread.sleep(5000);
						} catch (InterruptedException e) {
						}   		
			    	}
				}while(true);

				action.login();
				long startDetect = System.currentTimeMillis();
				do {
					//获取考试信息
					exam = action.detect(planClient);
						
					//预约
					if(exam != null){
						try {
							int wait = exam.sysj;
							applicationLog.record("考试表示的剩余时间为：" + exam.sysj);
							applicationLog.record("累计等待" + wait);
							Thread.sleep(wait);
						} catch (InterruptedException e) {
						}

						ksrq = exam.ksrq;
						applicationLog.record("获取考试成功，预约考试");
						success = action.book(exam);		
					}else{
						applicationLog.record("获取考试失败");
						
						if(System.currentTimeMillis() - startDetect > 5 * 60 * 1000){
							try {
								Thread.sleep(10 * 1000);
							} catch (InterruptedException e) {
							}
							throw new RetryException("长时间未能获取到考试日期，重新进行循环，以避免超时问题");
						}
					}					
				}while(!success);
					
			}catch(UnloginException ex){
				applicationLog.record("未登录错误");
			}catch(RetryException pe){
				applicationLog.record("重新开始循环：" + pe.getReason());
			}catch(PauseException ne){
				if(Single.status() == plan.getId()){
					Single.setStatus(0);					
				}
				applicationLog.record("进入下一个账号：" + ne.getReason());
			}catch(StopException se){
				reason = se.getReason();
				applicationLog.record("停止预约：" + se.getReason());
				break;
			} catch (SuccessException se) {
				Pattern pattern = Pattern.compile(plan.getKsrqFormat());
				Matcher m = pattern.matcher(se.getKsrq());
				if(m.find()){
					success = true;
					ksrq = se.getKsrq();
				}else if(plan.seIncrease()){
					reason = "多次检查发现上一次预约记录无法清除";
					break;
				}
			}
		}while(!success);
				
		if(success){
			reason = "预约成功";
		}

		Single.finishPlan(plan.getId());

		boolean reportSuccess = false;
		do {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}

			applicationLog.record("汇报计划运行结果");
			reportSuccess = planClient.report(plan, ksrq, success, reason);
		}while(!reportSuccess);
		applicationLog.record("汇报成功");

		
		action.close();
		explorer.close();
		applicationLog.close();
	}
}
