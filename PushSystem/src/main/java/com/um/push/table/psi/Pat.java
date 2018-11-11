package com.um.push.table.psi;

import java.util.ArrayList;
import java.util.Arrays;
import com.um.push.PubTools;
import com.um.push.drv.UmDriver;
import com.um.push.table.Table;

public class Pat extends Table {

	private class TransportStream
	{
		private int m_prognum = 0;
		private int m_serviceid = 0;
		public TransportStream(int prognum, int serviceid)
		{
			m_prognum = prognum;
			m_serviceid = serviceid;
		}
		public int getPrognum()
		{
			return  m_prognum;
		}
		public int getServiceId()
		{
			return m_serviceid;
		}
	}

	private ArrayList<TransportStream> m_transport_stream_list = new ArrayList<TransportStream>();
	
	public Pat(UmDriver driver, int channelid, int pmtPid,
			int serviceId) {
		super(driver, channelid);
		m_transport_stream_list.add(new TransportStream(pmtPid, serviceId));
	}
	
	public Pat(UmDriver driver, int channelid, int[] pmtPids,
			int[] serviceIds) {
		super(driver, channelid);
		for(int i= 0; i<pmtPids.length; i++)
		{
			m_transport_stream_list.add(new TransportStream(pmtPids[i], serviceIds[i]));
		}
	}
	
	@Override
	public int fill(byte version)
	{
		setVersion(version);
		return super.fillsection(getBytes());		
	}
	
	@Override
	public byte[] getBytes()
	{
		return fillPat(m_transport_stream_list, getVersion());
	}
	
	private byte[] fillPat(ArrayList<TransportStream> list, byte version) {
		byte[] buff = new byte[184];
		Arrays.fill(buff, (byte)0xff);
		int sectionLength = 0; 
		buff[0] = 0;
		buff[1] = 0;	
		buff[4] = 0x0;
		buff[5] = 0x0;
		buff[6] = (byte)(version << 1 | 1);
		buff[7] = 0x0;
		buff[8] = 0x0;
		int offset = 9;
		for(TransportStream t :list)
		{
			buff[offset++] = (byte)(t.getServiceId() >> 8);
			buff[offset++] = (byte)(t.getServiceId() & 0xff);
			buff[offset++] = (byte)(t.getPrognum()  >> 8 & 0x1f);
			buff[offset++] = (byte)(t.getPrognum() & 0xff);
		}		
		sectionLength = offset ; 
		buff[2] = (byte)((0xB0) + ((sectionLength >> 8) & 0xf));
		buff[3] =  (byte)(sectionLength & 0xff);
		int crc = PubTools.getCrc(buff, 1, offset - 1);
		PubTools.fillInt(buff, offset, crc);
		return buff;
	}

	@Override
	public int fill(int batchid, byte version) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int fill(int channelid, int batchid, byte version) {
		// TODO Auto-generated method stub
		return 0;
	}
}
