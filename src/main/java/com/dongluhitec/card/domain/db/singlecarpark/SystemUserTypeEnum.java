package com.dongluhitec.card.domain.db.singlecarpark;

public enum SystemUserTypeEnum {
	系统管理员(1),
	普通管理员(2),
	操作员(3);
	
	int level;
	SystemUserTypeEnum(int i){
		level=i;
	}
	public int getLevel() {
		return level;
	}
}
