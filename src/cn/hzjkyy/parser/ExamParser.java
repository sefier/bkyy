package cn.hzjkyy.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.User;

public class ExamParser extends Parser{
	private Exam exam;

	private List<Pattern> patterns = new ArrayList<Pattern>();
	private Plan plan;
	
	public ExamParser(Plan plan, User user){
		this.plan = plan;
		String ksrqFormat = plan.getKsrqFormat();
		String[] ksrqs = ksrqFormat.split(",");
		for(String ksrq : ksrqs){
			if(plan.getKsdd() != null && plan.getKsdd().length() == 7){
				patterns.add(Pattern.compile("<item.*?<kscc>(\\d+)</kscc>.*?<sysj>(\\d+)</sysj>.*?<ksdd>(" + plan.getKsdd() + ")</ksdd><ksrq>(" + ksrq + ")</ksrq></item>"));
			}else if(user.getKskm().equals("3")){
				//50%的几率预约江涵路
				String preferKsdd = plan.getId() % 10 < 5 ? "3301022" : "3301034";
				patterns.add(Pattern.compile("<item.*?<kscc>(\\d+)</kscc>.*?<sysj>(\\d+)</sysj>.*?<ksdd>(" + preferKsdd + ")</ksdd><ksrq>(" + ksrq + ")</ksrq></item>"));
				patterns.add(Pattern.compile("<item.*?<kscc>(\\d+)</kscc>.*?<sysj>(\\d+)</sysj>.*?<ksdd>(\\d+)</ksdd><ksrq>(" + ksrq + ")</ksrq></item>"));
			}else{
				patterns.add(Pattern.compile("<item.*?<kscc>(\\d+)</kscc>.*?<sysj>(\\d+)</sysj>.*?<ksdd>(\\d+)</ksdd><ksrq>(" + ksrq + ")</ksrq></item>"));
			}
		}		
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
				for(Pattern examPattern : patterns){
					if(examPattern == null){
						continue;
					}
					Matcher m = examPattern.matcher(response);
					int startOffset = 0;
					
					while (m.find(startOffset)) {
						startOffset = m.end();
						String kscc = m.group(1);
						int sysj = Integer.parseInt(m.group(2));
						String ksdd = m.group(3);
						String ksrq = m.group(4);
						
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
							
							exam = new Exam(kscc, ksdd, ksrq, sysj * 1000);
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
