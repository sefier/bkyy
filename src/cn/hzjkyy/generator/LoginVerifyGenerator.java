package cn.hzjkyy.generator;

import cn.hzjkyy.model.User;

public class LoginVerifyGenerator extends Generator {
	private User user;
	public LoginVerifyGenerator(User user) {
		this.user = user;
	}
	
	@Override
	public String getJkid(){
		return "A001672";
	}
	
	@Override
	public String getXmlDoc(){
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><QueryCondition><token>")
			.append(user.getToken())
			.append("</token><loginid>")
			.append(user.getSfzmhm())
			.append("</loginid></QueryCondition></root>")
			.toString();
	}
}
