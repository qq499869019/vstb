package com.um.push;

import com.um.push.drv.UmDriver;
import com.um.push.drv.channel.UmPushChannel;

public abstract class PriPushObject extends PushObject {

	protected int m_channelId = 0;
	protected int m_prichannelId = 0;
	public PriPushObject(UmDriver driver, int channelId) {
		super(driver, channelId >> 4  & UmPushChannel.MASK);
		m_channelId = channelId >> 4  & UmPushChannel.MASK;
		m_prichannelId = channelId & 0xf;
		PubTools.debug("channelId=" + channelId + ",prichannelId=" + m_prichannelId + ",channelid=" + m_channelId);
	}
	
	public int createPriChannel(int bits, int rate, String remark)
	{
		return  m_driver.createBitschannel(m_driver.getChannelPid(m_channelId), bits, "FILS");
	}
	
	@Override
	public void fillsectionsFor184(int channelid,  byte[] data)
	{
		if((channelid >> 20) == 0)
		{
			PubTools.debug("fill pri channel " + m_prichannelId + " filedata len= " + data.length);
			super.fillsectionsFor184(channelid, m_prichannelId, data);
		}
		else
		{
			PubTools.debug("fill pri channel " + m_channelId + ":" + m_prichannelId + " data len= " + data.length);
			super.fillsectionsFor184(m_channelId, m_prichannelId, data);
		}
	}
	
	public int clearprichannel(int channelid) {
		PubTools.debug("clear files data " + channelid + ":" + m_prichannelId);
		return m_driver.remove(channelid, m_prichannelId);
	}
}
