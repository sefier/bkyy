package cn.hzjkyy.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JlcParser extends Parser {
	private String[] jlcs = new String[0];
	private String kskm;
	
	public String[] getJlcs() {
		return jlcs;
	}
	
	public String getKskm() {
		return kskm;
	}

	public void clear() {
		super.clear();
		jlcs = new String[0];
	}
	
	private Pattern jlcsPattern = Pattern.compile("<jlc>(.+)</jlc>");
	private Pattern kskmPattern = Pattern.compile("<kskm>(.+)</kskm>");
	public void parse(String response) {
		clear();
		
		Matcher m = jlcsPattern.matcher(response);
		if (m.find()) {
			String jlcsString = m.group(1);
			jlcs = jlcsString.split(",");
		}
		
		m = kskmPattern.matcher(response);
		if (m.find()) {
			kskm = m.group(1);
		}
		
		if(jlcs.length > 0 && kskm.length() > 0){
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}		
	}
}
