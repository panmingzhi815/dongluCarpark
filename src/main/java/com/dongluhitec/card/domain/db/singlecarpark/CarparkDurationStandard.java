package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
@Table(name="carpark_duration_standard")
public class CarparkDurationStandard extends DomainObject{
	
	public static enum Property{
		carparkChargeStandard,unitDuration,unitPrice,startTime,endTime,carparkDurationPriceList,crossDayUnitDuration,crossDayPrice
	}
	
	private static final long serialVersionUID = -4190588429081282350L;

	@ManyToOne
	@JoinColumn(name="standard_id",referencedColumnName="id")
	private CarparkChargeStandard carparkChargeStandard;
	
	@Column(name="unit_duration")
	private Integer unitDuration;
	
	@Column(name="unit_price")
	private Float unitPrice;
	
	@Column(name="Cross_unit_duration")
	private Integer crossDayUnitDuration;
	
	@Column(name="Cross_unit_price")
	private Float crossDayPrice;
	
	@Column(name="start_time")
	private Date startTime;
	
	@Column(name="end_time")
	private Date endTime;
	
	@Column(name="code")
	private String standardCode;
	
	@OneToMany(mappedBy="carparkDurationStandard",cascade=CascadeType.ALL)
	private List<CarparkDurationPrice> carparkDurationPriceList = new ArrayList<CarparkDurationPrice>(); 

	
	public CarparkChargeStandard getCarparkChargeStandard() {
		return carparkChargeStandard;
	}

	public void setCarparkChargeStandard(CarparkChargeStandard carparkChargeStandard) {
		this.carparkChargeStandard = carparkChargeStandard;
	}

	public Integer getUnitDuration() {
		return unitDuration;
	}

	public void setUnitDuration(Integer unitDuration) {
		this.unitDuration = unitDuration;
	}

	public Float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getCrossDayUnitDuration() {
		return crossDayUnitDuration;
	}

	public void setCrossDayUnitDuration(Integer crossDayUnitDuration) {
		this.crossDayUnitDuration = crossDayUnitDuration;
	}

	public Float getCrossDayPrice() {
		return crossDayPrice;
	}

	public void setCrossDayPrice(Float crossDayPrice) {
		this.crossDayPrice = crossDayPrice;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStandardCode() {
		return standardCode;
	}

	public void setStandardCode(String standardCode) {
		this.standardCode = standardCode;
	}

	public List<CarparkDurationPrice> getCarparkDurationPriceList() {
		return carparkDurationPriceList;
	}

	public void setCarparkDurationPriceList(
			List<CarparkDurationPrice> carparkDurationPriceList) {
		for (CarparkDurationPrice carparkDurationPrice : carparkDurationPriceList) {
			carparkDurationPrice.setCarparkDurationStandard(this);
		}
		this.carparkDurationPriceList = carparkDurationPriceList;
	}
	
	public void addCarparkDurationPriceList(CarparkDurationPrice carparkDurationPrice) {
		carparkDurationPrice.setCarparkDurationStandard(this);
		this.carparkDurationPriceList.add(carparkDurationPrice);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o instanceof CarparkDurationStandard){
            CarparkDurationStandard sds = (CarparkDurationStandard)o;
            if(sds.getId() == null) return false;
            return sds.getId().intValue() == getId().intValue();
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getId() == null ? 0 : getId().intValue();
    }
}
