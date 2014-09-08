package cn.hzjkyy.tool;

/**
 * 
 * @author sefier
 * 这个类，主要用来表示某一个动作的状态，0表示进行中，正数表示成功，负数表示失败（默认的成功是1，默认的失败是-1，可以自行增加其他的数据，表示不同的成功或失败结果类型）
 */
public class StatusPanel {
	private int status;

	// 0代表未开始， -1代表失败，1代表成功，
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	//初始化
	public void start() {
		status = 0;
	}
	
	//以正确的结果结束
	public void finish(boolean isSuccess) {
		if(status == 0){
			if(isSuccess){
				status = 1;
			}else{
				status = -1;
			}
		}
	}
	
	public void error() {
		status = -1;
	}
	
	public boolean isError() {
		return status < 0;
	}
	
	public void success() {
		status = 1;
	}
	
	public boolean isSuccess() {
		return status > 0;
	}
}
