package com.um.push.csa;

public class tools {
	
	public static int  csaTsBlock(byte[] pdata, int len, byte[]  pkey)
	{		
		dvbcsa_key_s key = dvbcsa_algo.dvbcsa_key_alloc();
		dvbcsa_block.dvbcsa_key_set(pkey, key);	
		dvbcsa_algo.dvbcsa_encrypt(key, pdata, len);
		dvbcsa_algo.dvbcsa_key_free(key);
		return 0;
	}
	byte[] csaFile(byte[] pdata, int len, byte[] pkey)
	{	
//		*poutdata = (UM_U8*)umapi_malloc(len);
//	#if 0	
//		struct dvbcsa_key_s	*key = dvbcsa_key_alloc();
//		dvbcsa_key_set(pkey1, key);
//		dvbcsa_encrypt(key, pdata, len);
//		dvbcsa_key_free(key);
//	#endif	
//		umapi_memcpy(*poutdata,  pdata,  len);	
//		*poutlen = len;
		return null;
	}


}
