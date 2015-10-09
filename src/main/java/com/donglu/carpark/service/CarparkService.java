package com.donglu.carpark.service;

import java.util.List;
import java.util.Map;

import com.donglu.carpark.service.impl.CarparkServiceImpl;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.google.inject.ImplementedBy;

public interface CarparkService {
	public Long saveCarpark(SingleCarparkCarpark carpark);
	public Long deleteCarpark(SingleCarparkCarpark carpark);
	public List<SingleCarparkCarpark> findAllCarpark();
	List<SingleCarparkCarpark> findCarparkToLevel();
	SingleCarparkCarpark findCarparkTopLevel();
	
	Long saveMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge);
	Long deleteMonthlyCharge(SingleCarparkMonthlyCharge monthlyCharge);
	public Long deleteMonthlyCharge(Long id);
	List<SingleCarparkMonthlyCharge> findAllMonthlyCharge();
	
	public List<SingleCarparkMonthlyCharge> findMonthlyChargeByCarpark(SingleCarparkCarpark carpark);
	
	/**
	 * 保存设备
	 * @param device
	 * @return
	 */
	public Long saveCarparkDevice(SingleCarparkDevice device);
	/**
	 * 删除设备
	 * @param device
	 * @return
	 */
	public Long deleteDevice(SingleCarparkDevice device);
	/**
	 * 查询所有设备
	 * @return
	 */
	public List<SingleCarparkDevice> findAll();
	
	public Long saveMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h);
	public Long deleteMonthlyUserPayHistory(SingleCarparkMonthlyUserPayHistory h);
	
	public List<SingleCarparkMonthlyUserPayHistory> findByPropety(Map<String, Object> map);
	
}
