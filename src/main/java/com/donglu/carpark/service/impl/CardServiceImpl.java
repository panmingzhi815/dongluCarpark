package com.donglu.carpark.service.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.criteria4jpa.Criteria;
import org.criteria4jpa.CriteriaUtils;
import org.criteria4jpa.criterion.Restrictions;
import org.criteria4jpa.order.Order;

import com.donglu.carpark.service.CardService;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.service.impl.DatabaseOperation;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.google.inject.persist.UnitOfWork;

public class CardServiceImpl implements CardService {
	private Provider<EntityManager> emProvider;
	private UnitOfWork unitOfWork;
	@Inject
	public CardServiceImpl(Provider<EntityManager> emProvider, UnitOfWork unitOfWork) {
		this.emProvider = emProvider;
		this.unitOfWork = unitOfWork;
	}
	@Transactional
	@Override
	public Long saveCard(List<SingleCarparkCard> list) {
		DatabaseOperation<SingleCarparkCard> dom = DatabaseOperation.forClass(SingleCarparkCard.class, emProvider.get());
		for (SingleCarparkCard singleCarparkCard : list) {
			if(singleCarparkCard.getId()==null){
				dom.insert(singleCarparkCard);
			}else{
				dom.save(singleCarparkCard);
			}
		}
		return list.size()*1l;
	}
	@Transactional
	@Override
	public Long deleteCard(List<SingleCarparkCard> list) {
		DatabaseOperation<SingleCarparkCard> dom = DatabaseOperation.forClass(SingleCarparkCard.class, emProvider.get());
		for (SingleCarparkCard singleCarparkCard : list) {
			dom.remove(singleCarparkCard);
		}
		return list.size()*1l;
	}

	@Override
	public List<SingleCarparkCard> findCard(int start, int size, String identifier, String serialNumber) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emProvider.get(), SingleCarparkCard.class);
			if (!StrUtil.isEmpty(identifier)) {
				c.add(Restrictions.like(SingleCarparkCard.Property.identifier.name(), identifier));
			}
			if (!StrUtil.isEmpty(serialNumber)) {
				c.add(Restrictions.like(SingleCarparkCard.Property.serialNumber.name(), serialNumber));
			}
			c.setFirstResult(start);
			c.setMaxResults(size);
			return c.getResultList();
		} finally {
			unitOfWork.end();
		}
	}
	@Override
	public SingleCarparkCard findCard(String identifier, String serialNumber) {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emProvider.get(), SingleCarparkCard.class);
			if (!StrUtil.isEmpty(identifier)) {
				c.add(Restrictions.like(SingleCarparkCard.Property.identifier.name(), identifier));
			}
			if (!StrUtil.isEmpty(serialNumber)) {
				c.add(Restrictions.like(SingleCarparkCard.Property.serialNumber.name(), serialNumber));
			}
			c.setFirstResult(0);
			c.setMaxResults(1);
			return (SingleCarparkCard) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}
	@Override
	public SingleCarparkCard findLastCard() {
		unitOfWork.begin();
		try {
			Criteria c = CriteriaUtils.createCriteria(emProvider.get(), SingleCarparkCard.class);
			c.addOrder(Order.desc("id"));
			c.setFirstResult(0);
			c.setMaxResults(1);
			return (SingleCarparkCard) c.getSingleResultOrNull();
		} finally {
			unitOfWork.end();
		}
	}

}
