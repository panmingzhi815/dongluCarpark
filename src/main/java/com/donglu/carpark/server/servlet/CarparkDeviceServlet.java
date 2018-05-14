package com.donglu.carpark.server.servlet;

import java.util.Collection;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkDeviceService;
import com.google.inject.Inject;

public class CarparkDeviceServlet extends HessianServlet implements CarparkDeviceService {
	
	private CarparkDeviceService carparkDeviceService;

	@Inject
	public CarparkDeviceServlet(CarparkDatabaseServiceProvider sp) {
		this.carparkDeviceService = sp.getCarparkDeviceService();
	}

	@Override
	public boolean openDoor(String ip) {
		return carparkDeviceService.openDoor(ip);
	}
	@Override
	public String getOpenDoorDevice(Collection<String> devices) {
		return carparkDeviceService.getOpenDoorDevice(devices);
	}
}
