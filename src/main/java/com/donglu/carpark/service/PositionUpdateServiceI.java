package com.donglu.carpark.service;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;


public interface PositionUpdateServiceI {
	void updatePosion(SingleCarparkCarpark carpark,Long userId, boolean inOrOut);
	void updatePosion(SingleCarparkCarpark carpark, boolean isFixOrTemp, int slot);
}
