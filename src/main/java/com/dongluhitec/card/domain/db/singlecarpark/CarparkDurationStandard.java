package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.joda.time.DateTime;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

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
	
	
	private Integer startStepTime;
	private Float startStepPrice;
	@Column(name="unit_duration")
	private Integer unitDuration;
	
	private Float maxPrice;
	
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
	@Transient
	private Object tempObject;
	
	public String getStartTimeLabel(){
		return StrUtil.formatDate(startTime, "HH:mm");
	}
	public String getEndTimeLabel(){
		return StrUtil.formatDate(endTime, "HH:mm");
	}
	
	public CarparkChargeStandard getCarparkChargeStandard() {
		return carparkChargeStandard;
	}

	public void setCarparkChargeStandard(CarparkChargeStandard carparkChargeStandard) {
		this.carparkChargeStandard = carparkChargeStandard;
	}

	public Integer getUnitDuration() {
		if (unitDuration==null) {
			return 0;
		}
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

	public Integer getStartStepTime() {
		return startStepTime;
	}

	public void setStartStepTime(Integer startStepTime) {
		this.startStepTime = startStepTime;
		if (pcs != null)
			pcs.firePropertyChange("startStepTime", null, null);
	}

	public Float getStartStepPrice() {
		return startStepPrice;
	}

	public void setStartStepPrice(Float startStepPrice) {
		this.startStepPrice = startStepPrice;
		if (pcs != null)
			pcs.firePropertyChange("startStepPrice", null, null);
	}

	public Float getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Float maxPrice) {
		this.maxPrice = maxPrice;
		if (pcs != null)
			pcs.firePropertyChange("maxPrice", null, null);
	}

	public int getCarparkDurationHoursSize() {
		int countTime = CarparkUtils.countTime(startTime, endTime, TimeUnit.HOURS);
		DateTime dateTime = new DateTime(endTime);
		if (dateTime.getHourOfDay()<new DateTime(startTime).getHourOfDay()) {
			Date date = dateTime.plusDays(1).toDate();
			countTime = CarparkUtils.countTime(startTime, date, TimeUnit.HOURS);
		}
		if (countTime==0) {
			countTime=24;
		}else if (countTime<0) {
			countTime=countTime*-1;
		}
		return countTime;
	}
	public String getStartEndLabel(){
		try {
			String startEndLabel = StrUtil.formatDate(getStartTime(), "HH:mm:ss") + "-" + StrUtil.formatDate(getEndTime(), "HH:mm:ss");
			return startEndLabel;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getTempObject() {
		return tempObject;
	}

	public void setTempObject(Object tempObject) {
		this.tempObject = tempObject;
		firePropertyChange("tempObject", null, null);
	}
}
