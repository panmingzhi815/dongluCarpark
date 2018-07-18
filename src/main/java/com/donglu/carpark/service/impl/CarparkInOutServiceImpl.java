package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.dongluhitec.card.domain.db.singlecarpark.haiyu.CarparkRecordHistory;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.criterion.SimpleExpression;
import org.criteria4jpa.order.Order;
import org.criteria4jpa.projection.ProjectionList;
import org.criteria4jpa.projection.Projections;
import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkHolidayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkOffLineHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkStillTime;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceErrorMessage;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkImageHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser.CarparkSlotTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

@SuppressWarnings("unchecked")
public class CarparkInOutServiceImpl implements CarparkInOutServiceI {
	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;
	
	static Cache<String, Number> numberCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(1, TimeUnit.MINUTES).build();
	static Cache<Object, List<SingleCarparkCarpark>> carparkCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

	@Override
	@Transactional
	public Long saveInOutHistory(SingleCarparkInOutHistory inout) {
		DatabaseOperation<SingleCarparkInOutHistory> dom = DatabaseOperation.forClass(SingleCarparkInOutHistory.class, emprovider.get());
//		Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
//		c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.plateNo.name(), inout.getPlateNo()));
//		c.add(restrictions)
		
		if (inout.getId() == null) {
			dom.insert(inout);
			this.emprovider.get().persist(new CarparkRecordHistory(inout,UpdateEnum.新添加));
		} else {
			if (inout.isSavePayHistory()&&inout.getFactMoney()!=null&&inout.getFactMoney()>0) {
				savePayHistory(inout);
			}
			dom.save(inout);
			this.emprovider.get().merge(new CarparkRecordHistory(inout, UpdateEnum.被修改));
		}
		if (inout.getCarType().equals("临时车")) {
			numberCache.invalidate("findFactMoneyByName-"+inout.getOperaName());
			numberCache.invalidate("findFreeMoneyByName-"+inout.getOperaName());
			numberCache.invalidate("findFactMoneyByName-"+inout.getChargeOperaName());
			numberCache.invalidate("findFreeMoneyByName-"+inout.getChargeOperaName());
			numberCache.invalidate("findTempSlotIsNow-"+inout.getCarparkId());
		}else{
			numberCache.invalidate("findFixSlotIsNow-"+inout.getCarparkId());
		}
		numberCache.invalidate("findTotalSlotIsNow-"+inout.getCarparkId());
		return inout.getId();
	}

	private void savePayHistory(SingleCarparkInOutHistory inout) {
		if (!(inout.isSavePayHistory()&&inout.getFactMoney()!=null&&inout.getFactMoney()>0)) {
			return;
		}
		CarPayHistory carPayHistory = new CarPayHistory(inout);
		this.emprovider.get().persist(carPayHistory);
	}

