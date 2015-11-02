package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;

public interface CarparkService {
	/**
	 * 保存停车场
	 * @param carpark
	 * @return
	 */
	public Long saveCarpark(SingleCarparkCarpark carpark);
	/**
	 * 删除停车场
	 * @param carpark
	 * @return
	 */
	public Long deleteCarpark(SingleCarparkCarpark carpark);
	/**
	 * 删除停车场
	 * @return
	 */
	public List<SingleCarparkCarpark> findAllCarpark();
	/**
	 * 查询最上级停车场
	 * @return
	 */
	List<SingleCarparkCarpark> findCarparkToLevel();
	/**
	 * 
	 * @return
	 */
	SingleCarparkCarpark findCarparkTopLevel();
	/**
	 * 保存月租设置
	 * @param monthlyCharge
	 * @return
	 */
	Long saveMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge);
	/**
	 * 删除月租设置
	 * @param monthlyCharge
	 * @return
	 */
	Long deleteMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge);
	/**
	 * 删除月租设置
	 * @param id
	 * @return
	 */
	public Long deleteMonthlyCharge(Long id);
	
	/**
	 * 查询所有月租设置
	 * @return
	 */
	List<SingleCarparkMonthlyCharge> findAllMonthlyCharge();
	/**
	 * 查询指定停车场的月租设置
	 * @param carpark
	 * @return
	 */
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
	/**
	 * 保存月租充值记录
	 * @param h
	 * @return
	 */
	public Long saveMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h);
	/**
	 * 
	 * @param h
	 * @return
	 */
	public Long deleteMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h);
	//停车场设置
	/**
	 * 
	 * @return
	 */
	public List<SingleCarparkSystemSetting> findAllSystemSetting();
	/**
	 * 
	 * @param h
	 * @return
	 */
	public Long saveSystemSetting(SingleCarparkSystemSetting h);
	/**
	 * 
	 * @param key
	 * @return
	 */
	public SingleCarparkSystemSetting findSystemSettingByKey(String key);
	
	//停车场临时收费
	public CarparkChargeStandard findCarparkChargeStandardByCode(String code);
	/**
	 * 获取停车场车类型列表
	 * 
	 * @return
	 */
	public List<CarparkCarType> getCarparkCarTypeList();
	/**
	 * 
	 * @param carparkChargeStandard
	 * @return
	 */
	public Long saveCarparkChargeStandard(CarparkChargeStandard carparkChargeStandard);
	
	/**
	 * 获取临时收费设置
	 * @param carpark
	 * @return
	 */
	public List<CarparkChargeStandard> findTempChargeByCarpark(SingleCarparkCarpark carpark);
	/**
	 * 
	 * @param id
	 * @return
	 */
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
	/**
	 * 统计所有充值记录数量
	 * @param userName
	 * @param operaName
	 * @param start
	 * @param end
	 * @return
	 */
	public int countMonthlyUserPayHistoryByCondition(String userName, String operaName, Date start, Date end);
	/**
	 * 
	 * @param a
	 * @return
	 */
	public Long saveReturnAccount(SingleCarparkReturnAccount a);
	
	/**
	 * 
	 * @param id
	 * @return
	 */
	public SingleCarparkCarpark findCarparkById(Long id);
	/**
	 * 归账信息查询
	 * @param size
	 * @param i
	 * @param userName
	 * @param operaName
	 * @param start
	 * @param end
	 * @return
	 */
	public List<SingleCarparkReturnAccount> findReturnAccountByCondition(int size, int i, String userName, String operaName, Date start, Date end);
	public int countReturnAccountByCondition(String userName, String operaName, Date start, Date end);
	
	/**
	 * 查询固定设置
	 * @param id
	 * @return
	 */
	public SingleCarparkMonthlyCharge findMonthlyChargeById(Long id);
	//黑名单
	public Long saveBlackUser(SingleCarparkBlackUser b);
	public Long deleteBlackUser(SingleCarparkBlackUser b);
	public List<SingleCarparkBlackUser> findAllBlackUser();
	/**
	 * 节假日
	 * @param year
	 * @return
	 */
	public List<Holiday> findHolidayByYear(int year);
	public Long deleteHoliday(List<Holiday> list);
	public Long saveHoliday(List<Holiday> list);
	/**
	 * 计算临时收费
	 * @param carTypeId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	float calculateTempCharge(Long carTypeId, Date startTime, Date endTime);
	/**
	 * 查找车牌是否为黑名单
	 * @param plateNO
	 * @return
	 */
	public SingleCarparkBlackUser findBlackUserByPlateNO(String plateNO);
	/**
	 * 查找有固定车位的用户的数量
	 * @return
	 */
	public int countMonthUserByHaveCarSite();
	public List<CarparkChargeStandard> findCarparkTempCharge(long l);
	/**
	 * 查找指定日期的节假日
	 * @param date
	 * @return
	 */
	Holiday findHolidayByDate(Date date);
	/**
	 * 根据编码查找月租设置
	 * @param code
	 * @return
	 */
	public SingleCarparkMonthlyCharge findMonthlyChargeByCode(String code);
	
	public List<CarparkChargeStandard> findAllCarparkChargeStandard();
	
	public SingleCarparkCarpark findCarparkByCode(String code);
	public Map<SNSettingType, String> findAllSN();
}
