package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkStoreChargeHistory extends DomainObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2651979792443728013L;
	
	private Long storyId;
	private String storyName;
	private Float payMoney;
	private Float freeMoney;
	private Float freeHours;
	private String operaName;
	private String payType;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;


	public Long getStoryId() {
		return storyId;
	}


	public void setStoryId(Long storyId) {
		this.storyId = storyId;
		if (pcs != null)
			pcs.firePropertyChange("storyId", null, null);
	}


	public String getStoryName() {
		return storyName;
	}


	public void setStoryName(String storyName) {
		this.storyName = storyName;
		if (pcs != null)
			pcs.firePropertyChange("storyName", null, null);
	}


	public Float getPayMoney() {
		return payMoney;
	}


	public void setPayMoney(Float payMoney) {
		this.payMoney = payMoney;
		if (pcs != null)
			pcs.firePropertyChange("payMoney", null, null);
	}


	public Float getFreeMoney() {
		return freeMoney;
	}


	public void setFreeMoney(Float freeMoney) {
		this.freeMoney = freeMoney;
		if (pcs != null)
			pcs.firePropertyChange("freeMoney", null, null);
	}


	public Float getFreeHours() {
		return freeHours;
	}


	public void setFreeHours(Float freeHours) {
		this.freeHours = freeHours;
		if (pcs != null)
			pcs.firePropertyChange("freeHours", null, null);
	}


	public String getOperaName() {
		return operaName;
	}


	public void setOperaName(String operaName) {
		this.operaName = operaName;
		if (pcs != null)
			pcs.firePropertyChange("operaName", null, null);
	}


	public Date getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		if (pcs != null)
			pcs.firePropertyChange("createTime", null, null);
	}


	public String getPayType() {
		return payType;
	}


	public void setPayType(String payType) {
		this.payType = payType;
		if (pcs != null)
			pcs.firePropertyChange("payType", null, null);
	}
	
}
