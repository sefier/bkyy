package cn.hzjkyy.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.model.Exam;

public class ExamParser extends Parser{
	private Exam exam;
	
	public void clear() {
		super.clear();
		exam = null;
	}
	
	public Exam getExam(){
		return exam;
	}
	
	private Pattern examPattern = Pattern.compile("<item.*?<kscc>(\\d+)</kscc><ksdd>(\\d+)</ksdd><ksrq>(2014-10.+?)</ksrq></item>");
	public void parse(String response) {
		clear();
		
		Matcher m = examPattern.matcher(response);
		if (m.find()) {
			String kscc = m.group(1);
			String ksdd = m.group(2);
			String ksrq = m.group(3);
			
			if(kscc.length() > 0 && ksdd.length() > 0 && ksrq.length() > 0){
				exam = new Exam(kscc, ksdd, ksrq);
				getStatusPanel().success();
			}
		}
		
		getStatusPanel().finish(false);
	}
}
