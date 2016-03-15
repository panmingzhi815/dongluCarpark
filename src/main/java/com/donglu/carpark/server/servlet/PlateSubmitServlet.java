package com.donglu.carpark.server.servlet;

import java.util.Date;

import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.PlateSubmitServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.google.inject.Inject;

public class PlateSubmitServlet extends HessianServlet implements PlateSubmitServiceI {
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4709119994519494966L;
	private PlateSubmitServiceI plateSubmitService;
	
	@Override
	public void init() throws ServletException {
		plateSubmitService = sp.getPlateSubmitService();
	}

	@Override
	public void submitPlate(String plateNO, Date date, byte[] image,  SingleCarparkDevice device) {
		plateSubmitService.submitPlate(plateNO, date, image, device);
	}

}
