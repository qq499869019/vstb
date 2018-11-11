package com.um.push.drv.channel;

import com.um.push.PubTools;
import com.um.push.drv.UmDriver;

public class UmTimePushChannel extends UmPushChannel {

	public UmTimePushChannel(UmDriver driver) {
		super(driver, UmPushChannel.ChannelType.time);
	}

	@Override
	public byte[] readFirst(int times) {
		synchronized (m_channeldatalock) {
			long t = times;
			long a = (t* UmPushChannel.interval)%getInterval();
			PubTools.debug("Duckey::1"+getremark()+" "+times+" "+UmPushChannel.interval+" "+getInterval()+" readFirst||"+a);
			if(a != 0)
			{
//				PubTools.debug("Duckey::"+getremark()+" is have data:"+super.readdata());
				return null;
			}		
			super.reset();
			byte[] ret = super.readdata();
			PubTools.debug("Duckey::2"+getremark()+" return readdata:"+ret);
			return ret;
		}
		
	}
	
	@Override
	public byte[] readNext(int times) {
		return super.readdata();
	}


}
