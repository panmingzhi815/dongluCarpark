package com.dongluhitec.card.domain.db.singlecarpark;

import org.slf4j.helpers.MessageFormatter;


public enum CameraTypeEnum {
	信路威("rtsp://{}:554/h264ESVideoTest"),臻识("rtsp://VisionZenith:147258369@{}:8557/h264"),其他(null);
	
	String rtsp;
	CameraTypeEnum(String rtsp){
		this.rtsp=rtsp;
	}
	
	public String getRtspAddress(Object... o){
		String message = MessageFormatter.arrayFormat(rtsp, o).getMessage();
		return message;
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
