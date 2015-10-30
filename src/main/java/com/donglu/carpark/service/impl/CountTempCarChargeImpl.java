package com.donglu.carpark.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CountTempCarChargeI;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkAcrossDayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationPrice;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkHolidayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.collect.Maps;

public class CountTempCarChargeImpl implements CountTempCarChargeI {

	private static final String YYYY_MM_DD = "yyyyMMdd";
	/**
	 * 
	 */
	private static final long serialVersionUID = 6806052301340681246L;
	Map<String, Date> mapHoliday=Maps.newHashMap();

	@Override
	public float charge(Long carType, Date inTime, Date outTime, CarparkDatabaseServiceProvider sp) {
		float money = 0;
		DateTime start = new DateTime(inTime);
		DateTime end = new DateTime(outTime);
		Map<CarparkHolidayTypeEnum, CarparkChargeStandard> map = Maps.newHashMap();
		List<CarparkChargeStandard> list = sp.getCarparkService().findCarparkTempCharge(carType);
		if (StrUtil.isEmpty(list)) {
			return 0;
		}
		for (CarparkChargeStandard carparkChargeStandard : list) {
			System.out.println(carparkChargeStandard.getCarparkHolidayTypeEnum());
			map.put(carparkChargeStandard.getCarparkHolidayTypeEnum(), carparkChargeStandard);
		}
		List<Holiday> findHolidayByYear = sp.getCarparkService().findHolidayByYear(start.getYear());
		for (Holiday singleCarparkHoliday : findHolidayByYear) {
			Date start2 = singleCarparkHoliday.getStart();
			if (StrUtil.isEmpty(start2)) {
				continue;
			}
			mapHoliday.put(StrUtil.formatDate(start2, YYYY_MM_DD), start2);
		}
		CarparkChargeStandard carparkChargeStandard = getCarparkChargeStandard(start, map);
		
		if (carparkChargeStandard.getCarparkDurationTypeEnum().equals(CarparkDurationTypeEnum.进场时长)) {
			List<Date> listDate = CarparkUtils.cutDaysByHours(start.toDate(), end.toDate());
			if (carparkChargeStandard.getCarparkAcrossDayTypeEnum().equals(CarparkAcrossDayTypeEnum.重复计费)) {
				for (int i = 1; i < listDate.size(); i++) {
					float countTempCarShouldMoney = countTempCarShouldMoney(2L, listDate.get(i-1), listDate.get(i), 0, map);
					money += countTempCarShouldMoney;
					System.out.println("进场时长-重复计费==="+money+"===="+countTempCarShouldMoney);
				}
			}else{
				
			}
		}else{
			List<Date> listDate = CarparkUtils.cutDaysByDay(start.toDate(), end.toDate());
			if (carparkChargeStandard.getCarparkAcrossDayTypeEnum().equals(CarparkAcrossDayTypeEnum.重复计费)) {
				for (int i = 1; i < listDate.size(); i++) {
					int m=0;//第二个时间是 xx:59:59所以要加一秒开始
					if (i-1>0) {
						m=1;
					}
					Date startTime = new DateTime(listDate.get(i-1)).plusSeconds(m).toDate();
					Date endTime = listDate.get(i);
					float countTempCarShouldMoney = countTempCarShouldMoney(2L, startTime, endTime, 0, map);
					money += countTempCarShouldMoney;
					System.out.println("自然天--重复计费=="+money+"+++++++++++"+countTempCarShouldMoney);
				}
			}else{
				money += countTempCarShouldMoney(2L, new DateTime(listDate.get(0)).toDate(), listDate.get(listDate.size()-1), 0, map);
			}
		}
		
		return money;
	}

	/**
	 * @param start
	 * @param map
	 * @return
	 */
	private CarparkChargeStandard getCarparkChargeStandard(DateTime start, Map<CarparkHolidayTypeEnum, CarparkChargeStandard> map) {
		CarparkChargeStandard carparkChargeStandard;
		if (mapHoliday.get(start.toString(YYYY_MM_DD))==null) {
			carparkChargeStandard = map.get(CarparkHolidayTypeEnum.工作日);
			if (StrUtil.isEmpty(carparkChargeStandard)) {
				carparkChargeStandard = map.get(CarparkHolidayTypeEnum.非工作日);
			}
		}else{
			carparkChargeStandard = map.get(CarparkHolidayTypeEnum.非工作日);
			if (StrUtil.isEmpty(carparkChargeStandard)) {
				carparkChargeStandard = map.get(CarparkHolidayTypeEnum.工作日);
			}
		}
		return carparkChargeStandard;
	}
	
