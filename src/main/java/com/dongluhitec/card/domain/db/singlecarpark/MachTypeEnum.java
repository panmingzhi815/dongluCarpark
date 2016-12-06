package com.dongluhitec.card.domain.db.singlecarpark;

public enum MachTypeEnum {
	P,C,PAC,POC;
	public String toString() {
		switch (this) {
		case P:
			return "车牌";
		case C:
			return "卡片";
		case PAC:
			return "车牌和卡片";
		case POC:
			return "车牌或卡片";
		}
		return "";
	};
	
}
