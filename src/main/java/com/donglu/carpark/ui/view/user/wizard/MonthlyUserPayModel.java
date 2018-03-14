package com.donglu.carpark.ui.view.user.wizard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.util.StrUtil;

public class MonthlyUserPayModel extends SingleCarparkMonthlyUserPayHistory {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 167497205337286603L;
	private int count;
	private List<SingleCarparkMonthlyCharge> allmonth=new ArrayList<>();
	private SingleCarparkMonthlyCharge selectMonth;
	@SuppressWarnings("unused")
	private String createTimeLabel;
	private boolean free=true;
	private boolean isSelectedSize=true;
	private boolean payMoney=false;
	private boolean payDate=true;
	
	private Long userId;
	
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
		if (selectMonth!=null) {
			setCarType(selectMonth.getCarType());
			setMonthCharge(selectMonth.getPrice());
			setMonthamount(selectMonth.getRentingDays());
		}
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
		s.setUserType(getUserType());
		s.setUserAddress(getUserAddress());
		if (selectMonth!=null) {
			s.setMonthChargeId(selectMonth.getId());
			s.setMonthChargeCode(selectMonth.getChargeCode());
			s.setMonthChargeName(selectMonth.getChargeName());
		}
		return s;
	}

	@Override
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
		setSelectedSize(free);
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

	public boolean isSelectedSize() {
		return isSelectedSize;
	}

	public void setSelectedSize(boolean isSelectedSize) {
		this.isSelectedSize = isSelectedSize;
		firePropertyChange("isSelectedSize", null, null);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
