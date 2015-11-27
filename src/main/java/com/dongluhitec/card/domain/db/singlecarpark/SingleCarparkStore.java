package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class SingleCarparkStore extends DomainObject {
	
	public enum Property{
		storeName,address,userName,leftFreeHour,leftFreeMoney,createTime
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4875162570044797127L;
	
	@Column(unique=true)
	private String storeName;
	private String address;
	private String userName;
	private String loginName;
	private String loginPawword;
	private Float leftFreeHour;
	private Float leftFreeMoney;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
		if (pcs != null)
			pcs.firePropertyChange("storeName", null, null);
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
		if (pcs != null)
			pcs.firePropertyChange("loginName", null, null);
	}
	public String getLoginPawword() {
		return loginPawword;
	}
	public void setLoginPawword(String loginPawword) {
		this.loginPawword = loginPawword;
		if (pcs != null)
			pcs.firePropertyChange("loginPawword", null, null);
	}
	public Float getLeftFreeHour() {
		return leftFreeHour;
	}
	public void setLeftFreeHour(Float leftFreeHour) {
		this.leftFreeHour = leftFreeHour;
		if (pcs != null)
			pcs.firePropertyChange("leftFreeHour", null, null);
	}
	public Float getLeftFreeMoney() {
		return leftFreeMoney;
	}
	public void setLeftFreeMoney(Float leftFreeMoney) {
		this.leftFreeMoney = leftFreeMoney;
		if (pcs != null)
			pcs.firePropertyChange("leftFreeMoney", null, null);
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		if (pcs != null)
			pcs.firePropertyChange("createTime", null, null);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
		if (pcs != null)
			pcs.firePropertyChange("address", null, null);
	}
}
