package com.um.push.drv;


public class DtvDriver extends UmDriver {

	private static DtvDriver me = null;
	private Object object = new Object();

	private DtvDriver(){
		int ret = DtvDriverJNI.DtvDriverInit();
		System.out.println("ret = "+ret);
	}
	
	public static DtvDriver getInstance(){
		synchronized ("") {
			if(me == null)
				me = new DtvDriver();
			return me;
		}
	}
	public int callback(int eventid) {
		return 0;
	}

	@Override
	public boolean open(final int bits) {
		System.out.println("DtvDriver open bits = "+bits);
		int ret = DtvDriverJNI.open(bits);
		System.out.println("open ret = "+ret);
	
		return true;
	}

	@Override
	public int close() {
		System.out.println("java DtvDriver close");
		int ret = DtvDriverJNI.close();
		System.out.println("close ret = "+ret);
	
		return  0;
	}
	
	private byte[] data_head = null;
	@Override
	public int write(byte[] data) {
		data_head = data;
		return 0;
	}

	@Override
	public int write(byte[] data, int offset, int len) {
		synchronized (object) {
			byte[] dataAll = new byte[data_head.length+len];
			System.arraycopy(data_head, 0, dataAll, 0, data_head.length);
			System.arraycopy(data,offset,dataAll,data_head.length,len);
			return DtvDriverJNI.write(dataAll);
		}
		
		
	}

}
