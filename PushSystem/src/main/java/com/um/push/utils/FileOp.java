package com.um.push.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;



public class FileOp {
	private static Logger logger = Logger.getLogger(FileOp.class); 
	

	
	public static String saveStr2File(String baseFileDir, String filename,String saveStrContent) {
		logger.info("baseFileDir:" + baseFileDir+" filename:"+filename);
		logger.info("saveStrContent:"+saveStrContent);
		String path = null;

		System.out.println("file:" + baseFileDir + filename);
		File savefile = new File(baseFileDir + filename);

		/* 构建文件目录以及目录文件 */
		File fileDir = new File(baseFileDir);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		if (!savefile.exists()) {

			try {
				savefile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			FileOutputStream out = new FileOutputStream(savefile, true);
			out.write(saveStrContent.getBytes("utf-8"));
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		path = filename;

		return path;
	}
	
	public static String getFileContent(String saveFilePath)
			throws IOException {
		/* 文件存储在容器中的绝对路径 */
		String file = saveFilePath;
		System.out.println("file="+file);
		logger.info(file);
		File fp = new File(file);

		if (!fp.exists() || fp.isDirectory())
			throw new FileNotFoundException();
		FileInputStream fis = new FileInputStream(file);
		int length = fis.available();
		byte[] buf = new byte[length];
		fis.read(buf);
		fis.close();
		return new String(buf, "UTF-8");

	}

	public static String getFileContent(String saveFilePath, String name)
			throws IOException {
		/* 文件存储在容器中的绝对路径 */
		String file = saveFilePath + name;
		System.out.println("file="+file);
		logger.info(file);
		File fp = new File(file);

		if (!fp.exists() || fp.isDirectory())
			throw new FileNotFoundException();
		FileInputStream fis = new FileInputStream(file);
		int length = fis.available();
		byte[] buf = new byte[length];
		fis.read(buf);
		fis.close();
		return new String(buf, "UTF-8");

	}

	public static int delFile(String saveFilePath, String name) {
		/* 文件存储在容器中的绝对路径 */
		// String saveFilePath =
		// this.getServletConfig().getServletContext().getRealPath("") +"/";
		String file = saveFilePath + name;
		System.out.println("[delFile]file = "+file);
		File fp = new File(file);

		if (fp.exists()) {
			fp.delete();
			return 0;
		} else {
			return -1;
		}
	}

	/**
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @param srcCoding
	 * @param destCoding
	 * @return
	 * @throws IOException
	 */
	public static boolean copyFile(String sourceTxt, String destTxt)
			throws IOException {// 把文件转换为GBK文件

		/* 构建文件目录以及目录文件 */
		File file = new File(destTxt);
		//System.out.println("fileDir.getCanonicalPath(): " + file.getParent());

		File fileDir = new File(file.getParent());
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		try {
			OutputStream os = new FileOutputStream(file);
			InputStream fis = new FileInputStream(sourceTxt);
			byte[] buf = new byte[255];
			int len = 0;
			while ((len = fis.read(buf)) != -1) {
				os.write(buf, 0, len);
			}
			fis.close();
			os.flush();
			os.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void createFolder(String folderPath) {
		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		return ;
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	

	/**
	 * 删除本地硬盘资源
	 * 
	 * @param filepathString
	 *            （文件全路径，包括文件名）
	 * @return
	 */
	public static boolean deleteLocalDiskFile(String filepathString) {
		boolean result = false;
		File file = new File(filepathString);
		if (file.exists()) {
			result = file.delete();
		}
		return result;
	}

	/**
	 * 删除本地硬盘资源
	 * 
	 * @param filepathString
	 *            文件绝对路径
	 * @param filenameString
	 *            文件名
	 * @return
	 */

	public static boolean deleteLocalDiskFile(String filepathString,
			String filenameString) {
		boolean result = false;
		File file = new File(filepathString + filenameString);
		if (file.exists()) {
			result = file.delete();
		}
		System.out.println("result:"+result);
		return result;
	}

	public static String encChineseStr(String str)
	{
		if(str == null)
			return str;
		try
		{
			return java.net.URLEncoder.encode(str, "UTF-8");
		}
		catch(Exception ex)
		{
			return str;
		}
	}
	
	/** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
   public static void copyFolder(String oldPath, String newPath) { 

       try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File a=new File(oldPath); 
           String[] file=a.list(); 
           File temp=null; 
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ 
                   temp=new File(oldPath+file[i]); 
               } 
               else{ 
                   temp=new File(oldPath+File.separator+file[i]); 
               } 

               if(temp.isFile()){ 
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i]); 
               } 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制整个文件夹内容操作出错"); 
           e.printStackTrace(); 

       } 

   }
   
	  /** 
     * 复制整个文件夹内容 
     * @param oldPath String 原文件路径 如：c:/fqf 
     * @param newPath String 复制后路径 如：f:/fqf/ff 
     * @return boolean 
     */ 
   public static void copyFolder(String oldPath, String newPath,String filter) { 

       try { 
           (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹 
           File a=new File(oldPath); 
           if(!a.exists()){
        	   System.out.println("no oldpath copyFolder failed");
        	   return ;
           }
           String[] file=a.list(); 
           File temp=null; 
           for (int i = 0; i < file.length; i++) { 
               if(oldPath.endsWith(File.separator)){ 
                   temp=new File(oldPath+file[i]); 
               } 
               else{ 
                   temp=new File(oldPath+File.separator+file[i]); 
               } 
               if(temp.getName().contains(filter))
            	   continue;
               if(temp.isFile()){ 
                   FileInputStream input = new FileInputStream(temp); 
                   FileOutputStream output = new FileOutputStream(newPath + "/" + 
                           (temp.getName()).toString()); 
                   byte[] b = new byte[1024 * 5]; 
                   int len; 
                   while ( (len = input.read(b)) != -1) { 
                       output.write(b, 0, len); 
                   } 
                   output.flush(); 
                   output.close(); 
                   input.close(); 
               } 
               if(temp.isDirectory()){//如果是子文件夹 
                   copyFolder(oldPath+"/"+file[i],newPath+"/"+file[i],filter); 
               } 
           } 
       } 
       catch (Exception e) { 
           System.out.println("复制整个文件夹内容操作出错"); 
           e.printStackTrace(); 

       } 

   }
   
   /** 
	* 复制单个文件  add 2017-2-18 
	* @param oldPath String 原文件路径 如：c:/fqf.txt 
	* @param newPath String 复制后路径 如：f:/fqf.txt 
	* @return boolean
	* @author zhiye.chen
	*/ 
	public static void copyOnlyFile(String oldPath, String newPath) { 
		try { 
			int bytesum = 0; 
			int byteread = 0; 
			File oldfile = new File(oldPath); 
			if (oldfile.exists()) { //文件存在时 
				InputStream inStream = new FileInputStream(oldPath); //读入原文件 
				FileOutputStream fs = new FileOutputStream(newPath); 
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.flush();
				fs.close();
			} 
		}catch (Exception e) { 
			System.out.println("复制单个文件操作出错"); 
			e.printStackTrace(); 	
		} 

	} 
	


	/** *//**文件重命名 
    * @param path 文件目录 
    * @param oldname  原来的文件名 
    * @param newname 新文件名 
    */ 
    public static void renameFile(String path,String oldname,String newname){ 
        if(!oldname.equals(newname)){//新的文件名和以前文件名不同时,才有必要进行重命名 
            File oldfile=new File(path+"/"+oldname); 
            File newfile=new File(path+"/"+newname); 
            if(!oldfile.exists()){
                return;//重命名文件不存在
            }
            if(newfile.exists())//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
                System.out.println(newname+"已经存在！"); 
            else{ 
                oldfile.renameTo(newfile); 
            } 
        }else{
            System.out.println("新文件名和旧文件名相同...");
        }
    }

	
	/*************************************************
	 * 和文件编码格式有关
	 * *****************************************************************************************************************/
	public static void convert(String oldFile, String oldCharset,String newFlie, String newCharset) {
		BufferedReader bin;
		FileOutputStream fos;
		StringBuffer content = new StringBuffer();
		try {
			System.out.println("the old file is :" + oldFile);
			System.out.println("The oldCharset is : " + oldCharset);
			bin = new BufferedReader(new InputStreamReader(new FileInputStream(
					oldFile), oldCharset));
			String line = null;
			while ((line = bin.readLine()) != null) {
				// System.out.println("content:" + content);
				content.append(line);
				content.append(System.getProperty("line.separator"));
			}
			bin.close();
			// File dir = new File(newFlie.substring(0,
			// newFlie.lastIndexOf("\\")));
			File dir = new File(newFlie);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			fos = new FileOutputStream(newFlie);
			Writer out = new OutputStreamWriter(fos, newCharset);
			out.write(content.toString());
			out.close();
			fos.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void fetchFileList(String strPath, List<String> filelist,final String regex) {
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		Pattern p = Pattern.compile(regex);
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				fetchFileList(files[i].getAbsolutePath(), filelist, regex);
			} else {
				String strFileName = files[i].getAbsolutePath().toLowerCase();
				Matcher m = p.matcher(strFileName);
				if (m.find()) {
					filelist.add(strFileName);
				}
			}
		}
	}

	/**
	 * 判断文件的编码格式
	 * 
	 * @param fileName
	 *            :file
	 * @return 文件编码格式
	 * @throws Exception
	 */
	public static String codeString(String fileName) throws Exception {
		File file = new File(fileName);
		if(!file.exists()){
			return "";
		}
		FileInputStream fis = new FileInputStream(fileName);
		BufferedInputStream bin = new BufferedInputStream(fis);
		int p = (bin.read() << 8) + bin.read();
		String code = null;
		System.out.println("[luanma][codeString]p = "+Integer.toHexString(p));
		switch (p) {
			case 0xe9ba:
			case 0xefbb:
			case 0xe6b7:
			case 0x7b22:
			case 0xe688:
				code = "UTF-8";
				break;
			case 0xfffe:
				code = "Unicode";
				break;
			case 0xfeff:
				code = "UTF-16BE";
				break;
			default:
				code = "GBK";
		}
		bin.close();
		fis.close();
		return code;
	}
	
	

	@SuppressWarnings("resource")
	public static String readStrFromFile(String jsonpath) {
		// TODO Auto-generated method stub
		String dc = "UTF-8";
		try {
			dc = codeString(jsonpath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if("".equals(dc)){
			return "";
		}
		File file = new File(jsonpath);
		if(file.exists()){
			String json = "";
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonpath),dc));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			String line = "";  
			try {
				if(br!=null){
					while ((line = br.readLine()) != null) {  
					      json+=line;  
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					br.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return json;
		}else{
			return "";
		}
		
	}
	public static void doTXT2UTF8(String filePath) throws Exception{
		List<String> files = new ArrayList<String>();
		FileOp.fetchFileList(filePath, files,".txt");
		String filecode  = FileOp.codeString(filePath);
		System.out.println("filecode : " + filecode);
		if (!filecode.equals("UTF-8")) {
			FileOp.convert(filePath, filecode,filePath, "UTF-8");
		}
	}

	public static List<String> getFileList(String path, String contain) {
		// TODO Auto-generated method stub
		List<String> filepath = new ArrayList<String>();
		File pathFile = new File(path);
		if(!pathFile.isDirectory())
			return filepath;
		else{
			File[] files = pathFile.listFiles();
			for(File f:files){
				if(f.getName().contains(contain)){
					filepath.add(f.getAbsolutePath());
				}
			}
		}
		return filepath;
	}
	
}
