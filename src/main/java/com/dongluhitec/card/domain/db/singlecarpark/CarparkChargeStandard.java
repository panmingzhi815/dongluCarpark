package com.dongluhitec.card.domain.db.singlecarpark;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
@Table(name="carpark_charge_standard")
@NamedQueries({
		@NamedQuery(name = "CarparkChargeStandard.findByCarTypeId",query = "SELECT ccs FROM CarparkChargeStandard ccs left join ccs.carparkCarType ccst WHERE ccst.id = ?1")
})
public class CarparkChargeStandard extends DomainObject{
	
	private static final long serialVersionUID = 6079310052511753350L;

	public static enum Property{
		id,carparkCarType,code,name,carparkDurationTypeEnum,onedayMaxCharge,freeTime,carparkAcrossDayTypeEnum,
		acrossdayChargeEnable,carparkHolidayTypeEnum,carparkDurationStandards, carpark, startStepTime, startStepPrice,using,acrossDayPrice
		,acrossDayIsFree
	}

	public static enum Query{
		findByCarTypeId;
		public String query(){
			return CarparkChargeStandard.class.getSimpleName() + "." + name();
		}
	}
	
	@ManyToOne
	@JoinColumn(name="carpark_id",referencedColumnName="id")
	private SingleCarparkCarpark carpark;
	
	@ManyToOne
	@JoinColumn(name="car_id",referencedColumnName="id")
	private CarparkCarType carparkCarType;
	
	@NotBlank(message="编码不能空")
	@Column(name="code")
	private String code;
	
	@NotBlank(message="名称不能空")
	@Column(name="name")
	private String name;
	
	@Enumerated
	@Column(name="charge_time_type")
	private CarparkDurationTypeEnum carparkDurationTypeEnum;
	
	@Min(value=0,message="最大收费必须大于等于0")
	@Column(name="oneday_max_charge")
	private float onedayMaxCharge;
	
	@Min(value=0,message="免费时间必须大于等于0")
	@Column(name="free_time")
	private int freeTime;
	
	@Enumerated
	@Column(name="acrossday_charge_style")
	private CarparkAcrossDayTypeEnum carparkAcrossDayTypeEnum;
	
	@Column(name="acrossday_charge_enable")
	private int acrossdayChargeEnable; //免费时长收费
	
	//起步时长
	@Min(value=0,message="起步收费时长必须大于等于0")
	@Column(name="First_Time")
	private Integer startStepTime;
	
	//起步金额
	@Column(name="First_Time_Fee")
	@Min(value=0,message="起步收费金额必须大于等于0")
	private Float startStepPrice;
	
	@Enumerated
	@Column(name = "workday_type")
	private CarparkHolidayTypeEnum carparkHolidayTypeEnum;
	@Min(value=0,message="起步收费金额必须大于等于0")
	private Float acrossDayPrice=0F;
	
	private Boolean acrossDayIsFree=false;//跨天继续免费？
	//是否启用
	private Boolean using;
	
	@OneToMany(mappedBy="carparkChargeStandard",cascade=CascadeType.ALL)
	private List<CarparkDurationStandard> carparkDurationStandards = new ArrayList<CarparkDurationStandard>();;

	public CarparkCarType getCarparkCarType() {
		return carparkCarType;
	}

	public void setCarparkCarType(CarparkCarType carparkCarType) {
		firePropertyChange(Property.carparkCarType.name(), this.carparkCarType, this.carparkCarType = carparkCarType);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		firePropertyChange(Property.code.name(), this.code, this.code = code);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		firePropertyChange(Property.name.name(), this.name, this.name = name);
	}

	public CarparkDurationTypeEnum getCarparkDurationTypeEnum() {
		return carparkDurationTypeEnum;
	}

	public void setCarparkDurationTypeEnum(
			CarparkDurationTypeEnum carparkDurationTypeEnum) {
		firePropertyChange(Property.carparkDurationTypeEnum.name(), this.carparkDurationTypeEnum, this.carparkDurationTypeEnum = carparkDurationTypeEnum);
	}

	public float getOnedayMaxCharge() {
		return onedayMaxCharge;
	}

