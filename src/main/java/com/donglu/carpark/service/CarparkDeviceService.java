package com.donglu.carpark.service;

import java.util.Collection;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;

public interface CarparkDeviceService {
	public boolean openDoor(String ip);
	public default String getOpenDoorDevice(Collection<String> devices){return null;}
	
	public List<SingleCarparkDevice> findAllDevice(String host,String code);
	Long saveDevice(List<SingleCarparkDevice> devices);
	Long saveDevice(SingleCarparkDevice device);
	Long deleteDevice(SingleCarparkDevice device);
}
