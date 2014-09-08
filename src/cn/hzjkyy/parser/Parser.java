package cn.hzjkyy.parser;

import cn.hzjkyy.tool.StatusPanel;

public abstract class Parser {
	public Parser() {
		statusPanel = new StatusPanel();
	}
	public abstract void parse(String response);
	
	private StatusPanel statusPanel;
	
	public StatusPanel getStatusPanel() {
		return statusPanel;
	}
	
	protected void clear() {
		statusPanel.start();
	}
}
