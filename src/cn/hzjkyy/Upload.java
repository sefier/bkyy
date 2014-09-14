package cn.hzjkyy;

import java.io.File;
import java.nio.file.Paths;

import cn.hzjkyy.tool.Oss;

public class Upload {
	public static void main(String[] args){
		File folder = new File(Paths.get("").toAbsolutePath().toString());
		for(File file : folder.listFiles()){
			if(file.getName().endsWith(".log")){
				new Oss().upload(file);
			}
		}
	}
}
