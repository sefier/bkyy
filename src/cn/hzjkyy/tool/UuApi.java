package cn.hzjkyy.tool;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

/*
 * 说明：此类函数是优优云图片识别平台的API接口,调用类中的函数可以进行图片识别 优优云官网：www.uuwise.com QQ：87280085
 * 
 * 类中的公有函数： setSoftInfo($softID,$softKey); //设置软件ID和KEY
 * userLogin($userName,$passWord); //用户登录,登录成功返回用户的ID
 * getPoint($userName,$passWord); //获取用户剩余题分 upload($imagePath,$codeType);
 * //根据图片路径上传,返回验证码在服务器的ID,$codeType取值查看：http://www.uuwise.com/price.html
 * getResult($codeID); //根据验证码ID获取识别结果 autoRecognition($imagePath,$codeType);
 * //将upload和getResult放到一个函数来执行,返回验证码识别结果 reportError($codeID); //识别结果不正确报错误
 * regUser($userName,$userPassword) //注册新用户,注册成功返回新用户的ID pay($userName,$Card);
 * //充值题分，充值成功返回用户当前题分
 * 
 * 类中的公有变量： $macAddress='00e021ac7d'; //客户机的mac地址,服务器暂时没有用,后期准备用于绑定mac地址 赋值方法：
 * $obj->macAddress='00e021ac7d'; $timeOut='60000'; //超时时间,建议不要改动此值 赋值方法：
 * $obj->timeOut=60000;
 * 
 * 函数调用方法： 需要先new一个对象 $obj=new uuApi;
 * $obj->setSoftInfo('2097','b7ee76f547e34516bc30f6eb6c67c7db');
 * //如何获取这两个值？请查看这个页面：http://dll.uuwise.com/index.php?n=ApiDoc.GetSoftIDandKEY
 * $obj->userLogin('userName','userPassword');
 * $result=autoRecognition($imagePath,$codeType);
 */
@SuppressWarnings( { "unchecked", "unused" })
public class UuApi {

	private String softID; // 软件ID
	private String softKEY;// 软件 KEY
	private String userName;// 用户账户
	private String userPassword;// 用户密码

	private String uid; //
	private String userKey;
	private String softContentKEY;

	private String uuUrl;
	private String uhash;
	private String uuVersion = "1.1.0.1";
	private String userAgent;
	private String gkey;

	// public String macAddress = "00-FF-CD-9B-8D-E8";
	public String macAddress = "b8:ca:3a:f3:75:5f";
	//public String macAddress = "b8ca3af3755f";
	// 客户机的mac地址,服务器暂时没有用,后期准备用于绑定mac地址
	// 赋值方法：
	// $obj->macAddress='00e021ac7d';
	public int timeOut = 24000;

	// 超时时间,建议不要改动此值 赋值方法： $obj->timeOut=60000;

	// 设置软件ID和KEY
	public String setSoftInfo(String id, String key) {
		// if(id.isEmpty() && key.isEmpty()){
		this.softID = id;
		this.softKEY = key;
		this.uhash = Md5.MD5(id + key.toUpperCase());
		return "YES";
		// }
		// return "NO";
	}

	private String getServerUrl(String server) {
		String url = "http://common.taskok.com:9000/Service/ServerConfig.aspx";

		String result = uuGetUrl(url, new HashMap(), false);

		if (result.isEmpty()) {
			return "-1001";
		}

		String arr[] = result.split(",");

		if (server.equals("service")) {
			return "http://" + arr[1].substring(0, arr[1].lastIndexOf(":"));
		} else if (server.equals("upload")) {
			return "http://" + arr[2].substring(0, arr[2].lastIndexOf(":"));
		} else if (server.equals("code")) {
			return "http://" + arr[3].substring(0, arr[3].lastIndexOf(":"));
		} else {
			return "parameter error";

		}

	}

	// 用户登录,登录成功返回用户的ID
	public  String userLogin(String userName, String passWord) {

		if (softID.isEmpty() || softKEY.isEmpty()) {
			return "sorry,SoftID or softKey is not set! Please use the setSoftInfo(id,key) function to set!";
		}

		if ((userName.isEmpty() || passWord.isEmpty())) {
			return "userName or passWord is empty!";
		}

		this.userName = userName;
		this.userPassword = passWord;
		this.userAgent = Md5.MD5(this.softKEY.toUpperCase() + this.userName.toUpperCase()) + macAddress;

		String url = getServerUrl("service") + "/Upload/Login.aspx?U=" + userName + "&P=" + Md5.MD5(userPassword) + "&R=" + System.currentTimeMillis();
		String result = uuGetUrl(url, new HashMap(), false);
		if (!result.isEmpty()) {
			this.userKey = result;
			String[] uids = userKey.split("_");
			this.uid = uids[0];
			this.softContentKEY = Md5.MD5((userKey + softID + softKEY).toLowerCase());
			this.gkey = Md5.MD5((softKEY + userName).toUpperCase()) + macAddress;

			return uid;
		}
		return result;

	}

