package cn.hzjkyy.agent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import cn.hzjkyy.Single;
import cn.hzjkyy.model.Plan;
import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;
import cn.hzjkyy.tool.Log;

public class Explorer {
	public void close() {
		tabs.clear();
		explorerLog.close();
	}
	private int offset = 3000;
	
	public int getOffset(){
		return this.offset;
	}

	//浏览器请求信息
	private String serverUrl = "http://service.zscg.hzcdt.com/api/httpapi";
	private List<NameValuePair> basicNvps = new ArrayList <NameValuePair>();
	
	//浏览器引擎（阻塞性）
	private CloseableHttpClient httpclient = HttpClients.createDefault();
	private HttpPost httpPost = new HttpPost(serverUrl);

	protected int timeout;
	protected Log explorerLog; 
	private int limits = 300;
	
	public int getLimits() {
		return limits;
	}
	public void setLimits(int limits) {
		this.limits = limits;
	}
	public Log getExplorerLog() {
		return explorerLog;
	}
	public Explorer(String name){
		//初始化浏览器请求信息
		explorerLog = Log.getLog(plan, "explorer" + "-" + name);
		basicNvps.add(new BasicNameValuePair("xlh", "0C2B3243AFCB169B0E0C07533816A4D3"));		
	}
	
	private Plan plan;
	public Explorer(Plan plan){
		this.plan = plan;
		//初始化浏览器请求信息
		explorerLog = Log.getLog(plan, "explorer");
		basicNvps.add(new BasicNameValuePair("xlh", "0C2B3243AFCB169B0E0C07533816A4D3"));
		httpPost.setHeader(HTTP.USER_AGENT, "car/1.1 CFNetwork/672.1.14 Darwin/14.0.0");

	}
	
	private void setTimeout(int timeout){
		this.timeout = timeout;
		
		//初始化阻塞性浏览器引擎
		RequestConfig params = RequestConfig.custom().setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		httpPost.setConfig(params);
	}
	
	public Explorer(Plan plan, int timeout){
		this(plan);
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
	
	private boolean checkingMode = true;
	public void setCheckingMode(boolean checkingMode){
		this.checkingMode = checkingMode;
	}
	//启动浏览器引擎，访问某个地址，并将结果设定在tab上
	void sendRequest(Tab tab) throws UnloginException, RetryException, StopException, PauseException {
		Request request = tab.getRequest();
		//设定请求参数
		List<NameValuePair> nvps = generateNvps(request);
		
		//开始访问
		CloseableHttpResponse httpResponse = null;
		Response response = tab.getResponse();
		String exceptionString = null;

		int tries = 0;
		long tryStartAt = System.currentTimeMillis();
		do{
	    	//检查服务器指令
	    	int serverStatus = Single.status();
	    	
	    	if(serverStatus == 3){
	    		throw new StopException("服务器指示：3");
	    	}else if(checkingMode && serverStatus != 1 && serverStatus != plan.getId()){
	    		throw new RetryException("服务器指示：" + serverStatus);
	    	}				

			tries++;
			request.setSentAt(System.currentTimeMillis());
			exceptionString = null;
			try {
		        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			    httpResponse = httpclient.execute(httpPost);

			    int status = httpResponse.getStatusLine().getStatusCode();
		        if (status >= 200 && status < 300){
		        	String responseString = EntityUtils.toString(httpResponse.getEntity());
		        	if(responseString.contains("JDBC")){
			        	exceptionString = "JDBC异常:" + responseString;
		        	}else if(responseString.contains("您的系统未在本地址正确注册")){
			        	exceptionString = "未注册异常:" + responseString;
		        	}else if(responseString.contains("登录已超时")){
		        		throw new UnloginException();
		        	}else if(responseString.contains("后再按确定按钮") || responseString.contains("秒后再按预约按钮")){
		        		exceptionString = responseString;
		        		Pattern p1 = Pattern.compile("请\\[(\\d+)\\]秒后再按");
		        		Matcher m1 = p1.matcher(responseString);
		        		String value = null;
		        		if (m1.find()) {
		        			value = m1.group(1);
		        		}else{
		        			value = "32";
		        		}
		        		
		        		if(value != null){
		        			try {
		        				int sleep = Integer.parseInt(value);
		        				int second = sleep * 1000;
		        				
								Thread.sleep(second);
								if(sleep < 30000){
									offset += sleep;
								}
							} catch (NumberFormatException e) {
							} catch (InterruptedException e) {
							}		        			
		        		}
		        		throw new RetryException("按钮点击过早");
		        	}else if(responseString.contains("系统检测到您的账号访问过于频繁")){
		        		throw new StopException("访问过于频繁");
		        	}else if(responseString.contains("你的操作已超时")){
		        		throw new RetryException("操作超时");
		        	}else if(responseString.contains("Cursor")){
		        		throw new RetryException("Cursor is closed");
//		        	}else if(responseString.contains("该考点截止已无可用名额") || responseString.contains("更换其他时间")){
//		        		throw new PauseException("考点无名额");
		        	}else if(responseString.contains("密码错误")){
		        		throw new StopException("密码错误");
		        	}else if(responseString.contains("恶意刷名额嫌疑")){
		        		throw new StopException("恶意刷名额嫌疑");
		        	}else{
			        	response.getStatusPanel().success();
			        	response.setResponseBody(responseString);
			        	break;
		        	}
		        }else{
		        	exceptionString = "Status:" + status + EntityUtils.toString(httpResponse.getEntity());
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
			
			explorerLog.record("异常信息：" + exceptionString); 
	    	explorerLog.record("耗时：" + (System.currentTimeMillis() - request.getSentAt()));
	    	
	    	if(System.currentTimeMillis() - tryStartAt > 5 * 60 * 1000){
	    		throw new RetryException("请求超时");
	    	}
		}while(tries < getLimits());

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
}
