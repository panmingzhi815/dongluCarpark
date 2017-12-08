package com.donglu.carpark.service;

import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;

public interface CardService {
	Long saveCard(List<SingleCarparkCard> list);
	Long deleteCard(List<SingleCarparkCard> list);
	List<SingleCarparkCard> findCard(int start,int size,String identifier,String serialNumber);
	SingleCarparkCard findCard(String identifier,String serialNumber);
	SingleCarparkCard findLastCard();
}
