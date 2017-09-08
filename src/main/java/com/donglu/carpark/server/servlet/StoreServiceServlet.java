package com.donglu.carpark.server.servlet;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.StoreServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreChargeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.server.util.HibernateSerializerFactory;
import com.google.inject.Inject;

public class StoreServiceServlet extends HessianServlet implements StoreServiceI {
	/**
	 * 
	 */
	private static final long serialVersionUID = 543632220720089722L;
	/**
	 * 
	 */
	private Logger LOGGER = LoggerFactory.getLogger(StoreServiceServlet.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private StoreServiceI storeService;



    @Override
    public void init() throws ServletException {
        try {
        	getSerializerFactory().addFactory(new HibernateSerializerFactory());
        	sp.start();
        } catch (Exception e) {
            LOGGER.error("Cannot start service provider in the StoreServiceServlet engine", e);
            throw new ServletException("Cannot start service provider in the servlet engine");
        }
       storeService = sp.getStoreService();
    }



	@Override
	public SingleCarparkStore findByLogin(String loginName, String loginPassword) {
		return storeService.findByLogin(loginName, loginPassword);
	}



	@Override
	public Long saveStore(SingleCarparkStore store) {
		return storeService.saveStore(store);
	}



	@Override
	public Long deleteStore(SingleCarparkStore store) {
		return storeService.deleteStore(store);
	}



	@Override
	public Long saveStoreFree(SingleCarparkStoreFreeHistory storeFree) {
		return storeService.saveStoreFree(storeFree);
	}



	@Override
	public List<SingleCarparkStoreFreeHistory> findByPlateNO(int page, int rows, String storeName, String plateNO, String used, Date start, Date end) {
		return storeService.findByPlateNO(page, rows, storeName, plateNO, used, start, end);
	}



	@Override
	public Long countByPlateNO(String storeName, String plateNO, String used, Date start, Date end) {
		return storeService.countByPlateNO(storeName, plateNO, used, start, end);
	}



	@Override
	public Long saveStorePay(SingleCarparkStoreChargeHistory storePay) {
		return storeService.saveStorePay(storePay);
	}



	@Override
	public List<SingleCarparkStoreChargeHistory> findStoreChargeHistoryByTime(int page, int rows, String storeName, String operaName, Date start, Date end) {
		return storeService.findStoreChargeHistoryByTime(page, rows, storeName, operaName, start, end);
	}



	@Override
	public Long countStoreChargeHistoryByTime(String storeName, String operaName, Date start, Date end) {
		return storeService.countStoreChargeHistoryByTime(storeName, operaName, start, end);
	}



	@Override
	public SingleCarparkStoreFreeHistory findStoreFreeById(Long id) {
		return storeService.findStoreFreeById(id);
	}



	@Override
	public List<SingleCarparkStore> findStoreByCondition(int start, int max, String storeName) {
		return storeService.findStoreByCondition(start, max, storeName);
	}



	@Override
	public Long countStoreByCondition(String storeName) {
		return storeService.countStoreByCondition(storeName);
	}



	@Override
	public SingleCarparkStore findStoreById(Long id) {
		return storeService.findStoreById(id);
	}
}