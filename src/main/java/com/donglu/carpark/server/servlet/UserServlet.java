package com.donglu.carpark.server.servlet;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.service.SystemOperaLogServiceI;
import com.donglu.carpark.service.SystemUserServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.server.util.HibernateSerializerFactory;
import com.google.inject.Inject;

public class UserServlet extends HessianServlet implements CarparkUserService, SystemUserServiceI,SystemOperaLogServiceI {
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
		return carparkUserService.deleteUser(user);
	}

	@Override
	public List<SingleCarparkUser> findAll() {
		return carparkUserService.findAll();
	}

	@Override
	public List<SingleCarparkUser> findByNameOrPlateNo(String name, String plateNo, int willOverdue, String overdue) {
		return carparkUserService.findByNameOrPlateNo(name, plateNo, willOverdue, overdue);
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

}
