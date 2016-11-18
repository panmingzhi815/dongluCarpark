package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public interface CarparkUserService {
	Long saveUser(SingleCarparkUser user);
	Long deleteUser(SingleCarparkUser user);
	
	List<SingleCarparkUser> findAll();
	List<SingleCarparkUser> findByNameOrPlateNo(String name,String plateNo, String address, SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue);
	/**
	 * 根据车牌查找单个用户
	 * @param plateNO
	 * @param carparkId
	 * @return
	 */
	SingleCarparkUser findUserByPlateNo(String plateNO, Long carparkId);
	/**
	 * 根据车牌过期时间查找所有用户
	 * @param plateNO
	 * @param carparkId
	 * @param validTo
	 * @return
	 */
	List<SingleCarparkUser> findAllUserByPlateNO(String plateNO, Long carparkId,Date validTo);
	/**
	 * 根据车牌计算用户总车位数
	 * @param plateNO
	 * @param carparkId
	 * @param validTo
	 * @return
	 */
	int sumAllUserSlotByPlateNO(String plateNO, Long carparkId,Date validTo);
	
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
	
	SingleCarparkUser findUserById(Long userId);
	/**
	 * 根据用户名,停车场过期时间查找用户
	 * @param name
	 * @param carpark
	 * @param validTo
	 * @return
	 */
	List<SingleCarparkUser> findUserByNameAndCarpark(String name, SingleCarparkCarpark carpark,Date validTo);
	/**
	 * 根据车位查找用户
	 * @param parkingSpace
	 * @return
	 */
	SingleCarparkUser findUserByParkingSpace(String parkingSpace);
	/**
	 * 查找相似车牌
	 * @param start
	 * @param size
	 * @param plateNO
	 * @param likeSize
	 * @param carparkId
	 * @param validTo
	 * @return
	 */
	List<SingleCarparkUser> findUserByPlateNoLikeSize(int start, int size, String plateNO, int likeSize, Long carparkId, Date validTo);
}