	public float countTempCarShouldMoney(Long carType, Date inTime, Date outTime, int crossDays, Map<CarparkHolidayTypeEnum, CarparkChargeStandard> map) {
		float money = 0;
		try {

			DateTime start = new DateTime(inTime);
			DateTime end = new DateTime(outTime);
			System.out.println(start+"======="+end);
			DateTime oldStart = new DateTime(inTime);
			int minusMinute = StrUtil.MinusMinute(start.toDate(), end.toDate());
			int hours = minusMinute / 60;
			int minus = minusMinute % 60;
			int oldDay = start.getDayOfYear();

			// List<SingleCarparkHoliday> findHolidayByYear = sp.getCarparkService().findHolidayByYear(2015);
			// System.out.println(findHolidayByYear);
			CarparkChargeStandard ccs = map.get(CarparkHolidayTypeEnum.工作日);
			if (StrUtil.isEmpty(ccs)) {
				return 0;
			}
			// 如果在免费时长之内，则直接返回0
			if (minusMinute <= ccs.getFreeTime()) {
				System.out.println(0);
				return 0;
			}
			// 如果在起步时长之内,则直接返回起步金额，如果不在，则累加起步金额，将时间向后推起步时间
			if (ccs.getStartStepTime() >= 0) {
				if (minusMinute < ccs.getStartStepTime()) {
					return ccs.getStartStepPrice();
				}
			}
			boolean flag = true;
			int crossDay = crossDays;
			while (flag) {
				ccs =getCarparkChargeStandard(start, map);
				Map<CarparkDurationStandard, Map<Integer, Float>> map1 = Maps.newHashMap();
				List<CarparkDurationStandard> carparkDurationStandards = ccs.getCarparkDurationStandards();

				int t = 0;
				System.out.println(money);
				for (CarparkDurationStandard cds : carparkDurationStandards) {
					if (start.getDayOfYear() != oldStart.getDayOfYear()) {
						crossDay++;
						// money=money>ccs.getOnedayMaxCharge()?ccs.getOnedayMaxCharge():money;
					}
					Map<Integer, Float> map2 = Maps.newHashMap();
					List<CarparkDurationPrice> carparkDurationPriceList = cds.getCarparkDurationPriceList();
					for (CarparkDurationPrice carparkDurationPrice : carparkDurationPriceList) {
						map2.put(carparkDurationPrice.getDurationLength(), carparkDurationPrice.getDurationLengthPrice());
					}
					map1.put(cds, map2);

					Date startTime = cds.getStartTime();
					Date endTime = cds.getEndTime();
					boolean after = startTime.after(endTime);

					if (after) {
						Calendar cs = Calendar.getInstance();
						Calendar ce = Calendar.getInstance();
						cs.setTime(startTime);
						cs.set(start.getYear(), start.getMonthOfYear() - 1, start.getDayOfMonth());
						ce.setTime(endTime);
						ce.set(start.getYear(), start.getMonthOfYear() - 1, start.getDayOfMonth() + 1, 00, 00, 00);
						Date time = ce.getTime();
						if (start.plusSeconds(1).toDate().after(cs.getTime()) && start.toDate().before(time)) {
							if (end.toDate().before(time)) {
								t++;
								if (crossDay == 0) {
									money += map2.get(hours);
									money += ((minus / cds.getUnitDuration()) + (minus % cds.getUnitDuration() > 0 ? 1 : 0)) * cds.getUnitPrice();
								} else {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time);
									money += ((minusMinute2 / cds.getCrossDayUnitDuration()) + (minusMinute2 % cds.getCrossDayUnitDuration() > 0 ? 1 : 0)) * cds.getCrossDayPrice();
								}
								return getMoney(money, ccs);
							} else {
								t++;
								if (crossDay == 0) {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time);
									money += map2.get(minusMinute2 / 60)==null?0:map2.get(minusMinute2 / 60);
									minus = minusMinute2 % 60;
									money += ((minus / cds.getUnitDuration()) + (minus % cds.getUnitDuration() > 0 ? 1 : 0)) * cds.getUnitPrice();
								} else {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time);
									money += ((minusMinute2 / cds.getCrossDayUnitDuration()) + (minusMinute2 % cds.getCrossDayUnitDuration() > 0 ? 1 : 0)) * cds.getCrossDayPrice();
								}
								start = new DateTime(time);
								minusMinute = StrUtil.MinusMinute(start.toDate(), end.toDate());
								hours = minusMinute / 60;
								minus = minusMinute % 60;
								continue;
							}

						}
						cs.setTime(startTime);
						cs.set(start.getYear(), start.getMonthOfYear() - 1, start.getDayOfMonth(), 00, 00, 00);
						ce.setTime(endTime);
						ce.set(start.getYear(), start.getMonthOfYear() - 1, start.getDayOfMonth());
						time = ce.getTime();
						if (start.plusSeconds(1).toDate().after(cs.getTime()) && start.toDate().before(time)) {
							if (end.toDate().before(time)) {
								t++;
								if (crossDay == 0) {
									money += map2.get(hours);
									money += ((minus / cds.getUnitDuration()) + (minus % cds.getUnitDuration() > 0 ? 1 : 0)) * cds.getUnitPrice();
								} else {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time);
									money += ((minusMinute2 / cds.getCrossDayUnitDuration()) + (minusMinute2 % cds.getCrossDayUnitDuration() > 0 ? 1 : 0)) * cds.getCrossDayPrice();
								}
								return getMoney(money, ccs);
							} else {
								t++;
								if (crossDay == 0) {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time);
									money += map2.get(minusMinute2 / 60);
									minus = minusMinute2 % 60;
									money += ((minus / cds.getUnitDuration()) + (minus % cds.getUnitDuration() > 0 ? 1 : 0)) * cds.getUnitPrice();
								} else {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time);
									int i = (minusMinute2 / cds.getCrossDayUnitDuration()) + (minusMinute2 % cds.getCrossDayUnitDuration() > 0 ? 1 : 0);
									money += i * cds.getCrossDayPrice();
								}
								start = new DateTime(time);
								minusMinute = StrUtil.MinusMinute(start.toDate(), end.toDate());
								hours = minusMinute / 60;
								minus = minusMinute % 60;

								continue;
							}
						}

					} else {
						Calendar cs = Calendar.getInstance();
						cs.setTime(startTime);
						cs.set(start.getYear(), start.getMonthOfYear() - 1, start.getDayOfMonth());
						Calendar ce = Calendar.getInstance();
						ce.setTime(endTime);
						ce.set(start.getYear(), start.getMonthOfYear() - 1, start.getDayOfMonth());
						Date time = cs.getTime();
						Date time2 = ce.getTime();
						if (start.plusSeconds(1).toDate().after(time) && start.toDate().before(time2)) {
							if (end.toDate().before(ce.getTime())) {
								t++;
								if (crossDay == 0) {
									money += map2.get(hours);
									money += ((minus / cds.getUnitDuration()) + (minus % cds.getUnitDuration() > 0 ? 1 : 0)) * cds.getUnitPrice();
								} else {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time2);
									money += ((minusMinute2 / cds.getCrossDayUnitDuration()) + (minusMinute2 % cds.getCrossDayUnitDuration() > 0 ? 1 : 0)) * cds.getCrossDayPrice();
								}
								return getMoney(money, ccs);
							} else {
								t++;
								if (crossDay == 0) {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), ce.getTime());
									money += map2.get(minusMinute2 / 60);
									minus = minusMinute2 % 60;
									money += ((minus / cds.getUnitDuration()) + (minus % cds.getUnitDuration() > 0 ? 1 : 0)) * cds.getUnitPrice();
								} else {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time2);
									money += ((minusMinute2 / cds.getCrossDayUnitDuration()) + (minusMinute2 % cds.getCrossDayUnitDuration() > 0 ? 1 : 0)) * cds.getCrossDayPrice();
								}
								start = new DateTime(ce.getTime());
								minusMinute = StrUtil.MinusMinute(start.toDate(), end.toDate());
								hours = minusMinute / 60;
								minus = minusMinute % 60;

								continue;
							}
						} 
						if (cs.get(Calendar.HOUR)==0&&ce.get(Calendar.HOUR)==0&&cs.get(Calendar.SECOND)==0&&ce.get(Calendar.SECOND)==0&&
								cs.get(Calendar.MINUTE)==0&&ce.get(Calendar.MINUTE)==0) {
							ce.add(Calendar.DATE, 1);
							if (end.toDate().before(ce.getTime())) {
								t++;
								if (crossDay == 0) {
									money += map2.get(hours);
									money += ((minus / cds.getUnitDuration()) + (minus % cds.getUnitDuration() > 0 ? 1 : 0)) * cds.getUnitPrice();
								} else {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time2);
									money += ((minusMinute2 / cds.getCrossDayUnitDuration()) + (minusMinute2 % cds.getCrossDayUnitDuration() > 0 ? 1 : 0)) * cds.getCrossDayPrice();
								}
								return getMoney(money, ccs);
							} else {
								t++;
								if (crossDay == 0) {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), ce.getTime());
									money += map2.get(minusMinute2 / 60);
									minus = minusMinute2 % 60;
									money += ((minus / cds.getUnitDuration()) + (minus % cds.getUnitDuration() > 0 ? 1 : 0)) * cds.getUnitPrice();
								} else {
									int minusMinute2 = StrUtil.MinusMinute(start.toDate(), time2);
									money += ((minusMinute2 / cds.getCrossDayUnitDuration()) + (minusMinute2 % cds.getCrossDayUnitDuration() > 0 ? 1 : 0)) * cds.getCrossDayPrice();
								}
								start = new DateTime(ce.getTime());
								minusMinute = StrUtil.MinusMinute(start.toDate(), end.toDate());
								hours = minusMinute / 60;
								minus = minusMinute % 60;

								continue;
							}
						}
					}

				}
				if (t == 0 && crossDay > 0) {
					flag = false;
				}
				// money=money>ccs.getOnedayMaxCharge()?ccs.getOnedayMaxCharge():money;
				// if (Integer.valueOf(start.toString("MM"))!=Integer.valueOf(oldStart.toString("MM"))) {
				// crossDay++;
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return money;
	}

	/**
	 * @param money
	 * @param ccs
	 * @return
	 */
	private float getMoney(float money, CarparkChargeStandard ccs) {
		if (ccs.getOnedayMaxCharge()==0) {
			return money;
		}
		return money > ccs.getOnedayMaxCharge() ? ccs.getOnedayMaxCharge() : money;
	}
}
