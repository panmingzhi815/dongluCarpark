package com.donglu.carpark.service.background.impl;

import java.util.concurrent.TimeUnit;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.SystemUserServiceI;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.LoginCheckServiceI;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class LoginCheckServiceImpl extends AbstractCarparkBackgroundService implements LoginCheckServiceI {

	private String hostIp;
	private CarparkDatabaseServiceProvider sp;
	private SystemUserServiceI systemUserService;
	private CommonUIFacility commonui;
	@Inject
	public LoginCheckServiceImpl(CarparkDatabaseServiceProvider sp,CommonUIFacility commonui) {
		super(Scheduler.newFixedDelaySchedule(3, 3, TimeUnit.SECONDS), "检测用户登录状态");
		this.sp = sp;
		this.commonui = commonui;
		systemUserService = sp.getSystemUserService();
		hostIp = StrUtil.getHostIp();
	}

	@Override
	protected void run() {
		String loginStatus = systemUserService.loginStatus(ConstUtil.getUserName());
		if (loginStatus==null) {
			commonui.info("登录提示","你的登录凭证已经失效，请重新登录！！！");
			System.exit(0);
			return;
		}else if(loginStatus.equals("不检查")){
			
		}else if(!loginStatus.equals(hostIp)){
			commonui.info("登录提示","你的账户在：["+loginStatus+"]上登录!!!");
			System.exit(0);
			return;
		}
		
	}
	
}
