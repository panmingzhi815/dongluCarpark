package com.donglu.carpark.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkService;
import com.dongluhitec.card.domain.db.CardUserGroup;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.service.MapperConfig;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.dongluhitec.card.service.impl.SettingServiceImpl;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class CarparkServiceImpl implements CarparkService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SettingServiceImpl.class);

	@Inject
	private Provider<EntityManager> emprovider;

	@Inject
	private UnitOfWork unitOfWork;

	@Inject
	private MapperConfig mapper;

	@Transactional
	public Long saveCarpark(SingleCarparkCarpark carpark) {
		DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
		if (carpark.getId() == null) {
			dom.insert(carpark);
		} else {
			dom.save(carpark);
		}
		return carpark.getId();
	}

	@Transactional
	public Long deleteCarpark(SingleCarparkCarpark carpark) {
		DatabaseOperation<SingleCarparkCarpark> dom = DatabaseOperation.forClass(SingleCarparkCarpark.class, emprovider.get());
		dom.remove(carpark.getId());
		return carpark.getId();
	}

	public List<SingleCarparkCarpark> findAll() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}

	public List<SingleCarparkCarpark> findCarparkToLevel() {
		unitOfWork.begin();
		try {
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
				public boolean apply(SingleCarparkCarpark arg0) {
					return arg0.getParent() == null;
				}
			});

			List<SingleCarparkCarpark> list = new ArrayList<>(filter);
			return list;
		} finally {
			unitOfWork.end();
		}
	}

	public SingleCarparkCarpark findCarparkTopLevel() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emprovider.get(), SingleCarparkCarpark.class);
			c.add(Restrictions.isNull("parent"));
			return (SingleCarparkCarpark) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}
}
