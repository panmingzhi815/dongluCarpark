package com.donglu.carpark.server.servlet;

import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkDeviceService;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.server.util.HibernateSerializerFactory;
import com.google.inject.Inject;

public class CarparkDeviceServlet extends HessianServlet implements CarparkDeviceService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CarparkDeviceService carparkDeviceService;

	@Inject
	public CarparkDeviceServlet(CarparkDatabaseServiceProvider sp) {
		this.carparkDeviceService = sp.getCarparkDeviceService();
	}
	
	@Override
	public void init() throws ServletException {
		getSerializerFactory().addFactory(new HibernateSerializerFactory());
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
	public List<SingleCarparkDevice> findAllDevice(String host,String code) {
		return carparkDeviceService.findAllDevice(host, code);
	}

	@Override
	public Long saveDevice(List<SingleCarparkDevice> devices) {
		return carparkDeviceService.saveDevice(devices);
	}

	@Override
	public Long saveDevice(SingleCarparkDevice device) {
		return carparkDeviceService.saveDevice(device);
	}

	@Override
	public Long deleteDevice(SingleCarparkDevice device) {
		return carparkDeviceService.deleteDevice(device);
	}

}
