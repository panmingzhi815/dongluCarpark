package com.dongluhitec.card.domain.db.singlecarpark;

public enum ScreenTypeEnum {
	零八接口显示小屏,零八接口显示大屏,一二接口显示屏,七五接口显示屏,户外屏;
	public int getType(){
		switch (this) {
		case 零八接口显示小屏:
			return 1;
		case 零八接口显示大屏:
			return 2;
		case 一二接口显示屏:
			return 3;
		case 七五接口显示屏:
			return 4;
		case 户外屏:
			return 5;
		}
		return 0;
	}
}
