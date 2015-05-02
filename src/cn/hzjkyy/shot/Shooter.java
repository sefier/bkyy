package cn.hzjkyy.shot;

public class Shooter {
	private static int headTotal = 60;
	private static int headInterval = 60;
	
	private static int bodyInterval = 5;
	private static int bodyTotal = 120;
	private static int postBodyLength = (int)Math.ceil((float)bodyTotal / bodyInterval) + 1;;
	
	private static int total = 150;
	
	public static void main(String[] args) throws Exception {
		Shooter shooter = new Shooter("121.199.52.17", 80);
		String head = "POST /api/httpapi HTTP/1.1\r\nConnection:close\r\nContent-Type: application/x-www-form-urlencoded\r\nHost: service.zscg.hzcdt.com\r\nContent-Length: 328\r\n\r\n";
		String preBody = "jkid=A001665&xlh=0C2B3243AFCB169B0E0C07533816A4D3&xmlDoc=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CQueryCondition%3E%3CloginId%3E330184199310123920%3C%2FloginId%3E%";
		String postBody = "3Cpass%3E119734%3C%2Fpass%3E%3Cdevice_token%3Eb5da1c4bbb894b9ebe81bd61d3341a75%3C%2Fdevice_token%3E%3C%2FQueryCondition%3E%3C%2Froot%3E";
		
		// 等到指定时间射出
//		long expectedShot = System.currentTimeMillis() + 3 * 1000;
//		shooter.shot(head, body, expectedShot);
		
		// 先预备，然后在指定时间射出其他部分
//		long lastSentAt = System.currentTimeMillis() + 3 * 60 * 1000;
//		long realSentAt = System.currentTimeMillis() + 35 * 1000;
//		shooter.prepare(head, preBody, lastSentAt);
//		Thread.sleep(35 * 1000);
//		shooter.shot(postBody, realSentAt);
		
		// 先预备，然后直接在指定时间射出
//		long lastSentAt = System.currentTimeMillis() + 3 * 60 * 1000;
//		long realSentAt = System.currentTimeMillis() + 10 * 1000;
//		shooter.prepare(head, preBody + postBody, lastSentAt);
//		Thread.sleep(5 * 1000);
//		shooter.shot(realSentAt);
		
		// 先预备，然后直接射出
		long lastSentAt = System.currentTimeMillis() + 3 * 60 * 1000;
		shooter.prepare(head, preBody + postBody, lastSentAt);
		shooter.shot();
		System.out.println();
	}
	
	public static void record(Object content) {
		System.out.print(content);
	}
	
	public static void recordln(Object content) {
		System.out.println(content);
	}
		
	private AutoGun gun;
	private boolean asked = false;
	public Shooter(String host, int port) {
		gun = new AutoGun(host, port);
	}
	
	public void setAsked(boolean asked) {
		this.asked = asked;
	}
	
	public boolean getAsked() {
		return asked;
	}

	public AutoGun getGun() {
		return gun;
	}
	
	public void waitGunFinished() {
		while(!gun.getReport().isFinished()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public boolean hasShot() {
		return gun.isFull();
	}
		
	public void shot(String head, String body, long sentAt) {
		prepare(head, body, sentAt);
		gun.fillFull();
	}

	public void prepare(String head, String body, long aboutSentAt) {
		String preBody = "";
		String postBody = body;
		if(body.length() >= postBodyLength){
			preBody = body.substring(0, body.length() - postBodyLength);
			postBody = body.substring(preBody.length(), body.length());
		}

		int postBodyTime = (postBody.length() - 1) * bodyInterval;
		int headTime = Math.min(Math.min((head.length() - 1) * headInterval, headTotal), total - postBodyTime);
		
		long postBodyStart = aboutSentAt - postBodyTime * 1000;
		long headStart = postBodyStart - headTime * 1000;
		gun.load(new Ball(head, headStart, postBodyStart));
		gun.load(new Ball(preBody, postBodyStart, postBodyStart));
		gun.load(new Ball(postBody, postBodyStart, aboutSentAt));
	}
	
	public void shot() {
		shot(0);
	}
	
	public void shot(long sentAt) {
		shot("", sentAt);
	}
	
	public void shot(String body, long sentAt) {
		gun.load(new Ball(body, sentAt - (body.length() - 1) * bodyInterval * 1000, sentAt));
		gun.fillFull();
	}
}
