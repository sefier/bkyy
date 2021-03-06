package cn.hzjkyy.tool;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Log {
	public static Path currentPath = Paths.get("").toAbsolutePath();
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static List<Log> logs = new ArrayList<Log>();
	private static int lineLimit = 5000;
	
	public static Log getTestLog(String name){
		Log.lineLimit = 1; 
		return getLog(name);
	}
	
	public static Log getLog(String name) {
		for(Log log : logs) {
			if(log.getName() == name){
				return log;
			}
		}
		
		Log log = new Log(name);
		logs.add(log);
		return log;
	}
	
	public static String exceptionStacktraceToString(Exception e)
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(baos);
	    e.printStackTrace(ps);
	    ps.close();
	    return baos.toString();
	}
	
	private String getName() {
		return name;
	}

	private String name;
	private Log(String name) {
		this.name = name;
	}
	
	private StringBuilder content = new StringBuilder();
	
	private int lines;
	public void record(String message) {
		content.append("[" + dateFormat.format(new Date()) + "]" + message + "\n");
		
		lines++;
		if(lines >= lineLimit) {
			write();
			lines = 0;
		}
	}
	
	public void write() {
		String data = content.toString();
		content = new StringBuilder();
		
		if(lineLimit == 1){
			writeToConsole(data);
		}else{
			writeToFile(data);
		}
	}
	
	public void upload(){
		if(file != null){
			new Oss().upload(file);
		}
	}
	
	private File file;
	private void writeToFile(String data){
		if(file == null){
			file = new File(currentPath.resolve(name + "-" + dateFormat.format(new Date()) + ".txt").toString());			
		}

		try {
			if(!file.exists()){
				file.createNewFile();
			}
			try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
			    out.print(data);
			}catch (IOException e) {
			}
		} catch (IOException e) {
		}
		
	}
	
	private void writeToConsole(String data){
		System.out.print(data);
	}
}
