package cn.hzjkyy.generator;

import cn.hzjkyy.model.Request;

public abstract class Generator {
	public abstract String getJkid();
	public abstract String getXmlDoc();
	
	public Request generate() {
		return new Request(getJkid(), getXmlDoc());
	}
}
