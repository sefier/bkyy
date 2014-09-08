package cn.hzjkyy.model;

import java.util.UUID;

public class Device {
	private String deviceToken;
		
	public Device() {
		deviceToken = UUID.randomUUID().toString().replaceAll("-", "");	
	}
	
	public void reGenerate() {
		deviceToken = UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public String getDeviceToken() {
		//return "c265560a328a325e421afae66dd6fd79";
		return deviceToken;
	}
}
