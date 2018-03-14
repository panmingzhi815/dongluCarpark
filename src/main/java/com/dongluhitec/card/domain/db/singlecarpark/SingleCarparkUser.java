package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SingleCarparkUser extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1116467109412674474L;
	public enum Property{
		id,plateNo,name,type,carType,address,carparkNo,leftMoney,createDate,validTo,remark
		,telephone,parkingSpace,carpark,
		carparkSlot,carparkSlotType,monthChargeId,monthChargeName
	}
	public enum Label{
	valitoLabel
	}
	public enum UserType{
		普通,免费,储值
	}
	public enum CarparkSlotTypeEnum{
		固定车位,非固定车位
	}
	
	private String name;
	private String plateNo;
	private String type;
	private String address;
	private String carparkNo;
	private Integer carparkSlot;
	private CarparkSlotTypeEnum carparkSlotType=CarparkSlotTypeEnum.非固定车位;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;
	private Integer remindDays=0;
	private Integer delayDays=0;
	private Long monthChargeId;//月租编号
	private String monthChargeCode;
	private String monthChargeName;
	
	private CarTypeEnum carType=CarTypeEnum.SmallCar;
	
	private Float leftMoney=0F;
	
	private String remark;
	private String tempCarTime;
	@Column(length=20)
	private String telephone;
	@Column(length=200)
	private String parkingSpace;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastEditDate;
	
	
	@ManyToOne
	private SingleCarparkCarpark carpark;
	
	@Transient
	private boolean createHistory=true;
	
	@PrePersist
	public void initCreateDate(){
		createDate=new Date();
	}
	
	public String getValitoLabel(){
		return StrUtil.formatDate(validTo, "yyyy-MM-dd");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if (pcs != null)
			pcs.firePropertyChange("name", null, null);
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		if(plateNo!=null){
			plateNo=plateNo.trim();
		}
		this.plateNo = plateNo;
		if (pcs != null)
			pcs.firePropertyChange("plateNo", null, null);
	}
	public String getType() {
		if (type==null) {
			return "普通";
		}
		return type;
	}
	public void setType(String type) {
		this.type = type;
		if (pcs != null)
			pcs.firePropertyChange("type", null, null);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
		if (pcs != null)
			pcs.firePropertyChange("address", null, null);
	}
	public String getCarparkNo() {
		if (getCarparkSlot()==null) {
			return "1";
		}
		return getCarparkSlot()+"";
	}

	public void setCarparkNo(String carparkNo) {
		this.carparkNo = carparkNo;
		Integer valueOf = 1;
		try {
			valueOf = Integer.valueOf(carparkNo);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		setCarparkSlot(valueOf);
		if (pcs != null)
			pcs.firePropertyChange("carparkNo", null, null);
	}
	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}
	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
		if (pcs != null)
			pcs.firePropertyChange("createDate", null, null);
	}
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
		if (pcs != null)
			pcs.firePropertyChange("validTo", null, null);
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
		if (pcs != null)
			pcs.firePropertyChange("remark", null, null);
	}
	public Integer getRemindDays() {
		return remindDays==null?0:remindDays;
	}
	public void setRemindDays(Integer remindDays) {
		this.remindDays = remindDays;
		if (pcs != null)
			pcs.firePropertyChange("remindDays", null, null);
	}
	public Integer getDelayDays() {
		return delayDays==null?0:delayDays;
	}
	public void setDelayDays(Integer delayDays) {
		this.delayDays = delayDays;
		if (pcs != null)
			pcs.firePropertyChange("delayDays", null, null);
	}
	public Long getMonthChargeId() {
		return monthChargeId;
	}
	public void setMonthChargeId(Long monthChargeId) {
		this.monthChargeId = monthChargeId;
		if (pcs != null)
			pcs.firePropertyChange("monthChargeId", null, null);
	}
	public String getTempCarTime() {
		return tempCarTime;
	}
	public void setTempCarTime(String tempCarTime) {
		this.tempCarTime = tempCarTime;
		if (pcs != null)
			pcs.firePropertyChange("tempCarTime", null, null);
	}
	@Override
	public String getLabelString() {
		return plateNo+","+name;
	}
	public Float getLeftMoney() {
		return leftMoney;
	}
	public void setLeftMoney(Float leftMoney) {
		this.leftMoney = leftMoney;
		if (pcs != null)
			pcs.firePropertyChange("leftMoney", null, null);
	}
	public CarTypeEnum getCarType() {
		if (carType==null) {
			return CarTypeEnum.SmallCar;
		}
		return carType;
	}
	public void setCarType(CarTypeEnum carType) {
		this.carType = carType;
		if (pcs != null)
			pcs.firePropertyChange("carType", null, null);
	}
	public Integer getCarparkSlot() {
		if(carparkSlot==null){
			carparkSlot=1;
		}
		return carparkSlot;
	}
	public void setCarparkSlot(Integer carparkSlot) {
		this.carparkSlot = carparkSlot;
		firePropertyChange("carparkSlot", null, null);
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
		firePropertyChange("telephone", null, null);
	}
	public String getParkingSpace() {
		return parkingSpace;
	}
	public void setParkingSpace(String parkingSpace) {
		this.parkingSpace = parkingSpace;
		firePropertyChange("parkingSpace", null, null);
	}
	@Override
	public String toString() {
		return name+"-"+plateNo;
	}
	public CarparkSlotTypeEnum getCarparkSlotType() {
		if (carparkSlotType==null) {
			return CarparkSlotTypeEnum.非固定车位;
		}
		return carparkSlotType;
	}
	public void setCarparkSlotType(CarparkSlotTypeEnum carparkSlotType) {
		this.carparkSlotType = carparkSlotType;
		firePropertyChange("carparkSlotType", null, null);
	}
	public String getMonthChargeName() {
		return monthChargeName;
	}
	public void setMonthChargeName(String monthChargeName) {
		this.monthChargeName = monthChargeName;
		firePropertyChange("monthChargeName", null, null);
	}
	public String getMonthChargeCode() {
		return monthChargeCode;
	}
	public void setMonthChargeCode(String monthChargeCode) {
		this.monthChargeCode = monthChargeCode;
		firePropertyChange("monthChargeCode", null, null);
	}
	public Date getLastEditDate() {
		if(lastEditDate==null){
			return createDate;
		}
		return lastEditDate;
	}
	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
		firePropertyChange("lastEditDate", null, null);
	}
	public boolean isCreateHistory() {
		return createHistory;
	}
	public void setCreateHistory(boolean createHistory) {
		this.createHistory = createHistory;
	}
	
}
