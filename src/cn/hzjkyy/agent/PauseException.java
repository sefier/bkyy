package cn.hzjkyy.agent;

public class PauseException extends Exception {
	private static final long serialVersionUID = 4585026745474272575L;
	private String reason;
	public PauseException(String reason){
		this.reason = reason;
	}

	public String getReason(){
		return this.reason;
	}
}
