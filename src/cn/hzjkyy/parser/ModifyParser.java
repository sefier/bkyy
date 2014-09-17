package cn.hzjkyy.parser;

public class ModifyParser extends Parser {
	public void parse(String response) {
		clear();
		if(response.contains("密码修改成功")){
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}		
	}
}
