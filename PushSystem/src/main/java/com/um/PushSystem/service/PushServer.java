package com.um.PushSystem.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.hamcrest.core.Is;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.um.PushSystem.config.ChannelConfig;
import com.um.PushSystem.model.DIRPARAMS;
import com.um.PushSystem.model.UmPushChannelInfoVo;
import com.um.PushSystem.model.pushConfigVo;
import com.um.push.ChannelVersion;
import com.um.push.Dir;
import com.um.push.PMTchangeCallback;
import com.um.push.PubTools;
import com.um.push.PushEnum;
import com.um.push.PushObject;
import com.um.push.UMPmt;
import com.um.push.Dir.dirType;
import com.um.push.drv.DtvDriver2;
import com.um.push.drv.FileDriver;
import com.um.push.drv.UmDriver;
import com.um.push.drv.channel.UmPushChannel;
import com.um.push.table.psi.Pat;
import com.um.push.table.psi.Pmt;
import com.um.push.table.si.Tdt;
import com.um.push.utils.FileOp;

@Service
public class PushServer implements PMTchangeCallback,DIRchangeCallback {

	@Autowired
	private ChannelConfig channelConfig;
	private UmDriver m_driver = null;
	private Object object = new Object();
	private int m_pmtpid = 0x1ffd;
	private int m_serviceid = 0x1234;
	private int m_psi_patChanelId = 0,m_psi_pmtChanelId = 0;
	private int TDTChanelId = 0;
	private int UmpmtChanelId = 0;
	private int Umpmtdatapid = 0;
	private int baseparamChanelId = 0;
	private int adChanelId = 0;
	private int playepgChanelId = 0;
	private int specialChanelId = 0;
	private static HashMap<Integer, PushObject> channel_map = new HashMap<Integer, PushObject>();
	private static final String versionInitvalue = "00000000000000";
	private static HashMap<String, ChannelVersion> m_channelVersionMap = new HashMap<String, ChannelVersion>();
	private ArrayList<byte[]> versionbytes = new ArrayList<byte[]>();

