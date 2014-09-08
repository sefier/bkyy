package cn.hzjkyy.generator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.hzjkyy.model.Exam;
import cn.hzjkyy.model.User;

public class BookGenerator extends Generator {
	private User user;
	private String jlc;
	private Exam exam;
	public BookGenerator(User user, String jlc, Exam exam) {
		this.user = user;
		this.jlc = jlc;
		this.exam = exam;
	}

	@Override
	public String getJkid() {
		return "B001100";
	}

	@Override
	public String getXmlDoc() {
		String xmlDoc = "";
		try {
			xmlDoc = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?><root><WriteCondition><ksdd>")
				.append(exam.ksdd)
				.append("</ksdd><ksrq>")
				.append(exam.ksrq)
				.append("</ksrq><sfzmhm>")
				.append(user.getSfzmhm())
				.append("</sfzmhm><sfzmmc>")
				.append(user.getSfzmmc())
				.append("</sfzmmc><xm>")
				.append(URLEncoder.encode(user.getXm(), "UTF-8"))
				.append("</xm><kskm>")
				.append(user.getKskm())
				.append("</kskm><token>")
				.append(user.getToken())
				.append("</token><kscc>")
				.append(exam.kscc)
				.append("</kscc><hphm>")
				.append(URLEncoder.encode(jlc, "UTF-8"))
				.append("</hphm><ly>A</ly></WriteCondition></root>")
				.toString();
		} catch (UnsupportedEncodingException e) {
		}
		
		return xmlDoc;
	}


}
