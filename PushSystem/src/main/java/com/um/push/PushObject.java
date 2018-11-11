package com.um.push;

import com.um.push.drv.UmDriver;
import com.um.push.table.Tools;

public abstract class PushObject {
	protected UmDriver m_driver = null;
	private int m_channelId = -1;
	protected boolean isInterrupt = false;
	protected abstract void doSomethingWhenInterrupt();
	private int channelSize = 0;
	
	public PushObject(UmDriver driver, int channelId) {
		m_driver = driver;
		m_channelId = channelId;
	}
	
	public UmDriver getDriver()
	{
		return m_driver;
	}
	
	public int getChannel()
	{
		return m_channelId;
	}
	
	public int fillsection(byte[] secdata)
	{
		this.channelSize = secdata.length; 
		if(m_channelId != -1)
			return m_driver.send(m_channelId, secdata);
		else
			return 0;
	}
	
	public int fillsectionById(int channelid,int batchid, byte[] secdata)
	{
		this.channelSize = secdata.length; 
		if(channelid != -1)
			return m_driver.send(channelid, batchid, secdata);
		else
			return 0;
	}
	
	public int removeSection(int batchid){
		PubTools.debug("clear data " + m_channelId + ":" + batchid);
		this.channelSize = 0;
		if(m_channelId != -1)
			return m_driver.remove(m_channelId, batchid);
		else
			return 0;
	}
	
	public byte[] getKeys() {
		return m_driver.getKeys();
	}

	protected void finalize()
	{
	}
	private void fillsection(int channelid, byte[] secdata)
	{
		if(channelid != -1)
			m_driver.send(channelid, secdata);
	}
	protected int  fillsections(int channelid, byte[] data) {
		if(data == null)
			return 0;
		for(int i =0 ;i< data.length; )
		{
			int seclen = data.length - i > Tools.getMaxSectionlen() ? 
					Tools.getMaxSectionlen() : data.length - i;
			byte [] sec = new byte[seclen];
			System.arraycopy(data, i, sec, 0, sec.length);
			fillsection(channelid, sec);
			i += seclen;
		}
		return data.length;
	}
	public void fillsections(byte[] data) {
		if(data == null)
			return ;
		for(int i =0 ;i< data.length; )
		{
			int seclen = data.length - i > Tools.getMaxSectionlen() ? 
					Tools.getMaxSectionlen() : data.length - i;
			byte [] sec = new byte[seclen];
			System.arraycopy(data, i, sec, 0, sec.length);
			fillsection(sec);
			i += seclen;
		}
	}
	
	public void fillsectionsFor184(byte[] data) {
		if(data == null)
			return ;
		int tspacklen = (data.length/184);
		int seclen = (tspacklen % 23 == 0 )? (tspacklen / 23) : ((tspacklen / 23) + 1); 
		int offset = 0;
		for(int i = 0; i<seclen ; i++)
		{
			int datalen = data.length - offset > Tools.getMaxSectionlen() ? 
					Tools.getMaxSectionlen() : data.length - offset;
			byte [] sec = new byte[datalen];
			System.arraycopy(data, offset, sec, 0, sec.length);
			fillsection(sec);
			offset += datalen;
		}
	}
	
	public void fillsectionsFor184(int channelid, byte[] data) {
		if(data == null)
			return ;
		int tspacklen = (data.length/184);
		int seclen = (tspacklen % 23 == 0 )? (tspacklen / 23) : (tspacklen / 23 + 1); 
		int offset = 0;
		for(int i = 0; i<seclen ; i++)
		{
			int datalen = data.length - offset > Tools.getMaxSectionlen() ? 
					Tools.getMaxSectionlen() : data.length - offset;
			byte [] sec = new byte[datalen];
			System.arraycopy(data, offset, sec, 0, sec.length);
			fillsection(channelid, sec);
			offset += datalen;
		}
	}
	public int fillsectionsFor184(int channelid, int batchid,  byte[] data) {
		if(data == null)
			return 0;
		int tspacklen = (data.length / 184);
		int seclen = (tspacklen % 23 == 0 )? (tspacklen / 23) : (tspacklen / 23 + 1); 
		int offset = 0;
		for(int i = 0; i<seclen ; i++)
		{
			int datalen = data.length - offset > Tools.getMaxSectionlen() ? 
					Tools.getMaxSectionlen() : data.length - offset;
			byte [] sec = new byte[datalen];
			System.arraycopy(data, offset, sec, 0, sec.length);
			fillsectionById(channelid, batchid, sec);
			offset += datalen;
		}
		return offset;
	}	
	public void interruptDirSendingData(){
		isInterrupt = true;
		doSomethingWhenInterrupt();
	}

	public int getChannelSize() {
		return channelSize;
	}
	
	
}
