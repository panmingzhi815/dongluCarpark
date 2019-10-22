package com.donglu.carpark.service.background.impl;

import java.util.Arrays;

import com.donglu.carpark.service.background.OnlineOrderCheckServiceI;
import com.donglu.carpark.service.background.ServerBackgroudServiceI;
import com.google.common.util.concurrent.ServiceManager;
import com.google.inject.Inject;

public class ServerBackgroudServiceImpl implements ServerBackgroudServiceI {
	@Inject
	public OnlineOrderCheckServiceI onlineOrderCheckServiceI;
	private ServiceManager sm;
	
	
	public ServerBackgroudServiceImpl(OnlineOrderCheckServiceI onlineOrderCheckServiceI) {
		this.onlineOrderCheckServiceI = onlineOrderCheckServiceI;
		sm = new ServiceManager(Arrays.asList(onlineOrderCheckServiceI));
	}

	@Override	
	public void startServices() {
		sm.startAsync();
	}

	@Override
	public void stopServices() {
		sm.stopAsync();
	}

}
