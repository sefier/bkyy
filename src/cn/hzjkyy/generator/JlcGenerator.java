package cn.hzjkyy.generator;

import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.User;

public class JlcGenerator extends Generator {
	
	private User user;

	public JlcGenerator(User user) {
		change(user);
	}
	
	public Request generate(User user) {
		change(user);
		return super.generate();
	}

	private void change(User user) {
		this.user = user;
	}
	
	@Override
	public String getJkid() {
		return "A001740";
	}

	@Override
	public String getXmlDoc() {
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><QueryCondition><sfzmmc>")
			.append(user.getSfzmmc())
			.append("</sfzmmc><sfzmhm>")
			.append(user.getSfzmhm())
			.append("</sfzmhm><dxyzm>")
			.append(user.dxyzm)
			.append("</dxyzm><tpyzm>")
			.append(user.tpyzm)
			.append("</tpyzm><token>")
			.append(user.getToken())
			.append("</token></QueryCondition></root>")
			.toString();
	}

}