	@PostConstruct
	private void init() {
		System.out.println("@PostConstruct");
		PubTools.setDebug(channelConfig.isEnableDebug());
		boolean isDtv = false;
		Umpmtdatapid = channelConfig.getPid()+1;
		if("Windows".equals(System.getProperties().getProperty("os.name").split(" ")[0])){
			m_driver = new FileDriver();
		}else{
			m_driver = new DtvDriver2();//DtvDriver.getInstance();
			isDtv = true;
		}
		m_driver.init(isDtv);
		m_driver.setDataPid(channelConfig.getPid());
		m_driver.setBits(channelConfig.getBitRate());
		m_driver.setEncrypt(channelConfig.isEncryptflag());
		initDir(channelConfig.getPushDir());
		initPsiSiMap();
		PsiStart(m_pmtpid, m_serviceid);
		if(channelConfig.isEnablePriTDT())
			openTDTTask();
		
	}
	private void initDir(String pushdir) {
		// TODO Auto-generated method stub
		File file = new File(channelConfig.getPushDir());
		if(!file.exists())
			file.mkdirs();
		DirSyncTask dst = new DirSyncTask(channelConfig.getMainurl(),channelConfig.getPushDir(), this);
		if(!dst.getDirFromMainWeb("ALL")){
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String version = df.format(new Date());
			
			if(!checkChannelDir(pushdir+DIRPARAMS.DIR_AD, version)){
				makeDir(pushdir+DIRPARAMS.DIR_AD_NORMAL);
				makeDir(pushdir+DIRPARAMS.DIR_AD_POINT);
				makeDir(pushdir+DIRPARAMS.DIR_AD_STATION);
				makeDir(pushdir+DIRPARAMS.DIR_AD_TIMING);
				makeDir(pushdir+DIRPARAMS.DIR_AD_AREA);
			}
			if(!checkChannelDir(pushdir+DIRPARAMS.DIR_BASEPARAM, version)){
				makeDir(pushdir+DIRPARAMS.DIR_BASEPARAM_BUSDATA);
				makeDir(pushdir+DIRPARAMS.DIR_BASEPARAM_DEFAULTED);
				makeDir(pushdir+DIRPARAMS.DIR_BASEPARAM_PUBLIC);
			}
			if(!checkChannelDir(pushdir+DIRPARAMS.DIR_PLAYEPGS, version)){
				makeDir(pushdir+DIRPARAMS.DIR_PLAYEPGS_NORMAL);
				makeDir(pushdir+DIRPARAMS.DIR_PLAYEPGS_STATION);
				makeDir(pushdir+DIRPARAMS.DIR_PLAYEPGS_POINT);
				makeDir(pushdir+DIRPARAMS.DIR_PLAYEPGS_TIMING);
				makeDir(pushdir+DIRPARAMS.DIR_PLAYEPGS_AREA);
			}
			if(!checkChannelDir(pushdir+DIRPARAMS.DIR_SPECIAL, version)){
				makeDir(pushdir+DIRPARAMS.DIR_SPECIAL+DIRPARAMS.DIR_EMERGENCY,version);
			}
		}
		
		if(channelConfig.isDrivingflag()){
			Thread thread = new Thread(dst);
			thread.start();
		}
	}
	private void makeDir(String path, String version) {
		// TODO Auto-generated method stub
		JSONObject jo = new JSONObject();
		try {
			jo.put("time", version);
			JSONArray jaArray = new JSONArray();
			jo.put("playepgs", jaArray);
			FileOp.saveStr2File(path,DIRPARAMS.FILE_EMERGENCY_JSON, jo.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void makeDir(String path){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
			
	}
	
	private boolean checkChannelDir(String path,String version){
		File file = new File(path);
		if(!file.exists()){
			file.mkdirs();
			FileOp.saveStr2File(path, DIRPARAMS.FILE_DIR_VERSION, version);
			return false;
		}
		return true;
	}
	private void initPsiSiMap(){
		String versionpath = channelConfig.getPushDir()+DIRPARAMS.FILE_CHANNEL_VERSION;
		File f = new File(versionpath);
		if(f.exists()) {
			try {
				String versions = FileOp.getFileContent(versionpath);
				try {
					JSONArray ja = new JSONArray(versions);
					for(int i = 0;i<ja.length();i++) {
						JSONObject jObject = ja.getJSONObject(i);
						String type = jObject.getString("type");
						ChannelVersion cv = new ChannelVersion(jObject.getInt("version"));
						m_channelVersionMap.put(type, cv);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			m_channelVersionMap.put(PushEnum.CHANNEL_NAME_PAT, new ChannelVersion(0));
			m_channelVersionMap.put(PushEnum.CHANNEL_NAME_PMT, new ChannelVersion(0));
			if(channelConfig.isEnablePriPmt()) {
				m_channelVersionMap.put(PushEnum.CHANNEL_NAME_PRI_PMT, new ChannelVersion(0));
			}
			m_channelVersionMap.put(PushEnum.CHANNEL_NAME_TDT, new ChannelVersion(0));
			m_channelVersionMap.put(PushEnum.CHANNEL_NAME_PRI_BASEPARAM, new ChannelVersion(0));
			m_channelVersionMap.put(PushEnum.CHANNEL_NAME_PRI_PLAYEPGS, new ChannelVersion(0));
			m_channelVersionMap.put(PushEnum.CHANNEL_NAME_PRI_AD, new ChannelVersion(0));
			m_channelVersionMap.put(PushEnum.CHANNEL_NAME_PRI_SPECIAL, new ChannelVersion(0));
			updateLocalVersion();
		}
		
	}
	private int PsiStart(int pmtPid, int serviceId)
	{
		m_psi_patChanelId = m_driver.createTimeChannel(0x0, 400, PushEnum.CHANNEL_NAME_PAT);		
		Pat pat = new Pat(m_driver, m_psi_patChanelId, pmtPid, serviceId);
		pat.fill((m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PAT).getVersion()));
		channel_map.put(m_psi_patChanelId, pat);
		
		m_psi_pmtChanelId = m_driver.createTimeChannel(pmtPid, 300, PushEnum.CHANNEL_NAME_PMT);	
		String pushdir = channelConfig.getPushDir();
		System.out.println("******"+pushdir);
		String base_version = FileOp.readStrFromFile(pushdir+DIRPARAMS.DIR_BASEPARAM+DIRPARAMS.FILE_DIR_VERSION);
		versionbytes.add(changeVersionTobyte(PubTools.getDirTableID(dirType.BASEPARAM),base_version));
		String ad_version = FileOp.readStrFromFile(pushdir+DIRPARAMS.DIR_AD+DIRPARAMS.FILE_DIR_VERSION);
		versionbytes.add(changeVersionTobyte(PubTools.getDirTableID(dirType.AD),ad_version));
		String playepg_version = FileOp.readStrFromFile(pushdir+DIRPARAMS.DIR_PLAYEPGS+DIRPARAMS.FILE_DIR_VERSION);
		versionbytes.add(changeVersionTobyte(PubTools.getDirTableID(dirType.PLAYEPG),playepg_version));
		String special_version = FileOp.readStrFromFile(pushdir+DIRPARAMS.DIR_SPECIAL+DIRPARAMS.FILE_DIR_VERSION);
		versionbytes.add(changeVersionTobyte(PubTools.getDirTableID(dirType.SPECIAL),special_version));
		Pmt pmt = new Pmt(m_driver, m_psi_pmtChanelId, serviceId, Umpmtdatapid, 0x82,channelConfig.getPid(), 0x80, channelConfig.isEncryptflag(), channelConfig.isZipflag(),versionbytes);
		pmt.fill((m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PMT).getVersion()));
		channel_map.put(m_psi_pmtChanelId, pmt);
		if(channelConfig.isEnablePriPmt()) {
			UmpmtChanelId = m_driver.createTimeChannel(Umpmtdatapid, 300, PushEnum.CHANNEL_NAME_PRI_PMT);	
			UMPmt umpmt = new UMPmt(m_driver, UmpmtChanelId, serviceId, channelConfig.getPid(), 0x80, channelConfig.isEncryptflag(), channelConfig.isZipflag(),versionbytes);
			umpmt.fill(UmpmtChanelId,m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_PMT).getVersion());
			channel_map.put(UmpmtChanelId, pmt);
		}
		
		System.out.println("[PushServer]PsiStart::"+base_version+" "+ad_version+" "+playepg_version+" "+special_version);
		doPushDir(PushEnum.PUSHTYPE_BASEPARAM, pushdir+DIRPARAMS.DIR_BASEPARAM, "", "", base_version);
		doPushDir(PushEnum.PUSHTYPE_AD, pushdir+DIRPARAMS.DIR_AD, "", "", ad_version);
		doPushDir(PushEnum.PUSHTYPE_PLAYEPG, pushdir+DIRPARAMS.DIR_PLAYEPGS, "", "", playepg_version);
		doPushDir(PushEnum.PUSHTYPE_SPCIAL, pushdir+DIRPARAMS.DIR_SPECIAL, "", "", special_version);
		return 0;
	}
	

	private byte[] changeVersionTobyte(byte DirTableID, String version){
		if(version.length()>14)
			version = version.substring(0, 14);
		byte[] vByte = version.getBytes();
		byte[] returnbyte = new byte[vByte.length/2+2];
		byte[] versionbyte = new byte[vByte.length/2];
		returnbyte[0]=(byte) 0xA0;
		returnbyte[1] = DirTableID;
		for(int i = 0;i<versionbyte.length;i++){
			versionbyte[i] = (byte) (((vByte[2*i]&0xf)<<4) | (vByte[2*i+1]&0xf));
		}
		System.arraycopy(versionbyte, 0, returnbyte, 2, versionbyte.length);
		return returnbyte;
	}
	
	private void changeVersionByTableId(byte dirTableID,String version) {
		System.out.println("changeVersionByTableId dirTableID = "+Integer.toHexString(dirTableID&0xff)+" version="+version);
		for(int i = 0;i<versionbytes.size();i++){
			byte[] bs = versionbytes.get(i);
			if(bs[1] == dirTableID){
				versionbytes.remove(i);
				break;
			}
		}
		versionbytes.add(changeVersionTobyte(dirTableID, version));
	}
	private boolean checkHasInvalueVersion() {
		System.out.println("checkHasInvalueVersion in versionbytes.length="+versionbytes.size());
		for(byte[] b:versionbytes){
			String version = "";
			for(int i = 2;i<b.length;i++){
				version+=String.format("%02X", b[i]);
			}
			
			if(version.equals(versionInitvalue)){
				System.out.println("checkHasInvalueVersion out true");
				return true;
			}else{
				System.out.println("checkHasInvalueVersion:::"+String.format("%02X", b[1])+" "+version);
			}
		}
		System.out.println("checkHasInvalueVersion out false");
		return false;
	}
	
	public void openTDTTask() {
		Thread thread = new Thread(new Runnable() {
			
			public void run() {
				while(true){
					if(TDTChanelId!=0){
						m_driver.freechannel(TDTChanelId);
						TDTChanelId = 0;
					}
					pushTDT();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
	
	private void pushTDT() {
		if(TDTChanelId == 0){
			TDTChanelId = m_driver.createTimeChannel(channelConfig.getPid(), 1000, PushEnum.CHANNEL_NAME_TDT);
		}
		new Tdt(m_driver, TDTChanelId , channelConfig.isZipflag());
	}
	
	
	public void updatePushDir(final int type ,final String dirPath,final String groups,final String stbs, String version) {
		// TODO Auto-generated method stub
		synchronized (object) {
			System.out.println("updatePushDir in channel_map size = "+channel_map.size());
			switch (type) {
				case PushEnum.PUSHTYPE_BASEPARAM:
					if(baseparamChanelId != 0){
						if(channel_map.get(baseparamChanelId)!=null) {
							channel_map.get(baseparamChanelId).interruptDirSendingData();
							freePushChannel(baseparamChanelId);
						}
						baseparamChanelId=0;
						changeVersionByTableId(PubTools.getDirTableID(dirType.BASEPARAM),versionInitvalue);
						m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_BASEPARAM).changeVersion();
					}
					break;
					
				case PushEnum.PUSHTYPE_PLAYEPG:
					if(playepgChanelId != 0){
						if(channel_map.get(playepgChanelId)!=null) {
							channel_map.get(playepgChanelId).interruptDirSendingData();
							freePushChannel(playepgChanelId);
						}
						playepgChanelId = 0;
						changeVersionByTableId(PubTools.getDirTableID(dirType.PLAYEPG),versionInitvalue);
						m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_PLAYEPGS).changeVersion();
					}
						break;
						
				case PushEnum.PUSHTYPE_AD:
					if(adChanelId != 0){
						if(channel_map.get(adChanelId)!=null) {
							channel_map.get(adChanelId).interruptDirSendingData();
							freePushChannel(adChanelId);
						}
						adChanelId = 0;
						changeVersionByTableId(PubTools.getDirTableID(dirType.AD),versionInitvalue);
						m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_AD).changeVersion();
					}
					break;
				case PushEnum.PUSHTYPE_SPCIAL:
					if(specialChanelId != 0){
						if(channel_map.get(specialChanelId)!=null) {
							channel_map.get(specialChanelId).interruptDirSendingData();
							freePushChannel(specialChanelId);
						}
						specialChanelId = 0;
						changeVersionByTableId(PubTools.getDirTableID(dirType.SPECIAL),versionInitvalue);
						m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_SPECIAL).changeVersion();
					}
					break;
					
				default:
					break;
			}
			doPushDir(type, dirPath, groups, stbs,version);
			System.out.println("updatePushDir out channel_map size = "+channel_map.size());
//			Iterator<Entry<Integer, PushObject>> iter = channel_map.entrySet().iterator();
//			while(iter.hasNext()) {
//				Entry<Integer, PushObject> enter = iter.next();
//				System.out.println(enter.getKey()+" "+enter.getValue().getChannel());
//			}
		}
	}

	private void doPushDir(final int type ,final String dirPath,final String groups,final String stbs,String version){
		PushObject obj = null;
		int channelSize = 0;
		System.out.println("[PushServer]doPushDir::"+"call pusDir version="+version);
		byte cur_ver = 0;
		switch (type) {
			case PushEnum.PUSHTYPE_BASEPARAM:
				if(baseparamChanelId == 0){
					channelSize = getSetChannelSizeByname(PushEnum.CHANNEL_NAME_DIR_BASEPARAM);
					baseparamChanelId = createPushChannel(channelConfig.getPid(), channelSize,  5,  
							PushEnum.CHANNEL_NAME_DIR_BASEPARAM, PushEnum.CHANNEL_NAME_PRI_BASEPARAM);
				}
				System.out.println("baseparamChanelId = "+baseparamChanelId);
				cur_ver = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_BASEPARAM).getVersion();
				obj = new Dir(this,m_driver, baseparamChanelId, Dir.dirType.BASEPARAM,  dirPath, groups, 
						stbs, channelConfig.isZipflag(), cur_ver);	
				channel_map.put(baseparamChanelId, obj);
//				changeVersionByTableId(PubTools.getDirTableID(dirType.BASEPARAM),version);
				updateLocalVersion(PushEnum.CHANNEL_NAME_PRI_BASEPARAM,cur_ver);
				break;
				
			case PushEnum.PUSHTYPE_PLAYEPG:
				if(playepgChanelId == 0){
					channelSize = getSetChannelSizeByname(PushEnum.CHANNEL_NAME_DIR_PLAYEPG);
					playepgChanelId = createPushChannel(channelConfig.getPid(), channelSize,  5,  
							PushEnum.CHANNEL_NAME_DIR_PLAYEPG, PushEnum.CHANNEL_NAME_PRI_PLAYEPGS);
				}
				System.out.println("playepgChanelId = "+playepgChanelId);
				cur_ver = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_PLAYEPGS).getVersion();
				obj = new Dir(this,m_driver, playepgChanelId, Dir.dirType.PLAYEPG,  dirPath, groups, stbs, 
						channelConfig.isZipflag(), cur_ver);			
				channel_map.put(playepgChanelId, obj);
//				changeVersionByTableId(PubTools.getDirTableID(dirType.PLAYEPG),version);
				updateLocalVersion(PushEnum.CHANNEL_NAME_PRI_PLAYEPGS,cur_ver);
				break;
				
			case PushEnum.PUSHTYPE_AD:
				if(adChanelId == 0){
					channelSize = getSetChannelSizeByname(PushEnum.CHANNEL_NAME_DIR_AD);
					adChanelId = createPushChannel(channelConfig.getPid(), channelSize,  5, 
							PushEnum.CHANNEL_NAME_DIR_AD, PushEnum.CHANNEL_NAME_PRI_AD);
				}
				System.out.println("adChanelId = "+adChanelId);
				cur_ver = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_AD).getVersion();
				obj = new Dir(this,m_driver, adChanelId, Dir.dirType.AD,  dirPath, groups, stbs, 
						channelConfig.isZipflag(),cur_ver);
				channel_map.put(adChanelId, obj);
//				changeVersionByTableId(PubTools.getDirTableID(dirType.AD),version);
				updateLocalVersion(PushEnum.CHANNEL_NAME_PRI_AD,cur_ver);
				break;
				
			case PushEnum.PUSHTYPE_SPCIAL:
				if(specialChanelId == 0){
					channelSize = getSetChannelSizeByname(PushEnum.CHANNEL_NAME_DIR_SPECIAL);
					specialChanelId = createPushChannel(channelConfig.getPid(), channelSize,  5, 
							PushEnum.CHANNEL_NAME_DIR_SPECIAL, PushEnum.CHANNEL_NAME_PRI_SPECIAL);
				}
				System.out.println("specialChanelId = "+specialChanelId);
				cur_ver = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_SPECIAL).getVersion();
				obj = new Dir(this,m_driver, specialChanelId, Dir.dirType.SPECIAL,  dirPath, groups, stbs, 
						channelConfig.isZipflag(),cur_ver);
				channel_map.put(specialChanelId, obj);
//				changeVersionByTableId(PubTools.getDirTableID(dirType.SPECIAL),version);
				updateLocalVersion(PushEnum.CHANNEL_NAME_PRI_SPECIAL,cur_ver);
				break;
				
			default:
				break;
		}
	}

