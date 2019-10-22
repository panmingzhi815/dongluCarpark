package com.donglu.carpark.util.aliyun;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class AliyunSmsUtil {
	private static Logger LOGGER=LoggerFactory.getLogger(AliyunSmsUtil.class);
	public static void main(String[] args) throws Exception {
		String accessKeyId = "LTAIXZ4qJGagzqIv";
		String accessSecret = "90poU2cJhvavnoUAnkfvGExNZYBBgS";
		String signName = "东云智联";
		String templateCode = "SMS_150735052";
		String tel = "13537630413";
		String templateParam = "{\"code\":\"123456\"}";
		
		sendSms(accessKeyId, accessSecret, signName, templateCode, tel, templateParam);
	}

	/**
	 * @param accessKeyId
	 * @param accessSecret
	 * @param signName
	 * @param templateCode
	 * @param tel 电话
	 * @param templateParam 模板参数
	 */
	public static String sendSms(String accessKeyId, String accessSecret, String signName, String templateCode, String tel, String templateParam){
		JSONObject result=new JSONObject();
		result.put("Code", "-1");
		result.put("Message", "发送失败");
		try {
			Map<String, String> paras = new HashMap<>();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			paras.put("SignatureMethod", "HMAC-SHA1");
			paras.put("SignatureNonce", java.util.UUID.randomUUID().toString());
			paras.put("AccessKeyId", accessKeyId);
			paras.put("SignatureVersion", "1.0");
			paras.put("Timestamp", df.format(new Date()));
			paras.put("Format", "JSON");
			paras.put("Action", "SendSms");
			paras.put("Version", "2017-05-25");
			paras.put("RegionId", "cn-hangzhou");
			paras.put("PhoneNumbers", tel);
			paras.put("SignName", signName);
			paras.put("TemplateParam", templateParam);
			paras.put("TemplateCode", templateCode);
			java.util.TreeMap<String, String> sortParas = new java.util.TreeMap<String, String>();
			sortParas.putAll(paras);
			java.util.Iterator<String> it = sortParas.keySet().iterator();
			StringBuilder sortQueryStringTmp = new StringBuilder();
			while (it.hasNext()) {
				String key = it.next();
				sortQueryStringTmp.append("&").append(specialUrlEncode(key)).append("=").append(specialUrlEncode(paras.get(key)));
			}
			String sortedQueryString = sortQueryStringTmp.substring(1);// 去除第一个多余的&符号
//			System.out.println(sortedQueryString);

			StringBuilder stringToSign = new StringBuilder();
			stringToSign.append("GET").append("&");
			stringToSign.append(specialUrlEncode("/")).append("&");
			stringToSign.append(specialUrlEncode(sortedQueryString));
//			System.out.println(stringToSign);

			String sign = sign(accessSecret + "&", stringToSign.toString());
			LOGGER.info("待签名数据：{}，签名：{}",stringToSign,sign);

			String url = "http://dysmsapi.aliyuncs.com/?Signature=" + sign + sortQueryStringTmp;
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));) {
				String readLine = br.readLine();
				LOGGER.info("发送短信返回消息：{}",readLine);
				return readLine;
			} finally {
				conn.disconnect();
			}
		} catch (Exception e) {
			LOGGER.error("发送短信时发生错误",e);
			result.put("Message", "发送失败:"+e.getMessage());
		}
		return result.toJSONString();
	}

	public static String specialUrlEncode(String value) throws UnsupportedEncodingException {
		return java.net.URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
	}

	public static String sign(String accessSecret, String stringToSign) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
		mac.init(new javax.crypto.spec.SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA1"));
		byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(signData);
	}
}
