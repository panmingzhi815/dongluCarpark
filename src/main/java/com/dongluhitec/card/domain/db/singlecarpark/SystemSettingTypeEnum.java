package com.dongluhitec.card.domain.db.singlecarpark;

public enum SystemSettingTypeEnum {
	车位满是否允许临时车入场("false"),
	车位满是否允许免费车入场("false"),
	车位满是否允许普通车入场("true"),//免费车 参数和车类型相反
	车位满是否允许储值车入场("false"),
	临时车入场是否确认("false"),临时车弹窗确认("false"),出场收费弹窗显示("false"),
	临时车零收费是否自动出场("false"),
	固定车入场是否确认("false"),
	固定车出场确认("false"),
	数据库备份位置("d:\\carpark.bak"),
	图片保存位置(System.getProperty("user.dir")),
	图片保存多少天("30"),
	是否自动删除图片("false"),
	是否允许无牌车进("false"),
	同一车牌识别间隔("1"), 出场确认放行("false"),出场免费确认放行("true"),
	固定车到期变临时车("true"),固定车到期所属停车场限制("false"),
	固定车非所属停车场停留收费("false"), 固定车非所属停车场停留时间("15"),固定车到期提醒("false"),车位满临时车收费到固定车出场("false"),
	双摄像头识别间隔("0"),
	双摄像头忽略间隔("3000"),
	左下监控("false"),
	右下监控("false"),
	固定车车位满作临时车计费("false"),自动识别出场车辆类型("false"),进场允许修改车牌("false"),
	进场允许手动入场("false"),
	储值车提醒金额("60"),储值车进出场限制金额("20"),启用集中收费("false"),集中收费延迟出场时间("15"),集中收费时允许岗亭收费("false"),
	临时车通道限制("false"),
	启用车牌报送("false"),
	车位数显示方式("0"),
	免费原因("其他原因"),
	固定车提醒时间(""),停车场重复计费("false"),保存遥控开闸记录("false"),
	启用CJLAPP支付("false"),
	退出时需要密码("false"),
	访客车进场次数用完不能随便出("false"),
	固定车车牌匹配字符数("7"),
	绑定车辆允许场内换车("true"),绑定车辆场内换车时间("30"),换车时间内车辆无限制("true"),
	同一账号只能在一个地方登录("true"),
	收费口无人值守("false"),
	固定车转临时车弹窗提示("true"),
	显示指定停留时间的场内车("false"),
	无车牌时使用二维码进出场("false"),使用二维码缴费("true"),
	出场时检测云平台缴费间隔("10"),
	出场时等待云平台缴费超时时长("120"),
	优先使用云平台计费("false"),
	监控界面提示网络故障("true"),
	使用设备二维码("false"),
	显示网上支付金额("false"),
	
	抬杆自动收费放行("false"),收费放行打印小票("false"),
	
	自动关闭未选中的监控视频("false"),
	固定车到期变临时车收费自动记费出场("贵州演艺集团"),
	
	启动HTTP对外服务("false"),
	无记录自动放行("false"),
	特殊车辆自动放行("true"),
	特殊车辆车牌类型("(.*警)|(.*巡)|(.*应急.*)|(.*消.*)|(WJ.*)"),
	
	上传数据到绿地平台("false"),
	
	启用测速系统("false"),
	固定车超速速度("50"),
	固定车超速发送短信(""),
	固定车超速自动删除("30-3"),
	临时车超速自动拉黑("30-3-7"),
	普通超速速度("36-44"),
	严重超速速度("45"),
	临时车严重超速拉黑("365"),
	固定车严重超速取消授权("false"),
	固定车严重超速发送短信(""),
	
	支付完成后出场时间("15"),
	DateBase_version("1.0.0.31"),软件版本("1.0.0.33"),发布时间("2020-03-19 17:00:00"), 更新文件夹("jar,native"), 自动下载车牌("false"), 允许设备限时("false"), 访客车名称("访客车"), CadreSetting("false"), 
	启动短信发送服务("false"), 短信服务appid(""), 短信服务appsecret(""), 短信签名(""), 短信模板(""), 
	手动开闸更改车位("true"),    
	;
	
	private String defaultValue;
	
	SystemSettingTypeEnum(String defaultValue){
		this.defaultValue=defaultValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public boolean getBooleanValue(){
		try {
			return Boolean.valueOf(defaultValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public int getIntValue() {
		return Integer.valueOf(defaultValue);
	}
}
