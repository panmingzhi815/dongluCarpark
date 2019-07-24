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

import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkDeviceService;
import com.donglu.carpark.service.background.impl.ShanghaidibiaoSynchroServiceImpl;
import com.donglu.carpark.ui.servlet.WebSocketServer;
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
	private static final Map<String,SingleCarparkDevice> mapDevices=new HashMap<>();
	@Inject
	public CarparkDeviceServiceImpl(Provider<EntityManager> emProvider, UnitOfWork unitOfWork) {
		this.emProvider = emProvider;
		this.unitOfWork = unitOfWork;
	}

	Map<String, String> mapWaitOpenDevices=new HashMap<>();
	@Override
	public boolean openDoor(String ip) {
		mapWaitOpenDevices.put(ip, ip);
		JSONObject jo=new JSONObject();
		jo.put("type", "openDoor");
		jo.put("ip", ip);
		System.out.println(jo.toString());
		WebSocketServer.sendToAll(jo.toString());
		return true;
	}
	@Override
	public boolean openDoor(String ip, String userName) {
		mapWaitOpenDevices.put(ip, ip);
		JSONObject jo=new JSONObject();
		jo.put("type", "openDoor");
		jo.put("ip", ip);
		jo.put("userName", userName);
		System.out.println(jo.toString());
		WebSocketServer.sendToAll(jo.toString());
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

	@Override
	public boolean closeDoor(String ip) {
		JSONObject jo=new JSONObject();
		jo.put("type", "closeDoor");
		jo.put("ip", ip);
		WebSocketServer.sendToAll(jo.toJSONString());
		return true;
	}

	@Override
	public List<SingleCarparkDevice> getAllDevice() {
		return new ArrayList<>(mapDevices.values());
	}
	@Override
	public void setDevices(Map<String,SingleCarparkDevice> map) {
		mapDevices.putAll(map);
		for (SingleCarparkDevice d : map.values()) {
			ShanghaidibiaoSynchroServiceImpl.addTopic(d.getIdentifire());
		}
	}
	
}
