package com.um.push.table;

import com.um.push.PushObject;
import com.um.push.drv.UmDriver;

public abstract class Table extends PushObject{

	private byte m_version = 0;
	
	public Table(UmDriver driver, int channelId) {
		super(driver, channelId);
		driver.setChannelCanEncrypt(channelId, false);
	}

	@Override
	protected void doSomethingWhenInterrupt() {
		// TODO Auto-generated method stub
		
	}
	
	public byte[] getBytes()
	{
		return null;
		
	}
	
	public abstract int fill(int channelid,  int batchid, byte version);
	
	public abstract int fill(int batchid, byte version);
	
	public abstract int fill(byte version);
	
	public void setVersion(byte version)
	{
		m_version = version;
	}
	
	public byte getVersion()
	{
		return m_version;
	}
}
