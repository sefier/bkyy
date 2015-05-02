package cn.hzjkyy.shot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import cn.hzjkyy.tool.Log;

public class Gun {
	public static void main(String[] args) throws Exception{
		Gun gun = new Gun("121.199.52.17", 80);
		String head = "POST /api/httpapi HTTP/1.1\r\nConnection:close\r\nContent-Length: 328\r\nContent-Type: application/x-www-form-urlencoded\r\nHost: service.zscg.hzcdt.com\r\n\r\n";
		String body = "xlh=0C2B3243AFCB169B0E0C07533816A4D3&jkid=A001665&xmlDoc=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CQueryCondition%3E%3Cpass%3E119734%3C%2Fpass%3E%3CloginId%3E330184199310123920%3C%2FloginId%3E%3Cdevice_token%3Eb5da1c4bbb894b9ebe81bd61d3341a75%3C%2Fdevice_token%3E%3C%2FQueryCondition%3E%3C%2Froot%3E";
		
		//手动手枪
		gun.fire(head);
		gun.fire(body);
		gun.viewTarget(1);
	}
	
	private boolean pushed = false;
	private String host;
	private int port;
	
	private Socket client;
	private Writer writer;
	private Reader reader;
	
	public Gun(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	//拉开枪栓
	public void pull() throws GunMalfunctionException {
		if(client == null){
			try {
				client = new Socket(host, port);
				writer = new OutputStreamWriter(client.getOutputStream());
				reader = new InputStreamReader(client.getInputStream());
			} catch (IOException e) {
				throwGunException(e);
			}
		}
	}
		
	//发射子弹
	public void fire(Object ball) throws GunMalfunctionException {
		try {
			pull();
			writer.write(ball.toString());
			writer.flush();
		} catch (IOException e) {
			throwGunException(e);
		}
	}

	//查看靶子
	public Target viewTarget() {
		return viewTarget(0);
	}
	
	//指定时间内查看靶子
	public Target viewTarget(int seconds) {
		Shooter.recordln("开始检查靶标");

		pushed = true;
		StringBuffer sb = new StringBuffer();
		Target target;
		try {
			client.setSoTimeout(seconds * 1000);
			//写完以后进行读操作
			char chars[] = new char[64];  
			int len;
			while ((len=reader.read(chars)) != -1) {
				sb.append(new String(chars, 0, len));  
			}
			target = new Target(sb.toString(), 0);
		} catch (IOException e) {
			Shooter.recordln(Log.exceptionStacktraceToString(e));
			target = new Target(sb.toString(), 1);
		}
		
		push();
		Shooter.recordln("检查结束");
		Shooter.recordln(target);
		return target;
	}
	
	public boolean hasBeenPushed() {
		return pushed;
	}
	
	//关上枪栓
	protected void push() {
		if(client != null){
			try {
				reader.close();
				writer.close();
				client.close();			
			} catch (IOException e) {
			}
		}
	}
	
	protected void throwGunException(Exception e) throws GunMalfunctionException {
		throw new GunMalfunctionException(Log.exceptionStacktraceToString(e));
	}
}
