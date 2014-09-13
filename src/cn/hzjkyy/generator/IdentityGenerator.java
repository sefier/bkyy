package cn.hzjkyy.generator;

import cn.hzjkyy.model.User;

public class IdentityGenerator extends Generator {
	private User user;
	public IdentityGenerator(User user) {
		this.user = user;
	}
	
	@Override
	public String getJkid(){
		return "A001669";
	}
	
	@Override
	public String getXmlDoc(){
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><QueryCondition><sfzmmc>")
			.append(user.getSfzmmc())
			.append("</sfzmmc><sfzmhm>")
			.append(user.getSfzmhm())
			.append("</sfzmhm><token>")
			.append(user.getToken())
			.append("</token></QueryCondition></root>")
			.toString();
	}
}
