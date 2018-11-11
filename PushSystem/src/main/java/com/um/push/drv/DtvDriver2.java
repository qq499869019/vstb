package com.um.push.drv;

public class DtvDriver2 extends UmDriver {

	private native int DtvDriver2Open(int bits) ;
	private native int DtvDriver2Close();
	private native int DtvDriver2Write(byte[] data);
	public int callback(int eventid) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean open(int bits) {
//		System.out.println("DtvDriver2 Open bits=" + bits);
		DtvDriver2Open(bits);
		return false;
	}

	@Override
	public int close() {
//		System.out.println("DtvDriver2 close");
		DtvDriver2Close();
		return 0;
	}
	
	private byte[] data_head = new byte[4];
	@Override
	public int write(byte[] data) {
//		System.out.println("DtvDriver2 write Head");
		System.arraycopy(data, 0, data_head, 0, 4);
		return 0;
	}
		
	@Override
	public int write(byte[] data, int offset, int len) {
		int hlen = data_head.length;
		byte[] dataAll = new byte[hlen + len];
		System.arraycopy(data_head, 0, dataAll, 0, hlen);
		System.arraycopy(data, offset, dataAll, hlen, len);
		DtvDriver2Write(dataAll);
		dataAll = null;
		return 0;
	}

}
