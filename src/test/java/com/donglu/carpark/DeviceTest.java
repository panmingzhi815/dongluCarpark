package com.donglu.carpark;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.google.common.base.Strings;

public class DeviceTest {
	public static void main(String[] args) throws Exception {
		Socket s=new Socket("192.168.1.137", 10001);
		for (int i = 0; i < 10; i++) {
			OutputStream os = s.getOutputStream();
			os.write(getBytes());
			InputStream is = s.getInputStream();
			byte[] bs = new byte[11];
			is.read(bs);
			System.out.println(byteToString(bs));
		}
		s.close();
	}

	private static byte[] getBytes() {
		String s="01 57 00 01 00 01 46 02 04 04 01 01 16 00 B3 B5 C5 C6 D7 D4 B6 AF CA B6 B1 F0 2C C7 EB BC F5 CB D9 BB BA D0 D0 21 FF 0D FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF 03 73";
		String[] split = s.split(" ");
		byte[] bs=new byte[split.length];
		for (int i = 0; i < split.length; i++) {
			String string = split[i];
			Integer integer = Integer.valueOf(string,16);
			bs[i]=(byte) integer.intValue();
		}
		return bs;
	}
	public static String byteToString(byte[] bs){
		String s="";
		for (byte b : bs) {
			String string = Integer.toHexString(b&0xff).toUpperCase();
			s+=" "+Strings.padStart(string, 2, '0');
		}
		return s;
	}
}
