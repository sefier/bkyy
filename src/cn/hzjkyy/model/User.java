package cn.hzjkyy.model;

import java.net.URLEncoder;

public class User {
	private String sfzmhm;
	private String pass;
	private String xm;
	private String token;
	private String sfzmmc;
	private String kskm;
	private String jlc;
	private String tpyzm;
	private String dxyzm;
	
	public String getDxyzm() {
		return dxyzm;
	}

	public void setDxyzm(String dxyzm) {
		this.dxyzm = dxyzm;
	}

	public String getTpyzm() {
		return tpyzm;
	}

	public void setTpyzm(String tpyzm) {
		this.tpyzm = tpyzm;
	}

	public String getEncodedJlc() {
		String encodedJlc = "";
		try{
			encodedJlc = URLEncoder.encode(URLEncoder.encode(getJlc(), "UTF-8"), "UTF-8");			
		}catch(Exception e){			
		}
		
		return encodedJlc;
	}
	public String getJlc() {
		return jlc;
	}

	public void setJlc(String jlc) {
		this.jlc = jlc;
	}

	public String getKskm() {
		return kskm;
	}

	public void setKskm(String kskm) {
		this.kskm = kskm;
	}

	public String getSfzmmc() {
		return sfzmmc;
	}

	public void setSfzmmc(String sfzmmc) {
		this.sfzmmc = sfzmmc;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getEncodedXm() {
		String encodedXm = "";
		try{
			encodedXm = URLEncoder.encode(URLEncoder.encode(getXm(), "UTF-8"), "UTF-8");			
		}catch(Exception e){			
		}
		
		return encodedXm;
	}

	public String getXm() {
		return xm;
	}

	public void setXm(String xm) {
		this.xm = xm;
	}

	public String getSfzmhm() {
		return sfzmhm;
	}

	public void setSfzmhm(String sfzmhm) {
		this.sfzmhm = sfzmhm;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public User(String sfzmhm, String pass) {
		this.sfzmhm = sfzmhm;
		this.pass = pass;
	}
	
	public String toString() {
		return "姓名：" + xm + "\n密码：" + pass + "\n密钥：" + token + "\n身份证号码：" + sfzmhm;
	}
}
