package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;
@Entity
public class SingleCarparkLockCar extends DomainObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6134599053843482723L;
	public enum Property{
		plateNO,status,operaName,createTime,createTimeLabel
	}
	public enum Status{
		已锁定,已解锁
	}
	@Column(length=8)
	private String plateNO;
	private String status;
	private Date createTime;
	private String operaName;
	
	public String getCreateTimeLabel(){
		return StrUtil.formatDate(createTime, StrUtil.DATETIME_PATTERN);
	}
	
	public String getPlateNO() {
		return plateNO;
	}
	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		if (pcs != null)
			pcs.firePropertyChange("plateNO", null, null);
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
		if (pcs != null)
			pcs.firePropertyChange("status", null, null);
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		if (pcs != null)
			pcs.firePropertyChange("createTime", null, null);
	}

	public String getOperaName() {
		return operaName;
	}

	public void setOperaName(String operaName) {
		this.operaName = operaName;
		if (pcs != null)
			pcs.firePropertyChange("operaName", null, null);
	}
}
