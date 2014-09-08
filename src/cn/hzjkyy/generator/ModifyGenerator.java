package cn.hzjkyy.generator;

import cn.hzjkyy.model.User;

public class ModifyGenerator extends Generator {
	private User user;
	private String newpass;
	public ModifyGenerator(User user, String newpass) {
		this.user = user;
		this.newpass = newpass;
	}
	
	@Override
	public String getJkid(){
		return "B001097";
	}
	
	@Override
	public String getXmlDoc(){
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><WriteCondition><oldpass>")
			.append(user.getPass())
			.append("</oldpass><loginId>")
			.append(user.getSfzmhm())
			.append("</loginId><newpass>")
			.append(newpass)
			.append("</newpass><token>")
			.append(user.getToken())
			.append("</token></WriteCondition></root>")
			.toString();
	}

}
