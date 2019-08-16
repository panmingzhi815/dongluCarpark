package com.dongluhitec.card.domain.db.singlecarpark.shanghai;

import com.dongluhitec.card.domain.db.DomainObject;

public class Village extends DomainObject {
//	villageName String 小区名称 
//	villageCode String 小区编码 
//	provinceCode Number 省编码 
//	cityCode Number 城市编码 
//	districtCode Number 区县编码 
//	streetCode Number 街道编码 
//	roadCode String 道路编码 
//	policeStationCode String 派出所编码 
//	address String 地址 
//	picUrl String 照片url 
//	createTime String 创建时间 
//	updateTime String 数据更新时间 
	
	private String villageCode;
	private String villageName;
	private String provinceCode;
	private String cityCode;
	private String districtCode;
	private String streetCode;
	private String roadCode;
	private String policeStationCode;
	private String address;
	private String picUrl;
	private String createTime;
	private String updateTime;
	
	public String getVillageCode() {
		return villageCode;
	}
	public void setVillageCode(String villageCode) {
		this.villageCode = villageCode;
		firePropertyChange("villageCode", null, null);
	}
	public String getVillageName() {
		return villageName;
	}
	public void setVillageName(String villageName) {
		this.villageName = villageName;
		firePropertyChange("villageName", null, null);
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
		firePropertyChange("provinceCode", null, null);
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
		firePropertyChange("cityCode", null, null);
	}
	public String getDistrictCode() {
		return districtCode;
	}
	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
		firePropertyChange("districtCode", null, null);
	}
	public String getStreetCode() {
		return streetCode;
	}
	public void setStreetCode(String streetCode) {
		this.streetCode = streetCode;
		firePropertyChange("streetCode", null, null);
	}
	public String getRoadCode() {
		return roadCode;
	}
	public void setRoadCode(String roadCode) {
		this.roadCode = roadCode;
		firePropertyChange("roadCode", null, null);
	}
	public String getPoliceStationCode() {
		return policeStationCode;
	}
	public void setPoliceStationCode(String policeStationCode) {
		this.policeStationCode = policeStationCode;
		firePropertyChange("policeStationCode", null, null);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
		firePropertyChange("address", null, null);
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
		firePropertyChange("picUrl", null, null);
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
		firePropertyChange("createTime", null, null);
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
		firePropertyChange("updateTime", null, null);
	}
}
