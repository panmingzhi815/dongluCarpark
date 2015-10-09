package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;

public interface CarparkInOutServiceI {
	
	Long saveInOutHistory(SingleCarparkInOutHistory inout);
	
	List<SingleCarparkInOutHistory> findByNoOut(String plateNo);

	float findTotalCharge(String userName);
	
	
	List<SingleCarparkInOutHistory> findByCondition(int maxResult,int size,String plateNo,String userName,String carType,String inout,Date in,Date out,String operaName);
	Long countByCondition(String plateNo,String userName,String carType,String inout,Date in,Date out,String operaName);
}
