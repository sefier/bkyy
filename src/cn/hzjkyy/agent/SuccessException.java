package cn.hzjkyy.agent;

public class SuccessException extends Exception {
	private static final long serialVersionUID = 3642572219382370364L;
	
	private String ksrq;
	public SuccessException(String ksrq){
		this.ksrq = ksrq;
	}
	
	public String getKsrq(){
		return this.ksrq;
	}


}
