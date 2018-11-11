package com.um.push.drv.channel;

public class PriChannel {
	private int m_priId = 0;
	private static byte g_channelpriId = 0;
	public PriChannel(int channelid, int bits, int rate, String remark) {
		m_priId = (PriChannel.g_channelpriId ++) & 0xf;
	}
	public int getPriID()
	{
		return m_priId ;
	}
}
