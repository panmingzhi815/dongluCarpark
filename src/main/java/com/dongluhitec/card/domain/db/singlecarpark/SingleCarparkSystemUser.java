package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
@Entity
public class SingleCarparkSystemUser extends DomainObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3053445464781295304L;
	@Column(unique=true)
	private String userName;
	private String password;
	private String type;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastEditDate;
	private String lastEditUser; 
	private String remark;
	
	
	public void setLast(){
		lastEditDate=new Date();
		lastEditUser=System.getProperty("userName");
	}
	
	public String getCreateDateLabel() {
		return StrUtil.formatDate(getCreateDate(), "yyyy-MM-dd HH:mm:ss");
	}
	
	public String getLastEditDateLabel() {
		return StrUtil.formatDate(getLastEditDate(), "yyyy-MM-dd HH:mm:ss");
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
		if (pcs != null)
			pcs.firePropertyChange("userName", null, null);
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		if (pcs != null)
			pcs.firePropertyChange("password", null, null);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		if (pcs != null)
			pcs.firePropertyChange("type", null, null);
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
		if (pcs != null)
			pcs.firePropertyChange("createDate", null, null);
	}
	public Date getLastEditDate() {
		return lastEditDate;
	}
	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
		if (pcs != null)
			pcs.firePropertyChange("lastEditDate", null, null);
	}
	public String getLastEditUser() {
		return lastEditUser;
	}
	public void setLastEditUser(String lastEditUser) {
		this.lastEditUser = lastEditUser;
		if (pcs != null)
			pcs.firePropertyChange("lastEditUser", null, null);
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
		if (pcs != null)
			pcs.firePropertyChange("remark", null, null);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		
		return this.userName;
	}
	
	
}
