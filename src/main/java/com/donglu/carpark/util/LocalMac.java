package com.donglu.carpark.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;


import java.util.HashSet;
import java.util.Set;


/*

 * 物理地址是48位，别和ipv6搞错了

 */

public class LocalMac {
	/**
	 * @param args
	 * @throws IOException 
	 * 
	 */

	public static void main(String[] args) throws IOException {
		Process exec = Runtime.getRuntime().exec("arp -a");
		InputStream inputStream = exec.getInputStream();
		BufferedReader br=new BufferedReader(new InputStreamReader(inputStream,"gbk"));
		Set<String> sets=new HashSet<>();
		String s;
		while((s=br.readLine()) != null){
			String[] split = s.split(" ");
			for (int i = 0; i < split.length; i++) {
				String string = split[i].trim();
				if (string.equals("")) {
					continue;
				}
				System.out.println("split[i].trim()==="+string);
				String substring = string.substring(0, 1);
				try {
					Integer valueOf = Integer.valueOf(substring);
					System.out.println("substring==="+substring+"---"+valueOf);
					sets.add(string);
				} catch (NumberFormatException e) {
					
				}
				break;
			}
		}
		for (String string : sets) {
			System.out.println("sets====="+string);
		}
	}

	public static String getLocalMac() {
		// 获取网卡，获取地址
		try {
			InetAddress ia=InetAddress.getLocalHost();
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			return getMacByByte(mac);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param mac
	 * @return
	 */
	private static String getMacByByte(byte[] mac) {
		if (mac==null) {
			return null;
		}
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// 字节转换为整数
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp);
			if (str.length() == 1) {
				sb.append("0" + str);
			} else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}

}