	private int createPushChannel(int pid, int bits, int rate,
			String remark, String fremark) {		
		int channel  = m_driver.createTimeChannel(pid, 1800, fremark);
		int prichannel = m_driver.createprichannel(channel, 0, rate, remark);
		System.out.println("create Channel pid=" + pid +  ",handle=" +
				(0x1 << 20 | channel << 4 | prichannel));
		PubTools.debug("create Channel pid=" + pid +  ",handle=" +
				(0x1 << 20 | channel << 4 | prichannel));
		return 0x1 << 20 | channel << 4 | prichannel;
	}
	
	private int getSetChannelSizeByname(String remark){
		int channelsize = 0;
		return channelsize == 0?125:channelsize;
	}

	@Override
	public void updatepmt(dirType type,String version,long dirsize) {
		synchronized (object) {
			changeIntervalByType(type,dirsize);
			if(!compareDirversion(type,version)) {
				byte tableId = PubTools.getDirTableID(type);
				changeVersionByTableId(tableId,version);
				if(!checkHasInvalueVersion()){
					System.out.println("channels ready  and up pri pmt version");
					for(int i = 0;i<versionbytes.size();i++) {
						byte[] bs = versionbytes.get(i);
						for(byte b:bs) {
							System.out.print(Integer.toHexString(b&0xff));
						}
						System.out.println();
					}
					byte cur_ver = 0x0;
					if(channelConfig.isEnablePriPmt()) {
						if(channel_map.get(UmpmtChanelId) != null){
							channel_map.get(UmpmtChanelId).interruptDirSendingData();
//							m_driver.freechannel(UmpmtChanelId);
							freePushChannel(UmpmtChanelId);
							UmpmtChanelId = 0;
						}
						if(UmpmtChanelId == 0){
							UmpmtChanelId = m_driver.createTimeChannel(Umpmtdatapid, 300, PushEnum.CHANNEL_NAME_PRI_PMT);	
						}
						UMPmt umpmt = new UMPmt(m_driver, UmpmtChanelId, m_serviceid, channelConfig.getPid(), 0x80, channelConfig.isEncryptflag(), channelConfig.isZipflag(),versionbytes);
						cur_ver = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_PMT).changeVersion();
						umpmt.fill(UmpmtChanelId,cur_ver);
						updateLocalVersion(PushEnum.CHANNEL_NAME_PRI_PMT, cur_ver);
						channel_map.put(UmpmtChanelId, umpmt);
					}
					
					if(channel_map.get(m_psi_pmtChanelId) != null){
						channel_map.get(m_psi_pmtChanelId).interruptDirSendingData();
//						m_driver.freechannel(m_psi_pmtChanelId);
						freePushChannel(m_psi_pmtChanelId);
						m_psi_pmtChanelId = 0;
					}
					if(m_psi_pmtChanelId == 0){
						m_psi_pmtChanelId = m_driver.createTimeChannel(m_pmtpid, 300, PushEnum.CHANNEL_NAME_PMT);	
					}
					Pmt pmt = new Pmt(m_driver, m_psi_pmtChanelId, m_serviceid,Umpmtdatapid, 0x82,channelConfig.getPid(), 0x80, channelConfig.isEncryptflag(), channelConfig.isZipflag(),versionbytes);
					cur_ver = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PMT).changeVersion();
					pmt.fill(cur_ver);
					updateLocalVersion(PushEnum.CHANNEL_NAME_PMT, cur_ver);
					channel_map.put(m_psi_pmtChanelId, pmt);
				}	
			}
			
		}
	}	

	private void changeIntervalByType(dirType type, long dirsize) {
		// TODO Auto-generated method stub
		ChannelVersion cv = null;
		int channelId = 0;
		switch (type) {
			case AD:
				cv = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_AD);
				channelId = (adChanelId >> 4 )& UmPushChannel.MASK;
				break;
			case PLAYEPG:
				cv = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_PLAYEPGS);
				channelId = (playepgChanelId >> 4 )  & UmPushChannel.MASK;
				break;
			case BASEPARAM:
				cv = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_BASEPARAM);
				channelId = (baseparamChanelId >> 4 )  & UmPushChannel.MASK;
				break;
			case SPECIAL:
				cv = m_channelVersionMap.get(PushEnum.CHANNEL_NAME_PRI_SPECIAL);
				channelId = (specialChanelId >> 4 )  & UmPushChannel.MASK;
				break;
	
			default:
				break;
		}
		System.out.println("changeIntervalByType::"+cv.getCurChannelSize()+" "+dirsize);
		if(cv.getCurChannelSize() < dirsize) {
			cv.setCurChannelSize(dirsize);
//			m_driver.setIntervalById(channelId,3600);
		}
	}
	private boolean compareDirversion(dirType type, String version) {
		// TODO Auto-generated method stub
		byte[] vs = changeVersionTobyte(PubTools.getDirTableID(type), version);
		for(int i = 0;i<versionbytes.size();i++){
			byte[] bs = versionbytes.get(i);
			if(bs[1] == PubTools.getDirTableID(type)){
				for(int j = 2;j<bs.length;j++) {
					if(bs[j] != vs[j]) {
						System.out.println("[compareDirversion]is change");
						return false;
					}
				}
				break;
			}
		}
		System.out.println("[compareDirversion]is same");
		return true;
	}
	private void freePushChannel(int channelid)
	{
		PubTools.debug("[freePushChannel] " + channelid);
		PubTools.debug("[freePushChannel] channelid >> 20 = " + (channelid >> 20));
		if((channelid >> 20) == 0) {
			m_driver.freechannel(channelid);
		}
		else
		{
			int id = channelid >> 4 & UmPushChannel.MASK;
			int prid = channelid & 0xf;
			m_driver.freeprichannel(id, prid);
			PubTools.debug("[freePushChannel] m_driver.getprichannelsCount(id) = "+m_driver.getprichannelsCount(id));
			if(m_driver.getprichannelsCount(id) == 0)
			{
				m_driver.freechannel(id);
			}
		}
		channel_map.remove(channelid);
	}

	public int setConfig(pushConfigVo pcv) {
		// TODO Auto-generated method stub
		channelConfig.setPid(pcv.getPid());
		m_driver.setDataPid(pcv.getPid());
		System.out.println("********cur pid = "+channelConfig.getPid());
		channelConfig.setEncryptflag(pcv.isEncryptFlag());
		m_driver.setEncrypt(channelConfig.isEncryptflag());
		if(channelConfig.getBitRate() != pcv.getBitRate()){
			System.out.println("here set bitrate");
			channelConfig.setBitRate(pcv.getBitRate());
			m_driver.setBits(channelConfig.getBitRate());
		}
		return 0;
	}

	public pushConfigVo getConfig() {
		// TODO Auto-generated method stub
		System.out.println("here getConfig");
		pushConfigVo vo = new pushConfigVo();
		vo.setBitRate(channelConfig.getBitRate());
		vo.setPid(channelConfig.getPid());
		vo.setEncryptFlag(channelConfig.isEncryptflag());
		vo.setZipFlag(channelConfig.isZipflag());
		return vo;
	}

	public List<UmPushChannelInfoVo> getChannelList() {
		// TODO Auto-generated method stub
		List<UmPushChannelInfoVo> mList = new ArrayList<UmPushChannelInfoVo>();
		System.out.println("map size = "+channel_map.size());
		Iterator<Entry<Integer, PushObject>> iter = channel_map.entrySet().iterator();
		while(iter.hasNext()) {
			UmPushChannelInfoVo vo = new UmPushChannelInfoVo();
			Entry<Integer, PushObject> enter = iter.next();
			int channelId = enter.getKey();
			UmPushChannel channel =  m_driver.getChannel(channelId);
			if(channel == null)
				continue;
			vo.setChannelName(channel.getremark());
			vo.setChannelSize(channel.getChannelDateSize());
			vo.setId(channel.getId());
			mList.add(vo);
		}
		System.out.println("getChannelList="+mList.size());
		return mList;
	}
	
	private void updateLocalVersion(String key,byte version) {
		System.out.println("[updateLocalVersion]"+key+" "+version);
		m_channelVersionMap.remove(key);
		ChannelVersion cv = new ChannelVersion(version);
		m_channelVersionMap.put(key,cv);
		updateLocalVersion();
	}
	
	private void updateLocalVersion() {
		Iterator<Entry<String, ChannelVersion>> iter = m_channelVersionMap.entrySet().iterator();
		JSONArray ja = new JSONArray();
		while(iter.hasNext()) {
			Entry<String, ChannelVersion> enter = iter.next();
			JSONObject jo = new JSONObject();
			try {
				jo.put("type",enter.getKey());
				jo.put("version", enter.getValue().getVersion());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ja.put(jo);
		}
		FileOp.delFile(channelConfig.getPushDir(), DIRPARAMS.FILE_CHANNEL_VERSION);
		FileOp.saveStr2File(channelConfig.getPushDir(), DIRPARAMS.FILE_CHANNEL_VERSION, ja.toString());
	}
}
