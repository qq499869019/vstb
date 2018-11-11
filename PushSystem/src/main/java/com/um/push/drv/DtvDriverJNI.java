package com.um.push.drv;

public class DtvDriverJNI {

	public static native int DtvDriverInit();
	public static native int open(int bits) ;
	public static native int close();
	public static native int write(byte[] data);
	public static native int write(byte[] data, int offset, int len);
	public static native void debug(String str);

}
