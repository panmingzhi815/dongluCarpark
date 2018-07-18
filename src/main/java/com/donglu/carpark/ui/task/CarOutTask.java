package com.donglu.carpark.ui.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianRuntimeException;
import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.model.Result;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.view.message.MessageBoxUI.MessageBoxBtnCallback;
import com.donglu.carpark.ui.view.message.MessageUtil;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ImageUtils;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkCarType;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.FixCarInTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.ScreenTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor.VisitorStatus;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

public class CarOutTask extends AbstractTask{
	
	private static Logger LOGGER = LoggerFactory.getLogger(CarOutTask.class);
	
	// 保存设置信息
	private final Map<SystemSettingTypeEnum, String> mapSystemSetting;
	// 保存最近的手动拍照时间
	private final Map<String, Date> mapHandPhotograph;
	
	public static Map<String, CarparkCarType> mapTempCharge;
	
	public CarOutTask(String ip, String plateNO, byte[] bigImage, byte[] smallImage,CarparkMainModel model,
			CarparkDatabaseServiceProvider sp, CarparkMainPresenter presenter,Float rightSize) {
		super(model, sp, presenter, ip, plateNO, bigImage, smallImage, rightSize);
		mapSystemSetting = model.getMapSystemSetting();
		mapHandPhotograph = model.getMapHandPhotograph();
		mapTempCharge=model.getMapTempCharge();
		type="出场";
	}
	
	@Override
	public void start(){
		try {
			LOGGER.info("处理车辆：{} 的出场纪录",plateNO);
			model.setDisContinue(false);
			model.setHandSearch(false);
			model.setVisitor(null);
			long nanoTime = System.nanoTime();
			date = new Date();
			model.setLastCarOutTime(date.getTime());
			boolean checkPlateNODiscernGap = presenter.checkPlateNODiscernGap(mapPlateNoDate, plateNO, date);
			if (!checkPlateNODiscernGap) {
				return;
			}
			mapPlateNoDate.put(plateNO, date);
			initImgPath();
			long nanoTime1 = System.nanoTime();
			LOGGER.debug("==" + ip + "====" + plateNO);
			// 界面图片
			LOGGER.debug("车辆出场显示出口图片");
			model.setOutShowPlateNO(plateNO);
			model.setOutShowTime(StrUtil.formatDateTime(date));
			model.setOutBigImageName(bigImgFileName);
			model.setOutShowBigImg(bigImage);
			model.setOutShowSmallImg(smallImage);
			
			//
			if (StrUtil.isEmpty(device)) {
				LOGGER.error("没有找到ip为：" + ip + "的设备");
				return;
			}
			carpark = sp.getCarparkService().findCarparkById(device.getCarpark().getId());
			
			if (StrUtil.isEmpty(carpark)) {
				LOGGER.error("没有找到名字为：" + carpark + "的停车场");
				return;
			}
			model.setIp(ip);
			
			//
			if (StrUtil.isEmpty(plateNO)) {
				LOGGER.error("空的车牌");
				handSearch();
				model.setOutShowPlateNO("-无牌车");
				if (device.getScreenType().equals(ScreenTypeEnum.一体机)) {
					if (mapSystemSetting.get(SystemSettingTypeEnum.无车牌时使用二维码进出场).equals("true")) {
						SingleCarparkInOutHistory value = new SingleCarparkInOutHistory();
						value.setOutSmallImg(smallImgFileName);
						value.setOutBigImg(bigImgFileName);
						model.getMapWaitInOutHistory().put(device.getIp(), value);
						presenter.qrCodeInOut(plateNO, device, false);
					} else {
						presenter.showContentToDevice(editPlateNo, device, model.getMapVoice().get(DeviceVoiceTypeEnum.无牌车禁止扫码出场语音).getContent(), false);
					} 
				}
				return;
			}
			if(checkCarIsLock()){
				return;
			}
			
			refreshUserAndHistory(true);
			LOGGER.debug("车辆出场显示进口图片");
			if (cch!=null) {
				model.setInBigImageName(cch.getBigImg());
				model.setInShowBigImg(ImageUtils.getImageByte(cch.getBigImg()));
			}
			
			LOGGER.debug("发送显示车牌{}到设备{}",plateNO,ip);
			presenter.showPlateNOToDevice(device, plateNO);
			//
			long nanoTime3 = System.nanoTime();
			
			LOGGER.debug("车辆类型为：{}==通道类型为：{}", carType, device.getRoadType());
			long nanoTime2 = System.nanoTime();
			LOGGER.debug("==" + ip + "==" + device.getInType() + "==" + plateNO + "车辆类型：" + carType + "" + "保存图片：" + (nanoTime1 - nanoTime) + "==查找固定用户：" + (nanoTime2 - nanoTime3)
					+ "==界面操作：" + (nanoTime3 - nanoTime1));
			checkUserAndOut(true);
		} catch (Exception e) {
			LOGGER.error("车辆出场时发生错误",e);
			throw new RuntimeException(e.getCause());
		}
	
	}

	/**
	 * 
	 */
	public void handSearch() {
		model.setSearchPlateNo(plateNO);
		model.setSearchBigImage(bigImgFileName);
		model.setSearchSmallImage(smallImgFileName);
		model.setHandSearch(true);
		model.setOutPlateNOEditable(true);
	}

