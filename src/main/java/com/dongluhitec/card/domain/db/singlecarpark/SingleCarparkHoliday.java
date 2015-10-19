package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class SingleCarparkHoliday extends DomainObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 66741631352523916L;
	@Temporal(TemporalType.DATE)
	private Date holidayDate;
	
	public Date getHolidayDate() {
		return holidayDate;
	}
	public void setHolidayDate(Date holidayDate) {
		this.holidayDate = holidayDate;
		if (pcs != null)
			pcs.firePropertyChange("holidayDate", null, null);
	}
	
	
}