	// 获取用户剩余题分
	public String getPoint(String userName, String passWord) {

		if (userName.isEmpty() || passWord.isEmpty()) {
			return "userName or passWord is empty!";

		}
		String url = getServerUrl("service") + "/Upload/GetScore.aspx?U=" + userName + "&P=" + Md5.MD5(passWord) + "&R" + System.currentTimeMillis();

		String result = uuGetUrl(url, new HashMap(), false);

		return result;
	}

	// 根据图片路径上传,返回验证码在服务器的ID,$codeType取值查看
	public String upload(String imagePath, String codeType, boolean auth) {

		if (!(new File(imagePath).exists())) {
			return "-1003";
		}

		// File a = new File(imagePath);
		// if(!is_numeric($codeType)){return "-3004";}
		Map data = new HashMap();
		// data.put("img", "@" + imagePath);
		data.put("img", imagePath);
		// try {
		// data.put("img", toByteArray(new File(imagePath)));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		data.put("key", userKey);
		data.put("sid", softID);
		data.put("skey", softContentKEY);
		data.put("TimeOut", String.valueOf(timeOut));
		data.put("Type", codeType);
		if (auth) {
			data.put("Version", "100");
		}

		String url = getServerUrl("upload") + "/Upload/Processing.aspx?R=" + System.currentTimeMillis();

		return uuGetUrl(url, data, false);
	}

