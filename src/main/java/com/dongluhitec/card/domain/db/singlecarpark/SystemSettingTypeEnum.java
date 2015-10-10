package com.dongluhitec.card.domain.db.singlecarpark;

public enum SystemSettingTypeEnum {
	车位满是否允许临时车入场("false"),
	车位满是否允许免费车入场("false"),
	车位满是否允许储值车入场("false"),
	临时车入场是否确认("false"),
	临时车零收费是否自动出场("false"),
	固定车入场是否确认("false"),
	固定车出场确认("false"),
	数据库备份位置(""),
	图片保存位置(""),
	图片保存多少天("999");
	private String defaultValue;
	
	SystemSettingTypeEnum(String defaultValue){
		this.defaultValue=defaultValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
}
