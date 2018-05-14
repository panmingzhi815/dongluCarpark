package com.donglu.carpark.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.donglu.carpark.service.CarparkDeviceService;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;

public class CarparkDeviceServiceImpl implements CarparkDeviceService {
	Map<String, String> mapWaitOpenDevices=new HashMap<>();
	@Override
	public boolean openDoor(String ip) {
		mapWaitOpenDevices.put(ip, ip);
		return true;
	}
	
	@Override
	public String getOpenDoorDevice(Collection<String> devices) {
		for (String string : devices) {
			String s = mapWaitOpenDevices.get(string);
			if (s!=null) {
				return mapWaitOpenDevices.remove(s);
			}
		}
		return null;
	}
	
}
