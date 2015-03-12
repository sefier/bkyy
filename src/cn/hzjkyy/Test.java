package cn.hzjkyy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(URLEncoder.encode("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><WriteCondition><sfzmhm>330122198510201942</sfzmhm><sfzmmc>A</sfzmmc><xm>%E5%BA%94%E5%B0%BC</xm><kskm>2</kskm><token>E826D64C4E7C2311F3E64E8E0347FF24</token><hphm>%E6%B5%99AA168</hphm><ly>A</ly><ksdd>3301007</ksdd><ksrq>2015-03-24</ksrq><kscc>51</kscc></WriteCondition></root>", "utf-8"));
//		String head = "POST /api/httpapi HTTP/1.1\r\nConnection:close\r\nContent-Type: application/x-www-form-urlencoded\r\nHost: service.zscg.hzcdt.com\r\nContent-Length: %d\r\n\r\n";
//		System.out.printf(head, 100);
//		System.out.print(String.format("jkid=A001707&xlh=0C2B3243AFCB169B0E0C07533816A4D3&xmlDoc=3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CQueryCondition%3E%3Csfzmmc%3EA%3C%2Fsfzmmc%3E%3Csfzmhm%3E%d%3C%2Fsfzmhm%3E%3Cdxyzm%3E286569%3C%2Fdxyzm%3E%3Ctpyzm%3E2555%3C%2Ftpyzm%3E%3Ctoken%3EE826D64C4E7C2311F3E64E8E0347FF24%3C%2Ftoken%3E%3C%2FQueryCondition%3E%3C%2Froot%3E", 100));
	}

}
