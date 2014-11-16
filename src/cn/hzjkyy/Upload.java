package cn.hzjkyy;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import cn.hzjkyy.tool.Oss;

public class Upload {
	public static void main(String[] args){
		Set<Integer> overPlanIds = new HashSet<Integer>();
		overPlanIds.add(1000);
		overPlanIds.add(2000);
		System.out.println(overPlanIds.contains(1000));
		System.out.println(overPlanIds.contains(2000));
		System.out.println(overPlanIds.contains(3000));
		System.exit(1);
		String name = UUID.randomUUID().toString().replaceAll("-", "");
		
		Enumeration<NetworkInterface> networkInterfaces;
		try {
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
			while(networkInterfaces.hasMoreElements()){
				Enumeration<InetAddress> addresses = networkInterfaces.nextElement().getInetAddresses();
				while(addresses.hasMoreElements()){
					String address = addresses.nextElement().getHostAddress();
					if(!address.startsWith("192") && !address.startsWith("10.") && !address.startsWith("127")){
						name = address;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		DateFormat dayDateFormat = new SimpleDateFormat("yyyyMMdd");
		String fileName = String.format("%s_%s_server.log", dayDateFormat.format(new Date()), name);
		
		File folder = new File(Paths.get("").toAbsolutePath().toString());
		for(File file : folder.listFiles()){
			if(file.getName().endsWith("server.log")){
				new Oss().uploadAs(file, fileName);
			}
		}
	}
}
