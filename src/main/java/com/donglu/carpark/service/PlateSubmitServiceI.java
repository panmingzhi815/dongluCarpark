package com.donglu.carpark.service;

import java.util.Date;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;

public interface PlateSubmitServiceI {
	void submitPlate(String plateNO,Date date,byte[] image,SingleCarparkDevice device);
}
