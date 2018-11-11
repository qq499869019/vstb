package com.um.push.drv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import com.um.push.PubTools;
import com.um.push.drv.channel.PriChannel;
import com.um.push.drv.channel.TsHead;
import com.um.push.drv.channel.UmBitsPushChannel;
import com.um.push.drv.channel.UmPushChannel;
import com.um.push.drv.channel.TsHead.KEYTYPE;
import com.um.push.drv.channel.UmTimePushChannel;

public abstract class UmDriver implements  DevCallback {
	
	private int m_bits = 0;
	private boolean m_encryptflag = false;
	private byte[] m_keys = new byte[16];
	private Thread m_task = null;
	private HashMap<Integer, UmPushChannel> m_channellist = new HashMap<Integer, UmPushChannel>();
	private HashMap<Integer, TsHead> m_headlist = new HashMap<Integer, TsHead>();
	private int m_pid;
	private TsHead m_tsHead = null;
	protected Object m_channellock = new Object();
	private boolean isDtv = false;
	
	private void _doPush()
	{
		int rlen;
		int recordTimes = 0;
		if(isDtv){
			recordTimes = 10;
		}else{
			recordTimes = 10;
		}
//		UmPushChannelInfoVo.setRecordTimes(recordTimes);
//		Null nullPsi = new Null(this, 0);
		int times = 0;
		int channelreadlen = 0;
		while(true)
		{
			rlen = m_bits / 8 / 5;  //姣忔璇诲崐绉掔殑鏁版嵁鍑烘潵锛屼繚璇乸at,pmt,tdt绛夋椂闂撮棬闄�
			times ++;
			if(times<0) {
				times = 0;
			}
			try
			{
				java.util.Iterator<Entry<Integer, UmPushChannel>> iter = m_channellist.entrySet().iterator();
				while (iter.hasNext()) {
					synchronized (m_channellock){
						Map.Entry<Integer, UmPushChannel> entry = iter.next();
						UmPushChannel channel = (UmPushChannel) entry.getValue();					
						//PubTools.debug("check Channel " + i  + ",rlen=" + rlen + ",busyflag=" + m_channel.get(i).getBusyFlag());
						m_tsHead = channel.getTsHead();
						
						channelreadlen = 0;
						byte[] data = channel.First(times, rlen);
						PubTools.debug("Duckey:::3 check Channel " + channel +"times = "+times+", interval=" +channel.getInterval() + ",bits=" +  channel.getBits()
								+ ",remark=" + channel.getremark()+" "+data);
						while(data != null)
						{			
							rlen -= data.length;
							channelreadlen += data.length;
							for(int j = 0; j< data.length / 184; j ++)
							{
								KEYTYPE t = getRandom();
								if(j % 23 == 0)
								{
									if(m_encryptflag && m_tsHead.canEncrypt())
										write(m_tsHead.getData(1, t));
									else
										write(m_tsHead.getData(1));
								}
								else
								{
									if(m_encryptflag && m_tsHead.canEncrypt())
										write(m_tsHead.getData(0, t));
									else
										write(m_tsHead.getData(0));
								}
								if(m_encryptflag && m_tsHead.canEncrypt())
									write(PubTools.encrypt(data, j * 184,  184, getKey(t)));
								else
									write(data, j * 184,  184);
							}
							data = channel.Next(times, rlen);						
						}
						PubTools.debug("read Channel ..." + channel +", datalen = " + channelreadlen);
						updateChannelInfo(channelreadlen, channel);
					}
				}	
			}
			catch(Exception ex)
			{
				continue;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void updateChannelInfo(int readlen, UmPushChannel umPushChannel) {
//		String name = umPushChannel.getremark();
//		int uselength = readlen;
//		for(int i = 0;i<m_sendChannellist.size();i++){
//			if(name.equals(m_sendChannellist.get(i).getChannelName())){
//				m_sendChannellist.get(i).setUseStatus(uselength);
//			}
//		}
		
	}

	private byte[] getKey(KEYTYPE t) {
		byte[] key = new byte[8];
		switch(t)
		{
		case ODD:
			System.arraycopy(m_keys, 0, key, 0, 8);
			break;
		case EVEN:
			System.arraycopy(m_keys, 8, key, 0, 8);
			break;
		}
		return key;
	}

	private KEYTYPE getRandom() {
		Random rand = new Random();
		return (rand.nextInt(100) % 2 == 0 )? KEYTYPE.EVEN : KEYTYPE.ODD;
	}
	///锟斤拷始锟斤拷锟斤拷锟斤拷锟斤拷锟借备
	public int init(boolean isDtv)
	{
		this.isDtv = isDtv;
		m_task = new Thread(new  Runnable() {		
			public void run() {
				_doPush();
			}
		});
		m_task.start();
		return 0;
	}
	///锟酵放诧拷锟脚匡拷锟借备
	@SuppressWarnings("deprecation")
	public int deinit()
	{
		m_task.stop();
		return 0;
	}
	///锟斤拷锟借备,准锟斤拷锟斤拷锟斤拷锟斤拷锟借定锟斤拷锟斤拷锟绞匡拷始锟斤拷锟斤拷
	public abstract boolean open(int bits);
	///锟截憋拷锟借备
	public abstract int close();
	
	public abstract int write(byte[] data);
	public abstract int write(byte[] data, int offset, int len);
	///锟斤拷位锟借备
	public int reset()
	{
		return 0;
	}
	public int updateAllChannel(int bits)
	{
		PubTools.debug("reset bits=" + bits);
		synchronized (m_channellock)
		{
			java.util.Iterator<Entry<Integer, UmPushChannel>> iter = m_channellist.entrySet().iterator();
			while (iter.hasNext()) {
				synchronized (m_channellock)
				{
					Map.Entry<Integer, UmPushChannel> entry = iter.next();
					UmPushChannel channel = (UmPushChannel) entry.getValue();	
					if(channel.getInterval() == 0)
					{
						channel.setBits(bits);
					}
					else
						channel.setBits( bits / 8 * (1000 / channel.getInterval()));
					PubTools.debug("channel Interval=" + channel.getInterval() +
							",bits=" + channel.getBits());
				}
			}
		}
		return 0;
	}
	
	public int createBitschannel(int pid, int bits, String  remark)
	{
		synchronized (m_channellock) {
			int ubits = 0;
			UmPushChannel channel = null;
//			java.util.Iterator<Entry<Integer, UmPushChannel>> iter = m_channellist.entrySet().iterator();
//			while (iter.hasNext()) {
//				Map.Entry<Integer, UmPushChannel> entry = iter.next();
//				channel = (UmPushChannel) entry.getValue();
//				if(channel.getremark().equals(remark)){
//					return channel.getId();
//				}
//				ubits += channel.getBits();
//			}
			channel = new UmBitsPushChannel(this);
			if(m_headlist.get(pid) == null)
			{
				m_headlist.put(pid, new TsHead(pid, false));
			}
			channel.setPid(pid);
			if(bits != 0 )
			{
				channel.malloc(bits);
			}
			else {
				channel.malloc(m_bits - ubits);
			}
			channel.setremark(remark);
			PubTools.debug("channel [" + channel.getId() + "] pid = " + pid + ",bits=" + bits  + ",remark=" + remark);
			m_channellist.put(channel.getId(), channel);
			return channel.getId() ;
		}	
	}
	public int createTimeChannel(int pid, int interval, String  remark)
	{
		UmPushChannel channel = null;
		synchronized (m_channellock) {
//			java.util.Iterator<Entry<Integer, UmPushChannel>> iter = m_channellist.entrySet().iterator();
//			while (iter.hasNext()) {
//				Map.Entry<Integer, UmPushChannel> entry = iter.next();
//				channel = (UmPushChannel) entry.getValue();
//				if(channel.getremark().equals(remark))
//				{
//					return channel.getId();
//				}
//			}
			channel = new UmTimePushChannel(this);
			if(m_headlist.get(pid) == null)
			{
				m_headlist.put(pid, new TsHead(pid, false));
			}
			channel.setPid(pid);
			channel.malloc(4096);
			channel.setInterval(interval);
			channel.setremark(remark);
			PubTools.debug("timechannel [" + channel.getId() + "] pid = " + pid + ",bits=" + 4096 +  ",remark=" + remark);
			m_channellist.put(channel.getId(), channel);
			return channel.getId() ;
		}
	}
	

	
	private int m_batchid = 0x1;
	public int createchannelBatch(int channelid)
	{
		return m_batchid++;
	}
	public void destorychannelBatch(int channelid, int channelBatchid)
	{
		synchronized (m_channellock) {
			 m_channellist.get(channelid).remove(channelBatchid);
		}
		return ;
	}

	public int freechannel(int id)
	{
		synchronized (m_channellock) {
			PubTools.debug("channel [" + id + "] free ");
			 m_channellist.get(id).free();
			 m_channellist.remove(id);
			return 0;
		}
	}
	
	public int setIntervalById(int id, int i) {
		// TODO Auto-generated method stub
		synchronized (m_channellock) {
			
			UmPushChannel uChannel = m_channellist.get(id);
			if(uChannel!=null) {
				PubTools.debug("channel [" + id + "] setIntervalById "+i);
				uChannel.setInterval(i);
			}
				
			return 0;
		}
	}
	///锟斤拷锟斤拷锟斤拷锟�
	public int send(int id, byte[] data)
	{		
		PubTools.debugByteBuf(data);
		return m_channellist.get(id).send(data);
	}
	public int send(int id, int batchid,byte[] data)
	{		
		return m_channellist.get(id).send(batchid,data);
	}	
	public int remove(int id, int batchid)
	{		
		return m_channellist.get(id).remove(batchid);
	}
	
	public void setDataPid(int pid)
	{
		m_pid = pid;
		PubTools.debug("driver pid=" + pid);
	}
	
	public void setBits(int bits) {
		boolean rOpenflag = false;
		if(m_bits != bits)
		{
			rOpenflag = true;
			close();
		}
		m_bits = bits;
		if(rOpenflag)
		{
			open(m_bits);
			updateAllChannel(m_bits);
			if(m_tsHead != null)
				m_tsHead.setPid(m_pid);
		}
	}
	public void setEncrypt(boolean encryptflag) {
		m_encryptflag = encryptflag;
		if(encryptflag)
		{
			reCreateKeys();
		}
	}
	private void reCreateKeys() {
		Random rand = new Random();
		for(int i = 0; i< 3; i++)
		{
			m_keys[i] = (byte) rand.nextInt(255);
			m_keys[i + 4] = (byte) rand.nextInt(255);
			m_keys[i + 8] = (byte) rand.nextInt(255);
			m_keys[i + 12] = (byte) rand.nextInt(255);
		}
		m_keys[3] = (byte)((m_keys[0] + m_keys[1] + m_keys[2]) % 256);
		m_keys[7] = (byte)((m_keys[4] + m_keys[5] + m_keys[6]) % 256);
		m_keys[11] = (byte)((m_keys[8] + m_keys[9] + m_keys[10]) % 256);
		m_keys[15] = (byte)((m_keys[12] + m_keys[13] + m_keys[14]) % 256);
	}

	public byte[] getKeys() {
		for(int i = 0;i<m_keys.length;i++)
			System.out.print(m_keys[i]+",");
		System.out.println("");
		return m_keys;
	}

	public void setChannelCanEncrypt(int id, boolean bl) {
		synchronized (m_channellock) {
			m_channellist.get(id).setCanEncrypt(bl);
		}
	}

	public TsHead getTsHead(int pid) {
		return m_headlist.get(pid);
	}


	public int getChannelPid(int channelId) {
		synchronized (m_channellock) {
			return m_channellist.get(channelId).getPid();
		}
	}
	
	public UmPushChannel getChannel(int channelId) {
		synchronized (m_channellock) {
			return m_channellist.get(channelId);
		}
	}
	
	public void changeChannelSize(String name,int size){
		UmPushChannel channel = null;
		java.util.Iterator<Entry<Integer, UmPushChannel>> iter = m_channellist.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, UmPushChannel> entry = iter.next();
			channel = (UmPushChannel) entry.getValue();
			if(channel.getremark().equals(name))
			{
				channel.setBits(size);
				break;
			}
		}
	}
	public int removeChannelSize(String name){
		UmPushChannel channel = null;
		java.util.Iterator<Entry<Integer, UmPushChannel>> iter = m_channellist.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, UmPushChannel> entry = iter.next();
			channel = (UmPushChannel) entry.getValue();
			if(channel.getremark().equals(name))
			{
				return channel.free();
			}
		}
		return 0;
	}
	private HashMap<Integer ,ArrayList<PriChannel>> m_priChannelList = new HashMap<Integer ,ArrayList<PriChannel>>();
	private Object m_prichannellock = new Object();
	public int createprichannel(int channelid, int bits, int rate, String remark) {
		synchronized (m_prichannellock)
		{
			if(m_priChannelList.get(channelid) == null)
			{
				m_priChannelList.put(channelid, new ArrayList<PriChannel>());
			}
			m_priChannelList.get(channelid).add(new PriChannel(channelid, bits, rate, remark));
			return m_priChannelList.get(channelid).get(m_priChannelList.get(channelid).size() - 1).getPriID();
		}
		
	}
	public int getprichannelsCount(int channelid)
	{
		synchronized (m_prichannellock)
		{
			return m_priChannelList.get(channelid)!= null ? m_priChannelList.get(channelid).size(): 0;
		}
		
	}
	public void freeprichannel(int channelid, int prichannelid)
	{
		synchronized (m_prichannellock)
		{
			if(m_priChannelList.get(channelid) != null)
			{
				for(PriChannel pchannel : m_priChannelList.get(channelid))
				{
					if(pchannel.getPriID() == prichannelid)
					{
						synchronized (m_channellock) {
							remove(channelid, pchannel.getPriID());
						}
						m_priChannelList.get(channelid).remove(pchannel);
						break;
					}
				}
			}
		}
	}


}
