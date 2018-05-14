package com.donglu.carpark.hardware;

import java.util.Date;

import com.dongluhitec.card.domain.db.Device;

public interface CarparkScreenService {
	public interface CarparkScreenServiceLog{
		public void log(String log);
	}
	public boolean showCarparkQrCode(Device device,int type,String content);
	public boolean carIn(Device device,String plate,String content,boolean isOpen);
	public boolean carOut(Device device,String plate,int times,String shouldMoney,String leftMoney);
	public boolean showCarparkPosition(Device device,int position);
	public boolean showCarparkUsualContent(Device device,String content);
	public boolean screenOpenDoor(Device device,int selectedIndex);
	public boolean restartScreen(Device device);
	public boolean setScreenColor(Device device,int selectedIndex);
	public boolean showSingleRowContent(Device device, int rowIndex, int voice, String content);
	public boolean initDevice(Device device);
	public boolean setDeviceDateTime(Device device, Date value);
	public Date readDeviceDateTime(Device device);
	void setLog(CarparkScreenServiceLog log);
	public boolean setQrCodeColor(Device device,int type);
	public boolean setQrCodeTime(Device device,int seconds);
	public boolean showCarparkQrCode(Device device, int type, String shortUrl, String content);
}
