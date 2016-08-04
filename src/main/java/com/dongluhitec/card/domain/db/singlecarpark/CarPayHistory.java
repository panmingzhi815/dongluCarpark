package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
/**
 * 保存车辆缴费记录
 * @author Administrator
 *
 */
@Entity
public class CarPayHistory extends DomainObject{
	
	public enum Property{
		plateNO,payTime,createDate,payedMoney,remark,operaName
	}
	public enum Label{
		plateNO,payTimeLabel,createDateLabel,payedMoney,remark,operaName
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4222967774413025501L;
	
	private String plateNO;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date payTime;
	private float payedMoney=0;
	private String operaName;
	private String remark;
	
	public String getPayTimeLabel(){
		return StrUtil.formatDateTime(payTime);
	}
	public String getCreateDateLabel(){
		return StrUtil.formatDateTime(createDate);
	}
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		firePropertyChange("plateNO", null, null);
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
		firePropertyChange("createDate", null, null);
	}
	public Date getPayTime() {
		return payTime;
	}
	public void setPayTime(Date payTime) {
		this.payTime = payTime;
		firePropertyChange("payTime", null, null);
	}
	public float getPayedMoney() {
		return payedMoney;
	}
	public void setPayedMoney(float payedMoney) {
		this.payedMoney = payedMoney;
		firePropertyChange("payedMoney", null, null);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
		firePropertyChange("remark", null, null);
	}
	public String getOperaName() {
		return operaName;
	}
	public void setOperaName(String operaName) {
		this.operaName = operaName;
		firePropertyChange("operaName", null, null);
	}
}
