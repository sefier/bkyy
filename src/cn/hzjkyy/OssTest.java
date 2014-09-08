package cn.hzjkyy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import cn.hzjkyy.tool.Log;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;
import com.aliyun.openservices.oss.model.PutObjectResult;

public class OssTest {
	public static void main(String[] args) throws FileNotFoundException{
	    String accessKeyId = "O10x8yivS5XgbynQ";
	    String accessKeySecret = "ileywjxO7Dm6drHbzCB3dVLCMwwTNW";

	    // 初始化一个OSSClient
	    OSSClient client = new OSSClient(accessKeyId, accessKeySecret);
	    
	    // 获取指定文件的输入流
	    File file = new File(Log.currentPath.resolve("log-application.txt").toString());
	    InputStream content = new FileInputStream(file);

	    // 创建上传Object的Metadata
	    ObjectMetadata meta = new ObjectMetadata();

	    // 必须设置ContentLength
	    meta.setContentLength(file.length());

	    // 上传Object.
	    PutObjectResult result = client.putObject("hzjkyy", "testkey", content, meta);

	    // 打印ETag
	    System.out.println(result.getETag());
	}

}
