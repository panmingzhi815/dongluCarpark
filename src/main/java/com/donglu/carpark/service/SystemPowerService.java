package com.donglu.carpark.service;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

public interface SystemPowerService{
	public SingleCarparkSystemUser getUser();
    /**
     * 检查注册码是否己过期
     * @return
     */
    boolean isExpire();
}
