package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class SingleCarparkMonthlyUserPayHistory extends DomainObject{
	public enum Property{
		userName,plateNO,chargesMoney,operaName,overdueTime,createTime
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2691074438161066722L;
	
	String userName;
	String userIdCard;
	String plateNO;
	Date createTime;
	String rentType;
	String carType;
	int monthamount;
	float monthCharge;
	//过期时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date overdueTime;

    //缴费前的过期时间
    private Date oldOverDueTime;
    
    //本次缴费金额
    private Float chargesMoney;
    
    private String operaName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}

	public String getUserIdCard() {
		return userIdCard;
	}

	public void setUserIdCard(String userIdCard) {
		this.userIdCard = userIdCard;
		if (pcs != null)
			pcs.firePropertyChange("userIdCard", null, null);
	}

	public String getPlateNO() {
		return plateNO;
	}

	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		if (pcs != null)
			pcs.firePropertyChange("plateNO", null, null);
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		if (pcs != null)
			pcs.firePropertyChange("createTime", null, null);
	}

	public String getRentType() {
		return rentType;
	}

	public void setRentType(String rentType) {
		this.rentType = rentType;
		if (pcs != null)
			pcs.firePropertyChange("rentType", null, null);
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
		if (pcs != null)
			pcs.firePropertyChange("carType", null, null);
	}

	public int getMonthamount() {
		return monthamount;
	}

	public void setMonthamount(int monthamount) {
		this.monthamount = monthamount;
		if (pcs != null)
			pcs.firePropertyChange("monthamount", null, null);
	}

	public float getMonthCharge() {
		return monthCharge;
	}

	public void setMonthCharge(float monthCharge) {
		this.monthCharge = monthCharge;
		if (pcs != null)
			pcs.firePropertyChange("monthCharge", null, null);
	}

	public Date getOverdueTime() {
		return overdueTime;
	}

	public void setOverdueTime(Date overdueTime) {
		this.overdueTime = overdueTime;
		if (pcs != null)
			pcs.firePropertyChange("overdueTime", null, null);
	}

	public Date getOldOverDueTime() {
		return oldOverDueTime;
	}

	public void setOldOverDueTime(Date oldOverDueTime) {
		this.oldOverDueTime = oldOverDueTime;
		if (pcs != null)
			pcs.firePropertyChange("oldOverDueTime", null, null);
	}

	public Float getChargesMoney() {
		return chargesMoney;
	}

	public void setChargesMoney(Float chargesMoney) {
		this.chargesMoney = chargesMoney;
		if (pcs != null)
			pcs.firePropertyChange("chargesMoney", null, null);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getOperaName() {
		return operaName;
	}

	public void setOperaName(String operaName) {
		this.operaName = operaName;
		if (pcs != null)
			pcs.firePropertyChange("operaName", null, null);
	}
	
}
