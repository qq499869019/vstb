package com.um.PushSystem.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.um.PushSystem.model.DIRPARAMS;
import com.um.push.PushEnum;
import com.um.push.utils.FileOp;
import com.um.push.utils.PublicRequest;
import com.um.push.utils.UnZipCompressorByAnt;


public class DirSyncTask implements Runnable{
	private DIRchangeCallback m_pushserver_callback = null;
	private String m_mainurl = "";
	private String m_savePathroot = "";
	private final String version_action = "pcl_push_version.action";
	private final String dir_action = "pcl_push_dirdownload.action";
	private final String zipfile = "save.zip";
	private JSONObject versionJson = null;

	public DirSyncTask(String mainurl,String savePathroot,DIRchangeCallback callback) {
		// TODO Auto-generated constructor stub
		m_pushserver_callback = callback;
		m_mainurl = mainurl;  
		m_savePathroot = savePathroot;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			JSONObject jo = getDirVersion();
			if(jo!=null){
				if(!checkVersionChange(jo)){
					try {
						String path = m_savePathroot+DIRPARAMS.DIR_AD;
						String version = FileOp.getFileContent(path+DIRPARAMS.FILE_DIR_VERSION);
						if(!jo.getString("AD").equals(version)){
							getDirFromMainWeb(DIRPARAMS.DIR_AD);
							m_pushserver_callback.updatePushDir(PushEnum.PUSHTYPE_AD, path, "", "", version);
						}
						
						path = m_savePathroot+DIRPARAMS.DIR_SPECIAL;
						version = FileOp.getFileContent(path+DIRPARAMS.FILE_DIR_VERSION);
						if(!jo.getString("SPECIAL").equals(version)){
							getDirFromMainWeb(DIRPARAMS.DIR_SPECIAL);
							m_pushserver_callback.updatePushDir(PushEnum.PUSHTYPE_SPCIAL, path, "", "", version);
						}
						
						path = m_savePathroot+DIRPARAMS.DIR_PLAYEPGS;
						version = FileOp.getFileContent(path+DIRPARAMS.FILE_DIR_VERSION);
						if(!jo.getString("PLAYEPGS").equals(version)){
							getDirFromMainWeb(DIRPARAMS.DIR_PLAYEPGS);
							m_pushserver_callback.updatePushDir(PushEnum.PUSHTYPE_PLAYEPG, path, "", "", version);
						}
						
						path = m_savePathroot+DIRPARAMS.DIR_BASEPARAM;
						version = FileOp.getFileContent(path+DIRPARAMS.FILE_DIR_VERSION);
						if(!jo.getString("BASEPARAM").equals(version)){
							getDirFromMainWeb(DIRPARAMS.DIR_BASEPARAM);
							m_pushserver_callback.updatePushDir(PushEnum.PUSHTYPE_BASEPARAM, path, "", "", version);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					versionJson = jo;
				}
			}
			try {
				Thread.sleep(60*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private boolean checkVersionChange(JSONObject jo) {
		// TODO Auto-generated method stub
		try {
			if(!jo.getString("AD").equals(versionJson.getString("AD")))
				return false;
			if(!jo.getString("SPECIAL").equals(versionJson.getString("SPECIAL")))
				return false;
			if(!jo.getString("BASEPARAM").equals(versionJson.getString("BASEPARAM")))
				return false;
			if(!jo.getString("PLAYEPGS").equals(versionJson.getString("PLAYEPGS")))
				return false;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public boolean getDirFromMainWeb(String dirAd) {
		// TODO Auto-generated method stub
		HashMap<String, String> map = new HashMap<String,String>();
		dirAd = dirAd.replace("/", "");
		map.put("path",dirAd);
		boolean ret = false;
		UnZipCompressorByAnt unzip = new UnZipCompressorByAnt();
		try {
			System.out.println("[DirSyncTask]getDirFromMainWeb::"+dirAd);
			if("ALL".equals(dirAd)) {
				String channelVersion = FileOp.readStrFromFile(m_savePathroot+DIRPARAMS.FILE_CHANNEL_VERSION);
				File zipFile = new File(m_savePathroot+"../"+zipfile);
				zipFile.delete();
				ret = PublicRequest.downLoadFile(m_mainurl+dir_action,m_savePathroot+"../"+zipfile,map);
				if(ret){
					FileOp.delAllFile(m_savePathroot);
					unzip.unZip(m_savePathroot+"../"+zipfile, m_savePathroot, false);
					if(!"".equals(channelVersion))
						FileOp.saveStr2File(m_savePathroot, DIRPARAMS.FILE_CHANNEL_VERSION, channelVersion);
				}
			}else{
				
				ret = PublicRequest.downLoadFile(m_mainurl+dir_action,m_savePathroot+"../"+zipfile,map);
				if(ret){
					FileOp.delAllFile(m_savePathroot+dirAd+"/");
					unzip.unZip(m_savePathroot+"../"+zipfile, m_savePathroot+dirAd+"/", true);
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	private JSONObject getDirVersion() {
		String str = PublicRequest.httpURLConectionGET(m_mainurl+version_action);
		System.out.println("[getDirVersion]"+str);
		JSONObject jo_version;
		JSONObject jo = null;
		try {
			jo_version = new JSONObject(str);
			jo = jo_version.getJSONObject("versions");
			if(versionJson == null){
				versionJson = jo;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jo;
	}
}
