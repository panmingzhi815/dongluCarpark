package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.Criterion;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.criterion.SimpleExpression;
import org.criteria4jpa.order.Order;
import org.criteria4jpa.projection.Projections;
import org.joda.time.DateTime;

import com.donglu.carpark.server.imgserver.ImageServerUI;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.domain.db.singlecarpark.CameraTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.ProcessEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UpdateEnum;
import com.dongluhitec.card.domain.db.singlecarpark.haiyu.UserHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.plateDevice.PlateNOJNA;
import com.dongluhitec.card.hardware.plateDevice.PlateNOResult;
import com.dongluhitec.card.hardware.plateDevice.bean.PlateDownload;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;
@SuppressWarnings("unchecked")
public class CarparkUserServiceImpl implements CarparkUserService {

	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	
	@Override
	@Transactional
	public Long saveUser(SingleCarparkUser user) {
		DatabaseOperation<SingleCarparkUser> dom = DatabaseOperation.forClass(SingleCarparkUser.class, emprovider.get());
		if (user.getId() == null) {
			dom.insert(user);
			emprovider.get().persist(new UserHistory(user,UpdateEnum.新添加));
		} else {
			dom.save(user);
			if (user.isCreateHistory()) {
				emprovider.get().persist(new UserHistory(user,UpdateEnum.被修改));
			}
		}
		String[] split = user.getPlateNo().split(",");
		for (String string : split) {
			removeUserCache(user.getCarpark(), string);
		}
		return user.getId();
	}
	/**
	 * @param user
	 * @param plateNo
	 */
	public void removeUserCache(SingleCarparkCarpark carpark, String plateNo) {
		if (carpark==null) {
			return;
		}
		Long id = carpark.getId();
		userCache.invalidate("findUserByPlateNo-"+plateNo+"-"+id);
		userCache.invalidate("findUserByPlateNo-"+plateNo+"-null");
		userCache.invalidate("findUserByNameAndCarpark-"+plateNo+"-"+id);
		removeUserCache(carpark.getParent(), plateNo);
	}
	@Override
	@Transactional
	public Long deleteUser(SingleCarparkUser user) {
		DatabaseOperation<SingleCarparkUser> dom = DatabaseOperation.forClass(SingleCarparkUser.class, emprovider.get());
		dom.remove(user.getId());
		emprovider.get().persist(new UserHistory(user,UpdateEnum.被删除));
		String[] split = user.getPlateNo().split(",");
		for (String string : split) {
			removeUserCache(user.getCarpark(), string);
		}
		return user.getId();
	}
	
