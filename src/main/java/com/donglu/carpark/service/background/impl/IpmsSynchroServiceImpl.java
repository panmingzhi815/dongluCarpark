package com.donglu.carpark.service.background.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.IpmsSynchroServiceI;
import com.donglu.carpark.util.CarparkFileUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class IpmsSynchroServiceImpl extends AbstractCarparkBackgroundService implements IpmsSynchroServiceI {
	private static final String IMPS_USER_SAVE_HISTORY = "impsUserSaveHistory";
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private IpmsServiceI ipmsService;
	public IpmsSynchroServiceImpl() {
		super(Scheduler.newFixedDelaySchedule(5, 3, TimeUnit.SECONDS), "自动删除图片");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void run() {
		try {
			Map<Long, Date> userUpdateTime=(Map<Long, Date>) CarparkFileUtils.readObject(IMPS_USER_SAVE_HISTORY);
			userUpdateTime=userUpdateTime==null?new HashMap<>():userUpdateTime;
			List<SingleCarparkUser> findAll = sp.getCarparkUserService().findAll();
			if (StrUtil.isEmpty(findAll)) {
				userUpdateTime=new HashMap<>();
			}
			for (SingleCarparkUser singleCarparkUser : findAll) {
				Date date = userUpdateTime.get(singleCarparkUser.getId());
				Date lastEditDate = singleCarparkUser.getLastEditDate();
				if (date!=null&&lastEditDate.equals(date)) {
					continue;
				}
				boolean addUser = ipmsService.addUser(singleCarparkUser);
				if (!addUser) {
					addUser=ipmsService.updateUser(singleCarparkUser);
				}
				if (addUser) {
					userUpdateTime.put(singleCarparkUser.getId(), lastEditDate);
				}
			}
			CarparkFileUtils.writeObject(IMPS_USER_SAVE_HISTORY, userUpdateTime);
			ipmsService.updateFixCarChargeHistory();
			ipmsService.updateUserInfo();
			ipmsService.updateTempCarChargeHistory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
