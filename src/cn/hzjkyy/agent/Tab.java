package cn.hzjkyy.agent;

import cn.hzjkyy.model.Request;
import cn.hzjkyy.model.Response;

public class Tab {
	private Explorer explorer;
	public Tab(Explorer explorer) {
		this.explorer = explorer;
	}
	
	//将窗口从浏览器中剥离
	public void close() {
		explorer = null;
	}
	public boolean isClosed() {
		return explorer != null;
	}
	
	//当前窗口的请求
	private Request request;
	public Request getRequest() {
		return request;
	}

	//当前窗口的响应
	private Response response = new Response();
	public Response getResponse() {
		return response;
	}
	
	//访问，直至返回结果
	public Response visit(Request request) {
		visit(request, false);
		return this.response;
	}
	private int tries = 0;
	//只是在浏览器中键入地址访问而已
	public Tab visit(Request request, boolean async) {
		tries++;
		response.clear();
		request.setSentAt(System.currentTimeMillis());
		this.request = request;
		if(async){
			explorer.sendAsyncRequest(this);
		}else{
			explorer.sendRequest(this);
		}
		return this;
	}
	
	public void retry(){
		if(tries < 3){
			explorer.getExplorerLog().record("第" + tries + "次重试");
			visit(request, true);
		}
	}
	
	public int getTries(){
		return tries;
	}
	
	public boolean isOver() {
		int status = response.getStatusPanel().getStatus();
		return status > 0 || status < 0 && tries >= 3;
	}
}