	@Override
	public List<SingleCarparkInOutHistory> findByNoOut(String plateNo, SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull("outTime"));
			if (!StrUtil.isEmpty(plateNo)) {
				c.add(Restrictions.eq("plateNo", plateNo));
			}
			c.add(Restrictions.eq("carparkId", carpark.getId()));
			c.addOrder(Order.desc("inTime"));
			c.setFirstResult(0);
			c.setMaxResults(2);
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
			Long l = singleResult == null ? 0 : (Long) singleResult;
			return l.intValue();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findByCondition(int maxResult, int size, String plateNo, String userName, String carType, String inout, Date start, Date end,Date outStart, Date outEnd, String operaName,
			String inDevice, String outDevice, Long returnAccount, Long carparkId,float... shouldMoney) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);

			createCriteriaByCondition(c, plateNo, userName, carType, inout, start, end, outStart, outEnd, operaName, inDevice, outDevice, returnAccount,carparkId, shouldMoney);
			c.addOrder(Order.asc("id"));
			c.setFirstResult(maxResult);
			c.setMaxResults(size);
			List<SingleCarparkInOutHistory> resultList = c.getResultList();
			return resultList;
		} finally {
			unitOfWork.end();
		}
	}
	private void createCriteriaByCondition(Criteria c, String plateNo, String userName, String carType, String inout, Date start, Date end,Date outStart, Date outEnd, String operaName, String inDevice, String outDevice,
			Long returnAccount, Long carparkId, float... shouldMoney) {
		if (!StrUtil.isEmpty(inout)) {
			if (inout.equals("是")) {
				c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			} else if (inout.equals("否")) {
				c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
//				return;
			}
		}

		if (!StrUtil.isEmpty(plateNo)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), plateNo, MatchMode.START),
					Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), plateNo, MatchMode.END)));
		}
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkInOutHistory.Property.userName.name(), userName, MatchMode.START),
					Restrictions.like(SingleCarparkInOutHistory.Property.userName.name(), userName, MatchMode.END)));
		}

		if (!StrUtil.isEmpty(inDevice)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkInOutHistory.Property.inDevice.name(), inDevice, MatchMode.START),
					Restrictions.like(SingleCarparkInOutHistory.Property.inDevice.name(), inDevice, MatchMode.END)));
		}
		if (!StrUtil.isEmpty(outDevice)) {
			c.add(Restrictions.or(Restrictions.like(SingleCarparkInOutHistory.Property.outDevice.name(), outDevice, MatchMode.START),
					Restrictions.like(SingleCarparkInOutHistory.Property.outDevice.name(), outDevice, MatchMode.END)));
		}
		if (!StrUtil.isEmpty(returnAccount)) {
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.returnAccount.name(), returnAccount));
		}

		if (!StrUtil.isEmpty(carType)) {
			if (!carType.equals("全部")) {
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), carType));
			}
		}

		if (!StrUtil.isEmpty(start) ) {
			c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.inTime.name(), start));
		}
		if (!StrUtil.isEmpty(end) ) {
			c.add(Restrictions.le(SingleCarparkInOutHistory.Property.inTime.name(), end));
		}
		if (!StrUtil.isEmpty(outStart) ) {
			c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.outTime.name(), outStart));
		}
		if (!StrUtil.isEmpty(outEnd) ) {
			c.add(Restrictions.le(SingleCarparkInOutHistory.Property.outTime.name(), outEnd));
		}

		if (!StrUtil.isEmpty(operaName)) {
			c.add(Restrictions.like(SingleCarparkInOutHistory.Property.operaName.name(), operaName, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(carparkId)) {
			List<SingleCarparkCarpark> findSameCarpark = findSameCarpark(carparkId);
			List<SimpleExpression> list=new ArrayList<>();
			for (SingleCarparkCarpark singleCarparkCarpark : findSameCarpark) {
				SimpleExpression eq = Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), singleCarparkCarpark.getId());
				list.add(eq);
			}
			c.add(Restrictions.or(list.toArray(new SimpleExpression[list.size()])));
		}
		if (!StrUtil.isEmpty(shouldMoney)) {
			if (shouldMoney[0]>0) {
				c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.shouldMoney.name(), shouldMoney[0]));
			}
			if (shouldMoney.length>=2&&shouldMoney[1]>0) {
				c.add(Restrictions.le(SingleCarparkInOutHistory.Property.shouldMoney.name(), shouldMoney[1]));
			}
		}
	}

	@Override
	public Long countByCondition(String plateNo, String userName, String carType, String inout, Date in, Date out,Date outStart, Date outEnd, String operaName, String inDevice, String outDevice, Long returnAccount, Long carparkId,float... shouldMoney) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			createCriteriaByCondition(c, plateNo, userName, carType, inout, in, out, outStart, outEnd, operaName, inDevice, outDevice, returnAccount, carparkId,shouldMoney);
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
			Double singleResult = (Double) c.getSingleResult();
			return singleResult == null ? 0 : singleResult.floatValue();
		} finally {
			unitOfWork.end();
		}
	}
	
	@Override
	public float findFactMoneyByName(String userName) {
		
		try {
			return numberCache.get("findFactMoneyByName-"+userName, new Callable<Float>() {
				@Override
				public Float call() throws Exception {
					unitOfWork.begin();
					try {
						Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
						c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.returnAccount.name()));
						c.add(Restrictions.gt(SingleCarparkInOutHistory.Property.factMoney.name(), 0f));
						c.add(Restrictions.or(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()),Restrictions.isNotNull(SingleCarparkInOutHistory.Property.chargeTime.name())));
						c.add(Restrictions.or(Restrictions.eq(SingleCarparkInOutHistory.Property.operaName.name(), userName),Restrictions.eq(SingleCarparkInOutHistory.Property.chargeOperaName.name(), userName)));
						c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
						c.setProjection(Projections.sum(SingleCarparkInOutHistory.Property.factMoney.name()));
						Double singleResult = (Double) c.getSingleResult();
						return singleResult == null ? 0 : singleResult.floatValue();
					} finally {
						unitOfWork.end();
					}
				}
			}).floatValue();
		} catch (Exception e) {
			throw new DongluServiceException("获取实收金额时发生错误", e);
		}
		
	}

	@Override
	public float findFreeMoneyByName(String userName) {
		try {
			return numberCache.get("findFreeMoneyByName-"+userName, new Callable<Float>() {
				@Override
				public Float call() throws Exception {
					unitOfWork.begin();
					try {
						Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
						c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.freeReturnAccount.name()));
						c.add(Restrictions.gt(SingleCarparkInOutHistory.Property.freeMoney.name(), 0f));
						c.add(Restrictions.or(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()),Restrictions.isNotNull(SingleCarparkInOutHistory.Property.chargeTime.name())));
						c.add(Restrictions.or(Restrictions.eq(SingleCarparkInOutHistory.Property.operaName.name(), userName),Restrictions.eq(SingleCarparkInOutHistory.Property.chargeOperaName.name(), userName)));
						c.setProjection(Projections.sum(SingleCarparkInOutHistory.Property.freeMoney.name()));
						c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
						Double singleResult = (Double) c.getSingleResult();
						return singleResult == null ? 0 : singleResult.floatValue();
					} finally {
						unitOfWork.end();
					}
				}
			}).floatValue();
		} catch (Exception e) {
			throw new DongluServiceException("统计免费金额时发生错误！", e);
		}
		
	}

	@Override
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

		return list.size() * 1L;
	}

	@Override
	public int findFixSlotIsNow(SingleCarparkCarpark singleCarparkCarpark) {
		if (singleCarparkCarpark==null) {
			return 0;
		}
		try {
			return numberCache.get("findFixSlotIsNow-"+singleCarparkCarpark.getId(), new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					unitOfWork.begin();
					try {

						List<SingleCarparkCarpark> findSameCarpark = findSameCarpark(singleCarparkCarpark);
						int intValue = 0;

						if (findSameCarpark == null) {
							return 0;
						}

						for (SingleCarparkCarpark singleCarparkCarpark2 : findSameCarpark) {
							Integer fixNumberOfSlot = singleCarparkCarpark2.getFixNumberOfSlot();
							intValue += fixNumberOfSlot == null ? 0 : fixNumberOfSlot;
						}
						Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
						c.add(Restrictions.eq(SingleCarparkUser.Property.carparkSlotType.name(), CarparkSlotTypeEnum.固定车位));
						c.setProjection(Projections.sum(SingleCarparkUser.Property.carparkSlot.name()));
						Long singleResultOrNull = (Long) c.getSingleResultOrNull();
						if (singleResultOrNull==null) {
							return intValue;
						}
						intValue=intValue-singleResultOrNull.intValue();
						
						return intValue<0?0:intValue;
					} finally {
						unitOfWork.end();
					}
				}
			}).intValue();
		} catch (ExecutionException e) {
			throw new DongluServiceException("计算停车场固定车位数时发生错误！", e);
		}
		
	}

	@Override
	public int findTempSlotIsNow(SingleCarparkCarpark singleCarparkCarpark) {
		if (singleCarparkCarpark==null) {
			return 0;
		}
		try {
			return numberCache.get("findTempSlotIsNow-"+singleCarparkCarpark.getId(), new Callable<Number>() {
				@Override
				public Number call() throws Exception {
					unitOfWork.begin();
					try {
						List<SingleCarparkCarpark> findSameCarpark = findSameCarpark(singleCarparkCarpark);
						int intValue = 0;
						if (findSameCarpark == null) {
							return 0;
						}
						for (SingleCarparkCarpark singleCarparkCarpark2 : findSameCarpark) {
							Integer fixNumberOfSlot = singleCarparkCarpark2.getTempNumberOfSlot();
							intValue += fixNumberOfSlot == null ? 0 : fixNumberOfSlot;
						}

						// Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
						// c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
						// c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
						// c.setProjection(Projections.rowCount());
						// Long singleResult = (Long) c.getSingleResult();
						// int now=singleResult==null?0:singleResult.intValue();
						return intValue;
					} finally {
						unitOfWork.end();
					}
				}
			}).intValue();
		} catch (ExecutionException e) {
			throw new DongluServiceException("统计临时车位数时发生错误", e);
		}
		
	}

	@Override
	public Integer findTotalSlotIsNow(SingleCarparkCarpark singleCarparkCarpark) {
		if (singleCarparkCarpark==null) {
			return 0;
		}
		try {
			return numberCache.get("findTotalSlotIsNow-"+singleCarparkCarpark.getId(), new Callable<Number>() {

				@Override
				public Number call() throws Exception {
					unitOfWork.begin();
					try {
						List<SingleCarparkCarpark> findSameCarpark = findSameCarpark(singleCarparkCarpark);
						if (findSameCarpark == null) {
							return 0;
						}
						int intValue = 0;

						for (SingleCarparkCarpark singleCarparkCarpark2 : findSameCarpark) {
							Integer fixNumberOfSlot = singleCarparkCarpark2.getTempNumberOfSlot();
							intValue += fixNumberOfSlot == null ? 0 : fixNumberOfSlot;
						}

						Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
						c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
						c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
						c.add(Restrictions.in(SingleCarparkInOutHistory.Property.carparkId.name(),StrUtil.getListIdByEntity(findSameCarpark)));
						c.setProjection(Projections.rowCount());
						Long singleResult = (Long) c.getSingleResult();
						int now = singleResult == null ? 0 : singleResult.intValue();
						return intValue - now <= 0 ? 0 : intValue - now;
					} finally {
						unitOfWork.end();
					}
				}
			}).intValue();
		} catch (ExecutionException e) {
			throw new DongluServiceException(e.getMessage(),e);
		}
		
	}

	@Override
	public List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(String plateNO, boolean order, SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotEmpty(SingleCarparkInOutHistory.Property.plateNo.name()));
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.plateNo.name()));
			c.add(Restrictions.ne(SingleCarparkInOutHistory.Property.plateNo.name(), ""));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			List<SimpleExpression> list = new ArrayList<>();
			for (String s : CarparkUtils.splitPlateNO(plateNO)) {
				SimpleExpression like = Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), s, MatchMode.ANYWHERE);
				list.add(like);
			}
			c.add(Restrictions.or(list.toArray(new SimpleExpression[list.size()])));
			List<SimpleExpression> listD = new ArrayList<>();
			for (SingleCarparkCarpark d : findSameCarpark(carpark)) {
				SimpleExpression eq = Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), d.getId());
				listD.add(eq);
			}
			c.add(Restrictions.or(listD.toArray(new SimpleExpression[listD.size()])));
			if (order) {
				c.addOrder(Order.asc(SingleCarparkInOutHistory.Property.inTime.name()));
			} else {
				c.addOrder(Order.desc(SingleCarparkInOutHistory.Property.inTime.name()));
			}
			return c.getResultList();
		} catch (Exception e) {
			return new ArrayList<>();
		} finally {
			unitOfWork.end();
		}
	}

	
	@Override
	public List<SingleCarparkInOutHistory> findAddNoPlateNOHistory(boolean order) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.or(Restrictions.eq(SingleCarparkInOutHistory.Property.plateNo.name(), ""), Restrictions.isEmpty(SingleCarparkInOutHistory.Property.plateNo.name()),
					Restrictions.isNull(SingleCarparkInOutHistory.Property.plateNo.name())));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			if (order) {
				c.addOrder(Order.asc(SingleCarparkInOutHistory.Property.inTime.name()));
			} else {
				c.addOrder(Order.desc(SingleCarparkInOutHistory.Property.inTime.name()));
			}
			return c.getResultList();
		} catch (Exception e) {
			return new ArrayList<>();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	@Transactional
	public Long deleteAllHistory() {
		//
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
		List<SingleCarparkInOutHistory> resultList = c.getResultList();
		DatabaseOperation<SingleCarparkInOutHistory> dom = DatabaseOperation.forClass(SingleCarparkInOutHistory.class, emprovider.get());
//		dom.executeQuery("delete from SingleCarparkInOutHistory");
		for (SingleCarparkInOutHistory singleCarparkInOutHistory : resultList) {
			dom.remove(singleCarparkInOutHistory);
		}
		//
		Criteria c1 = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkMonthlyUserPayHistory.class);
		List<SingleCarparkMonthlyUserPayHistory> resultList1 = c1.getResultList();
		DatabaseOperation<SingleCarparkMonthlyUserPayHistory> dom1 = DatabaseOperation.forClass(SingleCarparkMonthlyUserPayHistory.class, emprovider.get());
		for (SingleCarparkMonthlyUserPayHistory h : resultList1) {
			dom1.remove(h);
		}
		//
		Criteria c2 = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkReturnAccount.class);
		List<SingleCarparkReturnAccount> resultList2 = c2.getResultList();
		DatabaseOperation<SingleCarparkReturnAccount> dom2 = DatabaseOperation.forClass(SingleCarparkReturnAccount.class, emprovider.get());
		for (SingleCarparkReturnAccount h : resultList2) {
			dom2.remove(h);
		}
		//
		Criteria c3 = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkOpenDoorLog.class);
		List<SingleCarparkOpenDoorLog> resultList3 = c3.getResultList();
		DatabaseOperation<SingleCarparkOpenDoorLog> dom3 = DatabaseOperation.forClass(SingleCarparkOpenDoorLog.class, emprovider.get());
		for (SingleCarparkOpenDoorLog h : resultList3) {
			dom3.remove(h);
		}
		
		return 1L;
	}

	@Override
	public List<SingleCarparkInOutHistory> findHistoryFactMoneyNotReturn(String userName) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.returnAccount.name()));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.operaName.name(), userName));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findHistoryFreeMoneyNotReturn(String userName) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.freeReturnAccount.name()));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.operaName.name(), userName));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public float findOneDayMaxCharge(Long carType, Long carparkId) {
		unitOfWork.begin();
		try {
			Criteria ccs=CriteriaUtils.createCriteria(emprovider.get(), CarparkChargeStandard.class);
			
			DatabaseOperation<CarparkCarType> dom = DatabaseOperation.forClass(CarparkCarType.class, emprovider.get());
			CarparkCarType type = dom.getEntityWithId(carType);
			ccs.add(Restrictions.eq("carparkCarType", type));
			ccs.add(Restrictions.eq("using", true));
			List<CarparkChargeStandard> ccsList = ccs.getResultList();
			if (StrUtil.isEmpty(ccsList)) {
				return 0;
			}
			if (ccsList.size()>1) {
				Criteria c=CriteriaUtils.createCriteria(emprovider.get(), Holiday.class);
				c.add(Restrictions.eq("start", new Date()));
				List<Holiday> resultList = c.getResultList();
				CarparkHolidayTypeEnum hilidayType=CarparkHolidayTypeEnum.工作日;
				if (resultList.size()>0) {
					hilidayType=CarparkHolidayTypeEnum.非工作日;
				}
				for (CarparkChargeStandard o : ccsList) {
					if (o.getCarparkHolidayTypeEnum().equals(hilidayType)) {
						return o.getOnedayMaxCharge();
					}
				}
			}
			return ccsList.get(0).getOnedayMaxCharge();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public float countTodayCharge(String plateNo,Date date, Date e) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.between(SingleCarparkInOutHistory.Property.outTime.name(), date,e));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.plateNo.name(), plateNo));
			c.setProjection(Projections.sum(SingleCarparkInOutHistory.Property.factMoney.name()));
			Double singleResultOrNull = (Double) c.getSingleResultOrNull();
			return singleResultOrNull == null ? 0 : singleResultOrNull.floatValue();
		} finally {
			unitOfWork.end();
		}
	}

	public List<SingleCarparkCarpark> findSameCarpark(SingleCarparkCarpark carpark) {
		try {
			return carparkCache.get("findSameCarpark-"+carpark.getId(), new Callable<List<SingleCarparkCarpark>>() {

				@Override
				public List<SingleCarparkCarpark> call() throws Exception {
					List<SingleCarparkCarpark> findCarparkToLevel = findCarparkToLevel();
					for (SingleCarparkCarpark singleCarparkCarpark : findCarparkToLevel) {
						List<SingleCarparkCarpark> list = new ArrayList<>();
						getCarpaek(singleCarparkCarpark, list);
						// System.out.println(list);
						if (list.contains(carpark)) {
							return list;
						}
					}
					return null;
				}
			});
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public List<SingleCarparkCarpark> findSameCarpark(Long carparkid) {
		DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
		SingleCarparkCarpark entityWithId = dom.getEntityWithId(carparkid);
		List<SingleCarparkCarpark> list = new ArrayList<>();
		getCarpaek(entityWithId, list);

		return list;
	}

	private void getCarpaek(SingleCarparkCarpark carpark, List<SingleCarparkCarpark> list) {
		list.add(carpark);
		if (!StrUtil.isEmpty(carpark.getChilds())) {
			for (SingleCarparkCarpark singleCarparkCarpark : carpark.getChilds()) {
				getCarpaek(singleCarparkCarpark, list);
			}
		}
	}
	private void getChildCarpaek(SingleCarparkCarpark carpark, List<SingleCarparkCarpark> list) {
		if (!StrUtil.isEmpty(carpark.getChilds())) {
			for (SingleCarparkCarpark singleCarparkCarpark : carpark.getChilds()) {
				list.add(singleCarparkCarpark);
				getCarpaek(singleCarparkCarpark, list);
			}
		}
	}
	public List<SingleCarparkCarpark> findCarparkToLevel() {
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
			@Override
			public boolean apply(SingleCarparkCarpark arg0) {
				return arg0.getParent() == null;
			}
		});

		List<SingleCarparkCarpark> list = new ArrayList<>(filter);
		return list;
	}

	@Override
	@Transactional
	public Long saveOpenDoorLog(SingleCarparkOpenDoorLog openDoor) {
		DatabaseOperation<SingleCarparkOpenDoorLog> dom = DatabaseOperation.forClass(SingleCarparkOpenDoorLog.class, emprovider.get());
		if (openDoor.getId() == null) {
			dom.insert(openDoor);
		} else {
			dom.save(openDoor);
		}
		return openDoor.getId();
	}

	@Override
	public List<SingleCarparkOpenDoorLog> findOpenDoorLogBySearch(String operaName, Date start, Date end, String deviceName) {
		unitOfWork.begin();
		try {
			Criteria c = createFindOpenDoorLogBySearchCriteria(operaName, start, end, deviceName);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	/**
	 * @param operaName
	 * @param start
	 * @param end
	 * @param deviceName
	 * @return
	 */
	public Criteria createFindOpenDoorLogBySearchCriteria(String operaName, Date start, Date end, String deviceName) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkOpenDoorLog.class);
		if (!StrUtil.isEmpty(operaName)) {
			c.add(Restrictions.like(SingleCarparkOpenDoorLog.Property.operaName.name(), operaName, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(SingleCarparkOpenDoorLog.Property.operaDate.name(), start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(SingleCarparkOpenDoorLog.Property.operaDate.name(), end));
		}
		if (!StrUtil.isEmpty(deviceName)) {
			c.add(Restrictions.like(SingleCarparkOpenDoorLog.Property.deviceName.name(), deviceName, MatchMode.ANYWHERE));
		}
		return c;
	}

	@Override
	public List<SingleCarparkInOutHistory> findHistoryByChildCarparkInOut(Long carparkId, String plateNO, Date inTime, Date outTime) {
		unitOfWork.begin();
		try {
			DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
			SingleCarparkCarpark entityWithId = dom.getEntityWithId(carparkId);
			if (StrUtil.isEmpty(entityWithId)) {
				return new ArrayList<>();
			}
			List<SingleCarparkCarpark> list=new ArrayList<>();
			getChildCarpaek(entityWithId, list);
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.between(SingleCarparkInOutHistory.Property.inTime.name(), inTime, outTime));
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.between(SingleCarparkInOutHistory.Property.outTime.name(), inTime, outTime));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.plateNo.name(), plateNO));
			c.add(Restrictions.in(SingleCarparkInOutHistory.Property.carparkId.name(), StrUtil.getListIdByEntity(list)));
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(Long id, String plateNO) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.plateNo.name(), plateNO));
			}
			if (!StrUtil.isEmpty(id)) {
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), id));
			}
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findCarInHistorys(int size) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.setFirstResult(0);
			c.setMaxResults(size);
			
			c.addOrder(Order.desc(SingleCarparkInOutHistory.Property.inTime.name()));
			List<SingleCarparkInOutHistory> resultList = c.getResultList();
			resultList=CarparkUtils.sortObjectPropety(resultList, "inTimeLabel", true);
			return resultList;
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkInOutHistory findInOutHistoryByPlateNO(String plateNO) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.plateNo.name(), plateNO));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.setFirstResult(0);
			c.setMaxResults(1);
			return (SingleCarparkInOutHistory) c.getSingleResultOrNull();
		} catch(Exception e){
			return null;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> searchHistoryByLikePlateNO(List<String> plateNOs, boolean order, SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
//			c.add(Restrictions.isNotEmpty(SingleCarparkInOutHistory.Property.plateNo.name()));
//			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.plateNo.name()));
//			c.add(Restrictions.ne(SingleCarparkInOutHistory.Property.plateNo.name(), ""));
			List<SimpleExpression> list = new ArrayList<>();
			for (String s : plateNOs) {
				SimpleExpression like = Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), s, MatchMode.ANYWHERE);
				list.add(like);
			}
			c.add(Restrictions.or(list.toArray(new SimpleExpression[list.size()])));
			if (!StrUtil.isEmpty(carpark)) {
//				List<SimpleExpression> listD = new ArrayList<>();
//				for (SingleCarparkCarpark d : findSameCarpark(carpark)) {
//					SimpleExpression eq = Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), d.getId());
//					listD.add(eq);
//				}
//				c.add(Restrictions.or(listD.toArray(new SimpleExpression[listD.size()])));
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), carpark.getId()));
			}
			if (order) {
				c.addOrder(Order.asc(SingleCarparkInOutHistory.Property.inTime.name()));
			} else {
				c.addOrder(Order.desc(SingleCarparkInOutHistory.Property.inTime.name()));
			}
			return c.getResultList();
		} catch (Exception e) {
			return new ArrayList<>();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public int findTotalCarIn(SingleCarparkCarpark carpark) {
		try {
			return numberCache.get("findTotalCarIn-"+carpark, new Callable<Number>() {
				@Override
				public Number call() throws Exception {
					if (carpark==null) {
						return 0;
					}
					unitOfWork.begin();
					try {
						Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
						c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
						c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), carpark.getId()));
						c.setProjection(Projections.rowCount());
						Long singleResultOrNull = (Long) c.getSingleResultOrNull();
						
						return singleResultOrNull==null?0:singleResultOrNull.intValue();
					} finally{
						unitOfWork.end();
					}
				}
			}).intValue();
		} catch (ExecutionException e) {
			throw new DongluServiceException(e.getMessage(), e);
		}
	}

	@Override
	public int findTotalTempCarIn(SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "临时车"));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), carpark.getId()));
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			
			return singleResultOrNull==null?0:singleResultOrNull.intValue();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public int findTotalFixCarIn(SingleCarparkCarpark carpark) {
		try {
			return numberCache.get("findTotalFixCarIn-"+carpark, new Callable<Number>() {
				@Override
				public Number call() throws Exception {
					if (StrUtil.isEmpty(carpark)) {
						return 0;
					}
					unitOfWork.begin();
					try {
						Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
						c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), "固定车"));
						c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
						c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), carpark.getId()));
						c.add(Restrictions.or(Restrictions.eq(SingleCarparkInOutHistory.Property.isCountSlot.name(),true),Restrictions.isNull(SingleCarparkInOutHistory.Property.isCountSlot.name())));
						c.setProjection(Projections.rowCount());
						Long singleResultOrNull = (Long) c.getSingleResultOrNull();
						
						return singleResultOrNull==null?0:singleResultOrNull.intValue();
					} finally{
						unitOfWork.end();
					}
				}
			}).intValue();
		} catch (ExecutionException e) {
			throw new DongluServiceException(e.getMessage(),e);
		}
		
	}

	@Override
	public SingleCarparkInOutHistory findInOutById(Long id) {
		unitOfWork.begin();
		try {
    		DatabaseOperation<SingleCarparkInOutHistory> dom = DatabaseOperation.forClass(SingleCarparkInOutHistory.class, emprovider.get());
    		SingleCarparkInOutHistory entityWithId = dom.getEntityWithId(id);
    		return entityWithId;
		}catch(Exception e){
			return null;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public float findAcrossDayPrice(CarTypeEnum carType, Long carparkId) {
		unitOfWork.begin();
		try {
			DatabaseOperation<CarparkCarType> dom = DatabaseOperation.forClass(CarparkCarType.class, emprovider.get());
			CarparkCarType type = dom.getEntityWithId(carType.index());
			List<CarparkChargeStandard> carparkChargeStandardList = type.getCarparkChargeStandardList();
			for (CarparkChargeStandard carparkChargeStandard : carparkChargeStandardList) {
				if (!StrUtil.isEmpty(carparkChargeStandard.getUsing())&&carparkChargeStandard.getUsing()&&carparkChargeStandard.getCarpark().getId().equals(carparkId)) {
					return carparkChargeStandard.getAcrossDayPrice()==null?0:carparkChargeStandard.getAcrossDayPrice();
				}
			}
			return 0;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findInHistoryThanIdMore(Long id,List<Long> errorIds) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.inTime.name(), DateTime.now().minusHours(1).toDate()));
			if (!StrUtil.isEmpty(errorIds)) {
				List<SimpleExpression> l=new ArrayList<>();
				if (!StrUtil.isEmpty(errorIds)) {
					for (Long long1 : errorIds) {
						SimpleExpression eq = Restrictions.eq("id", long1);
						l.add(eq);
					}
				}
				c.add(Restrictions.or(Restrictions.gt("id", id),Restrictions.or(l.toArray(new SimpleExpression[l.size()]))));
			}else{
				c.add(Restrictions.gt("id", id));
			}
			c.setFirstResult(0);
			c.setMaxResults(50);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findOutHistoryThanIdMore(Long id,List<Long> errorIds) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.outTime.name()));
