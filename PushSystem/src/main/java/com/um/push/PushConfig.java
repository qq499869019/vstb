package com.um.push;

public class PushConfig {
	private boolean zipFlag;
	private boolean encryptFlag;
	private int bitRate = 0;
	private int pid = 0;
	private byte version;
	
	public boolean isZipFlag() {
		return zipFlag;
	}
	
	public boolean isEncryptFlag() {
		return encryptFlag;
	}
	
	public int getBitRate() {
		return bitRate;
	}
	
	public int getPid() {
		return pid;
	}
	
	public byte getVersion()
	{
		return version;
	}
	
	public PushConfig(boolean zip,boolean en,int pid,int bitRate, byte version){
		this.pid = pid;
		this.bitRate = bitRate;
		this.zipFlag = zip;
		this.encryptFlag = en;
		this.version = version;
	}
}
