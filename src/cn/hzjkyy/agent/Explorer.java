package cn.hzjkyy.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.tool.Log;

public class Explorer {
	public void close() {
		tabs.clear();
		explorerLog.write();
		explorerLog.upload();
	}
	//浏览器请求信息
	private String serverUrl = "http://service.zscg.hzcdt.com/api/httpapi";
	private List<NameValuePair> basicNvps = new ArrayList <NameValuePair>();
	
	//浏览器引擎（阻塞性）
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	private HttpPost httpPost = new HttpPost(serverUrl);

	protected int timeout;
	protected Log explorerLog; 
	
	public Log getExplorerLog() {
		return explorerLog;
	}
	public Explorer(String name){
		//初始化浏览器请求信息
		explorerLog = Log.getLog("explorer" + "-" + name);
		basicNvps.add(new BasicNameValuePair("xlh", "0C2B3243AFCB169B0E0C07533816A4D3"));		
	}
	
	public Explorer(){
		//初始化浏览器请求信息
		explorerLog = Log.getLog("explorer");
		basicNvps.add(new BasicNameValuePair("xlh", "0C2B3243AFCB169B0E0C07533816A4D3"));		
	}
	
	private void setTimeout(int timeout){
		this.timeout = timeout;
		
		//初始化阻塞性浏览器引擎
		RequestConfig params = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(params);
	}
	
	public Explorer(int timeout){
		this();
		setTimeout(timeout);
	}
	
	//浏览器可以打开多个页面
	private List<Tab> tabs = new ArrayList<Tab>();
	public Tab newTab() {
		Tab tab = new Tab(this);
		tabs.add(tab);
		return new Tab(this);
	}
	public void closeTab(Tab tab) {
		tab.close();
		tabs.remove(tab);
	}
	
	protected List<NameValuePair> generateNvps(Request request) {
		List<NameValuePair> nvps = new ArrayList <NameValuePair>(basicNvps);
		nvps.add(new BasicNameValuePair("jkid", request.getJkid()));
		nvps.add(new BasicNameValuePair("xmlDoc", request.getXmlDoc()));
		
		return nvps;
	}
	
	//启动浏览器引擎，访问某个地址，并将结果设定在tab上
	void sendRequest(Tab tab) {
		Request request = tab.getRequest();
		//设定请求参数
		List<NameValuePair> nvps = generateNvps(request);
		
		//开始访问
		CloseableHttpResponse httpResponse = null;
		Response response = tab.getResponse();
		String exceptionString = null;
				
		try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
	        httpResponse = httpclient.execute(httpPost);
	        
	        int status = httpResponse.getStatusLine().getStatusCode();
	        if (status >= 200 && status < 300){
	        	response.getStatusPanel().success();
	        	response.setResponseBody(EntityUtils.toString(httpResponse.getEntity()));
	        }else{
	        	exceptionString = "" + status + EntityUtils.toString(httpResponse.getEntity());
	        }
		} catch (ParseException | IOException e) {
			exceptionString = Log.exceptionStacktraceToString(e);
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (IOException e) {
				}				
			}
		}

    	response.getStatusPanel().finish(false);
    	explorerLog.record("请求接口：" + tab.getRequest().getJkid());
    	explorerLog.record("请求正文：" + tab.getRequest().getXmlDoc());
    	explorerLog.record("响应：" + response.getResponseBody());
    	explorerLog.record("响应状态：" + response.getStatusPanel().getStatus());
    	if(exceptionString != null){
        	explorerLog.record("异常信息：" + exceptionString);    		
    	}
    	explorerLog.record("耗时：" + (System.currentTimeMillis() - request.getSentAt()));
	}
	
	void sendAsyncRequest(Tab tab) {
		sendRequest(tab);
	}
}
