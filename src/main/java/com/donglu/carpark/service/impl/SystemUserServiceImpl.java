package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.SystemUserServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkPrivilegeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

@SuppressWarnings("unchecked")
public class SystemUserServiceImpl implements SystemUserServiceI {
	private static final Logger LOGGER=LoggerFactory.getLogger(SystemUserServiceImpl.class);
	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	
	@Override
	public List<SingleCarparkSystemUser> findAllSystemUser() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemUser.class);
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	public SingleCarparkSystemUser findByNameAndPassword(String userName, String password) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemUser.class);
			c.add(Restrictions.eq("userName", userName));
			if (password!=null) {
				c.add(Restrictions.eq("password", password));
			}
			Object singleResultOrNull = c.getSingleResultOrNull();
			if (singleResultOrNull!=null) {
				Cache<String, Number> cache = CarparkInOutServiceImpl.numberCache;
				cache.invalidate("findFactMoneyByName-"+userName);
				cache.invalidate("findFreeMoneyByName-"+userName);
				return (SingleCarparkSystemUser) singleResultOrNull;
			}
			return null;
		}finally{
			unitOfWork.end();
		}
	}

	@Override
	@Transactional
	public Long removeSystemUser(SingleCarparkSystemUser systemUser) {
		DatabaseOperation<SingleCarparkSystemUser> dom = DatabaseOperation.forClass(SingleCarparkSystemUser.class, emprovider.get());
		if (!systemUser.getUserName().equals("admin")) {
			dom.remove(systemUser);
		}
		return systemUser.getId();
	}

	@Override
	@Transactional
	public Long saveSystemUser(SingleCarparkSystemUser systemUser) {
		DatabaseOperation<SingleCarparkSystemUser> dom = DatabaseOperation.forClass(SingleCarparkSystemUser.class, emprovider.get());
		if (systemUser.getId() == null) {
			dom.insert(systemUser);
		} else {
			dom.save(systemUser);
		}
		return systemUser.getId();
	}

	@Override
	@Transactional
	public void initSystemInfo() {
		DatabaseOperation<SingleCarparkSystemUser> dom = DatabaseOperation.forClass(SingleCarparkSystemUser.class, emprovider.get());
		SingleCarparkSystemUser systemUser=new SingleCarparkSystemUser();
		systemUser.setUserName("admin");
		systemUser.setPassword("admin");
		systemUser.setType("超级管理员");
		systemUser.setCreateDate(new Date());
		dom.insert(systemUser);
		DatabaseOperation<CarparkCarType> dom1= DatabaseOperation.forClass(CarparkCarType.class, emprovider.get());
		CarparkCarType t=new CarparkCarType();
		t.setName("小车");
		dom1.insert(t);
		t=new CarparkCarType();
		t.setName("大车");
		dom1.insert(t);
		t=new CarparkCarType();
		t.setName("摩托车");
		dom1.insert(t);
		
		DatabaseOperation<SingleCarparkDeviceVoice> voiceDom = DatabaseOperation.forClass(SingleCarparkDeviceVoice.class, emprovider.get());
		for (DeviceVoiceTypeEnum vt : DeviceVoiceTypeEnum.values()) {
			SingleCarparkDeviceVoice dv=new SingleCarparkDeviceVoice();
			dv.setContent(vt.getContent());
			dv.setVolume(vt.getVolume());
			dv.setType(vt);
			voiceDom.insert(dv);
		}
	}

	@Override
	public void login(String userName, String password, String ip) {
		mapLoginInfo.put(userName, ip);
	}
	
	@Override
	public void login(String userName, String password, String ip, String versionInfo) {
		LOGGER.info("用户：{} 登录电脑：{} 版本：{}",userName,ip,versionInfo);
		login(userName, password, ip);
	}

	@Override
	public void loginOut(String userName) {
		mapLoginInfo.remove(userName);
	}

	@Override
	public String loginStatus(String userName) {
		return mapLoginInfo.get(userName);
	}

	@Override
	public List<String> findPrivilegeByName(String userName,String password) {
		if (userName.equals("admin")) {
			return Arrays.asList(CarparkPrivilegeEnum.values()).stream().map(t->t.getKey()).collect(Collectors.toList());
		}
//		Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkSystemUser.class);
//		c.add(Restrictions.eq(SingleCarparkSystemUser.Property.userName.name(), userName));
//		c.add(Restrictions.eq(SingleCarparkSystemUser.Property.password.name(), password));
//		List<SingleCarparkSystemUser> resultList = c.getResultList();
//		if (StrUtil.isEmpty(resultList)) {
//			return new ArrayList<>();
//		}
//		SingleCarparkSystemUser systemUser = resultList.get(0);
		
		return new ArrayList<>();
	}

}
