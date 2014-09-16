package cn.hzjkyy.model;

public class Plan {
	private int id;
	private String sfzmhm;
	private String pass;
	private String ksdd;
	private String ksrq;
	private String token;
	private int number;
	private int total;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getKsrq() {
		return ksrq;
	}

	public void setKsrq(String ksrq) {
		this.ksrq = ksrq;
	}

	public String getKsdd() {
		return ksdd;
	}

	public void setKsdd(String ksdd) {
		this.ksdd = ksdd;
	}

}