//			c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.outTime.name(), DateTime.now().minusHours(1).toDate()));
			if (id==0) {
				c.add(Restrictions.gt(SingleCarparkInOutHistory.Property.outTime.name(), DateTime.now().minusHours(1).toDate()));
			}
			if (!StrUtil.isEmpty(errorIds)) {
				List<SimpleExpression> l=new ArrayList<>();
				if (!StrUtil.isEmpty(errorIds)) {
					for (Long long1 : errorIds) {
						SimpleExpression eq = Restrictions.eq("id", long1);
						l.add(eq);
					}
				}
				c.add(Restrictions.or(Restrictions.gt("id", id), Restrictions.or(l.toArray(new SimpleExpression[l.size()]))));
			} else {
				c.add(Restrictions.gt("id", id));
			}
			c.setFirstResult(0);
			c.setMaxResults(50);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> searchNotOutHistory(int page,int rows,String plateNO) {
		unitOfWork.begin();
		try {
			return searchNotOutHistoryByInfo(page*rows, rows, "%"+plateNO+"%", null);
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public Long countNotOutHistory(String plateNO) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), plateNO,MatchMode.ANYWHERE));
			}
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			return singleResultOrNull==null?0:singleResultOrNull;
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	@Transactional
	public void clearCarHistoryWithInByDate(int date) {
		DatabaseOperation<SingleCarparkInOutHistory> dom = DatabaseOperation.forClass(SingleCarparkInOutHistory.class, emprovider.get());
		dom.executeQuery(SingleCarparkInOutHistory.Query.deleteWithNotOutByDate.query(), new DateTime().minusDays(date).toDate());
	}

	@Override
	@Transactional
	public Long saveLockCar(SingleCarparkLockCar m) {
		Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkLockCar.class);
		c.add(Restrictions.eq(SingleCarparkLockCar.Property.plateNO.name(), m.getPlateNO()));
		SingleCarparkLockCar singleResultOrNull = (SingleCarparkLockCar) c.getSingleResultOrNull();
		if (singleResultOrNull!=null) {
			m.setId(singleResultOrNull.getId());
		}
		
		DatabaseOperation<SingleCarparkLockCar> dom = DatabaseOperation.forClass(SingleCarparkLockCar.class, emprovider.get());
		if (StrUtil.isEmpty(m.getId())) {
			dom.insert(m);
		}else{
			dom.save(m);
		}
		return null;
	}

	@Override
	public List<SingleCarparkLockCar> findLockCar(String plateNO, String status, String operaName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkLockCar.class);
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.like(SingleCarparkLockCar.Property.plateNO.name(), plateNO, MatchMode.ANYWHERE));
			}
			if (!StrUtil.isEmpty(status)) {
				c.add(Restrictions.eq(SingleCarparkLockCar.Property.status.name(), status));
			}
			if (!StrUtil.isEmpty(operaName)) {
				c.add(Restrictions.like(SingleCarparkLockCar.Property.operaName.name(), operaName, MatchMode.ANYWHERE));
			}
			if (!StrUtil.isEmpty(start)) {
				c.add(Restrictions.ge(SingleCarparkLockCar.Property.createTime.name(), StrUtil.getTodayTopTime(start)));
			}
			if (!StrUtil.isEmpty(end)) {
				c.add(Restrictions.le(SingleCarparkLockCar.Property.createTime.name(), StrUtil.getTodayBottomTime(end)));
			}
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkLockCar findLockCarByPlateNO(String plateNO,Boolean isLock) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkLockCar.class);
			c.add(Restrictions.eq(SingleCarparkLockCar.Property.plateNO.name(), plateNO));
			if (!StrUtil.isEmpty(isLock)) {
				if (isLock) {
					c.add(Restrictions.eq(SingleCarparkLockCar.Property.status.name(), SingleCarparkLockCar.Status.已锁定.name()));
				}else{
					c.add(Restrictions.eq(SingleCarparkLockCar.Property.status.name(), SingleCarparkLockCar.Status.已解锁.name()));
				}
			}
			c.setFirstResult(0);
			c.setMaxResults(1);
			SingleCarparkLockCar singleResultOrNull = (SingleCarparkLockCar) c.getSingleResultOrNull();
			return singleResultOrNull;
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public Long lockCar(String plateNO) {
		SingleCarparkLockCar m = new SingleCarparkLockCar();
		m.setPlateNO(plateNO);
		m.setStatus("已锁定");
		m.setOperaName(System.getProperty("userName"));
		m.setCreateTime(new Date());
		return saveLockCar(m);
	}
	@Transactional
	@Override
	public Long updateCarparkStillTime(SingleCarparkCarpark carpark,SingleCarparkDevice device, String plateNO, String bigImg) {
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), CarparkStillTime.class);
			c.add(Restrictions.isNull(CarparkStillTime.Property.outTime.name()));
			c.add(Restrictions.eq(CarparkStillTime.Property.plateNO.name(), plateNO));
			CarparkStillTime cst = (CarparkStillTime) c.getSingleResultOrNull();
			DatabaseOperation<CarparkStillTime> dom = DatabaseOperation.forClass(CarparkStillTime.class, emprovider.get());
			Date outTime = new Date();
			String inType = device.getInType();
			if (cst==null) {
				cst=new CarparkStillTime();
				cst.setCarparkId(carpark.getId());
				cst.setCarparkName(carpark.getName());
				cst.setPlateNO(plateNO);
				cst.setInTime(outTime);
				cst.setInBigImg(bigImg);
				cst.setInDevice(device.getName());
				dom.insert(cst);
			}else{
				if (inType.indexOf("进口")>-1) {
					CarparkStillTime carparkStillTime = new CarparkStillTime();
					carparkStillTime=new CarparkStillTime();
					carparkStillTime.setCarparkId(device.getCarpark().getId());
					carparkStillTime.setCarparkName(device.getCarpark().getName());
					carparkStillTime.setPlateNO(plateNO);
					carparkStillTime.setInTime(outTime);
					carparkStillTime.setInBigImg(bigImg);
					carparkStillTime.setInDevice(device.getName());
					dom.insert(carparkStillTime);
					
				}
				if (inType.indexOf("出口")>-1) {
					SingleCarparkCarpark parent = carpark.getParent();
					if (parent!=null) {
						CarparkStillTime carparkStillTime = new CarparkStillTime();
						carparkStillTime=new CarparkStillTime();
						carparkStillTime.setCarparkId(parent.getId());
						carparkStillTime.setCarparkName(parent.getName());
						carparkStillTime.setPlateNO(plateNO);
						carparkStillTime.setInTime(outTime);
						carparkStillTime.setInBigImg(bigImg);
						carparkStillTime.setInDevice(device.getName());
						dom.insert(carparkStillTime);
					}
				}
				cst.setOutBigImg(bigImg);
				cst.setOutTime(outTime);
				cst.setOutDevice(device.getName());
				cst.setStillSecond(CarparkUtils.MinusMinute(cst.getInTime(),outTime));
			}
			return cst.getId();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<CarparkStillTime> findCarparkStillTime(String plateNO, Date inTime) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), CarparkStillTime.class);
			c.add(Restrictions.eq(CarparkStillTime.Property.plateNO.name(), plateNO));
			c.add(Restrictions.ge(CarparkStillTime.Property.inTime.name(), inTime));
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}
	@Override
	public Map<String, Long> getDeviceFlows(boolean inOrOut,Date start,Date end){
		unitOfWork.begin();
		try {
			String deviceName=SingleCarparkInOutHistory.Property.inDevice.name();
			String time=SingleCarparkInOutHistory.Property.inTime.name();
			if (!inOrOut) {
				deviceName=SingleCarparkInOutHistory.Property.outDevice.name();
				time=SingleCarparkInOutHistory.Property.outTime.name();
			}
			Map<String, Long> mapDeviceToCount=new HashMap<>();
			String qlString = "select count(h),h."+deviceName+" from SingleCarparkInOutHistory h where h."+deviceName+" is not null and h."+time+" between ?1 and ?2 group by h."+deviceName;
			System.out.println(qlString);
			Query createQuery = emprovider.get().createQuery(qlString);
			createQuery.setParameter(1, start);
			createQuery.setParameter(2, end);
			List resultList = createQuery.getResultList();
			for (Object object : resultList) {
				Object[] os=(Object[]) object;
				mapDeviceToCount.put(os[1]+"", (Long) os[0]);
			}
			return mapDeviceToCount;
		} catch (Exception e) {
		}finally{
			unitOfWork.end();
		}
		return Collections.EMPTY_MAP;
	}

	@Override
	public List<String> findAllDeviceName(boolean inOrOut) {
		unitOfWork.begin();
		try {
			String deviceName=SingleCarparkInOutHistory.Property.inDevice.name();
			if (!inOrOut) {
				deviceName=SingleCarparkInOutHistory.Property.outDevice.name();
			}
			String qlString = "select h."+deviceName+" from SingleCarparkInOutHistory h where h."+deviceName+" is not null group by h."+deviceName;
			Query createQuery = emprovider.get().createQuery(qlString);
			List resultList = createQuery.getResultList();
			for (Object object : resultList) {
				System.out.println(object);
			}
			return resultList;
		} catch (Exception e) {
		}finally{
			unitOfWork.end();
		}
		return Collections.EMPTY_LIST;
	}

	@Override
	public List<DeviceErrorMessage> findDeviceErrorMessageBySearch(int first, int max, String deviceName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createDeviceErrorMessageBySearchCriteria(deviceName, start, end);
			c.setFirstResult(first);
			c.setMaxResults(max);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	/**
	 * @param deviceName
	 * @param start
	 * @param end
	 * @return
	 */
	private Criteria createDeviceErrorMessageBySearchCriteria(String deviceName, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), DeviceErrorMessage.class);
		if (!StrUtil.isEmpty(deviceName)) {
			c.add(Restrictions.like(DeviceErrorMessage.Property.deviceName.name(), deviceName, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(DeviceErrorMessage.Property.checkDate.name(), start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(DeviceErrorMessage.Property.checkDate.name(), end));
		}
		return c;
	}

	@Override
	public Long countDeviceErrorMessageBySearch(String deviceName, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c=createDeviceErrorMessageBySearchCriteria(deviceName, start, end);
			c.setProjection(Projections.rowCount());
			Long l=(Long) c.getSingleResultOrNull();
			return l==null?0L:l;
		} finally{
			unitOfWork.end();
		}
	}

	@Transactional
	@Override
	public Long saveDeviceErrorMessage(DeviceErrorMessage deviceErrorMessage) {
		DatabaseOperation<DeviceErrorMessage> dom = DatabaseOperation.forClass(DeviceErrorMessage.class, emprovider.get());
		if (deviceErrorMessage.getId() == null) {
			dom.insert(deviceErrorMessage);
		} else {
			dom.save(deviceErrorMessage);
		}
		return deviceErrorMessage.getId();
	}
	
	@Override
	public DeviceErrorMessage findDeviceErrorMessageByDevice(SingleCarparkDevice device) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), DeviceErrorMessage.class);
			c.add(Restrictions.eq(DeviceErrorMessage.Property.ip.name(), device.getIp()));
			c.add(Restrictions.isNull(DeviceErrorMessage.Property.nomalTime.name()));
			List<DeviceErrorMessage> list = c.getResultList();
			if (list.size()>1) {
				DatabaseOperation<DeviceErrorMessage> dom = DatabaseOperation.forClass(DeviceErrorMessage.class, emprovider.get());
				for (int i = 0; i < list.size()-1; i++) {
					DeviceErrorMessage deviceErrorMessage = list.get(i);
					dom.remove(deviceErrorMessage);
				}
				return list.get(list.size()-1);
			}else if(list.size()==1){
				return list.get(0);
			}
			return null;
		} finally{
			unitOfWork.end();
		}
	}

	@Transactional
	@Override
	public Long saveCarparkOffLineHistory(CarparkOffLineHistory carparkOffLineHistory) {
		DatabaseOperation<CarparkOffLineHistory> dom = DatabaseOperation.forClass(CarparkOffLineHistory.class, emprovider.get());
		if (carparkOffLineHistory.getId() == null) {
			dom.insert(carparkOffLineHistory);
		} else {
			dom.save(carparkOffLineHistory);
		}
		return carparkOffLineHistory.getId();
	}

	@Override
	public List<CarparkOffLineHistory> findCarparkOffLineHistoryBySearch(int first, int max, String plateNO, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createCarparkOffLineHistoryBySearchCriteria(plateNO, start, end);
			c.setFirstResult(first);
			c.setMaxResults(max);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	/**
	 * @param plateNO
	 * @param start
	 * @param end
	 * @return
	 */
	public Criteria createCarparkOffLineHistoryBySearchCriteria(String plateNO, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), CarparkOffLineHistory.class);
		if (!StrUtil.isEmpty(plateNO)) {
			c.add(Restrictions.like(CarparkOffLineHistory.Property.plateNO.name(), plateNO, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(CarparkOffLineHistory.Property.inTime.name(), start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(CarparkOffLineHistory.Property.inTime.name(), end));
		}
		return c;
	}

	@Override
	public Long countCarparkOffLineHistoryBySearch(String plateNO, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createCarparkOffLineHistoryBySearchCriteria(plateNO, start, end);
			c.setProjection(Projections.rowCount());
			Long l=(Long) c.getSingleResultOrNull();
			return l==null?0l:l;
		} finally{
			unitOfWork.end();
		}
	}

	public List<SingleCarparkFreeTempCar> findTempCarFreeByLike(int start, int maxValue, String plateNo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkFreeTempCar.class);
			if (!StrUtil.isEmpty(plateNo)) {
				c.add(Restrictions.like(SingleCarparkFreeTempCar.Property.plateNo.name(), plateNo,MatchMode.ANYWHERE));
			}
			c.setFirstResult(start);
			c.setMaxResults(maxValue);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}
	@Override
	public Long countTempCarFreeByLike(String plateNo){
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkFreeTempCar.class);
			if (!StrUtil.isEmpty(plateNo)) {
				c.add(Restrictions.like(SingleCarparkFreeTempCar.Property.plateNo.name(), plateNo, MatchMode.ANYWHERE));
			}
			c.setProjection(Projections.rowCount());
			Long l = (Long) c.getSingleResultOrNull();
			return l == null ? 0 : l;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkFreeTempCar findTempCarFreeByPlateNO(String plateNo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkFreeTempCar.class);
			c.add(Restrictions.eq(SingleCarparkFreeTempCar.Property.plateNo.name(), plateNo));
			c.setFirstResult(0);
			c.setMaxResults(1);
			return (SingleCarparkFreeTempCar) c.getSingleResultOrNull();
		} finally{
			unitOfWork.end();
		}
	}
	@Transactional
	@Override
	public Long deleteTempCarFree(SingleCarparkFreeTempCar ft) {
		DatabaseOperation<SingleCarparkFreeTempCar> dom = DatabaseOperation.forClass(SingleCarparkFreeTempCar.class, emprovider.get());
		dom.remove(ft);
		return ft.getId();
	}
	@Transactional
	@Override
	public Long saveTempCarFree(SingleCarparkFreeTempCar ft) {
		DatabaseOperation<SingleCarparkFreeTempCar> dom = DatabaseOperation.forClass(SingleCarparkFreeTempCar.class, emprovider.get());
		if (ft.getId()==null) {
			dom.insert(ft);
		}else{
			dom.save(ft);
		}
		return ft.getId();
	}

	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByUser(SingleCarparkUser user, Boolean b) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.userName.name(), user.getName()));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			if (b!=null) {
				if (b) {
					c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.reviseInTime.name()));
				}else{
					c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.reviseInTime.name()));
				}
			}
			return c.getResultList();
		} catch(Exception e){
			return null;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(SingleCarparkCarpark carpark, String plateNO,
			boolean b) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.plateNo.name(), plateNO));
			}
			if (!StrUtil.isEmpty(carpark)) {
//				carpark = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get()).getEntityWithId(carpark.getId());
//				List<SingleCarparkCarpark> list=new ArrayList<>();
//				list.add(carpark);
//				carpark.getChildCarpark(carpark, list);
//				c.add(Restrictions.in(SingleCarparkInOutHistory.Property.carparkId.name(), StrUtil.getListIdByEntity(list)));
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), carpark.getId()));
			}
			if (b) {
				c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.reviseInTime.name()));
			}else{
				c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.reviseInTime.name()));
			}
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}
	@Transactional
	@Override
	public Long saveImageHistory(SingleCarparkImageHistory ih) {
		DatabaseOperation<SingleCarparkImageHistory> dom = DatabaseOperation.forClass(SingleCarparkImageHistory.class, emprovider.get());
		if (ih.getId()!=null) {
			dom.save(ih);
		}else{
			dom.insert(ih);
		}
		return ih.getId();
	}
	@Transactional
	@Override
	public Long deleteImageHistory(SingleCarparkImageHistory ih) {
		DatabaseOperation<SingleCarparkImageHistory> dom = DatabaseOperation.forClass(SingleCarparkImageHistory.class, emprovider.get());
		dom.remove(ih);
		return ih.getId();
	}

	@Override
	public List<SingleCarparkImageHistory> findImageHistoryBySearch(int first, int max, String plate, String type,Date start,Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createImageHistoryBySearch(plate, type, start, end);
			c.setFirstResult(first);
			c.setMaxResults(max);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	/**
	 * @param plate
	 * @param type
	 * @param start
	 * @param end
	 * @return
	 */
	public Criteria createImageHistoryBySearch(String plate, String type, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkImageHistory.class);
		if (!StrUtil.isEmpty(plate)) {
			c.add(Restrictions.like("plateNO", plate, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(type)) {
			c.add(Restrictions.eq("type", type));
		}
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge("time", start));
		}
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le("time", end));
		}
		return c;
	}

	@Override
	public int countImageHistoryBySearch(String plate, String type, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createImageHistoryBySearch(plate, type, start, end);
			c.setProjection(Projections.rowCount());
			Long l = (Long) c.getSingleResultOrNull();
			return l == null ? 0 : l.intValue();
		} finally {
			unitOfWork.end();
		}
	}

    @Override
    public List<CarparkRecordHistory> findHaiYuRecordHistory(int start, int size, UpdateEnum[] updateEnums, ProcessEnum[] processEnums) {
		unitOfWork.begin();
		try {
			Criteria criteria = CriteriaUtils.createCriteria(this.emprovider.get(), CarparkRecordHistory.class);
			criteria.add(Restrictions.in("historyDetail.processState", processEnums));
			criteria.add(Restrictions.in("historyDetail.updateState", updateEnums));
			criteria.setFirstResult(start);
			criteria.setMaxResults(size);
			return criteria.getResultList();
		}finally {
			unitOfWork.end();
		}
    }

    @Override
	@Transactional
    public void updateHaiYuRecordHistory(List<Long> longList, ProcessEnum processEnum) {
		for (Long aLong : longList) {
			EntityManager entityManager = this.emprovider.get();
			CarparkRecordHistory reference = entityManager.getReference(CarparkRecordHistory.class, aLong);
			reference.getHistoryDetail().setProcessState(processEnum);
			reference.getHistoryDetail().setProcessTime(new Date());
			entityManager.merge(reference);
		}
	}
	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByCarparkAndPlateNO(SingleCarparkCarpark carpark, Collection<String> pns, boolean b) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			if (!StrUtil.isEmpty(pns)) {
				c.add(Restrictions.in(SingleCarparkInOutHistory.Property.plateNo.name(), pns));
			}
			if (!StrUtil.isEmpty(carpark)) {
				carpark = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get()).getEntityWithId(carpark.getId());
				List<SingleCarparkCarpark> list=new ArrayList<>();
				list.add(carpark);
				carpark.getChildCarpark(carpark, list);
				c.add(Restrictions.in(SingleCarparkInOutHistory.Property.carparkId.name(), StrUtil.getListIdByEntity(list)));
			}
			if (b) {
				c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.reviseInTime.name()));
			}else{
				c.add(Restrictions.isNotNull(SingleCarparkInOutHistory.Property.reviseInTime.name()));
			}
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findInOutHistoryByInTime(int i, int totalSlot, Set<String> plates, Date s) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.in(SingleCarparkInOutHistory.Property.plateNo.name(), plates));
			c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.inTime.name(), s));
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.addOrder(Order.asc(SingleCarparkInOutHistory.Property.inTime.name()));
			c.setFirstResult(i);
			c.setMaxResults(totalSlot);
			return c.getResultList();
		} finally{
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkOpenDoorLog> findOpenDoorLogBySearch(int startSize, int size, String operaName, Date start, Date end, String deviceName) {
		unitOfWork.begin();
		try {
			Criteria c = createFindOpenDoorLogBySearchCriteria(operaName, start, end, deviceName);
			c.setFirstResult(startSize);
			c.setMaxResults(size);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public Long countOpenDoorLogBySearch(String operaName, Date start, Date end, String deviceName) {
		unitOfWork.begin();
		try {
			Criteria c = createFindOpenDoorLogBySearchCriteria(operaName, start, end, deviceName);
			c.setProjection(Projections.rowCount());
			Long l = (Long) c.getSingleResultOrNull();
			return l == null ? 0 : l;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> searchNotOutHistory(int start, int size, String plateNo, SingleCarparkCarpark carpark) {
		unitOfWork.begin();
		try {
			return searchNotOutHistoryByInfo(start, size, plateNo, carpark);
		} finally {
			unitOfWork.end();
		}
	}
	private List<SingleCarparkInOutHistory> searchNotOutHistoryByInfo(int start, int size, String plateNo, SingleCarparkCarpark carpark){
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
		c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
		if (!StrUtil.isEmpty(plateNo)) {
			c.add(Restrictions.like(SingleCarparkInOutHistory.Property.plateNo.name(), plateNo));
		}
		if (!StrUtil.isEmpty(carpark)) {
			c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), carpark.getId()));
		}
		c.addOrder(Order.desc("id"));
		c.setFirstResult(start);
		c.setMaxResults(size);
		return c.getResultList();
	}

	@Override
	public List<SingleCarparkInOutHistory> findHistoryByIn(int start, int size, SingleCarparkCarpark carpark, String carType, Date startTime, Date endTime) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			if (!StrUtil.isEmpty(carType)) {
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carType.name(), carType));
			}
			if (!StrUtil.isEmpty(carpark)) {
				c.add(Restrictions.eq(SingleCarparkInOutHistory.Property.carparkId.name(), carpark.getId()));
			}
			if (!StrUtil.isEmpty(startTime)) {
				c.add(Restrictions.ge(SingleCarparkInOutHistory.Property.inTime.name(), startTime));
			}
			if (!StrUtil.isEmpty(endTime)) {
				c.add(Restrictions.le(SingleCarparkInOutHistory.Property.inTime.name(), endTime));
			}
			c.add(Restrictions.isNull(SingleCarparkInOutHistory.Property.outTime.name()));
			c.setFirstResult(start);
			c.setMaxResults(size);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public boolean exeUpdateSql(String sql) {
		unitOfWork.begin();
		try {
			Query query = emprovider.get().createNativeQuery(sql);
			int executeUpdate = query.executeUpdate();
			System.out.println("executeUpdate===" + executeUpdate);
			return true;
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<SingleCarparkInOutHistory> findHistoryThanId(Long id, int start, int size) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.gt("id", id));
			c.setFirstResult(start);
			c.setMaxResults(size);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	
	@Override
	public List<CarPayHistory> findCarPayHistoryThanId(Long id, int start, int size) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), CarPayHistory.class);
			c.add(Restrictions.gt("id", id));
			c.setFirstResult(start);
			c.setMaxResults(size);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	@Override
	public List<Double> countReturnMoney(String userName) {
		unitOfWork.begin();
		try {
			
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkInOutHistory.class);
			c.add(Restrictions.eq("operaName", userName));
			c.add(Restrictions.isNull("returnAccount"));
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.max("id"));
			projectionList.add(Projections.sum("shouldMoney"));
			projectionList.add(Projections.sum("factMoney"));
			projectionList.add(Projections.sum("freeMoney"));
			c.setProjection(projectionList);
