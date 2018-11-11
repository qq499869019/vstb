package com.um.push.table.psi;

import java.util.Arrays;

import com.um.push.PubTools;
import com.um.push.drv.UmDriver;
import com.um.push.table.Table;

public class Pri extends Table{

	private byte m_tableid;
	private byte m_version = 0;
	private int m_fid = 0;
	private boolean m_zipflag = false;
	private byte[] m_data = null;	
	private int m_maxsecdatalen = 4081;
	private int m_maxblockdatalen = 1044736; //m_maxsecdatalen * 256;
	private int m_maxsecbufflen = 4232;
	private int m_maxblockbufflen = 1083392; //m_maxsecbufflen * 256;
	
	public Pri(UmDriver driver, int channelId, byte tableid, byte version, int fid, boolean zipflag ) {
		super(driver, channelId);
		m_tableid = tableid;
		m_version = version;
		m_fid = fid;
		m_zipflag = zipflag;
	}
	
	@Override
	public int fill(byte version)
	{
		m_version = version;
		return fillsection(getBytes());
	}
	
	@Override
	public int fill(int batchid, byte version)
	{
		m_version = version;
		PubTools.debug("fill data " + getChannel() + " :" + batchid);
		return fillsectionsFor184(getChannel(), batchid, getBytes());
	}
	
	@Override
	public int fill(int channelid,  int batchid, byte version)
	{
		m_version = version;
		PubTools.debug("fill data " + getChannel() + " :" + batchid);
		return fillsectionsFor184(channelid, batchid, getBytes());
	}
	
	@Override
	public byte[] getBytes()
	{
		return fillPri(m_tableid , m_version, m_fid,  m_data, m_zipflag);
	}
	
	public void setTableId(byte tableid)
	{
		m_tableid = tableid;
	}
	

	
	public void setFid(int fid)
	{
		m_fid = fid;
	}
	
	public void setData(byte[] data)
	{
		m_data = data;
	}
	
	private byte[] fillPri(byte tableid, byte version, int fid, byte[] data, boolean zipflag)
	{
		byte [] ret = null;
		if(zipflag)
		{
			byte [] tmpdata = PubTools.zip(data);
			ret = fillData(tmpdata, tmpdata.length, fid, tableid, zipflag, version);
		}
		else
			ret = fillData(data, data.length, fid, tableid, zipflag, version);
		return ret;
	}
	
	public int remain(int len)
	{
		//11涓�section澶�4涓�crc 1涓寚閽堜綅 
		// 鎵�互绗竴涓�84閲岄潰鐨勬湁鏁堣礋杞藉彧鑳芥槸184-1-11-4
		if(len <= 168)
			return 184;
		else
			return (len - 168) % 184 == 0 ? 
					(len - 168) + 184 :
					(((len - 168) / 184) + 2) * 184;
	}
	
	private byte[] fillData(byte[] data, int datalen, int fid,
			byte tableid, boolean zipflag, byte version) {
		try{
			int alllength = data.length;
			byte[] ret = new byte[alllength % m_maxsecdatalen == 0 ? 
					(alllength / m_maxsecdatalen) * m_maxsecbufflen :
					(alllength / m_maxsecdatalen) * m_maxsecbufflen +
					remain(alllength - (alllength / m_maxsecdatalen) * m_maxsecdatalen)];
					
			PubTools.debug("malloc grouplistdata len=" + ret.length+" alllength = "+alllength);
			
			Arrays.fill(ret, (byte)0xff);
			int max_group =  (ret.length %  m_maxblockbufflen) == 0 
					? ret.length / m_maxblockbufflen 
					: ((ret.length / m_maxblockbufflen) + 1);
			PubTools.debug("grouplist maxgroup=" + max_group);
			int offset = 0;
			int srcPos = 0;
			int forward = 0;
			for(int i = 0; i < max_group; i++)
			{
				int rlen = data.length - i * m_maxblockdatalen;
				int max_secnum = 0;
				if(rlen >= m_maxblockdatalen)
					max_secnum = 256;
				else
					max_secnum = rlen % m_maxsecdatalen == 0 ? 
							rlen / m_maxsecdatalen : 
							((rlen / m_maxsecdatalen) + 1);
				
				PubTools.debug("grouplist group=" + i + ", maxsecnum=" + max_secnum);
				for(int j = 0; j <max_secnum ; j++)
				{
					offset = j * m_maxsecbufflen + i * m_maxblockbufflen;
					srcPos = j * m_maxsecdatalen + i * m_maxblockdatalen;
					srcPos -= forward;
					int len = (data.length - srcPos) >= m_maxsecdatalen ? m_maxsecdatalen :(data.length - srcPos);
					if( (i == (max_group - 1)) && (j == max_secnum -2))
					{
						if(data.length - srcPos - len < 4)
						{
							forward  = 4 - (data.length - srcPos - len);
							len -= forward;
						}
					}
					
					int seclen = len + 12;
					PubTools.debug("len=" + len + ",seclen=" + seclen + ", offset=" + offset + ",ret.len=" + ret.length + ",forward=" + forward);
					ret[offset++] = 0;
					ret[offset++] = tableid;
					ret[offset++] = (byte)((0xf << 4) | (seclen >> 8));
					ret[offset++] = (byte)(seclen & 0xff);
					ret[offset++] = (byte)((fid >> 8) & 0xff);
					ret[offset++] = (byte)(fid & 0xff);
					ret[offset++] = (byte)((i >> 4 ) & 0xff);
					ret[offset++] = (byte)(((i & 0xf) << 4) | (((max_group - 1) >> 8) & 0xf));
					ret[offset++] = (byte)((max_group - 1) & 0xff);
					ret[offset++] = (byte)((1 << 7) | ((zipflag ? 1 : 0) << 6) | (version << 1) | 1 );
					ret[offset++] = (byte)j;
					ret[offset++] = (byte)(max_secnum - 1);
					PubTools.debug("srcPos=" + srcPos +",offset=" + offset + ",len=" + len + ", j=" + j + ",i=" + i + ",max_secnum=" + max_secnum);
					System.arraycopy(data, srcPos, ret, offset, len);
					
					offset += len;
					int crc = PubTools.getCrc(ret, j * m_maxsecbufflen + i * m_maxblockbufflen + 1, len + 11);
					PubTools.fillInt(ret, offset, crc);
				}
			}
			return ret;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			PubTools.debug(ex.getMessage());
			return null ;
		}
	}

	public int createPriChannel(int bits, int rate, String string) {
		return  m_driver.createBitschannel(m_driver.getChannelPid(getChannel()), bits,string);
	}
	
	public int clearprichannel(int channelid, int batchid)
	{
		PubTools.debug("clear data " + channelid + ":" + getChannel());
		return m_driver.remove(channelid, batchid);
	}
}
