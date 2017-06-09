package com.donglu.carpark.service.background.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.IpmsSynchroServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UserHistory;
import com.google.inject.Inject;

public class IpmsSynchroServiceImpl extends AbstractCarparkBackgroundService implements IpmsSynchroServiceI {
	private static final String IMPS_USER_SAVE_HISTORY = "impsUserSaveHistory";
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private IpmsServiceI ipmsService;

	public IpmsSynchroServiceImpl() {
		super(Scheduler.newFixedDelaySchedule(5, 3, TimeUnit.SECONDS), "ipms信息同步服务");
	}

	@Override
	protected void run() {
		try {
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
				if (inOutHistory.getOutTime()==null) {
					result = ipmsService.addInOutHistory(inOutHistory);
				}else{
					result = ipmsService.updateInOutHistory(inOutHistory);
				}
				if (result) {
					sp.getCarparkInOutService().updateHaiYuRecordHistory(Arrays.asList(carparkRecordHistory.getId()), ProcessEnum.己处理);
				}
			}
			if (!findUserHistory.isEmpty()||!findHaiYuRecordHistory.isEmpty()) {
				return;
			}
			ipmsService.updateFixCarChargeHistory();
			ipmsService.updateUserInfo();
			ipmsService.updateTempCarChargeHistory();
			ipmsService.updateParkSpace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
