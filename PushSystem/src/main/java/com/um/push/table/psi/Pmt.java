package com.um.push.table.psi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.um.push.PubTools;
import com.um.push.descriptor.BustvEsInfo;
import com.um.push.descriptor.Descriptor;
import com.um.push.drv.UmDriver;
import com.um.push.table.Table;


public class Pmt extends Table {

	private class ProgramMap
	{
		private int m_pid;
		private int m_type;
		private Descriptor m_desc;
		public ProgramMap(int pid, int t, Descriptor desc)
		{
			m_pid = pid;
			m_type = t;
			m_desc = desc;
		}
		public int getDataPid()
		{
			return m_pid;
		}
		public int getType()
		{
			return m_type;
		}
		public Descriptor getDescriptor()
		{
			return m_desc;
		}
	}
	
	private int m_serviceId = 0;
	private ArrayList<ProgramMap> m_pms = new ArrayList<ProgramMap>();
	
	
	public Pmt(UmDriver driver, int channelid,
			int serviceId, int dataPid, int type, boolean encryptflag, 
			boolean zipflag,List<byte[]> dirversionlist) {
		super(driver, channelid);
		m_serviceId = serviceId;
		m_pms.add(new ProgramMap(dataPid, type, new BustvEsInfo(encryptflag, zipflag, dirversionlist, getKeys())));
	}
	
	public Pmt(UmDriver driver, int channelid,
			int serviceId, int dataPid1, int type1,int dataPid2, int type2, boolean encryptflag, 
			boolean zipflag,List<byte[]> dirversionlist) {
		super(driver, channelid);
		m_serviceId = serviceId;
		m_pms.add(new ProgramMap(dataPid1, type1, new BustvEsInfo(encryptflag, zipflag, null, getKeys())));
		m_pms.add(new ProgramMap(dataPid2, type2, new BustvEsInfo(encryptflag, zipflag, dirversionlist, getKeys())));
	}
	
	@Override
	public int fill(byte version)
	{
		setVersion(version);
		return fillsection(getBytes());
	}
	
	@Override
	public byte[] getBytes()
	{
		return fillPmt(m_serviceId, getVersion(), m_pms);
	}
	
	private byte[] fillPmt(int serviceId,
			byte version, ArrayList<ProgramMap> maps) {

			byte[] buff = new byte[4096];
			Arrays.fill(buff, (byte)0xff);
			buff[0] = 0;
			buff[1] = 0x02;	
			buff[4] = (byte)(serviceId >> 8);
			buff[5] = (byte)(serviceId & 0xff);
			buff[6] = (byte)(version << 1 | 1);
			buff[7] = 0x0;
			buff[8] = 0x0;
			buff[9] = (byte)(0x1fff >> 8);
			buff[10] = (byte)(0x1fff & 0xff);
			buff[11] = 0x00;
			buff[12] = 0x00;
			
			int dataoffset = 13;
			for(ProgramMap mp : maps)
			{
				buff[dataoffset++] = (byte)mp.getType();
				buff[dataoffset++] = (byte)(mp.getDataPid() >> 8 & 0xff);
				buff[dataoffset++] = (byte)(mp.getDataPid() & 0xff);
				Descriptor desc =  mp.getDescriptor();
				buff[dataoffset++] = (byte) ((desc.getLen() + 2) >> 8 & 0xff);
				buff[dataoffset++] = (byte) ((desc.getLen() + 2) & 0xff);
				System.arraycopy(desc.getBytes(), 0, buff, dataoffset, desc.getLen() + 2);
				dataoffset += desc.getLen() + 2;
			}
					
			buff[2] = (byte)((0xB0) + ((dataoffset >> 8) & 0xf));
			buff[3] = (byte)(dataoffset & 0xff) ;
			
			int crc = PubTools.getCrc(buff, 1 , dataoffset - 1);
			PubTools.fillInt(buff, dataoffset, crc);
			
			dataoffset += 4;
			int alen = dataoffset % 184 == 0 ? dataoffset : (dataoffset / 184 + 1 ) * 184;
			
			byte[] newbuff = new byte[alen];
			System.arraycopy(buff, 0, newbuff, 0, alen);
			
			return newbuff;
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
