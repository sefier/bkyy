package cn.hzjkyy.test;

import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.generator.ExamGenerator;
import cn.hzjkyy.generator.JlcGenerator;
import cn.hzjkyy.generator.LoginGenerator;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.model.User;
import cn.hzjkyy.parser.ExamParser;
import cn.hzjkyy.parser.JlcParser;
import cn.hzjkyy.parser.LoginParser;
import cn.hzjkyy.tool.Log;

public class Test4 {
	public static void main(String[] args) throws InterruptedException {
		//组建基本环境
		Log applicationLog = Log.getLog("application" + "-" + "Test4");
		Explorer explorer = new Explorer("Test4");
		User user = new User("37092119900918245X", "123456");
		Device device = new Device();
		Tab mainTab = explorer.newTab();

		//登录
		applicationLog.record("系统开始登录。");
		LoginGenerator loginGenerator = new LoginGenerator(user, device);
		Request loginRequest = loginGenerator.generate();
		LoginParser loginParser = new LoginParser();

		do {
			applicationLog.record("登录中...");
			Response response = mainTab.visit(loginRequest);
			if(response.getStatusPanel().isSuccess()){
				loginParser.parse(response.getResponseBody());					
			}
		} while(!loginParser.getStatusPanel().isSuccess());
		user.setXm(loginParser.getXm());
		user.setToken(loginParser.getToken());
		user.setSfzmmc(loginParser.getSfzmmc());
		applicationLog.record("系统登录成功，姓名：" + loginParser.getXm() + "，密钥：" + loginParser.getToken());

		//获取教练车信息
		applicationLog.record("系统开始获取教练车。");
		JlcGenerator jlkGenerator = new JlcGenerator(user);
		Request jlkRequest = jlkGenerator.generate();
		JlcParser jlcParser = new JlcParser();
		
		do {
			applicationLog.record("获取教练车...");
			Response response = mainTab.visit(jlkRequest);
			if(response.getStatusPanel().isSuccess()){
				jlcParser.parse(response.getResponseBody());
			}
		}while(!jlcParser.getStatusPanel().isSuccess());
		String jlc = jlcParser.getJlcs()[0];
		String kskm = jlcParser.getKskm();
		user.setKskm(kskm);
		applicationLog.record("获取教练车成功：" + jlc + ",考试科目：" + kskm);

		//获取考试信息
		applicationLog.record("系统开始获取考试信息：");
		while(System.currentTimeMillis() < 1410138600000L){
			ExamGenerator examGenerator = new ExamGenerator(user);
			Request examRequest = examGenerator.generate();
			ExamParser examParser = new ExamParser();
			
			applicationLog.record("获取考试...");
			Response response = mainTab.visit(examRequest);
			if(response.getStatusPanel().isSuccess()){
				examParser.parse(response.getResponseBody());
			}
			if(!examParser.getStatusPanel().isSuccess()){
				applicationLog.record("获取考试失败");
			}else{
				Exam exam = examParser.getExam();
				applicationLog.record("获取考试信息成功：" + exam.kscc + "," + exam.ksdd + "," + exam.ksrq);
				break;
			}

			//登录
			applicationLog.record("系统开始登录。");
			loginGenerator = new LoginGenerator(user, device);
			loginRequest = loginGenerator.generate();
			loginParser = new LoginParser();

			do {
				applicationLog.record("登录中...");
				response = mainTab.visit(loginRequest);
				if(response.getStatusPanel().isSuccess()){
					loginParser.parse(response.getResponseBody());					
				}
			} while(!loginParser.getStatusPanel().isSuccess());
			user.setXm(loginParser.getXm());
			user.setToken(loginParser.getToken());
			user.setSfzmmc(loginParser.getSfzmmc());
			applicationLog.record("系统登录成功，姓名：" + loginParser.getXm() + "，密钥：" + loginParser.getToken());
			
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
			}
		}
		
		explorer.close();
		applicationLog.write();
		applicationLog.upload();
	}
}
