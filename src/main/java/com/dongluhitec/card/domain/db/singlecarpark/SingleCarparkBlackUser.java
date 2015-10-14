package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkBlackUser extends DomainObject {
	public enum Property{
		plateNO,remark
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 2546271381567702939L;
	@Column(unique=true)
	private String plateNO;
	private String remark;
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPlateNO() {
		return plateNO;
	}

	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
		if (pcs != null)
			pcs.firePropertyChange("plateNO", null, null);
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
		if (pcs != null)
			pcs.firePropertyChange("remark", null, null);
	}
}
