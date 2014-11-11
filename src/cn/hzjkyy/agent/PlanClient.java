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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.hzjkyy.Single;
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
	
	public String hello() {
		String serverUrl = "http://" + getHost() + "/plans/hello";
		HttpPost httpPost = new HttpPost(serverUrl);
		CloseableHttpResponse httpResponse = null;
				
		try {
			List<NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("program_version", Single.programVersion));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	        httpResponse = httpclient.execute(httpPost);
	        
	        int status = httpResponse.getStatusLine().getStatusCode();
	        if (status >= 200 && status < 300){
	        	return EntityUtils.toString(httpResponse.getEntity());
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
		
		return null;
	}

	public ArrayList<Plan> fetch(int serverId) {
		ArrayList<Plan> plans = new ArrayList<Plan>();
		String serverUrl = "http://" + getHost() + "/plans/fetch";
		HttpPost httpPost = new HttpPost(serverUrl);
		CloseableHttpResponse httpResponse = null;
				
		try {
			List<NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("server_id", "" + serverId));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	        httpResponse = httpclient.execute(httpPost);
	        
	        int status = httpResponse.getStatusLine().getStatusCode();
	        if (status >= 200 && status < 300){
	        	String response = EntityUtils.toString(httpResponse.getEntity());
	        	
	        	String[] plansString = response.split("phantom");
	        	
	        	for(String planString : plansString){
	        		if(planString.trim().length() <= 0){
	        			continue;
	        		}
	        		Plan plan = new Plan();
		        	plan.setId(Integer.parseInt(getValue(planString, "id")));
		        	plan.setSfzmhm(getValue(planString, "sfzmhm"));
		        	plan.setPass(getValue(planString, "pass"));
		        	plan.setKsdd(getValue(planString, "ksdd"));
		        	plan.setStartKsrq(getValue(planString, "start_ksrq"));
		        	plan.setEndKsrq(getValue(planString, "end_ksrq"));
		        	plans.add(plan);
	        	}
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
		
		return plans;
	}
	
	public void report(Plan plan, String ksrq, boolean success) {
		String serverUrl = "http://" + getHost() + "/plans/" + plan.getId() + "/report";
		HttpPost httpPost = new HttpPost(serverUrl);
		CloseableHttpResponse httpResponse = null;
				
		try {
			List<NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("ksrq", ksrq));
			nvps.add(new BasicNameValuePair("success", success ? "2" : "3"));
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
	
	public boolean over(){
		boolean over = false;
		String serverUrl = "http://" + getHost() + "/plans/over";
		HttpGet httpGet = new HttpGet(serverUrl);
		CloseableHttpResponse httpResponse = null;
				
		try {
	        httpResponse = httpclient.execute(httpGet);
	        int status = httpResponse.getStatusLine().getStatusCode();
	        if (status >= 200 && status < 300){
	        	String response = EntityUtils.toString(httpResponse.getEntity());
	        	
	        	if(response.contains("计划结束")){
	        		over = true;
	        	}
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
		
		return over;
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
