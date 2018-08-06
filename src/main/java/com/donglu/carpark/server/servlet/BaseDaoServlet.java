package com.donglu.carpark.server.servlet;

import java.util.List;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.BaseDaoService;
import com.dongluhitec.card.domain.WithID;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;

public class BaseDaoServlet extends HessianServlet implements BaseDaoService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3498285305302003135L;
	protected BaseDaoService baseDaoService;
	public BaseDaoServlet() {
		
	}

	@Override
	public <T> List<T> find(Class<T> clz, QueryParameter... parameters) {
		return baseDaoService.find(clz, parameters);
	}

	@Override
	public <T> List<T> find(Class<T> clz, List<QueryParameter> parameters) {
		return baseDaoService.find(clz, parameters);
	}

	@Override
	public Long count(Class<?> clz, QueryParameter... parameters) {
		return baseDaoService.count(clz, parameters);
	}

	@Override
	public Long count(Class<?> clz, List<QueryParameter> parameters) {
		return baseDaoService.count(clz, parameters);
	}

	@Override
	public Long save(DomainObject model) {
		return baseDaoService.save(model);
	}

	@Override
	public <T extends WithID> Long delete(T o) {
		return baseDaoService.delete(o);
	}

	@Override
	public Object[] countMutil(Class<?> clz, List<QueryParameter> parameters) {
		return baseDaoService.countMutil(clz, parameters);
	}

}
