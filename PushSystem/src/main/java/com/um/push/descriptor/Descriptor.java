package com.um.push.descriptor;

public abstract class Descriptor {
	protected byte m_tag;
	protected int m_len;
	protected byte[] m_data;	

	public byte getTag()
	{
		return m_tag;
	}
	public int getLen()
	{
		return m_len;
	}
	public byte[] getData()
	{
		return m_data;
	}
	public void setTag(byte tag) {
		m_tag = tag;
	}
	public void setLen(int len)
	{
		m_len = len;
	}
	public void setData(byte[] data)
	{
		m_data = data;
	}
	public byte[] getBytes()
	{
		byte[] ret = new byte[2 + (m_data != null ? m_data.length : 0)];
		ret[0] = m_tag;
		ret[1] = (byte)m_len;
		System.arraycopy(m_data, 0, ret, 2, ret.length - 2);
		return ret;
	}
}
