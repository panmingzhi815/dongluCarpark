package com.donglu.carpark.service.impl;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.derby.vti.Restriction;
import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.order.Order;
import org.criteria4jpa.projection.Projections;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkService;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationPrice;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.MapperConfig;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.dongluhitec.card.service.impl.SettingServiceImpl;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class CarparkServiceImpl implements CarparkService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingServiceImpl.class);

	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	@Inject
	private MapperConfig mapper;

	@Transactional
	public Long saveCarpark(SingleCarparkCarpark carpark) {
		DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
		if (carpark.getId() == null) {
			dom.insert(carpark);
		} else {
			dom.save(carpark);
		}
		return carpark.getId();
	}

	@Transactional
	public Long deleteCarpark(SingleCarparkCarpark carpark) {
		DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
		dom.remove(carpark.getId());
		return carpark.getId();
	}

	public List<SingleCarparkCarpark> findAllCarpark() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	public List<SingleCarparkCarpark> findCarparkToLevel() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			List<SingleCarparkCarpark> resultList = c.getResultList();
			
			for (SingleCarparkCarpark singleCarparkCarpark : resultList) {
				singleCarparkCarpark.setChilds(new ArrayList<>());
			}
			for (SingleCarparkCarpark carpark : resultList) {
				if (carpark.getParent() != null) {
					carpark.getParent().getChilds().add(carpark);
				}
			}
			Collection<SingleCarparkCarpark> filter = Collections2.filter(resultList, new Predicate<SingleCarparkCarpark>() {
				public boolean apply(SingleCarparkCarpark arg0) {
					return arg0.getParent() == null;
				}
			});

			List<SingleCarparkCarpark> list = new ArrayList<>(filter);
			return list;
		} finally {
			unitOfWork.end();
		}
	}

	public SingleCarparkCarpark findCarparkTopLevel() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			c.add(Restrictions.isNull("parent"));
			return (SingleCarparkCarpark) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge) {
		DatabaseOperation<SingleCarparkMonthlyCharge> dom = DatabaseOperation.forClass(SingleCarparkMonthlyCharge.class, emprovider.get());
		if (monthlyCharge.getId() == null) {
			dom.insert(monthlyCharge);
		} else {
			dom.save(monthlyCharge);
		}
		return monthlyCharge.getId();
	}

	@Override
	@Transactional
	public Long deleteMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge) {
		DatabaseOperation<SingleCarparkMonthlyCharge> dom = DatabaseOperation.forClass(SingleCarparkMonthlyCharge.class, emprovider.get());
		dom.remove(monthlyCharge);
		return monthlyCharge.getId();
	}

	@Override
	public List<SingleCarparkMonthlyCharge> findAllMonthlyCharge() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyCharge.class);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkMonthlyCharge> findMonthlyChargeByCarpark(SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyCharge.class);
			if (!StrUtil.isEmpty(carpark)) {
				c.add(Restrictions.eq("carpark", carpark));
			}
			
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveCarparkDevice(SingleCarparkDevice device) {
		DatabaseOperation<SingleCarparkDevice> dom = DatabaseOperation.forClass(SingleCarparkDevice.class, emprovider.get());
		if (device.getId() == null) {
			dom.insert(device);
		} else {
			dom.save(device);
		}
		return device.getId();
	}

	@Override
	public Long deleteDevice(SingleCarparkDevice device) {
		DatabaseOperation<SingleCarparkDevice> dom = DatabaseOperation.forClass(SingleCarparkDevice.class, emprovider.get());
		dom.remove(device);
		return device.getId();
	}

	@Override
	public List<SingleCarparkDevice> findAll() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkDevice.class);
			c.addOrder(Order.asc("identifire"));
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h) {
		DatabaseOperation<SingleCarparkMonthlyUserPayHistory> dom = DatabaseOperation.forClass(SingleCarparkMonthlyUserPayHistory.class, emprovider.get());
		if (h.getId() == null) {
			dom.insert(h);
		} else {
			dom.save(h);
		}
		return h.getId();
	}

	@Override
	public Long deleteMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h) {
		DatabaseOperation<SingleCarparkMonthlyUserPayHistory> dom = DatabaseOperation.forClass(SingleCarparkMonthlyUserPayHistory.class, emprovider.get());
		dom.remove(h);
		return h.getId();
	}


	@Transactional
	public Long deleteMonthlyCharge(Long id) {
		DatabaseOperation<SingleCarparkMonthlyCharge> dom = DatabaseOperation.forClass(SingleCarparkMonthlyCharge.class, emprovider.get());
		dom.remove(id);
		return id;
	}

	@Override
	public List<SingleCarparkSystemSetting> findAllSystemSetting() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemSetting.class);
			
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveSystemSetting(SingleCarparkSystemSetting h) {
		Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemSetting.class);
		c.add(Restrictions.eq("settingKey", h.getSettingKey()));
		SingleCarparkSystemSetting set = (SingleCarparkSystemSetting) c.getSingleResultOrNull();
		
		DatabaseOperation<SingleCarparkSystemSetting> dom = DatabaseOperation.forClass(SingleCarparkSystemSetting.class, emprovider.get());
		if (set!=null) {
			h.setId(set.getId());
		}
		if (h.getId() == null) {
			dom.insert(h);
		} else {
			dom.save(h);
		}
		return h.getId();
	}

	@Override
	public SingleCarparkSystemSetting findSystemSettingByKey(String key) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemSetting.class);
			c.add(Restrictions.eq("settingKey", key));
			SingleCarparkSystemSetting set = (SingleCarparkSystemSetting) c.getSingleResultOrNull();
			return set;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public CarparkChargeStandard findCarparkChargeStandardByCode(String code, SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria criteria = CriteriaUtils.createCriteria(emprovider.get(),
					CarparkChargeStandard.class);
			criteria.add(Restrictions.eq(CarparkChargeStandard.Property.code.name(), code));
			criteria.add(Restrictions.eq("carpark", carpark));
			Object singleResultOrNull = criteria.getSingleResultOrNull();
			if (StrUtil.isEmpty(singleResultOrNull)) {
				return null;
			}
			CarparkChargeStandard c=(CarparkChargeStandard) singleResultOrNull;
			List<CarparkDurationStandard> carparkDurationStandards = c.getCarparkDurationStandards();
			for (CarparkDurationStandard carparkDurationStandard : carparkDurationStandards) {
				carparkDurationStandard.getCarparkDurationPriceList().size();
			}
			return c ;
		} catch (Exception e) {
			throw new DongluServiceException("查找临时收费标准失败!", e);
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<CarparkCarType> getCarparkCarTypeList() {
		unitOfWork.begin();
		try {
			Criteria criteria = CriteriaUtils.createCriteria(emprovider.get(),
					CarparkCarType.class);
			criteria.addOrder(Order.asc("id"));
			return criteria.getResultList();
		} catch (Exception e) {
			throw new DongluServiceException("获取停车场车类型列表失败!", e);
		} finally {
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveCarparkChargeStandard(CarparkChargeStandard carparkChargeStandard) {
		DatabaseOperation<CarparkChargeStandard> dom = DatabaseOperation
				.forClass(CarparkChargeStandard.class, emprovider.get());
		if (carparkChargeStandard.getId() == null) {
			dom.insert(carparkChargeStandard);
		} else {
			// 如果更改了新的时段,则先删除旧的时段
			CarparkChargeStandard carparkChargeStandard1 = emprovider.get()
					.find(CarparkChargeStandard.class,
							carparkChargeStandard.getId());
			if (carparkChargeStandard.getCarparkDurationStandards() != null) {
				List<CarparkDurationStandard> carparkDurationStandards = carparkChargeStandard1
						.getCarparkDurationStandards();
				for (CarparkDurationStandard carparkDurationStandard : carparkDurationStandards) {
					emprovider.get().remove(carparkDurationStandard);
				}
			}
			dom.save(carparkChargeStandard);
		}
		return carparkChargeStandard.getId();
	}

	@Override
	public List<CarparkChargeStandard> findTempChargeByCarpark(SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), CarparkChargeStandard.class);
			c.add(Restrictions.eq("carpark", carpark));
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Transactional
	public Long deleteTempCharge(Long id) {
		DatabaseOperation<CarparkChargeStandard> dom = DatabaseOperation.forClass(CarparkChargeStandard.class, emprovider.get());
		dom.remove(id);
		return id;
		
	}

	@Override
	public List<SingleCarparkMonthlyUserPayHistory> findMonthlyUserPayHistoryByCondition(int maxResult, int size, String userName, String operaName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyUserPayHistory.class);
			createCriteriaBySingleCarparkMonthlyUserPayHistory(c,userName,operaName,start,end);
			c.setFirstResult(maxResult);
			c.setMaxResults(size);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	private void createCriteriaBySingleCarparkMonthlyUserPayHistory(Criteria c, String userName, String operaName, Date start, Date end) {
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.userName.name(), userName,MatchMode.START),
					Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.userName.name(), userName,MatchMode.END)));
		}
		if (!StrUtil.isEmpty(operaName)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.operaName.name(), operaName,MatchMode.START),
					Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.operaName.name(), operaName,MatchMode.END)));
		}
		if (!StrUtil.isEmpty(start)) {
			Date todayTopTime = StrUtil.getTodayTopTime(start);
			c.add(Restrictions.ge(SingleCarparkMonthlyUserPayHistory.Property.createTime.name(), todayTopTime));
		}
		if (!StrUtil.isEmpty(end)) {
			Date todayTopTime = StrUtil.getTodayBottomTime(end);
			c.add(Restrictions.le(SingleCarparkMonthlyUserPayHistory.Property.createTime.name(), todayTopTime));
		}
		
	}

	@Override
	public int countMonthlyUserPayHistoryByCondition(String userName, String operaName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyUserPayHistory.class);
			createCriteriaBySingleCarparkMonthlyUserPayHistory(c,userName,operaName,start,end);
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			return singleResultOrNull.intValue();
		} finally{
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveReturnAccount(SingleCarparkReturnAccount a) {
		DatabaseOperation<SingleCarparkReturnAccount> dom = DatabaseOperation
				.forClass(SingleCarparkReturnAccount.class, emprovider.get());
		if (a.getId() == null) {
			dom.insert(a);
		} else {
			dom.save(a);
		}
		return a.getId();
		
	}

	@Override
	public SingleCarparkCarpark findCarparkById(Long id) {
		unitOfWork.begin();
		try {
    		DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation
    				.forClass(SingleCarparkCarpark.class, emprovider.get());
    		SingleCarparkCarpark entityWithId = dom.getEntityWithId(id);
    		return entityWithId;
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkReturnAccount> findReturnAccountByCondition(int max, int size, String userName, String operaName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c=
			createCriteriaBySingleCarparkReturnAccount(userName,operaName,start,end);
			c.setFirstResult(max);
			c.setMaxResults(size);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public int countReturnAccountByCondition(String userName, String operaName, Date start, Date end) {
		
		unitOfWork.begin();
		try {
			Criteria c=
			createCriteriaBySingleCarparkReturnAccount(userName,operaName,start,end);
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			return singleResultOrNull.intValue();
		} finally{
			unitOfWork.end();
		}
	}

	private Criteria createCriteriaBySingleCarparkReturnAccount(String userName, String operaName, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkReturnAccount.class);
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkReturnAccount.Property.returnUser.name(), userName,MatchMode.START),
					Restrictions.like(SingleCarparkReturnAccount.Property.returnUser.name(), userName,MatchMode.END)));
		}
		if (!StrUtil.isEmpty(operaName)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkReturnAccount.Property.operaName.name(), operaName,MatchMode.START),
					Restrictions.like(SingleCarparkReturnAccount.Property.operaName.name(), operaName,MatchMode.END)));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(SingleCarparkReturnAccount.Property.returnTime.name(), StrUtil.getTodayTopTime(start)));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(SingleCarparkReturnAccount.Property.returnTime.name(), StrUtil.getTodayBottomTime(end)));
		}
		return c;
	}

	@Override
	public SingleCarparkMonthlyCharge findMonthlyChargeById(Long id) {
		unitOfWork.begin();
		try {
			DatabaseOperation<SingleCarparkMonthlyCharge> dom = DatabaseOperation.forClass(SingleCarparkMonthlyCharge.class, emprovider.get());
			SingleCarparkMonthlyCharge entityWithId = dom.getEntityWithId(id);
			return entityWithId;
		} finally{
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveBlackUser(SingleCarparkBlackUser b) {
		DatabaseOperation<SingleCarparkBlackUser> dom = DatabaseOperation.forClass(SingleCarparkBlackUser.class, emprovider.get());
		if (b.getId()==null) {
			dom.insert(b);
		}else{
			dom.save(b);
		}
		return b.getId();
	}

	@Transactional
	public Long deleteBlackUser(SingleCarparkBlackUser b) {
		DatabaseOperation<SingleCarparkBlackUser> dom = DatabaseOperation.forClass(SingleCarparkBlackUser.class, emprovider.get());
		Long id = b.getId();
		dom.remove(id);
		return id;
	}

	@Override
	public List<SingleCarparkBlackUser> findAllBlackUser() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkBlackUser.class);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<Holiday> findHolidayByYear(int year) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), Holiday.class);
			Calendar cd=Calendar.getInstance();
			cd.set(year, 1, 1);
			Date time = cd.getTime();
			c.add(Restrictions.between("start", StrUtil.getYearTopTime(time), StrUtil.getYearBottomTime(time)));
			List<Holiday> resultList = c.getResultList();
			return resultList;
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<>();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public Holiday findHolidayByDate(Date date) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), Holiday.class);
			c.add(Restrictions.eq("start", date));
			return (Holiday) c.getSingleResultOrNull();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			unitOfWork.end();
		}
	}
	
	@Transactional
	public Long deleteHoliday(List<Holiday> list) {
		DatabaseOperation<Holiday> dom = DatabaseOperation.forClass(Holiday.class, emprovider.get());
		for (Holiday h:list) {
			dom.remove(h.getId());
		}
		return list.size()*1L;
	}

	@Transactional
	public Long saveHoliday(List<Holiday> list) {
		DatabaseOperation<Holiday> dom = DatabaseOperation.forClass(Holiday.class, emprovider.get());
		for (Holiday b : list) {
			if (b.getId()==null) {
				dom.insert(b);
			}else{
				dom.save(b);
			}
		}
		return list.size()*1L;
	}
	
	@Override
	public float calculateTempCharge(Long carparkId,final Long carTypeId,
			final Date startTime, final Date endTime) {
		try {
			unitOfWork.begin();
			Session unwrap = emprovider.get().unwrap(Session.class);
			Float o = unwrap.doReturningWork(new ReturningWork<Float>() {
				@Override
				public Float execute(Connection conn) throws SQLException {
					String spName = "{call upGetNewPakCarCharge(?,?,?,?)}";

					CallableStatement proc = conn
							.prepareCall("{call upGetNewPakCarCharge(?,?,?,?,?)}");
					proc.setBigDecimal(1, new BigDecimal(carparkId));
					proc.setString(2, carTypeId + "");
					proc.setTimestamp(3, new Timestamp(startTime.getTime()));
					proc.setTimestamp(4, new Timestamp(endTime.getTime()));
					proc.registerOutParameter(5, Types.NUMERIC);
					proc.execute();
					proc.getMoreResults();
					float money = proc.getFloat(5);
					proc.close();
					conn.close();
					return money;
				}
			});
			LOGGER.info(
					"计算临时收费成功:carTypeId={},startTime={},endTime={},money={}",
					carTypeId, StrUtil.formatDateTime(startTime),
					StrUtil.formatDateTime(endTime), o);
			return o;
		} catch (Exception e) {
			LOGGER.error("计算临时收费失败:carTypeId={},startTime={},endTime={}",
					carTypeId, StrUtil.formatDateTime(startTime),
					StrUtil.formatDateTime(endTime));
			throw new DongluServiceException("计算临时收费金额失败", e);
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkBlackUser findBlackUserByPlateNO(String plateNO) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkBlackUser.class);
			c.add(Restrictions.eq(SingleCarparkBlackUser.Property.plateNO.name(), plateNO));
			return (SingleCarparkBlackUser) c.getSingleResultOrNull();
		}catch(Exception e){
			return null;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public int countMonthUserByHaveCarSite() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			c.add(Restrictions.isNotNull("carparkNo"));
			c.setProjection(Projections.rowCount());
			return ((Long)c.getSingleResultOrNull()).intValue();
		}catch(Exception e){
			return 0;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<CarparkChargeStandard> findCarparkTempCharge(long l) {
		unitOfWork.begin();
		try {
			DatabaseOperation<CarparkCarType> dom = DatabaseOperation.forClass(CarparkCarType.class, emprovider.get());
			CarparkCarType entityWithId = dom.getEntityWithId(l);
			List<CarparkChargeStandard> carparkChargeStandardList = entityWithId.getCarparkChargeStandardList();
			for (CarparkChargeStandard carparkChargeStandard : carparkChargeStandardList) {
				List<CarparkDurationStandard> carparkDurationStandards = carparkChargeStandard.getCarparkDurationStandards();
				for (CarparkDurationStandard carparkDurationStandard : carparkDurationStandards) {
					List<CarparkDurationPrice> carparkDurationPriceList = carparkDurationStandard.getCarparkDurationPriceList();
					for (CarparkDurationPrice carparkDurationPrice : carparkDurationPriceList) {
						carparkDurationPrice.getId();
					}
				}
			}
			return carparkChargeStandardList;
		} catch (Exception e) {
			e.printStackTrace();
			return Lists.newArrayList();
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkMonthlyCharge findMonthlyChargeByCode(String code, SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyCharge.class);
			c.add(Restrictions.eq("chargeCode", code));
			c.add(Restrictions.eq("carpark", carpark));
			return (SingleCarparkMonthlyCharge) c.getSingleResultOrNull();
		}catch(Exception e){
			return null;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<CarparkChargeStandard> findAllCarparkChargeStandard() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), CarparkChargeStandard.class);
			c.add(Restrictions.eq("using", true));
			return c.getResultList();
		}catch(Exception e){
			return new ArrayList<>();
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkCarpark findCarparkByCode(String code) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			c.add(Restrictions.eq("code", code));
			return (SingleCarparkCarpark) c.getSingleResultOrNull();
		}catch(Exception e){
			return null;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public Map<SNSettingType, SingleCarparkSystemSetting> findAllSN() {
		unitOfWork.begin();
		try {
			Map<SNSettingType, SingleCarparkSystemSetting> map=new HashMap<>();
			for (SNSettingType t:SNSettingType.values()) {
				Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemSetting.class);
				c.add(Restrictions.eq("settingKey", t.name()));
				SingleCarparkSystemSetting ss = (SingleCarparkSystemSetting) c.getSingleResultOrNull();
				map.put(t, ss==null?null:ss);
			}
			return map;
		}catch(Exception e){
			return null;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkCarpark> findSameCarpark(SingleCarparkCarpark carpark) {
		List<SingleCarparkCarpark> findCarparkToLevel = findCarparkToLevel();
		for (SingleCarparkCarpark singleCarparkCarpark : findCarparkToLevel) {
			List<SingleCarparkCarpark> list=new ArrayList<>();
			getCarpaek(singleCarparkCarpark, list);
			System.out.println(list);
			if (list.contains(carpark)) {
				return list;
			}
		}
		return null;
	}
	private void getCarpaek(SingleCarparkCarpark carpark, List<SingleCarparkCarpark> list) {
		list.add(carpark);
		if (!StrUtil.isEmpty(carpark.getChilds())) {
			for (SingleCarparkCarpark singleCarparkCarpark : carpark.getChilds()) {
				getCarpaek(singleCarparkCarpark,list);
			}
		}
	}

	@Transactional
	public void changeChargeStandardState(Long id, boolean b) {
		DatabaseOperation<CarparkChargeStandard> dom = DatabaseOperation.forClass(CarparkChargeStandard.class, emprovider.get());
		CarparkChargeStandard entityWithId = dom.getEntityWithId(id);
		entityWithId.setUsing(b);
	}
		
}
