package cn.hzjkyy;

import cn.hzjkyy.agent.AdvancedExplorer;
import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.generator.BookGenerator;
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

public class SingleTry {
	public static void main(String[] args) throws InterruptedException {
		//组建基本环境
		Log applicationLog = Log.getTestLog("application");
		User user = new User("330781199111260221", "655754");
		Device device = new Device();
		Explorer explorer = new AdvancedExplorer(300000, 4);
		Tab mainTab = explorer.newTab();
//		
//		//登录
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
//		user.setXm("%E5%BC%A0%E6%A1%82%E6%9E%9D");
//		user.setToken("41630ACCB4CB9017CF22D7A000AF724B");
//		user.setSfzmmc("A");
//		
//		//获取教练车信息
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
//		user.setKskm("2");
		
		//获取考试信息
		applicationLog.record("系统开始获取考试信息：");
		ExamGenerator examGenerator = new ExamGenerator(user);
		Request examRequest = examGenerator.generate();
		ExamParser examParser = new ExamParser();
		
		do {
			applicationLog.record("获取考试...");
			Response response = mainTab.visit(examRequest);
			if(response.getStatusPanel().isSuccess()){
				examParser.parse(response.getResponseBody());
			}
		}while(!examParser.getStatusPanel().isSuccess());
		Exam exam = examParser.getExam();
		applicationLog.record("获取考试信息成功：" + exam.kscc + "," + exam.ksdd + "," + exam.ksrq);
		
		//预约考试
		//Exam exam = new Exam("51", "3301007", "2014-09-28");
		applicationLog.record("系统开始预约考试：");
//		String jlc = "%E6%B5%99A2291";
		BookGenerator bookGenerator = new BookGenerator(user, jlc, exam);
		Request bookRequest = bookGenerator.generate();
		
		do {
			applicationLog.record("预约中...");
			Response response = mainTab.visit(bookRequest);
			if(response.getStatusPanel().isSuccess() && response.getResponseBody().contains("<code>1</code>")){
				break;
			}
			
			explorer.newTab().visit(bookRequest, true);
		}while(true);
		applicationLog.record("预约考试成功！");
		
		explorer.close();
		applicationLog.write();
	}
	
	public static void record(long start){
		System.out.println("耗时：" + String.format("%,d", System.nanoTime() - start));
	}
}
