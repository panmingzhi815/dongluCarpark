package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
@Entity
public class SingleCarparkMonthlyUserPayHistory extends DomainObject{
	public enum Property{
		userName,userType,plateNO,chargesMoney,operaName,overdueTime,createTime,parkingSpace,monthChargeName
	}
	public enum Label{
		createTimeLabel,overdueTimeLabel,startTimeLabel
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -2691074438161066722L;
	
	private String userName;
	private String userIdCard;
	private String userType;
	private String plateNO;
	private Date createTime;
	private String rentType;
	private String carType;
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
    
    private Long monthChargeId;//月租编号
	private String monthChargeCode;
	private String monthChargeName;
	private String parkingSpace;
	private String remark;
	

	public String getUserName() {
		return userName;
	}
	
	public String getCreateTimeLabel(){
		return StrUtil.formatDate(createTime, "yyyy-MM-dd HH:mm:ss");
	}
	public String getOverdueTimeLabel(){
		return StrUtil.formatDate(overdueTime, "yyyy-MM-dd HH:mm:ss");
	}
	public String getStartTimeLabel(){
		if (oldOverDueTime==null) {
			return getCreateTimeLabel();
		}
		return StrUtil.formatDate(oldOverDueTime, "yyyy-MM-dd HH:mm:ss");
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
		if (pcs != null)
			pcs.firePropertyChange("userType", null, null);
	}

	public Long getMonthChargeId() {
		return monthChargeId;
	}

	public void setMonthChargeId(Long monthChargeId) {
		this.monthChargeId = monthChargeId;
		firePropertyChange("monthChargeId", null, null);
	}

	public String getMonthChargeCode() {
		return monthChargeCode;
	}

	public void setMonthChargeCode(String monthChargeCode) {
		this.monthChargeCode = monthChargeCode;
		firePropertyChange("monthChargeCode", null, null);
	}

	public String getMonthChargeName() {
		return monthChargeName;
	}

	public void setMonthChargeName(String monthChargeName) {
		this.monthChargeName = monthChargeName;
		firePropertyChange("monthChargeName", null, null);
	}

	public String getParkingSpace() {
		return parkingSpace;
	}

	public void setParkingSpace(String parkingSpace) {
		this.parkingSpace = parkingSpace;
		firePropertyChange("parkingSpace", null, null);
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
		firePropertyChange("remark", null, null);
	}
	
}
