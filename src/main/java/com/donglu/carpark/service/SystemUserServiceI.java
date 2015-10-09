package com.donglu.carpark.service;

import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

public interface SystemUserServiceI {
	
	public List<SingleCarparkSystemUser> findAll();
	
	public SingleCarparkSystemUser findByNameAndPassword(String userName,String password);
	
	public Long removeSystemUser(SingleCarparkSystemUser systemUser);
	
	public Long saveSystemUser(SingleCarparkSystemUser systemUser);

	public void init();
}
