package cn.hzjkyy.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.Single;
import cn.hzjkyy.agent.PauseException;
import cn.hzjkyy.agent.PlanClient;
import cn.hzjkyy.agent.RetryException;
import cn.hzjkyy.agent.StopException;
import cn.hzjkyy.agent.SuccessException;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.agent.UnloginException;
import cn.hzjkyy.generator.AgreeGenerator;
import cn.hzjkyy.generator.BookGenerator;
import cn.hzjkyy.generator.ExamGenerator;
import cn.hzjkyy.generator.FrontGenerator;
import cn.hzjkyy.generator.IdentityGenerator;
import cn.hzjkyy.generator.JlcGenerator;
import cn.hzjkyy.generator.LoginGenerator;
import cn.hzjkyy.generator.LoginVerifyGenerator;
import cn.hzjkyy.generator.ModifyGenerator;
import cn.hzjkyy.generator.SendGenerator;
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
import cn.hzjkyy.parser.SendParser;
import cn.hzjkyy.parser.TpyzmParser;
import cn.hzjkyy.shot.Army;
import cn.hzjkyy.shot.Shooter;
import cn.hzjkyy.tool.Log;
import cn.hzjkyy.tool.YzmDecoder;

public class Action {
	private Tab tab;
	private User user;
	private Device device;
	private Plan plan;
	private boolean isTest;
	protected Log actionLog;
	private YzmDecoder yzmDecoder;
	private Set<String> oldYzms = new HashSet<String>();
	public void close() {
		actionLog.close();
	}
	public int getOffset(){
		return tab.getExplorer().getOffset();
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
	
	public long getTimestamp(int hour, int minute, int second) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();		
	}
	
	public Action(Tab tab, User user, Device device, Plan plan, boolean isTest){
		this.tab = tab;
		this.user = user;
		this.device = device;
		this.plan = plan;
		this.isTest = isTest;
		this.yzmDecoder = new YzmDecoder();
		actionLog = Log.getLog(plan, "action");		
	}
	
