package cn.hzjkyy.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.aliyun.openservices.oss.OSSClient;
import com.aliyun.openservices.oss.model.ObjectMetadata;

public class Oss {
    private String accessKeyId = "O10x8yivS5XgbynQ";
    private String accessKeySecret = "ileywjxO7Dm6drHbzCB3dVLCMwwTNW";

    // 初始化一个OSSClient
    OSSClient client = new OSSClient(accessKeyId, accessKeySecret);
    
    public void upload(File file){
	    InputStream content;
		try {
			content = new FileInputStream(file);
		    ObjectMetadata meta = new ObjectMetadata();

		    // 必须设置ContentLength
		    meta.setContentLength(file.length());

		    // 上传Object.
		    client.putObject("hzjkyy", file.getName(), content, meta);
		} catch (FileNotFoundException e) {
		}

    }
}
