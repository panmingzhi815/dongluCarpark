package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
@Table(name="carpark_duration_price")
public class CarparkDurationPrice extends DomainObject{

	private static final long serialVersionUID = 4187723528540201134L;

	@ManyToOne
	@JoinColumn(name="duration_id",referencedColumnName="id")
	private CarparkDurationStandard carparkDurationStandard;
	
	@Column(name="duration_length")
	private int durationLength;
	
	@Column(name="duration_length_price")
	private float durationLengthPrice;
	
	@Column(name="code")
	private String standardCode;

	public CarparkDurationStandard getCarparkDurationStandard() {
		return carparkDurationStandard;
	}

	public void setCarparkDurationStandard(
			CarparkDurationStandard carparkDurationStandard) {
		this.carparkDurationStandard = carparkDurationStandard;
	}

	public int getDurationLength() {
		return durationLength;
	}

	public void setDurationLength(int durationLength) {
		this.durationLength = durationLength;
	}

	public float getDurationLengthPrice() {
		return durationLengthPrice;
	}

	public void setDurationLengthPrice(float durationLengthPrice) {
		this.durationLengthPrice = durationLengthPrice;
	}

	public String getStandardCode() {
		return standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}
}
