package cn.hzjkyy;


import cn.hzjkyy.agent.AdvancedExplorer;
import cn.hzjkyy.agent.Explorer;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.generator.LoginGenerator;
import cn.hzjkyy.generator.ModifyGenerator;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.model.User;
import cn.hzjkyy.parser.LoginParser;
import cn.hzjkyy.tool.Log;

public class LoginTest {

	public static void main(String[] args) {
		Log applicationLog = Log.getTestLog("application");
		User user = new User("342622198212277288", "333198");
		Device device = new Device();
		Explorer explorer = new AdvancedExplorer(300000, 4);
		Tab mainTab = explorer.newTab();
		
		//登录
		applicationLog.record("系统开始登录。");
		LoginGenerator loginGenerator = new LoginGenerator(user, device);
		Request loginRequest = loginGenerator.generate();
		LoginParser loginParser = new LoginParser();

//		Queue<User> otherUsers = new LinkedList<User>();
//		otherUsers.add(new User("420703199011223231", "242527"));
//		otherUsers.add(new User("340824197904190447", "759627"));
//		otherUsers.add(new User("32108819900406671x", "000000"));
//		otherUsers.add(new User("330102196403030039", "277618"));
//		otherUsers.add(new User("339005197810019626", "711927"));
//		otherUsers.add(new User("340821196508275920", "248620"));
//		otherUsers.add(new User("411425199309268150", "752665"));
//		otherUsers.add(new User("530322199201101975", "123456"));
//		otherUsers.add(new User("36232219860706003X", "000000"));
//		otherUsers.add(new User("330781198409120219", "000000"));
//		otherUsers.add(new User("522628198911183426", "152329"));
//		otherUsers.add(new User("330822196901153318", "123456"));
		do {
			applicationLog.record("登录中...");
			Response response = mainTab.visit(loginRequest);
			if(response.getStatusPanel().isSuccess()){
				loginParser.parse(response.getResponseBody());					
			}
			//user = otherUsers.remove();
			loginRequest = loginGenerator.generate(user, device);
		} while(false);

		user.setToken(loginParser.getToken());
		ModifyGenerator modifyGenerator = new ModifyGenerator(user, "000000");
		Request modifyRequest = modifyGenerator.generate();
		
		mainTab.visit(modifyRequest);
		
		mainTab.visit(loginRequest);
		user.setPass("000000");
		mainTab.visit(loginGenerator.generate(user, device));
		
		user.setXm(loginParser.getXm());
		user.setToken(loginParser.getToken());
		user.setSfzmmc(loginParser.getSfzmmc());
		applicationLog.record("系统登录成功，姓名：" + loginParser.getXm() + "，密钥：" + loginParser.getToken());

	}

}
