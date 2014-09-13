package cn.hzjkyy.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.hzjkyy.model.Plan;

public class PlanClient {
	//浏览器引擎（阻塞性）
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	private String testHost = "localhost:3000";
	private String host = "hzjkyy.sjyyt.com";
	private boolean isTest;
	public PlanClient(boolean isTest){
		this.isTest = isTest;
	}
	private String getHost(){
		return isTest ? testHost : host;
	}

	public Plan fetch() {
    	Plan plan = new Plan();
		String serverUrl = "http://" + getHost() + "/plans/fetch";
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
	        	plan.setKsdd(getValue(response, "ksdd"));
	        	plan.setKsrq(getValue(response, "ksrq"));
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
	
	public void report(Plan plan, String ksrq, boolean success) {
		String serverUrl = "http://" + getHost() + "/plans/" + plan.getId() + "/report";
		HttpPost httpPost = new HttpPost(serverUrl);
		CloseableHttpResponse httpResponse = null;
				
		try {
			List<NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("ksrq", ksrq));
			nvps.add(new BasicNameValuePair("success", new Boolean(success).toString()));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	        httpResponse = httpclient.execute(httpPost);
		} catch (ParseException | IOException e) {
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (IOException e) {
				}				
			}
		}
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
