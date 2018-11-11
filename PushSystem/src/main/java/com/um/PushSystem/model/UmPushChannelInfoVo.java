package com.um.PushSystem.model;


public class UmPushChannelInfoVo {
	private int id = 0;
	private String channelName = "";
	private int channelSize = 0;
	
	public UmPushChannelInfoVo() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public int getChannelSize() {
		return channelSize;
	}

	public void setChannelSize(int channelSize) {
		this.channelSize = channelSize;
	}
	
	
	
}
