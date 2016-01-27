package com.donglu.carpark.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;

import com.donglu.carpark.service.SystemOperaLogServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

@SuppressWarnings("unchecked")
public class SystemOperaLogServiceImpl implements SystemOperaLogServiceI {
	
	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;


	private ScheduledExecutorService saveLogService;
	
	@Override
	public void saveOperaLog(SystemOperaLogTypeEnum type, String content) {
		if (StrUtil.isEmpty(saveLogService)) {
			saveLogService = Executors.newSingleThreadScheduledExecutor();
		}
		SingleCarparkSystemOperaLog log=new SingleCarparkSystemOperaLog();
		log.setOperaName(System.getProperty("userName"));
		log.setOperaDate(new Date());
		log.setType(type);
		log.setContent(content);
		saveLogService.submit(()->{
			save(log);
		});
	}
	@Transactional
	void save(SingleCarparkSystemOperaLog log){
		DatabaseOperation<SingleCarparkSystemOperaLog> dom = DatabaseOperation.forClass(SingleCarparkSystemOperaLog.class, emprovider.get());
		dom.insert(log);
	}
	@Override
	public List<SingleCarparkSystemOperaLog> findBySearch(String operaName, Date start, Date end, SystemOperaLogTypeEnum type) {
		unitOfWork.begin();
		try {
			Criteria c = createSearchCriteria(operaName, start, end, type);
			
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	/**
	 * 
	 * @param operaName
	 * @param start
	 * @param end
	 * @param type
	 * @return
	 */
	private Criteria createSearchCriteria(String operaName, Date start, Date end, SystemOperaLogTypeEnum type) {
		Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemOperaLog.class);
		if (!StrUtil.isEmpty(operaName)) {
			c.add(Restrictions.like(SingleCarparkSystemOperaLog.Property.operaName.name(), operaName,MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(SingleCarparkSystemOperaLog.Property.operaDate.name(), start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(SingleCarparkSystemOperaLog.Property.operaDate.name(), end));
		}
		if (!StrUtil.isEmpty(type)) {
			c.add(Restrictions.eq(SingleCarparkSystemOperaLog.Property.type.name(), type));
		}
		return c;
	}
}
