package cn.hzjkyy.agent;

public class StopException extends Exception {
	private static final long serialVersionUID = -2359701544395194444L;

	private String reason;
	public StopException(String reason){
		this.reason = reason;
	}
	
	public String getReason(){
		return this.reason;
	}
}
