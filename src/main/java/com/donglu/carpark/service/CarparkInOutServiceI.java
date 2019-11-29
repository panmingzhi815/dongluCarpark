package com.donglu.carpark.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.donglu.carpark.server.module.CacheMethod;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.CarCheckHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkOffLineHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkStillTime;
import com.dongluhitec.card.domain.db.singlecarpark.CheckOnlineOrder;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceErrorMessage;
import com.dongluhitec.card.domain.db.singlecarpark.OverSpeedCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkImageHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SmsInfo;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;

/**
 * 对进出场信息操作，对锁车信息操作
 * @author Michael
 *
 */
public interface CarparkInOutServiceI {
	
	Long saveInOutHistory(SingleCarparkInOutHistory inout);
	
	List<SingleCarparkInOutHistory> findByNoOut(String plateNo, SingleCarparkCarpark carpark);

	float findTotalCharge(String userName);
	
	
	List<SingleCarparkInOutHistory> findByCondition(int maxResult,int size,String plateNo,String userName,String carType,String inout,Date in,Date out,Date outStart, Date outEnd, String operaName, String inDevice, String outDevice, Long returnAccount,Long carparkId, float... shouldMoney);
	Long countByCondition(String plateNo,String userName,String carType,String inout,Date start,Date end,Date outStart, Date outEnd, String operaName, String inDevice, String outDevice, Long returnAccount, Long carparkId, float... shouldMoney);

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
	@CacheMethod
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
	 * 查询现在的临时总车位数
	 * @param singleCarparkCarpark 
	 * @return
	 */
	int findTempSlotIsNow(SingleCarparkCarpark singleCarparkCarpark);
	/**
	 * 查询现在的临时车车位数
	 * @param singleCarparkCarpark 
	 * @return
	 */
	Integer findTotalSlotIsNow(SingleCarparkCarpark singleCarparkCarpark);

	List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(String plateNO, boolean order,SingleCarparkCarpark carpark);

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
	 * @param carparkId 
	 * @return
	 */
	float findOneDayMaxCharge(Long carType, Long carparkId);
	/**
	 * 查找车指定日期缴费金额
	 * @param plateNo
	 * @param e 
	 * @return
	 */
	float countTodayCharge(String plateNo,Date date, Date e);
	/**
	 * 保存抬杆记录
	 * @param openDoor
	 * @return
	 */
	Long saveOpenDoorLog(SingleCarparkOpenDoorLog openDoor);
	/**
	 * 查找抬杆记录
	 * @param operaName
	 * @param start
	 * @param end
	 * @param deviceName
	 * @return
	 */
	List<SingleCarparkOpenDoorLog> findOpenDoorLogBySearch(String operaName, Date start, Date end, String deviceName);
	List<SingleCarparkOpenDoorLog> findOpenDoorLogBySearch(int startSize,int size,String operaName, Date start, Date end, String deviceName,String plate);
	Long countOpenDoorLogBySearch(String operaName, Date start, Date end, String deviceName,String plate);
	/**
	 * 查找子停车场的进出场记录
	 * @param carparkId
	 * @param plateNO
	 * @param inTime
	 * @param outTime
	 * @return
	 */
	List<SingleCarparkInOutHistory> findHistoryByChildCarparkInOut(Long carparkId,String plateNO, Date inTime, Date outTime);
	/**
	 * 查找指定停车场的指定车牌的为出场纪录
	 * @param id 停车场编号
	 * @param pn 车牌号
	 * @return 多条进场记录
	 */
	List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(Long id, String pn);
	/**
	 * 查找一定数量的进场纪录
	 * @param size
	 * @return
	 */
	List<SingleCarparkInOutHistory> findCarInHistorys(int size);
	/**
	 * 查找车牌的一条进场纪录
	 * @param plateNO
	 * @return
	 */
	SingleCarparkInOutHistory findInOutHistoryByPlateNO(String plateNO);
	/**
	 * 查找一些车牌的纪录
	 * @param plateNOs
	 * @param order
	 * @param carpark
	 * @return
	 */
	List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(List<String> plateNOs, boolean order,
			SingleCarparkCarpark carpark);
	/**
	 * 查询场内车
	 * @param carpark 
	 * @return
	 */
	int findTotalCarIn(SingleCarparkCarpark carpark);

	int findTotalTempCarIn(SingleCarparkCarpark carpark);

