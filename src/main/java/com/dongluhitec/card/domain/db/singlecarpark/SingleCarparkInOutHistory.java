package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
@NamedQueries({
	@NamedQuery(name = "SingleCarparkInOutHistory.findModifyPlateNO", query = "select c from SingleCarparkInOutHistory c"
			+ "  where (c.inPlateNO!=c.plateNo or c.plateNo!=c.outPlateNO) and c.outTime is not null"),
	@NamedQuery(name="SingleCarparkInOutHistory.deleteWithNotOutByDate",query="delete from SingleCarparkInOutHistory i where i.inTime<? and i.outTime is null")
	})
public class SingleCarparkInOutHistory extends DomainObject{
	
	public enum Property{
		plateNo,userName,carType,inTime,outTime,inDevice,outDevice,operaName,returnAccount,shouldMoney,factMoney,freeMoney
		,freeReturnAccount,carparkId,inPlateNO,outPlateNO
	}
	public enum Label{
		inTimeLabel,outTimeLabel
	}
	public enum Query{
		deleteWithNotOutByDate;
		public String query() {
			
			return SingleCarparkInOutHistory.class.getSimpleName()+"."+name();
		}
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6080299047343306789L;
	
	@Column(length=20)
	private String plateNo;
	@Column(length=20)
	private String userName;
	
	private Long userId;
	@Column(length=20)
	private String carType;
	@Temporal(TemporalType.TIMESTAMP)
	private Date inTime;
	@Temporal(TemporalType.TIMESTAMP)
	private Date outTime;
	@Column(length=40)
	private String inDevice;
	@Column(length=40)
	private String outDevice;
	
	private Float shouldMoney;
	private Float factMoney;
	private Float freeMoney;
	@Column(length=20)
	private String operaName;
	
	private Long returnAccount;
	private Long freeReturnAccount;
	
	private String smallImg;
	private String bigImg;
	private String outSmallImg;
	private String outBigImg;
	private String inPlateNO;
	private String outPlateNO;
	@Column(length=20)
	private String inPhotographType;
	@Column(length=20)
	private String outPhotographType;
	
	private Long carparkId;
	private String carparkName;
	
	private Date chargeTime;
	
	private Date reviseInTime;

	public String getPlateNo() {
		return plateNo;
	}
	public String getInTimeLabel(){
		return StrUtil.formatDate(inTime, "yyyy-MM-dd HH:mm:ss");
	}
	public String getOutTimeLabel(){
		return StrUtil.formatDate(outTime, "yyyy-MM-dd HH:mm:ss");
	}
	
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		if (pcs != null)
			pcs.firePropertyChange("plateNo", null, null);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
		if (pcs != null)
			pcs.firePropertyChange("carType", null, null);
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}

	public Date getOutTime() {
		return outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = outTime;
		if (pcs != null)
			pcs.firePropertyChange("outTime", null, null);
	}


	public String getOperaName() {
		return operaName;
	}

