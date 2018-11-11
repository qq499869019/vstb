package com.um.push.drv.channel;

import com.um.push.PubTools;

public class TsHead {
	private int m_pid;
	private boolean m_encryptflag, m_canEncrypt = false;
	private int tscount = 0;
	private byte[] tsHeadData = new byte[4];
	public enum KEYTYPE { ODD, EVEN, NONE };
	public TsHead(int pid, boolean encryptflag)
	{
		m_pid = pid;
		m_encryptflag = encryptflag;
		tsHeadData[0] = (byte)0x47;
		PubTools.debug("pid=" + pid + ",encrypt=" + m_encryptflag);
	}
	public int getPid()
	{
		return m_pid;
	}
	public byte[] getData(int flag) {
		tsHeadData[1] = (byte)((flag & 1 ) << 6 | m_pid >> 8 );
		tsHeadData[2] = (byte)( m_pid & 0xff );
		tsHeadData[3] = (byte)(((0 << 6) | (1 << 4)) | tscount);
		tscount = (tscount + 1) % 0x10;
		return tsHeadData;
	}
	public byte[] getData(int flag, KEYTYPE t) {
		tsHeadData[1] = (byte)((flag & 1 ) << 6 | m_pid >> 8 );
		tsHeadData[2] = (byte)( m_pid & 0xff );
		tsHeadData[3] = (byte)( (t== KEYTYPE.ODD ? 2 : 3) << 6 | 1 << 4 | tscount);
		tscount = (tscount + 1) % 0x10;
		return tsHeadData;
	}
	public void setPid(int pid) {
		m_pid = pid;
		PubTools.debug("pid=" + pid);
	}
	public boolean canEncrypt() {
		return m_canEncrypt;
	}
	public void setCanEncrypt(boolean bl) {
		m_canEncrypt = bl;
	}
}