	public void calculateShot() {
		actionLog.record("计算精准射击时间");
//		long waitToQuery = System.currentTimeMillis() + 120 * 1000;
		long waitToQuery = getTimestamp(0, 28, 0);
		int windows = plan.getWindow();
		startQuery = waitToQuery + windows;
		endQuery = startQuery + 2 * 60 * 1000;
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
	
	public void sendYzm() throws UnloginException, RetryException, StopException, PauseException {
		actionLog.record("发送验证码");
		SendGenerator sendGenerator = new SendGenerator(user);
		Request sendRequest = sendGenerator.generate();
		SendParser sendParser = new SendParser();

		do {
			actionLog.record("发送验证码");
			Response response = tab.visit(sendRequest);

			if(response.getStatusPanel().isSuccess()){
				sendParser.parse(response.getResponseBody());
			}
		}while(!sendParser.getStatusPanel().isSuccess());
		lastSendAt = System.currentTimeMillis();
		
	}
	
	public void agree() throws UnloginException, RetryException, StopException, PauseException {
		actionLog.record("进行同意操作");
		
		AgreeGenerator agreeGenerator = new AgreeGenerator(user);
		Request agreeRequest = agreeGenerator.generate();
		LoginVerifyParser agreeParser = new LoginVerifyParser();
		
		do {
			actionLog.record("同意中...");
			Response response = tab.visit(agreeRequest);
			if(response.getStatusPanel().isSuccess()){
				agreeParser.parse(response.getResponseBody());					
			}			
		}while(!agreeParser.getStatusPanel().isSuccess());
		actionLog.record("同意操作成功");		
	}
	public boolean shooting = false;
	private long lastSendAt = 0;
	public Exam detect(PlanClient planClient) throws UnloginException, RetryException, StopException, PauseException, SuccessException {
		//获取图片验证码和短信验证码
		TpyzmParser tpyzmParser = new TpyzmParser(yzmDecoder);
		if(user.getDxyzm() == null || user.getDxyzm().isEmpty() || user.getTpyzm() == null || user.getTpyzm().isEmpty()){
			while(true){
				int serverStatus = Single.status();
				if(serverStatus == 3){
					throw new StopException("收集短信验证码阶段，服务器指示：3");
				}

				if(user.getDxyzm() == null || user.getDxyzm().isEmpty()){
					String dxYzm = planClient.yzmQuery(plan);
					if(dxYzm != null && dxYzm.length() == 6 && !oldYzms.contains(dxYzm)){
						user.setDxyzm(dxYzm);
					}else if(System.currentTimeMillis() - 30 * 60 * 1000 > lastSendAt){
						sendYzm();
					}
				}				
				
				if(user.getTpyzm() == null || user.getTpyzm().equals("")){
					actionLog.record("系统开始获取图片验证码");
					TpyzmGenerator tpyzmGenerator = new TpyzmGenerator(user);
					Request tpyzmRequest = tpyzmGenerator.generate();
					
					do {
						actionLog.record("获取图片验证码...");
						Response response = tab.visit(tpyzmRequest);
						if(response.getStatusPanel().isSuccess()){
							user.setTpyzm(null);
							tpyzmParser.parse(response.getResponseBody());
						}
					}while(!tpyzmParser.getStatusPanel().isSuccess());
					user.setTpyzm(tpyzmParser.getTpyzm());			
				}

				if(user.getDxyzm() == null || user.getDxyzm().isEmpty()){
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
					}
				}else{
					break;
				}
			}			
		}

		//获取考试流水要等待
		shooting = false;
		if(!hasShotten && user.getJlc() != null){
			if(startQuery == 0){
				calculateShot();
			}
			long startStamp = startQuery - 80 * 1000 + plan.getId() % 20 * 1000;
			if(System.currentTimeMillis() > (startStamp - 60 * 1000) && System.currentTimeMillis() < startStamp){
				preShot();
				waitUntil(startStamp);
				shooting = true;
			}
		}
		
		//获取考试流水
		actionLog.record("系统开始获取考试流水。");
		JlcGenerator jlcGenerator = new JlcGenerator(user);
		Request jlcRequest = jlcGenerator.generate();
		JlcParser jlcParser = new JlcParser();
		
		long wait = 37000;
		long startJlc = System.currentTimeMillis();
		int retryLimitMinutes = shooting ? 2 : 5;
		
		do {
			actionLog.record("获取考试流水...");
			startJlc = System.currentTimeMillis();
			Response response = tab.visit(jlcRequest, retryLimitMinutes);
			
			if(response.getStatusPanel().isSuccess()){
				if(response.getResponseBody().contains("不能重复预约")){
					Pattern ksrqPattern = Pattern.compile("201\\d-\\d+-\\d+");
					Matcher m = ksrqPattern.matcher(response.getResponseBody());
					throw new SuccessException(m.find() ? m.group() : "2015-01-01");
				}else if(response.getResponseBody().contains("图片验证码有误")){
					user.setTpyzm(null);
					tpyzmParser.reportError();
					throw new RetryException("图片验证码识别错误");
				}else if(response.getResponseBody().contains("短信验证码有误")){
					oldYzms.add(user.getDxyzm());
					user.setDxyzm(null);
					throw new RetryException("短信验证码错误");
				}else if(response.getResponseBody().contains("再次预约需在上次考试")){
					if(plan.seIncrease()){
						throw new StopException("10日预约限制");						
					}else{
						throw new RetryException("疑似10日预约限制");
					}
				}else if(response.getResponseBody().contains("本功能只能预约本科目的补考")){
					if(plan.seIncrease()){
						throw new StopException("不支持初考");
					}else{
						throw new RetryException("疑似不支持初考");
					}
				}
				jlcParser.parse(response.getResponseBody());
			}
		}while(!jlcParser.getStatusPanel().isSuccess());
		long costed = System.currentTimeMillis() - startJlc; 
		long exactCosted = System.currentTimeMillis() - jlcRequest.getSentAt();
		if(exactCosted < costed){
			costed = exactCosted;
		}
		
		actionLog.record("获取考试信息前已花费" + costed);
		costed = costed > 30000 ? 10000 : costed;
		actionLog.record("为防止时间过多，获取考试信息前已花费修正为" + costed);
		
		wait -= costed;
		String jlc = jlcParser.getJlcs()[0];
		String kskm = jlcParser.getKskm();
				
		user.setKskm(kskm);
		user.setJlc(jlc);
		actionLog.record("获取考试流水成功：");
		try {
			actionLog.record("获取考试信息前要等待" + wait);
			actionLog.record("获取考试信息前要额外等待" + this.getOffset());
			actionLog.record("获取考试信息前最终实际等待" + (wait + this.getOffset()));
			if(!shooting){
				Thread.sleep(wait + this.getOffset());				
			}else if(System.currentTimeMillis() + wait > startQuery){
				startQuery = System.currentTimeMillis() + wait;
			}
		} catch (InterruptedException e) {
		}
		
		//获取考试信息
		actionLog.record("系统开始获取考试信息：");
		ExamGenerator examGenerator = new ExamGenerator(user);
		Request examRequest = examGenerator.generate();
		ExamParser examParser = new ExamParser(plan, user);
		
		if(shooting){
			shot();
		}

		do{
			actionLog.record("获取考试信息...");
			Response response = tab.visit(examRequest);
			if(response.getStatusPanel().isSuccess()){
				examParser.parse(response.getResponseBody());
			}			
		}while(!examParser.getStatusPanel().isSuccess());
		
		return examParser.getExam();
	}
	
