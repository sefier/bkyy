package cn.hzjkyy.shot;

import java.util.ArrayList;

public class Army {
	
	public static void main(String[] args) throws InterruptedException {
		Army army = new Army("121.199.52.17", 80, 5);
		String head = "POST /api/httpapi HTTP/1.1\r\nConnection:close\r\nContent-Length: 328\r\nContent-Type: application/x-www-form-urlencoded\r\nHost: service.zscg.hzcdt.com\r\n\r\n";
		String preBody = "xlh=0C2B3243AFCB169B0E0C07533816A4D3&jkid=A001665&xmlDoc=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CQueryCondition%3E%3Cpass%3E119734%3C%2Fpass%3E%3CloginId%3";
		String postBody = "E330184199310123920%3C%2FloginId%3E%3Cdevice_token%3Eb5da1c4bbb894b9ebe81bd61d3341a75%3C%2Fdevice_token%3E%3C%2FQueryCondition%3E%3C%2Froot%3E";

//		army.prepare(head, preBody + postBody, System.currentTimeMillis() + 1 * 60 * 1000);
//		army.shot("", System.currentTimeMillis() + 15 * 1000);
//		army.dismiss();
		
		army.prepare(head, preBody, System.currentTimeMillis() + 2 * 60 * 1000);
		army.allShot(postBody, 10 * 1000);
	}
	
	private ArrayList<Shooter> shooters = new ArrayList<Shooter>();
	
	public Army(String host, int port, int count) {
		for(int i = 0; i < count; i++) {
			shooters.add(new Shooter(host, port));
		}
	}
	
	public ArrayList<Shooter> getShooters() {
		return shooters;
	}
	
	// 全体队员准备射击
	public void prepare(String head, String body, long aboutSentAt) {
		for(Shooter shooter : shooters) {
			shooter.prepare(head, body, aboutSentAt);
		}
	}
	
	private Shooter shootingOne;
	public Shooter getShootingOne() {
		return shootingOne;
	}
	// 下一个队员射击
	public Shooter shot(String body, long sentAt) {
		for(Shooter shooter : shooters) {
			if(!shooter.hasShot()){
				shooter.shot(body, sentAt);
				shootingOne = shooter;
				return shooter;
			}
		}
		
		return null;		
	}
	
	// 全体射击
	public void allShot(String body, long sentAt) {
		for(Shooter shooter : shooters) {
			if(!shooter.hasShot()){
				shooter.shot(body, sentAt);
			}
		}
	}
	
	// 解散
	public void dismiss() {
		for(Shooter shooter : shooters) {
			if(!shooter.hasShot()){
				shooter.getGun().discard();				
			}
		}
	}
}
