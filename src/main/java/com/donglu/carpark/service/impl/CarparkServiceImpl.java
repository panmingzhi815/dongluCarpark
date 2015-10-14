package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkService;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.MapperConfig;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.dongluhitec.card.service.impl.SettingServiceImpl;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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
			c.add(Restrictions.eq("carpark", carpark));
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
	public CarparkChargeStandard findCarparkChargeStandardByCode(String code) {
		unitOfWork.begin();
		try {
			Criteria criteria = CriteriaUtils.createCriteria(emprovider.get(),
					CarparkChargeStandard.class);
			criteria.add(Restrictions.eq(CarparkChargeStandard.Property.code.name(), code));
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

}
