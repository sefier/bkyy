package cn.hzjkyy.model;

import cn.hzjkyy.tool.StatusPanel;

public class Response {
	private String responseBody;
	private StatusPanel statusPanel = new StatusPanel();
	
	public Response() {
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