//			Query query = emprovider.get()
//					.createNativeQuery("select max(id) as 1,sum(shouldMoney) as 2,sum(factMoney) as 3,sum(freeMoney) as 4  from SingleCarparkInOutHistory where operaName='" + userName + "' and returnAccount is null");
//			Object singleResult = query.getSingleResult();
//			System.out.println(singleResult);
			Object[] resultList = (Object[]) c.getSingleResultOrNull();
			List<Double> arrayList = new ArrayList<>();
			for (Object object : resultList) {
				if (object instanceof Long) {
					arrayList.add(Long.class.cast(object).doubleValue());
					continue;
				}
				if (object instanceof Double) {
					arrayList.add((Double) object);
				}
			}
			return arrayList;
		} finally {
			unitOfWork.end();
		}
	}

	@Transactional
	@Override
	public Long updateRecount(Long maxId, Long returnAccountId, boolean free) {
		Query query = emprovider.get().createNativeQuery("update SingleCarparkInOutHistory set returnAccount="+returnAccountId+" where returnAccount is null and outTime is not null and id<="+maxId);
		int executeUpdate = query.executeUpdate();
		if (free) {
			query = emprovider.get().createNativeQuery("update SingleCarparkInOutHistory set freeReturnAccount="+returnAccountId+" where freeReturnAccount is null and outTime is not null and id<="+maxId);
			executeUpdate += query.executeUpdate();
		}
		return executeUpdate*1l;
	}
	
}
