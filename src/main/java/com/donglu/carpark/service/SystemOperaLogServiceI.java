package com.donglu.carpark.service;

import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;

public interface SystemOperaLogServiceI {
	void saveOperaLog(SystemOperaLogTypeEnum type,String content);
}
