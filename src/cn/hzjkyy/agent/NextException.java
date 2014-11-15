package cn.hzjkyy.agent;

public class NextException extends Exception {
	private static final long serialVersionUID = 4585026745474272575L;
	private String reason;
	public NextException(String reason){
		this.reason = reason;
	}

	public String getReason(){
		return this.reason;
	}
}
