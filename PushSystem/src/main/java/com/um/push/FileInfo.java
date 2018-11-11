package com.um.push;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.um.push.Dir.dirType;
import com.um.push.drv.UmDriver;
import com.um.push.table.psi.Pri;

public class FileInfo extends Pri{
	private String m_path = "";
	private int m_len = 0;
	private byte[] m_data = null;
	private byte[] m_md5 = null;
	private int m_fid = 0;
	private byte m_tableid = 0;
	public FileInfo(UmDriver driver, int channelid, String path, dirType type, 
			boolean zipflag, String basedirname,byte version) {
		super(driver, channelid, (byte)0, version, 0, zipflag);
		
		m_path = path.substring(basedirname.length());
		File f = new File(path);
		m_len = (int)f.length();
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedInputStream reader = new BufferedInputStream(fis);
			byte[] data = new byte[m_len + 4];
			reader.read(data, 0, m_len);
			int crc = PubTools.getCrc(data, 0, m_len);
			PubTools.fillInt(data, m_len, crc);
			m_md5 = PubTools.getMd5(data, 0, m_len);
			m_fid = PubTools.getFid(m_path, m_md5);
			PubTools.debug("fid = "+m_fid+" FileInfo " +path+" "+type.name());
			m_tableid = PubTools.getDirTableID(type);
			setData(data);
			setFid(m_fid);
			setTableId(m_tableid);	
			m_data = getBytes(); 
			reader.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
//		int len = fill(version);
//		PubTools.debug("fill file " + m_path + ", fid=" + m_fid + ", datalen=" + len);
	}
	
	public byte[] getData() {
		return m_data;
	}

	public long getFileSize() {
		return m_len;
	}

	public int getFid() {
		return m_fid;
	}

	public byte getTableId() {
		return m_tableid;
	}

	public int getDate() {
		return 0;
	}

	public int getReadonly() {
		return 0;
	}

	public byte[] getMd5() {
		return m_md5;
	}

	public String getPath() {
		return m_path;
	}
	
}
