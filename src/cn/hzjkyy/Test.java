package cn.hzjkyy;

import cn.hzjkyy.action.Action;
import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.PauseException;
import cn.hzjkyy.agent.RetryException;
import cn.hzjkyy.agent.StopException;
import cn.hzjkyy.agent.SuccessException;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.agent.UnloginException;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.User;
import cn.hzjkyy.tool.Log;

public class Test {
	public static void main(String[] args){
		boolean isTest = true;
		Plan plan = new Plan();
		plan.setSfzmhm("340811197811035817");
		plan.setPass("123456");
		
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

//		explorer.setCheckingMode(true);

		try{
			action.login();
//			action.sendYzm();
		} catch (RetryException | StopException | PauseException e) {
//		} catch (UnloginException ue){
//			
		}
//		System.exit(1);
		user.tpyzm = "6645";
		user.dxyzm = "734828";
		
		do {
			try{
				do {
					action.login();
					//获取考试信息
					if(exam == null || exam.ksdd != "3301007"){
						exam = action.detect();						
					}
						
					//预约
					if(exam != null){
						applicationLog.record("获取考试成功，预约考试");
						success = action.book(exam);						
					}else{
						applicationLog.record("获取考试失败");
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
				applicationLog.record("停止预约：" + se.getReason());
				break;
			} catch (SuccessException se) {
				success = true;
			}
		}while(!success);

	}

}
