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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.criterion.SimpleExpression;
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
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor.VisitorStatus;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.dongluhitec.card.service.impl.SettingServiceImpl;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;
@SuppressWarnings("unchecked")
public class CarparkServiceImpl implements CarparkService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingServiceImpl.class);

	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;


	@Override
	@Transactional
	public Long saveCarpark(SingleCarparkCarpark carpark) {
		DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
		if (carpark.getId() == null) {
			dom.insert(carpark);
		} else {
			dom.save(carpark);
			cache.invalidateAll();
			CarparkInOutServiceImpl.numberCache.invalidateAll();
			CarparkInOutServiceImpl.carparkCache.invalidateAll();
		}
		return carpark.getId();
	}

	@Override
	@Transactional
	public Long deleteCarpark(SingleCarparkCarpark carpark) {
		DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
		dom.remove(carpark.getId());
		cache.invalidateAll();
		return carpark.getId();
	}

	@Override
	public List<SingleCarparkCarpark> findAllCarpark() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
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
				@Override
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

	@Override
	public SingleCarparkCarpark findCarparkTopLevel() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			c.add(Restrictions.isNull("parent"));
			c.setFirstResult(0);
			c.setMaxResults(1);
			return (SingleCarparkCarpark) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	@Transactional
	public Long saveMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge) {
		if (monthlyCharge.getCarpark()==null&&monthlyCharge.getCarparkId()!=null) {
			SingleCarparkCarpark carpark = emprovider.get().getReference(SingleCarparkCarpark.class, monthlyCharge.getCarparkId());
			monthlyCharge.setCarpark(carpark);
		}
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

	@Override
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

	@Override
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
	@Transactional
	@Override
	public Long deleteMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h) {
		DatabaseOperation<SingleCarparkMonthlyUserPayHistory> dom = DatabaseOperation.forClass(SingleCarparkMonthlyUserPayHistory.class, emprovider.get());
		dom.remove(h);
		return h.getId();
	}


	@Override
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
			List<SimpleExpression> list=new ArrayList<>();
			for (SystemSettingTypeEnum sst : SystemSettingTypeEnum.values()) {
				SimpleExpression eq = Restrictions.eq("settingKey", sst.name());
				list.add(eq);
			}
			c.add(Restrictions.or(list.toArray(new SimpleExpression[list.size()])));
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	@Transactional
	public Long saveSystemSetting(SingleCarparkSystemSetting h) {
		Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemSetting.class);
		c.add(Restrictions.eq("settingKey", h.getSettingKey()));
		SingleCarparkSystemSetting set = (SingleCarparkSystemSetting) c.getSingleResultOrNull();
		
		if (set!=null) {
			if (set.getSettingValue().equals(h.getSettingValue())) {
				return set.getId();
			}
			h.setId(set.getId());
		}
		DatabaseOperation<SingleCarparkSystemSetting> dom = DatabaseOperation.forClass(SingleCarparkSystemSetting.class, emprovider.get());
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

	@Override
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

	@Override
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
			createCriteriaBySingleCarparkMonthlyUserPayHistory(c,userName,null,null,operaName,start,end);
			c.setFirstResult(maxResult);
			c.setMaxResults(size);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	private void createCriteriaBySingleCarparkMonthlyUserPayHistory(Criteria c, String userName,String plate, String address, String operaName, Date start, Date end) {
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.userName.name(), userName,MatchMode.START),
					Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.userName.name(), userName,MatchMode.END)));
		}
		if (!StrUtil.isEmpty(plate)) {
			c.add(Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.plateNO.name(), plate,MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(address)) {
			c.add(Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.userAddress.name(), address,MatchMode.ANYWHERE));
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
			createCriteriaBySingleCarparkMonthlyUserPayHistory(c,userName,null,null,operaName,start,end);
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			return singleResultOrNull.intValue();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
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
	static Cache<Object, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();
	
	@Override
	public SingleCarparkCarpark findCarparkById(Long id) {
		
		try {
			return (SingleCarparkCarpark) cache.get(""+getClass().getName()+"-findCarparkById-"+id, new Callable<Object>() {
				@Override
				public Object call() throws Exception {
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
			});
		} catch (Exception e) {
			throw new DongluServiceException("根据id查找停车场时发生错误", e);
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

	@Override
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

	@Override
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
			return new ArrayList<>();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public Holiday findHolidayByDate(Date date) {
		try {
			return (Holiday) cache.get(getClass().getName()+"-findHolidayByDate-"+StrUtil.formatDate(date), new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					unitOfWork.begin();
					try {
						Criteria c=CriteriaUtils.createCriteria(emprovider.get(), Holiday.class);
						c.add(Restrictions.eq("start", date));
						return (Holiday) c.getSingleResultOrNull();
					}catch(Exception e){
						return null;
					}finally{
						unitOfWork.end();
					}
				}
			});
		} catch (Exception e) {
			return null;
		}
		
	}
	
	@Override
	@Transactional
	public Long deleteHoliday(List<Holiday> list) {
		DatabaseOperation<Holiday> dom = DatabaseOperation.forClass(Holiday.class, emprovider.get());
		for (Holiday h:list) {
			dom.remove(h.getId());
		}
		return list.size()*1L;
	}

	@Override
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
					"计算临时收费成功:carTypeId={},carparkId={},startTime={},endTime={},money={}",
					carTypeId,carparkId,StrUtil.formatDateTime(startTime),
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
	public List<CarparkChargeStandard> findAllCarparkChargeStandard(SingleCarparkCarpark carpark, Boolean using) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), CarparkChargeStandard.class);
			c.add(Restrictions.eq(CarparkChargeStandard.Property.carpark.name(), carpark));
			if (!StrUtil.isEmpty(using)) {
				c.add(Restrictions.eq(CarparkChargeStandard.Property.using.name(), using));
			}
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

	@Override
	@Transactional
	public void changeChargeStandardState(Long id, boolean b) {
		DatabaseOperation<CarparkChargeStandard> dom = DatabaseOperation.forClass(CarparkChargeStandard.class, emprovider.get());
		CarparkChargeStandard entityWithId = dom.getEntityWithId(id);
		entityWithId.setUsing(b);
	}

	@Override
	public String getSystemSettingValue(SystemSettingTypeEnum settingType) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemSetting.class);
			c.add(Restrictions.eq("settingKey", settingType.name()));
			SingleCarparkSystemSetting set = (SingleCarparkSystemSetting) c.getSingleResultOrNull();
			return set==null?settingType.getDefaultValue():set.getSettingValue();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkDeviceVoice> findAllVoiceInfo() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkDeviceVoice.class);
			c.add(Restrictions.in("type", DeviceVoiceTypeEnum.values()));
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveDeviceVoice(List<SingleCarparkDeviceVoice> list) {
		DatabaseOperation<SingleCarparkDeviceVoice> dom = DatabaseOperation.forClass(SingleCarparkDeviceVoice.class, emprovider.get());
		for (SingleCarparkDeviceVoice singleCarparkDeviceVoice : list) {
			if (singleCarparkDeviceVoice.getId()!=null) {
				dom.save(singleCarparkDeviceVoice);
			}else{
				dom.insert(singleCarparkDeviceVoice);
			}
		}
		return list.size()*1l;
	}

	@Transactional
	public Long saveVisitor(SingleCarparkVisitor visitor) {
		DatabaseOperation<SingleCarparkVisitor> dom = DatabaseOperation.forClass(SingleCarparkVisitor.class, emprovider.get());
		if (visitor.getId()==null) {
			dom.insert(visitor);
		}else{
			dom.save(visitor);
		}
		return visitor.getId();
	}

	@Transactional
	public Long deleteVisitor(SingleCarparkVisitor visitor) {
		DatabaseOperation<SingleCarparkVisitor> dom = DatabaseOperation.forClass(SingleCarparkVisitor.class, emprovider.get());
		dom.remove(visitor);
		return visitor.getId();
	}

	@Override
	public List<SingleCarparkVisitor> findVisitorByLike(int start, int max, String userName, String plateNo) {
		unitOfWork.begin();
		try {
			Criteria c = createFindVisitorByLikeCriteria(userName, plateNo);
			c.addOrder(Order.desc("id"));
			c.setFirstResult(start);
			c.setMaxResults(max);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	/**
	 * @param userName
	 * @param plateNo
	 * @return
	 */
	public Criteria createFindVisitorByLikeCriteria(String userName, String plateNo) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkVisitor.class);
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.like(SingleCarparkVisitor.Property.name.name(), userName,MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(plateNo)) {
			c.add(Restrictions.like(SingleCarparkVisitor.Property.plateNO.name(), plateNo,MatchMode.ANYWHERE));
		}
		return c;
	}
	@Override
	public int countVisitorByLike(String userName, String plateNo) {
		unitOfWork.begin();
		try {
			Criteria c = createFindVisitorByLikeCriteria(userName, plateNo);
			c.setProjection(Projections.rowCount());
			Long total = (Long) c.getSingleResultOrNull();
			return total==null?0:total.intValue();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkVisitor findVisitorByPlateAndCarpark(String plateNo, SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkVisitor.class);
			if (!StrUtil.isEmpty(carpark)) {
				c.add(Restrictions.eq(SingleCarparkVisitor.Property.carpark.name(), carpark));
			}
			if (!StrUtil.isEmpty(plateNo)) {
				c.add(Restrictions.like(SingleCarparkVisitor.Property.plateNO.name(), plateNo,MatchMode.ANYWHERE));
			}
			c.add(Restrictions.eq(SingleCarparkVisitor.Property.status.name(), VisitorStatus.可用.name()));
			c.setFirstResult(0);
			c.setMaxResults(1);
			return (SingleCarparkVisitor) c.getSingleResultOrNull();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkMonthlyUserPayHistory> findMonthlyUserPayHistoryByValidTo(int i, int maxValue, String userName, String operaName, Date start) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyUserPayHistory.class);
			if (!StrUtil.isEmpty(userName)) {
				c.add(Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.userName.name(), userName, MatchMode.ANYWHERE));
			}
			if (!StrUtil.isEmpty(operaName)) {
				c.add(Restrictions.like(SingleCarparkMonthlyUserPayHistory.Property.operaName.name(), operaName, MatchMode.ANYWHERE));
			}
			if (!StrUtil.isEmpty(start)) {
				c.add(Restrictions.ge(SingleCarparkMonthlyUserPayHistory.Property.overdueTime.name(), start));
			}
			c.setFirstResult(i);
			c.setMaxResults(maxValue);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkMonthlyUserPayHistory> findMonthlyUserPayHistoryByCondition(int maxResult, int size, String userName, String plate, String address, String operaName, Date start,
			Date end) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyUserPayHistory.class);
			createCriteriaBySingleCarparkMonthlyUserPayHistory(c,userName,plate,address,operaName,start,end);
			c.setFirstResult(maxResult);
			c.setMaxResults(size);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public int countMonthlyUserPayHistoryByCondition(String userName, String plate, String address, String operaName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyUserPayHistory.class);
			createCriteriaBySingleCarparkMonthlyUserPayHistory(c,userName,plate,address, operaName, start,end);
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			return singleResultOrNull.intValue();
		} finally{
			unitOfWork.end();
		}
	}
		
}
