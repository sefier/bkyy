package cn.hzjkyy.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginParser extends Parser {
	private String xm;
	private Pattern xmPattern = Pattern.compile("<xm>(.+)</xm>");
	private Pattern tokenPattern = Pattern.compile("<token>(.+)</token>");
	private Pattern sfzmmcPattern = Pattern.compile("<sfzmmc>(.+)</sfzmmc>");
	
	public String getXm(){
		return xm;
	}
	private String token;
	public String getToken(){
		return token;
	}
	private String sfzmmc;
	public String getSfzmmc(){
		return sfzmmc;
	}
	
	public void clear() {
		super.clear();
		xm = token = null;
	}
		
	public void parse(String response) {
		clear();
		
		Matcher m = xmPattern.matcher(response);
		if (m.find()) {
			xm = m.group(1);
		}
		
		m = tokenPattern.matcher(response);
		if (m.find()) {
			token = m.group(1);
		}

		m = sfzmmcPattern.matcher(response);
		if (m.find()) {
			sfzmmc = m.group(1);
		}
		
		if(token != null && xm != null && sfzmmc != null){
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}		
	}
}
