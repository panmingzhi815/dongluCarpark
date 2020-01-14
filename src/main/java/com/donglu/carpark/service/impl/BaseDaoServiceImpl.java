package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.projection.Projections;

import com.donglu.carpark.service.BaseDaoService;
import com.dongluhitec.card.domain.WithID;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class BaseDaoServiceImpl implements BaseDaoService {
	
	protected Provider<EntityManager> emprovider;

	protected UnitOfWork unitOfWork;
	
	public BaseDaoServiceImpl(UnitOfWork unitOfWork,Provider<EntityManager> emprovider) {
		this.unitOfWork = unitOfWork;
		this.emprovider = emprovider;
	}

	@Override
	public <T> List<T> find(Class<T> clz, QueryParameter... parameters) {
		return find(clz, Arrays.asList(parameters));
	}
	@Override
	public <T> T findOne(Class<T> clz, QueryParameter... parameters) {
		ArrayList<QueryParameter> list = new ArrayList<>(Arrays.asList(parameters));
		list.add(QueryParameter.firstResult(0));
		list.add(QueryParameter.maxResult(1));
		List<T> find = find(clz, list);
		if (find.isEmpty()) {
			return null;
		}
		return find.get(0);
	}
	@Override
	public Long count(Class<?> clz,QueryParameter... parameters) {
		return count(clz, Arrays.asList(parameters));
	}
	@Override
	@Transactional
	public Long save(DomainObject o) {
		DatabaseOperation<DomainObject> dom = DatabaseOperation.forClass(DomainObject.class, emprovider.get());
		if (o.getId()==null) {
			dom.insert(o);
		}else{
			dom.save(o);
		}
		return o.getId();
	}
	@Override
	@Transactional
	public <T extends WithID> Long delete(T o) {
		try {
			EntityManager entityManager = emprovider.get();
			WithID find = entityManager.find(o.getClass(), o.getId());
			if (find!=null) {
				entityManager.remove(find);
			}
			return o.getId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public <T> List<T> find(Class<T> clz, List<QueryParameter> parameters) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), clz);
			for (QueryParameter queryParameter : parameters) {
				queryParameter.set(c);
			}
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	@Override
	public Long count(Class<?> clz, List<QueryParameter> parameters) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), clz);
			for (QueryParameter queryParameter : parameters) {
				queryParameter.set(c);
			}
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			return singleResultOrNull==null?0l:singleResultOrNull;
		} finally {
			unitOfWork.end();
		}
	}
	@Override
	public Object[] countMutil(Class<?> clz, List<QueryParameter> parameters){
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), clz);
			for (QueryParameter queryParameter : parameters) {
				queryParameter.set(c);
			}
			return (Object[]) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}


}
