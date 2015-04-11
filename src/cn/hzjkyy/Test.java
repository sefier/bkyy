package cn.hzjkyy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		for(String k : "2015-05-\\d+,2015-04-\\d+".split(",")){
			System.out.println(k);			
		}
	}

}
