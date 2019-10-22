package com.dongluhitec.card.domain.db.singlecarpark;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

@Entity
public class SingleCarparkDevice extends DomainObject{
	public enum Property{
		identifire,name,ip,inOrOut,cameraType,screenType,carpark,linkAddress,host
	}
	public enum DeviceInOutTypeEnum{
		进口,出口
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6409867850987940644L;
	
	private String identifire;//编号
	private String name;//名字
	private String ip="192.168.1.1";//车牌识别ip
	private String inType;//出入口类型 监控位置
	private String inOrOut; //进或者出
	private String type;//设备类型  tcp or 485
	private String linkAddress;//连接地址
	private String address="1.1";//设备地址
	private String roadType="混合车通道";//通道类型
	private Integer volume=1;
	private String advertise="车牌自动识别，请减速缓行";
	private ScreenTypeEnum screenType=ScreenTypeEnum.一二接口显示屏;
	private CameraTypeEnum cameraType=CameraTypeEnum.智芯;
	
	@ManyToOne
	private SingleCarparkCarpark carpark; //所属停车场
	
	private String controlTime;
	private String holidayControlTime;
	
	
	private Boolean isOpenCamera=true;
	
	private Boolean isHandCharge=true;
	
	private String deviceVersion;
	@Column(length=512)
	private String cameraVersion;
	private String host;
	
	public String getIdentifire() {
		return identifire;
	}
	public void setIdentifire(String identifire) {
		this.identifire = identifire;
		if (pcs != null)
			pcs.firePropertyChange("identifire", null, null);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		if (pcs != null)
			pcs.firePropertyChange("name", null, null);
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
		if (pcs != null)
			pcs.firePropertyChange("ip", null, null);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
		if (pcs != null)
			pcs.firePropertyChange("type", null, null);
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
		if (pcs != null)
			pcs.firePropertyChange("address", null, null);
	}
	public String getRoadType() {
		return roadType;
	}
	public void setRoadType(String roadType) {
		this.roadType = roadType;
		if (pcs != null)
			pcs.firePropertyChange("roadType", null, null);
	}
	public SingleCarparkCarpark getCarpark() {
		return carpark;
	}
	public void setCarpark(SingleCarparkCarpark carpark) {
		this.carpark = carpark;
		if (pcs != null)
			pcs.firePropertyChange("carpark", null, null);
	}
	public String getLinkAddress() {
		return linkAddress;
	}
	public void setLinkAddress(String linkAddress) {
		this.linkAddress = linkAddress;
		if (pcs != null)
			pcs.firePropertyChange("linkAddress", null, null);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getInType() {
		return inType;
	}
	public void setInType(String inType) {
		this.inType = inType;
		if (pcs != null)
			pcs.firePropertyChange("inType", null, null);
	}
	public Integer getVolume() {
		return volume;
	}
	public void setVolume(Integer volume) {
		this.volume = volume;
		if (pcs != null)
			pcs.firePropertyChange("volume", null, null);
	}
	
	public static void main(String[] args) {
		System.out.println("1.1".substring(2));
	}
	public String getAdvertise() {
		return advertise;
	}
	public void setAdvertise(String advertise) {
		this.advertise = advertise;
		if (pcs != null)
			pcs.firePropertyChange("advertise", null, null);
	}
	public ScreenTypeEnum getScreenType() {
		return screenType;
	}
	public void setScreenType(ScreenTypeEnum screenType) {
		this.screenType = screenType;
		if (pcs != null)
			pcs.firePropertyChange("screenType", null, null);
	}
	
	public String getLinkInfo(){
		return this.linkAddress+":"+this.address;
	}
	public CameraTypeEnum getCameraType() {
		return cameraType;
	}
	public void setCameraType(CameraTypeEnum cameraType) {
		this.cameraType = cameraType;
		if (pcs != null)
			pcs.firePropertyChange("cameraType", null, null);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj.getClass()!=this.getClass()) {
			return false;
		}
		if (!StrUtil.isEmpty(obj)) {
			SingleCarparkDevice device=(SingleCarparkDevice) obj;
			if (device.getIp().equals(getIp())) {
				return true;
			}
		}
		return super.equals(obj);
	}
	public String getInOrOut() {
		if (inOrOut==null) {
			if (getInType().indexOf("进口")>-1) {
				return "进口";
			}else{
				return "出口";
			}
		}
		return inOrOut;
	}
	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
		firePropertyChange("inOrOut", null, null);
	}
	public String getControlTime() {
		return controlTime;
	}
	public void setControlTime(String controlTime) {
		this.controlTime = controlTime;
		firePropertyChange("controlTime", null, null);
	}
	public String getHolidayControlTime() {
		return holidayControlTime;
	}
	public void setHolidayControlTime(String holidayControlTime) {
		this.holidayControlTime = holidayControlTime;
		firePropertyChange("holidayControlTime", null, null);
	}
	public Boolean getIsOpenCamera() {
		return isOpenCamera==null||isOpenCamera;
	}
	public void setIsOpenCamera(Boolean isOpenCamera) {
		this.isOpenCamera = isOpenCamera;
	}
	public Boolean getIsHandCharge() {
		return isHandCharge==null?false:isHandCharge;
	}
	public void setIsHandCharge(Boolean isHandCharge) {
		this.isHandCharge = isHandCharge;
		firePropertyChange("isHandCharge", null, null);
	}
	public String getDeviceVersion() {
		return deviceVersion;
	}
	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
		firePropertyChange("deviceVersion", null, null);
	}
	public String getCameraVersion() {
		return cameraVersion;
	}
	public void setCameraVersion(String cameraVersion) {
		this.cameraVersion = cameraVersion;
		//firePropertyChange("cameraVersion", null, null);
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
		//firePropertyChange("host", null, null);
	}
}
