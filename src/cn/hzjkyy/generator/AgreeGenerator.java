package cn.hzjkyy.generator;

import cn.hzjkyy.model.User;

public class AgreeGenerator extends Generator {
	private User user;
	public AgreeGenerator(User user) {
		this.user = user;
	}
	
	@Override
	public String getJkid(){
		return "A001725";
	}
	
	@Override
	public String getXmlDoc(){
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><QueryCondition><fzjg>%E6%B5%99A</fzjg><title>%E8%80%83%E8%AF%95%E9%A2%84%E7%BA%A6</title><token>")
			.append(user.getToken())
			.append("</token></QueryCondition></root>")
			.toString();
	}
}
