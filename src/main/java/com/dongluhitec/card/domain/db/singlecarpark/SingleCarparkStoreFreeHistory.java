package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SingleCarparkStoreFreeHistory extends DomainObject{
	public enum Property{
		storeName,storeId,freePlateNo,createTime,freeHour,freeMoney,coupon,used, createTimeLabel
	}
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
	private Boolean isAllFree;
	@Transient
	private String freeType;
	
	public String getCreateTimeLabel(){
		return StrUtil.formatDate(createTime, StrUtil.DATETIME_PATTERN);
	}
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
	public Boolean getIsAllFree() {
		return isAllFree;
	}
	public void setIsAllFree(Boolean isAllFree) {
		this.isAllFree = isAllFree;
		if (pcs != null)
			pcs.firePropertyChange("isAllFree", null, null);
	}
	public String getFreeType() {
		if (isAllFree==null||!isAllFree) {
			return "优惠";
		}else{
			return "全免";
		}
	}
	public void setFreeType(String freeType) {
		this.freeType = freeType;
		if (pcs != null)
			pcs.firePropertyChange("freeType", null, null);
	}
}
