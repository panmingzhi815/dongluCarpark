package com.donglu.carpark.service.impl.netty;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSONObject;

public class URLTest {
	public static void main(String[] args) throws Exception {
		URL url=new URL("http://192.168.2.189:8991");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		OutputStream os = conn.getOutputStream();
		JSONObject jo=new JSONObject();
		jo.put("buildingId", "7e257819d2764bb6aa5c1fd43baf2f71");
		jo.put("type", "PING_MSG");
		os.write(jo.toJSONString().getBytes());
		os.flush();
		InputStream is = conn.getInputStream();
		int read = is.read(new byte[1024]);
		System.out.println(read+"==");
	}
}
