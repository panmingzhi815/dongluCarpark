package com.donglu.carpark.service;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public interface IpmsServiceI {
	public boolean addInOutHistory(SingleCarparkInOutHistory inOutHistory);
	public boolean updateInOutHistory(SingleCarparkInOutHistory inOutHistory);
	public boolean addUser(SingleCarparkUser user);
	public boolean updateUser(SingleCarparkUser user);
}
