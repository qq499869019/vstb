package com.um.PushSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import org.springframework.util.ResourceUtils;


public class JNIDevelopment {

	private byte[] cache;  
	private List<String> sources;  
	public JNIDevelopment(){  
		sources = new LinkedList<String>();  
		//这里加入本地库文件名，也可以稍加修改读取一个外部xml或properties  
		sources.add("libDtvCU1216.so");  
		sources.add("libDtvDemultiplexer.so");  
		sources.add("libDtvDevice.so");
		sources.add("libDtvTransfer.so");
		sources.add("libshpci.so");
		sources.add("libshpcir.so");
		sources.add("libSHV_RTP.so");
		sources.add("libTWWSockets.so");
		sources.add("libDtvDriver.so");
		sources.add("libDtvDriver2.so");
	}  

    public void doDefaultDevelopment(){  
        for(String s:sources){  
            doDevelopment(s);  
        }  
    }  
    
	public boolean doDevelopment(String sourceName){  
        
		int len = 0;
//        File file;
//		try {
//			file = ResourceUtils.getFile("classpath:cLibs/"+sourceName);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
		ClassLoader classLoader = JNIDevelopment.class.getClassLoader();  
        URL resource = classLoader.getResource("cLibs/"+sourceName);  
        String path = resource.getPath();  
        System.out.println(path);  
        InputStream resourceAsStream = classLoader.getResourceAsStream("cLibs/"+sourceName);  
		File f = new File("." + File.separator + sourceName); 
    	if(!f.exists()){  
    		try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		f.setExecutable(true);//设置可执行权限  
    		f.setReadable(true);//设置可读权限  
    		f.setWritable(true);//设置可写权限  
    		System.out.println("[JNIDEV]:DEFAULT JNI INITION:"+sourceName);  
            FileOutputStream os;
    		try {
    			os = new FileOutputStream(f);
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			return false;
    			
    		}
            try {
    			System.out.println(" length = "+resourceAsStream.available());
    			cache = new byte[resourceAsStream.available()];
    	        Arrays.fill(cache,(byte)0); 
    			while ((len = resourceAsStream.read(cache)) != -1) {
    				os.write(cache, 0, len);
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}finally {
    			try {
    				resourceAsStream.close();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			try {
    				os.close();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}  
        return true;
        
    	
    
    }  
}
