package cn.hzjkyy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class Test {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String test = "jkid=B001100&xlh=0C2B3243AFCB169B0E0C07533816A4D3&xmlDoc=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CWriteCondition%3E%3Csfzmhm%3E342423199103188139%3C%2Fsfzmhm%3E%3Csfzmmc%3EA%3C%2Fsfzmmc%3E%3Cxm%3E%25E7%258E%258B%25E4%25BB%2581%25E4%25BF%258A%3C%2Fxm%3E%3Ckskm%3E3%3C%2Fkskm%3E%3Ctoken%3E730CDB554E79CDC03ED1A504EF089BD3%3C%2Ftoken%3E%3Chphm%3E%25E6%25B5%2599A86153C%2Fhphm%3E%3Cly%3EA%3C%2Fly%3E%3Cksdd%3E3301022%3C%2Fksdd%3E%3Cksrq%3E2015-05-18%3C%2Fksrq%3E%3Ckscc%3E51%3C%2Fkscc%3E%3C%2FWriteCondition%3E%3C%2Froot%3E";
		String real = "jkid=B001100&xlh=0C2B3243AFCB169B0E0C07533816A4D3&xmlDoc=" + URLEncoder.encode("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><WriteCondition><sfzmhm>342423199103188139</sfzmhm><sfzmmc>A</sfzmmc><xm>%E7%8E%8B%E4%BB%81%E4%BF%8A</xm><kskm>3</kskm><token>D7320E705CF679761FCB35C386179A52</token><hphm>%E6%B5%99A8615</hphm><ly>A</ly><ksdd>3301022</ksdd><ksrq>2015-05-17</ksrq><kscc>51</kscc></WriteCondition></root>", "UTF-8");
		System.out.println(test);
		System.out.println(real);
		System.out.println(test.length());
		System.out.println(real.length());
	}

}
