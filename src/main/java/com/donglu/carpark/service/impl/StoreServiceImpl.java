package com.donglu.carpark.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.projection.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.StoreServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.MapperConfig;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.dongluhitec.card.service.impl.SettingServiceImpl;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class StoreServiceImpl implements StoreServiceI {
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingServiceImpl.class);

	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	@Inject
	private MapperConfig mapper;

	@Override
	public SingleCarparkStore findByLogin(String loginName, String loginPassword) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkStore.class);
			c.add(Restrictions.eq("loginName", loginName));
			c.add(Restrictions.eq("loginPawword", loginPassword));
			Object singleResultOrNull = c.getSingleResultOrNull();
			if (StrUtil.isEmpty(singleResultOrNull)) {
				return null;
			}
			return (SingleCarparkStore) singleResultOrNull;
		} finally {
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveStore(SingleCarparkStore store) {
		DatabaseOperation<SingleCarparkStore> dom = DatabaseOperation.forClass(SingleCarparkStore.class, emprovider.get());
		if (store.getId() == null) {
			dom.insert(store);
		} else {
			dom.save(store);
		}
		return store.getId();
	}

	@Override
	public Long deleteStore(SingleCarparkStore store) {
		return null;
	}

	@Transactional
	public Long saveStoreFree(SingleCarparkStoreFreeHistory storeFree) {
		DatabaseOperation<SingleCarparkStoreFreeHistory> dom = DatabaseOperation.forClass(SingleCarparkStoreFreeHistory.class, emprovider.get());
		if (storeFree.getId() == null) {
			dom.insert(storeFree);
		} else {
			dom.save(storeFree);
		}
		return storeFree.getId();
	}

	@Override
	public List<SingleCarparkStoreFreeHistory> findByPlateNO(int page, int rows,String storeName, String plateNO, String used, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindStoreFreeHistoryCriteria(storeName,plateNO, used, start, end);
			c.setFirstResult(page * rows);
			c.setMaxResults(rows);
			List<SingleCarparkStoreFreeHistory> resultList = c.getResultList();
			return resultList;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public Long countByPlateNO(String storeName,String plateNO, String used, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindStoreFreeHistoryCriteria(storeName,plateNO, used,  start, end);
			c.setProjection(Projections.rowCount());
			return (Long) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}

	/**
	 * @param plateNO
	 * @param start
	 * @param end
	 * @return
	 */
	private Criteria createFindStoreFreeHistoryCriteria(String storeName,String plateNO, String used, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkStoreFreeHistory.class);
		if (!StrUtil.isEmpty(storeName)) {
			c.add(Restrictions.like("storeName", storeName, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(plateNO)) {
			c.add(Restrictions.like("freePlateNo", plateNO, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(used)) {
			c.add(Restrictions.eq("used", used));
		}

		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge("createTime", start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le("createTime", end));
		}
		return c;
	}

	@Transactional
	public Long saveStorePay(SingleCarparkStoreChargeHistory storePay) {
		DatabaseOperation<SingleCarparkStoreChargeHistory> dom = DatabaseOperation.forClass(SingleCarparkStoreChargeHistory.class, emprovider.get());
		if (storePay.getId() == null) {
			dom.insert(storePay);
		} else {
			dom.save(storePay);
		}
		return storePay.getId();
	}

	@Override
	public List<SingleCarparkStoreChargeHistory> findStoreChargeHistoryByTime(int page, int rows, String storeName,String operaName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindStoreChargeHistoryCriteria(storeName, operaName, start, end);
			c.setFirstResult(page * rows);
			c.setMaxResults(rows);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	/**
	 * @param operaName
	 * @param start
	 * @param end
	 * @return
	 */
	private Criteria createFindStoreChargeHistoryCriteria(String storeName,String operaName, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkStoreChargeHistory.class);
		if (!StrUtil.isEmpty(storeName)) {
			c.add(Restrictions.eq("storeName", storeName));
		}
		if (!StrUtil.isEmpty(operaName)) {
			c.add(Restrictions.like("operaName", operaName, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge("createTime", start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le("createTime", end));
		}
		return c;
	}

	@Override
	public Long countStoreChargeHistoryByTime(String storeName,String operaName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindStoreChargeHistoryCriteria(storeName, operaName, start, end);
			c.setProjection(Projections.rowCount());
			Long r = (Long) c.getSingleResultOrNull();
			return r;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkStoreFreeHistory findStoreFreeById(Long id) {
		unitOfWork.begin();
		try {
			DatabaseOperation<SingleCarparkStoreFreeHistory> dom = DatabaseOperation.forClass(SingleCarparkStoreFreeHistory.class, emprovider.get());
			SingleCarparkStoreFreeHistory entityWithId = dom.getEntityWithId(id);
			return entityWithId;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkStore> findStoreByCondition(int start, int max, String storeName) {
		unitOfWork.begin();
		try {
			Criteria c = createFindStoreCriteria(storeName);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	/**
	 * @param storeName
	 * @return
	 */
	private Criteria createFindStoreCriteria(String storeName) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkStore.class);
		if (!StrUtil.isEmpty(storeName)) {
			c.add(Restrictions.eq("storeName", storeName));
		}
		return c;
	}

	@Override
	public Long countStoreByCondition(String storeName) {
		unitOfWork.begin();
		try {
			Criteria c = createFindStoreCriteria(storeName);
			c.setProjection(Projections.rowCount());
			return (Long) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}
}
