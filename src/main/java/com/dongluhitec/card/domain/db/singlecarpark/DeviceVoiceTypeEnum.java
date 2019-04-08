package com.dongluhitec.card.domain.db.singlecarpark;

public enum DeviceVoiceTypeEnum {
	固定车通道语音("固定车通道",1),
	储值车通道语音("储值车通道",1),
	临时车通道语音("临时车通道",1),
	临时车进场语音("欢迎光临,请入场停车",1),
	固定车进场语音("月租车辆,欢迎光临,请入场停车",1),
	储值车进场语音("储值车辆,欢迎光临,请入场停车",1),
	临时车出场语音("祝您一路平安",1),
	固定车出场语音("月租车辆,祝您一路平安",1),
	储值车出场语音("祝您一路平安",1),
	储值车余额不足语音("余额不足,请联系管理员",1),
	固定车到期语音("车辆已过期,请联系管理员",1),
	进口开闸语音("欢迎光临,请入场停车",1),
	出口开闸语音("祝您一路平安",1), 
	固定停车场临时车进入语音("固定停车场,不允许临时车进",1), 
	固定车车位停满禁止进入语音("个人车位已满",1),
	临时车入场确认语音("临时车等待确认",1),
	黑名单入场语音("管制车辆，请联系管理员",1),
	无进场记录语音("此车未入场",1), 无牌车禁止扫码入场语音("无牌车,禁止入场",1), 无牌车禁止扫码出场语音("无牌车",1), 未出场在进场语音("车辆未出场",1);
	;
	
	private String content;
	private int volume;

	DeviceVoiceTypeEnum(String content,int volume){
		this.content=content;
		this.volume=volume;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}
}
