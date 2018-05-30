package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.apache.derby.vti.Restriction;
import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.order.Order;
import org.criteria4jpa.projection.ProjectionList;
import org.criteria4jpa.projection.Projections;

import com.donglu.carpark.service.CarPayServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class CarPayServiceImpl implements CarPayServiceI {
	@Inject
	private Provider<EntityManager> emprovider;
	private static Long maxId=0l;

	@Inject
	private UnitOfWork unitOfWork;
	@Transactional
	@Override
	public Long saveCarPayHistory(CarPayHistory cp) {
		DatabaseOperation<CarPayHistory> dom = DatabaseOperation.forClass(CarPayHistory.class, emprovider.get());
		if (cp.getId()==null) {
			dom.insert(cp);
		}else{
			dom.save(cp);
		}
		return cp.getId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CarPayHistory> findCarPayHistoryByLike(int i, int maxValue, String plateNo, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindCarPayHistoryByLikeCriteria(plateNo, start, end);
			c.addOrder(Order.desc(CarPayHistory.Property.payTime.name()));
			c.setFirstResult(i);
			c.setMaxResults(maxValue);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	/**
	 * @param plateNo
	 * @param start
	 * @param end
	 * @return
	 */
	public Criteria createFindCarPayHistoryByLikeCriteria(String plateNo, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), CarPayHistory.class);
		if (!StrUtil.isEmpty(plateNo)) {
			c.add(Restrictions.like(CarPayHistory.Property.plateNO.name(), plateNo));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(CarPayHistory.Property.payTime.name(), start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(CarPayHistory.Property.payTime.name(), end));
		}
		return c;
	}

	@Override
	public int countCarPayHistoryByLike(String plateNo, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindCarPayHistoryByLikeCriteria(plateNo, start, end);
			c.setProjection(Projections.rowCount());
			Long l = (Long) c.getSingleResultOrNull();
			return l==null?0:l.intValue();
		} finally{
			unitOfWork.end();
		}
	}
	@Transactional
	@Override
	public Long deleteCarPayHistory(Long id) {
		DatabaseOperation<CarPayHistory> dom = DatabaseOperation.forClass(CarPayHistory.class, emprovider.get());
		dom.remove(id);
		return id;
	}

	@Override
	public CarPayHistory findCarPayHistoryByPayId(String payId) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), CarPayHistory.class);
			c.add(Restrictions.like(CarPayHistory.Property.payId.name(), payId));
			return (CarPayHistory) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}
	private static Cache<String, List<CarPayHistory>> build = CacheBuilder.newBuilder().expireAfterWrite(800, TimeUnit.MILLISECONDS).build();
	@Override
	public List<CarPayHistory> getCarPayHistoryWithNew() {
		try {
			return build.get("getCarPayHistoryWithNew", new Callable<List<CarPayHistory>>() {
				@Override
				public List<CarPayHistory> call() throws Exception {
					unitOfWork.begin();
					try {
						Criteria c = CriteriaUtils.createCriteria(emprovider.get(), CarPayHistory.class);
						if (maxId == 0) {
							c.setProjection(Projections.max("id"));
							Object singleResultOrNull = c.getSingleResultOrNull();
							if (singleResultOrNull != null) {
								maxId = (Long) singleResultOrNull;
							}
							return new ArrayList<>();
						}
						c.add(Restrictions.gt("id", maxId));
						List<CarPayHistory> list = c.getResultList();
						for (CarPayHistory carPayHistory : list) {
							if (carPayHistory.getId()>maxId) {
								maxId=carPayHistory.getId();
							}
						}
						return list;
					} finally {
						unitOfWork.end();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public List<Double> countCarPayHistoryMoney(String plateNo, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindCarPayHistoryByLikeCriteria(plateNo, start, end);
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.sum("payedMoney"));
			projectionList.add(Projections.sum("cashCost"));
			projectionList.add(Projections.sum("onlineCost"));
			projectionList.add(Projections.sum("couponValue"));
			c.setProjection(projectionList);
			Object[] singleResult = (Object[])c.getSingleResult();
			List<Double> asList = new ArrayList<>();
			for (int i = 0; i < singleResult.length; i++) {
				Object object = singleResult[i];
				asList.add((Double) object);
			}
			return asList;
		} finally{
			unitOfWork.end();
		}
	}
	
}
