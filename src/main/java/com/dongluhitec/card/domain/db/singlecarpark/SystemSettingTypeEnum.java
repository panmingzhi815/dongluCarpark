package com.dongluhitec.card.domain.db.singlecarpark;

public enum SystemSettingTypeEnum {
	车位满是否允许临时车入场("false"),
	车位满是否允许免费车入场("false"),
	车位满是否允许储值车入场("false"),
	临时车入场是否确认("false"),
	临时车零收费是否自动出场("false"),
	固定车入场是否确认("false"),
	固定车出场确认("false"),
	数据库备份位置("d:\\carpark.bak"),
	图片保存位置(System.getProperty("user.dir")),
	图片保存多少天("30"),
	是否自动删除图片("false"),
	是否允许无牌车进("false"),
	同一车牌识别间隔("1"), 出场确认放行("false"),
	固定车到期变临时车("false"),
	双摄像头识别间隔("0"),
	左下监控("false"),
	右下监控("false"),
	固定车车位满作临时车计费("false"),自动识别出场车辆类型("false"),进场允许修改车牌("false"),
	进场允许手动入场("false"),
	
	DateBase_version("1.0.0.2"),软件版本("1.0.0.2"),    ;
	;
	
	private String defaultValue;
	
	SystemSettingTypeEnum(String defaultValue){
		this.defaultValue=defaultValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
}
