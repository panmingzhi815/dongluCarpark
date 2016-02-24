package com.dongluhitec.card.domain.db.singlecarpark;

import org.slf4j.helpers.MessageFormatter;

import com.donglu.carpark.ui.Login;
import com.dongluhitec.card.hardware.plateDevice.PlateNOJNA;
import com.dongluhitec.card.hardware.plateDevice.lpr.LPRJNA;
import com.dongluhitec.card.hardware.plateDevice.xinluwei.XinlutongJNA;
import com.google.inject.Injector;


public enum CameraTypeEnum {
	信路威("rtsp://{}:554/h264ESVideoTest",XinlutongJNA.class),臻识("rtsp://VisionZenith:147258369@{}:8557/h264",LPRJNA.class),其他(null,null);
	
	String rtsp;
	private Class<? extends PlateNOJNA> c;
	CameraTypeEnum(String rtsp,Class<? extends PlateNOJNA> c){
		this.rtsp=rtsp;
		this.c=c;
	}
	
	public String getRtspAddress(Object... o){
		String message = MessageFormatter.arrayFormat(rtsp, o).getMessage();
		return message;
	}
	public PlateNOJNA getJNA(Injector i){
		if (i==null) {
			return null;
		}
		return i.getInstance(c);
	}
	@Override
	public String toString() {
		switch (this) {
		case 信路威:
			return "X型";
		case 臻识:
			return "Z型";
		default:
			return super.toString();
		}
	}
}
