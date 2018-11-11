package com.um.push;

import java.io.File;
import java.util.ArrayList;

import com.um.PushSystem.model.DIRPARAMS;
import com.um.push.drv.UmDriver;
import com.um.push.drv.channel.UmPushChannel;
import com.um.push.table.psi.Pri;
import com.um.push.utils.FileOp;


public class Dir extends Pri {
	public static enum dirType{BASEPARAM,PLAYEPG,AD,SPECIAL, NONE};
	private Thread m_taskHandle = null;
	private String m_dirname = "";
	private dirType m_type = dirType.NONE;
	private boolean m_zipflag = false;
	private int m_priChannelid = -1;
	private byte m_version = 0;
	private int m_channelid = 0;
	private PMTchangeCallback pmtcallback;
	private ArrayList<FileInfo> m_list = new ArrayList<FileInfo>();
	private long curDirSize = 0;
	
	private static int m_filechannelid  = 0;
	
	private int getFileChannelid()
	{
		if(m_filechannelid==0)
		{
			m_filechannelid = super.createPriChannel(170342, 0, "files for all");
		}
		return m_filechannelid;
	}
	public Dir(PMTchangeCallback pushServer, UmDriver driver, int channelid, dirType type,
			String dirname, String groups, String stbs, boolean zipflag, byte version) {
		super(driver, (channelid >> 4) & UmPushChannel.MASK , PubTools.getDirTableID(type),
				PubTools.getDirVersion(type), 0, zipflag);
		pmtcallback = pushServer;		
		m_dirname = dirname;
		m_type = type;
		m_zipflag = zipflag;
		m_channelid = channelid & 0xf;
		scanDir(version);
	}
	public void scanDir(byte version)
	{		
		if(m_taskHandle != null)
		{
			m_taskHandle.interrupt();
			m_list.clear();
		}
		m_version = version;
		scanDir();
//		m_taskHandle = new Thread(new Runnable() {			
//			public void run() {
//				scanDir();
//			}
//		});
//		m_taskHandle.start();
	}
	protected void scanDir() {
		File file = new File(m_dirname);
		if(file == null || !file.exists())
			return ;
		
		m_priChannelid = getFileChannelid() ;//super.createPriChannel(170342, 0, "files for " + m_type.name());
		scanDir(file.getPath());
		PubTools.debug("Find ["+ m_dirname + "] files count=" + m_list.size());
		String version = FileOp.readStrFromFile(m_dirname+DIRPARAMS.FILE_DIR_VERSION);
		fillDirsections(m_type,version);
		
		PubTools.debug("Find ["+ m_dirname + "],m_listsize=" + m_list.size());
		int len = 0;
		for(int i=0; i< m_list.size(); i++)
		{
			if(isInterrupt)
			{				
				return;
			}
			len += m_list.get(i).fill(m_priChannelid, m_channelid, getVersion());
		}
		PubTools.debug("Find ["+ m_dirname + "],filesdata len=" + len);
		m_taskHandle = null;
	}
	
	private void fillDirsections(dirType type, String version) {
		byte [] buff = new byte[2 * 1024 * 1024 ];
		int offset = 10;
		int count = 0;
		long allsize = 0;
		for(int i=0; i< m_list.size(); i++)
		{
			FileInfo pnode = m_list.get(i);
			count++;			
			int fid = pnode.getFid();
			byte tableid = pnode.getTableId();
			int fsize = (int)pnode.getFileSize();
			allsize += fsize;	
			PubTools.debug("allsize="+ allsize + " ," + fsize);
			
			buff[offset + 1] = (byte)(fid >> 8);
			buff[offset + 2] = (byte)(fid & 0xff);
			buff[offset + 3] = (byte)(0 << 7 | pnode.getDate() << 1| pnode.getReadonly());
			PubTools.fillInt(buff, offset + 4, fsize);
			System.arraycopy(pnode.getMd5(), 0, buff, offset + 8, 16);
			String path = pnode.getPath().replaceAll("\\\\","/");
			int pathlen = path.length();
			PubTools.debug("fID=" + fid  + ", size=" + fsize + ", tableid=" + tableid + ",offset=" + offset +",path=" + path +", pathlen=" + pathlen);
			buff[offset + 24] = (byte)(path.length());
			System.arraycopy(path.getBytes(), 0, buff, offset + 25, pathlen);
			buff[offset + 25 + pathlen] = tableid;
			buff[offset] = (byte)(26 + pathlen);
			offset += (26 + pathlen);
		}
		PubTools.fillShort(buff, 0, count);
		PubTools.filllong(buff, 2, allsize);
		int crc = PubTools.getCrc(buff, 0, offset + 10);
		PubTools.fillInt(buff, offset, crc);
		byte[] tmpbuff = new byte[offset + 4];
		System.arraycopy(buff, 0, tmpbuff, 0, offset + 4);
		buff = null;
		m_version = PubTools.getDirVersion(m_type);
		setData(tmpbuff);
		int len = fill(m_channelid, m_version);
		
		PubTools.debug("Find ["+ m_dirname + "],dirdata len=" + len);
		pmtcallback.updatepmt(type,version,curDirSize);
	}
	private void scanDir(String path) {
		if(isInterrupt)
			return;
		File file = new File(path);
		if(file.exists())
		{
			File[] files = file.listFiles();
	        if (files.length == 0) {
	            return;
	        } else {
	            for (File file2 : files) {
	                if (file2.isDirectory()) {
	                    scanDir(file2.getPath());
	                } else {
	                	m_list.add(new FileInfo(getDriver(), m_priChannelid,
	                			file2.getPath(), m_type, m_zipflag, m_dirname,m_version));
	                	curDirSize += file2.length();
	                }
	            }
	        }
		}
	}
	@Override
	protected void doSomethingWhenInterrupt() {
		System.out.println("DIR "+m_dirname+"doSomethingWhenInterrupt");
		System.out.println("m_priChannelid="+m_priChannelid);
		if(m_priChannelid == -1)
			return;
		int clearlen = removeSection(m_channelid);
		PubTools.debug("clear DIR " + m_dirname + " len=" + clearlen);
		clearlen = clearprichannel(m_filechannelid, m_channelid);
		PubTools.debug("clear DIR " + m_dirname + " fileslen=" + clearlen);
		m_priChannelid = -1;
	}
}
