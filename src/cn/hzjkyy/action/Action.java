package cn.hzjkyy.action;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.agent.PauseException;
import cn.hzjkyy.agent.RetryException;
import cn.hzjkyy.agent.StopException;
import cn.hzjkyy.agent.SuccessException;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.agent.UnloginException;
import cn.hzjkyy.generator.BookGenerator;
import cn.hzjkyy.generator.ExamGenerator;
import cn.hzjkyy.generator.FrontGenerator;
import cn.hzjkyy.generator.IdentityGenerator;
import cn.hzjkyy.generator.JlcGenerator;
import cn.hzjkyy.generator.LoginGenerator;
import cn.hzjkyy.generator.LoginVerifyGenerator;
import cn.hzjkyy.generator.ModifyGenerator;
import cn.hzjkyy.generator.TpyzmGenerator;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.model.User;
import cn.hzjkyy.parser.ExamParser;
import cn.hzjkyy.parser.FrontParser;
import cn.hzjkyy.parser.JlcParser;
import cn.hzjkyy.parser.LoginParser;
import cn.hzjkyy.parser.LoginVerifyParser;
import cn.hzjkyy.parser.ModifyParser;
import cn.hzjkyy.parser.TpyzmParser;
import cn.hzjkyy.tool.Log;

public class Action {
	private Tab tab;
	private User user;
	private Device device;
	private Plan plan;
	private boolean isTest;
	protected Log actionLog; 
	public void close() {
		actionLog.close();
	}
	public Action waitUntil(long timestamp){
		actionLog.record("等待至：" + Log.dateFormat.format(new Date(timestamp)));
		if(!isTest){
			while(System.currentTimeMillis() < timestamp){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}	
		}

		return this;
	}
	public Action waitUntil(int hour, int minute) {
		long timestamp = getTimestamp(hour, minute);
		return waitUntil(timestamp);
	}
	
	public long getTimestamp(int hour, int minute) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	public Action(Tab tab, User user, Device device, Plan plan, boolean isTest){
		this.tab = tab;
		this.user = user;
		this.device = device;
		this.plan = plan;
		this.isTest = isTest;
		actionLog = Log.getLog(plan, "action");
	}
	
	public void changePass(String newPass) throws UnloginException, RetryException, StopException, PauseException{
		actionLog.record("更改密码：" + user.getPass() + "=>" + newPass);
		ModifyGenerator modifyGenerator = new ModifyGenerator(user, newPass);
		Request modifyRequest = modifyGenerator.generate();
		ModifyParser modifyParser = new ModifyParser();
		
		do {
			actionLog.record("更改密码中...");
			Response response = tab.visit(modifyRequest);
			
			if(response.getStatusPanel().isSuccess()){
				modifyParser.parse(response.getResponseBody());
			}
		}while(!modifyParser.getStatusPanel().isSuccess());
		
		actionLog.record("更改密码成功");
		user.setPass(newPass);
	}
	
	public void login() throws RetryException, StopException, PauseException {
		//登录
		actionLog.record("登录：");
		LoginGenerator loginGenerator = new LoginGenerator(user, device);
		Request loginRequest = loginGenerator.generate();
		LoginParser loginParser = new LoginParser();

		do {
			actionLog.record("登录中...");
			Response response;
			try {
				response = tab.visit(loginRequest);
				if(response.getStatusPanel().isSuccess()){
					loginParser.parse(response.getResponseBody());					
				}
			} catch (UnloginException e) {
			}
		} while(!loginParser.getStatusPanel().isSuccess());
		actionLog.record("登录成功");
		user.setXm(loginParser.getXm());
		user.setToken(loginParser.getToken());
		user.setSfzmmc(loginParser.getSfzmmc());
	}
	
	public void front() throws UnloginException, RetryException, StopException, PauseException {
		//首页
		actionLog.record("获取首页");
		FrontGenerator frontGenerator = new FrontGenerator(user);
		Request frontRequest = frontGenerator.generate();
		FrontParser frontParser = new FrontParser();
		do {
			actionLog.record("获取首页中...");
			Response response = tab.visit(frontRequest);
			if(response.getStatusPanel().isSuccess()){
				frontParser.parse(response.getResponseBody());					
			}
			
		} while(!frontParser.getStatusPanel().isSuccess());
		actionLog.record("首页获取成功");

		
		//登录验证
		actionLog.record("进行登录验证");
		LoginVerifyGenerator loginVerifyGenerator = new LoginVerifyGenerator(user);
		Request loginVerifyRequest = loginVerifyGenerator.generate();
		LoginVerifyParser loginVerifyParser = new LoginVerifyParser();
		do {
			actionLog.record("登录验证中...");
			Response response = tab.visit(loginVerifyRequest);
			if(response.getStatusPanel().isSuccess()){
				loginVerifyParser.parse(response.getResponseBody());					
			}
		} while(!loginVerifyParser.getStatusPanel().isSuccess());
		actionLog.record("登录验证成功");

		//身份验证
		actionLog.record("进行身份验证");
		IdentityGenerator identityGenerator = new IdentityGenerator(user);
		Request identityRequest = identityGenerator.generate();
		LoginVerifyParser identityParser = new LoginVerifyParser();
		do {
			actionLog.record("身份验证中...");
			Response response = tab.visit(identityRequest);
			if(response.getStatusPanel().isSuccess()){
				identityParser.parse(response.getResponseBody());					
			}
		} while(!identityParser.getStatusPanel().isSuccess());
		actionLog.record("身份验证成功");		
	}
	
