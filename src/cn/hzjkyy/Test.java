package cn.hzjkyy;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Test {

	public static void main(String[] args) {
		Calendar calendar = new GregorianCalendar();
		System.out.println(calendar.get(Calendar.SECOND));
	}

}
