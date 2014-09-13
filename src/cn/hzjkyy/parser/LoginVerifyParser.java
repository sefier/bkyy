package cn.hzjkyy.parser;

public class LoginVerifyParser extends Parser {
	public void parse(String response) {
		clear();
		if(response.contains("获取数据成功")){
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}		
	}
}
