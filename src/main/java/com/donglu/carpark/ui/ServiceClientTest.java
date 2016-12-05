package com.donglu.carpark.ui;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.HttpConnection;

public class ServiceClientTest {
	private static String s="00000000ABCDEF12";
	public static void main(String[] args) {
		try {
			URL url = new URL("http://192.168.3.71:10004/");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(s.getBytes());
			os.flush();
			InputStream is = con.getInputStream();
			byte[] b=new byte[1024];
			is.read(b);
			System.out.println("==============="+new String(b));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
