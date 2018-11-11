package com.um.PushSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AutoConfigurationPackage
@SpringBootApplication
public class PushSystemApplication {
	public static boolean isClibsInner = true;
	public static void main(String[] args) {
		
		if("Windows".equals(System.getProperties().getProperty("os.name").split(" ")[0])){
		}else{
			loadClibs();
		}
		SpringApplication.run(PushSystemApplication.class, args);
	}
	
	private static void loadClibs() {
		// TODO Auto-generated method stub
		if(isClibsInner) {
			JNIDevelopment deve = new JNIDevelopment();  
	        deve.doDefaultDevelopment();  
		}
		System.loadLibrary("DtvCU1216");
		System.loadLibrary("DtvDemultiplexer"); 
		System.loadLibrary("DtvDevice");
		System.loadLibrary("DtvTransfer");
		System.loadLibrary("shpci");
		System.loadLibrary("shpcir");
		System.loadLibrary("SHV_RTP");
		System.loadLibrary("TWWSockets");
		System.loadLibrary("DtvDriver");
		System.loadLibrary("DtvDriver2");
		
	}
	
	

}
