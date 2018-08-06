package com.donglu.carpark.server.servlet;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.criteria4jpa.CriteriaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.service.SettingService;
import com.donglu.carpark.service.SystemOperaLogServiceI;
import com.donglu.carpark.service.SystemUserServiceI;
import com.dongluhitec.card.domain.WithID;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkAccountCar;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UserHistory;
import com.dongluhitec.card.server.util.HibernateSerializerFactory;
import com.google.inject.Inject;

public class UserServlet extends BaseDaoServlet implements CarparkUserService, SystemUserServiceI,SystemOperaLogServiceI,SettingService{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6754128674765616314L;
	private Logger LOGGER = LoggerFactory.getLogger(UserServlet.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;

	private CarparkUserService carparkUserService;
	private SystemUserServiceI systemUserService;
	private SystemOperaLogServiceI systemOperaLogService;
	private SettingService settingService;


    @Override
    public void init() throws ServletException {
        try {
        	getSerializerFactory().addFactory(new HibernateSerializerFactory());
        	sp.start();
        } catch (Exception e) {
            LOGGER.error("Cannot start service provider in the CardUsageServlet engine", e);
            throw new ServletException("Cannot start service provider in the servlet engine");
        }
        this.carparkUserService = sp.getCarparkUserService();
        this.systemUserService = sp.getSystemUserService();
        this.systemOperaLogService = sp.getSystemOperaLogService();
        settingService=sp.getSettingService();
        settingService.initData();
        baseDaoService=carparkUserService;
        
    }
	
	@Override
	public List<SingleCarparkSystemUser> findAllSystemUser() {
		return systemUserService.findAllSystemUser();
	}

	@Override
	public SingleCarparkSystemUser findByNameAndPassword(String userName, String password) {
		return systemUserService.findByNameAndPassword(userName, password);
	}

	@Override
	public Long removeSystemUser(SingleCarparkSystemUser systemUser) {
		return systemUserService.removeSystemUser(systemUser);
	}

	@Override
	public Long saveSystemUser(SingleCarparkSystemUser systemUser) {
		return systemUserService.saveSystemUser(systemUser);
	}

	@Override
	public void initSystemInfo() {
		systemUserService.initSystemInfo();
	}

	@Override
	public Long saveUser(SingleCarparkUser user) {
		return carparkUserService.saveUser(user);
	}

	@Override
	public Long deleteUser(SingleCarparkUser user) {
		Long deleteUser = carparkUserService.deleteUser(user);
		return deleteUser;
	}

	@Override
	public List<SingleCarparkUser> findAll() {
		return carparkUserService.findAll();
	}

	@Override
	public List<SingleCarparkUser> findByNameOrPlateNo(String name, String plateNo,String address,SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue) {
		return carparkUserService.findByNameOrPlateNo(name, plateNo,address, monthlyCharge, willOverdue, overdue);
	}

	@Override
	public SingleCarparkUser findUserByPlateNo(String plateNO, Long carparkId) {
		return carparkUserService.findUserByPlateNo(plateNO, carparkId);
	}

	@Override
	public List<SingleCarparkUser> findUserByMonthChargeId(Long id) {
		return carparkUserService.findUserByMonthChargeId(id);
	}

	@Override
	public Long saveUserByMany(List<SingleCarparkUser> list) {
		return carparkUserService.saveUserByMany(list);
	}

	@Override
	public List<SingleCarparkUser> findUserThanIdMore(Long id, List<Long> errorIds) {
		return carparkUserService.findUserThanIdMore(id, errorIds);
	}

	@Override
	public List<SingleCarparkLockCar> findLockCarByPlateNO(String plateNO) {
		return carparkUserService.findLockCarByPlateNO(plateNO);
	}

	@Override
	public Long saveLockCar(SingleCarparkLockCar lc) {
		return carparkUserService.saveLockCar(lc);
	}

	@Override
	public Long deleteLockCar(SingleCarparkLockCar lc) {
		return carparkUserService.deleteLockCar(lc);
	}

	@Override
	public Long savePrepaidUserPayHistory(SingleCarparkPrepaidUserPayHistory pph) {
		return carparkUserService.savePrepaidUserPayHistory(pph);
	}

	@Override
	public List<SingleCarparkPrepaidUserPayHistory> findPrepaidUserPayHistoryList(int begin, int max, String userName, String plateNO, Date start, Date end) {
		return carparkUserService.findPrepaidUserPayHistoryList(begin, max, userName, plateNO, start, end);
	}

	@Override
	public int countPrepaidUserPayHistoryList(String userName, String plateNO, Date start, Date end) {
		return carparkUserService.countPrepaidUserPayHistoryList(userName, plateNO, start, end);
	}

	@Override
	public void saveOperaLog(SystemOperaLogTypeEnum type, String content,String operaName) {
		systemOperaLogService.saveOperaLog(type, content,operaName);
	}

	@Override
	public List<SingleCarparkSystemOperaLog> findBySearch(String operaName, Date start, Date end, SystemOperaLogTypeEnum type) {
		return systemOperaLogService.findBySearch(operaName, start, end, type);
	}

	@Override
	public void saveOperaLog(SystemOperaLogTypeEnum systemOperaLogType, String content, byte[] bigImage, String operaName, Object... objects) {
		systemOperaLogService.saveOperaLog(systemOperaLogType, content, bigImage, operaName, objects);
	}

	@Override
	public SingleCarparkUser findUserById(Long userId) {
		return carparkUserService.findUserById(userId);
	}

	@Override
	public List<File> getServerChildFiles(String file) {
		return settingService.getServerChildFiles(file);
	}

	@Override
	public boolean backupDataBase(String filePath) {
		return settingService.backupDataBase(filePath);
	}

	@Override
	public int restoreDataBase(String filePath) {
		return settingService.restoreDataBase(filePath);
	}

	@Override
	public boolean createServerDirectory(String path) {
		return settingService.createServerDirectory(path);
	}

	@Override
	public boolean createServerFile(String path) {
		return settingService.createServerFile(path);
	}

	@Override
	public List<SingleCarparkUser> findAllUserByPlateNO(String plateNO, Long carparkId, Date validTo) {
		return carparkUserService.findAllUserByPlateNO(plateNO, carparkId, validTo);
	}

	@Override
	public int sumAllUserSlotByPlateNO(String plateNO, Long carparkId, Date validTo) {
		return carparkUserService.sumAllUserSlotByPlateNO(plateNO, carparkId, validTo);
	}

	@Override
	public List<SingleCarparkUser> findUserByNameAndCarpark(String name, SingleCarparkCarpark carpark, Date validTo) {
		return carparkUserService.findUserByNameAndCarpark(name, carpark, validTo);
	}

	@Override
	public SingleCarparkUser findUserByParkingSpace(String parkingSpace) {
		return carparkUserService.findUserByParkingSpace(parkingSpace);
	}

	@Override
	public List<SingleCarparkUser> findUserByPlateNoLikeSize(int start, int size, String plateNO, int likeSize, Long carparkId, Date validTo) {
		return carparkUserService.findUserByPlateNoLikeSize(start, size, plateNO, likeSize, carparkId, validTo);
	}

	@Override
	public void initCarpark() {
		
	}

	@Override
	public void login(String userName, String password, String ip) {
		systemUserService.login(userName, password, ip);		
	}

	@Override
	public void loginOut(String userName) {
		systemUserService.loginOut(userName);
	}

	@Override
	public String loginStatus(String userName) {
		return systemUserService.loginStatus(userName);
	}

	@Override
	public List<SingleCarparkUser> findByNameOrPlateNo(int start, int max, String name, String plateNo, String address, SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue) {
		return carparkUserService.findByNameOrPlateNo(start, max, name, plateNo, address, monthlyCharge, willOverdue, overdue);
	}

	@Override
	public Long countByNameOrPlateNo(String name, String plateNo, String address, SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue) {
		return carparkUserService.countByNameOrPlateNo(name, plateNo, address, monthlyCharge, willOverdue, overdue);
	}

	@Override
	public List<SingleCarparkSystemOperaLog> findSystemOperaLogBySearch(int startSize, int size, String operaName, Date start, Date end, SystemOperaLogTypeEnum type) {
		return systemOperaLogService.findSystemOperaLogBySearch(startSize, size, operaName, start, end, type);
	}

	@Override
	public Long countSystemOperaLogBySearch(String operaName, Date start, Date end, SystemOperaLogTypeEnum type) {
		return systemOperaLogService.countSystemOperaLogBySearch(operaName, start, end, type);
	}

	@Override
	public List<UserHistory> findUserHistory(UpdateEnum[] updates, ProcessEnum[] processEnums) {
		return carparkUserService.findUserHistory(updates, processEnums);
	}

	@Override
	public Long updateUserHistory(UserHistory history, ProcessEnum process) {
		return carparkUserService.updateUserHistory(history, process);
	}

	@Override
	public Date getServerDate() {
		return settingService.getServerDate();
	}

	@Override
	public List<SingleCarparkUser> findUserByNameOrCarpark(String name, SingleCarparkCarpark carpark, Date validTo) {
		return carparkUserService.findUserByNameOrCarpark(name, carpark, validTo);
	}

	@Override
	public void initData() {
		settingService.initData();
	}

	@Override
	public List<SingleCarparkUser> findOverdueUserByLastEditTime(int start, int max, Date startTime, Date endTime) {
		return carparkUserService.findOverdueUserByLastEditTime(start, max, startTime, endTime);
	}

	@Override
	public List<CarparkAccountCar> findAccountCard(List<QueryParameter> parameters) {
		return carparkUserService.findAccountCard(parameters);
	}

	@Override
	public int countAccountCar(List<QueryParameter> parameters) {
		return carparkUserService.countAccountCar(parameters);
	}

	@Override
	public List<String> findPrivilegeByName(String userName,String password) {
		return systemUserService.findPrivilegeByName(userName,password);
	}
}
