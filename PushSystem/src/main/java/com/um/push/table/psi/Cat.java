package com.um.push.table.psi;

import com.um.push.drv.UmDriver;
import com.um.push.table.Table;

public class Cat extends Table {

	public Cat(UmDriver driver, int channelId) {
		super(driver, channelId);
		// TODO Auto-generated constructor stub
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
