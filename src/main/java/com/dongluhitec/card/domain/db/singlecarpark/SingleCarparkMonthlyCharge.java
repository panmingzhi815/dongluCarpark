package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.dongluhitec.card.domain.db.DomainObject;

@Entity
public class SingleCarparkMonthlyCharge extends DomainObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4552101069592956871L;

	@ManyToOne
	@JoinColumn(name = "carpark")
	private SingleCarparkCarpark carpark;

	@Column(name = "carpark", updatable = false, insertable = false, nullable = false)
	private Long carparkId;

	//收费名称
	private String chargeName;

	//收费编码
	private String chargeCode;

	//车类型
	private String carType;

	//月租月数
	private Integer rentingDays;

	//到期提醒天数
	private Integer expiringDays = 5;

	//到期延迟时间
	private Integer delayDays = 0;

	//租赁金额
	private long price;

	private String note;

	
	private String parkType;


	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}


	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		this.carparkId=carpark.getId();
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}


	public Long getCarparkId() {
		return carparkId;
	}


	public void setCarparkId(Long carparkId) {
		this.carparkId = carparkId;
		if (pcs != null)
			pcs.firePropertyChange("carparkId", null, null);
	}


	public String getChargeName() {
		return chargeName;
	}


	public void setChargeName(String chargeName) {
		this.chargeName = chargeName;
		if (pcs != null)
			pcs.firePropertyChange("chargeName", null, null);
	}


	public String getChargeCode() {
		return chargeCode;
	}


	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
		if (pcs != null)
			pcs.firePropertyChange("chargeCode", null, null);
	}


	public String getCarType() {
		return carType;
	}


	public void setCarType(String carType) {
		this.carType = carType;
		if (pcs != null)
			pcs.firePropertyChange("carType", null, null);
	}


	public Integer getRentingDays() {
		return rentingDays;
	}


	public void setRentingDays(Integer rentingDays) {
		this.rentingDays = rentingDays;
		if (pcs != null)
			pcs.firePropertyChange("rentingDays", null, null);
	}


	public Integer getExpiringDays() {
		return expiringDays;
	}


	public void setExpiringDays(Integer expiringDays) {
		this.expiringDays = expiringDays;
		if (pcs != null)
			pcs.firePropertyChange("expiringDays", null, null);
	}


	public Integer getDelayDays() {
		return delayDays;
	}


	public void setDelayDays(Integer delayDays) {
		this.delayDays = delayDays;
		if (pcs != null)
			pcs.firePropertyChange("delayDays", null, null);
	}


	public long getPrice() {
		return price;
	}


	public void setPrice(long price) {
		this.price = price;
		if (pcs != null)
			pcs.firePropertyChange("price", null, null);
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
		if (pcs != null)
			pcs.firePropertyChange("note", null, null);
	}


	public String getParkType() {
		return parkType;
	}


	public void setParkType(String parkType) {
		this.parkType = parkType;
		if (pcs != null)
			pcs.firePropertyChange("parkType", null, null);
	}
	
	
}
