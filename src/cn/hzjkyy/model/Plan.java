package cn.hzjkyy.model;

public class Plan {
	private int id;
	private String sfzmhm;
	private String pass;
	private String ksdd;
	private String startKsrq;
	private String endKsrq;

	private int window;
	public int getWindow() {
		return window;
	}
	public void setWindow(int window) {
		this.window = window;
	}
	private String ksrqFormat = "2015-04-\\d+";
	public String getKsrqFormat(){
		return ksrqFormat;
	}
	public void setKsrqFormat(String ksrqFormat){
		this.ksrqFormat = ksrqFormat;
	}
	private int seTimes = 0;
	public boolean seIncrease() {
		seTimes += 1;
		return seTimes > 3;
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
