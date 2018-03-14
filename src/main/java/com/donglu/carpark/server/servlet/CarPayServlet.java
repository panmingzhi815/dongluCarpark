package com.donglu.carpark.server.servlet;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarPayServiceI;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.google.inject.Inject;

public class CarPayServlet extends HessianServlet implements CarPayServiceI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7728929910083868460L;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private CarPayServiceI carPayService;
	@Override
	public void init() throws ServletException {
		carPayService = sp.getCarPayService();
		getCarPayHistoryWithNew();
	}

	@Override
	public Long saveCarPayHistory(CarPayHistory cp) {
		return carPayService.saveCarPayHistory(cp);
	}

	@Override
	public List<CarPayHistory> findCarPayHistoryByLike(int i, int maxValue, String plateNo, Date start, Date end) {
		return carPayService.findCarPayHistoryByLike(i, maxValue, plateNo, start, end);
	}

	@Override
	public int countCarPayHistoryByLike(String plateNo, Date start, Date end) {
		return carPayService.countCarPayHistoryByLike(plateNo, start, end);
	}

	@Override
	public Long deleteCarPayHistory(Long id) {
		return carPayService.deleteCarPayHistory(id);
	}

	@Override
	public CarPayHistory findCarPayHistoryByPayId(String payId) {
		return carPayService.findCarPayHistoryByPayId(payId);
	}

	@Override
	public List<CarPayHistory> getCarPayHistoryWithNew() {
		return carPayService.getCarPayHistoryWithNew();
	}

}
