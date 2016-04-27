package com.donglu.carpark.model.storemodel;



public class FreeInfo extends Info {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2913497111432331938L;
	
	private Long id;
	private String plateNo;
	private Float hour;
	private Float money;
	private String freeType;
	//1，add 2，edit 3.getById
	private int useType;
	private String storeName;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPlateNo() {
		return plateNo;
	}
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}
	public Float getHour() {
		return hour;
	}
	public void setHour(Float hour) {
		this.hour = hour;
	}
	public Float getMoney() {
		return money;
	}
	public void setMoney(Float money) {
		this.money = money;
	}
	public String getFreeType() {
		return freeType;
	}
	public void setFreeType(String freeType) {
		this.freeType = freeType;
	}
	public int getUseType() {
		return useType;
	}
	public void setUseType(int useType) {
		this.useType = useType;
	}
	public void setStoreName(String storeName) {
		this.storeName=storeName;
	}
	public String getStoreName() {
		return storeName;
	}
}
