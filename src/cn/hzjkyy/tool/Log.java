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

import cn.hzjkyy.model.Plan;

public class Log {
	public static Path currentPath = Paths.get("").toAbsolutePath();
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static DateFormat dayDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static List<Log> logs = new ArrayList<Log>();
	private static int lineLimit;
	private Plan plan;
	
	public synchronized Plan getPlan(){
		return this.plan;
	}
	
	public static synchronized void init(int lineLimit1){
		lineLimit = lineLimit1;
	}
	
	public synchronized static Log getLog(Plan plan, String name) {
		if(plan == null){
			System.out.println(name);
		}
		for(Log log : logs) {
			if(log.getName() == name
					&& log.getPlan().getId() == plan.getId()){
				return log;
			}
		}
		
		Log log = new Log(name, plan);
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

	private Log(String name, Plan plan) {
		this.name = name;
		this.plan = plan;
	}
	private String name;
	private String getName() {
		return name;
	}
	
	private String filePath;
	private String getFilePath() {
		if(filePath == null){
			String fileName = String.format("%s_%d_%s", dayDateFormat.format(new Date()), plan.getId(), name);
			filePath = currentPath.resolve(fileName + ".log").toString();
		}
		
		return filePath;
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
			writeToFile(data);
		}else{
			writeToFile(data);
		}
	}
	
	public void upload(){
		if(file != null){
			new Oss().upload(file);
		}
	}
	
	public void close() {
		write();
		upload();
	}
	
	private File file;
	private void writeToFile(String data){
		if(file == null){
			file = new File(getFilePath());			
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
