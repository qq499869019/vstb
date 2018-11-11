package com.um.PushSystem.service.push;

import java.util.List;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebService;

import com.um.PushSystem.model.UmPushChannelInfoVo;
import com.um.PushSystem.model.pushConfigVo;

@WebService(name = "PushService")
public interface PushService {

	@WebMethod
	public void updatePushDir(int type, String path, String groups, String stbs, String version);
	
	@WebMethod
	public void updatePushDirDataHandler(int type, DataHandler fileData, String groups, String stbs, String version);
	
	@WebMethod
	public List<UmPushChannelInfoVo> getChannelList();
	
	@WebMethod
	public void changeChannelSize(String name, int size);
	
	@WebMethod
	public void removeChannel(String name);
	
	@WebMethod
	public int setConfig(pushConfigVo pcv);
	
	@WebMethod
	public pushConfigVo getConfig();
	
}
