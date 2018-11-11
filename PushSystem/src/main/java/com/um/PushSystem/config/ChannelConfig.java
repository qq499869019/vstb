package com.um.PushSystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "channel")
public class ChannelConfig {
	
	/**
	 * 主业务地址
	 */
	private String mainurl = "";
	/**
	 * 推送文件夹根目录
	 */
	private String pushDir = "";
	/**
	 * 推送pid
	 */
	private int pid = 0;
	/**
	 * 推送码率
	 */
	private int bitRate= 1400000;
	/**
	 * 加扰标识
	 */
	private boolean   encryptflag = false;
	/**
	 * 压缩标识
	 */
	private boolean   zipflag = false;
	/**
	 * 是否开启打印
	 */
	private boolean enableDebug = false;
	/**
	 * 是否启用TDT表
	 */
	private boolean enablePriTDT = false;
	/**
	 * 是否启用私有pmt表
	 */
	private boolean enablePriPmt = false;
	/**
	 * 配置当前进程是否主动跟主业务系统同步文件标识
	 */
	private boolean drivingflag = false;

	public String getPushDir() {
		return pushDir;
	}

	public void setPushDir(String pushDir) {
		this.pushDir = pushDir;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getBitRate() {
		return bitRate;
	}

	public void setBitRate(int bitRate) {
		this.bitRate = bitRate;
	}

	public boolean isEncryptflag() {
		return encryptflag;
	}

	public void setEncryptflag(boolean encryptflag) {
		this.encryptflag = encryptflag;
	}

	public boolean isZipflag() {
		return zipflag;
	}

	public void setZipflag(boolean zipflag) {
		this.zipflag = zipflag;
	}

	public String getMainurl() {
		return mainurl;
	}

	public void setMainurl(String mainurl) {
		this.mainurl = mainurl;
	}

	public boolean isDrivingflag() {
		return drivingflag;
	}

	public void setDrivingflag(boolean drivingflag) {
		this.drivingflag = drivingflag;
	}

	public boolean isEnablePriTDT() {
		return enablePriTDT;
	}

	public void setEnablePriTDT(boolean enablePriTDT) {
		this.enablePriTDT = enablePriTDT;
	}

	public boolean isEnablePriPmt() {
		return enablePriPmt;
	}

	public void setEnablePriPmt(boolean enablePriPmt) {
		this.enablePriPmt = enablePriPmt;
	}

	public boolean isEnableDebug() {
		return enableDebug;
	}

	public void setEnableDebug(boolean enableDebug) {
		this.enableDebug = enableDebug;
	}
	
	
}
