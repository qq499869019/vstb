package com.um.push.table.si;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.um.push.PubTools;
import com.um.push.drv.UmDriver;
import com.um.push.table.Table;
import com.um.push.table.Tools;

public class Tdt extends Table{

	public Tdt(UmDriver driver, int channelid,boolean zipflag) {
		super(driver, channelid);
		// TODO Auto-generated constructor stub
		SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); 
		String curTime = f.format(Calendar.getInstance().getTime());
		String[] times = curTime.split("-");
		int re_ymd = ymd_to_mjd(Integer.parseInt(times[0]),Integer.parseInt(times[1]),Integer.parseInt(times[2]));
		int hms = hms_to_bcd(Integer.parseInt(times[3]),Integer.parseInt(times[4]),Integer.parseInt(times[5]));
		byte[] tmp = new byte[5]; 
		tmp[0] = (byte) (re_ymd>>8);
		tmp[1] = (byte) (re_ymd&0x00ff);
		tmp[2] = (byte) ((hms&0x00ff0000)>>16);
		tmp[3] = (byte) ((hms&0x0000ff00)>>8);
		tmp[4] = (byte) (hms&0x000000ff);
		byte[] data = Tools.fillTDT(PubTools.getTDTTableID(),PubTools.getTDTVersion(),  tmp);
		PubTools.debugByteBuf(data);
		super.fillsections(data);	
	}

	/*
	*Convert Y/M/D  to mjd code.  Y >= 1900
	*/
	static int ymd_to_mjd(int y, int m, int d)
	{
		int l;
		int Y=y;
		
		if (m == 1 || m== 2)
			l = 1;
		else
			l = 0;
		
		Y -= 1900;
		y=(Y < 0)?0:Y;
			
//		return (14956 + d + (UINT32)((y -l) * 365.25) + (UINT32)(((m + 1 + l * 12) * 30.6001)));
		int yl = (y -l);
		int yy = yl * 365 + yl / 4;

		return (14956 + d + yy + (int)(((m + 1 + l * 12) * 306001)/10000));
	}
	
	// Convert hour, min, sec to BCD code; return 24 bit
	static int hms_to_bcd(int hour, int min, int sec)
	{
		int bcd1 = ((hour/10)<<4) | ((hour%10)&0x0f);
		int bcd2 = ((min/10)<<4) | ((min%10)&0x0f);
		int bcd3 = ((sec/10)<<4) | ((sec%10)&0x0f);
		return (bcd1<<16) | (bcd2<<8) | bcd3;
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
