package cn.hzjkyy.model;

import java.util.UUID;

public class Device {
	private String deviceToken;
		
	public Device(String token) {
		if(token != null){
			deviceToken = token;
		}else{
			deviceToken = UUID.randomUUID().toString().replaceAll("-", "");				
		}
	}
	
	public void reGenerate() {
		deviceToken = UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public String getDeviceToken() {
		return deviceToken;
		//return "c265560a328a325e421afae66dd6fd79";
	}
}
