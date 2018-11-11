package com.um.push.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class PublicRequest {

	public static String httpURLConectionGET(String GET_URL) {
		StringBuilder sb = null;
        try {
        	URL url = new URL(GET_URL);    // 把字符串转换为URL请求地址
        	HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 打开连接
        	connection.connect();// 连接会话
        	// 获取输入流
        	BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
               sb.append(line);
            }
            br.close();// 关闭流
           connection.disconnect();// 断开连接
           System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("失败!");
        }
        return sb == null? "":sb.toString();
    }
	
    /**
     * 接口调用  POST
     */
    public static String httpURLConnectionPOST (String POST_URL,HashMap<String,String> params) {
    	StringBuilder sb = null;
        try {
        	if(params == null) {
        		return "";
        	}
        	URL url = new URL(POST_URL);
            // 将url 以 open方法返回的urlConnection  连接强转为HttpURLConnection连接  (标识一个url所引用的远程对象连接)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();// 此时cnnection只是为一个连接对象,待连接中
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
            // 设置连接输入流为true
            connection.setDoInput(true);
            // 设置请求方式为post
            connection.setRequestMethod("POST");
            // post请求缓存设为false
            connection.setUseCaches(false);
            // 设置该HttpURLConnection实例是否自动执行重定向
            connection.setInstanceFollowRedirects(true);
            // 设置请求头里面的各个属性 (以下为设置内容的类型,设置为经过urlEncoded编码过的from参数)
            // application/x-javascript text/xml->xml数据 application/x-javascript->json对象 application/x-www-form-urlencoded->表单数据
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("contentType", "UTF-8");
            // 建立连接 (请求未开始,直到connection.getInputStream()方法调用时才发起,以上各个参数设置需在此方法之前进行)
            connection.connect();
            // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
            Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
            String param = "";
            while(iterator.hasNext()) {
            	Entry<String, String> enter = iterator.next();
            	param += enter.getKey()+"="+enter.getValue()+"&";
            }
            if(param.length()>0)
            	param = param.substring(0, param.length()-1);
            System.out.println(param);
            OutputStreamWriter out = new OutputStreamWriter(connection  
                    .getOutputStream(), "UTF-8"); 
            out.write(param);
            out.flush();
            out.close();
            System.out.println(connection.getResponseCode());
            
            // 连接发起请求,处理服务器响应  (从连接获取到输入流并包装为bufferedReader)
            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8")); 
            String line = "";
            sb = new StringBuilder(); // 用来存储响应数据
           
            // 循环读取流,若不到结尾处
           while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            bf.close();    // 重要且易忽略步骤 (关闭流,切记!) 
            connection.disconnect(); // 销毁连接
    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb == null? "":sb.toString();
    }
    
    public static String upLoadFile(String urlPath,String path,String file,HashMap<String, String> map) {
		System.out.println("urlPath-------------" + urlPath); 
		String filePath = path+file;
		DefaultHttpClient  client = new DefaultHttpClient();  
	    HttpPost post = new HttpPost(urlPath);  
	    post.setHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;)"); 
	    post.setHeader("Host", "****");  
        post.setHeader("Accept-Encoding","gzip");  
        post.setHeader("charset", "utf-8");
//        post.setHeader("Content-Type","multipart/form-data");
        MultipartEntity multipartEntity = new MultipartEntity();    
	    FileBody cbFileBody = new FileBody(new File(filePath));    
	    multipartEntity.addPart("file", cbFileBody); 
	    Iterator<Entry<String, String>> Iter = map.entrySet().iterator();
	    while(Iter.hasNext()) {
	    	Entry<String, String> enter = Iter.next();
	    	try {
	    		System.out.println(enter.getKey() +" "+enter.getValue());
				multipartEntity.addPart(enter.getKey(), new StringBody(enter.getValue(), Charset.forName("UTF-8")));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}   
	    }
	    
	    post.setEntity(multipartEntity);  
	    int  statusCode = -1;  
	    try {  
	        HttpResponse response = client.execute(post);  
	        statusCode = response.getStatusLine().getStatusCode();  
	    } catch (Throwable e) {  
	        //记录异常日志  
	        System.out.println("文件post到solr服务器："+urlPath+" 创建索引出现异常错误！异常信息："+e.getMessage());  
	    }  
	    System.out.println("statusCode = "+statusCode);
	    client.getConnectionManager().shutdown();
	    return "";  
	}
    
    
    /** 
     * 下载文件 
     *  
     * @param url 
     * @param filePath 
     */  
    public static boolean downLoadFile(String url, String filePath, Map<String, String> headMap) { 
    	boolean ret = false;
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        String URL = url;
        try {  
        	System.out.println("[downLoadFile]call");
            
            StringBuilder params = new StringBuilder("?");
            if (headMap != null && headMap.size() > 0) {  
                Set<String> keySet = headMap.keySet();  
                for (String key : keySet) {  
                	params.append(key+"="+headMap.get(key)+"&");
                }
                if(params.length()>1) {
                	URL+=params.substring(0, params.length()-1);
                }
            }  
            System.out.println("URL="+URL);
            HttpGet httpGet = new HttpGet(URL);  
            CloseableHttpResponse response1 = httpclient.execute(httpGet);  
            try {  
                System.out.println(response1.getStatusLine());  
                HttpEntity httpEntity = response1.getEntity();  
                long contentLength = httpEntity.getContentLength();  
                System.out.println("[downLoadFile]contentLength="+contentLength);
                InputStream is = httpEntity.getContent();  
                // 根据InputStream 下载文件  
                ByteArrayOutputStream output = new ByteArrayOutputStream();  
                byte[] buffer = new byte[4096];  
                int r = 0;  
                long totalRead = 0;  
                while ((r = is.read(buffer)) > 0) {  
                    output.write(buffer, 0, r);  
                    totalRead += r;  
                }  
                FileOutputStream fos = new FileOutputStream(filePath);  
                output.writeTo(fos);  
                output.flush();  
                output.close();  
                fos.close();  
                EntityUtils.consume(httpEntity);  
                ret = true;
            } catch (Exception e) {
				// TODO: handle exception
            	ret = false;
			}finally {  
                response1.close();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
            System.out.println("[downLoadFile]"+ret);
        }  
        return ret;
    }  
}
