package cn.hzjkyy.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

public class YzmDecoder {
	private UuApi uuApi;
	public YzmDecoder() {
		uuApi = new UuApi();
		uuApi.setSoftInfo("101749", "c5964b2abbc6427f886d2deda1973a2a");
		uuApi.userLogin("sefier", "iamtmx203");
	}
	
	public String decode(String yzmPic){
		String fileName = String.format("%s_%d", UUID.randomUUID().toString().replaceAll("-", ""), System.currentTimeMillis());
		String filePath = Log.currentPath.resolve(fileName + ".jpeg").toString();
		
		File file = new File(filePath);			

		try {
			if(!file.exists()){
				file.createNewFile();
			}
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			    out.print(yzmPic);
			}catch (IOException e) {
			}
		} catch (IOException e) {
		}

		String l = uuApi.upload(filePath, "8001", false);
		
		String r = null;
		for(int i = 0; i < 5; i++){
			r = uuApi.getResult(l);
			if(r.length() == 4){
				break;
			}else{
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}
		
		return r;
	}
}
