package com.donglu.carpark.service;

import java.util.List;

import com.dongluhitec.card.domain.WithID;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;

public interface BaseDaoService {
	public <T> List<T> find(Class<T> clz,QueryParameter... parameters);
	public <T> List<T> find(Class<T> clz,List<QueryParameter> parameters);
	Long count(Class<?> clz, QueryParameter... parameters);
	Long count(Class<?> clz, List<QueryParameter> parameters);
	Long save(DomainObject model);
	<T extends WithID> Long delete(T o);
	Object[] countMutil(Class<?> clz, List<QueryParameter> parameters);
}
