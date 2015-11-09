package com.donglu.carpark.util;

import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;

public @interface SystemLog {
	SystemOperaLogTypeEnum type();
	String conntent();
}
