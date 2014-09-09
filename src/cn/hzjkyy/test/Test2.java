package cn.hzjkyy.test;

import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.generator.BookGenerator;
import cn.hzjkyy.generator.JlcGenerator;
import cn.hzjkyy.generator.LoginGenerator;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.model.User;
import cn.hzjkyy.parser.JlcParser;
import cn.hzjkyy.parser.LoginParser;
import cn.hzjkyy.tool.Log;

public class Test2 {
	public static void main(String[] args) {
		//组建基本环境
		Log applicationLog = Log.getLog("application" + "-" + "Test2");
		Explorer explorer = new Explorer("Test2");
		Tab mainTab = explorer.newTab();
		User user = new User("330822196901153318", "123456");
		Device device = new Device();
		
		//登录
		applicationLog.record("系统开始登录。");
		LoginGenerator loginGenerator = new LoginGenerator(user, device);
		Request loginRequest = loginGenerator.generate();
		LoginParser loginParser = new LoginParser();

		applicationLog.record("登录中...");
		do {
			Response response = mainTab.visit(loginRequest);
			if(response.getStatusPanel().isSuccess()){
				loginParser.parse(response.getResponseBody());					
			}			
		}while(!loginParser.getStatusPanel().isSuccess());
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
		
		while(System.currentTimeMillis() < 1410224460000L){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}

		//预约考试
		applicationLog.record("系统开始预约考试：");
		Exam exam = new Exam("51", "3301996", "2014-10-01");
		BookGenerator bookGenerator = new BookGenerator(user, jlc, exam);
		Request bookRequest = bookGenerator.generate();
		
		applicationLog.record("预约中...");
		Response response = mainTab.visit(bookRequest);
		if(response.getStatusPanel().isSuccess() && response.getResponseBody().contains("<code>1</code>")){
			applicationLog.record("预约考试成功！");
		}else{
			applicationLog.record("预约失败");
		}
		explorer.close();
		applicationLog.write();
		applicationLog.upload();
	}
}
