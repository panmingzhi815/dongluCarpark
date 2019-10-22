package com.donglu.carpark.service.background.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.background.AbstractCarparkBackgroundService;
import com.donglu.carpark.service.background.OnlineOrderCheckServiceI;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CheckOnlineOrder;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory.PayTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

public class OnlineOrderCheckServiceImpl extends AbstractCarparkBackgroundService implements OnlineOrderCheckServiceI {
	private final Logger LOGGER=LoggerFactory.getLogger(getClass());
	private CarparkDatabaseServiceProvider sp;

	public OnlineOrderCheckServiceImpl(CarparkDatabaseServiceProvider sp) {
		super(Scheduler.newFixedDelaySchedule(5, 60, TimeUnit.MINUTES), "网上支付订单检查服务");
		this.sp = sp;
	}

	@Override
	protected void run() {
		try {
			List<CheckOnlineOrder> list = sp.getCarparkInOutService().findByMap(0, Integer.MAX_VALUE, CheckOnlineOrder.class, new HashMap<String, Object>());
			Date time=null;
			if (list.isEmpty()) {
				List<CarPayHistory> lc=sp.getCarPayService().findCarPayHistoryByLike(0, 1, null, null, null);
				if (lc.isEmpty()) {
					return;
				}
				time=StrUtil.getTodayTopTime(lc.get(0).getPayTime());
			}else {
				time=new DateTime(StrUtil.parseDate(list.get(0).getTime())).plusDays(1).toDate();
			}
			if (new Date().getTime()-time.getTime()<24l*60*60*1000) {
				return;
			}
			int days=StrUtil.countTime(time, new Date(), TimeUnit.DAYS);
			for (int i = 0; i < days; i++) {
				LOGGER.info("对账网上支付账单：{}",StrUtil.formatDateTime(time));
				Date start = StrUtil.getTodayTopTime(time);
				Date end = StrUtil.getTodayBottomTime(time);
				List<JSONObject> findOnlineOrder = sp.getIpmsService().findOnlineOrder(start, end);
				double online=0d;
				Map<String,JSONObject> map=new HashMap<>();
				for (JSONObject jsonObject : findOnlineOrder) {
					if (StrUtil.isEmpty(jsonObject.getString("id"))) {
						return;
					}
					double doubleValue = jsonObject.getDoubleValue("onlineCost");
					online+=doubleValue/100;
					if (doubleValue>0) {
						map.put(jsonObject.getString("id"), jsonObject);
					}
				}
				if (online<=0) {
					CheckOnlineOrder order = new CheckOnlineOrder();
					order.setTime(StrUtil.formatDate(start));
					order.setStatus(0);
					sp.getCarparkInOutService().saveEntity(order);
					return;
				}
				List<Double> payHistoryMoney = sp.getCarPayService().countCarPayHistoryMoney(null, start, end);
				double factOnline=0d;
				if (!payHistoryMoney.isEmpty()) {
					factOnline=payHistoryMoney.get(2);
				}
				if (online<=factOnline) {
					CheckOnlineOrder order = new CheckOnlineOrder();
					order.setTime(StrUtil.formatDate(start));
					order.setStatus(0);
					sp.getCarparkInOutService().saveEntity(order);
					return;
				}
				List<CarPayHistory> lc=sp.getCarPayService().findCarPayHistoryByLike(0, Integer.MAX_VALUE, null, start, end);
				for (CarPayHistory carPayHistory : lc) {
					map.remove(carPayHistory.getPayId());
				}
				for (JSONObject jData : map.values()) {
					String payId = jData.getString("id");
					float payedMoney=jData.getFloat("totalCost")/100;
					double balanceAmount=jData.getDouble("balanceAmount")/100;
					double cashCost=jData.getDouble("cashCost")/100;
					double onlineCost=jData.getDouble("onlineCost")/100;
					double couponValue=jData.getDouble("couponValue")/100;
					int couponTime=jData.getIntValue("couponTime");
					String plateNo = jData.getString("carNum");
					Date createTime = StrUtil.parseDateTime(jData.getString("payFinishTimeStr"));
					CarPayHistory pay=new CarPayHistory();
					pay.setPayedMoney(payedMoney);
					pay.setPayTime(createTime);
					pay.setCreateDate(new Date());
					pay.setPlateNO(plateNo);
					pay.setRemark("云平台支付");
					pay.setBalanceAmount(balanceAmount);
					pay.setCashCost(cashCost);
					pay.setOnlineCost(onlineCost);
					pay.setPayType(PayTypeEnum.getType(jData.getIntValue("paymentMethod")));
					pay.setPayId(payId);
					pay.setCouponTime(couponTime);
					pay.setCouponValue(couponValue);
					if (PayTypeEnum.优惠券抵扣==pay.getPayType()) {
						pay.setCouponValue(pay.getPayedMoney()*1d);
					}
					pay.setOperaName("在线缴费");
					String parkingRecordId = jData.getString("parkingRecordId");
					if (jData.getIntValue("paymentType")!=3) {
						try {
							String parkId=jData.getString("parkId");
							if (parkingRecordId.contains(parkId)) {
								parkingRecordId = parkingRecordId.replaceAll(parkId, "");
							}else {
								parkingRecordId=parkingRecordId.substring(32);
							}
							pay.setHistoryId(Long.valueOf(parkingRecordId));
							SingleCarparkInOutHistory inOutHistory = sp.getCarparkInOutService().findInOutById(Long.valueOf(parkingRecordId));
							if (inOutHistory != null) {
								pay.setInTime(inOutHistory.getInTime());
								pay.setOutTime(inOutHistory.getOutTime());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						sp.getCarPayService().saveCarPayHistory(pay);
					}
				}
				CheckOnlineOrder order = new CheckOnlineOrder();
				order.setTime(StrUtil.formatDate(start));
				order.setStatus(0);
				sp.getCarparkInOutService().saveEntity(order);
			}
		} catch (Exception e) {
			LOGGER.error("网上支付对账时发生错误",e);
		}
	}
	@Override
	protected void startUp() throws Exception {
		SingleCarparkSystemSetting key = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用CJLAPP支付.name());
		if (key==null||!key.getBooleanValue()) {
			stopAsync();
			return;
		}
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("D:\\word\\车牌识别\\湖北工业大学\\湖北工业大学8-24同步记录.csv");
		List<String> list = Files.readAllLines(file.toPath());
		list.remove(0);
		int total=0;
		for (String s : list) {
			System.out.println(s);
			String substring = s.substring(s.indexOf(",\"{")+2, s.lastIndexOf("}\",")+1);
			System.out.println(substring);
			JSONObject jo = JSON.parseObject(substring);
			total += jo.getIntValue("onlineCost");
		}
		System.out.println(total);
	}

}
