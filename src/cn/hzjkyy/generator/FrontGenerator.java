package cn.hzjkyy.generator;

import cn.hzjkyy.model.User;

public class FrontGenerator extends Generator {
	private User user;
	public FrontGenerator(User user) {
		this.user = user;
	}
	
	@Override
	public String getJkid(){
		return "A001713";
	}
	
	@Override
	public String getXmlDoc(){
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><QueryCondition><token>")
			.append(user.getToken())
			.append("</token></QueryCondition></root>")
			.toString();
	}
}
