package com.um.push.drv.channel;

import java.util.ArrayList;
import com.um.push.PubTools;
import com.um.push.drv.UmDriver;

public abstract class UmPushChannel {
	
	private boolean m_busyflag = false;
	protected Object m_channeldatalock = new Object();
	private int m_bits = 0;
	private int m_readbits = 0;  
	private int m_readPacks = 0;
	private int m_listindex = 0;
	private int m_rate = 0;
	private int m_readrate = 0;
	private int m_pid = 0;
	private int m_id = 0;
	private UmDriver m_driver;
	private int m_times = 0,  m_readtimes = 0;
	private int m_interval = 0; //鍑虹幇鐨勬渶澶ч棿闅旓紝姣鏁般�0涓哄敖閲忎笉闂撮殧
	private ArrayList<UmPushChannelData> m_datalist = new ArrayList<UmPushChannelData>();
	
	private String m_remark = "";
	public enum ChannelType {time, bits};
	private ChannelType m_channelType;
	
	private static int channelid = 0;
	public static final int MASK = 0xffff;
	public static final int interval = 200;
	
	public UmPushChannel(UmDriver driver, ChannelType t) {
		
		m_id = UmPushChannel.channelid++ & UmPushChannel.MASK;
		m_driver = driver;
		m_channelType = t;
	}
	
	public int getId()
	{
		return m_id;
	}
	
	public int getPid()
	{
		return m_pid;
	}
	public void setPid(int pid)
	{
		m_pid = pid;
		
	}
	public boolean getBusyFlag() {
		return m_busyflag;
	}
	
	public int send(byte[] data) {		
		synchronized (m_channeldatalock) {
			m_datalist.add(new UmPushChannelData(data));
			PubTools.debug("send data for channel [" + m_id + "] pid = " + m_pid + ",bits=" + m_bits + ", rate=" 
					+ m_rate + ",remark=" + m_remark + ",datalen=" + data.length +
					",datasize=" + m_datalist.size());
		}
		return data.length;
	}	
	public int send(int batchid,byte[] data) {
		synchronized (m_channeldatalock) {
			UmPushChannelData upcd = new UmPushChannelData(batchid, data);
			m_datalist.add(upcd);
		}
		return data.length;
	}
	
	public int remove(int batchid){
		int len = 0;
		int rlen = 0;
		synchronized (m_channeldatalock) {
			PubTools.debug("remove batchid=" + batchid);			
			while((rlen = removebatch(batchid)) != 0 ) 
			{
				len += rlen;
			}
		}
		return len;
	}	
	private int removebatch(int batchid) {
		int datalen = 0;
		for(int i =0; i< m_datalist.size(); i++)
		{
			if(m_datalist.get(i).getBatchId() == batchid)
			{
				datalen += m_datalist.get(i).getData(0).length;
				m_datalist.remove(i);
				return datalen;
			}
		}
		return 0;
	}
	public void removeall(){
		synchronized (m_channeldatalock) {
			PubTools.debug("removeall");		
			m_datalist.clear();
		}
	}
	
	public int free() {
		synchronized (m_channeldatalock) {
			m_datalist.clear();
		}
		m_busyflag = false;
		return 0;
	}
	public void malloc(int bits) {
		m_busyflag = true;
		m_bits = bits;
	}
	
