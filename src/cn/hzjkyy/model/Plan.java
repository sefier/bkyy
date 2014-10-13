package cn.hzjkyy.model;

public class Plan {
	private int id;
	private String sfzmhm;
	private String pass;
	private String ksdd;
	private String startKsrq;
	private String endKsrq;


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

	public String getStartKsrq() {
		return startKsrq;
	}

	public void setStartKsrq(String startKsrq) {
		this.startKsrq = startKsrq;
	}
	
	public String getEndKsrq() {
		return endKsrq;
	}

	public void setEndKsrq(String endKsrq) {
		this.endKsrq = endKsrq;
	}

	public String getKsdd() {
		return ksdd;
	}

	public void setKsdd(String ksdd) {
		this.ksdd = ksdd;
	}

}
