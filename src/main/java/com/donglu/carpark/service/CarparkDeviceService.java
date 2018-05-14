package com.donglu.carpark.service;

import java.util.Collection;

public interface CarparkDeviceService {
	public boolean openDoor(String ip);
	public default String getOpenDoorDevice(Collection<String> devices){return null;};
}
