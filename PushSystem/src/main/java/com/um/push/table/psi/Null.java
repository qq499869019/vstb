package com.um.push.table.psi;

import java.util.Arrays;
import com.um.push.drv.UmDriver;
import com.um.push.table.Table;

public class Null extends Table{
	private byte[] m_data = new byte[184];
	
	public Null(UmDriver driver, int channelid)
	{
		super(driver, channelid);
		Arrays.fill(m_data, (byte)0xff);
	}
	public byte[] getdata() {
		// TODO Auto-generated method stub
		return m_data;
	}
	@Override
	public int fill(int batchid, byte version) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int fill(byte version) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int fill(int channelid, int batchid, byte version) {
		// TODO Auto-generated method stub
		return 0;
	}

}
