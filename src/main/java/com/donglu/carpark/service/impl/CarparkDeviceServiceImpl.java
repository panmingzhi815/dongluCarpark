package com.donglu.carpark.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

	@Override
	public List<SingleCarparkDevice> findAllDevice(String host) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public Long saveDevice(List<SingleCarparkDevice> devices) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public Long saveDevice(SingleCarparkDevice device) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public Long deleteDevice(SingleCarparkDevice device) {
		// TODO 自动生成的方法存根
		return null;
	}
	
}
