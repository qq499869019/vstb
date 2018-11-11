package com.um.PushSystem.model;

public class pushConfigVo {
	private boolean zipFlag = false;
	private boolean encryptFlag = false;
	private int bitRate = 0;
	private int pid = 0;
	private byte version = (byte)0x0;
	
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
	
	
	
	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public void setZipFlag(boolean zipFlag) {
		this.zipFlag = zipFlag;
	}

	public void setEncryptFlag(boolean encryptFlag) {
		this.encryptFlag = encryptFlag;
	}

	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public pushConfigVo(boolean zip,boolean en,int pid,int bitRate){
		this.pid = pid;
		this.bitRate = bitRate;
		this.zipFlag = zip;
		this.encryptFlag = en;
	}
	
	public pushConfigVo() {
		// TODO Auto-generated constructor stub
	}
	
}
