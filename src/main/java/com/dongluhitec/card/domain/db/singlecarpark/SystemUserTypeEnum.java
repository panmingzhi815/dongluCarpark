package com.dongluhitec.card.domain.db.singlecarpark;

public enum SystemUserTypeEnum {
	超级管理员(99),
	系统管理员(3),
	普通管理员(2),
	操作员(1);
	
	int level;
	SystemUserTypeEnum(int i){
		level=i;
	}
	public int getLevel() {
		return level;
	}
	public static int getLevel(String name){
		switch (name) {
		case "财务管理员":
			return 普通管理员.getLevel();
		case "维护管理员":
			return 系统管理员.getLevel();
		}
		try {
			return SystemUserTypeEnum.valueOf(name).getLevel();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	@Override
	public String toString() {
		switch (this) {
		case 普通管理员:
			return "财务管理员";
		case 系统管理员:
			return "维护管理员";
		}
		return super.toString();
	}
}
