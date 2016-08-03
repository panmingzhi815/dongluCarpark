package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import com.dongluhitec.card.domain.db.DomainObject;
/**
 * 保存车辆缴费记录
 * @author Administrator
 *
 */
public class CarPayHistory extends DomainObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4222967774413025501L;
	
	private String plateNO;
	private Date createDate;
	private Date payTime;
	private float payedMoney=0;
	
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
}
