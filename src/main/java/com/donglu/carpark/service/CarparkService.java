package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.donglu.carpark.service.impl.CarparkServiceImpl;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;

public interface CarparkService {
	public Long saveCarpark(SingleCarparkCarpark carpark);
	public Long deleteCarpark(SingleCarparkCarpark carpark);
	public List<SingleCarparkCarpark> findAllCarpark();
	List<SingleCarparkCarpark> findCarparkToLevel();
	SingleCarparkCarpark findCarparkTopLevel();
	
	Long saveMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge);
	Long deleteMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge);
	public Long deleteMonthlyCharge(Long id);
	List<SingleCarparkMonthlyCharge> findAllMonthlyCharge();
	
	public List<SingleCarparkMonthlyCharge> findMonthlyChargeByCarpark(SingleCarparkCarpark carpark);
	
	/**
	 * 保存设备
	 * @param device
	 * @return
	 */
	public Long saveCarparkDevice(SingleCarparkDevice device);
	/**
	 * 删除设备
	 * @param device
	 * @return
	 */
	public Long deleteDevice(SingleCarparkDevice device);
	/**
	 * 查询所有设备
	 * @return
	 */
	public List<SingleCarparkDevice> findAll();
	
	public Long saveMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h);
	public Long deleteMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h);
	
	public List<SingleCarparkMonthlyUserPayHistory> findByPropety(Map<String, Object> map);
	
	//停车场设置
	public List<SingleCarparkSystemSetting> findAllSystemSetting();
	public Long saveSystemSetting(SingleCarparkSystemSetting h);
	public SingleCarparkSystemSetting findSystemSettingByKey(String key);
	
	//停车场临时收费
	public CarparkChargeStandard findCarparkChargeStandardByCode(String code);
	/**
	 * 获取停车场车类型列表
	 * 
	 * @return
	 */
	public List<CarparkCarType> getCarparkCarTypeList();
	public Long saveCarparkChargeStandard(com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard carparkChargeStandard);
	
	/**
	 * 获取临时收费设置
	 * @param carpark
	 * @return
	 */
	public List<CarparkChargeStandard> findTempChargeByCarpark(SingleCarparkCarpark carpark);
	public Long deleteTempCharge(Long id);
	/**
	 * 查找充值记录
	 * @param size 
	 * @param maxResult 
	 * @param userName
	 * @param operaName
	 * @param start
	 * @param end
	 * @return
	 */
	public List<SingleCarparkMonthlyUserPayHistory> findMonthlyUserPayHistoryByCondition(int maxResult, int size, String userName, String operaName, Date start, Date end);
	public int countMonthlyUserPayHistoryByCondition(String userName, String operaName, Date start, Date end);
	
	
}
