package com.donglu.carpark.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;


public class AddDeviceModel extends SingleCarparkDevice{
	private List<SingleCarparkCarpark> list=new ArrayList<SingleCarparkCarpark>();
	private String serialAddress="COM1";
	private String tcpAddress="192.168.1.1";
	private String voice="1";
	private String addressLabel;
	private String tcpLabel;
	private DeviceRoadTypeEnum deviceRoadType=DeviceRoadTypeEnum.混合车通道;
	
	public AddDeviceModel(){
		SingleCarparkCarpark s=new SingleCarparkCarpark();
		s.setName("停车场1");
		list.add(s);
	}
	public List<SingleCarparkCarpark> getList() {
		return list;
	}

	public void setList(List<SingleCarparkCarpark> list) {
		this.list = list;
		if (pcs != null)
			pcs.firePropertyChange("list", null, null);
	}
	public SingleCarparkDevice getDevice(){
		SingleCarparkDevice device=new SingleCarparkDevice();
		device.setAddress(getAddress());
		device.setCarpark(getCarpark());
		device.setId(getId());
		device.setIdentifire(getIdentifire());
		device.setInType(getInType());
		device.setIp(getIp());
		device.setLinkAddress(getLinkAddress());
		device.setName(getName());
		device.setRoadType(getDeviceRoadType().name());
		device.setType(getType());
		device.setVolume(getVolume());
		device.setAdvertise(getAdvertise());
		device.setScreenType(getScreenType());
		return device;
	}
	public String getSerialAddress() {
		return serialAddress;
	}
	public void setSerialAddress(String serialAddress) {
		this.serialAddress = serialAddress;
		if (pcs != null)
			pcs.firePropertyChange("serialAddress", null, null);
	}
	public String getTcpAddress() {
		return tcpAddress;
	}
	public void setTcpAddress(String tcpAddress) {
		this.tcpAddress = tcpAddress;
		if (pcs != null)
			pcs.firePropertyChange("tcpAddress", null, null);
	}
	public void setDevice(SingleCarparkDevice device) {
		setAddress(device.getAddress());
		setCarpark(device.getCarpark());
		setId(device.getId());
		setIdentifire(device.getIdentifire());
		setInType(device.getInType());
		setIp(device.getIp());
		setLinkAddress(device.getLinkAddress());
		setName(device.getName());
		setRoadType(device.getRoadType());
		setDeviceRoadType(DeviceRoadTypeEnum.valueOf(device.getRoadType()));
		setType(device.getType());
		String type2 = device.getType();
		if (type2.equals("tcp")) {
			setTcpAddress(device.getLinkAddress());
		}else{
			setSerialAddress(device.getLinkAddress());
		}
		setVolume(device.getVolume());
		setAdvertise(device.getAdvertise());
		setScreenType(device.getScreenType());
	}
	public String getVoice() {
		return voice;
	}
	public void setVoice(String voice) {
		this.voice = voice;
		if (pcs != null)
			pcs.firePropertyChange("voice", null, null);
	}
	public String getAddressLabel() {
		return addressLabel;
	}
	public void setAddressLabel(String addressLabel) {
		this.addressLabel = addressLabel;
		if (pcs != null)
			pcs.firePropertyChange("addressLabel", null, null);
	}
	public String getTcpLabel() {
		return getLinkAddress()==null?null:getLinkAddress().substring(0, getLinkAddress().indexOf(":"));
	}
	public void setTcpLabel(String tcpLabel) {
		this.tcpLabel = tcpLabel;
		if (pcs != null)
			pcs.firePropertyChange("tcpLabel", null, null);
	}
	public DeviceRoadTypeEnum getDeviceRoadType() {
		return deviceRoadType;
	}
	public void setDeviceRoadType(DeviceRoadTypeEnum deviceRoadType) {
		this.deviceRoadType = deviceRoadType;
		if (pcs != null)
			pcs.firePropertyChange("deviceRoadType", null, null);
	}
	
}
