package com.donglu.carpark.service.impl;

import com.donglu.carpark.service.IpmsServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

public class IpmsServiceImpl implements IpmsServiceI {

	@Override
	public boolean addInOutHistory(SingleCarparkInOutHistory inOutHistory) {
		String url="http://120.24.168.192:81/ipms/api/syncParkingRecord.action";
		String content="?data=[{\"operation\":\"add\",\"origin\":\"东陆高新\","
				+ "\"parkingRecord\":{\"carNum\":\"{}\",\"carType\":\"{}\",\"id\":\"{}\",\"inTimeStr\":\"2016-10-10 10:10:10\",\"outTimeStr\":\"\",\"parkId\":\"adddd\",\"parkName\":\" 测试停车场\",\"reduceTime\":\"10\",\"status\":\"1\",\"userType\":\"1\"},\"syncId\":\"3\"}]";
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

}
