package com.dongluhitec.card.domain.db.singlecarpark;

import org.slf4j.helpers.MessageFormatter;


public enum CameraTypeEnum {
	A型("rtsp://{}:554/h264ESVideoTest"),B型("rtsp://VisionZenith:147258369@{}:8557/h264");
	
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
			return "A型";
		case 臻识:
			return "B型";
		}
		return super.toString();
	}
}
