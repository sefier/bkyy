package cn.hzjkyy.parser;

public class FrontParser extends Parser {
	public void parse(String response) {
		clear();
		if(response.contains("数据获取成功")){
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}		
	}
}