	public static byte[] toByteArray(File imageFile) throws Exception {
		BufferedImage img = ImageIO.read(imageFile);
		ByteArrayOutputStream buf = new ByteArrayOutputStream((int) imageFile.length());
		try {
			ImageIO.write(img, "jpg", buf);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return buf.toByteArray();
	}

	// //根据验证码ID获取识别结果
	public String getResult(String codeID) {
		// if(!is_numeric($codeID)){return '-1009|The codeID is not number';}

		String url = getServerUrl("code") + "/Upload/GetResult.aspx?KEY=" + userKey + "&ID=" + codeID + "&Random=" + System.currentTimeMillis();
		String result = "-3";
		int timer = 0;
		while (result.equals("-3") && timer < timeOut) {
			result = uuGetUrl(url, new HashMap(), false);
			try {
				Thread.sleep(3000);
				timer += 3000;
			} catch (InterruptedException e) {
			}
			// usleep(100000);
		}
		// curl_close($this->uuUrl);
//		if (result == "-3") {
//			return "-1002";
//		}
		return result;
	}

	// 将upload和getResult放到一个函数来执行,返回验证码识别结果
	public String autoRecognition(String imagePath, String codeType) {
		String result = upload(imagePath, codeType, true);
		if (result.isEmpty()) {
			String[] arrayResult = result.split("|");
			if (!arrayResult[1].isEmpty()) {
				return arrayResult[1];
			}
			return getResult(result);

		}
		return result;
	}

	private String uuGetUrl(String url, Map postData, boolean closeUrl) {
		try {
			uid = "100";
			HttpClient client = new HttpClient();
			List<Header> headers = new ArrayList<Header>();
			headers.add(new Header("Accept", "text/html, application/xhtml+xml, */*"));
			headers.add(new Header("Accept-Language", "zh-CN"));
			headers.add(new Header("Connection", "Keep-Alive"));
			headers.add(new Header("Cache-Control", "no-cache"));
			headers.add(new Header("SID", this.softID));
			headers.add(new Header("HASH", this.uhash));
			headers.add(new Header("UUVersion", this.uuVersion));
			headers.add(new Header("UID", this.uid));
			headers.add(new Header("User-Agent", this.userAgent));
			headers.add(new Header("KEY", this.gkey));
			try {
				int executeMethod = 0;
				String bodyAsString = null;
				if (!postData.isEmpty() && postData.size() > 0) {
					// headers.add(new Header("Content-Type",
					// "multipart/form-data"));
					PostMethod post = new PostMethod(url);
					setHeaders(post);
					Part[] parts = new Part[postData.size()];
					int temp = 0;
					for (Entry<String, String> ent : ((Map<String, String>) postData).entrySet()) {
						if (ent.getKey().equals("img"))
							parts[temp] = new FilePart(ent.getKey(), new File(ent.getValue()));
						else
							parts[temp] = new StringPart(ent.getKey(), ent.getValue());
						temp++;
					}
					post.setRequestEntity(new MultipartRequestEntity(parts, post.getParams()));
					executeMethod = client.executeMethod(post);
					bodyAsString = post.getResponseBodyAsString();
					post.releaseConnection();
					return bodyAsString;
				}
				GetMethod get = new GetMethod(url);
				setHeaders(get);
				executeMethod = client.executeMethod(get);

				// System.out.println(executeMethod);
				bodyAsString = get.getResponseBodyAsString();
				get.releaseConnection();
				// System.out.println(bodyAsString);
				return bodyAsString;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	private void setHeaders(HttpMethod method) {
		method.setRequestHeader("Accept", "text/html, application/xhtml+xml, */*");
		method.setRequestHeader("Accept-Language", "zh-cn");
		method.setRequestHeader("Connection", "Keep-Alive");
		method.setRequestHeader("Cache-Control", "no-cache");
		method.setRequestHeader("SID", softID);
		method.setRequestHeader("HASH", uhash);
		method.setRequestHeader("UUVersion", uuVersion);
		method.setRequestHeader("UID", uid);
		method.setRequestHeader("User-Agent", userAgent);
		method.setRequestHeader("KEY", gkey);
	}

	// 识别结果不正确报错误
	public String reportError(String codeID) {
		// if(!is_numeric($codeID)){return '-1009|The codeID is not number';}
		if (!softContentKEY.isEmpty() && !userKey.isEmpty()) {
			String url = getServerUrl("code") + "/Upload/ReportError.aspx?key=" + userKey + "&ID=" + codeID + "&sid=" + softID + "&skey=" + softContentKEY + "&R=" + System.currentTimeMillis();
			String result = uuGetUrl(url, new HashMap(), false);
			if (result.equals("OK")) {
				return "OK";
			}
			return result;
		}
		return "-1";
	}

	// 注册新用户,注册成功返回新用户的ID
	public String regUser(String userName, String userPassword) {
		if (softID.isEmpty() && softKEY.isEmpty()) {
			if (userName.isEmpty() && userPassword.isEmpty()) {

				Map data = new HashMap();
				data.put("U", userName);
				data.put("P", userPassword);
				data.put("sid", softID);
				data.put("UKEY", Md5.MD5(userName.toUpperCase() + userPassword + softID + softKEY.toLowerCase()));

				String url = getServerUrl("service") + "/Service/Reg.aspx";

				return uuGetUrl(url, data, false);
			}
			return "userName or userPassword is empty!";
		}
		return "-1";
	}

	// 充值题分，充值成功返回用户当前题分
	public String pay(String userName, String Card) {
		if (softID.isEmpty() && softKEY.isEmpty()) {
			if (userName.isEmpty() && Card.isEmpty()) {

				Map data = new HashMap();
				data.put("U", userName);
				data.put("card", Card);
				data.put("sid", softID);
				data.put("pkey", Md5.MD5(userName.toUpperCase() + softID + softKEY + Card.toUpperCase()));

				String url = getServerUrl("service") + "/Service/Pay.aspx";

				return uuGetUrl(url, data, false);

			}
			return "userName or Card is empty!";
		}
		return "-1";
	}

	public static void main(String[] args) {
		String s = "JODSSDsdoo";
		System.out.println(s.toLowerCase());
		UuApi a=new UuApi();
		a.setSoftInfo("101749", "c5964b2abbc6427f886d2deda1973a2a");
		a.userLogin("sefier", "AnLu@203");
		String l=a.upload("/Users/sefier/Downloads/car/4a5daacc7488d760b3544922682277ec_261838_261858.bmp", "8001",false);
		
		String r;
		r=a.getResult(l); 
		System.out.println(r);	    
	}

	static class Md5 {
		/**
		 * 使用加密规则对字符串进行加密，如果成功，返回加密后的字符串，否则返回原字符串
		 * 
		 * @param password
		 * @param algorithm
		 * 
		 * @return
		 */
		public static String MD5(String password) {
			byte[] unencodedPassword = password.getBytes();

			MessageDigest md = null;

			try {
				md = MessageDigest.getInstance("MD5");
			} catch (Exception e) {
				e.printStackTrace();
				return password;
			}

			md.reset();
			md.update(unencodedPassword);

			byte[] encodedPassword = md.digest();

			StringBuffer buf = new StringBuffer();

			for (int i = 0; i < encodedPassword.length; i++) {
				if ((encodedPassword[i] & 0xff) < 0x10) {
					buf.append("0");
				}

				buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
			}

			return buf.toString();
		}
	}
}
