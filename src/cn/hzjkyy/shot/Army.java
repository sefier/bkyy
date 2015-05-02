package cn.hzjkyy.shot;

import java.util.ArrayList;

public class Army {
	
	public static void main(String[] args) throws InterruptedException {
		Army army = new Army("121.199.52.17", 80, 1);
		String head = "POST /api/httpapi HTTP/1.1\r\nConnection:close\r\nContent-Length: 550\r\nContent-Type: application/x-www-form-urlencoded\r\nHost: service.zscg.hzcdt.com\r\n\r\n";
		String preBody = "jkid=B001100&xlh=0C2B3243AFCB169B0E0C07533816A4D3&xmlDoc=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CWriteCondition%3E%3Csfzmhm%3E342423199103188139%3C%2Fsfzmhm%3E%3Csfzmmc%3EA%3C%2Fsfzmmc%3E%3Cxm%3E%25E7%258E%258B%25E4%25BB%2581%25E4%25BF%258A%3C%2Fxm%3E%3Ckskm%3E3%3C%2Fkskm%3E%3Ctoken%3E5585CF567589203FEE5497D48F3C57D6%3C%2Ftoken%3E%3Chphm%3E%25E6%25B5%2599A8615%3C%2Fhphm%3E%3Cly%3EA%3C%2Fly%3E%3Cksdd%3E";
		String postBody = "3301022%3C%2Fksdd%3E%3Cksrq%3E2015-05-18%3C%2Fksrq%3E%3Ckscc%3E51%3C%2Fkscc%3E%3C%2FWriteCondition%3E%3C%2Froot%3E";

//		army.prepare(head, preBody + postBody, System.currentTimeMillis() + 1 * 60 * 1000);
//		army.shot("", System.currentTimeMillis());
//		army.dismiss();
		
		army.prepare(head, preBody, System.currentTimeMillis() + 2 * 60 * 1000);
		army.allShot(postBody, System.currentTimeMillis());
	}
	
	private ArrayList<Shooter> shooters = new ArrayList<Shooter>();
	
	public Army(String host, int port, int count) {
		for(int i = 0; i < count; i++) {
			shooters.add(new Shooter(host, port));
		}
	}
	
	public boolean allAsked() {
		for(Shooter s : shooters) {
			if(!s.getAsked()){
				return false;
			}
		}
		
		return true;
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
//			if(!shooter.hasShot()){
				shooter.getGun().discard();				
//			}
		}
	}
}