	int findTotalFixCarIn(SingleCarparkCarpark carpark);

	/**
	 * 根据id查找进场记录
	 * @param id
	 * @return
	 */
	SingleCarparkInOutHistory findInOutById(Long id);
	/**
	 * 查找跨天收费金额
	 * @param carType
	 * @param carparkId
	 * @return
	 */
	float findAcrossDayPrice(CarTypeEnum carType, Long carparkId);

	
	/**
	 * 查找未上传的进出场记录
	 * @param id
	 * @param errorIds
	 * @return
	 */
	List<SingleCarparkInOutHistory> findInHistoryThanIdMore(Long id, List<Long> errorIds);
	List<SingleCarparkInOutHistory> findOutHistoryThanIdMore(Long id, List<Long> errorIds);
	/**
	 * 查找未出场的记录
	 * @param string
	 * @return
	 */
	List<SingleCarparkInOutHistory> searchNotOutHistory(int page,int rows,String plateNO);
	Long countNotOutHistory(String plateNO);
	
	/**
	 * 清理停了超过多少天的场内车
	 * @param date 天数
	 */
	void clearCarHistoryWithInByDate(int date);

	/**
	 * 锁车操作
	 * @param m
	 * @return
	 */
	Long saveLockCar(SingleCarparkLockCar m);

	List<SingleCarparkLockCar> findLockCar(String plateNO, String status, String operaName, Date start, Date end);

	SingleCarparkLockCar findLockCarByPlateNO(String plateNO, Boolean isLock);

	Long lockCar(String plateNO);

	Long updateCarparkStillTime(SingleCarparkCarpark carpark,SingleCarparkDevice device, String plateNO, String bigImg);

	List<CarparkStillTime> findCarparkStillTime(String plateNO, Date inTime);
	
	Map<String, Long> getDeviceFlows(boolean inOrOut,Date start,Date end);
	List<String> findAllDeviceName(boolean inOrOut);
	/**
	 * 获取设备故障信息
	 * @param first
	 * @param max
	 * @param deviceName
	 * @param start
	 * @param end
	 * @return
	 */
	List<DeviceErrorMessage> findDeviceErrorMessageBySearch(int first, int max, String deviceName, Date start, Date end);

	Long countDeviceErrorMessageBySearch(String deviceName, Date start, Date end);
	
	Long saveDeviceErrorMessage(DeviceErrorMessage deviceErrorMessage);

	DeviceErrorMessage findDeviceErrorMessageByDevice(SingleCarparkDevice device);

	Long saveCarparkOffLineHistory(CarparkOffLineHistory carparkOffLineHistory);
	
	List<CarparkOffLineHistory> findCarparkOffLineHistoryBySearch(int first, int max, String plateNO, Date start, Date end);

	Long countCarparkOffLineHistoryBySearch(String plateNO, Date start, Date end);
	/**
	 * 临时车优惠
	 * @param start
	 * @param maxValue
	 * @param plateNo
	 * @return
	 */
	List<SingleCarparkFreeTempCar> findTempCarFreeByLike(int start, int maxValue, String plateNo);
	Long countTempCarFreeByLike(String plateNo);
	SingleCarparkFreeTempCar findTempCarFreeByPlateNO(String plateNo);
	Long deleteTempCarFree(SingleCarparkFreeTempCar ft);
	Long saveTempCarFree(SingleCarparkFreeTempCar ft);
	/**
	 * 查找固定车的场内记录
	 * @param user 用户
	 * @param b 是否为固定车进入 true 固定车进入 false 临时车进入
	 * @return
	 */
	List<SingleCarparkInOutHistory> findInOutHistoryByUser(SingleCarparkUser user, Boolean b);
	/**
	 * 查找场内车记录
	 * @param carpark
	 * @param pn
	 * @param b 是否为固定车
	 * @return
	 */
	List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(SingleCarparkCarpark carpark, String pn, boolean b);
	/**
	 * 保存图片记录
	 * @param ih
	 * @return
	 */
	Long saveImageHistory(SingleCarparkImageHistory ih);
	/**
	 * 保存图片记录
	 * @param ih
	 * @return
	 */
	Long deleteImageHistory(SingleCarparkImageHistory ih);
	/**
	 * 查找图片记录
	 * @param first
	 * @param max
	 * @param plate
	 * @param type
	 * @return
	 */
	List<SingleCarparkImageHistory> findImageHistoryBySearch(int first,int max,String plate,String type,Date start,Date end);
	int countImageHistoryBySearch(String plate,String type,Date start,Date end);
	

