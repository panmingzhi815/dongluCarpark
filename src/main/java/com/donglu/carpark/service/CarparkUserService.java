package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
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
	/**
	 * 保存储值用户的消费记录
	 * @param pph
	 * @return
	 */
	Long savePrepaidUserPayHistory(SingleCarparkPrepaidUserPayHistory pph);
	List<SingleCarparkPrepaidUserPayHistory> findPrepaidUserPayHistoryList(int begin,int max,String userName,String plateNO,Date start,Date end);
	int countPrepaidUserPayHistoryList(String userName,String plateNO,Date start,Date end);
}