	public abstract byte[] readFirst(int i);
	public abstract byte[] readNext(int i);
	public byte[] First(int times, int len) {
		switch(m_channelType)
		{
			case time:
				return readFirst(times);
			case bits:
				return readFirst(len);
		}
		return null;
	}	
	public byte[] Next(int times, int len) {
		switch(m_channelType)
		{
			case time:
				return readNext(times);
			case bits:
				return readNext(len);
		}
		return null;
	}	
	public boolean canReadFirst()
	{
		m_readbits = 0;
		/*锟斤拷锟斤拷细锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷丫锟斤拷锟饺�锟斤拷锟斤拷 锟斤拷锟斤拷锟斤拷*/
		if( m_readrate>= m_rate || m_readPacks >= m_datalist.size())
		{			
			PubTools.debug("channel [" + m_remark +"] readFirst rate=" + m_rate + 
					",readrate=" + m_readrate +", reset readrate ! m_readPacks=" + m_readPacks +
					",size=" + m_datalist.size());
			if(m_readrate>= m_rate)
				m_readrate = 0;
			if( m_readPacks >= m_datalist.size())
				m_readPacks = 0;
		}
		if((m_rate != 0) && (m_readrate++ != 0))
		{
			PubTools.debug("channel [" + m_remark +"] readFirst rate=" + m_rate + ",readrate=" + m_readrate +", return null !");
			return false;
		}
		return true;
	}
	protected byte[] readdata(int readlen)
	{
		
		if(readlen <= 0 || m_readbits >= m_bits || m_datalist.size() <= 0 || m_readPacks >= m_datalist.size())
		{		
			return null;
		}
		byte[] data = null;
		synchronized (m_channeldatalock) {
//			PubTools.debug("UmPushChannel ["+m_remark+"] m_listindex = "+m_listindex + ",m_datalist.size() = "+m_datalist.size()+ ",m_times = "+m_times+",m_readtimes ="+m_readtimes);
			if(m_listindex >= m_datalist.size())
			{
				if(m_times >0)
				{
					if(m_readtimes++ > m_times)
					{
						m_datalist.clear();
						PubTools.debug("tims=" + m_times + ", readtime=" + m_readtimes);
						return null;
					}
					if(m_readtimes > 0)
					{
						PubTools.debug("tims=" + m_times + ", readtime=" + m_readtimes);
						m_listindex = 0;
						return null;
					}
				}
				m_listindex = 0;
			}
			if(m_datalist.size()!=0){
				data = m_datalist.get(m_listindex).getData(m_times);
				if(data == null)
				{
					m_datalist.remove(m_listindex);
					return null;
				}
			}
			m_readbits += data.length;
	//		PubTools.debugByteBuf(data);
			if(data.length > m_bits * 2)
			{
				PubTools.debug("");
			}
			PubTools.debug("UmPushChannel [" + m_remark +"]  read data len= " + data.length +", secIndex=" + m_listindex +", bits=" + m_bits + ", readlen=" + m_readbits+" m_times ="+m_times);
			m_listindex ++;
			m_readPacks++;
		}
		
		return data;
	}
	protected byte[] readdata()
	{
		synchronized (m_channeldatalock) {
			if(m_datalist.size() > m_listindex)
			{
				return m_datalist.get(m_listindex++ ).getData(0);
			}
			return null;
		}
	}
	
	protected void reset()
	{
		synchronized (m_channeldatalock) {
			m_listindex = 0;
			m_readPacks = 0;
			m_readbits = 0;
		}
	}

	public TsHead getTsHead() {
		return m_driver.getTsHead(m_pid);
	}
	public void setCanEncrypt(boolean bl) {
		TsHead head =  m_driver.getTsHead(m_pid);
		if(head != null)
		{
			head.setCanEncrypt(bl);
		}
	}
	public void setremark(String remark) {
		m_remark = remark;
	}
	public String getremark(){
		return m_remark;
	}
	public int getBits()
	{
		return m_bits;
	}
	public void setBits(int bits) {
		m_bits = bits;
	}
	
	public int getInterval() {
		return m_interval;
	}
	public void setInterval(int interval)
	{
		m_interval = interval;
	}
	
	public void clear(int priID) {
		
	}
	
	public ChannelType getType() {
		return m_channelType;
	}
	
	@Override
	public String toString()
	{
		return "[" + m_remark + ":" + m_id + "]";
	}
	
	public int getChannelDateSize() {
		synchronized (m_channeldatalock) {
			int len = 0;
			for(int i = 0;i<m_datalist.size();i++) {
				len += m_datalist.get(i).getData(i).length;
			}
			return len;
		}
	}
	
}
