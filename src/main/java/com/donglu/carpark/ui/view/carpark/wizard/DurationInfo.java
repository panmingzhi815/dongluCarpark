package com.donglu.carpark.ui.view.carpark.wizard;

public class DurationInfo {
	private String name;
	private Integer time;
	private Float price;
	private String dayDurationName;
	private Float dayDurationPrice;
	private Float crossDayDurationPrice;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getTime() {
		return time;
	}
	public void setTime(Integer time) {
		this.time = time;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public Float getDayDurationPrice() {
		return dayDurationPrice;
	}
	public void setDayDurationPrice(Float dayDurationPrice) {
		this.dayDurationPrice = dayDurationPrice;
	}
	public Float getCrossDayDurationPrice() {
		return crossDayDurationPrice;
	}
	public void setCrossDayDurationPrice(Float crossDayDurationPrice) {
		this.crossDayDurationPrice=crossDayDurationPrice;
	}
	public String getDayDurationName() {
		return dayDurationName;
	}
	public void setDayDurationName(String dayDurationName) {
		this.dayDurationName=dayDurationName;
	}
}
