package cn.hzjkyy.tool;
 
import java.io.IOException;
 
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
 
public class OcsClient {
    static final String host = "bb74434e335711e4.m.cnhzalicm10pub001.ocs.aliyuncs.com";//控制台上的“内网地址”
    static final String port ="11211"; //默认端口 11211，不用改
    static final String username = "bb74434e335711e4";//控制台上的“访问账号”
    static final String password = "AnLu_203";//邮件或短信中提供的“密码”
    static MemcachedClient cache = null;
    static {
    	AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"}, new PlainCallbackHandler(username, password));
    	try {
			cache = new MemcachedClient(
			        	new ConnectionFactoryBuilder().setProtocol(Protocol.BINARY)
			        		.setAuthDescriptor(ad)
			        		.build(),
			        	AddrUtil.getAddresses(host + ":" + port));
		} catch (IOException e) {
		}
    }
    
	public static void set(String key, String value) {
		if(cache != null){
	        cache.set(key, 12 * 3600, value);			
		}
	}
	
	public static Object get(String key) {
		if(cache != null){
			return cache.get(key);
		}else{
			return null;
		}
	}
}  