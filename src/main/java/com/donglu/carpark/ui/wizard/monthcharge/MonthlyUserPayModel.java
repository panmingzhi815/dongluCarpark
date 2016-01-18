package com.donglu.carpark.ui.wizard.monthcharge;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.util.StrUtil;

public class MonthlyUserPayModel extends SingleCarparkMonthlyUserPayHistory {
	
	private int count;
	private List<SingleCarparkMonthlyCharge> allmonth=new ArrayList<>();
	private SingleCarparkMonthlyCharge selectMonth;
	private String createTimeLabel;
	private boolean free=true;
	private boolean payMoney=true;
	private boolean payDate=true;
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
		s.setCreateTime(new Date());
		s.setMonthamount(getMonthamount());
		s.setMonthCharge(getMonthCharge());
		s.setOldOverDueTime(getOldOverDueTime());
		s.setOverdueTime(getOverdueTime());
		s.setPlateNO(getPlateNO());
		if (!StrUtil.isEmpty(selectMonth)) {
			s.setRentType(selectMonth.getChargeName());
		}
		s.setUserIdCard(getUserIdCard());
		s.setUserName(getUserName());
		s.setId(id);
		s.setOperaName(getOperaName());
		return s;
	}

	public String getCreateTimeLabel() {
		return StrUtil.formatDate(getCreateTime(), "yyyy-MM-dd HH:mm:ss");
	}

	public void setCreateTimeLabel(String createTimeLabel) {
		this.createTimeLabel = createTimeLabel;
		if (pcs != null)
			pcs.firePropertyChange("createTimeLabel", null, null);
	}

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
		if (pcs != null)
			pcs.firePropertyChange("free", null, null);
	}

	public boolean isPayMoney() {
		return payMoney;
	}

	public void setPayMoney(boolean payMoney) {
		this.payMoney = payMoney;
		if (pcs != null)
			pcs.firePropertyChange("payMoney", null, null);
	}

	public boolean isPayDate() {
		return payDate;
	}

	public void setPayDate(boolean payDate) {
		this.payDate = payDate;
		if (pcs != null)
			pcs.firePropertyChange("payDate", null, null);
	}
}
