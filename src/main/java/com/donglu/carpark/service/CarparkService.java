package com.donglu.carpark.service;

import java.util.List;


import com.donglu.carpark.service.impl.CarparkServiceImpl;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.google.inject.ImplementedBy;

public interface CarparkService {
	public Long saveCarpark(SingleCarparkCarpark carpark);
	public Long deleteCarpark(SingleCarparkCarpark carpark);
	public List<SingleCarparkCarpark> findAll();
	List<SingleCarparkCarpark> findCarparkToLevel();
	SingleCarparkCarpark findCarparkTopLevel();
}
