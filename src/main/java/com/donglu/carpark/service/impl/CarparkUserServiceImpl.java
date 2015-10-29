package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.MatchMode;
import org.criteria4jpa.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkUserService;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.MapperConfig;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.dongluhitec.card.service.impl.SettingServiceImpl;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class CarparkUserServiceImpl implements CarparkUserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingServiceImpl.class);

	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	@Inject
	private MapperConfig mapper;
	
	@Transactional
	public Long saveUser(SingleCarparkUser user) {
		DatabaseOperation<SingleCarparkUser> dom = DatabaseOperation.forClass(SingleCarparkUser.class, emprovider.get());
		if (user.getId() == null) {
			dom.insert(user);
		} else {
			dom.save(user);
		}
		return user.getId();
	}
	@Transactional
	public Long deleteUser(SingleCarparkUser user) {
		DatabaseOperation<SingleCarparkUser> dom = DatabaseOperation.forClass(SingleCarparkUser.class, emprovider.get());
		dom.remove(user.getId());
		return user.getId();
	}
	
	public List<SingleCarparkUser> findAll() {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}

	public List<SingleCarparkUser> findByNameOrPlateNo(String name, String plateNo) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			if (!StrUtil.isEmpty(name)) {
				c.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
			}
			if (!StrUtil.isEmpty(plateNo)) {
				c.add(Restrictions.like("plateNo", plateNo, MatchMode.ANYWHERE));
			}
			return c.getResultList();
		}finally{
			unitOfWork.end();
		}
	}
	@Override
	public List<SingleCarparkUser> findUserByPlateNo(String plateNO) {
		unitOfWork.begin();
		try {
			Criteria c=CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkUser.class);
			
			if (!StrUtil.isEmpty(plateNO)) {
				c.add(Restrictions.like("plateNo", plateNO));
			}else{
				return new ArrayList<>();
			}
			return c.getResultList();
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

}
