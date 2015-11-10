package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;

public interface CarparkInOutServiceI {
	
	Long saveInOutHistory(SingleCarparkInOutHistory inout);
	
	List<SingleCarparkInOutHistory> findByNoOut(String plateNo);

	float findTotalCharge(String userName);
	
	
	List<SingleCarparkInOutHistory> findByCondition(int maxResult,int size,String plateNo,String userName,String carType,String inout,Date in,Date out,String operaName, String inDevice, String outDevice, Long returnAccount);
	Long countByCondition(String plateNo,String userName,String carType,String inout,Date start,Date end,String operaName, String inDevice, String outDevice, Long returnAccount);

	List<SingleCarparkInOutHistory> findNotReturnAccount(String returnUser);
	/**
	 * 查询应收金额
	 * @param userName
	 * @return
	 */
	float findShouldMoneyByName(String userName);
	/**
	 * 查询实收金额
	 * @param userName
	 * @return
	 */
	float findFactMoneyByName(String userName);
	/**
	 * 查询免费金额
	 * @param userName
	 * @return
	 */
	float findFreeMoneyByName(String userName);
	
	Long saveInOutHistoryOfList(List<SingleCarparkInOutHistory> list);
	/**
	 * 查询现在的固定车位数
	 * @param singleCarparkCarpark 
	 * @return
	 */
	int findFixSlotIsNow(SingleCarparkCarpark singleCarparkCarpark);
	/**
	 * 查询现在的临时车位数
	 * @param singleCarparkCarpark 
	 * @return
	 */
	int findTempSlotIsNow(SingleCarparkCarpark singleCarparkCarpark);
	/**
	 * 查询现在的总车位数
	 * @param singleCarparkCarpark 
	 * @return
	 */
	int findTotalSlotIsNow(SingleCarparkCarpark singleCarparkCarpark);

	List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(String plateNO, boolean order, List<SingleCarparkDevice> list);

	List<SingleCarparkInOutHistory> findAddNoPlateNOHistory(boolean order);
	/**
	 * 
	 * @return
	 */
	Long deleteAllHistory();
	/**
	 * 查找实收未归账的记录
	 * @param userName
	 * @return
	 */
	List<SingleCarparkInOutHistory> findHistoryFactMoneyNotReturn(String userName);
	/**
	 * 查找免费金额未归账的记录
	 * @param userName
	 * @return
	 */
	List<SingleCarparkInOutHistory> findHistoryFreeMoneyNotReturn(String userName);
	/**
	 * 查找一天最大收费
	 * @param carType
	 * @return
	 */
	float findOneDayMaxCharge(CarTypeEnum carType);
	/**
	 * 查找车今天缴费
	 * @param plateNo
	 * @return
	 */
	float countTodayCharge(String plateNo);
	/**
	 * 保存抬杆记录
	 * @param openDoor
	 * @return
	 */
	Long saveOpenDoorLog(SingleCarparkOpenDoorLog openDoor);

	List<SingleCarparkOpenDoorLog> findOpenDoorLogBySearch(String operaName, Date start, Date end, String deviceName);
}
