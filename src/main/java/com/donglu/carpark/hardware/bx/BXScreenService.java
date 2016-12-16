package com.donglu.carpark.hardware.bx;

import java.util.List;

public interface BXScreenService {
	boolean sendContent(int identitifire,String ip, String content);
	public boolean sendPosition(int identitifire,String ip, int position);
	boolean sendPlateNO(int identitifire,String ip, String  plateNO,boolean isTrue);
	boolean init(int handle);
	void setPlateControlStatus(boolean plateControlSetting);
	void setWillInPlate(List<String> willInPlate);
}
