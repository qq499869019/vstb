package com.um.push;

public class ChannelVersion {

	private byte version = (byte)0x0;
	private long curChannelSize = 14;
	public byte changeVersion() {
		version+=1;
		version = version<0?0:version;
		byte v = version;
		return v;
	}
	public ChannelVersion(int v) {
		this.version = (byte)v;
	}
	public byte getVersion() {
		return version;
	}
	public long getCurChannelSize() {
		return curChannelSize;
	}
	public void setCurChannelSize(long curChannelSize) {
		this.curChannelSize = curChannelSize;
	}
	
}
