package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.Restrictions;

import com.donglu.carpark.service.CarparkDeviceService;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class CarparkDeviceServiceImpl implements CarparkDeviceService {
	private Provider<EntityManager> emProvider;
	private UnitOfWork unitOfWork;

	@Inject
	public CarparkDeviceServiceImpl(Provider<EntityManager> emProvider, UnitOfWork unitOfWork) {
		this.emProvider = emProvider;
		this.unitOfWork = unitOfWork;
	}

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
	public List<SingleCarparkDevice> findAllDevice(String host,String code) {
		Criteria c = CriteriaUtils.createCriteria(emProvider.get(), SingleCarparkDevice.class);
		if (!StrUtil.isEmpty(host)) {
			c.add(Restrictions.eq("", host));
		}
		if (!StrUtil.isEmpty(code)) {
			c.add(Restrictions.eq("", code));
		}
		return c.getResultList();
	}
	
	@Transactional
	@Override
	public Long saveDevice(List<SingleCarparkDevice> devices) {
		DatabaseOperation<SingleCarparkDevice> dom = DatabaseOperation.forClass(SingleCarparkDevice.class, emProvider.get());
		for (SingleCarparkDevice device : devices) {
			dom.save(device);
		}
		return (long) devices.size();
	}

	@Override
	public Long saveDevice(SingleCarparkDevice device) {
		return device.getId();
	}

	@Override
	public Long deleteDevice(SingleCarparkDevice device) {
		return null;
	}
	
}
