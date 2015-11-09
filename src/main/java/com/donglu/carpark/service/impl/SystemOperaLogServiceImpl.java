package com.donglu.carpark.service.impl;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.persistence.EntityManager;

import com.donglu.carpark.service.SystemOperaLogServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.MapperConfig;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class SystemOperaLogServiceImpl implements SystemOperaLogServiceI {
	
	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	@Inject
	private MapperConfig mapper;

	private ScheduledExecutorService saveLogService;
	
	@Override
	public void saveOperaLog(SystemOperaLogTypeEnum type, String content) {
		if (StrUtil.isEmpty(saveLogService)) {
			saveLogService = Executors.newSingleThreadScheduledExecutor();
		}
		SingleCarparkSystemOperaLog log=new SingleCarparkSystemOperaLog();
		log.setOperaName(System.getProperty("userName"));
		log.setOperaDate(new Date());
		log.setType(type);
		log.setContent(content);
		saveLogService.submit(()->{
			save(log);
		});
	}
	@Transactional
	void save(SingleCarparkSystemOperaLog log){
		DatabaseOperation<SingleCarparkSystemOperaLog> dom = DatabaseOperation.forClass(SingleCarparkSystemOperaLog.class, emprovider.get());
		dom.insert(log);
	}
}
