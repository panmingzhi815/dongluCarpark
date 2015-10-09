package com.donglu.carpark.wizard.monthcharge;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;

public class MonthlyUserPayModel extends SingleCarparkMonthlyUserPayHistory {
	
	private int count;
	private List<SingleCarparkMonthlyCharge> allmonth=new ArrayList<>();
	private SingleCarparkMonthlyCharge selectMonth;
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
		if (pcs != null)
			pcs.firePropertyChange("count", null, null);
	}

	public List<SingleCarparkMonthlyCharge> getAllmonth() {
		return allmonth;
	}

	public void setAllmonth(List<SingleCarparkMonthlyCharge> allmonth) {
		this.allmonth = allmonth;
		if (pcs != null)
			pcs.firePropertyChange("allmonth", null, null);
	}

	public SingleCarparkMonthlyCharge getSelectMonth() {
		return selectMonth;
	}

	public void setSelectMonth(SingleCarparkMonthlyCharge selectMonth) {
		this.selectMonth = selectMonth;
		if (pcs != null)
			pcs.firePropertyChange("selectMonth", null, null);
	}
	
	public SingleCarparkMonthlyUserPayHistory getSingleCarparkMonthlyUserPayHistory(){
		SingleCarparkMonthlyUserPayHistory s=new SingleCarparkMonthlyUserPayHistory();
		s.setCarType(this.getCarType());
		s.setChargesMoney(getChargesMoney());
		s.setCreateTime(getCreateTime());
		s.setMonthamount(getMonthamount());
		s.setMonthCharge(getMonthCharge());
		s.setOldOverDueTime(getOldOverDueTime());
		s.setOverdueTime(getOverdueTime());
		s.setPlateNO(getPlateNO());
		s.setRentType(selectMonth.getChargeName());
		s.setUserIdCard(getUserIdCard());
		s.setUserName(getUserName());
		s.setId(id);
		return s;
	}
}
