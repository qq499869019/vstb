package com.um.push.table;

import java.util.Arrays;
import java.util.List;

import com.um.push.PubTools;
import com.um.push.rsa.RsaApi;

public class Tools {
	private static int m_maxsecdatalen = 4081;
	private static int m_maxblockdatalen = m_maxsecdatalen * 256;
	private static int m_maxsecbufflen = 4232;
	private static int m_maxblockbufflen = 4232 * 256;
	private static synchronized byte[] fill8(byte tableid, byte version, byte[] data)
	{
		byte[] ret = new byte[data.length % m_maxsecdatalen == 0 ? 
				(data.length / m_maxsecdatalen) * m_maxsecbufflen :
				(data.length / m_maxsecdatalen) * m_maxsecbufflen + remain(data.length - (data.length / m_maxsecdatalen) * m_maxsecdatalen)];
		PubTools.debug("malloc grouplistdata len=" + ret.length);
		Arrays.fill(ret, (byte)0xff);
		int max_group =  (ret.length %  m_maxblockbufflen) == 0 
				? ret.length / m_maxblockbufflen 
				: ((ret.length / m_maxblockbufflen) + 1);
		PubTools.debug("grouplist maxgroup=" + max_group);
		int offset = 0;
		int srcPos = 0;
		for(int i = 0; i < max_group; i++)
		{
			int rlen = ret.length - i * m_maxsecbufflen;
			int max_secnum = 0;
			if(rlen >= m_maxblockbufflen)
				max_secnum = 256;
			else
				max_secnum = rlen % m_maxsecbufflen == 0 ? 
						rlen / m_maxsecbufflen : 
						((rlen / m_maxsecbufflen) + 1);
			PubTools.debug("grouplist group=" + i + ", maxsecnum=" + max_secnum);
			for(int j = 0; j <max_secnum; j++)
			{
				offset = j * m_maxsecbufflen + i * m_maxblockbufflen;
				srcPos = j * m_maxsecdatalen + i * m_maxblockdatalen;
				int len = (data.length - srcPos) >= m_maxsecbufflen ? 
							m_maxsecbufflen :
							(data.length - srcPos > 0 ? data.length - srcPos : 0);	
				int seclen = len + 9;
				ret[offset++] = 0;
				ret[offset++] = tableid;
				ret[offset++] = (byte)((0xf << 4) | (seclen >> 8));
				ret[offset++] = (byte)(seclen & 0xff);
				ret[offset++] = (byte)i;
				ret[offset++] = (byte)(max_group - 1);
				ret[offset++] = (byte)((3 << 6) | (version << 1) | 1 );
				ret[offset++] = (byte)j;
				ret[offset++] = (byte)(max_secnum - 1);
				System.arraycopy(data, srcPos, ret, offset, len);
				offset += len;
				int crc = PubTools.getCrc(ret, j * m_maxsecbufflen + i * m_maxblockbufflen + 1, len + 8);
				PubTools.fillInt(ret, offset, crc);
			}
		}
		return ret;
	}
	public static synchronized byte[] fillGroupStblist(byte tableid, byte version, byte[] data) {
		return fill8(tableid, version, data);		
	}
	public static synchronized int remain(int len)
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

	private static synchronized int getDirversionlistDatalen(List<byte[]> dirversionlist)
	{
		int len = 0;
		if(dirversionlist!=null){
			for(byte[] bs:dirversionlist){
				len += bs!=null?bs.length:0;
			}
		}
		return len;
	}
	public static synchronized byte[] fillPmt(int serviceId, int dataPid,
		byte version, boolean zipflag, boolean scramblingflag, byte[] keys,List<byte[]> dirversionlist) {

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
		buff[13] = (byte)0x80;
		buff[14] = (byte)(dataPid >> 8 & 0xff);
		buff[15] = (byte)(dataPid & 0xff);
		int dataoffset = 0;
		if(scramblingflag)
		{
			buff[16] = 0x0;
			buff[17] = (byte)(0x84 + getDirversionlistDatalen(dirversionlist));
		}
		else
		{
			buff[16] = 0x0;
			buff[17] = (byte)(0x4  + getDirversionlistDatalen(dirversionlist) );
		}
		buff[18] = (byte)0x90;
		buff[19] = (byte)(buff[17] - 2);
		if(scramblingflag)
		{
			buff[20] = (byte)131;
		}
		else
			buff[20] = 2;
		buff[21] = (byte)(((zipflag?1:0) << 7) | ((scramblingflag?1:0) << 6) | (0xff & 0x3f));
		if(scramblingflag)
		{
			byte[] nkey = null;
			try {
				nkey = RsaApi.encryptByPrivateKey(keys);
				System.arraycopy(nkey, 0, buff, 22, nkey.length);
			} catch (Exception e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();
			}			
			dataoffset = 22+(nkey!=null?nkey.length:0);
		}
		else
			dataoffset = 22;
		if(dirversionlist!=null){
			for(byte[] bs:dirversionlist){
				System.arraycopy(bs, 0, buff, dataoffset, bs.length);
				dataoffset += bs!=null?bs.length:0;
			}
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
	
	public static synchronized int getMaxSectionlen() {
		return m_maxsecbufflen;
	}
	
	public static synchronized byte[] fillProgPlaylist(byte tableid,	byte version, byte[] data) {
		return fill8(tableid, version, data);		
	}
	
	public static synchronized byte[] fillCmd(byte tableid,	byte version, byte[] data) {
		return fill8(tableid, version, data);		
	}
	
	public static synchronized byte[] fillFileData(byte tableid, byte version, int fid, byte[] data, boolean zipflag)
	{
	//	int  crc = PubTools.getCrc(data, 0, data.length - 4);
	//	PubTools.fillInt(data, data.length - 4, crc);
		byte [] ret = null;
		if(zipflag)
		{
			byte [] tmpdata = PubTools.zip(data);
			ret = fillFileData(tmpdata, tmpdata.length, fid, tableid, zipflag, version);
		}
		else
			ret = fillFileData(data, data.length, fid, tableid, zipflag, version);
		return ret;
	}
	private static synchronized byte[] fillFileData(byte[] data, int datalen, int fid,
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
				for(int j = 0; j <max_secnum; j++)
				{
					offset = j * m_maxsecbufflen + i * m_maxblockbufflen;
					srcPos = j * m_maxsecdatalen + i * m_maxblockdatalen;
					
					int len = (data.length - srcPos) >= m_maxsecdatalen ? m_maxsecdatalen :(data.length - srcPos);
					int seclen = len + 12;
					PubTools.debug("len=" + len + ",seclen=" + seclen + ", offset=" + offset + ",ret.len=" + ret.length);
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
	public static synchronized byte[] fillTDT(byte TableID, byte version,byte[] data) {
		// TODO Auto-generated method stub
		byte[] b = new byte[184];
		Arrays.fill(b, (byte)0xff);
		b[0] = 0;
		b[1] = TableID;
		int section_length = data.length + 4;
		b[2] = (byte) ((section_length & 0x0f00)<<12);
		b[3] = (byte) (section_length & 0x00ff);
		System.arraycopy(data, 0, b, 4, data.length);
		int crc = PubTools.getCrc(b, 1, data.length + 3);
		PubTools.fillInt(b, data.length + 4, crc);
		return  b;
	}
}
