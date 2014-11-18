package cn.hzjkyy.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.tool.YzmDecoder;

public class TpyzmParser extends Parser {
	private String tpyzm;
	private YzmDecoder yzmDecoder;
	public TpyzmParser(YzmDecoder yzmDecoder) {
		this.yzmDecoder = yzmDecoder;
	}
		
	public String getTpyzm() {
		return tpyzm;
	}

	public void clear() {
		super.clear();
	}
	
	private Pattern tpyzmPattern = Pattern.compile("<yzm>(.+)</yzm>");
	public void parse(String response) {
		clear();
		String tpyzmPic = null;
		Matcher m = tpyzmPattern.matcher(response);
		if (m.find()) {
			tpyzmPic = m.group(1);
			if(tpyzmPic.length() > 10){
				tpyzm = yzmDecoder.decode(tpyzmPic);
			}
		}
		
		if(tpyzm.length() == 4){			
			getStatusPanel().success();
		}else{
			getStatusPanel().error();
		}		
	}
}
