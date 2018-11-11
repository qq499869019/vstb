package com.um.push;

import java.util.ArrayList;

import com.um.push.drv.UmDriver;
import com.um.push.table.psi.Pmt;

public class UMPmt extends Pmt {

	public UMPmt(UmDriver driver, int channelid, int serviceId, int dataPid, int type, boolean encryptflag,
			boolean zipflag, ArrayList<byte[]> versionbytes) {
		super(driver, channelid, serviceId, dataPid, type, encryptflag, zipflag,versionbytes);
		// TODO Auto-generated constructor stub
	}

	public int fill(int channelid,byte version)
	{
		setVersion(version);
		fillsectionsFor184(channelid, getBytes());
		return 0;
	}
}