	/**
	 * @param carType
	 * @throws Exception
	 */
	public void checkUserAndOut(boolean check) throws Exception {
		if (!StrUtil.isEmpty(user)) {
			if (!user.getType().equals("储值")) {
				if (fixCarOutProcess(check)) {
					return;
				}
			}else{
				if(prepaidCarOut(device, date, carpark, bigImgFileName, smallImgFileName, user)){
					return;
				}
			}
			presenter.plateSubmit(cch, date, device, bigImage);
			presenter.updatePosition(carpark, cch, false);
		} else {// 临时车操作
			tempCarOutProcess(null);
		}
		model.getMapCameraLastImage().put(ip, bigImgFileName);
	}

	/**
	 * 
	 * @return true=已锁
	 */
	private boolean checkCarIsLock() {
		//锁车判断
		LOGGER.info("锁车判断");
		SingleCarparkLockCar findLockCarByPlateNO = sp.getCarparkInOutService().findLockCarByPlateNO(plateNO, true);
		if (!StrUtil.isEmpty(findLockCarByPlateNO)) {
			LOGGER.info("车辆已锁，禁止出场");
			presenter.showPlateNOToDevice(device, plateNO);
			presenter.showContentToDevice(editPlateNo,device, "车辆已锁", false);
			return true;
		}
		return false;
	}
	
