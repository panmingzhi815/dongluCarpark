package com.donglu.carpark.service;

import java.util.Date;
import java.util.List;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;

public interface StoreServiceI {
	/**
	 * 商铺登录
	 * @param loginName
	 * @param loginPassword
	 * @return
	 */
	public SingleCarparkStore findByLogin(String loginName,String loginPassword);
	/**
	 * 保存商铺信息
	 * @param store
	 * @return
	 */
	public Long saveStore(SingleCarparkStore store);
	/**
	 * 删除商铺
	 * @param store
	 * @return
	 */
	public Long deleteStore(SingleCarparkStore store);
	/**
	 * 保存商铺的优惠信息
	 * @param storeFree
	 * @return
	 */
	public Long saveStoreFree(SingleCarparkStoreFreeHistory storeFree);
	/**
	 * 查找商铺优惠信息
	 * @param page
	 * @param rows
	 * @param storeName
	 * @param plateNO
	 * @param used
	 * @param start
	 * @param end
	 * @return
	 */
	public List<SingleCarparkStoreFreeHistory> findByPlateNO(int page,int rows,String storeName,String plateNO,String used,Date start,Date end);
	public Long countByPlateNO(String storeName, String plateNO,String used,Date start,Date end);
	
	/**
	 * 保存商铺充值记录
	 * @param storePay
	 * @return
	 */
	public Long saveStorePay(SingleCarparkStoreChargeHistory storePay);
	
	/**
	 * 查找商铺充值记录
	 * @param page
	 * @param rows
	 * @param operaName
	 * @param storeName 
	 * @param start
	 * @param end
	 * @return
	 */
	public List<SingleCarparkStoreChargeHistory> findStoreChargeHistoryByTime(int page, int rows,  String storeName,String operaName, Date start,Date end);
	public Long countStoreChargeHistoryByTime(String storeName,String operaName,Date start, Date end);
	
	public SingleCarparkStoreFreeHistory findStoreFreeById(Long id);
	
	/**
	 * 查找商铺信息
	 * @param start
	 * @param max
	 * @param storeName
	 * @return
	 */
	public List<SingleCarparkStore> findStoreByCondition(int start, int max, String storeName);
	public Long countStoreByCondition(String storeName);
	public SingleCarparkStore findStoreById(Long id);
}
