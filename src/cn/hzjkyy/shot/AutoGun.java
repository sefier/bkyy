package cn.hzjkyy.shot;

import java.util.ArrayList;

public class AutoGun extends Gun implements Runnable {
	public static void main(String[] args) throws InterruptedException {
		AutoGun gun = new AutoGun("121.199.52.17", 80);
		String head = "POST /api/httpapi HTTP/1.1\r\nConnection:close\r\nContent-Length: 328\r\nContent-Type: application/x-www-form-urlencoded\r\nHost: service.zscg.hzcdt.com\r\n\r\n";
		String body = "xlh=0C2B3243AFCB169B0E0C07533816A4D3&jkid=A001665&xmlDoc=%3C%3Fxml+version%3D%221.0%22+encoding%3D%22utf-8%22%3F%3E%3Croot%3E%3CQueryCondition%3E%3Cpass%3E119734%3C%2Fpass%3E%3CloginId%3E330184199310123920%3C%2FloginId%3E%3Cdevice_token%3Eb5da1c4bbb894b9ebe81bd61d3341a75%3C%2Fdevice_token%3E%3C%2FQueryCondition%3E%3C%2Froot%3E";

		//自动步枪
		long start = System.currentTimeMillis();
		gun.load(new Ball(head, start + 3 * 1000, start + 5 * 1000));
		gun.load(new Ball(body, start, start + 3 * 1000));
		gun.setViewLimit(10);
		gun.fillFull();
	}
	
	private Thread thread = new Thread(this); 
	public AutoGun(String host, int port) {
		super(host, port);
		thread.start();
	}
		
	private int viewLimit = 30;
	public void setViewLimit(int viewLimit) {
		this.viewLimit = viewLimit;
	}
	
	public void discard() {
		thread.interrupt();
	}

	//弹药匣子
	private ArrayList<Ball> balls = new ArrayList<Ball>();
	//装填子弹
	public void load(Ball ball) {
		for(Ball b : balls){
			if(b.getStartFire() > ball.getStartFire()){
				b.setStartFire(ball.getStartFire());				
			}
			if(b.getStopFire() > ball.getStartFire()){
				b.setStopFire(ball.getStartFire());				
			}
		}
		balls.add(ball);
	}

	//是否已经填满弹匣
	private boolean full = false;
	//子弹装满
	public void fillFull() {
		full = true;
	}
	
	private Target report = new Target(null, 4);
	public Target getReport(){
		return report;
	}

	//目前正打算射击的子弹
	private int position = 0;
	public void run() {
		try{
			//射出子弹
			do {
				int result = waitAndCheck(100);
				if(result == 0){ //射击子弹
					Ball currentBall = balls.get(position);
					
					int length = currentBall.getContent().length();
					
					while(System.currentTimeMillis() < currentBall.getStartFire()){
						wait(100);
					}

					for(int i = 0; i < length; i++){
						fire(currentBall.getContent().charAt(i));
												
						if( i < length - 1) { //如果这个子弹链条，还有子弹
							int rest = (int)(currentBall.getStopFire() - System.currentTimeMillis());
							if(rest > 0){ //还有时间
								wait(rest / (length - (i + 1)));
							}else{ //没有时间了，直接全部打光
								fire(currentBall.getContent().substring(i + 1, currentBall.getContent().length()));
								break;
							}						
						}
					}
					position++;
				}else if(result == 2){ //装弹结束
					break;
				}
			}while(true);

			//检查靶标
			report = new Target(null, 5);
			report = viewTarget(viewLimit);
		}catch(InterruptedException e){
			report = new Target(null, 3);
			Shooter.record("手枪被扔掉");
		}catch(GunMalfunctionException e){
			report = new Target(null, 2);
			Shooter.record("手枪失灵");
		}finally{
			//关上枪栓
			push();
		}		
	}
	
	//检查弹匣：0）有新的子弹，处理 1）没有新的子弹，等待 2）没有新的子弹，并且装弹结束，break
	private int waitAndCheck(int duration) throws InterruptedException{
		int result = 0;
		if(balls.size() <= position){
			result = full ? 2 : 1;
			if(result == 1){
				wait(duration);
			}			
		}
		
		return result;
	}
		
	private void wait(int duration) throws InterruptedException {
		if(thread.isInterrupted()){
			throw new InterruptedException();	
		}
		Thread.sleep(duration);
	}	

}
