package com.um.push.csa;

public class dvbcsa_algo {
	public static dvbcsa_key_s dvbcsa_key_alloc()
	{
	  return new dvbcsa_key_s();
	}
	public static void dvbcsa_key_free(dvbcsa_key_s key)
	{
	  key = null;
	}
	static void dvbcsa_xor_64 (byte[] dst, int dstoffset, byte[] src, int srcoffset)
	{
	  for (int i = 0; i < 8; i++)
		  dst[i + dstoffset] ^= src[i + srcoffset];
	}
	static void dvbcsa_encrypt (dvbcsa_key_s key, byte[] data, int len)
	{
	  int alen = len & (int)(~0x7);
	  int		i;

	  if (len < 8)
	    return;

	  dvbcsa_block.dvbcsa_block_encrypt(key.sch, data,  alen - 8, data , alen - 8);

	  for (i = alen - 16; i >= 0; i -= 8)
	    {
	      dvbcsa_xor_64(data , i, data , i + 8);
	      dvbcsa_block.dvbcsa_block_encrypt(key.sch, data, i, data, i);
	    }
	//  dvbcsa_block.debugData(data, 184);
	  dvbcsa_stream.dvbcsa_stream_xor(key.cws, data, data,  8, len - 8);
	//  dvbcsa_block.debugData(data, 184);
	}

}
