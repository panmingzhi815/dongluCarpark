package com.donglu.carpark.server.servlet;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.server.util.HibernateSerializerFactory;
import com.google.inject.Inject;

public class CarparkServlet extends HessianServlet implements CarparkService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2647002887391917387L;
	private Logger LOGGER = LoggerFactory.getLogger(UserServlet.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private CarparkService carparkService;



    @Override
    public void init() throws ServletException {
        try {
        	getSerializerFactory().addFactory(new HibernateSerializerFactory());
        	sp.start();
        } catch (Exception e) {
            LOGGER.error("Cannot start service provider in the CardUsageServlet engine", e);
            throw new ServletException("Cannot start service provider in the servlet engine");
        }
        this.carparkService = sp.getCarparkService();
    }



	@Override
	public Long saveCarpark(SingleCarparkCarpark carpark) {
		return carparkService.saveCarpark(carpark);
	}



	@Override
	public Long deleteCarpark(SingleCarparkCarpark carpark) {
		return carparkService.deleteCarpark(carpark);
	}



	@Override
	public List<SingleCarparkCarpark> findAllCarpark() {
		return carparkService.findAllCarpark();
	}



	@Override
	public List<SingleCarparkCarpark> findCarparkToLevel() {
		return carparkService.findCarparkToLevel();
	}



	@Override
	public SingleCarparkCarpark findCarparkTopLevel() {
		return carparkService.findCarparkTopLevel();
	}



	@Override
	public Long saveMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge) {
		return carparkService.saveMonthlyCharge(monthlyCharge);
	}



	@Override
	public Long deleteMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge) {
		return carparkService.deleteMonthlyCharge(monthlyCharge);
	}



	@Override
	public Long deleteMonthlyCharge(Long id) {
		return carparkService.deleteMonthlyCharge(id);
	}



	@Override
	public List<SingleCarparkMonthlyCharge> findAllMonthlyCharge() {
		return carparkService.findAllMonthlyCharge();
	}



	@Override
	public List<SingleCarparkMonthlyCharge> findMonthlyChargeByCarpark(SingleCarparkCarpark carpark) {
		return carparkService.findMonthlyChargeByCarpark(carpark);
	}



	@Override
	public Long saveCarparkDevice(SingleCarparkDevice device) {
		return carparkService.saveCarparkDevice(device);
	}



	@Override
	public Long deleteDevice(SingleCarparkDevice device) {
		return carparkService.deleteDevice(device);
	}



	@Override
	public List<SingleCarparkDevice> findAll() {
		return carparkService.findAll();
	}



	@Override
	public Long saveMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h) {
		return carparkService.saveMonthlyUserPayHistory(h);
	}



	@Override
	public Long deleteMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h) {
		return carparkService.deleteMonthlyUserPayHistory(h);
	}



	@Override
	public List<SingleCarparkSystemSetting> findAllSystemSetting() {
		return carparkService.findAllSystemSetting();
	}



	@Override
	public Long saveSystemSetting(SingleCarparkSystemSetting h) {
		return carparkService.saveSystemSetting(h);
	}



	@Override
	public SingleCarparkSystemSetting findSystemSettingByKey(String key) {
		return carparkService.findSystemSettingByKey(key);
	}



	@Override
	public CarparkChargeStandard findCarparkChargeStandardByCode(String code, SingleCarparkCarpark carpark) {
		return carparkService.findCarparkChargeStandardByCode(code, carpark);
	}



	@Override
	public List<CarparkCarType> getCarparkCarTypeList() {
		return carparkService.getCarparkCarTypeList();
	}



	@Override
	public Long saveCarparkChargeStandard(CarparkChargeStandard carparkChargeStandard) {
		return carparkService.saveCarparkChargeStandard(carparkChargeStandard);
	}



	@Override
	public List<CarparkChargeStandard> findTempChargeByCarpark(SingleCarparkCarpark carpark) {
		return carparkService.findTempChargeByCarpark(carpark);
	}



	@Override
	public Long deleteTempCharge(Long id) {
		return carparkService.deleteTempCharge(id);
	}



	@Override
	public List<SingleCarparkMonthlyUserPayHistory> findMonthlyUserPayHistoryByCondition(int maxResult, int size, String userName, String operaName, Date start, Date end) {
		return carparkService.findMonthlyUserPayHistoryByCondition(maxResult, size, userName, operaName, start, end);
	}



	@Override
	public int countMonthlyUserPayHistoryByCondition(String userName, String operaName, Date start, Date end) {
		return carparkService.countMonthlyUserPayHistoryByCondition(userName, operaName, start, end);
	}



	@Override
	public Long saveReturnAccount(SingleCarparkReturnAccount a) {
		return carparkService.saveReturnAccount(a);
	}



	@Override
	public SingleCarparkCarpark findCarparkById(Long id) {
		return carparkService.findCarparkById(id);
	}



	@Override
	public List<SingleCarparkReturnAccount> findReturnAccountByCondition(int size, int i, String userName, String operaName, Date start, Date end) {
		return carparkService.findReturnAccountByCondition(size, i, userName, operaName, start, end);
	}



	@Override
	public int countReturnAccountByCondition(String userName, String operaName, Date start, Date end) {
		return carparkService.countReturnAccountByCondition(userName, operaName, start, end);
	}



	@Override
	public SingleCarparkMonthlyCharge findMonthlyChargeById(Long id) {
		return carparkService.findMonthlyChargeById(id);
	}



	@Override
	public Long saveBlackUser(SingleCarparkBlackUser b) {
		return carparkService.saveBlackUser(b);
	}



	@Override
	public Long deleteBlackUser(SingleCarparkBlackUser b) {
		return carparkService.deleteBlackUser(b);
	}



	@Override
	public List<SingleCarparkBlackUser> findAllBlackUser() {
		return carparkService.findAllBlackUser();
	}



	@Override
	public List<Holiday> findHolidayByYear(int year) {
		return carparkService.findHolidayByYear(year);
	}



	@Override
	public Long deleteHoliday(List<Holiday> list) {
		return carparkService.deleteHoliday(list);
	}



	@Override
	public Long saveHoliday(List<Holiday> list) {
		return carparkService.saveHoliday(list);
	}



	@Override
	public float calculateTempCharge(Long carparkId, Long carTypeId, Date startTime, Date endTime) {
		return carparkService.calculateTempCharge(carparkId, carTypeId, startTime, endTime);
	}



	@Override
	public SingleCarparkBlackUser findBlackUserByPlateNO(String plateNO) {
		return carparkService.findBlackUserByPlateNO(plateNO);
	}



	@Override
	public int countMonthUserByHaveCarSite() {
		return carparkService.countMonthUserByHaveCarSite();
	}



	@Override
	public List<CarparkChargeStandard> findCarparkTempCharge(long l) {
		return carparkService.findCarparkTempCharge(l);
	}



	@Override
	public Holiday findHolidayByDate(Date date) {
		return carparkService.findHolidayByDate(date);
	}



	@Override
	public SingleCarparkMonthlyCharge findMonthlyChargeByCode(String code, SingleCarparkCarpark carpark) {
		return carparkService.findMonthlyChargeByCode(code, carpark);
	}



	@Override
	public List<CarparkChargeStandard> findAllCarparkChargeStandard(SingleCarparkCarpark carpark, Boolean using) {
		return carparkService.findAllCarparkChargeStandard(carpark, using);
	}



	@Override
	public SingleCarparkCarpark findCarparkByCode(String code) {
		return carparkService.findCarparkByCode(code);
	}



	@Override
	public Map<SNSettingType, SingleCarparkSystemSetting> findAllSN() {
		return carparkService.findAllSN();
	}



	@Override
	public List<SingleCarparkCarpark> findSameCarpark(SingleCarparkCarpark carpark) {
		return carparkService.findSameCarpark(carpark);
	}



	@Override
	public void changeChargeStandardState(Long id, boolean b) {
		carparkService.changeChargeStandardState(id, b);
	}



	@Override
	public String getSystemSettingValue(SystemSettingTypeEnum settingType) {
		return carparkService.getSystemSettingValue(settingType);
	}



	@Override
	public List<SingleCarparkDeviceVoice> findAllVoiceInfo() {
		return carparkService.findAllVoiceInfo();
	}
}