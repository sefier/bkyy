package cn.hzjkyy.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TpyzmParser extends Parser {
	private String tpyzm;
		
	public String getTpyzm() {
		return tpyzm;
	}

	public void clear() {
		super.clear();
	}
	
	private Pattern tpyzmPattern = Pattern.compile("<yzm>(.+)</yzm>");
	public void parse(String response) {
		clear();
		
		Matcher m = tpyzmPattern.matcher(response);
		if (m.find()) {
			tpyzm = m.group(1);
		}
		
		if(tpyzm.length() > 0){
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}		
	}
}
