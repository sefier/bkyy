package cn.hzjkyy.model;

import cn.hzjkyy.shot.Target;
import cn.hzjkyy.tool.StatusPanel;

public class Response {
	public boolean suspect;
	private String responseBody;
	private StatusPanel statusPanel = new StatusPanel();
	
	public Response() {
	}
	
	public static Response parseTarget(Target target) {
		Response response = new Response();
		
		if(target.getStatus() == 0 && target.getContent().contains("OK")) {
			String[] parts = target.getContent().split("\r\n\r\n");
			if(parts.length == 2){
				response.getStatusPanel().success();
				response.setResponseBody(parts[1]);
			}
		}
		
		response.getStatusPanel().finish(false);
		
		return response;
	}
	
	public Response(String responseBody) {
		statusPanel.success();
		this.responseBody = responseBody;
	}

	// 获取响应正文
	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		statusPanel.success();
		this.responseBody = responseBody;
	}
	
	public StatusPanel getStatusPanel() {
		return statusPanel;
	}
	
	public void clear() {
		statusPanel.start();
		responseBody = null;
	}
}
