package com.donglu.carpark.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.util.ThreadUtil;

public class HttpRequestUtil {
	private static ExecutorService httpExecutor = Executors.newCachedThreadPool(ThreadUtil.createThreadFactory("云平台数据同步线程"));
	private static final Logger log = LoggerFactory.getLogger(HttpRequestUtil.class);
	public static String get(String httpUrl) {
		return get(httpUrl, null);
	}
	public static String get(String httpUrl,Map<String,String> headers) {
//		log.debug("准备对地址：["+actionUrl+"]发送消息:"+parameters);
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			if (headers==null) {
				connection.setRequestProperty("Charset", "UTF-8");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			}else {
				for (String key : headers.keySet()) {
					connection.setRequestProperty(key, headers.get(key));
				}
			}
			connection.connect();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String lines;
			StringBuffer sbf = new StringBuffer();
			while ((lines = reader.readLine()) != null) {
				lines = new String(lines.getBytes(), "utf-8");
				sbf.append(lines);
			}
			String msg = sbf.toString();
			
			reader.close();
			// 断开连接
			connection.disconnect();
			return msg;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String post(String string, Map<String, Object> jo) {
		try {
			return postMssage(string, jo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private static String postMssage(String actionUrl, Map<String, Object> maps) throws Exception{
		Set<String> keySet = maps.keySet();
		String parameters=null;
		for (String p : keySet) {
			Object object = maps.get(p);
			if(parameters==null){
				parameters=p+"="+object;
			}else{
				parameters+="&"+p+"="+object;
			}
		}
		return httpPostMssage(actionUrl, parameters);
	}
	private static String httpPostMssage(String actionUrl, String parameters) throws Exception {
		return httpPostMssage(actionUrl, parameters,10000);
	}
	private static String httpPostMssageInThread(String actionUrl, String parameters,int readTimeOut) throws Exception {
		Future<String> submit = httpExecutor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return httpPostMssage(actionUrl, parameters, readTimeOut);
			}
		});
		return submit.get(readTimeOut, TimeUnit.MILLISECONDS);
	}
	public static String httpPostMssage(String actionUrl, String parameters,int readTimeOut) throws Exception {
		return httpPostMssage(actionUrl, parameters, null, readTimeOut);
	}
	public static String httpPostMssage(String actionUrl, String parameters,String[] headers,int readTimeOut) throws Exception {
		if (StrUtil.isEmpty(actionUrl)) {
			return null;
		}
		log.info("准备对地址：["+actionUrl+"]发送消息:"+parameters);
		URL url = new URL(actionUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setReadTimeout(readTimeOut);
		connection.setConnectTimeout(3000);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Charset", "UTF-8");
		
		if (headers!=null) {
			for (String string : headers) {
				String[] split = string.split("=");
				connection.setRequestProperty(split[0], split[1]);
			} 
		}else {
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		}
		
		OutputStream os = connection.getOutputStream();
		os.write(parameters.getBytes("UTF-8"));
		os.flush();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String lines;
		StringBuffer sbf = new StringBuffer();
		while ((lines = reader.readLine()) != null) {
			lines = new String(lines.getBytes(), "utf-8");
			sbf.append(lines);
		}
		String msg = sbf.toString();
		log.debug(msg);
		reader.close();
		// 断开连接
		connection.disconnect();
		return msg;
	}
	public static String post(String url, String[] headers, String jsonString) throws Exception {
		return httpPostMssage(url, jsonString,headers, 3000);
	}

}
