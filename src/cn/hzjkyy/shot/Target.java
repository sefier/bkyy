package cn.hzjkyy.shot;

public class Target {
	private String content;
	private int status = 4; //0：正常，1：眼瞎了，2：手枪失灵，3：自动步枪被下令终止，4：还没打完靶子，5：正在检查靶子
	
	public Target(String content, int status) {
		this.content = content;
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getContent() {
		return content;
	}
	
	public boolean isFinished() {
		return status < 4; 
	}
	
	public String toString() {
		return "状态：" + status + "，内容：" + content;
	}
}
