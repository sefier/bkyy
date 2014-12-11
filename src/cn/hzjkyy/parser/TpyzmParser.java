package cn.hzjkyy.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.tool.YzmDecoder;

public class TpyzmParser extends Parser {
	private String tpyzm;
	private String codeId;
	private YzmDecoder yzmDecoder;
	public TpyzmParser(YzmDecoder yzmDecoder) {
		this.yzmDecoder = yzmDecoder;
	}
		
	public String getTpyzm() {
		return tpyzm;
	}
	
	public void reportError() {
		if(codeId != null){
			System.out.println("汇报验证码识别错误：" + codeId);
			yzmDecoder.reportError(codeId);
		}
	}

	public void clear() {
		super.clear();
	}
	
	private Pattern tpyzmPattern = Pattern.compile("<yzm>([\\s\\S]*)</yzm>");
	public void parse(String response) {
		clear();
		String tpyzmPic = null;
		Matcher m = tpyzmPattern.matcher(response);
		if (m.find()) {
			tpyzmPic = m.group(1);
			if(tpyzmPic.length() > 10){
				codeId = yzmDecoder.decode(tpyzmPic);
				tpyzm = yzmDecoder.fetch(codeId);
			}
		}
		
		if(tpyzm != null && tpyzm.length() == 4){
			getStatusPanel().success();
		}else{
			reportError();
			getStatusPanel().error();
		}		
	}
}
