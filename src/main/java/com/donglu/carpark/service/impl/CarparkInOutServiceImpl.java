package com.donglu.carpark.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.derby.vti.Restriction;
import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.order.Order;
import org.criteria4jpa.projection.Projections;

import com.donglu.carpark.service.CarparkInOutServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.MapperConfig;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class CarparkInOutServiceImpl implements CarparkInOutServiceI {
	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	@Inject
	private MapperConfig mapper;

	@Transactional
	public Long saveInOutHistory(SingleCarparkInOutHistory inout) {
		DatabaseOperation<SingleCarparkInOutHistory> dom = DatabaseOperation.forClass(SingleCarparkInOutHistory.class, emprovider.get());
		if (inout.getId() == null) {
			dom.insert(inout);
		} else {
			dom.save(inout);
		}
		return inout.getId();
	}

	public List<SingleCarparkInOutHistory> findByNoOut(String plateNo) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.eq("plateNo", plateNo));
			c.add(Restrictions.isNull("outTime"));
			c.addOrder(Order.asc("inTime"));
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public float findTotalCharge(String userName) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.eq("operaName", userName));
			c.add(Restrictions.isNotNull("outTime"));
			c.add(Restrictions.isNull("returnAccount"));
			c.setProjection(Projections.sum("factMoney"));
			Object singleResult = c.getSingleResultOrNull();
			Long l=singleResult==null?0:(Long) singleResult;
			return l.intValue();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findByCondition(int maxResult, int size, String plateNo, String userName, String carType, String inout, Date in, Date out, String operaName) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);

			createCriteriaByCondition(c,plateNo, userName, carType, inout, in, out, operaName);
			c.setFirstResult(maxResult);
			c.setMaxResults(size);
			List<SingleCarparkInOutHistory> resultList = c.getResultList();
			return resultList;
		} finally {
			unitOfWork.end();
		}
	}

	private void createCriteriaByCondition(Criteria c, String plateNo, String userName, String carType, String inout, Date in, Date out, String operaName) {
		if (!StrUtil.isEmpty(plateNo)) {
			c.add(Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), plateNo,MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.like(SingleCarparkInOutHistory.Property.userName.name(), userName,MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(carType)) {
			if (!carType.equals("全部")) {
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), carType));
			}
		}
		if (!StrUtil.isEmpty(inout)) {
			if (inout.equals("是")) {
				c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			}else if(inout.equals("否")){
				c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			}
			
		}
		if (!StrUtil.isEmpty(in)) {
			Date todayTopTime = StrUtil.getTodayTopTime(in);
			c.add(Restrictions.or(Restrictions.ge(SingleCarparkInOutHistory.Property.inTime.name(), todayTopTime),
					Restrictions.ge(SingleCarparkInOutHistory.Property.outTime.name(), todayTopTime)));
//			c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.inTime.name(), StrUtil.getTodayTopTime(in)));
//			c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.outTime.name(), StrUtil.getTodayTopTime(in)));
		}
		if (!StrUtil.isEmpty(out)) {
			Date todayBottomTime = StrUtil.getTodayBottomTime(out);
			c.add(Restrictions.or(Restrictions.le(SingleCarparkInOutHistory.Property.inTime.name(), todayBottomTime),
					Restrictions.le(SingleCarparkInOutHistory.Property.outTime.name(), todayBottomTime)));
//			c.add(Restrictions.le(SingleCarparkInOutHistory.Property.inTime.name(), todayBottomTime));
//			c.add(Restrictions.le(SingleCarparkInOutHistory.Property.outTime.name(), todayBottomTime));
		}
		if (!StrUtil.isEmpty(operaName)) {
			c.add(Restrictions.like(SingleCarparkInOutHistory.Property.operaName.name(), operaName,MatchMode.ANYWHERE));
		}
	}

	@Override
	public Long countByCondition(String plateNo, String userName, String carType, String inout, Date in, Date out, String operaName) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			createCriteriaByCondition(c,plateNo, userName, carType, inout, in, out, operaName);
			c.setProjection(Projections.rowCount());
			Long resultList = (Long) c.getSingleResult();
			return resultList;
		} finally {
			unitOfWork.end();
		}
	}

}
