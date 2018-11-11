package com.um.push.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class UnZipCompressorByAnt {

	  private static final int BUFFEREDSIZE = 1024;

	    /**
	     * 解压缩
	     * 
	     * @param zipFilePath
	     *            压缩包路径
	     * @param fileSavePath
	     *            解压路径
	     * @param isDelete
	     *            是否删除源文件
	     * @throws Exception
	     */
	    public void unZip(String zipFilePath, String fileSavePath, boolean isDelete)
	            throws Exception {
	        try {
	            (new File(fileSavePath)).mkdirs();
	            File f = new File(zipFilePath);
	            if ((!f.exists()) && (f.length() <= 0)) {
	                throw new Exception("要解压的文件不存在!");
	            }
	            ZipFile zipFile = new ZipFile(f);
	            String strPath, gbkPath, strtemp;
	            File tempFile = new File(fileSavePath);// 从当前目录开始
	            strPath = tempFile.getAbsolutePath();// 输出的绝对位置
	            Enumeration<ZipEntry> e = zipFile.getEntries();
	            while (e.hasMoreElements()) {
	                org.apache.tools.zip.ZipEntry zipEnt = e.nextElement();
	                gbkPath = zipEnt.getName();
	                if (zipEnt.isDirectory()) {
	                    strtemp = strPath + File.separator + gbkPath;
	                    File dir = new File(strtemp);
	                    dir.mkdirs();
	                    continue;
	                } else {
	                    // 读写文件
	                    InputStream is = zipFile.getInputStream(zipEnt);
	                    BufferedInputStream bis = new BufferedInputStream(is);
	                    gbkPath = zipEnt.getName();
	                    strtemp = strPath + File.separator + gbkPath;
	                    // 建目录
	                    String strsubdir = gbkPath;
	                    for (int i = 0; i < strsubdir.length(); i++) {
	                        if (strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
	                            String temp = strPath + File.separator
	                                    + strsubdir.substring(0, i);
	                            File subdir = new File(temp);
	                            if (!subdir.exists())
	                                subdir.mkdir();
	                        }
	                    }
	                    FileOutputStream fos = new FileOutputStream(strtemp);
	                    BufferedOutputStream bos = new BufferedOutputStream(fos);
	                    int len;
	                    byte[] buff = new byte[BUFFEREDSIZE];
	                    while ((len = bis.read(buff)) != -1) {
	                        bos.write(buff, 0, len);
	                    }
	                    bos.close();
	                    fos.close();
	                    bis.close();
	                    is.close();
	                }
	            }
	            zipFile.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw e;
	        }
	        if (isDelete) {
	        	System.out.println(zipFilePath);
	            new File(zipFilePath).delete();
	        }
	    }
	    
//	    public static void main(String[] args) {
//	        UnZipCompressorByAnt cpr = new UnZipCompressorByAnt();
//	        try {
//	            cpr.unZip("E:\\pushsystem\\baseparam\\baseparam.zip", "E:\\\\pushsystem\\\\baseparam\\\\", true);
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//
//	    }
}
