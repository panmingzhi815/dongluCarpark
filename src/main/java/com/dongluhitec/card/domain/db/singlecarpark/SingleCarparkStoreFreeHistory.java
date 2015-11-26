package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkStoreFreeHistory extends DomainObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7523941635458715254L;
	private String storeName;
	private Long storeId;
	private String freePlateNo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	private Float freeHour;
	private Float freeMoney;
	private String coupon;
	private String used;
	
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
		if (pcs != null)
			pcs.firePropertyChange("storeName", null, null);
	}
	public Long getStoreId() {
		return storeId;
	}
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
		if (pcs != null)
			pcs.firePropertyChange("storeId", null, null);
	}
	public String getFreePlateNo() {
		return freePlateNo;
	}
	public void setFreePlateNo(String freePlateNo) {
		this.freePlateNo = freePlateNo;
		if (pcs != null)
			pcs.firePropertyChange("freePlateNo", null, null);
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		if (pcs != null)
			pcs.firePropertyChange("createTime", null, null);
	}
	public Float getFreeHour() {
		return freeHour;
	}
	public void setFreeHour(Float freeHour) {
		this.freeHour = freeHour;
		if (pcs != null)
			pcs.firePropertyChange("freeHour", null, null);
	}
	public Float getFreeMoney() {
		return freeMoney;
	}
	public void setFreeMoney(Float freeMoney) {
		this.freeMoney = freeMoney;
		if (pcs != null)
			pcs.firePropertyChange("freeMoney", null, null);
	}
	public String getCoupon() {
		return coupon;
	}
	public void setCoupon(String coupon) {
		this.coupon = coupon;
		if (pcs != null)
			pcs.firePropertyChange("coupon", null, null);
	}
	public String getUsed() {
		return used;
	}
	public void setUsed(String used) {
		this.used = used;
		if (pcs != null)
			pcs.firePropertyChange("used", null, null);
	}
}
