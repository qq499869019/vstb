package com.um.push.descriptor;

import java.util.List;

import com.um.push.rsa.RsaApi;

public class BustvEsInfo extends Descriptor {

	public BustvEsInfo(boolean encrypt, boolean zip,
			List<byte[]> versions, byte [] keys) {
		setTag((byte) 0x90);
		if(encrypt)
		{
			setLen(0x82 + getVersionslen(versions));			
		}
		else
		{
			setLen(0x2 + getVersionslen(versions));
		}
		byte[] data = new byte[getLen()];
		int dataoffset = 0;
		if(encrypt) {
			data[0] = (byte)131;
		}else {
			data[0] = 2;
		}
		data[1] = (byte)((( zip ? 1 : 0) << 7) 
				| ((encrypt ? 1 : 0) << 6) 
				| (0xff & 0x3f));
		if(encrypt)
		{
			byte[] nkey = null;
			try {
				nkey = RsaApi.encryptByPrivateKey(keys);
				System.arraycopy(nkey, 0, data, 1, nkey.length);
			} catch (Exception e) {			
				e.printStackTrace();
			}			
			dataoffset = 2 + (nkey!=null ? nkey.length : 0);
		}
		else
			dataoffset = 2;
		
		if(versions!=null){
			for(byte[] bs : versions){
				System.arraycopy(bs, 0, data, dataoffset, bs.length);
				dataoffset += bs!=null ? bs.length : 0;
			}
		}
		setData(data);
	}
	
	private int getVersionslen(List<byte[]> versions)
	{
		int len = 0;
		if(versions!=null){
			for(byte[] bs:versions){
				len += bs!=null ? bs.length : 0;
			}
		}
		return len;
	}
}
