package com.um.push.drv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.um.push.PubTools;


public class FileDriver extends UmDriver {
	private String filePath = "G:/shanghai_bustv_test/";
	int findex = 0;
	File m_file = null;  
    FileOutputStream m_stream = null;  
    
	public int callback(int eventid) {
		return 0;
	}

	@Override
	public boolean open(int bits) {
		File path = new File(filePath);
		if(!path.exists())
			path.mkdirs();
		m_file = new File(filePath + "test" + findex + ".ts");
		if(m_file.exists()){
			m_file.delete();
		}
		try {
			m_file.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			m_stream = new FileOutputStream(m_file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public int close() {
		try {
			if(m_stream!=null)
				m_stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return 0;
	}

	@Override
	public int write(byte[] data) {
//		PubTools.debug("write ts1:::");
//		PubTools.debugByteBuf(data);
		try {
			m_stream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return data.length;
	}

	@Override
	public int write(byte[] data, int offset, int len) {
//		PubTools.debug("write ts2:::");
//		PubTools.debugByteBuf(data,offset,len);		
		try {
			m_stream.write(data, offset, len);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return len;
	}

}
