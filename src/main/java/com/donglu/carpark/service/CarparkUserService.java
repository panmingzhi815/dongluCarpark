package com.donglu.carpark.service;

import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public interface CarparkUserService {
	Long saveUser(SingleCarparkUser user);
	Long deleteUser(SingleCarparkUser user);
	
	List<SingleCarparkUser> findAll();
	List<SingleCarparkUser> findByNameOrPlateNo(String name,String plateNo);
}
