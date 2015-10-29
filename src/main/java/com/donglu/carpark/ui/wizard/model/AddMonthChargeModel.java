package com.donglu.carpark.ui.wizard.model;

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
	public SingleCarparkMonthlyCharge getSingleCarparkMonthlyCharge() {
		SingleCarparkMonthlyCharge monthlyCharge = new SingleCarparkMonthlyCharge();
		monthlyCharge.setCarpark(getCarpark());
		monthlyCharge.setCarType(getCarType());
		monthlyCharge.setChargeCode(getChargeCode());
		monthlyCharge.setChargeName(getChargeName());
		monthlyCharge.setDelayDays(getDelayDays());
		monthlyCharge.setExpiringDays(getExpiringDays());
		monthlyCharge.setNote(getNote());
		monthlyCharge.setParkType(getParkType());
		monthlyCharge.setPrice(getPrice());
		monthlyCharge.setRentingDays(getRentingDays());
		monthlyCharge.setId(getId());
		return monthlyCharge;
	}
	
}
