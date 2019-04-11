package com.donglu.carpark.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;

public interface CarparkDeviceService {
	public boolean openDoor(String ip);
	public default String getOpenDoorDevice(Collection<String> devices){return null;}
	
	public List<SingleCarparkDevice> findAllDevice(String host,String code);
	Long saveDevice(List<SingleCarparkDevice> devices);
	Long saveDevice(SingleCarparkDevice device);
	Long deleteDevice(SingleCarparkDevice device);
	public boolean closeDoor(String ip);
	public List<SingleCarparkDevice> getAllDevice();
	void setDevices(Map<String, SingleCarparkDevice> map);
	public default boolean openDoor(String ip, String userName) {return openDoor(ip);}
}
