package com.um.push.drv.channel;

import com.um.push.drv.UmDriver;

public class UmBitsPushChannel extends UmPushChannel {

	public UmBitsPushChannel(UmDriver driver) {
		super(driver, UmPushChannel.ChannelType.bits);
	}
	
	@Override
	public byte[] readFirst(int readlen) {
		if(canReadFirst())
			return  super.readdata(readlen);
		return null;
	}

	@Override
	public byte[] readNext(int readlen) {
		return super.readdata(readlen);
	}

}
