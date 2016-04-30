package com.donglu.carpark.server.servlet;

import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.PositionUpdateServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.google.inject.Inject;

public class UpdatePositionServlet extends HessianServlet implements PositionUpdateServiceI {
	/**
	 * 
	 */
	private static final long serialVersionUID = -861018805868089390L;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	PositionUpdateServiceI positionUpdateService;
	@Override
	public void init() throws ServletException {
		super.init();
		positionUpdateService=sp.getPositionUpdateService();
	}
	@Override
	public void updatePosion(SingleCarparkCarpark carpark, Long userId, boolean inOrOut) {
		positionUpdateService.updatePosion(carpark, userId, inOrOut);
	}
	@Override
	public void updatePosion(SingleCarparkCarpark carpark, boolean isFixOrTemp, int slot) {
		positionUpdateService.updatePosion(carpark, isFixOrTemp, slot);
	}

}
