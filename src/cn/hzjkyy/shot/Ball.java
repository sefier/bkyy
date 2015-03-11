package cn.hzjkyy.shot;

public class Ball {
	private String content;
	private long startFire;
	private long stopFire;
	
	public Ball(String content, long startFire, long stopFire) {
		this.content = content;
		this.startFire = Math.min(startFire, stopFire);
		this.stopFire = stopFire;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getStartFire() {
		return startFire;
	}
	public void setStartFire(long startFire) {
		this.startFire = startFire;
	}
	public long getStopFire() {
		return stopFire;
	}
	public void setStopFire(long stopFire) {
		this.stopFire = stopFire;
	}
}
