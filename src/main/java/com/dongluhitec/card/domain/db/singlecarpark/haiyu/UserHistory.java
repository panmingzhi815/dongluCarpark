package com.dongluhitec.card.domain.db.singlecarpark.haiyu;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
@Entity
public class UserHistory implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3060347755054851127L;
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	protected Long auto_id;
    protected Long id;
	
	private String name;
	private String plateNo;
	private String address;
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
	@Column(length=20)
	private String telephone;
	
	private String idCard;
	
	private CarTypeEnum carType=CarTypeEnum.SmallCar;
	private String carTypeShanghai;
	private String plateType="普通蓝牌";
	
	@Embedded
    private HistoryDetail historyDetail = new HistoryDetail();
	
	public UserHistory(SingleCarparkUser user,UpdateEnum updateEnum) {
		setUser(user);
		historyDetail.setUpdateState(updateEnum);
	}
	public UserHistory() {}
	public SingleCarparkUser getUser() {
		SingleCarparkUser user = new SingleCarparkUser();
		user.setPlateNo(plateNo);
		user.setName(name);
		user.setAddress(address);
		user.setCarType(carType);
		user.setValidTo(validTo);
		user.setTelephone(telephone);
		user.setCreateDate(createDate);
		user.setId(id);
		user.setIdCard(idCard);
		user.setPlateType(plateType);
		return user;
	}
	public void setUser(SingleCarparkUser user) {
		setPlateNo(user.getPlateNo());
		setName(user.getName());
		setAddress(user.getAddress());
		setCarType(user.getCarType());
		setValidTo(user.getValidTo());
		setTelephone(user.getTelephone());
		setCreateDate(user.getCreateDate());
		setId(user.getId());
		setIdCard(user.getIdCard());
		setCarTypeShanghai(user.getCarTypeShanghai());
		setPlateType(user.getPlateType());
	}
	public HistoryDetail getHistoryDetail() {
		return historyDetail;
	}
	public void setHistoryDetail(HistoryDetail historyDetail) {
		this.historyDetail = historyDetail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getValidTo() {
		return validTo;
	}
	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public CarTypeEnum getCarType() {
		return carType;
	}
	public void setCarType(CarTypeEnum carType) {
		this.carType = carType;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAuto_id() {
		return auto_id;
	}
	public void setAuto_id(Long auto_id) {
		this.auto_id = auto_id;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	public String getCarTypeShanghai() {
		return carTypeShanghai;
	}
	public void setCarTypeShanghai(String carTypeShanghai) {
		this.carTypeShanghai = carTypeShanghai;
	}
	public String getPlateType() {
		return plateType;
	}
	public void setPlateType(String plateType) {
		this.plateType = plateType;
	}
}