	public void setOnedayMaxCharge(float onedayMaxCharge) {
		firePropertyChange(Property.onedayMaxCharge.name(), this.onedayMaxCharge, this.onedayMaxCharge = onedayMaxCharge);
	}

	public int getFreeTime() {
		return freeTime;
	}

	public void setFreeTime(int freeTime) {
		firePropertyChange(Property.freeTime.name(), this.freeTime, this.freeTime = freeTime);
	}

	public CarparkAcrossDayTypeEnum getCarparkAcrossDayTypeEnum() {
		return carparkAcrossDayTypeEnum;
	}

	public void setCarparkAcrossDayTypeEnum(
			CarparkAcrossDayTypeEnum carparkAcrossDayTypeEnum) {
		firePropertyChange(Property.carparkAcrossDayTypeEnum.name(), this.carparkAcrossDayTypeEnum, this.carparkAcrossDayTypeEnum = carparkAcrossDayTypeEnum);
	}

	public int getAcrossdayChargeEnable() {
		return acrossdayChargeEnable;
	}

	public void setAcrossdayChargeEnable(int acrossdayChargeEnable) {
		firePropertyChange(Property.acrossdayChargeEnable.name(), this.acrossdayChargeEnable, this.acrossdayChargeEnable = acrossdayChargeEnable);
	}

	public CarparkHolidayTypeEnum getCarparkHolidayTypeEnum() {
		return carparkHolidayTypeEnum;
	}

	public void setCarparkHolidayTypeEnum(
			CarparkHolidayTypeEnum carparkHolidayTypeEnum) {
		firePropertyChange(Property.carparkHolidayTypeEnum.name(), this.carparkHolidayTypeEnum, this.carparkHolidayTypeEnum = carparkHolidayTypeEnum);
	}

	public Integer getStartStepTime() {
		return startStepTime;
	}

	public void setStartStepTime(Integer startStepTime) {
		firePropertyChange(Property.startStepTime.name(), this.startStepTime, this.startStepTime = startStepTime);
	}

	public Float getStartStepPrice() {
		return startStepPrice;
	}

	public void setStartStepPrice(Float startStepPrice) {
		firePropertyChange(Property.startStepPrice.name(), this.startStepPrice, this.startStepPrice = startStepPrice);
	}

	public List<CarparkDurationStandard> getCarparkDurationStandards() {
		return carparkDurationStandards;
	}

	public void setCarparkDurationStandards(List<CarparkDurationStandard> carparkDurationStandards) {
		this.carparkDurationStandards.clear();
		for (CarparkDurationStandard carparkDurationStandard : carparkDurationStandards) {
			carparkDurationStandard.setCarparkChargeStandard(this);
		}
		this.carparkDurationStandards.addAll(carparkDurationStandards);
		firePropertyChange(Property.carparkDurationStandards.name(), null, null);
	}
	
	public void addCarparkDurationStandard(CarparkDurationStandard carparkDurationStandard){
		carparkDurationStandard.setCarparkChargeStandard(this);
		this.carparkDurationStandards.add(carparkDurationStandard);
		firePropertyChange(Property.carparkDurationStandards.name(), null, null);
	}

	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}

	public void setCarpark(SingleCarparkCarpark carpark) {
		firePropertyChange(Property.carpark.name(), this.carpark, this.carpark = carpark);
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarparkChargeStandard that = (CarparkChargeStandard) o;

        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

	public Boolean getUsing() {
		return using;
	}

	public void setUsing(Boolean using) {
		this.using = using;
		if (pcs != null)
			pcs.firePropertyChange("using", null, null);
	}

	public Float getAcrossDayPrice() {
		return acrossDayPrice;
	}

	public void setAcrossDayPrice(Float acrossDayPrice) {
		this.acrossDayPrice = acrossDayPrice;
		if (pcs != null)
			pcs.firePropertyChange("acrossDayPrice", null, null);
	}

	public Boolean getAcrossDayIsFree() {
		return acrossDayIsFree;
	}

	public void setAcrossDayIsFree(Boolean acrossDayIsFree) {
		this.acrossDayIsFree = acrossDayIsFree;
		if (pcs != null)
			pcs.firePropertyChange("acrossDayIsFree", null, null);
	}
}
