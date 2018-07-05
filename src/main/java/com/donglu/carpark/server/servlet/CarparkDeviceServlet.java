package com.donglu.carpark.server.servlet;

import java.util.Collection;
import java.util.List;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkDeviceService;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
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
