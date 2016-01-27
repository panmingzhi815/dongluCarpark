package com.donglu.carpark.ui.view.carpark;

import com.dongluhitec.card.domain.db.DomainObject;

public class CarparkChargeInfo extends DomainObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -366901442262079621L;
	public enum Property{
		code,name,type,carType,useType,holidayType
	}
	private String code;
	private String name;
	private String type;
	private String carType;
	private String useType;
	private String holidayType;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
		if (pcs != null)
			pcs.firePropertyChange("code", null, null);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if (pcs != null)
			pcs.firePropertyChange("name", null, null);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		if (pcs != null)
			pcs.firePropertyChange("type", null, null);
	}
	public String getCarType() {
		return carType;
	}
	public void setCarType(String carType) {
		this.carType = carType;
		if (pcs != null)
			pcs.firePropertyChange("carType", null, null);
	}
	public String getUseType() {
		return useType;
	}
	public void setUseType(String useType) {
		this.useType = useType;
		if (pcs != null)
			pcs.firePropertyChange("useType", null, null);
	}
	public String getHolidayType() {
		return holidayType;
	}
	public void setHolidayType(String holidayType) {
		this.holidayType = holidayType;
		if (pcs != null)
			pcs.firePropertyChange("holidayType", null, null);
	}
	
}
