package cn.hzjkyy.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;

public class ExamParser extends Parser{
	private Exam exam;

	private Pattern[] patterns = new Pattern[2];
	
	public ExamParser(Plan plan){
		String ksrq = plan.getKsrq() == null ? "2014-10.+?" : plan.getKsrq();
		patterns[0] = Pattern.compile("<item.*?<kscc>(\\d+)</kscc><ksdd>(" + plan.getKsdd() + ")</ksdd><ksrq>(" + ksrq + ")</ksrq></item>");
		patterns[1] = Pattern.compile("<item.*?<kscc>(\\d+)</kscc><ksdd>(\\d+)</ksdd><ksrq>(" + ksrq + ")</ksrq></item>");
	}
	
	public void clear() {
		super.clear();
		exam = null;
	}
	
	public Exam getExam(){
		return exam;
	}
	
	public void parse(String response) {
		clear();
		
		if(response.contains("<code>1</code>") || response.contains("该考点截止已无可用名额")){
			getStatusPanel().success();
			if(response.contains("<code>1</code>")){
				for(Pattern examPattern: patterns){
					Matcher m = examPattern.matcher(response);
					if (m.find()) {
						String kscc = m.group(1);
						String ksdd = m.group(2);
						String ksrq = m.group(3);
						
						if(kscc.length() > 0 && ksdd.length() > 0 && ksrq.length() > 0){
							exam = new Exam(kscc, ksdd, ksrq);
							break;
						}
					}
				}				
			}
		}else{
			getStatusPanel().error();
		}
	}
}
