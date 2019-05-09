package com.donglu.carpark.service.background.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.IpmsSynchroServiceI;
import com.donglu.carpark.util.ExecutorsUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UserHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Service;
import com.google.inject.Inject;

public class IpmsSynchroServiceImpl extends AbstractCarparkBackgroundService implements IpmsSynchroServiceI {
	private static final String IMPS_USER_SAVE_HISTORY = "impsUserSaveHistory";
	private CarparkDatabaseServiceProvider sp;
	private IpmsServiceI ipmsService;
	private ScheduledExecutorService uploadHistoryExecutorService;
	private boolean isRunService=false;
	private ScheduledExecutorService uploadImageExecutorService;

	@Inject
	public IpmsSynchroServiceImpl(IpmsServiceI ipmsService,CarparkDatabaseServiceProvider sp) {
		super(Scheduler.newFixedDelaySchedule(3, 3, TimeUnit.MINUTES), "ipms信息同步服务");
		this.ipmsService = ipmsService;
		this.sp = sp;
		uploadHistoryExecutorService = ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (!isRunService) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return;
				}
				//同步用户信息
				CarparkUserService carparkUserService = sp.getCarparkUserService();
				List<UserHistory> findUserHistory = carparkUserService.findUserHistory(UpdateEnum.values(), new ProcessEnum[] { ProcessEnum.未处理, ProcessEnum.处理失败 });
				for (UserHistory userHistory : findUserHistory) {
					UpdateEnum updateState = userHistory.getHistoryDetail().getUpdateState();
					boolean result=false;
					switch (updateState) {
					case 新添加:
						result=ipmsService.addUser(userHistory.getUser());
						break;
					case 被修改:
						result=ipmsService.updateUser(userHistory.getUser());
						break;
					case 被删除:
						result=ipmsService.deleteUser(userHistory.getUser());
						break;
					}
					if (result) {
						carparkUserService.updateUserHistory(userHistory, ProcessEnum.己处理);
					}
				}
				List<CarparkRecordHistory> findHaiYuRecordHistory = sp.getCarparkInOutService().findHaiYuRecordHistory(0, 1000, UpdateEnum.values(), new ProcessEnum[]{ProcessEnum.未处理,ProcessEnum.处理失败});
				for (CarparkRecordHistory carparkRecordHistory : findHaiYuRecordHistory) {
					SingleCarparkInOutHistory inOutHistory=carparkRecordHistory.getHistory();
					boolean result=false;
//				switch (carparkRecordHistory.getHistoryDetail().getUpdateState()) {
//				case 新添加:
//					result = ipmsService.addInOutHistory(inOutHistory);;
//					break;
//				case 被修改:
//					result = ipmsService.updateInOutHistory(inOutHistory);
//					break;
//				case 被删除:
//					break;
//				}
					if (inOutHistory.getOutTime()==null) {
						result = ipmsService.addInOutHistory(inOutHistory);
					}else{
						if(inOutHistory.getInTime()!=null){
							result = ipmsService.updateInOutHistory(inOutHistory);
						}else{
							result=true;
						}
					}
					if (result) {
						sp.getCarparkInOutService().updateHaiYuRecordHistory(Arrays.asList(carparkRecordHistory.getId()), ProcessEnum.己处理);
					}
				}
			}
		}, 5, 3, TimeUnit.SECONDS, "ipms记录上传服务");
		uploadImageExecutorService = ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (!isRunService) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return;
				}
				syncImages();
			}
		}, 5, 3, TimeUnit.SECONDS, "ipms图片上传服务");
	}

	protected void syncImages() {
		ipmsService.synchroImage(10);
	}

	@Override
	protected void run() {
		try {
			checkSetting();
			if (!isRunService) {
				return;
			}
			ipmsService.updateTempCarChargeHistory();
			ipmsService.updateFixCarChargeHistory();
			ipmsService.updateUserInfo();
			ipmsService.updateParkSpace();
			checkUserValidTo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void checkSetting() {
		try {
			SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用CJLAPP支付.name());
			if (findSystemSettingByKey != null && findSystemSettingByKey.getBooleanValue()) {
				isRunService=true;
			}else {
				isRunService=false;
			}
		} catch (Exception e) {
			log.error("检测设置时发生错误!",e);
		}
	}
	@Override
	protected void shutDown() throws Exception {
		super.shutDown();
		if (uploadHistoryExecutorService!=null) {
			uploadHistoryExecutorService.shutdown();
		}
		if(uploadImageExecutorService!=null) {
			uploadImageExecutorService.shutdown();
		}
	}
	@Override
	protected void startUp() throws Exception {
		super.startUp();
		checkSetting();
	}

	private void checkUserValidTo() {
		List<SingleCarparkUser> list = sp.getCarparkUserService().findOverdueUserByLastEditTime(0,50,null,StrUtil.getTodayTopTime(new Date()));
		for (SingleCarparkUser user : list) {
			user.setLastEditDate(new Date());
			sp.getCarparkUserService().saveUser(user);
		}
	}

}
