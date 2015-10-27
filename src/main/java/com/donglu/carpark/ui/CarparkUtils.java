package com.donglu.carpark.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.dongluhitec.card.domain.util.StrUtil;

public class CarparkUtils {
	
	public static List<String> splitPlateNO(String plateNo){
		if (StrUtil.isEmpty(plateNo)) {
			return new ArrayList<>();
		}
		List<String> list=new ArrayList<>();
		for (int i=plateNo.length();i>0;i--) {
			for (int j=0;j<plateNo.length();j++) {
				if ((j+i)>plateNo.length()) {
					break;
				}
				String substring = plateNo.substring(j, j+i);
				list.add(substring);
			}
		}
		return list;
	}
	/**
	 * 日期相减的小时分钟差
	 * 
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int MinusMinute(Date time1, Date time2) {
		long nm = 1000 * 60;// 一分钟的毫秒数
		long diff;
		// 获得两个时间的毫秒时间差异
		diff = time2.getTime() - time1.getTime();
		return Long.valueOf(diff / nm).intValue();
	}
	public static List<Date> cutDaysByDay(Date start, Date end) {
		long nd = 1000 * 60 * 60 * 24;// 一分钟的毫秒数
		long diff;
		// 获得两个时间的毫秒时间差异
		diff = end.getTime() - start.getTime();
		int day=Long.valueOf(diff/nd).intValue();
		List<Date> list=new ArrayList<>();
		list.add(start);
		Date date=start;
		if (day>0) {
			for (int i = 0; i < day; i++) {
				DateTime d = new DateTime(start);
				DateTime dateTime = new DateTime(d.getYear(),d.getMonthOfYear(),d.getDayOfMonth(),23,59,59).plusDays(i);
				date=dateTime.toDate();
				list.add(date);
			}
		}
		list.add(end);
		return list;
	}
	public static List<Date> cutDaysByHours(Date start, Date end) {
		long nd = 1000 * 60 * 60 ;// 一分钟的毫秒数
		long diff;
		// 获得两个时间的毫秒时间差异
		diff = end.getTime() - start.getTime();
		int hours=Long.valueOf(diff/nd).intValue()/24;
		List<Date> list=new ArrayList<>();
		list.add(start);
		Date date=start;
		if (hours>0) {
			for (int i = 0; i < hours; i++) {
				DateTime d = new DateTime(date).plusDays(1);
				date=d.toDate();
				list.add(date);
			}
		}
		list.add(end);
		return list;
	}
	
	public static void main(String[] args) {
		System.out.println(splitPlateNO("粤BD021W"));
	}
}
