package com.donglu.carpark.service.impl;

import com.donglu.carpark.model.Result;
import com.donglu.carpark.service.IpmsServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public class IpmsServiceLvdiImpl implements IpmsServiceI {

	@Override
	public boolean addInOutHistory(SingleCarparkInOutHistory inOutHistory) {
		
		return false;
	}

	@Override
	public boolean updateInOutHistory(SingleCarparkInOutHistory inOutHistory) {
		
		return false;
	}

	@Override
	public boolean addUser(SingleCarparkUser user) {
		
		return false;
	}

	@Override
	public boolean updateUser(SingleCarparkUser user) {
		
		return false;
	}

	@Override
	public boolean deleteUser(SingleCarparkUser user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateTempCarChargeHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateUserInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFixCarChargeHistory() {
		// TODO Auto-generated method stub

	}

	@Override
	public int pay(SingleCarparkInOutHistory inout, float chargeMoney) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Result getPayResult(SingleCarparkInOutHistory inout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateParkSpace() {
		// TODO Auto-generated method stub

	}

	@Override
	public void synchroImage(int maxSize) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean pustFee(String parkingRecordId, double fee) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean notifyDeviceCarIn(String deviceId, String plate) {
		// TODO Auto-generated method stub
		return false;
	}

}
