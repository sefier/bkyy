package cn.hzjkyy.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;

public class ExamParser extends Parser{
	private Exam exam;

	private Pattern[] patterns = new Pattern[2];
	private Plan plan;
	
	public ExamParser(Plan plan){
		this.plan = plan;
		String ksrq = "2014-12-.+?"; //plan.getStartKsrq() == null ? "2014-11.+?" : plan.getStartKsrq();
		patterns[0] = Pattern.compile("<item.*?<kscc>(\\d+)</kscc>.*?<ksdd>(" + plan.getKsdd() + ")</ksdd><ksrq>(" + ksrq + ")</ksrq></item>");
		patterns[1] = Pattern.compile("<item.*?<kscc>(\\d+)</kscc>.*?<ksdd>(\\d+)</ksdd><ksrq>(" + ksrq + ")</ksrq></item>");
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
		
		if(response.contains("<code>1</code>") || response.contains("已无可用名额")){
			getStatusPanel().success();
			if(response.contains("<code>1</code>")){
				for(Pattern examPattern: patterns){
					Matcher m = examPattern.matcher(response);
					if (m.find()) {
						String kscc = m.group(1);
						String ksdd = m.group(2);
						String ksrq = m.group(3);
						
						if(kscc.length() > 0 && ksdd.length() > 0 && ksrq.length() > 0){
							try {
								if(plan.getStartKsrq() != null || plan.getEndKsrq() != null){
								    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								    Date ksrqDate = format.parse(ksrq); 
								    
								    if(plan.getStartKsrq() != null){
								    	Date start = format.parse(plan.getStartKsrq());
								    	if(start.compareTo(ksrqDate) > 0){
								    		continue;
								    	}
								    }
								    
								    if(plan.getEndKsrq() != null){
								    	Date end = format.parse(plan.getEndKsrq());
								    	if(end.compareTo(ksrqDate) < 0){
								    		continue;
								    	}
								    }
								}								
							}catch(ParseException e){
							}
							
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
