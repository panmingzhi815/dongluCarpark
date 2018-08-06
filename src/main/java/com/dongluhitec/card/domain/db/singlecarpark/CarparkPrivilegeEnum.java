package com.dongluhitec.card.domain.db.singlecarpark;

public enum CarparkPrivilegeEnum {
	carparkManage("carparkManage","停车场管理",null),
	carparkManageAddTop("carparkManageAddTop","添加组停车场",carparkManage),
	userManage("userManage","固定车管理",null),
	systemUserManage("systemUserManage","固定车管理",null),
	accountCarManage("accountCarManage","记账车管理",null),
	storeManage("storeManage","商铺管理",null),
	visitorManage("visitorManage","访客车管理",null),
	tempCarFreeManage("tempCarFreeManage","临时车优惠管理",null),
	searchHistory("searchHistory","记录查询",null)
	;
	
	private String key;
	private String value;
	private CarparkPrivilegeEnum parent;

	CarparkPrivilegeEnum(String key,String value,CarparkPrivilegeEnum parent){
		this.key = key;
		this.value = value;
		this.parent = parent;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public CarparkPrivilegeEnum getParent() {
		return parent;
	}
	
}
