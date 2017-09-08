package com.donglu.carpark.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CountTempCarChargeI;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.exception.DongluAppException;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.collect.Maps;
public class CountTempCarChargeImpl implements CountTempCarChargeI {
	private Logger LOGGER = LoggerFactory.getLogger(CountTempCarChargeImpl.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 6806052301340681246L;
	Map<String, Date> mapHoliday=Maps.newHashMap();

	@Override
	public float charge(Long carparkId, Long carType, Date startTime, Date endTime, CarparkDatabaseServiceProvider sp, CarparkMainModel model,boolean reCharge) {
		
		float totalCharge = 0;
		Float money = 0F;// 免费金额
		Float hour = 0F;// 免费时间
		try {
			// 查找优惠信息
			List<SingleCarparkStoreFreeHistory> findByPlateNO = sp.getStoreService().findByPlateNO(0, Integer.MAX_VALUE, null, model.getPlateNo(), "未使用", startTime, endTime);
			if (!StrUtil.isEmpty(findByPlateNO)) {
				for (SingleCarparkStoreFreeHistory free : findByPlateNO) {
					if (free.getIsAllFree()!=null&&free.getIsAllFree()) {
						model.setStroeFrees(findByPlateNO);
						return 0;
					}
					if (!StrUtil.isEmpty(free.getFreeHour())) {
						hour += free.getFreeHour();
					}
					if (!StrUtil.isEmpty(free.getFreeMoney())) {
						money += free.getFreeMoney();
					}
				}
			}
//			LOGGER.info("车牌{}在时间{}-{}内优惠金额{}元，优惠时间{}小时", model.getPlateNo(), startTime, endTime, money, hour);
			model.setStroeFrees(findByPlateNO);
			// 变更收费时间
			startTime = new DateTime(startTime).plusHours(hour.intValue()).plusMinutes(Float.valueOf((hour % 1F)).intValue()).toDate();
			//获取今天的最大收费
			float findOneDayMaxCharge = sp.getCarparkInOutService().findOneDayMaxCharge(carType, carparkId);
			//计算收费
			float calculateTempCharge = sp.getCarparkService().calculateTempCharge(carparkId, carType, startTime, endTime);
//			LOGGER.info("今天最大收费{}元，今天缴费了{}元，计算收费{}元",findOneDayMaxCharge,countTodayCharge,calculateTempCharge);
			if (!reCharge) {
				//今天收费
				float countTodayCharge = sp.getCarparkInOutService().countTodayCharge(model.getPlateNo(), StrUtil.getTodayTopTime(new Date()), StrUtil.getTodayBottomTime(new Date()));
				
				if (findOneDayMaxCharge>0&&countTodayCharge>findOneDayMaxCharge) {
//				LOGGER.info("今天最大收费{}元，今天缴费了{}元",findOneDayMaxCharge,countTodayCharge);
					return 0;
				}
				if(countTodayCharge>0&&findOneDayMaxCharge>0&&(calculateTempCharge+countTodayCharge)>(findOneDayMaxCharge)){
					totalCharge+=(findOneDayMaxCharge-countTodayCharge);
				}else {
					totalCharge+=calculateTempCharge;
				}
			}else{
				return calculateTempCharge;
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return (totalCharge - money < 0 ? 0 : totalCharge - money);
	}

}
