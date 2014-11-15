package cn.hzjkyy.agent;

public class PauseException extends Exception {
	private static final long serialVersionUID = -2641886747466477985L;

	private String reason;
	public PauseException(String reason){
		this.reason = reason;
	}
	
	public String getReason(){
		return this.reason;
	}

}