	@Override
	public List<SingleCarparkUser> findAll() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findByNameOrPlateNo(String name, String plateNo,String address,SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue) {
		unitOfWork.begin();
		try {
			Criteria c = createFindByNameOrPlateNoCriteria(name, plateNo, address, monthlyCharge, willOverdue, overdue);
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	/**
	 * @param name
	 * @param plateNo
	 * @param address
	 * @param monthlyCharge
	 * @param willOverdue
	 * @param overdue
	 * @return
	 */
	public Criteria createFindByNameOrPlateNoCriteria(String name, String plateNo, String address, SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue) {
		Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
		if (!StrUtil.isEmpty(name)) {
			c.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(plateNo)) {
			c.add(Restrictions.like("plateNo", plateNo, MatchMode.ANYWHERE));
		}
		if (willOverdue>0) {
			Date date = new DateTime(StrUtil.getTodayBottomTime(new Date())).plusDays(willOverdue).toDate();
			c.add(Restrictions.le(SingleCarparkUser.Property.validTo.name(), date));
		}
		if (!StrUtil.isEmpty(monthlyCharge)) {
			c.add(Restrictions.eq(SingleCarparkUser.Property.monthChargeId.name(), monthlyCharge.getId()));
		}
		if (!StrUtil.isEmpty(address)) {
			c.add(Restrictions.like("address", address, MatchMode.ANYWHERE));
		}
		if (!StrUtil.isEmpty(overdue)) {
			if (overdue.equals("是")) {
				c.add(Restrictions.le(SingleCarparkUser.Property.validTo.name(), new Date()));
			}else{
				c.add(Restrictions.ge(SingleCarparkUser.Property.validTo.name(), new Date()));
			}
		}
		return c;
	}
	
	static Cache<Object, Object> userCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.DAYS).build();
	@Override
	public SingleCarparkUser findUserByPlateNo(String plateNO,Long carparkId) {
		try {
			SingleCarparkUser user = (SingleCarparkUser) userCache.get("findUserByPlateNo-"+plateNO+"-"+carparkId, new Callable<SingleCarparkUser>() {
				@Override
				public SingleCarparkUser call() throws Exception {
					unitOfWork.begin();
					try {
						Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//					c.add(Restrictions.isNotNull("validTo"));
						
						if (!StrUtil.isEmpty(plateNO)) {
							c.add(Restrictions.like("plateNo", plateNO,MatchMode.ANYWHERE));
						}else{
							return new SingleCarparkUser();
						}
						if (!StrUtil.isEmpty(carparkId)) {
							List<SingleCarparkCarpark> list = getCarparkAndAllChild(carparkId);
							c.add(Restrictions.in("carpark",list));
						}
						c.setFirstResult(0);
						c.setMaxResults(1);
						SingleCarparkUser user = (SingleCarparkUser) c.getSingleResultOrNull();
						if (user==null||(!user.getType().equals("储值")&&StrUtil.isEmpty(user.getValidTo()))) {
							return new SingleCarparkUser();
						}
						return user;
					}finally{
						unitOfWork.end();
					}
				}
			});
			if (user.getId()==null) {
				return null;
			}
			return user;
		} catch (ExecutionException e) {
			throw new DongluServiceException("获取固定用户时发生错误！", e);
		}
		
	}
	/**
	 * @param carparkId
	 * @return
	 */
	public List<SingleCarparkCarpark> getCarparkAndAllChild(Long carparkId) {
		try {
			return (List<SingleCarparkCarpark>) CarparkServiceImpl.cache.get(getClass().getName()+"-getCarparkAndAllChild-"+carparkId, new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
					SingleCarparkCarpark entityWithId = dom.getEntityWithId(carparkId);
					List<SingleCarparkCarpark> list=entityWithId.getCarparkAndAllChilds();
					return list;
				}
			});
		} catch (ExecutionException e) {
			return new ArrayList<>();
		}
		
	}
	@Override
	public List<SingleCarparkUser> findAllUserByPlateNO(String plateNO, Long carparkId, Date validTo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//			c.add(Restrictions.isNotNull("validTo"));
			
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.like("plateNo", plateNO,MatchMode.ANYWHERE));
			}else{
				return new ArrayList<>();
			}
			if (!StrUtil.isEmpty(validTo)) {
				c.add(Restrictions.ge(SingleCarparkUser.Property.validTo.name(), validTo));
			}
			if (!StrUtil.isEmpty(carparkId)) {
				List<SingleCarparkCarpark> list = getCarparkAndAllChild(carparkId);
				c.add(Restrictions.in("carpark",list));
			}
			SingleCarparkUser user = (SingleCarparkUser) c.getSingleResultOrNull();
			if (user!=null&&!user.getType().equals("储值")&&StrUtil.isEmpty(user.getValidTo())) {
				return null;
			}
			return null;
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public int sumAllUserSlotByPlateNO(String plateNO, Long carparkId, Date validTo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//			c.add(Restrictions.isNotNull("validTo"));
			
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.eq("name", plateNO));
			}else{
				return 0;
			}
			if (!StrUtil.isEmpty(carparkId)) {
				List<SingleCarparkCarpark> list = getCarparkAndAllChild(carparkId);
				c.add(Restrictions.in("carpark",list));
			}
			c.setProjection(Projections.sum(SingleCarparkUser.Property.carparkSlot.name()));
			Object singleResultOrNull = c.getSingleResultOrNull();
			System.out.println(singleResultOrNull.getClass());
			return 0;
		}finally{
			unitOfWork.end();
		}
	
	}
	@Override
	public List<SingleCarparkUser> findUserByMonthChargeId(Long id) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			c.add(Restrictions.eq("monthChargeId", id));
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	@Transactional
	public Long saveUserByMany(List<SingleCarparkUser> list) {
		for (SingleCarparkUser user : list) {
			DatabaseOperation<SingleCarparkUser> dom = DatabaseOperation.forClass(SingleCarparkUser.class, emprovider.get());
			if (user.getId() == null) {
				dom.insert(user);
			} else {
				dom.save(user);
			}
		}
		return list.size()*1L;
	}
	@Override
	public List<SingleCarparkUser> findUserThanIdMore(Long id,List<Long> errorIds) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			if (!StrUtil.isEmpty(errorIds)) {
				List<SimpleExpression> list=new ArrayList<>();
				for (Long long1 : errorIds) {
					SimpleExpression eq = Restrictions.eq("id", long1);
					list.add(eq);
				}
				c.add(Restrictions.or(Restrictions.gt("id", id),Restrictions.or(list.toArray(new SimpleExpression[list.size()]))));
			}else{
				c.add(Restrictions.gt("id", id));
			}
			c.setFirstResult(0);
			c.setMaxResults(50);
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	
	@Override
	public List<SingleCarparkLockCar> findLockCarByPlateNO(String plateNO) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkLockCar.class);
			c.add(Restrictions.eq(SingleCarparkLockCar.Property.plateNO.name(), plateNO));
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	@Transactional
	public Long saveLockCar(SingleCarparkLockCar lc) {
		DatabaseOperation<SingleCarparkLockCar> dom = DatabaseOperation.forClass(SingleCarparkLockCar.class, emprovider.get());
		if (StrUtil.isEmpty(lc.getId())) {
			dom.insert(lc);
		}else{
			dom.update(lc);
		}
		return lc.getId();
	}
	@Override
	@Transactional
	public Long deleteLockCar(SingleCarparkLockCar lc) {
		DatabaseOperation<SingleCarparkLockCar> dom = DatabaseOperation.forClass(SingleCarparkLockCar.class, emprovider.get());
		dom.remove(lc);
		return lc.getId();
	}
	@Override
	@Transactional
	public Long savePrepaidUserPayHistory(SingleCarparkPrepaidUserPayHistory pph) {
		DatabaseOperation<SingleCarparkPrepaidUserPayHistory> dom = DatabaseOperation.forClass(SingleCarparkPrepaidUserPayHistory.class, emprovider.get());
		if (StrUtil.isEmpty(pph.getId())) {
			dom.insert(pph);
		}else{
			dom.update(pph);
		}
		return pph.getId();
	}
	
	@Override
	public List<SingleCarparkPrepaidUserPayHistory> findPrepaidUserPayHistoryList(int first, int max, String userName, String plateNO, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindPrepaidUserPayHistoryCriteria(userName, plateNO, start, end);
			c.setFirstResult(first);
			c.setMaxResults(max);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	/**
	 * @param userName
	 * @param plateNO
	 * @param start
	 * @param end
	 * @return
	 */
	public Criteria createFindPrepaidUserPayHistoryCriteria(String userName, String plateNO, Date start, Date end) {
		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkPrepaidUserPayHistory.class);
		if (!StrUtil.isEmpty(plateNO)) {
			c.add(Restrictions.like(SingleCarparkPrepaidUserPayHistory.Property.plateNO.name(), plateNO, MatchMode.ANYWHERE));
		}
		
		if (!StrUtil.isEmpty(userName)) {
			c.add(Restrictions.like(SingleCarparkPrepaidUserPayHistory.Property.userName.name(), userName, MatchMode.ANYWHERE));
		}
		
		if (!StrUtil.isEmpty(start)) {
			c.add(Restrictions.ge(SingleCarparkPrepaidUserPayHistory.Property.createTime.name(), start));
		}
		
		if (!StrUtil.isEmpty(end)) {
			c.add(Restrictions.le(SingleCarparkPrepaidUserPayHistory.Property.createTime.name(), end));
		}
		return c;
	}
	@Override
	public int countPrepaidUserPayHistoryList(String userName, String plateNO, Date start, Date end) {
		unitOfWork.begin();
		try {
			Criteria c = createFindPrepaidUserPayHistoryCriteria(userName, plateNO, start, end);
			c.setProjection(Projections.rowCount());
			Long singleResultOrNull = (Long) c.getSingleResultOrNull();
			return singleResultOrNull==null?0:singleResultOrNull.intValue();
		} finally {
			unitOfWork.end();
		}
	}
	@Override
	public SingleCarparkUser findUserById(Long userId) {
		unitOfWork.begin();
		try {
			SingleCarparkUser find = emprovider.get().find(SingleCarparkUser.class, userId);
			return find;
		} finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findUserByNameAndCarpark(String name, SingleCarparkCarpark carpark, Date validTo) {
		try {
			return (List<SingleCarparkUser>) userCache.get("findUserByNameAndCarpark-"+name+"-"+carpark.getId(), new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					unitOfWork.begin();
					try {
						Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//					c.add(Restrictions.isNotNull("validTo"));
						
						if (!StrUtil.isEmpty(name)) {
							c.add(Restrictions.like(SingleCarparkUser.Property.plateNo.name(), name,MatchMode.ANYWHERE));
						}else{
							return new ArrayList<>();
						}
						if (!StrUtil.isEmpty(validTo)) {
							c.add(Restrictions.ge(SingleCarparkUser.Property.validTo.name(), validTo));
						}
						if (!StrUtil.isEmpty(carpark)) {
							DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
							SingleCarparkCarpark entityWithId = dom.getEntityWithId(carpark.getId());
							List<SingleCarparkCarpark> list=entityWithId.getCarparkAndAllChilds();
							c.add(Restrictions.in("carpark",list));
						}
						List<SingleCarparkUser> resultList = c.getResultList();
						resultList=resultList.stream().filter(new Predicate<SingleCarparkUser>() {
							@Override
							public boolean test(SingleCarparkUser user) {
								return !user.getType().equals("储值")&&!StrUtil.isEmpty(user.getValidTo());
							}
						}).collect(Collectors.toList());
						return resultList;
					}finally{
						unitOfWork.end();
					}
				}
			});
		} catch (ExecutionException e) {
			throw new DongluServiceException("查找车牌所有用户时发生错误！", e);
		}
		
	
	}
	@Override
	public SingleCarparkUser findUserByParkingSpace(String parkingSpace) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			c.add(Restrictions.eq(SingleCarparkUser.Property.parkingSpace.name(), parkingSpace));
			return (SingleCarparkUser) c.getSingleResultOrNull();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findUserByPlateNoLikeSize(int start, int size, String plateNO, int likeSize, Long carparkId, Date validTo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
//			c.add(Restrictions.isNotNull("validTo"));
			
			if (!StrUtil.isEmpty(plateNO)) {
				if (likeSize<=0||likeSize>=7) {
					c.add(Restrictions.like(SingleCarparkUser.Property.plateNo.name(), plateNO,MatchMode.ANYWHERE));
				}else{
					Set<String> set = CarparkUtils.splitPlateWithIgnoreSize(plateNO, 1);
					List<Criterion>  list=new ArrayList<>();
					for (String string : set) {
						SimpleExpression like = Restrictions.like(SingleCarparkUser.Property.plateNo.name(), string,MatchMode.ANYWHERE);
						list.add(like);
					}
					c.add(Restrictions.or(list.toArray(new Criterion[list.size()])));
				}
			}else{
				return new ArrayList<>();
			}
			if (!StrUtil.isEmpty(validTo)) {
				c.add(Restrictions.ge(SingleCarparkUser.Property.validTo.name(), validTo));
			}
			if (!StrUtil.isEmpty(carparkId)) {
				List<SingleCarparkCarpark> list = getCarparkAndAllChild(carparkId);
				c.add(Restrictions.in("carpark",list));
			}
			c.setFirstResult(start);
			c.setMaxResults(size);
			List<SingleCarparkUser> resultList = c.getResultList();
			resultList=resultList.stream().filter(new Predicate<SingleCarparkUser>() {
				@Override
				public boolean test(SingleCarparkUser user) {
					return !user.getType().equals("储值")&&!StrUtil.isEmpty(user.getValidTo());
				}
			}).collect(Collectors.toList());
			return resultList;
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findByNameOrPlateNo(int start, int max, String name, String plateNo, String address, SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue) {
		unitOfWork.begin();
		try {
			Criteria c = createFindByNameOrPlateNoCriteria(name, plateNo, address, monthlyCharge, willOverdue, overdue);
			c.setFirstResult(start);
			c.setMaxResults(max);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	@Override
	public Long countByNameOrPlateNo(String name, String plateNo, String address, SingleCarparkMonthlyCharge monthlyCharge, int willOverdue, String overdue) {
		unitOfWork.begin();
		try {
			Criteria c = createFindByNameOrPlateNoCriteria(name, plateNo, address, monthlyCharge, willOverdue, overdue);
			c.setProjection(Projections.rowCount());
			Long l = (Long) c.getSingleResultOrNull();
			return l == null ? 0 : l;
		} finally {
			unitOfWork.end();
		}
	}
	@Override
	public List<UserHistory> findUserHistory(UpdateEnum[] updates,ProcessEnum[] processEnums) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), UserHistory.class);
			c.add(Restrictions.in("historyDetail.processState", processEnums));
			c.add(Restrictions.in("historyDetail.updateState", updates));
			c.addOrder(Order.asc("id"));
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	@Transactional
	@Override
	public Long updateUserHistory(UserHistory history, ProcessEnum process) {
		UserHistory userHistory = emprovider.get().getReference(UserHistory.class, history.getAuto_id());
		userHistory.getHistoryDetail().setProcessState(process);
		userHistory.getHistoryDetail().setProcessTime(new Date());
		return history.getAuto_id();
	}
	@Override
	public boolean downPlateToCamera(String ip, String type) {
		PlateNOJNA plateNOJNA = CameraTypeEnum.get(type).getJNA(ImageServerUI.serverInjector);
		List<PlateDownload> plateDownloadList = null;
		try {
			plateDownloadList = (List<PlateDownload>) userCache.get("findAllUser", new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					List<SingleCarparkUser> findAll = findAll();
					ArrayList<PlateDownload> list = new ArrayList<>();
					Map<String, String> map=new HashMap<>();
					for (SingleCarparkUser user : findAll) {
						String[] split = user.getPlateNo().split(",");
						if (split.length>1) {
							continue;
						}
						PlateDownload pd=new PlateDownload();
						Date validTo = user.getValidTo();
						if (validTo==null||validTo.before(new Date())) {
							pd.setUse(false);
						}
						String key = user.getPlateNo();
						if (map.get(key)==null) {
							pd.setDate(validTo);
							pd.setPlate(user.getPlateNo());
							list.add(pd);
							map.put(key, key);
						}
					}
					return list;
				}
			});
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		try {
			plateNOJNA.openEx(ip, new PlateNOResult() {
				@Override
				public void invok(String ip, int channel, String plateNO, byte[] bigImage, byte[] smallImage, float rightSize) {
				}
			});
			plateNOJNA.plateDownload(plateDownloadList, ip);
		} finally {
			plateNOJNA.closeEx(ip);
		}
		return true;
	}
}
