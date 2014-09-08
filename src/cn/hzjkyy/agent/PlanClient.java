package cn.hzjkyy.agent;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import cn.hzjkyy.model.Plan;

public class PlanClient {
	//浏览器引擎（阻塞性）
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	private String host = "localhost:3000";
	//private String host = "hzjkyy.sjyyt.com";

	public Plan fetch() {
    	Plan plan = new Plan();
		String serverUrl = "http://" + host + "/plans/fetch";
		HttpPost httpPost = new HttpPost(serverUrl);
		CloseableHttpResponse httpResponse = null;
				
		try {
	        httpResponse = httpclient.execute(httpPost);
	        
	        int status = httpResponse.getStatusLine().getStatusCode();
	        if (status >= 200 && status < 300){
	        	String response = EntityUtils.toString(httpResponse.getEntity());
	        	plan.setId(Integer.parseInt(getValue(response, "id")));
	        	plan.setSfzmhm(getValue(response, "sfzmhm"));
	        	plan.setPass(getValue(response, "pass"));
	        }
		} catch (ParseException | IOException e) {
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (IOException e) {
				}				
			}
		}
		
		return plan;

	}
	
	public void report() {
		
	}
	
	private String getValue(String response, String key){
		Pattern p = Pattern.compile(String.format("<%s>(.+)<%s>", key, key));
		Matcher m = p.matcher(response);
		String value = null;
		if (m.find()) {
			value = m.group(1);
		}
		return value;		
	}
}
