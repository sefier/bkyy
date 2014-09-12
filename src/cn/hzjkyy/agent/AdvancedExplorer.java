package cn.hzjkyy.agent;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import cn.hzjkyy.model.Request;
import cn.hzjkyy.tool.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.concurrent.FutureCallback;

public class AdvancedExplorer extends Explorer {
    ExecutorService threadpool;
    Async async;
    Queue<Future<Content>> queue = new LinkedList<Future<Content>>();
	
	public AdvancedExplorer(int timeout, int poolsize) {
		super(timeout);
	    threadpool = Executors.newFixedThreadPool(poolsize);
	    async = Async.newInstance().use(threadpool);
	}
	
	public void close() {
		for(Future<Content> future : queue){
			if(!future.isDone()){
				future.cancel(true);
			}
		}
        threadpool.shutdown();
        super.close();
	}

	//异步访问某个地址，快速返回，并在成功访问之后，将内容写在tab上
	@Override
	void sendAsyncRequest(final Tab tab) {
		Request request = tab.getRequest();
		List<NameValuePair> nvps = generateNvps(request);
		org.apache.http.client.fluent.Request fluentRequest = org.apache.http.client.fluent.Request
				.Post("http://service.zscg.hzcdt.com/api/httpapi")
				.connectTimeout(timeout)
                .socketTimeout(timeout)
                .bodyForm(nvps);
		Future<Content> future = async.execute(fluentRequest, new FutureCallback<Content>() {
			public void record(String exceptionString){
		    	explorerLog.record("请求接口：" + tab.getRequest().getJkid());
		    	explorerLog.record("请求正文：" + tab.getRequest().getXmlDoc());
		    	explorerLog.record("响应：" + tab.getResponse().getResponseBody());
		    	explorerLog.record("响应状态：" + tab.getResponse().getStatusPanel().getStatus());
		    	if(exceptionString != null){
		        	explorerLog.record("异常信息：" + exceptionString);    		
		    	}
		    	explorerLog.record("耗时：" + (System.currentTimeMillis() - tab.getRequest().getSentAt()));
			}
			
            public void failed(Exception ex) {
            	tab.getResponse().getStatusPanel().error();
            	record(Log.exceptionStacktraceToString(ex));
            	tab.retry();
            }

            public void completed(Content content) {
            	tab.getResponse().getStatusPanel().success();
            	tab.getResponse().setResponseBody(content.toString());
            	record(null);
            }

            public void cancelled() {
            	tab.getResponse().getStatusPanel().setStatus(-2);
            	record(null);
            	tab.retry();
            }
        });
		queue.add(future);
	}
}
