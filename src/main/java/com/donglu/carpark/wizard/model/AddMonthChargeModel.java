package com.donglu.carpark.wizard.model;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;

public class AddMonthChargeModel extends SingleCarparkMonthlyCharge {

	public AddMonthChargeModel(SingleCarparkMonthlyCharge m) {
		setCarpark(m.getCarpark());
		setCarType(m.getCarType());
		setChargeCode(m.getChargeCode());
		setChargeName(m.getChargeName());
		setDelayDays(m.getDelayDays());
		setExpiringDays(m.getExpiringDays());
		setNote(m.getNote());
		setParkType(m.getParkType());
		setPrice(m.getPrice());
		setRentingDays(m.getRentingDays());
		setId(m.getId());
	}
	public AddMonthChargeModel() {
		
	}
	public static AddMonthChargeModel init() {
		AddMonthChargeModel model=new AddMonthChargeModel();
		model.setCarType("小车");
		model.setParkType("固定车位");
		model.setPrice(500);
		return model;
	}
	
}
