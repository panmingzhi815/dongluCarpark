package com.donglu.carpark.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.projection.Projections;

import com.donglu.carpark.service.CarPayServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class CarPayServiceImpl implements CarPayServiceI {
	@Inject
	private Provider<EntityManager> emprovider;

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
			c.add(Restrictions.like(CarPayHistory.Property.plateNO.name(), plateNo, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(plateNo)) {
			c.add(Restrictions.ge(CarPayHistory.Property.payTime.name(), start));
		}
		if (!StrUtil.isEmpty(plateNo)) {
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
	
}
