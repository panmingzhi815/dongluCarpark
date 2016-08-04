package com.donglu.carpark.server.servlet;

import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.IpmsServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.google.inject.Inject;

public class IpmsServlet extends HessianServlet implements IpmsServiceI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5489797873783489607L;
	@Inject
	private IpmsServiceI ipmsService;
	@Override
	public void init() throws ServletException {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean addInOutHistory(SingleCarparkInOutHistory inOutHistory) {
		return ipmsService.addInOutHistory(inOutHistory);
	}

	@Override
	public boolean updateInOutHistory(SingleCarparkInOutHistory inOutHistory) {
		return ipmsService.updateInOutHistory(inOutHistory);
	}

	@Override
	public boolean addUser(SingleCarparkUser user) {
		return ipmsService.addUser(user);
	}

	@Override
	public boolean updateUser(SingleCarparkUser user) {
		return ipmsService.updateUser(user);
	}

	@Override
	public void updateTempCarChargeHistory() {
		ipmsService.updateTempCarChargeHistory();
	}

	@Override
	public void updateUserInfo() {
		ipmsService.updateUserInfo();
	}

	@Override
	public void updateFixCarChargeHistory() {
		ipmsService.updateFixCarChargeHistory();
	}

	@Override
	public int pay(SingleCarparkInOutHistory inout, float chargeMoney) {
		return ipmsService.pay(inout, chargeMoney);
	}

}
