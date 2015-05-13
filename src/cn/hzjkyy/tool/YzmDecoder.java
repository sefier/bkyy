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
		String result = "";
		do {
			result = uuApi.userLogin("sefier", "wind&limit");
			System.out.println("登录结果：" + result);
		}while(!result.equals("554752"));
		decodeResult.put("-10000", "");
	}
	
	private static Map<String, String> decodeResult = new HashMap<String, String>();
	
	public String fetch(String l){
		return decodeResult.get(l);
	}
	
	public void reportError(String l){
		uuApi.reportError(l);
	}
	
	public String decode(String yzmPic){
		String l = "-10000";
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

			//上传验证码图片
			int tries = 0;
			do {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}

				tries++;
				l = uuApi.upload(filePath, "8002", false);
				
				if(tries > 3){
					break;
				}
			}while(l.startsWith("-"));
			
			String r = uuApi.getResult(l);
			
			System.out.println("识别结果：" + r);
			
			decodeResult.put(l, r);
		} catch (IOException e) {
			System.out.println("上传验证码图片错误");
			e.printStackTrace();
		}
		
		return l;
	}
}
