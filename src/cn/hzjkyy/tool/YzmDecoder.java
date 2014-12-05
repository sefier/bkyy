package cn.hzjkyy.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

public class YzmDecoder {
	private UuApi uuApi;
	public YzmDecoder() {
		uuApi = new UuApi();
		uuApi.setSoftInfo("101749", "c5964b2abbc6427f886d2deda1973a2a");
		uuApi.userLogin("sefier", "AnLu@203");
	}
	
	private static Map<String, String> decodeResult = new HashMap<String, String>();
	
	public String fetch(String l){
		return decodeResult.get(l);
	}
	
	public void reportError(String l){
		uuApi.reportError(l);
	}
	
	public String decode(String yzmPic){
		String fileName = String.format("%s_%d", UUID.randomUUID().toString().replaceAll("-", ""), System.currentTimeMillis());
		String filePath = Log.currentPath.resolve(fileName + ".jpeg").toString();
		
		File file = new File(filePath);			

		try {
			if(!file.exists()){
				file.createNewFile();
			}
			byte[] data = Base64.decodeBase64(yzmPic);

			try (OutputStream stream = new FileOutputStream(file)) {
			    stream.write(data);
			}
		} catch (IOException e) {
		}

		String l = uuApi.upload(filePath, "8002", false);
		
		String r = null;
		for(int i = 0; i < 30; i++){
			r = uuApi.getResult(l);
			System.out.println("识别结果：" + r);
			if(r.equals("-3")){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}				
			}else{
				break;
			}
		}
		
		decodeResult.put(l, r);
		
		return l;
	}
}
