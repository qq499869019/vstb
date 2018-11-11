package com.um.push;

import com.um.push.Dir.dirType;

public interface PMTchangeCallback {

	public void updatepmt(dirType type,String version, long curDirSize);
}
