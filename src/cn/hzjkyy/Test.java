package cn.hzjkyy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(URLEncoder.encode("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><WriteCondition><sfzmhm>330122198510201942</sfzmhm><sfzmmc>A</sfzmmc><xm>%E5%BA%94%E5%B0%BC</xm><kskm>2</kskm><token>E826D64C4E7C2311F3E64E8E0347FF24</token><hphm>%E6%B5%99AA168</hphm><ly>A</ly><ksdd>3301007</ksdd><ksrq>2015-03-24</ksrq><kscc>51</kscc></WriteCondition></root>", "utf-8"));
	}

}
