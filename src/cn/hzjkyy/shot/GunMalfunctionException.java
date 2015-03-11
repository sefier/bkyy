package cn.hzjkyy.shot;

public class GunMalfunctionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String reason;
	public String getReason() {
		return reason;
	}
	public GunMalfunctionException(String reason) {
		this.reason = reason;
	}

}
