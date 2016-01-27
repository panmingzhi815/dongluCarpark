package com.donglu.carpark.ui.view.store.wizard;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;

public class ChargeStoreModel extends SingleCarparkStoreChargeHistory {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5467677951308287688L;
	private Float num;

	public Float getNum() {
		return num;
	}

	public void setNum(Float num) {
		this.num = num;
		if (pcs != null)
			pcs.firePropertyChange("num", null, null);
	}

	public SingleCarparkStoreChargeHistory getStoreCharge() {
		SingleCarparkStoreChargeHistory sc=new SingleCarparkStoreChargeHistory();
		sc.setCouponNum(getCouponNum());
		sc.setCreateTime(getCreateTime());
		sc.setFreeHours(getFreeHours());
		sc.setFreeMoney(getFreeMoney());
		sc.setId(getId());
		sc.setStoryId(getStoryId());
		sc.setStoreName(getStoreName());
		sc.setLoginName(getLoginName());
		sc.setOperaName(getOperaName());
		sc.setPayType(getPayType());
		sc.setPayMoney(getPayMoney());
		sc.setRemark(getRemark());
		return sc;
	}
	public void setInfo(SingleCarparkStoreChargeHistory sc){
		setCouponNum(sc.getCouponNum());
		setCreateTime(sc.getCreateTime());
		setFreeHours(sc.getFreeHours());
		setFreeMoney(sc.getFreeMoney());
		setId(sc.getId());
		setStoryId(sc.getStoryId());
		setStoreName(sc.getStoreName());
		setLoginName(sc.getLoginName());
		setOperaName(sc.getOperaName());
		setPayType(sc.getPayType());
		setPayMoney(sc.getPayMoney());
		setRemark(sc.getRemark());
	}
}
