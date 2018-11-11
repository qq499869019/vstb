package com.um.push.rsa;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.um.push.rsa.Base64;

/*********************
 * 鐩存帴杩愯璇ョ被鍙敓鎴愮粓绔В瀵嗘墍闇�鐨勫叕閽ュご鏂囦欢銆傦紙瀵嗛挜瀵规洿鏂帮紝缁堢鐨勫叕閽ュご鏂囦欢蹇呴』鏇存柊锛�
 * 
 * 椤圭洰涓娇鐢ㄦ椂锛屽ご绔敤绉侀挜鍔犲瘑锛岀粓绔敤鍏挜瑙ｅ瘑銆�
 * 
 * 浣跨敤鏂规硶锛�
 * 1.绯荤粺鍒濆鍖栨椂锛宨nit锛屼粠鑰屽姞杞藉瘑閽ャ�
 * ************************/

public class RsaApi extends Coder {
	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	private static String m_rootpath = "";
	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";
	private static RSAPrivateKey m_privateKey;
	private static RSAPublicKey m_publicKey;
	/**
     * RSA鏈�ぇ鍔犲瘑鏄庢枃澶у皬
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA鏈�ぇ瑙ｅ瘑瀵嗘枃澶у皬
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * 瑙ｅ瘑<br>
	 * 鐢ㄧ閽ヨВ瀵�
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data)
			throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 瀵规暟鎹В瀵�
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, m_privateKey);
		return cipher.doFinal(data);
	}

	/**
	 * 瑙ｅ瘑<br>
	 * 鐢ㄥ叕閽ヨВ瀵�
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data) {		
		Cipher cipher = null;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);		
			// 瀵规暟鎹В瀵�
			cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, m_publicKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int inputLen = data.length;
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    int offSet = 0;
	    byte[] cache;
	    int i = 0;
	    try
	    {
			// 瀵规暟鎹垎娈佃В瀵�
			while (inputLen - offSet > 0) {
			    if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
			        cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
			    } else {
			        cache = cipher.doFinal(data, offSet, inputLen - offSet);
			    }
			    out.write(cache, 0, cache.length);
			    i++;
			    offSet = i * MAX_DECRYPT_BLOCK;
			}
	    }catch(Exception ex)
	    {
	    	ex.printStackTrace();
	    }
		byte[] decryptedData = out.toByteArray();
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decryptedData;
	}

	/**
	 * 鍔犲瘑<br>
	 * 鐢ㄥ叕閽ュ姞瀵�
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data)
			throws Exception {
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 瀵规暟鎹姞瀵�
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, m_publicKey);
		return cipher.doFinal(data);
	}

	/**
	 * 鍔犲瘑<br>
	 * 鐢ㄧ閽ュ姞瀵�
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data)
			throws Exception {
		
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		// 瀵规暟鎹姞瀵�
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, m_privateKey);
		int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 瀵规暟鎹垎娈靛姞瀵�
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
	}

	/**
	 * 鍙栧緱绉侀挜
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static byte [] getPrivateKey()
			throws Exception {
		return m_privateKey.getEncoded();
	}

	/**
	 * 鍙栧緱鍏挜
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static byte [] getPublicKey()
			throws Exception {
		return m_publicKey.getEncoded();
	}
	private static String readFromFile(String fname)
	{
		String ret = "";
		File file = new File(fname);
        BufferedReader reader = null;
        try {
            //System.out.println("浠ヨ涓哄崟浣嶈鍙栨枃浠跺唴瀹癸紝涓�璇讳竴鏁磋锛�);
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 涓�璇诲叆涓�锛岀洿鍒拌鍏ull涓烘枃浠剁粨鏉�
            while ((tempString = reader.readLine()) != null) {
                // 鏄剧ず琛屽彿
                //System.out.println("line " + line + ": " + tempString);
            	if(!tempString.startsWith("----")){
            		ret += tempString;
            	}
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return ret;
	}
	public static void init(String rootpath)
	{	
		m_rootpath = rootpath;
        try {
			loadPrivateKey(readFromFile(rootpath + "rsa_key/pkcs8_rsa_private_key.pem"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
        	loadPublicKey(readFromFile(rootpath + "rsa_key/rsa_public_key.pem"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void loadPrivateKey(String privateKeyStr) throws Exception{  
        try {          	

            byte[] buffer= Base64.decode(privateKeyStr); 
            PKCS8EncodedKeySpec keySpec= new PKCS8EncodedKeySpec(buffer);  
            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
            m_privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("鏃犳绠楁硶");  
        } catch (InvalidKeySpecException e) {  
            throw new Exception("绉侀挜闈炴硶");  
        } catch (NullPointerException e) {  
            throw new Exception("绉侀挜鏁版嵁涓虹┖");  
        }  
    }  
	
	
	private static void writePublicKey(byte[] modulus, byte[] exponent){
		try
		{
			FileWriter writer = new FileWriter(m_rootpath + "/publicKey.h");
			writer.write("#ifndef __PUBLICKEY_H__\r\n#define __PUBLICKEY_H__\r\n");
			writer.write("#include \"rsaref.h\"\r\n");
			writer.write("R_RSA_PUBLIC_KEY publickey = {\r\n");
			writer.write("\t1024,\r\n");
			writer.write("\t{\r\n\t");
			int bcount = 0;
			for(int i=0; i<128 -modulus.length; i++ )
			{
				writer.write("0x00,");
				bcount++;
				if((bcount % 12) == 0)
					writer.write("\r\n");
			}
			for(int i=1 ;i<modulus.length; i++)
			{
				writer.write("0x" + Integer.toHexString(modulus[i] & 0xff));
				if(i < modulus.length -1)
					writer.write(",");
				bcount++;
				if((bcount % 12) == 0)
					writer.write("\r\n\t");
			}
			writer.write("\r\n\t},\r\n\t{\r\n\t");
			
			bcount = 0;
			for(int i=0; i<128 -exponent.length; i++ )
			{
				writer.write("0x00,");
				bcount++;
				if((bcount % 12) == 0)
					writer.write("\r\n\t");
			}
			for(int i=0; i<exponent.length; i++)
			{
				writer.write("0x" + Integer.toHexString(exponent[i] & 0xff));
				if(i < exponent.length - 1)
					writer.write(",");
				bcount++;
				if((bcount % 12) == 0)
					writer.write("\r\n");
			}
			writer.write("\r\n\t}\r\n};\r\n#endif\r\n");
			writer.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	private static  void loadPublicKey(String publicKeystr)
	{
		 try {          	
	            byte[] buffer= Base64.decode(publicKeystr); 
	            X509EncodedKeySpec keySpec= new X509EncodedKeySpec(buffer);  
	            KeyFactory keyFactory= KeyFactory.getInstance("RSA");  
	            m_publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);	
	           // m_publicKey.
	            writePublicKey(m_publicKey.getModulus().toByteArray(), m_publicKey.getPublicExponent().toByteArray());
	        } catch (Exception e) {  
	            e.printStackTrace();
	        }  
	}

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		init("D:/");;
//		byte[] data= new byte[16];
//		for(int i=0; i< data.length-1; i++)
//		{
//			data[i] = (byte)(i+1);
//		}
//		data[15] = 0;
//		byte []dst = null;
//		try {
//			dst =encryptByPrivateKey(data);
//			debugByteBuf(dst, 0, dst.length);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		byte[] dec = decryptByPublicKey(dst);
//		debugByteBuf(dec, 0, dec.length);
//	}

	public static void debugByteBuf(byte[] data, int offset, int len) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();  
		String tmp = "";
		for(int i = offset;i<offset+len;i++){
			tmp = Integer.toHexString(0xFF & data[i]);  
		    if (tmp.length() == 1)// 姣忎釜瀛楄妭8涓猴紝杞负16杩涘埗鏍囧織锛�涓�6杩涘埗浣� 
			{  
			    tmp = "0" + tmp;  
			}  
			sb.append(tmp+" ");  
		}
		System.out.println(sb.toString());	
	}
	/**
	 * 鑾峰彇鍔犲瘑瀵嗘枃锛�
	 * @param password 鍘熸枃
	 * @return 瀵嗘枃
	 */
	public static String getRsaCryptograph(String password) {
		// TODO Auto-generated method stub
		String key = "";
		byte[] nkey = password.getBytes();
		try {
			nkey = encryptByPrivateKey(nkey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0;i<nkey.length;i++){
			System.out.print(nkey[i]+",");
			key += nkey[i]+"";
		}
		return key;
	}
}