	/**
	 * 查询停车场历史进出记录任务,贵州海誉
	 * @param start
	 * @param size
	 * @param updateEnums
	 * @param processEnums
	 * @return
	 */
    List<CarparkRecordHistory> findHaiYuRecordHistory(int start, int size, UpdateEnum[] updateEnums, ProcessEnum[] processEnums);

	void updateHaiYuRecordHistory(List<Long> longList, ProcessEnum processEnum);
	/**
	 * 查找场内车记录
	 * @param carpark
	 * @param pn
	 * @param b 是否为固定车
	 * @return
	 */
	List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(SingleCarparkCarpark carpark, Collection<String> pns, boolean b);
	/**
	 * 根据车牌和进场时间查找未出场记录，默认根据进场时间排序
	 * @param i
	 * @param totalSlot
	 * @param plates
	 * @param s
	 * @return
	 */
	List<SingleCarparkInOutHistory> findInOutHistoryByInTime(int i, int totalSlot, Set<String> plates, Date s);
	/**
	 * 查找场内车记录
	 * @param start
	 * @param size
	 * @param plateNo
	 * @param carpark
	 * @return
	 */
	List<SingleCarparkInOutHistory> searchNotOutHistory(int start, int size, String plateNo, SingleCarparkCarpark carpark);

	List<SingleCarparkInOutHistory> findHistoryByIn(int start, int size, SingleCarparkCarpark carpark, String carType, Date startTime, Date endTime);
	
	boolean exeUpdateSql(String sql);

	List<SingleCarparkInOutHistory> findHistoryThanId(Long id, int start, int size);

	List<CarPayHistory> findCarPayHistoryThanId(Long id, int start, int size);

	List<Double> countReturnMoney(String userName);

	Long updateRecount(Long maxId, Long returnAccountId, boolean free);
	Long updateRecount(Long maxId, Long returnAccountId, boolean free,String userName);

	List<SingleCarparkInOutHistory> findHistoryByTimeOrder(int start, int size, String plate, Date begin, Date end, int timeType);

	Long saveOverSpeedCar(OverSpeedCar car);
	
	public List<OverSpeedCar> findOverSpeedCarByMap(int start,int size,Map<String,Object> map);
	public Long countOverSpeedCarByMap(Map<String,Object> map);

	Map<String, Integer> countFreeSize(String plateNo, String userName, String carType, String inout, Date start, Date end, Date outStart, Date outEnd, String operaName, String inDevice,
			String outDevice, Long returnAccount, Long carparkId, float... shouldMoney);
	
	public float[] countMoney(String plateNo, String userName, String carType, String inout, Date start, Date end, Date outStart, Date outEnd, String operaName, String inDevice,
			String outDevice, Long returnAccount, Long carparkId, float... shouldMoney);
	/**
	 * 根据操作员，时间统计费用
	 * @param start
	 * @param end
	 * @param operaName
	 * @param type
	 * @return
	 */
	List<Object[]> countFeeBySearch(Date start, Date end, String operaName, int type);
	/**
	 * 根据时间，操作员，免费原因,统计免费车数量
	 * @param start
	 * @param end
	 * @param userName
	 * @param type
	 * @return
	 */
	List<Object[]> countFreeBySearch(Date start, Date end, String operaName, int type);

	List<SmsInfo> findSmsInfoByStatus(int size, int[] status);

	Long saveSmsInfo(SmsInfo smsInfo);

	<T> List<T> findByMap(int current, int pageSize, Class<T> class1, Map<String, Object> map);

	Long countByMap(Class<?> class1, Map<String, Object> map);

	List<Object[]> countCarPayBySearch(Date start, Date end, String operaName, int type);

	<T extends DomainObject> Long saveEntity(T t);
	<T extends DomainObject> Long saveEntity(List<T> t);
	<T extends DomainObject> Long deleteEntity(T t);
	<T extends DomainObject> Long deleteEntity(List<T> t);
	<T extends DomainObject> T findById(Class<T> class1, Long id);

	List<Double> sum(Class<? extends DomainObject> class1, List<String> list,Map<String,Object> map);

	List<Double> countNoReturnAccountMoney(String userName);

}
