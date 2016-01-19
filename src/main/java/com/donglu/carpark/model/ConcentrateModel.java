package com.donglu.carpark.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;

public class ConcentrateModel extends DomainObject {
	private String userName;
	private String workTime;
	private Float totalFact;
	private Float totalFree;
	
	private String plateNO;
	private String inTime;
	private String stillTime;
	private Float shouldMoney;
	private Float factMoney;
	private Float paidMoney;
	
	private SingleCarparkCarpark carpark;
	private List<SingleCarparkCarpark> listCarpark=new ArrayList<>();
	
	private CarTypeEnum carType;
	private List<CarTypeEnum> listCarType=new ArrayList<>();
	private SingleCarparkInOutHistory in;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}
	public String getWorkTime() {
		return workTime;
	}
	public void setWorkTime(Date workTime) {
		this.workTime = StrUtil.formatDate(workTime, StrUtil.DATETIME_PATTERN);
		if (pcs != null)
			pcs.firePropertyChange("workTime", null, null);
	}
	public Float getTotalFact() {
		return totalFact;
	}
	public void setTotalFact(Float totalFact) {
		this.totalFact = totalFact;
		if (pcs != null)
			pcs.firePropertyChange("totalFact", null, null);
	}
	public Float getTotalFree() {
		return totalFree;
	}
	public void setTotalFree(Float totalFree) {
		this.totalFree = totalFree;
		if (pcs != null)
			pcs.firePropertyChange("totalFree", null, null);
	}
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		if (pcs != null)
			pcs.firePropertyChange("plateNO", null, null);
	}
	public String getInTime() {
		return inTime;
	}
	public void setInTime(Date inTime) {
		this.inTime = StrUtil.formatDate(inTime, StrUtil.DATETIME_PATTERN);
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}
	public String getStillTime() {
		return stillTime;
	}
	public void setStillTime(String stillTime) {
		this.stillTime = stillTime;
		if (pcs != null)
			pcs.firePropertyChange("stillTime", null, null);
	}
	public Float getShouldMoney() {
		return shouldMoney;
	}
	public void setShouldMoney(Float shouldMoney) {
		this.shouldMoney = shouldMoney;
		if (pcs != null)
			pcs.firePropertyChange("shouldMoney", null, null);
	}
	public Float getFactMoney() {
		return factMoney;
	}
	public void setFactMoney(Float factMoney) {
		this.factMoney = factMoney;
		if (pcs != null)
			pcs.firePropertyChange("factMoney", null, null);
	}
	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}
	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}
	public List<SingleCarparkCarpark> getListCarpark() {
		return listCarpark;
	}
	public void setListCarpark(List<SingleCarparkCarpark> listCarpark) {
		this.listCarpark = listCarpark;
		if (pcs != null)
			pcs.firePropertyChange("listCarpark", null, null);
	}
	public CarTypeEnum getCarType() {
		return carType;
	}
	public void setCarType(CarTypeEnum carType) {
		this.carType = carType;
		if (pcs != null)
			pcs.firePropertyChange("carType", null, null);
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}
	public Float getPaidMoney() {
		return paidMoney;
	}
	public void setPaidMoney(Float paidMoney) {
		this.paidMoney = paidMoney;
		if (pcs != null)
			pcs.firePropertyChange("paidMoney", null, null);
	}
	public SingleCarparkInOutHistory getIn() {
		return in;
	}
	public void setIn(SingleCarparkInOutHistory in) {
		this.in = in;
		if (pcs != null)
			pcs.firePropertyChange("in", null, null);
	}
	public List<CarTypeEnum> getListCarType() {
		return listCarType;
	}
	public void setListCarType(List<CarTypeEnum> listCarType) {
		this.listCarType = listCarType;
		if (pcs != null)
			pcs.firePropertyChange("listCarType", null, null);
	}
	
}
