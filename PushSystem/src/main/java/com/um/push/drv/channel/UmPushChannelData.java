package com.um.push.drv.channel;

public class UmPushChannelData {
	private int m_readtime = 0;
	private byte[] m_data = null;
	private int m_batchid = 0;
	public UmPushChannelData(byte[] data)
	{
		m_data = data;
	}
	public UmPushChannelData(int batchid, byte[] data) {
		m_data = data;
		m_batchid = batchid;
	}
	public byte[] getData(int maxtime)
	{
		if( (maxtime == 0) || (maxtime > m_readtime++ ))
		{
			return m_data;
		}
		return m_data = null;
	}
	public int getBatchId() {
		return m_batchid;
	}
}
