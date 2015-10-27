package com.dongluhitec.card.domain.db.singlecarpark;

import com.google.common.base.Strings;

public enum CarTypeEnum {
	Motorcycle,SmallCar,BigCar;

	public static final String BIGCAR = "大车";
	public static final String SMALLCAR = "小车";
	public static final String MOTORCYCLE = "摩托车";

	@Override
	public String toString() {
		switch (this) {
			case BigCar:
				return CarTypeEnum.BIGCAR;
			case SmallCar:
				return CarTypeEnum.SMALLCAR;
			case Motorcycle:
				return CarTypeEnum.MOTORCYCLE;
			default:
				assert false : "Code cannot go here";
				return "";
		}
	}
	
	public static CarTypeEnum parse(String name){
		if(Strings.isNullOrEmpty(name)){
			return null;
		}
		CarTypeEnum[] values = CarTypeEnum.values();
		for (CarTypeEnum carTypeEnum : values) {
			if(carTypeEnum.toString().equals(name)){
				return carTypeEnum;
			}
		}
		return null;
	}
	
	public long index(){
		switch (this) {
		case SmallCar:
			return 2L;
		case BigCar:
			return 1L;
		case Motorcycle:
			return 3L;
		default:
			return 0L;
		}
	}
}
