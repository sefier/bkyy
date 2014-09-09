package cn.hzjkyy;

import cn.hzjkyy.tool.OcsClient;

public class Test {
	public static void main(String[] args){
		OcsClient.set("test", "2014-10-31");
		System.out.println(OcsClient.get("test"));
		OcsClient.close();
	}
}
