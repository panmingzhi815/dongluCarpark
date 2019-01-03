package com.donglu.carpark.model;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4889402514192857133L;
	private boolean success=false;
	private int code=0;
	private String msg="";
	private Object obj;
	private float deptFee=0;
	private float payedFee=0;
	private float couponValue=0;
	private Date outTime;
	private Date inTime;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getObj() {
		return obj;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public float getDeptFee() {
		return deptFee;
	}
	public void setDeptFee(float deptFee) {
		this.deptFee = deptFee;
	}
	public float getPayedFee() {
		return payedFee;
	}
	public void setPayedFee(float payedFee) {
		this.payedFee = payedFee;
	}
	public Date getOutTime() {
		return outTime;
	}
	public void setOutTime(Date outTime) {
		this.outTime = outTime;
		//firePropertyChange("outTime", null, null);
	}
	public float getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(float couponValue) {
		this.couponValue = couponValue;
	}
	public Date getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = inTime;
		//firePropertyChange("inTime", null, null);
	}
	
}
