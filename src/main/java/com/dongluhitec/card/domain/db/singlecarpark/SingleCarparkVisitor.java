package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SingleCarparkVisitor extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = -146910856653100859L;
	public enum Property{
		id,plateNO,name,telephone,allIn,inCount,outCount,validTo,remark,carpark,status,outNeedCharge
	}
	public enum Label{
		validToLabel
	}
	public enum VisitorStatus{
		可用,不可用
	}
	private String plateNO;
	private String name;
	private String telephone;
	private Integer allIn=0;
	private int inCount=0;
	private Integer outCount=0;
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;
	private String remark;
	@ManyToOne
	private SingleCarparkCarpark carpark;
	private String status;
	
	private Boolean outNeedCharge=false;
	public String getValidToLabel(){
		if (validTo==null) {
			return "";
		}
		return StrUtil.formatDate(validTo,"yyyy-MM-dd HH:mm");
	}
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		firePropertyChange("plateNO", null, null);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		firePropertyChange("name", null, null);
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
		firePropertyChange("telephone", null, null);
	}
	public Integer getAllIn() {
		return allIn;
	}
	public void setAllIn(Integer allIn) {
		this.allIn = allIn;
		firePropertyChange("allIn", null, null);
	}
	public int getInCount() {
		return inCount;
	}
	public void setInCount(int inCount) {
		this.inCount = inCount;
		firePropertyChange("inCount", null, null);
	}
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
		firePropertyChange("validTo", null, null);
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
		firePropertyChange("remark", null, null);
	}
	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}
	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		firePropertyChange("carpark", null, null);
	}
	public void setStatus(String status) {
		this.status=status;
	}
	public String getStatus() {
		if (status==null) {
			return VisitorStatus.可用.name();
		}
		return status;
	}
	public int getOutCount() {
		if (outCount==null) {
			return 0;
		}
		return outCount;
	}
	public void setOutCount(int outCount) {
		this.outCount = outCount;
		firePropertyChange("outCount", null, null);
	}
	public Boolean getOutNeedCharge() {
		if(outNeedCharge==null){
			return false;
		}
		return outNeedCharge;
	}
	public void setOutNeedCharge(Boolean outNeedCharge) {
		this.outNeedCharge = outNeedCharge;
		firePropertyChange("outNeedCharge", null, null);
	}
	
}
