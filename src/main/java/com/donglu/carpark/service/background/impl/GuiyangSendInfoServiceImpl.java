package com.donglu.carpark.service.background.impl;

import java.util.concurrent.TimeUnit;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.GuiyangSendInfoServiceI;

public class GuiyangSendInfoServiceImpl extends AbstractCarparkBackgroundService implements GuiyangSendInfoServiceI {

	private CarparkDatabaseServiceProvider sp;

	public GuiyangSendInfoServiceImpl(CarparkDatabaseServiceProvider sp) {
		super(Scheduler.newFixedDelaySchedule(10, 5, TimeUnit.SECONDS), "贵阳车牌报送上传服务");
		this.sp = sp;
	}

	@Override
	protected void run() {
		sp.getCarparkInOutService();
	}

}
