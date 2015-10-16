package com.donglu.carpark.server.imgserver;

import

java.io.DataOutputStream;

import

java.io.FileInputStream;

import

java.io.IOException;

import

java.io.InputStream;

import

java.net.HttpURLConnection;

import

java.net.URL;

public class FileuploadSend {

	public static String upload(String actionUrl, String FileName) throws IOException {

		String BOUNDARY = java.util.UUID.

		randomUUID().toString();

		String PREFFIX ="--", LINEND ="\r\n";

		String MULTIPART_FROM_DATA ="multipart/form-data";

		String CHARSET ="UTF-8";

		URL uri =new URL(actionUrl);

		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

		// 设置从主机读取数据超时

		conn.setReadTimeout(10 * 1000);

		conn.setDoInput(true);

		conn.setDoOutput(true);

		conn.setUseCaches(false);

		conn.setRequestMethod("POST");

		conn.setRequestProperty("connection","keep-alive");

		conn.setRequestProperty("Charset","UTF-8");

		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA +";boundary="+ BOUNDARY);

		DataOutputStream outStream =new DataOutputStream(conn.getOutputStream());

		if(FileName !=null) {

			// 构建发送字符串数据

			StringBuilder sb1 =new StringBuilder();

			sb1.append(PREFFIX);

			sb1.append(BOUNDARY);

			sb1.append(LINEND);

			sb1.append("Content-Disposition: form-data; name=\"file\";filename=\""+ FileName +"\""+ LINEND);

			sb1.append("Content-Type: application/octet-stream;chartset="+ CHARSET + LINEND);

			sb1.append(LINEND);

			// 写入到输出流中

			outStream.write(sb1.toString().getBytes());

			// 将文件读入输入流中

			InputStream is =new FileInputStream(FileName);

			byte[] buffer =new byte[1024];

			int len = 0;
			// 写入输出流
			while((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			is.close();
			// 添加换行标志
			outStream.write(LINEND.getBytes());
		}
		// 请求结束标志
		byte[] end_data = (PREFFIX + BOUNDARY + PREFFIX +LINEND).getBytes();
		outStream.write(end_data);
		// 刷新发送数据
		outStream.flush();
		// 得到响应码
		int
		res = conn.getResponseCode();
		// System.out.println(res);
		InputStream in =null;

		// 上传成功返回200

		if(res == 200) {

			in = conn.getInputStream();
			int ch;
			StringBuilder sb2 = new StringBuilder();
			// 保存数据
			while ((ch = in.read()) != -1) {

				sb2.append((char) ch);
			}
		}
		// 如果数据不为空，则以字符串方式返回数据，否则返回null
		return in ==null?null: in.toString();

	}

}
