package cn.hzjkyy;


import cn.hzjkyy.tool.Log;

public class LogTest {

	public static void main(String[] args) {
		long start = System.nanoTime();
		log();
		long end = System.nanoTime();
		long logCost = end - start;
		

		start = System.nanoTime();
		print();
		end = System.nanoTime();
		System.out.println("Log cost: " + String.format("%,d", logCost));
		System.out.println("Print cost: " + String.format("%,d", (end - start)));
	}
	
	private static void log() {
		Log log = Log.getLog("application");
		for(int i = 0; i < 90010; i++){
			log.record("Yes, it 多发发达放大发发发");			
		}
	}
	
	private static void print() {
		for(int i = 0; i < 100010; i++){
			System.out.println("Yes, it 多发发达放大发发发");			
		}
	}
}
