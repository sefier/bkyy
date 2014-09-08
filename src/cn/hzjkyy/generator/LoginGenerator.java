package cn.hzjkyy.generator;

import cn.hzjkyy.model.Device;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.User;

public class LoginGenerator extends Generator{	
	private User user;
	private Device device;

	public LoginGenerator(User user, Device device) {
		change(user, device);
	}
	
	public Request generate(User user, Device device) {
		change(user, device);
		return super.generate();
	}
	
	private void change(User user, Device device) {
		this.user = user;
		this.device = device;
	}
	
	@Override
	public String getJkid(){
		return "A001665";
	}
	
	@Override
	public String getXmlDoc(){
		return new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><QueryCondition><pass>")
			.append(user.getPass())
			.append("</pass><loginId>")
			.append(user.getSfzmhm())
			.append("</loginId><device_token>")
			.append(device.getDeviceToken())
			.append("</device_token></QueryCondition></root>")
			.toString();
	}
}
