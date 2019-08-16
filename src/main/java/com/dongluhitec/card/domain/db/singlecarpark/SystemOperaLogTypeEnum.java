package com.dongluhitec.card.domain.db.singlecarpark;

public enum SystemOperaLogTypeEnum {
	全部,固定用户,停车场,固定收费设置,临时收费设置,黑名单,系统用户, 参数设置, 商铺, 锁车, 改车牌, 车队操作, 访客, 登录登出,数据上传;
	
	String operaName;
	
	SystemOperaLogTypeEnum(){
		operaName=System.getProperty("userName");
	}

	public String getOperaName() {
		return operaName;
	}
	
	
}
