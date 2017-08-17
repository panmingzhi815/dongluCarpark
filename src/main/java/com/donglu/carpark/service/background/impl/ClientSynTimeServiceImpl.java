package com.donglu.carpark.service.background.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;


import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.ClientSynTimeServiceI;
import com.donglu.carpark.util.SystemUtils;
import com.google.inject.Inject;

public class ClientSynTimeServiceImpl extends AbstractCarparkBackgroundService implements ClientSynTimeServiceI {
	
	public ClientSynTimeServiceImpl() {
		super(Scheduler.newFixedDelaySchedule(1, 300, TimeUnit.SECONDS), "客户端时间同步服务器时间");
	}

	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	@Override
	protected void run() {
		try {
			Date serverDate = sp.getSettingService().getServerDate();
//			serverDate=new DateTime(2017,8,11,1,1).toDate();
			SystemUtils.setLocalTime(serverDate);
			log.info("从服务器获取时间成功！");
		} catch (Exception e) {
			log.error("同步时间时发生错误！"+e.getMessage(),e);
		}
	}
	
	@Override
	protected void startUp() throws Exception {
		String s = System.getProperty("synServerTime", "true");
		if(s.equals("false")){
			stopAsync();
		}
	}
}
