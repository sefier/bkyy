package cn.hzjkyy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hzjkyy.agent.PlanClient;

public class YzmUpload {

	public static void main(String[] args) {	
		PlanClient planClient = new PlanClient(false);
		Pattern p = Pattern.compile("\\d{6}");

		do {
			//读取文件
			File folder = new File(Paths.get("").toAbsolutePath().toString());
			for(File file : folder.listFiles()){
				if(file.getName().startsWith("360") && file.getName().endsWith(".csv")){
					System.out.println("识别文件" + file.getAbsolutePath());
					BufferedReader br = null;
					String line = "";
					String cvsSplitBy = ",";
				 
					try {
						br = new BufferedReader(
								   new InputStreamReader(
						                      new FileInputStream(file), "UTF8"));
						//br = new BufferedReader(new FileReader(file));
						Map<String, YzmRecord> yzms = new HashMap<String, YzmRecord>();
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						while ((line = br.readLine()) != null) {
							System.out.println("读取中");
							String[] dxLine = line.split(cvsSplitBy);
							if(dxLine.length >= 7){
								String sjhm = dxLine[5].substring(1);
								if(sjhm.startsWith("+86")){
									sjhm = sjhm.substring(3);
								}
								String message = dxLine[8];
								String code = "";
								
								Matcher m = p.matcher(message);
								if (m.find()) {
									code = m.group();
									if(code.length() == 6){
										long timestamp = 0;
										try {
											timestamp = dateFormat.parse(dxLine[6]).getTime();
										} catch (ParseException e) {
										}
										
										YzmRecord yzmRecord = new YzmRecord(timestamp, code);
										
										YzmRecord oldYzmRecord = yzms.get(sjhm);
										if(oldYzmRecord == null || oldYzmRecord.timestamp < yzmRecord.timestamp){
											yzms.put(sjhm, yzmRecord);									
										}
									}
								}								
							}
						}
						
					    StringBuilder sb = new StringBuilder();
					    Set<String> sjhms = yzms.keySet();
					    Iterator<String> iterator = sjhms.iterator();

					    if(iterator.hasNext()){
					    	String sjhm = iterator.next();
					    	YzmRecord yzmRecord = yzms.get(sjhm);
					    	sb.append(sjhm + "," + yzmRecord.yzm);
					    }
						while(iterator.hasNext()) {
							sb.append(";");
					    	String sjhm = iterator.next();
					    	YzmRecord yzmRecord = yzms.get(sjhm);
					    	sb.append(sjhm + "," + yzmRecord.yzm);
						}
					    
					    String yzm = sb.toString();
					    planClient.uploadYzm(yzm);
					    System.out.println("验证码上传" + yzm);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (br != null) {
							try {
								br.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					file.setWritable(true);
					if(!file.delete()){
						System.out.println("未删除成功!");
					}
				}
			}
			
			try {
				Thread.sleep(5000);
				System.out.println("等待");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(true);
	}

}
