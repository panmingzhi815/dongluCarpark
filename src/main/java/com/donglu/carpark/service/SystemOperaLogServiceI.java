package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;

public interface SystemOperaLogServiceI {
	void saveOperaLog(SystemOperaLogTypeEnum systemOperaLogType,String content, String operaName);

	List<SingleCarparkSystemOperaLog> findBySearch(String operaName, Date start, Date end, SystemOperaLogTypeEnum type);

	void saveOperaLog(SystemOperaLogTypeEnum systemOperaLogType, String content, byte[] bigImage, String operaName,Object... objects);
}
