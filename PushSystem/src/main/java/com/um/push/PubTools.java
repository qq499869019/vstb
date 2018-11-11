package com.um.push;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPOutputStream;

import com.um.push.Dir.dirType;
import com.um.push.drv.DtvDriverJNI;

public class PubTools {
	private static boolean isDebug = false;
	private static boolean isJNIDebug = false;
	private static FileOutputStream m_logstream = null; 
	private static String debugPath = "G:/shanghai_bustv_test/";
	public  static void setDebug(boolean debugflag) {
		isDebug = debugflag;
	}
	static{
		if("Windows".equals(System.getProperties().getProperty("os.name").split(" ")[0])){
			 debugPath = "G:/shanghai_bustv_test/";
		}else{
			 debugPath = "/home/shanghai_bustv_test/";
		}
		File f = new File(debugPath);
		if(!f.exists())
			f.mkdirs();
	}
	public static FileOutputStream getOutStreamer()
	{
		if(m_logstream == null)
		{
			try {
				File logf = new File(debugPath + "push.log");
				if(logf.exists())
					logf.delete();
				logf.createNewFile();
				m_logstream = new FileOutputStream(logf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return m_logstream;
	}
	public static void debug(String str) {
		if(isDebug)
		{
//			System.out.println("[com.push] " + str);
			if(!isJNIDebug || "Windows".equals(System.getProperties().getProperty("os.name").split(" ")[0])){
				FileOutputStream out = getOutStreamer();
				if(out != null)
				{
					try {
						out.write(("[com.push] " + str + "\r\n").getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else{
				DtvDriverJNI.debug(str);
			}
		}
	}
	
	public static void debugByteBuf(byte[] data) {
		if(isDebug){
			StringBuilder sb = new StringBuilder();  
			String tmp = "";
			for (byte b : data)  
	        {  
	             tmp = Integer.toHexString(0xFF & b);  
	            if (tmp.length() == 1)
	            {  
	                tmp = "0" + tmp;  
	            }  
	            sb.append(tmp+" ");  
	        }  
			debug(sb.toString());		
		}
		
	}
	
	public static void debugByteBuf(byte[] data, int offset, int len) {
	/*	
		if(isDebug){
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
		*/
	}

	public static int getMultiple(int num , int groupsize)
	{
		if( num % groupsize == 0)
			return num ;
		else
			return ( num / groupsize + 1) * groupsize;
	}
//	public static byte getContingencyTableID()
//	{
//		return (byte)0x80;
//	}
//	private static byte c_info_tableId = (byte) 0xA0;
//	public static byte getContingencyTableInfoID(){
//		c_info_tableId++;
//		c_info_tableId = (byte) (c_info_tableId==0xB0?0xA0:c_info_tableId);
//		return c_info_tableId;
//	}
//	public static byte getGroupListTableID()
//	{
//		return (byte)0x81;
//	}
//	public static byte getProgPlayListTableID() {
//		return (byte)0x82;
//	}
//	public static byte getCmdTableID() {
//		return (byte)0x83;
//	}
//	
	public static byte getTDTTableID() {
		return (byte)0x70;
	}
	
	public static byte getSplitScreenTableID()
	{
		return (byte)0x84;
	}
	
	private static int Crc32Table[] = {
	    0x00000000, 0x04c11db7, 0x09823b6e, 0x0d4326d9, 0x130476dc, 0x17c56b6b,
	    0x1a864db2, 0x1e475005, 0x2608edb8, 0x22c9f00f, 0x2f8ad6d6, 0x2b4bcb61,
	    0x350c9b64, 0x31cd86d3, 0x3c8ea00a, 0x384fbdbd, 0x4c11db70, 0x48d0c6c7,
	    0x4593e01e, 0x4152fda9, 0x5f15adac, 0x5bd4b01b, 0x569796c2, 0x52568b75,
	    0x6a1936c8, 0x6ed82b7f, 0x639b0da6, 0x675a1011, 0x791d4014, 0x7ddc5da3,
	    0x709f7b7a, 0x745e66cd, 0x9823b6e0, 0x9ce2ab57, 0x91a18d8e, 0x95609039,
	    0x8b27c03c, 0x8fe6dd8b, 0x82a5fb52, 0x8664e6e5, 0xbe2b5b58, 0xbaea46ef,
	    0xb7a96036, 0xb3687d81, 0xad2f2d84, 0xa9ee3033, 0xa4ad16ea, 0xa06c0b5d,
	    0xd4326d90, 0xd0f37027, 0xddb056fe, 0xd9714b49, 0xc7361b4c, 0xc3f706fb,
	    0xceb42022, 0xca753d95, 0xf23a8028, 0xf6fb9d9f, 0xfbb8bb46, 0xff79a6f1,
	    0xe13ef6f4, 0xe5ffeb43, 0xe8bccd9a, 0xec7dd02d, 0x34867077, 0x30476dc0,
	    0x3d044b19, 0x39c556ae, 0x278206ab, 0x23431b1c, 0x2e003dc5, 0x2ac12072,
	    0x128e9dcf, 0x164f8078, 0x1b0ca6a1, 0x1fcdbb16, 0x018aeb13, 0x054bf6a4,
	    0x0808d07d, 0x0cc9cdca, 0x7897ab07, 0x7c56b6b0, 0x71159069, 0x75d48dde,
	    0x6b93dddb, 0x6f52c06c, 0x6211e6b5, 0x66d0fb02, 0x5e9f46bf, 0x5a5e5b08,
	    0x571d7dd1, 0x53dc6066, 0x4d9b3063, 0x495a2dd4, 0x44190b0d, 0x40d816ba,
	    0xaca5c697, 0xa864db20, 0xa527fdf9, 0xa1e6e04e, 0xbfa1b04b, 0xbb60adfc,
	    0xb6238b25, 0xb2e29692, 0x8aad2b2f, 0x8e6c3698, 0x832f1041, 0x87ee0df6,
	    0x99a95df3, 0x9d684044, 0x902b669d, 0x94ea7b2a, 0xe0b41de7, 0xe4750050,
	    0xe9362689, 0xedf73b3e, 0xf3b06b3b, 0xf771768c, 0xfa325055, 0xfef34de2,
	    0xc6bcf05f, 0xc27dede8, 0xcf3ecb31, 0xcbffd686, 0xd5b88683, 0xd1799b34,
	    0xdc3abded, 0xd8fba05a, 0x690ce0ee, 0x6dcdfd59, 0x608edb80, 0x644fc637,
	    0x7a089632, 0x7ec98b85, 0x738aad5c, 0x774bb0eb, 0x4f040d56, 0x4bc510e1,
	    0x46863638, 0x42472b8f, 0x5c007b8a, 0x58c1663d, 0x558240e4, 0x51435d53,
	    0x251d3b9e, 0x21dc2629, 0x2c9f00f0, 0x285e1d47, 0x36194d42, 0x32d850f5,
	    0x3f9b762c, 0x3b5a6b9b, 0x0315d626, 0x07d4cb91, 0x0a97ed48, 0x0e56f0ff,
	    0x1011a0fa, 0x14d0bd4d, 0x19939b94, 0x1d528623, 0xf12f560e, 0xf5ee4bb9,
	    0xf8ad6d60, 0xfc6c70d7, 0xe22b20d2, 0xe6ea3d65, 0xeba91bbc, 0xef68060b,
	    0xd727bbb6, 0xd3e6a601, 0xdea580d8, 0xda649d6f, 0xc423cd6a, 0xc0e2d0dd,
	    0xcda1f604, 0xc960ebb3, 0xbd3e8d7e, 0xb9ff90c9, 0xb4bcb610, 0xb07daba7,
	    0xae3afba2, 0xaafbe615, 0xa7b8c0cc, 0xa379dd7b, 0x9b3660c6, 0x9ff77d71,
	    0x92b45ba8, 0x9675461f, 0x8832161a, 0x8cf30bad, 0x81b02d74, 0x857130c3,
	    0x5d8a9099, 0x594b8d2e, 0x5408abf7, 0x50c9b640, 0x4e8ee645, 0x4a4ffbf2,
	    0x470cdd2b, 0x43cdc09c, 0x7b827d21, 0x7f436096, 0x7200464f, 0x76c15bf8,
	    0x68860bfd, 0x6c47164a, 0x61043093, 0x65c52d24, 0x119b4be9, 0x155a565e,
	    0x18197087, 0x1cd86d30, 0x029f3d35, 0x065e2082, 0x0b1d065b, 0x0fdc1bec,
	    0x3793a651, 0x3352bbe6, 0x3e119d3f, 0x3ad08088, 0x2497d08d, 0x2056cd3a,
	    0x2d15ebe3, 0x29d4f654, 0xc5a92679, 0xc1683bce, 0xcc2b1d17, 0xc8ea00a0,
	    0xd6ad50a5, 0xd26c4d12, 0xdf2f6bcb, 0xdbee767c, 0xe3a1cbc1, 0xe760d676,
	    0xea23f0af, 0xeee2ed18, 0xf0a5bd1d, 0xf464a0aa, 0xf9278673, 0xfde69bc4,
	    0x89b8fd09, 0x8d79e0be, 0x803ac667, 0x84fbdbd0, 0x9abc8bd5, 0x9e7d9662,
	    0x933eb0bb, 0x97ffad0c, 0xafb010b1, 0xab710d06, 0xa6322bdf, 0xa2f33668,
	    0xbcb4666d, 0xb8757bda, 0xb5365d03, 0xb1f740b4};

	public static int getCrc(byte[] data, int offset , int len)
	{
		int crc = 0xFFFFFFFF;
	    byte ch = 0;
	    for (int i = offset; i < offset + len; i++)
	    {
	        ch = data[i];
	        crc = (crc << 8) ^ Crc32Table[((crc >> 24) ^ ch) & 0xFF];
	    }
	    return crc;
	}
	/**
	* 使锟斤拷zip锟斤拷锟斤拷压锟斤拷
	* @param str 压锟斤拷前锟斤拷锟侥憋拷
	* @return 锟斤拷锟斤拷压锟斤拷锟斤拷锟斤拷谋锟�
	*/
	public static byte[] zip(byte[] in) {
		byte[] b = null;
		  try {
		   ByteArrayOutputStream bos = new ByteArrayOutputStream();
		   GZIPOutputStream gzip = new GZIPOutputStream(bos);
		   gzip.write(in);
		   gzip.finish();
		   gzip.close();
		   b = bos.toByteArray();
		   bos.close();
		  } catch (Exception ex) {
		   ex.printStackTrace();
		  }
		  return b;
	}
	public static byte[] encrypt(byte[] data, int offset, int len, byte[] key) {
		byte [] out = new byte[len];
		System.arraycopy(data, offset, out, 0, len);
		com.um.push.csa.tools.csaTsBlock(out, out.length, key);
		return out;
	}
	public static void fillInt(byte[] data, int offset, int val)
	{
		data[offset] = (byte)(val >> 24);
		data[offset + 1] = (byte)(val >> 16 & 0xff);
		data[offset + 2] = (byte)(val >> 8 & 0xff);
		data[offset + 3] = (byte)(val >> 0 & 0xff);
	}
	private static byte groupList_changeFlag = 1;
	public static byte getGroupListVersion() {
		return groupList_changeFlag++;
	}
	public static byte getProgPlayListVersion() {
		return 0;
	}
	private static byte cmd_changeFlag = 0;
	public static byte getCmdVersion() {
		return cmd_changeFlag++;
	}
	private static byte TDT_changeFlag = 0;
	public static byte getTDTVersion() {
		return TDT_changeFlag++;
	}
	public static byte getSplitScreenVersion() {
		return 0;
	}
	public static byte[] getMd5(byte[] data, int offset, int len) {
		MessageDigest md = null;
		try {
			 md = MessageDigest.getInstance("MD5");
			 md.update(data, offset, len);
		     return  md.digest(); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}  
        return null;
	}
	public static byte getDirTableID(dirType type) {
		switch(type)
		{
			case BASEPARAM:
				return (byte)0x3b;
			case PLAYEPG:
				return (byte)0x3c;
			case AD:
				return (byte)0x3d;
			case SPECIAL:
				return (byte)0x3e;
		}
		return 0;
	}
	private static byte baseparam_ver = 0;
	private static byte playerepg_ver = 0;
	private static byte ad_ver = 0;
	public static byte getDirVersion(dirType type) {
		byte ver = 0;
		switch (type) {
			case BASEPARAM:
				ver = baseparam_ver++;
				break;
			case PLAYEPG:
				ver = playerepg_ver++;
				break;
			case AD:
				ver = ad_ver++;
				break;
			default:
				break;
		}
		return ver;
	}
	private static byte pmt_ver = 0;
	public static byte getPmtVersion(){
		byte ver = 0;
		ver = pmt_ver++;
		return ver;
	}
	/*锟斤拷锟斤保证锟斤拷fid锟斤拷锟斤拷锟斤拷锟街节讹拷同时锟斤拷为0,锟斤拷为锟斤拷锟斤拷锟斤拷锟斤拷锟矫的凤拷锟斤拷锟斤拷锟�只支锟斤拷锟斤拷锟斤拷锟街斤拷之锟斤拷锟斤拷锟斤拷系*/
	private static int m_fid = 256;
	public static int getFid(String m_path, byte[] m_md5) {
		int id = m_fid++;
		while((id & 0xff) == 0)
		{
			id = m_fid++;
		}
		return id;
	}
	
	public static void fillByte(byte[] data, int offset, byte val){
		data[offset] = (byte)(val & 0xff);
	}
	public static void fillShort(byte[] data, int offset, int val) {
		data[offset] = (byte)(val >> 8 & 0xff);
		data[offset + 1] = (byte)(val >> 0 & 0xff);
	}
	public static void filllong(byte[] data, int offset, long val) {
		data[offset] =  (byte)((val >> 56) & 0xff) ;
		data[offset + 1] =  (byte)((val >> 48) & 0xff) ;
		data[offset + 2] =  (byte)((val >> 40) & 0xff) ;
		data[offset + 3] =  (byte)((val >> 32) & 0xff) ;
		data[offset + 4] =  (byte)((val >> 24) & 0xff) ;
		data[offset + 5] =  (byte)((val >> 16) & 0xff) ;
		data[offset + 6] =  (byte)((val >> 8) & 0xff) ;
		data[offset + 7] = (byte)(val & 0xff) ;
	}

	public static void writeToFlie(String name ,byte[] tmpdata) {
		File file = new File(debugPath+name);
		if(!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			try {
				fos.write(tmpdata);
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
			
	}

}
