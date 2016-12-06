package com.donglu.carpark.hardware.bx;

public interface BXScreenService {
	boolean sendContent(int identitifire,String ip, String content);
	public boolean sendPosition(int identitifire,String ip, int position);
	boolean sendPlateNO(int identitifire,String ip, String  plateNO,boolean isTrue);
	boolean init(int handle);
}
