package com.um.PushSystem.service.push.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.activation.DataHandler;

import org.springframework.beans.factory.annotation.Autowired;

import com.um.PushSystem.config.ChannelConfig;
import com.um.PushSystem.model.DIRPARAMS;
import com.um.PushSystem.model.UmPushChannelInfoVo;
import com.um.PushSystem.model.pushConfigVo;
import com.um.PushSystem.service.PushServer;
import com.um.PushSystem.service.push.PushService;
import com.um.push.PubTools;
import com.um.push.PushEnum;
import com.um.push.utils.FileOp;
import com.um.push.utils.UnZipCompressorByAnt;

public class PushServiceImpl implements PushService{

	@Autowired
	private PushServer pushserver;
	
	@Autowired
	private ChannelConfig channelConfig;
	
	private static int i = 0;
	private static Object zipLock = new Object();
	
	@Override
	public void updatePushDir(int type, String path, String groups, String stbs, String version) {
		// TODO Auto-generated method stub
		pushserver.updatePushDir(type, path, groups, stbs, version);
	}
	
	@Override
	public void updatePushDirDataHandler(int type, DataHandler fileData, String groups, String stbs, String version) {
		// TODO Auto-generated method stub
		synchronized (zipLock) {
			saveFile(fileData, type, groups, stbs, version);
		}
	}

	@Override
	public List<UmPushChannelInfoVo> getChannelList() {
		// TODO Auto-generated method stub
		return pushserver.getChannelList();
	}

	@Override
	public void changeChannelSize(String name, int size) {
		// TODO Auto-generated method stub
		return ;
	}

	@Override
	public void removeChannel(String name) {
		// TODO Auto-generated method stub
		return ;
	}

	@Override
	public int setConfig(pushConfigVo pcv) {
		// TODO Auto-generated method stub
		return pushserver.setConfig(pcv);
	}

	@Override
	public pushConfigVo getConfig() {
		// TODO Auto-generated method stub
		return pushserver.getConfig();
	}

	private boolean saveFile(DataHandler dataHandler, int type, String groups, String stbs, String version) {
		boolean result = true;
		OutputStream os = null;
        InputStream is = null;
        BufferedOutputStream bos = null;
        String zipfile = i+".zip";
        String filePath = channelConfig.getPushDir()+zipfile;
        try {
            is = dataHandler.getInputStream();
            File dest = new File(filePath);
            os = new FileOutputStream(dest);
            bos = new BufferedOutputStream(os);
            byte[] buffer = new byte[1024*1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            bos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            if(bos != null){
                try{
                    bos.close();
                }catch(Exception e){                    
                }
            }
            if(os != null){
                try{
                    os.close();
                }catch(Exception e){                    
                }
            }
            if(is != null){
                try{
                    is.close();
                }catch(Exception e){                    
                }
            }
        }
        
        UnZipCompressorByAnt unzip = new UnZipCompressorByAnt();
        String path = "";
		try {
			switch (type) {
				case PushEnum.PUSHTYPE_BASEPARAM:
					FileOp.delFolder(channelConfig.getPushDir()+DIRPARAMS.DIR_BASEPARAM+"/");
					path = channelConfig.getPushDir()+DIRPARAMS.DIR_BASEPARAM+version+"/";
					break;
				case PushEnum.PUSHTYPE_AD:
					FileOp.delFolder(channelConfig.getPushDir()+DIRPARAMS.DIR_AD+"/");
					path = channelConfig.getPushDir()+DIRPARAMS.DIR_AD+version+"/";
					break;
				case PushEnum.PUSHTYPE_PLAYEPG:
					FileOp.delFolder(channelConfig.getPushDir()+DIRPARAMS.DIR_PLAYEPGS+"/");
					path = channelConfig.getPushDir()+DIRPARAMS.DIR_PLAYEPGS+version+"/";
					break;
				case PushEnum.PUSHTYPE_SPCIAL:
					FileOp.delFolder(channelConfig.getPushDir()+DIRPARAMS.DIR_SPECIAL+"/");
					path = channelConfig.getPushDir()+DIRPARAMS.DIR_SPECIAL+version+"/";
					break;
				default:
					break;
			}
			unzip.unZip(channelConfig.getPushDir()+zipfile, path, true);
			update2Ts(type, path, groups, stbs, version);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		i++;
        return result;
	}

	private void update2Ts(final int type, final String path, final String groups, final String stbs, final String version) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pushserver.updatePushDir(type, path, groups, stbs, version);
			}
		}).start();
		
	}
}
