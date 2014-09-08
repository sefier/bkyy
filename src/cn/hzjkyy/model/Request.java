package cn.hzjkyy.model;

public class Request {
	private String jkid;
	private String xmlDoc;
	private long sentAt;
	
	public Request(String jkid, String xmlDoc){
		this.jkid = jkid;
		this.xmlDoc = xmlDoc;
	}
	
	public String getJkid() {
		return jkid;
	}
	
	public String getXmlDoc() {
		return xmlDoc;
	}
	
	public long getSentAt() {
		return sentAt;
	}
	
	public void setSentAt(long sentAt) {
		this.sentAt = sentAt;
	}
}
