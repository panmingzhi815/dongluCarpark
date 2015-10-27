package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;

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
	 * @return
	 */
	int findFixSlotIsNow();
	/**
	 * 查询现在的临时车位数
	 * @return
	 */
	int findTempSlotIsNow();
	/**
	 * 查询现在的总车位数
	 * @return
	 */
	int findTotalSlotIsNow();

	List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(String plateNO, boolean order);

	List<SingleCarparkInOutHistory> findAddNoPlateNOHistory(boolean order);
}
