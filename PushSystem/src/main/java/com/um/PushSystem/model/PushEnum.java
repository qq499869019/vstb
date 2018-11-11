package com.um.PushSystem.model;

public class PushEnum {
	
	public static final String CHANNEL_NAME_PAT = "PAT";
	public static final String CHANNEL_NAME_PMT = "PMT";
	public static final String CHANNEL_NAME_TDT = "TDT";
//	public static final String CHANNEL_NAME_PRI = "PRI";
	public static final String CHANNEL_NAME_PRI_BASEPARAM = "PRI_BASEPARAM";
	public static final String CHANNEL_NAME_PRI_AD = "PRI_AD";
	public static final String CHANNEL_NAME_PRI_PLAYEPGS = "PRI_PLAYEPGS";
	public static final String CHANNEL_NAME_PRI_SPECIAL = "PRI_SPECIAL";
	
	public static final String CHANNEL_NAME_DIR_BASEPARAM = "baseparam";
	public static final int PUSHTYPE_BASEPARAM = 1;
	public static final String CHANNEL_NAME_DIR_PLAYEPG = "playepg";
	public static final int PUSHTYPE_PLAYEPG = 2;
	public static final String CHANNEL_NAME_DIR_AD = "ad";
	public static final int PUSHTYPE_AD = 3;
	
	public static final String CHANNEL_NAME_DIR_SPECIAL = "special";//用于升级，应急，默认节目单的视频垫播
	public static final int PUSHTYPE_SPCIAL = 4;
	
}
