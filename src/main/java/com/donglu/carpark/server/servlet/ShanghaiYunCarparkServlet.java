package com.donglu.carpark.server.servlet;

import java.util.List;

import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.blservice.ShangHaiYunCarParkService;
import com.dongluhitec.card.domain.db.shanghaiyunpingtai.HistoryUseStatus;
import com.dongluhitec.card.domain.db.shanghaiyunpingtai.YunCarparkCarInOut;
import com.dongluhitec.card.domain.db.shanghaiyunpingtai.YunCarparkDevice;
import com.dongluhitec.card.domain.db.shanghaiyunpingtai.YunCarparkPayHistory;
import com.dongluhitec.card.domain.db.shanghaiyunpingtai.YunCarparkSlot;
import com.google.inject.Inject;

public class ShanghaiYunCarparkServlet extends HessianServlet implements ShangHaiYunCarParkService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8563286231910748168L;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private ShangHaiYunCarParkService yunCarparkService;
	
	@Override
	public void init() throws ServletException {
		yunCarparkService = sp.getYunCarparkService();
	}
	
	@Override
	public Long saveCarparkSlot(YunCarparkSlot slot) {
		return yunCarparkService.saveCarparkSlot(slot);
	}

	@Override
	public Long saveCarparkCarInOut(YunCarparkCarInOut inout) {
		return yunCarparkService.saveCarparkCarInOut(inout);
	}

	@Override
	public Long saveCarparkPayHistory(YunCarparkPayHistory pay) {
		return yunCarparkService.saveCarparkPayHistory(pay);
	}

	@Override
	public YunCarparkSlot findCarparkSlot() {
		return yunCarparkService.findCarparkSlot();
	}

	@Override
	public List<YunCarparkCarInOut> findInWaitUploadHistory(int size) {
		return yunCarparkService.findInWaitUploadHistory(size);
	}

	@Override
	public List<YunCarparkCarInOut> findOutWaitUploadHistory(int size) {
		return yunCarparkService.findOutWaitUploadHistory(size);
	}

	@Override
	public List<YunCarparkCarInOut> findInOutWaitUploadFullHistory(int size) {
		return yunCarparkService.findInOutWaitUploadFullHistory(size);
	}

	@Override
	public List<YunCarparkPayHistory> findWaitUploadPayHistory(int size) {
		return yunCarparkService.findWaitUploadPayHistory(size);
	}

	@Override
	public Long updateYunCarparkCarInStatus(Long id, HistoryUseStatus status) {
		return yunCarparkService.updateYunCarparkCarInStatus(id, status);
	}

	@Override
	public Long updateYunCarparkCarOutStatus(Long id, HistoryUseStatus status) {
		return yunCarparkService.updateYunCarparkCarOutStatus(id, status);
	}

	@Override
	public Long updateYunCarparkCarInOutStatus(Long id, HistoryUseStatus status) {
		return yunCarparkService.updateYunCarparkCarInOutStatus(id, status);
	}

	@Override
	public Long updateYunCarparkPayHistory(Long id, HistoryUseStatus status) {
		return yunCarparkService.updateYunCarparkPayHistory(id, status);
	}

	@Override
	public YunCarparkCarInOut findInCarHistoryByPlate(String plate) {
		return yunCarparkService.findInCarHistoryByPlate(plate);
	}

	@Override
	public void saveYunCarParkDevice(YunCarparkDevice yunCarparkDevice) {
		yunCarparkService.saveYunCarParkDevice(yunCarparkDevice);
	}
}