	private boolean hasShotten = false;
	private Army queryArmy;
	private Army bookArmy;
	private long startQuery;
	private long endQuery;
	
	public void preShot() {
		String host = "121.199.52.17";
		int port = 80;
		String headFormat = "POST /api/httpapi HTTP/1.1\r\nConnection:close\r\nContent-Type: application/x-www-form-urlencoded\r\nHost: service.zscg.hzcdt.com\r\nContent-Length: %d\r\n\r\n";
		
		// 准备查询考试日期
		queryArmy = new Army(host, port, 10);
		int queryLength = 371;
		String queryBody = "jkid=A001707&xlh=0C2B3243AFCB169B0E0C07533816A4D3&xmlDoc=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CQueryCondition%3E%3Csfzmmc%3EA%3C%2Fsfzmmc%3E%3Csfzmhm%3E"
				+ user.getSfzmhm() + "%3C%2Fsfzmhm%3E%3Cdxyzm%3E" + user.getDxyzm() + 
				"%3C%2Fdxyzm%3E%3Ctpyzm%3E" + user.getTpyzm() + "%3C%2Ftpyzm%3E%3Ctoken%3E" + user.getToken() +
				"%3C%2Ftoken%3E%3C%2FQueryCondition%3E%3C%2Froot%3E";
		queryArmy.prepare(String.format(headFormat, queryLength), queryBody, endQuery);
		long realStartQuery = startQuery;
		int queryOffset = 2000;
		Shooter shooter = null;
		do {
			shooter = queryArmy.shot("", realStartQuery);
			realStartQuery += queryOffset;
		}while(shooter != null);
		
		// 准备预约
		bookArmy = new Army(host, port, 10);
		String preBookBody = "jkid=B001100&xlh=0C2B3243AFCB169B0E0C07533816A4D3&xmlDoc=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CWriteCondition%3E%3Csfzmhm%3E"
				+ user.getSfzmhm() + "%3C%2Fsfzmhm%3E%3Csfzmmc%3EA%3C%2Fsfzmmc%3E%3Cxm%3E"
				+ user.getEncodedXm() + "%3C%2Fxm%3E%3Ckskm%3E"
				+ user.getKskm() + "%3C%2Fkskm%3E%3Ctoken%3E"
				+ user.getToken() + "%3C%2Ftoken%3E%3Chphm%3E"
				+ user.getEncodedJlc() + "%3C%2Fhphm%3E%3Cly%3EA%3C%2Fly%3E%3Cksdd%3E";
		int bookLength = preBookBody.length() + 114;
		bookArmy.prepare(String.format(headFormat, bookLength), preBookBody, endQuery);
		shooting = true;
	}
	
