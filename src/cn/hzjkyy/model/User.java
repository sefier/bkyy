package cn.hzjkyy.model;

public class User {
	private String sfzmhm;
	private String pass;
	private String xm;
	private String token;
	private String sfzmmc;
	private String kskm;
	private String jlc;
	
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
