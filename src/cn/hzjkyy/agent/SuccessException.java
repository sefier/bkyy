package cn.hzjkyy.agent;

public class SuccessException extends Exception {
	private static final long serialVersionUID = 3642572219382370364L;
	
	private String reason;
	public SuccessException(String reason){
		this.reason = reason;
	}
	
	public String getReason(){
		return this.reason;
	}


}
