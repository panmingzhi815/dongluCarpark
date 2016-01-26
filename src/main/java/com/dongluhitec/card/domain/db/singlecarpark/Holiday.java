package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.QueryEnum;
import com.dongluhitec.card.domain.db.DomainObject;
@Entity
public class Holiday extends DomainObject{
	
	public static enum Query implements QueryEnum {
		findAll;

        @Override
		public String query() {
            return Holiday.class.getSimpleName() + "." + this.name();
        }
    }
	/**
	 * 
	 */
	private static final long serialVersionUID = 66741631352523916L;
	@Temporal(TemporalType.DATE)
	private Date start;
	@Column
	private int length;
	
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
		if (pcs != null)
			pcs.firePropertyChange("start", null, null);
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
		if (pcs != null)
			pcs.firePropertyChange("length", null, null);
	}
	
	
	
	
}
