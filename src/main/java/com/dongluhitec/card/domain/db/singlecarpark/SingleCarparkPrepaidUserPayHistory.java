package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
@Entity
public class SingleCarparkPrepaidUserPayHistory extends DomainObject{
	public enum Property{
		userName,plateNO,operaName,createTime,payMoney
	}
	public enum Label{
		createTimeLabel,inTimeLabel,outTimeLabel
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
	//出场时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date outTime;

    //进场
    private Date inTime;
    
    //本次缴费金额
    private Float payMoney;
    
    private String operaName;

	public String getUserName() {
		return userName;
	}
	
	public String getCreateTimeLabel(){
		return StrUtil.formatDate(createTime, "yyyy-MM-dd HH:mm:ss");
	}
	public String getInTimeLabel(){
		return StrUtil.formatDate(inTime, "yyyy-MM-dd HH:mm:ss");
	}
	public String getOutTimeLabel(){
		return StrUtil.formatDate(outTime, "yyyy-MM-dd HH:mm:ss");
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

	public Date getOutTime() {
		return outTime;
	}

	public void setOutTime(Date outTime) {
		this.outTime = outTime;
		if (pcs != null)
			pcs.firePropertyChange("outTime", null, null);
	}

	public Date getInTime() {
		return inTime;
	}

	public void setInTime(Date inTime) {
		this.inTime = inTime;
		if (pcs != null)
			pcs.firePropertyChange("inTime", null, null);
	}

	public Float getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Float payMoney) {
		this.payMoney = payMoney;
		if (pcs != null)
			pcs.firePropertyChange("payMoney", null, null);
	}
	
}
