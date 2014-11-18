
package cn.hzjkyy.generator;

import cn.hzjkyy.model.User;

public class SendGenerator extends Generator {
	private User user;
	public SendGenerator(User user) {
		this.user = user;
	}

	@Override
	public String getJkid() {
		return "B001117";
	}

	@Override
	public String getXmlDoc() {
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><WriteCondition><sfzmmc>")
			.append(user.getSfzmmc())
			.append("</sfzmmc><sfzmhm>")
			.append(user.getSfzmhm())
			.append("</sfzmhm><token>")
			.append(user.getToken())
			.append("</token></WriteCondition></root>")
			.toString();
	}
}

