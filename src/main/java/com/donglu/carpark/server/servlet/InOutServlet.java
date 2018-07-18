package com.donglu.carpark.server.servlet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkOffLineHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkStillTime;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceErrorMessage;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkImageHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.server.util.HibernateSerializerFactory;
import com.google.inject.Inject;

public class InOutServlet extends HessianServlet implements CarparkInOutServiceI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1750095210460236232L;
	private Logger LOGGER = LoggerFactory.getLogger(UserServlet.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;
//	@Inject
//	private com.donglu.carpark.service.WebService WebService;
	private CarparkInOutServiceI carparkInOutService;



    @Override
    public void init() throws ServletException {
        try {
        	getSerializerFactory().addFactory(new HibernateSerializerFactory());
        	sp.start();
        } catch (Exception e) {
            LOGGER.error("Cannot start service provider in the InOutServlet engine", e);
            throw new ServletException("Cannot start service provider in the servlet engine");
        }
        carparkInOutService = sp.getCarparkInOutService();
    }



	@Override
	public Long saveInOutHistory(SingleCarparkInOutHistory inout) {
		return carparkInOutService.saveInOutHistory(inout);
	}



	@Override
	public List<SingleCarparkInOutHistory> findByNoOut(String plateNo, SingleCarparkCarpark carpark) {
		return carparkInOutService.findByNoOut(plateNo, carpark);
	}



	@Override
	public float findTotalCharge(String userName) {
		return carparkInOutService.findTotalCharge(userName);
	}



	@Override
	public List<SingleCarparkInOutHistory> findByCondition(int maxResult, int size, String plateNo, String userName, String carType, String inout, Date in, Date out,Date outStart, Date outEnd, String operaName, String inDevice,
			String outDevice, Long returnAccount, Long carparkId, float... shouldMoney) {
		return carparkInOutService.findByCondition(maxResult, size, plateNo, userName, carType, inout, in, out, outStart, outEnd, operaName, inDevice, outDevice, returnAccount, carparkId, shouldMoney);
	}



	@Override
	public Long countByCondition(String plateNo, String userName, String carType, String inout, Date start, Date end,Date outStart, Date outEnd, String operaName, String inDevice, String outDevice, Long returnAccount,
			Long carparkId, float... shouldMoney) {
		return carparkInOutService.countByCondition(plateNo, userName, carType, inout, start, end, outStart, outEnd, operaName, inDevice, outDevice, returnAccount, carparkId, shouldMoney);
	}



	@Override
	public List<SingleCarparkInOutHistory> findNotReturnAccount(String returnUser) {
		return carparkInOutService.findNotReturnAccount(returnUser);
	}



	@Override
	public float findShouldMoneyByName(String userName) {
		return carparkInOutService.findShouldMoneyByName(userName);
	}



	@Override
	public float findFactMoneyByName(String userName) {
		return carparkInOutService.findFactMoneyByName(userName);
	}



	@Override
	public float findFreeMoneyByName(String userName) {
		return carparkInOutService.findFreeMoneyByName(userName);
	}



	@Override
	public Long saveInOutHistoryOfList(List<SingleCarparkInOutHistory> list) {
		return carparkInOutService.saveInOutHistoryOfList(list);
	}



	@Override
	public int findFixSlotIsNow(SingleCarparkCarpark singleCarparkCarpark) {
		return carparkInOutService.findFixSlotIsNow(singleCarparkCarpark);
	}



	@Override
	public int findTempSlotIsNow(SingleCarparkCarpark singleCarparkCarpark) {
		return carparkInOutService.findTempSlotIsNow(singleCarparkCarpark);
	}



	@Override
	public Integer findTotalSlotIsNow(SingleCarparkCarpark singleCarparkCarpark) {
		return carparkInOutService.findTotalSlotIsNow(singleCarparkCarpark);
	}



	@Override
	public List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(String plateNO, boolean order, SingleCarparkCarpark carpark) {
		return carparkInOutService.searchHistoryByLikePlateNO(plateNO, order, carpark);
	}



	@Override
	public List<SingleCarparkInOutHistory> findAddNoPlateNOHistory(boolean order) {
		return carparkInOutService.findAddNoPlateNOHistory(order);
	}



	@Override
	public Long deleteAllHistory() {
		return carparkInOutService.deleteAllHistory();
	}



	@Override
	public List<SingleCarparkInOutHistory> findHistoryFactMoneyNotReturn(String userName) {
		return carparkInOutService.findHistoryFactMoneyNotReturn(userName);
	}



	@Override
	public List<SingleCarparkInOutHistory> findHistoryFreeMoneyNotReturn(String userName) {
		return carparkInOutService.findHistoryFreeMoneyNotReturn(userName);
	}



	@Override
	public float findOneDayMaxCharge(Long carType, Long carparkId) {
		return carparkInOutService.findOneDayMaxCharge(carType, carparkId);
	}



	@Override
	public float countTodayCharge(String plateNo, Date date, Date e) {
		return carparkInOutService.countTodayCharge(plateNo, date, e);
	}



	@Override
	public Long saveOpenDoorLog(SingleCarparkOpenDoorLog openDoor) {
		return carparkInOutService.saveOpenDoorLog(openDoor);
	}



	@Override
	public List<SingleCarparkOpenDoorLog> findOpenDoorLogBySearch(String operaName, Date start, Date end, String deviceName) {
		return carparkInOutService.findOpenDoorLogBySearch(operaName, start, end, deviceName);
	}



	@Override
	public List<SingleCarparkInOutHistory> findHistoryByChildCarparkInOut(Long carparkId, String plateNO, Date inTime, Date outTime) {
		return carparkInOutService.findHistoryByChildCarparkInOut(carparkId, plateNO, inTime, outTime);
	}



	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(Long id, String pn) {
		return carparkInOutService.findInOutHistoryByCarparkAndPlateNO(id, pn);
	}



	@Override
	public List<SingleCarparkInOutHistory> findCarInHistorys(int size) {
		return carparkInOutService.findCarInHistorys(size);
	}



	@Override
	public SingleCarparkInOutHistory findInOutHistoryByPlateNO(String plateNO) {
		return carparkInOutService.findInOutHistoryByPlateNO(plateNO);
	}



	@Override
	public List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(List<String> plateNOs, boolean order, SingleCarparkCarpark carpark) {
		return carparkInOutService.searchHistoryByLikePlateNO(plateNOs, order, carpark);
	}



	@Override
	public int findTotalCarIn(SingleCarparkCarpark carpark) {
		return carparkInOutService.findTotalCarIn(carpark);
	}



	@Override
	public int findTotalTempCarIn(SingleCarparkCarpark carpark) {
		return carparkInOutService.findTotalTempCarIn(carpark);
	}



	@Override
	public int findTotalFixCarIn(SingleCarparkCarpark carpark) {
		return carparkInOutService.findTotalFixCarIn(carpark);
	}



	@Override
	public SingleCarparkInOutHistory findInOutById(Long id) {
		return carparkInOutService.findInOutById(id);
	}



	@Override
	public float findAcrossDayPrice(CarTypeEnum carType, Long carparkId) {
		return carparkInOutService.findAcrossDayPrice(carType, carparkId);
	}



	@Override
	public List<SingleCarparkInOutHistory> findInHistoryThanIdMore(Long id, List<Long> errorIds) {
		return carparkInOutService.findInHistoryThanIdMore(id, errorIds);
	}



	@Override
	public List<SingleCarparkInOutHistory> findOutHistoryThanIdMore(Long id, List<Long> errorIds) {
		return carparkInOutService.findOutHistoryThanIdMore(id, errorIds);
	}



	@Override
	public List<SingleCarparkInOutHistory> searchNotOutHistory(int page, int rows, String plateNO) {
		return carparkInOutService.searchNotOutHistory(page, rows, plateNO);
	}



	@Override
	public Long countNotOutHistory(String plateNO) {
		return carparkInOutService.countNotOutHistory(plateNO);
	}



	@Override
	public void clearCarHistoryWithInByDate(int date) {
		carparkInOutService.clearCarHistoryWithInByDate(date);
	}



	@Override
	public Long saveLockCar(SingleCarparkLockCar m) {
		return carparkInOutService.saveLockCar(m);
	}



	@Override
	public List<SingleCarparkLockCar> findLockCar(String plateNO, String status, String operaName, Date start, Date end) {
		return carparkInOutService.findLockCar(plateNO, status, operaName, start, end);
	}



	@Override
	public SingleCarparkLockCar findLockCarByPlateNO(String plateNO, Boolean isLock) {
		return carparkInOutService.findLockCarByPlateNO(plateNO, isLock);
	}



	@Override
	public Long lockCar(String plateNO) {
		Long lockCar2 = carparkInOutService.lockCar(plateNO);
//		boolean lockCar = WebService.lockCar(plateNO, 1);
//		if (!lockCar) {
//			return null;
//		}
		return lockCar2;
	}



	@Override
	public Long updateCarparkStillTime(SingleCarparkCarpark carpark, SingleCarparkDevice device, String plateNO, String bigImg) {
		return carparkInOutService.updateCarparkStillTime(carpark, device, plateNO, bigImg);
	}



	@Override
	public List<CarparkStillTime> findCarparkStillTime(String plateNO, Date inTime) {
		return carparkInOutService.findCarparkStillTime(plateNO, inTime);
	}



	@Override
	public Map<String, Long> getDeviceFlows(boolean inOrOut,Date start,Date end) {
		return carparkInOutService.getDeviceFlows(inOrOut, start, end);		
	}



	@Override
	public List<String> findAllDeviceName(boolean inOrOut) {
		return carparkInOutService.findAllDeviceName(inOrOut);
	}



	@Override
	public List<DeviceErrorMessage> findDeviceErrorMessageBySearch(int first, int max, String deviceName, Date start, Date end) {
		return carparkInOutService.findDeviceErrorMessageBySearch(first, max, deviceName, start, end);
	}



	@Override
	public Long countDeviceErrorMessageBySearch(String deviceName, Date start, Date end) {
		return carparkInOutService.countDeviceErrorMessageBySearch(deviceName, start, end);
	}



	@Override
	public Long saveDeviceErrorMessage(DeviceErrorMessage deviceErrorMessage) {
		return carparkInOutService.saveDeviceErrorMessage(deviceErrorMessage);
	}



	@Override
	public DeviceErrorMessage findDeviceErrorMessageByDevice(SingleCarparkDevice device) {
		return carparkInOutService.findDeviceErrorMessageByDevice(device);
	}



	@Override
	public Long saveCarparkOffLineHistory(CarparkOffLineHistory carparkOffLineHistory) {
		return carparkInOutService.saveCarparkOffLineHistory(carparkOffLineHistory);
	}



	@Override
	public List<CarparkOffLineHistory> findCarparkOffLineHistoryBySearch(int first, int max, String plateNO, Date start, Date end) {
		return carparkInOutService.findCarparkOffLineHistoryBySearch(first, max, plateNO, start, end);
	}



	@Override
	public Long countCarparkOffLineHistoryBySearch(String plateNO, Date start, Date end) {
		return carparkInOutService.countCarparkOffLineHistoryBySearch(plateNO, start, end);
	}
	public List<SingleCarparkFreeTempCar> findTempCarFreeByLike(int start, int maxValue, String plateNo) {
		return carparkInOutService.findTempCarFreeByLike(start, maxValue, plateNo);
	}
	@Override
	public Long countTempCarFreeByLike(String plateNo){
		return carparkInOutService.countTempCarFreeByLike(plateNo);
	}



	@Override
	public SingleCarparkFreeTempCar findTempCarFreeByPlateNO(String plateNo) {
		return carparkInOutService.findTempCarFreeByPlateNO(plateNo);
	}



	@Override
	public Long deleteTempCarFree(SingleCarparkFreeTempCar ft) {
		return carparkInOutService.deleteTempCarFree(ft);
	}



	@Override
	public Long saveTempCarFree(SingleCarparkFreeTempCar ft) {
		return carparkInOutService.saveTempCarFree(ft);
	}



	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByUser(SingleCarparkUser user, Boolean b) {
		return carparkInOutService.findInOutHistoryByUser(user, b);
	}



	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(SingleCarparkCarpark carpark, String pn,
			boolean b) {
		return carparkInOutService.findInOutHistoryByCarparkAndPlateNO(carpark, pn, b);
	}


	@Override
	public Long saveImageHistory(SingleCarparkImageHistory ih) {
		return carparkInOutService.saveImageHistory(ih);
	}



	@Override
	public Long deleteImageHistory(SingleCarparkImageHistory ih) {
		return carparkInOutService.deleteImageHistory(ih);
	}



	@Override
	public List<SingleCarparkImageHistory> findImageHistoryBySearch(int first, int max, String plate, String type, Date start, Date end) {
		return carparkInOutService.findImageHistoryBySearch(first, max, plate, type, start, end);
	}



	@Override
	public int countImageHistoryBySearch(String plate, String type, Date start, Date end) {
		return carparkInOutService.countImageHistoryBySearch(plate, type, start, end);
	}
    @Override
    public List<CarparkRecordHistory> findHaiYuRecordHistory(int start, int size, UpdateEnum[] updateEnums, ProcessEnum[] processEnums) {
		return carparkInOutService.findHaiYuRecordHistory(start, size, updateEnums, processEnums);
	}

    @Override
    public void updateHaiYuRecordHistory(List<Long> longList, ProcessEnum processEnum) {
        carparkInOutService.updateHaiYuRecordHistory(longList, processEnum);
    }

	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByInTime(int i, int totalSlot, Set<String> plates, Date s) {
		return carparkInOutService.findInOutHistoryByInTime(i, totalSlot, plates, s);
	}



	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(SingleCarparkCarpark carpark, Collection<String> pns, boolean b) {
		return carparkInOutService.findInOutHistoryByCarparkAndPlateNO(carpark, pns, b);
	}



	@Override
	public List<SingleCarparkOpenDoorLog> findOpenDoorLogBySearch(int startSize, int size, String operaName, Date start, Date end, String deviceName) {
		return carparkInOutService.findOpenDoorLogBySearch(startSize, size, operaName, start, end, deviceName);
	}



	@Override
	public Long countOpenDoorLogBySearch(String operaName, Date start, Date end, String deviceName) {
		return carparkInOutService.countOpenDoorLogBySearch(operaName, start, end, deviceName);
	}



	@Override
	public List<SingleCarparkInOutHistory> searchNotOutHistory(int start, int size, String plateNo, SingleCarparkCarpark carpark) {
		return carparkInOutService.searchNotOutHistory(start, size, plateNo, carpark);
	}



	@Override
	public List<SingleCarparkInOutHistory> findHistoryByIn(int start, int size, SingleCarparkCarpark carpark, String carType, Date startTime, Date endTime) {
		return carparkInOutService.findHistoryByIn(start, size, carpark, carType, startTime, endTime);
	}



	@Override
	public boolean exeUpdateSql(String sql) {
		return carparkInOutService.exeUpdateSql(sql);
	}



	@Override
	public List<SingleCarparkInOutHistory> findHistoryThanId(Long id, int start, int size) {
		return carparkInOutService.findHistoryThanId(id, start, size);
	}



	@Override
	public List<CarPayHistory> findCarPayHistoryThanId(Long id, int start, int size) {
		return carparkInOutService.findCarPayHistoryThanId(id, start, size);
	}



	@Override
	public List<Double> countReturnMoney(String userName) {
		return carparkInOutService.countReturnMoney(userName);
	}



	@Override
	public Long updateRecount(Long maxId, Long returnAccountId, boolean free) {
		return carparkInOutService.updateRecount(maxId, returnAccountId, free);
	}
}