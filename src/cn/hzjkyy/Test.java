package cn.hzjkyy;

import java.util.Calendar;
import java.util.GregorianCalendar;

import cn.hzjkyy.tool.OcsClient;

public class Test {
	public static void main(String[] args){
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 50);
		calendar.set(Calendar.MILLISECOND, 0);
		System.out.println(calendar.getTimeInMillis());
		
		OcsClient.set("km2", "2014-10-01");
		System.out.println(OcsClient.get("km2"));
	}
}