	public void shot() throws SuccessException, RetryException {
		hasShotten = true;
		
		ExamParser examParser = new ExamParser(plan, user);
		ArrayList<Shooter> queryShooters = queryArmy.getShooters();

		QueryLoop:
		while(System.currentTimeMillis() < endQuery){
			for(Shooter s : queryShooters){
				if(!s.getAsked() && s.getGun().getReport().isFinished()){
					s.setAsked(true);
					Response response = Response.parseTarget(s.getGun().getReport());
					if(response.getStatusPanel().isSuccess()){
						examParser.parse(response.getResponseBody());
						if(examParser.getStatusPanel().isSuccess()){
							break QueryLoop;
						}
					}
				}
			}
			
			if(queryArmy.allAsked()){
				break;
			}
		}
		
		Exam exam = examParser.getExam();
		if(exam == null) {
			stopShot();
			throw new RetryException("shot没有获取考试日期");
		}else{
			queryArmy.dismiss();
			actionLog.record("shot成功获取到考试日期");
		}
		
		// 当查到日期后，开启10个线程去预约考试
		String postBookBody = exam.ksdd + "%3C%2Fksdd%3E%3Cksrq%3E" + exam.ksrq + "%3C%2Fksrq%3E%3Ckscc%3E" + exam.kscc + "%3C%2Fkscc%3E%3C%2FWriteCondition%3E%3C%2Froot%3E";
		actionLog.record("shot准备预约考试");
		bookArmy.allShot(postBookBody, 0);
		ArrayList<Shooter> shooters = bookArmy.getShooters();
		
		while(System.currentTimeMillis() < endQuery){
			for(Shooter s : shooters){
				if(!s.getAsked() && s.getGun().getReport().isFinished()){
					s.setAsked(true);
					Response response = Response.parseTarget(s.getGun().getReport());

					if(response.getResponseBody().contains("重复预约")){
						Pattern ksrqPattern = Pattern.compile("201\\d-\\d+-\\d+");
						Matcher m = ksrqPattern.matcher(response.getResponseBody());
						stopShot();
						throw new SuccessException(m.find() ? m.group() : "2015-01-01");
					}					

					if(response.getStatusPanel().isSuccess() && (response.getResponseBody().contains("您已预约成功"))){
						actionLog.record("shot预约考试成功！");
						stopShot();
						throw new SuccessException(exam.ksrq);
					}	
				}				
			}
			
			if(bookArmy.allAsked()){
				break;
			}
		}
		
		stopShot();
	}
	
	public void stopShot() {
		if(shooting){
			queryArmy.dismiss();
			bookArmy.dismiss();
			shooting = false;
		}
	}
	
	//预约
	public boolean book(Exam exam) throws UnloginException, RetryException, StopException, PauseException, SuccessException {
		actionLog.record("开始预约考试：");
		BookGenerator bookGenerator = new BookGenerator(user, exam);
		Request bookRequest = bookGenerator.generate();
		
		for(int i = 0; i < 10; i++){
			Response response = tab.visit(bookRequest);
			
			if(response.suspect){
				actionLog.record("预约结果可疑！");
			}
			
			if(response.getResponseBody().contains("重复预约")){
				Pattern ksrqPattern = Pattern.compile("201\\d-\\d+-\\d+");
				Matcher m = ksrqPattern.matcher(response.getResponseBody());
				throw new SuccessException(m.find() ? m.group() : "2015-01-01");
			}
			
//			if(response.getResponseBody().contains("请在次月再行预约")){
//				throw new StopException("驾校名额已满");
//			}

			if(response.getStatusPanel().isSuccess() && (response.getResponseBody().contains("您已预约成功"))){
				actionLog.record("预约考试成功！");
				return true;
			}	
		}		
		
		throw new RetryException("尝试预约考试失败");
	}
}
