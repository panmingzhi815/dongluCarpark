package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.derby.vti.Restriction;
import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.criterion.SimpleExpression;
import org.criteria4jpa.order.Order;
import org.criteria4jpa.projection.Projections;

import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
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
	public List<SingleCarparkInOutHistory> findByCondition(int maxResult, int size, String plateNo, String userName, String carType, String inout, Date start, Date end, String operaName, String inDevice, String outDevice, Long returnAccount) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);

			createCriteriaByCondition(c,plateNo, userName, carType, inout, start, end, operaName, outDevice, outDevice, returnAccount);
			c.setFirstResult(maxResult);
			c.setMaxResults(size);
			List<SingleCarparkInOutHistory> resultList = c.getResultList();
			return resultList;
		} finally {
			unitOfWork.end();
		}
	}

	private void createCriteriaByCondition(Criteria c, String plateNo, String userName, String carType, String inout, Date start, Date end, String operaName, String inDevice, String outDevice, Long returnAccount) {
		if (!StrUtil.isEmpty(plateNo)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), plateNo,MatchMode.START),
					Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), plateNo,MatchMode.END)));
		}
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkInOutHistory.Property.userName.name(), userName,MatchMode.START),
					Restrictions.like(SingleCarparkInOutHistory.Property.userName.name(), userName,MatchMode.END)));
		}
		
		if (!StrUtil.isEmpty(inDevice)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkInOutHistory.Property.inDevice.name(), inDevice,MatchMode.START),
					Restrictions.like(SingleCarparkInOutHistory.Property.inDevice.name(), inDevice,MatchMode.END)));
		}
		if (!StrUtil.isEmpty(outDevice)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkInOutHistory.Property.outDevice.name(), outDevice,MatchMode.START),
					Restrictions.like(SingleCarparkInOutHistory.Property.outDevice.name(), outDevice,MatchMode.END)));
		}
		if (!StrUtil.isEmpty(returnAccount)) {
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.returnAccount.name(), returnAccount));
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
		if (!StrUtil.isEmpty(start)) {
			Date todayTopTime = StrUtil.getTodayTopTime(start);
			c.add(Restrictions.or(Restrictions.ge(SingleCarparkInOutHistory.Property.inTime.name(), todayTopTime),
					Restrictions.ge(SingleCarparkInOutHistory.Property.outTime.name(), todayTopTime)));
		}
		if (!StrUtil.isEmpty(end)) {
			Date todayBottomTime = StrUtil.getTodayBottomTime(end);
			c.add(Restrictions.or(Restrictions.le(SingleCarparkInOutHistory.Property.inTime.name(), todayBottomTime),
					Restrictions.le(SingleCarparkInOutHistory.Property.outTime.name(), todayBottomTime)));
		}
		if (!StrUtil.isEmpty(operaName)) {
			c.add(Restrictions.like(SingleCarparkInOutHistory.Property.operaName.name(), operaName,MatchMode.ANYWHERE));
		}
	}

	@Override
	public Long countByCondition(String plateNo, String userName, String carType, String inout, Date in, Date out, String operaName, String inDevice, String outDevice, Long returnAccount) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			createCriteriaByCondition(c,plateNo, userName, carType, inout, in, out, operaName, outDevice, outDevice, returnAccount);
			c.setProjection(Projections.rowCount());
			Long resultList = (Long) c.getSingleResult();
			return resultList;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findNotReturnAccount(String returnUser) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.returnAccount.name()));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.operaName.name(), returnUser));
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public float findShouldMoneyByName(String userName) {
		unitOfWork.begin();
		try {
			System.out.println();
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.returnAccount.name()));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.operaName.name(), userName));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
			c.setProjection(Projections.sum(SingleCarparkInOutHistory.Property.shouldMoney.name()));
			Object singleResult2 = c.getSingleResult();
			Double singleResult = (Double) c.getSingleResult();
			return singleResult==null?0:singleResult.intValue();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public float findFactMoneyByName(String userName) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.returnAccount.name()));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.operaName.name(), userName));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
			c.setProjection(Projections.sum(SingleCarparkInOutHistory.Property.factMoney.name()));
			Double singleResult = (Double) c.getSingleResult();
			return singleResult==null?0:singleResult.intValue();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public float findFreeMoneyByName(String userName) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.returnAccount.name()));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.operaName.name(), userName));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
			c.setProjection(Projections.sum(SingleCarparkInOutHistory.Property.freeMoney.name()));
			Double singleResult = (Double) c.getSingleResult();
			return singleResult==null?0:singleResult.intValue();
		} finally {
			unitOfWork.end();
		}
	}

	@Transactional
	public Long saveInOutHistoryOfList(List<SingleCarparkInOutHistory> list) {
		DatabaseOperation<SingleCarparkInOutHistory> dom = DatabaseOperation.forClass(SingleCarparkInOutHistory.class, emprovider.get());
		for (SingleCarparkInOutHistory inout : list) {
			if (inout.getId() == null) {
				dom.insert(inout);
			} else {
				dom.save(inout);
			}
		}
		
		return list.size()*1L;
	}

	@Override
	public int findFixSlotIsNow() {
		unitOfWork.begin();
		try {
			Criteria cc = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			cc.setProjection(Projections.sum(SingleCarparkCarpark.Property.fixNumberOfSlot.name()));
			Long singleResult2 = (Long) cc.getSingleResult();
			int intValue = singleResult2==null?0:singleResult2.intValue();
			
//			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
//			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
//			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "固定车"));
//			c.setProjection(Projections.rowCount());
//			Long singleResult = (Long) c.getSingleResult();
//			int now=singleResult==null?0:singleResult.intValue();
			return intValue;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public int findTempSlotIsNow() {
		unitOfWork.begin();
		try {
			Criteria cc = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			cc.setProjection(Projections.sum(SingleCarparkCarpark.Property.tempNumberOfSlot.name()));
			Long singleResult2 = (Long) cc.getSingleResult();
			int intValue = singleResult2==null?0:singleResult2.intValue();
			
//			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
//			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
//			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
//			c.setProjection(Projections.rowCount());
//			Long singleResult = (Long) c.getSingleResult();
//			int now=singleResult==null?0:singleResult.intValue();
			return intValue;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public int findTotalSlotIsNow() {
		unitOfWork.begin();
		try {
			Criteria cc = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
            cc.add(Restrictions.isNull("parent"));			
			cc.setProjection(Projections.sum(SingleCarparkCarpark.Property.tempNumberOfSlot.name()));
			Object singleResult2 = cc.getSingleResult();
			int intValue = singleResult2==null?0:((Long)singleResult2).intValue();
			
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
			c.setProjection(Projections.rowCount());
			Long singleResult = (Long) c.getSingleResult();
			int now=singleResult==null?0:singleResult.intValue();
			return intValue-now<=0?0:intValue-now;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(String plateNO, boolean order) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotEmpty(SingleCarparkInOutHistory.Property.plateNo.name()));
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.plateNo.name()));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			List<SimpleExpression> list=new ArrayList<>();
			for (String s : CarparkUtils.splitPlateNO(plateNO)) {
				SimpleExpression like = Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), s,MatchMode.ANYWHERE);
				list.add(like);
			}
			
			c.add(Restrictions.or(list.toArray(new SimpleExpression[list.size()])));
			if (order) {
				c.addOrder(Order.asc(SingleCarparkInOutHistory.Property.inTime.name()));
			}else{
				c.addOrder(Order.desc(SingleCarparkInOutHistory.Property.inTime.name()));
			}
			return c.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findAddNoPlateNOHistory(boolean order) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.or(Restrictions.isEmpty(SingleCarparkInOutHistory.Property.plateNo.name()),
					Restrictions.isNull(SingleCarparkInOutHistory.Property.plateNo.name())));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			if (order) {
				c.addOrder(Order.asc(SingleCarparkInOutHistory.Property.inTime.name()));
			}else{
				c.addOrder(Order.desc(SingleCarparkInOutHistory.Property.inTime.name()));
			}
			return c.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}finally{
			unitOfWork.end();
		}
	}

	@Transactional
	public Long deleteAllHistory() {
		//
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
		List<SingleCarparkInOutHistory> resultList = c.getResultList();
		DatabaseOperation<SingleCarparkInOutHistory> dom = DatabaseOperation.forClass(SingleCarparkInOutHistory.class, emprovider.get());
		for (SingleCarparkInOutHistory singleCarparkInOutHistory : resultList) {
			dom.remove(singleCarparkInOutHistory);
		}
		//
		Criteria c1 = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyUserPayHistory.class);
		List<SingleCarparkMonthlyUserPayHistory> resultList1 = c.getResultList();
		DatabaseOperation<SingleCarparkMonthlyUserPayHistory> dom1 = DatabaseOperation.forClass(SingleCarparkMonthlyUserPayHistory.class, emprovider.get());
		for (SingleCarparkMonthlyUserPayHistory h : resultList1) {
			dom1.remove(h);
		}
		//
		Criteria c2 = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkReturnAccount.class);
		List<SingleCarparkReturnAccount> resultList2 = c.getResultList();
		DatabaseOperation<SingleCarparkReturnAccount> dom2 = DatabaseOperation.forClass(SingleCarparkReturnAccount.class, emprovider.get());
		for (SingleCarparkReturnAccount h : resultList2) {
			dom2.remove(h);
		}
		//
		
		
		return 1L;
	}

}
