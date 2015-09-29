package com.donglu.carpark.wizard;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;


public class AddDeviceModel extends SingleCarparkDevice{
	private List<SingleCarparkCarpark> list=new ArrayList<SingleCarparkCarpark>();
	
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
		device.setRoadType(getRoadType());
		device.setType(getType());
		return device;
	}
	
}
