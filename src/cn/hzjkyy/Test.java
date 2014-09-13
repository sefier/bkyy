package cn.hzjkyy;

import cn.hzjkyy.agent.AdvancedExplorer;
import cn.hzjkyy.agent.Tab;
import cn.hzjkyy.generator.LoginGenerator;
import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.model.User;
import cn.hzjkyy.parser.LoginParser;
import cn.hzjkyy.tool.Log;

public class Test {
	public static void main(String[] args){
		Plan plan = new Plan();
		plan.setId(120);
		Log.init(plan, 1);
		User user = new User("330681198406087454", "179056");
		Device device = new Device();
		AdvancedExplorer explorer = new AdvancedExplorer(300000, 4);
		Tab mainTab = explorer.newTab();
		
		LoginGenerator loginGenerator = new LoginGenerator(user, device);
		Request loginRequest = loginGenerator.generate();
		LoginParser loginParser = new LoginParser();

		do {
			Response response = mainTab.visit(loginRequest);
			if(response.getStatusPanel().isSuccess()){
				loginParser.parse(response.getResponseBody());					
			}
		} while(!loginParser.getStatusPanel().isSuccess());
		user.setXm(loginParser.getXm());
		user.setToken(loginParser.getToken());
		user.setSfzmmc(loginParser.getSfzmmc());
	}
}
