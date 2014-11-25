package cn.hzjkyy.parser;

public class SendParser extends Parser {
	public void parse(String response) {
		clear();
		if(response.contains("短信已发送") || response.contains("短信验证码已发送至你的手机")){
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}
	}
}