	public void setOperaName(String operaName) {
		this.operaName = operaName;
		if (pcs != null)
			pcs.firePropertyChange("operaName", null, null);
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSmallImg() {
		return smallImg;
	}

	public void setSmallImg(String smallImg) {
		this.smallImg = smallImg;
		if (pcs != null)
			pcs.firePropertyChange("smallImg", null, null);
	}

	public String getBigImg() {
		return bigImg;
	}

	public void setBigImg(String bigImg) {
		this.bigImg = bigImg;
		if (pcs != null)
			pcs.firePropertyChange("bigImg", null, null);
	}


	public String getInDevice() {
		return inDevice;
	}

	public void setInDevice(String inDevice) {
		this.inDevice = inDevice;
		if (pcs != null)
			pcs.firePropertyChange("inDevice", null, null);
	}

	public String getOutDevice() {
		return outDevice;
	}

	public void setOutDevice(String outDevice) {
		this.outDevice = outDevice;
		if (pcs != null)
			pcs.firePropertyChange("outDevice", null, null);
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
		if (pcs != null)
			pcs.firePropertyChange("userId", null, null);
	}

	public Float getShouldMoney() {
		return shouldMoney;
	}

	public void setShouldMoney(float shouldMoney) {
		this.shouldMoney = shouldMoney;
		if (pcs != null)
			pcs.firePropertyChange("shouldMoney", null, null);
	}

	public Float getFactMoney() {
		return factMoney;
	}

	public void setFactMoney(float factMoney) {
		this.factMoney = factMoney;
		if (pcs != null)
			pcs.firePropertyChange("factMoney", null, null);
	}

	public Float getFreeMoney() {
		return freeMoney;
	}

	public void setFreeMoney(float freeMoney) {
		this.freeMoney = freeMoney;
		if (pcs != null)
			pcs.firePropertyChange("freeMoney", null, null);
	}

	public Long getReturnAccount() {
		return returnAccount;
	}

	public void setReturnAccount(Long returnAccount) {
		this.returnAccount = returnAccount;
		if (pcs != null)
			pcs.firePropertyChange("returnAccount", null, null);
	}

	public String getInPhotographType() {
		return inPhotographType;
	}

	public void setInPhotographType(String inPhotographType) {
		this.inPhotographType = inPhotographType;
		if (pcs != null)
			pcs.firePropertyChange("inPhotographType", null, null);
	}

	public String getOutPhotographType() {
		return outPhotographType;
	}

	public void setOutPhotographType(String outPhotographType) {
		this.outPhotographType = outPhotographType;
		if (pcs != null)
			pcs.firePropertyChange("outPhotographType", null, null);
	}
	public String getOutSmallImg() {
		return outSmallImg;
	}
	public void setOutSmallImg(String outSmallImg) {
		this.outSmallImg = outSmallImg;
		if (pcs != null)
			pcs.firePropertyChange("outSmallImg", null, null);
	}
	public String getOutBigImg() {
		return outBigImg;
	}
	public void setOutBigImg(String outBigImg) {
		this.outBigImg = outBigImg;
		if (pcs != null)
			pcs.firePropertyChange("outBigImg", null, null);
	}
	public String getInPlateNO() {
		return inPlateNO;
	}
	public void setInPlateNO(String inPlateNO) {
		this.inPlateNO = inPlateNO;
		if (pcs != null)
			pcs.firePropertyChange("inPlateNO", null, null);
	}
	public String getOutPlateNO() {
		return outPlateNO;
	}
	public void setOutPlateNO(String outPlateNO) {
		this.outPlateNO = outPlateNO;
		if (pcs != null)
			pcs.firePropertyChange("outPlateNO", null, null);
	}
	public Long getFreeReturnAccount() {
		return freeReturnAccount;
	}
	public void setFreeReturnAccount(Long freeReturnAccount) {
		this.freeReturnAccount = freeReturnAccount;
		if (pcs != null)
			pcs.firePropertyChange("freeReturnAccount", null, null);
	}
	public Long getCarparkId() {
		return carparkId;
	}
	public void setCarparkId(Long carparkId) {
		this.carparkId = carparkId;
		if (pcs != null)
			pcs.firePropertyChange("carparkId", null, null);
	}
	public String getCarparkName() {
		return carparkName;
	}
	public void setCarparkName(String carparkName) {
		this.carparkName = carparkName;
		if (pcs != null)
			pcs.firePropertyChange("carparkName", null, null);
	}
	public Date getReviseInTime() {
		return reviseInTime;
	}
	public void setReviseInTime(Date reviseInTime) {
		this.reviseInTime = reviseInTime;
		if (pcs != null)
			pcs.firePropertyChange("reviseInTime", null, null);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj!=null){
			if (!obj.getClass().equals(this.getClass()) ) {
				return false;
			}
			SingleCarparkInOutHistory h=(SingleCarparkInOutHistory) obj;
			if (h.getId()==null) {
				return false;
			}
			if (h.getId().equals(this.getId())||(h.inTime.equals(this.inTime))) {
				return true;
			}else{
				return false;
			}
		}
		return super.equals(obj);
	}
	public void setShouldMoney(Float shouldMoney) {
		this.shouldMoney = shouldMoney;
		if (pcs != null)
			pcs.firePropertyChange("shouldMoney", null, null);
	}
	public void setFactMoney(Float factMoney) {
		this.factMoney = factMoney;
		if (pcs != null)
			pcs.firePropertyChange("factMoney", null, null);
	}
	public void setFreeMoney(Float freeMoney) {
		this.freeMoney = freeMoney;
		if (pcs != null)
			pcs.firePropertyChange("freeMoney", null, null);
	}
	public Date getChargeTime() {
		return chargeTime;
	}
	public void setChargeTime(Date chargeTime) {
		this.chargeTime = chargeTime;
		if (pcs != null)
			pcs.firePropertyChange("chargeTime", null, null);
	}
	@Override
	public int hashCode() {
		return id==null?0:id.intValue();
	}
	
}