	/**
	 * @param device
	 * @param date
	 * @param carpark
	 * @param bigImg
	 * @param smallImg
	 * @param user
	 * @param cch
	 * @return true=终止
	 */
	public boolean prepaidCarOut(SingleCarparkDevice device, Date date, SingleCarparkCarpark carpark, String bigImg, String smallImg, SingleCarparkUser user) {
		model.setOutShowPlateNO(model.getOutShowPlateNO()+"-储值车");
		if (cch==null) {
			notFindInHistory();
			return true;
		}
		LOGGER.info("储值车出场");
		if (CarparkUtils.checkRoadType(device,model, presenter, DeviceRoadTypeEnum.临时车通道,DeviceRoadTypeEnum.固定车通道)) {
			return true;
		}
		Float leftMoney = user.getLeftMoney();
		Float valueOf = Float.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.储值车进出场限制金额));
		String haveNoMoney = model.getMapVoice().get(DeviceVoiceTypeEnum.储值车余额不足语音).getContent();
		if (leftMoney<valueOf) {
			presenter.showContentToDevice(editPlateNo,device, haveNoMoney, false);
			return true;
		}
		
		CarTypeEnum parse = user.getCarType();
		Date inTime = cch.getInTime();
		float calculateTempCharge = sp.getCarparkService().calculateTempCharge(carpark.getId(),parse.index(), inTime, date);
		model.setPlateNo(cch.getPlateNo());
		model.setInTime(inTime);
		model.setOutTime(date);
		model.setShouldMony(calculateTempCharge);
		model.setReal(calculateTempCharge);
		model.setTotalTime(StrUtil.MinusTime2(inTime, date));
		model.setCarType("储值车");
		LOGGER.info("等待收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", plateNO, device.getCarpark(), "储值车", model.getInTime(), model.getOutTime(), model.getTotalTime(), model.getShouldMony());
		if (calculateTempCharge>leftMoney) {
			presenter.showContentToDevice(editPlateNo,device, CarparkUtils.formatFloatString("请缴费"+calculateTempCharge+"元,"+haveNoMoney), false);
			return true;
		}
		LOGGER.info("准备保存储值车出场数据到数据库");
		cch.setShouldMoney(calculateTempCharge);
		cch.setCarparkId(carpark.getId());
		cch.setCarparkName(carpark.getName());
		cch.setBigImg(bigImg);
		cch.setSmallImg(smallImg);
		cch.setOutTime(date);
		cch.setFactMoney(calculateTempCharge);
		cch.setFreeMoney(0);
		cch.setOutDevice(device.getName());
		cch.setOutSmallImg(smallImg);
		cch.setOutBigImg(bigImg);
		cch.setOutPlateNO(plateNO);
		user.setLeftMoney(user.getLeftMoney()-calculateTempCharge);
		
		//消费记录保存
		SingleCarparkPrepaidUserPayHistory pph=new SingleCarparkPrepaidUserPayHistory();
		pph.setCreateTime(date);
		pph.setOutTime(date);
		pph.setInTime(inTime);
		pph.setOperaName(System.getProperty("userName"));
		pph.setPayMoney(calculateTempCharge);
		pph.setPlateNO(user.getPlateNo());
		pph.setUserName(user.getName());
		pph.setUserType(user.getType());
		sp.getCarparkUserService().savePrepaidUserPayHistory(pph);
		
		sp.getCarparkUserService().saveUser(user);
		sp.getCarparkInOutService().saveInOutHistory(cch);
		LOGGER.info("保存储值车出场数据到数据库成功，对设备{}进行语音开闸",device);
		String sLeftMoney = "剩余"+user.getLeftMoney()+"元";
		if (user.getLeftMoney()<0) {
			sLeftMoney="欠费"+Math.abs(user.getLeftMoney())+"元";
		}
		String s =",扣费"+calculateTempCharge+"元,"+sLeftMoney;
		s = CarparkUtils.getCarStillTime(model.getTotalTime())+CarparkUtils.formatFloatString(s);
		String content = model.getMapVoice().get(DeviceVoiceTypeEnum.储值车出场语音).getContent();
		presenter.showContentToDevice(editPlateNo,device, s+","+content, true);
		return false;
	}
	/**
	 * 未找到进场记录操作
	 * @param device
	 * @param bigImg
	 * @param smallImg
	 */
	private void notFindInHistory() {
		LOGGER.info("没有找到车牌{}的入场记录", plateNO);
		presenter.showPlateNOToDevice(device, plateNO);
		presenter.showContentToDevice(editPlateNo,device, model.getMapVoice().get(DeviceVoiceTypeEnum.无进场记录语音).getContent(), false);
		model.setOutShowPlateNO(model.getOutShowPlateNO()+"-未入场");
		handSearch();
		model.setSearchCarpark(device.getCarpark());
	}
	
	/**
	 * @param check 
	 * @return 返回true终止操作
	 */
	private boolean fixCarOutProcess(boolean check) throws Exception {
		carType = "固定车";
		logger.info("固定车出场");
		if (check) {
			model.setOutShowPlateNO(model.getOutShowPlateNO()+"-"+user.getName());
			if (CarparkUtils.checkRoadType(device, model, presenter, DeviceRoadTypeEnum.临时车通道,DeviceRoadTypeEnum.储值车通道)) {
				return true;
			}
			// 固定车出场确认
			Boolean valueOf = Boolean
					.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认) == null ? SystemSettingTypeEnum.固定车出场确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认));
			if (valueOf) {
				presenter.showContentToDevice(editPlateNo,device, "固定车等待确认", false);
				model.setOutCheckClick(true);
				model.setOutPlateNOEditable(true);
				model.getMapOutCheck().put(plateNO, this);
				return true;
			}
		}
		Date validTo = user.getValidTo();
		List<SingleCarparkUser> findUserByNameAndCarpark = sp.getCarparkUserService().findUserByNameAndCarpark(editPlateNo, carpark, null);
		for (SingleCarparkUser singleCarparkUser : findUserByNameAndCarpark) {
			if (singleCarparkUser.getValidTo().after(validTo)) {
				validTo=singleCarparkUser.getValidTo();
				user=singleCarparkUser;
			}
		}
		Integer delayDays = user.getDelayDays();

		Calendar c = Calendar.getInstance();
		c.setTime(validTo);
		c.add(Calendar.DATE, delayDays);
		Date time = c.getTime();
		//过期判断
		if (StrUtil.getTodayBottomTime(time).before(date)) {
			LOGGER.info("车辆:{}已到期", editPlateNo);
			Date d = null;
			if (StrUtil.isEmpty(cch) || cch.getInTime().before(validTo)) {
				d = validTo;
			} else {
				d = cch.getInTime();
			}
			if (cch!=null&&cch.getReviseInTime()!=null) {
				presenter.showContentToDevice(editPlateNo,device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent(), false);
				boolean confirm = new ConfimBox(editPlateNo, "车辆在[" + StrUtil.formatDate(validTo) + "]过期\n是否允许车辆按临时车计费出场:["+carpark.getName()+"]")
						.open();
				if (confirm) {
					tempCarOutProcess(cch.getReviseInTime());
				}
				return true;
			}
			if (Boolean.valueOf(getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期变临时车))) {
				tempCarOutProcess(d);
				return true;
			} else if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期所属停车场限制).equals("true")) {
				if (device.getCarpark().equals(user.getCarpark())) {
					presenter.showContentToDevice(editPlateNo,device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent() + StrUtil.formatDate(user.getValidTo(), ConstUtil.VILIDTO_DATE), true);
				}
			}
			presenter.showContentToDevice(editPlateNo,device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent() + StrUtil.formatDate(user.getValidTo(), ConstUtil.VILIDTO_DATE), false);
			model.setOutShowPlateNO(model.getOutShowPlateNO()+"-已过期");
			return true;
		}
		//车位判断
		if (cch!=null&&!StrUtil.isEmpty(cch.getReviseInTime())&&!cch.getIsOverdue()) {
			boolean isTemp=true;
			if(mapSystemSetting.get(SystemSettingTypeEnum.绑定车辆允许场内换车).equals("true")&&mapSystemSetting.get(SystemSettingTypeEnum.换车时间内车辆无限制).equals("true")){
				Integer integer = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.绑定车辆场内换车时间));
				if(!new DateTime(cch.getReviseInTime()).plusMinutes(integer).toDate().before(date)){
					isTemp=false;
				}
			}
			if (isTemp) {
				boolean confirm = new ConfimBox(editPlateNo, "用户车位满进场,进场时车辆" + cch.getRemarkString() + "在场内\n是否作为临时车出场[" + carpark.getName() + "]").open();
				if (confirm) {
					LOGGER.info("固定车做临时车计费：{}", cch.getReviseInTime());
					tempCarOutProcess(cch.getReviseInTime());
					model.setUser(user);
				}
				return true;
			}
		}
		boolean fixCarStillCharge = CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车非所属停车场停留收费).equals("true")&&!device.getCarpark().equals(user.getCarpark());
		model.setPlateNo(editPlateNo);
		model.setCarType(carType);
		model.setOutTime(date);
		model.setShouldMony(0);
		model.setReal(0);
		model.setChargedMoney(0f);
		// 未找到入场记录
		if (StrUtil.isEmpty(cch)) {
			logger.warn("为找到固定车{}的入场记录",editPlateNo);
			cch=new SingleCarparkInOutHistory();
			cch.setCarparkId(carpark.getId());
			cch.setCarparkName(carpark.getName());
			model.setInTime(null);
			model.setTotalTime("未入场");
			if (fixCarStillCharge) {
				logger.info("固定车非所属停车场停留超时收费设置为{},终止放行",fixCarStillCharge);
				notFindInHistory();
				return true;
			}
		}else{
			Date inTime = cch.getInTime();
			model.setInTime(inTime);
			model.setTotalTime(StrUtil.MinusTime2(inTime, date));
		}
		cch.setPlateNo(editPlateNo);
		cch.setCarType(carType);
		cch.setUserId(user.getId());
		cch.setUserName(user.getName());
		if (fixCarStillCharge) {
			logger.info("固定车非所属停车场停留超时收费设置为{}",fixCarStillCharge);
			float shouldMoney=presenter.countFixCarShouldMoney(user,device,cch.getInTime(),date,plateNO);
			if (shouldMoney>0) {
				model.setShouldMony(shouldMoney);
				model.setReal(shouldMoney);
				showContentToDevice(device, "请缴费"+shouldMoney+"元", false);
				model.setChargeDevice(device);
				model.setChargeHistory(cch);
				model.setBtnClick(true);
				return true;
			}
		}
		
		c.setTime(validTo);
		c.add(Calendar.DATE, user.getRemindDays() == null ? 0 : user.getRemindDays() * -1);
		time = c.getTime();
		String content = model.getMapVoice().get(DeviceVoiceTypeEnum.固定车出场语音).getContent();
		
		if (StrUtil.getTodayBottomTime(time).before(date)) {
			presenter.showContentToDevice(editPlateNo,device, content + ",剩余" + CarparkUtils.countDayByBetweenTime(date, user.getValidTo()) + "天", true);
			LOGGER.info("车辆:{}即将到期", editPlateNo);
		} else {
			presenter.showContentToDevice(editPlateNo,device, content, true);
		}
		if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.绑定车辆允许场内换车))) {
			LOGGER.info("允许场内换车");
			if (carpark.getParent()==null) {
				List<SingleCarparkUser> listUsers = findUserByNameAndCarpark.stream().filter(new Predicate<SingleCarparkUser>() {
					@Override
					public boolean test(SingleCarparkUser t) {
						return t.getValidTo()!=null&&t.getValidTo().after(date);
					}
				}).collect(Collectors.toList());
				Set<String> plates=new HashSet<>();
				int totalSlot=0;
				boolean isCheckSlot=false;
				for (SingleCarparkUser user : listUsers) {
					String[] split = user.getPlateNo().split(",");
					plates.addAll(Arrays.asList(split));
					Integer carparkSlot = user.getCarparkSlot();
					if (split.length>carparkSlot) {
						isCheckSlot=true;
					}
					totalSlot+=carparkSlot;
				}
				if (isCheckSlot) {
					Integer integer = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.绑定车辆场内换车时间));
					Date s = new DateTime(date).minusMinutes(integer).toDate();
					List<SingleCarparkInOutHistory> list=sp.getCarparkInOutService().findInOutHistoryByInTime(0,Integer.MAX_VALUE,plates,s);
					plates.clear();
					for (SingleCarparkInOutHistory singleCarparkInOutHistory : list) {
						String p = singleCarparkInOutHistory.getPlateNo();
						if(p.contains(editPlateNo)){
							continue;
						}
						if (plates.size()>=totalSlot&&!plates.contains(p)) {
							continue;
						}
						if(singleCarparkInOutHistory.getReviseInTime()!=null||singleCarparkInOutHistory.getFixCarInType().ordinal()>1){
							singleCarparkInOutHistory.setFixCarInType(FixCarInTypeEnum.固定车);
							singleCarparkInOutHistory.setIsOverdue(null);
							singleCarparkInOutHistory.setReviseInTime(null);
							singleCarparkInOutHistory.setRemarkString(singleCarparkInOutHistory.getRemarkString()+";"+editPlateNo+"出场换车");
							plates.add(p);
							sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
						}
					}
				}
			}
		}
		saveOutHistory();
		if (mapSystemSetting.get(SystemSettingTypeEnum.固定车非所属停车场停留收费).equals("true")) {
			sp.getCarparkInOutService().updateCarparkStillTime(device.getCarpark(), device, plateNO, bigImgFileName);
		}
		model.setBtnClick(false);
		return false;
	}

	private void showContentToDevice(SingleCarparkDevice device, String content, boolean isOpenDoor) {
		if (cch.getShouldMoney()!=null&&cch.getShouldMoney()>0&&device.getScreenType().equals(ScreenTypeEnum.一体机)&&(device.getIsHandCharge()&&mapSystemSetting.get(SystemSettingTypeEnum.使用二维码缴费).equals("true"))) {
			return;
		}
		presenter.showContentToDevice(editPlateNo, device, content, isOpenDoor);
	}

	/**
	 * @return false 出场完成，true 做临时车
	 */
	private String getSettingValue(Map<SystemSettingTypeEnum,String> map,SystemSettingTypeEnum type) {
		return map.get(type)==null?type.getDefaultValue():mapSystemSetting.get(type);
	}
	private boolean visitorCarOut(){
		SingleCarparkVisitor visitor = sp.getCarparkService().findVisitorByPlateAndCarpark(plateNO, carpark);
		if (visitor==null||visitor.getStatus().equals(VisitorStatus.不可用.name())||visitor.getOutNeedCharge()) {
			return true;
		}
		boolean flag=false;
		Date validTo = visitor.getValidTo();
		validTo=StrUtil.getTodayBottomTime(validTo);
		if (validTo!=null) {
			//过期判断
			if (validTo.before(date)) {
				flag= true;
				if (Boolean.valueOf(model.getMapSystemSetting().getOrDefault(SystemSettingTypeEnum.访客车进场次数用完不能随便出, "false"))) {
					model.setOutShowPlateNO(model.getOutShowPlateNO() + "-出场限制");
					visitor.setStatus(VisitorStatus.不可用.name());
					sp.getCarparkService().saveVisitor(visitor);
					return false;
				}
			} else {//判断是否超次
				Integer allIn = visitor.getAllIn();
				if (allIn != null && allIn > 0) {
					int inCount = visitor.getOutCount();
					if (visitor.getInCount() >= allIn) {
						flag = true;
					}
					if (inCount >= allIn) {
						if (Boolean.valueOf(model.getMapSystemSetting().getOrDefault(SystemSettingTypeEnum.访客车进场次数用完不能随便出, "false"))) {
							model.setOutShowPlateNO(model.getOutShowPlateNO() + "-出场限制");
							if (visitor.getInCount() >= allIn) {
								visitor.setStatus(VisitorStatus.不可用.name());
								sp.getCarparkService().saveVisitor(visitor);
							}
							return false;
						}
					}
				}
			}
		}else{
			Integer allIn = visitor.getAllIn();
			int inCount = visitor.getOutCount();
			if (allIn!=null&&allIn>0) {
				if(visitor.getInCount()>=allIn){
					flag=true;
				}
				if (inCount >= allIn) {
					if (Boolean.valueOf(model.getMapSystemSetting().getOrDefault(SystemSettingTypeEnum.访客车进场次数用完不能随便出, "false"))) {
						model.setOutShowPlateNO(model.getOutShowPlateNO() + "-出场限次");
						if (visitor.getInCount() >= allIn) {
							visitor.setStatus(VisitorStatus.不可用.name());
							sp.getCarparkService().saveVisitor(visitor);
						}
						return false;
					}
				}
			}
		}
		visitor.setOutCount(visitor.getOutCount()+1);
		if (flag){
			visitor.setStatus(VisitorStatus.不可用.name());
		}
		if(visitor.getOutNeedCharge()){
			model.setVisitor(visitor);
			return true;
		}
		sp.getCarparkService().saveVisitor(visitor);
		if (cch==null) {
			cch=new SingleCarparkInOutHistory();
			model.setTotalTime("未入场");
		}else{
			model.setTotalTime(StrUtil.MinusTime2(cch.getInTime(), date));
		}
		model.setPlateNo(plateNO);
		model.setCarType(ConstUtil.getVisitorName());
		model.setInTime(cch.getInTime());
		model.setOutTime(date);
		cch.setPlateNo(plateNO);
		cch.setCarType("临时车");
		cch.setRemarkString(ConstUtil.getVisitorName());
		saveOutHistory();
		showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
		model.setOutShowPlateNO(model.getOutShowPlateNO()+"-"+ConstUtil.getVisitorName());
		presenter.updatePosition(carpark, null, false);
		return false;
	}
	public void refreshUserAndHistory(boolean isNew) {
		logger.info("刷新固定用户和进场记录信息");
		if (isNew||!editPlateNo.equals(plateNO)) {
			user = sp.getCarparkUserService().findUserByPlateNo(editPlateNo,device.getCarpark().getId());
			if (user==null) {
				String plateLikeSize = mapSystemSetting.get(SystemSettingTypeEnum.固定车车牌匹配字符数);
				if (!plateLikeSize.equals("7")) {
					int likeSize = Integer.valueOf(plateLikeSize);
					List<SingleCarparkUser> list = sp.getCarparkUserService().findUserByPlateNoLikeSize(0,1,plateNO,likeSize, carpark.getId(),new Date());
					if (!StrUtil.isEmpty(list)) {
						//获取用户所有车牌
						Map<String, SingleCarparkUser> map=new HashMap<String, SingleCarparkUser>();
						Set<String> plates=new HashSet<>();
						for (SingleCarparkUser singleCarparkUser : list) {
							String[] split = singleCarparkUser.getPlateNo().split(",");
							for (String string : split) {
								map.put(string, singleCarparkUser);
								plates.add(string);
							}
						}
						//取得最相似的车牌
						editPlateNo=CarparkUtils.checkAlikeString(plateNO, plates.toArray(new String[plates.size()]));
						user=map.get(editPlateNo);
						model.setOutShowPlateNO(editPlateNo);
					}
				}
			}
			LOGGER.info("查找固定用户为{}",user);
			List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(editPlateNo,carpark);
			cch = StrUtil.isEmpty(findByNoOut)?null:findByNoOut.get(0);
			logger.info("获取到进场记录：{}",cch);
		}
	}
	/**
	 * 
	 */
	private void saveOutHistory() {
		logger.info("保存车牌{}出场数据到数据库",editPlateNo);
		cch.setOutPlateNO(plateNO);
		cch.setOutTime(date);
		cch.setOperaName(model.getUserName());
		cch.setOutDevice(device.getName());
		cch.setOutPhotographType("自动");
		cch.setOutBigImg(bigImgFileName);
		cch.setOutSmallImg(smallImgFileName);
		Date handPhotographDate = mapHandPhotograph.get(ip);
		if (!StrUtil.isEmpty(handPhotographDate)) {
			DateTime plusSeconds = new DateTime(handPhotographDate).plusSeconds(3);
			boolean after = plusSeconds.toDate().after(date);
			if (after)
				cch.setOutPhotographType("手动");
		}
		sp.getCarparkInOutService().saveInOutHistory(cch);
		logger.info("保存车牌{}出场数据到数据库成功",editPlateNo);
	}
	private void tempCarOutProcess(Date reviseInTime) throws Exception{
		if (!visitorCarOut()) {
			return;
		}
		logger.info("临时车：{} 计费出场",editPlateNo);
		model.setOutShowPlateNO(model.getOutShowPlateNO()+"-临时车");
		model.setPlateInTime(date, 30);
		if (!Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车通道限制))) {
			if (CarparkUtils.checkRoadType(device,model, presenter, DeviceRoadTypeEnum.储值车通道,DeviceRoadTypeEnum.固定车通道)) {
				return;
			}
		}
		Boolean isCharge = device.getCarpark().getIsCharge();
		if (!StrUtil.isEmpty(cch)) {//找到进场记录
			SingleCarparkInOutHistory singleCarparkInOutHistory = cch;
			if (!StrUtil.isEmpty(reviseInTime)) {
				singleCarparkInOutHistory.setReviseInTime(reviseInTime);
			}else{
				singleCarparkInOutHistory.setReviseInTime(singleCarparkInOutHistory.getInTime());
			}
			singleCarparkInOutHistory.setOutTime(date);
			singleCarparkInOutHistory.setOperaName(model.getUserName());
			singleCarparkInOutHistory.setOutDevice(device.getName());
			singleCarparkInOutHistory.setOutDeviceIp(device.getIp());
			singleCarparkInOutHistory.setOutPhotographType("自动");
			singleCarparkInOutHistory.setOutBigImg(bigImgFileName);
			singleCarparkInOutHistory.setOutSmallImg(smallImgFileName);
			singleCarparkInOutHistory.setOutPlateNO(plateNO);
			
			//
			Date handPhotographDate = mapHandPhotograph.get(ip);
			if (!StrUtil.isEmpty(handPhotographDate)) {
				DateTime plusSeconds = new DateTime(handPhotographDate).plusSeconds(3);
				boolean after = plusSeconds.toDate().after(date);
				if (after)
					singleCarparkInOutHistory.setOutPhotographType("手动");
			}
			
			Date inTime = singleCarparkInOutHistory.getReviseInTime();
			if (model.isBtnClick()&&device.getScreenType().equals(ScreenTypeEnum.一体机)&&mapSystemSetting.get(SystemSettingTypeEnum.使用二维码缴费).equals("true")&&!device.getIsHandCharge()) {
				carType=model.getPlateColorCache().asMap().getOrDefault(plateNO, "蓝色").contains("黄")?"大车":"小车";
				float countShouldMoney = presenter.countShouldMoney(device.getCarpark().getId(), carType, inTime, date,cch);
				if(countShouldMoney-cch.getFactMoney()>0){
					singleCarparkInOutHistory.setShouldMoney(countShouldMoney);
					if (fixCarExpireAutoChargeOut(countShouldMoney,0,countShouldMoney,false)) {
						return;
					}
					if (mapSystemSetting.get(SystemSettingTypeEnum.使用二维码缴费).equals("true")) {
						model.getMapWaitInOutHistory().put(device.getIp(), singleCarparkInOutHistory);
						presenter.qrCodeInOut(editPlateNo, device, false, singleCarparkInOutHistory,"缴费"+CarparkUtils.formatFloatString(countShouldMoney+"")+"元,请在黄线外扫码付费");
//						return;
						MessageUtil.info(plateNO, "车牌："+plateNO+"\t\n进场时间："+cch.getInTimeLabel()+"\t\n出场时间："+cch.getOutTimeLabel()
						+"\t\n停留时间："+StrUtil.MinusTime2(inTime, cch.getOutTime())+"\t\n应缴费用："+countShouldMoney,new String[]{"收费放行","免费放行"}, 120000,new MessageBoxBtnCallback() {
							@Override
							public int call(int result) {
								if (result==0) {
									return presenter.chargeCarPass(device, singleCarparkInOutHistory, true, countShouldMoney, 0, countShouldMoney, false)?0:1;
								}else if (result==1){
									return presenter.chargeCarPass(device, singleCarparkInOutHistory, true, countShouldMoney, 0, 0, false)?0:1;
								}
								return 1;
							}
						});
						presenter.checkCharge(device, singleCarparkInOutHistory);
					}
				}else{
					sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
					presenter.updatePosition(carpark, null, false);
					showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
				}
				return;
			}
			logger.info("显示车辆：{} 信息",editPlateNo);
			// 临时车操作
			model.setPlateNo(editPlateNo);
			model.setCarType("临时车");
			model.setOutTime(date);
			model.setInTime(inTime);
			model.setTotalTime(StrUtil.MinusTime2(inTime, date));
			model.setShouldMony(0);
			model.setReal(0);
			model.setChargedMoney(0F);
			if (StrUtil.isEmpty(isCharge) || !isCharge) {
				sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
				presenter.updatePosition(carpark, null, false);
				showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
			} else {
				float shouldMoney = 0;
				if (device.getScreenType().equals(ScreenTypeEnum.一体机)&&mapSystemSetting.get(SystemSettingTypeEnum.使用二维码缴费).equals("true")) {
					if (!device.getIsHandCharge()) {
						carType=model.getPlateColorCache().asMap().getOrDefault(plateNO, "蓝色").contains("黄")?"大车":"小车";
						shouldMoney = presenter.countShouldMoney(device.getCarpark().getId(), carType, inTime, date,cch);
						model.setShouldMony(shouldMoney);
						float chargedMoney=presenter.countChargedMoney(cch);
						model.setChargedMoney(chargedMoney>shouldMoney?shouldMoney:chargedMoney);
						
						model.setTotalTime(StrUtil.MinusTime2(inTime, singleCarparkInOutHistory.getOutTime()));
						singleCarparkInOutHistory.setShouldMoney(shouldMoney);
						model.setReal(model.getShouldMony()-model.getChargedMoney());
						if (fixCarExpireAutoChargeOut(shouldMoney,0,shouldMoney,false)) {
							return;
						}
						if (mapSystemSetting.get(SystemSettingTypeEnum.优先使用云平台计费).equals("false")) {
							Result result = presenter.getPayResult(singleCarparkInOutHistory);
							if (result!=null&&result.getCode() == 2005) {
								model.setReal(0);
								model.setChargedMoney(result.getPayedFee());
								model.setShouldMony(result.getPayedFee());
								singleCarparkInOutHistory.setFreeMoney(0);
								singleCarparkInOutHistory.setShouldMoney(result.getPayedFee());
								singleCarparkInOutHistory.setFactMoney(result.getPayedFee());
								singleCarparkInOutHistory.setChargeOperaName("在线支付");
								singleCarparkInOutHistory.setRemarkString("在线缴费完成，在规定时间内出场！");
								model.setPlateNo(singleCarparkInOutHistory.getPlateNo() + "-已在线支付");
								model.setChargeHistory(singleCarparkInOutHistory);
								model.setChargeDevice(device);
								presenter.charge(false);
								return;
							} 
						}
						if (shouldMoney-model.getChargedMoney()>0) {
							model.getMapWaitInOutHistory().put(device.getIp(), singleCarparkInOutHistory);
							presenter.qrCodeInOut(editPlateNo, device, false, singleCarparkInOutHistory,"缴费"+CarparkUtils.formatFloatString(shouldMoney+"")+"元,请在黄线外扫码付费");
							presenter.checkCharge(device,singleCarparkInOutHistory);
							return;
						}
					}
				}
				String carType = getCarType(singleCarparkInOutHistory);
				if (carType==null) {
					return;
				}
				shouldMoney = presenter.countShouldMoney(device.getCarpark().getId(), carType, inTime, date,cch);
				model.setTotalTime(StrUtil.MinusTime2(inTime, singleCarparkInOutHistory.getOutTime()));
				singleCarparkInOutHistory.setShouldMoney(shouldMoney);
				// model.setComboCarTypeEnable(false);
				
				//集中收费
				Boolean idConcentrate=Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用集中收费));
				if (idConcentrate) {
					Float chargedMoney = singleCarparkInOutHistory.getFactMoney()==null?0:singleCarparkInOutHistory.getFactMoney();
					if (shouldMoney>0) {
						Date chargeTime = singleCarparkInOutHistory.getChargeTime();
						if (StrUtil.isEmpty(chargeTime)) {
							showContentToDevice(device, "请到管理处缴费", false);
							return;
						}
						Integer concentrateLateTime = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.集中收费延迟出场时间));
						DateTime plusMinutes = new DateTime(chargeTime).plusMinutes(concentrateLateTime);
						if (plusMinutes.isBeforeNow()) {
							if (shouldMoney>chargedMoney) {
								String content = "应缴费"+shouldMoney+"元,已缴费"+chargedMoney+"元,请到管理处续费";
								presenter.showContentToDevice(editPlateNo,device, CarparkUtils.formatFloatString(content), false);
								return;
							}
						}
					}
					singleCarparkInOutHistory.setOutTime(date);
					singleCarparkInOutHistory.setBigImg(bigImgFileName);
					singleCarparkInOutHistory.setSmallImg(smallImgFileName);
					model.setShouldMony(shouldMoney);
					singleCarparkInOutHistory.setShouldMoney(shouldMoney);
					model.setChargedMoney(chargedMoney);
					model.setReal(shouldMoney-chargedMoney);
				}else{
					model.setShouldMony(shouldMoney);
					singleCarparkInOutHistory.setShouldMoney(shouldMoney);
					model.setReal(model.getShouldMony()-model.getChargedMoney());
				}
				model.setCartypeEnum(carType);
				LOGGER.info("等待收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", plateNO, device.getCarpark(), carType, model.getInTime(), model.getOutTime(), model.getTotalTime(), shouldMoney);
				String s = "请缴费" + shouldMoney + "元";
				s = CarparkUtils.getCarStillTime(model.getTotalTime())+CarparkUtils.formatFloatString(s);

				String property = System.getProperty(ConstUtil.TEMP_CAR_AUTO_PASS);
				Boolean valueOf = Boolean.valueOf(property);
				// 临时车零收费是否自动出场
				if (shouldMoney>0) {
					presenter.checkIsPay(singleCarparkInOutHistory, 0f, false);
					if (model.getChargedMoney() > 0 && model.getReal() > 0) {
						s += ",已缴费" + CarparkUtils.formatFloatString(model.getChargedMoney() + "") + "元";
					} 
				}
				Boolean tempCarNoChargeIsPass = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车零收费是否自动出场));
				model.setBtnClick(true);
				LOGGER.info("等待收费");
				if (tempCarNoChargeIsPass) {
					if (model.getReal() > 0) {
						showContentToDevice(device, s, false);
						model.setChargeDevice(device);
						model.setChargeHistory(singleCarparkInOutHistory);
						if (fixCarExpireAutoChargeOut(model.getShouldMony(),model.getChargedMoney(),model.getReal(),true)) {
							return;
						}
					} else {
						presenter.chargeCarPass(device, singleCarparkInOutHistory, false);
					}
				} else {
					showContentToDevice(device, s, false);
					model.setChargeDevice(device);
					model.setChargeHistory(singleCarparkInOutHistory);
					if (fixCarExpireAutoChargeOut(model.getShouldMony(),model.getChargedMoney(),model.getReal(),true)) {
						return;
					}
				}
