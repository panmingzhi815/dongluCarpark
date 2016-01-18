package com.donglu.carpark.service;

import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public interface CarparkUserService {
	Long saveUser(SingleCarparkUser user);
	Long deleteUser(SingleCarparkUser user);
	
	List<SingleCarparkUser> findAll();
	List<SingleCarparkUser> findByNameOrPlateNo(String name,String plateNo, int willOverdue, String overdue);
	SingleCarparkUser findUserByPlateNo(String plateNO, Long carparkId);
	
	List<SingleCarparkUser> findUserByMonthChargeId(Long id);
	Long saveUserByMany(List<SingleCarparkUser> list);
	List<SingleCarparkUser> findUserThanIdMore(Long id, List<Long> errorIds);
	
	List<SingleCarparkLockCar> findLockCarByPlateNO(String plateNO);
	Long saveLockCar(SingleCarparkLockCar lc);
	Long deleteLockCar(SingleCarparkLockCar lc);
}