	public Exam detect() throws UnloginException, RetryException, StopException, PauseException, SuccessException {
//		actionLog.record("进行同意操作");
//		
//		AgreeGenerator agreeGenerator = new AgreeGenerator(user);
//		Request agreeRequest = agreeGenerator.generate();
//		LoginVerifyParser agreeParser = new LoginVerifyParser();
//		
//		do {
//			actionLog.record("同意中...");
//			Response response = tab.visit(agreeRequest);
//			if(response.getStatusPanel().isSuccess()){
//				agreeParser.parse(response.getResponseBody());					
//			}			
//		}while(!agreeParser.getStatusPanel().isSuccess());
//		actionLog.record("同意操作成功");

		//获取图片验证码
		actionLog.record("系统开始获取图片验证码");
		TpyzmGenerator tpyzmGenerator = new TpyzmGenerator(user);
		Request tpyzmRequest = tpyzmGenerator.generate();
		TpyzmParser tpyzmParser = new TpyzmParser();
		
		do {
			actionLog.record("获取图片验证码...");
			Response response = tab.visit(tpyzmRequest);
			if(response.getStatusPanel().isSuccess()){
				tpyzmParser.parse(response.getResponseBody());
			}
		}while(!tpyzmParser.getStatusPanel().isSuccess());
		user.setTpyzm(tpyzmParser.getTpyzm());
		
		//获取考试流水
		actionLog.record("系统开始获取考试流水。");
		JlcGenerator jlcGenerator = new JlcGenerator(user);
		Request jlcRequest = jlcGenerator.generate();
		JlcParser jlcParser = new JlcParser();
		
		do {
			actionLog.record("获取考试流水...");
			Response response = tab.visit(jlcRequest);
			if(response.getStatusPanel().isSuccess()){
				if(response.getResponseBody().contains("不能重复预约")){
					Pattern ksrqPattern = Pattern.compile("2014-\\d+-\\d+");
					Matcher m = ksrqPattern.matcher(response.getResponseBody());
					throw new SuccessException(m.find() ? m.group() : "2014-12-06");
				}
				jlcParser.parse(response.getResponseBody());
			}
		}while(!jlcParser.getStatusPanel().isSuccess());		
		String jlc = jlcParser.getJlcs()[0];
		String kskm = jlcParser.getKskm();
		
		
		user.setKskm(kskm);
		user.setJlc(jlc);
		actionLog.record("获取考试流水成功：");
		
		//获取考试信息
		actionLog.record("系统开始获取考试信息：");
		ExamGenerator examGenerator = new ExamGenerator(user);
		Request examRequest = examGenerator.generate();
		ExamParser examParser = new ExamParser(plan);
		
		do{
			actionLog.record("获取考试信息...");
			Response response = tab.visit(examRequest);
			if(response.getStatusPanel().isSuccess()){
				examParser.parse(response.getResponseBody());
			}			
		}while(!examParser.getStatusPanel().isSuccess());
		
		return examParser.getExam();
	}
	
	//预约
	public boolean book(Exam exam) throws UnloginException, RetryException, StopException, PauseException, SuccessException {
		actionLog.record("开始预约考试：");
		BookGenerator bookGenerator = new BookGenerator(user, exam);
		Request bookRequest = bookGenerator.generate();

		Response response = tab.visit(bookRequest);
		
		if(response.getResponseBody().contains("重复预约")){
			Pattern ksrqPattern = Pattern.compile("2014-\\d+-\\d+");
			Matcher m = ksrqPattern.matcher(response.getResponseBody());
			throw new SuccessException(m.find() ? m.group() : "2014-12-06");
		}

		if(response.getStatusPanel().isSuccess() && (response.getResponseBody().contains("您已预约成功"))){
			actionLog.record("预约考试成功！");
			return true;
		}
		
		return false;
	}
}