//				handSearch();
				if (valueOf) {
					singleCarparkInOutHistory.setFactMoney(shouldMoney);
					presenter.chargeCarPass(device, singleCarparkInOutHistory, false);
				}
				if (model.getReal()>0&&device.getScreenType().equals(ScreenTypeEnum.一体机)&&mapSystemSetting.get(SystemSettingTypeEnum.使用二维码缴费).equals("true")) {
					if (device.getIsHandCharge()) {
    					model.getMapWaitInOutHistory().put(device.getIp(), singleCarparkInOutHistory);
    					presenter.qrCodeInOut(editPlateNo, device, false, singleCarparkInOutHistory,"缴费"+CarparkUtils.formatFloatString(shouldMoney+"")+"元,请在黄线外扫码付费");
						return;
					}
				}
			}
		}else{
			LOGGER.info("车辆{}未入场且停车场是否收费设置为{}",plateNO,isCharge);
			if (!isCharge) {
				SingleCarparkInOutHistory io=new SingleCarparkInOutHistory();
				io.setPlateNo(plateNO);
				io.setOutBigImg(bigImgFileName);
				io.setOutSmallImg(smallImgFileName);
				io.setOutTime(date);
				io.setFactMoney(0);
				io.setShouldMoney(0);
				io.setFreeMoney(0);
				io.setCarparkId(device.getCarpark().getId());
				io.setCarparkName(device.getCarpark().getName());
				io.setCarType("临时车");
				io.setOutDevice(device.getName());
				io.setOperaName(System.getProperty("userName"));
				io.setOutPlateNO(plateNO);
				model.setPlateNo(plateNO);
				model.setInTime(null);
				model.setOutTime(date);
				model.setCarType("临时车");
				model.setTotalTime("未入场");
				sp.getCarparkInOutService().saveInOutHistory(io);
				LOGGER.info("保存车辆{}的出场记录成功",plateNO);
				presenter.showPlateNOToDevice(device, plateNO);
				presenter.updatePosition(carpark, io, false);
				presenter.showContentToDevice(editPlateNo,device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
			}else{
				notFindInHistory();
//				if(device.getScreenType().equals(ScreenTypeEnum.一体机)&&mapSystemSetting.get(SystemSettingTypeEnum.无车牌时使用二维码进出场).equals("true")){
//					SingleCarparkInOutHistory value = new SingleCarparkInOutHistory();
//					value.setOutSmallImg(smallImgFileName);
//					value.setOutBigImg(bigImgFileName);
//					model.getMapWaitInOutHistory().put(device.getIp(), value);
//					presenter.qrCodeInOut("", device, false);
//				}
			}
		}
	}

	/**
	 * @param factMoney 
	 * @param chargedMoney 
	 * @param shouldMoney 
	 * @param updateui 
	 * 
	 */
	public boolean fixCarExpireAutoChargeOut(float shouldMoney, float chargedMoney, float factMoney, boolean updateui) {
		String string = mapSystemSetting.get(SystemSettingTypeEnum.固定车到期变临时车收费自动记费出场);
		if (cch.getUserName() != null && !StrUtil.isEmpty(string)) {
			List<String> list = Arrays.asList(string.split(","));
			if (list.contains(cch.getUserName())) {
				return presenter.chargeCarPass(device, cch, false, shouldMoney, 0, 0, updateui);
			}
		}
		return false;
	}

	/**
	 * @param singleCarparkInOutHistory
	 * @return
	 */
	public String getCarType(SingleCarparkInOutHistory singleCarparkInOutHistory) {
		String carType = "小车";
		if (mapTempCharge.keySet().size() > 1) {
			model.setComboCarTypeEnable(true);
			model.setSelectCarType(true);
			model.setCarparkCarType("请选择车型");
			Boolean autoSelectCarType = Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.自动识别出场车辆类型));
			if (!autoSelectCarType&&!model.isBtnClick()) {
				model.setChargeDevice(device);
				model.setChargeHistory(singleCarparkInOutHistory);
				model.setBtnClick(true);
				return null;
			}
			//自动识别大车小车
			if (autoSelectCarType) {
				String s = model.getPlateColorCache().asMap().get(editPlateNo);
				if (!StrUtil.isEmpty(s)) {
					boolean b = !StrUtil.isEmpty(mapTempCharge.get("大车"));
					if (s.contains("黄")&&b) {
						model.setCarparkCarType("大车");
					}else{
						model.setCarparkCarType("小车");
					}
				}
			}
			carType = model.getCarparkCarType();
			if(carType.contains("请选择")){
				carType = "小车";
			}
		} else if (mapTempCharge.keySet().size() == 1) {
			List<String> list = new ArrayList<>();
			list.addAll(mapTempCharge.keySet());
			carType = list.get(0);
		}
		return carType;
	}
	
	private CarTypeEnum getCarparkCarType(String carparkCarType) {
		if (carparkCarType.equals("大车")) {
			return CarTypeEnum.BigCar;
		}
		if (carparkCarType.equals("小车")) {
			return CarTypeEnum.SmallCar;
		}
		if (carparkCarType.equals("摩托车")) {
			return CarTypeEnum.Motorcycle;
		}
		return CarTypeEnum.SmallCar;
	}

	public String getPlateNO() {
		return plateNO;
	}

	public void setPlateNO(String plateNO) {
		this.plateNO = plateNO;
	}

	public byte[] getBigImage() {
		return bigImage;
	}

	public void setBigImage(byte[] bigImage) {
		this.bigImage = bigImage;
	}

	public byte[] getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(byte[] smallImage) {
		this.smallImage = smallImage;
	}

	public Float getRightSize() {
		return rightSize;
	}
	

	public void setRightSize(Float rightSize) {
		this.rightSize = rightSize;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return "ip："+ip+"-车牌："+plateNO+"-清晰度："+rightSize;
	}

	public void setEditPlateNO(String editPlateNO) {
		this.editPlateNo = editPlateNO;
	}

	public String getBigImgFilePath() {
		return bigImgFileName;
	}
}
