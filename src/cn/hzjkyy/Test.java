package cn.hzjkyy;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Test {

	public static void main(String[] args) {
		Calendar calendar = new GregorianCalendar();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		System.out.println(hour + ":" + minute + ":" + second);
	}

}
