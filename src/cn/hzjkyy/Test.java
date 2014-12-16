package cn.hzjkyy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		Pattern testPattern = Pattern.compile("(2015-\\d+-\\d+)<");
		Matcher m = testPattern.matcher("2015-01-1<123123");
		if(m.find()){
			System.out.println(5 / 2);
		}

	}

}
