package cn.hzjkyy.parser;

public class SendParser extends Parser {
	public void parse(String response) {
		clear();
		if(response.contains("短信已发送")){
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}
	}
}