package cn.hzjkyy.generator;

import cn.hzjkyy.model.User;

public class TpyzmGenerator extends Generator {
	private User user;
	public TpyzmGenerator(User user) {
		this.user = user;
	}
	
	@Override
	public String getJkid(){
		return "A001663";
	}
	
	@Override
	public String getXmlDoc(){
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><QueryCondition><sfzmhm>")
			.append(user.getSfzmhm())
			.append("</sfzmhm><token>")
			.append(user.getToken())
			.append("</token><ywlx>1</ywlx></QueryCondition></root>")
			.toString();
	}
}
