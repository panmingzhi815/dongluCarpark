package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkSystemSetting extends DomainObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2546271381567702939L;
	@Column(unique=true)
	private String settingKey;
	@Column(length=999)
	private String settingValue;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate=new Date();
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSettingKey() {
		return settingKey;
	}

	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
		if (pcs != null)
			pcs.firePropertyChange("settingKey", null, null);
	}

	public String getSettingValue() {
		return settingValue;
	}

	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
		if (pcs != null)
			pcs.firePropertyChange("settingValue", null, null);
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
		if (pcs != null)
			pcs.firePropertyChange("lastUpdate", null, null);
	}
	public Boolean getBooleanValue(){
		Boolean b = false;
		try {
			b = Boolean.valueOf(settingValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
}
