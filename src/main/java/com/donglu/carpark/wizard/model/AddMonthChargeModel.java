package com.donglu.carpark.wizard.model;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;

public class AddMonthChargeModel extends SingleCarparkMonthlyCharge {

	public static AddMonthChargeModel init() {
		AddMonthChargeModel model=new AddMonthChargeModel();
		model.setCarType("小车");
		model.setParkType("固定车位");
		model.setPrice(500);
		return model;
	}
	
}
