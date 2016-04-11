package com.donglu.carpark.ui.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.CarparkMainApp;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

public class CarOutTask implements Runnable{
	private static final Display DEFAULT_DISPLAY = Display.getDefault();

	private static Logger LOGGER = LoggerFactory.getLogger(CarInTask.class);
	
	
	private String plateNO;
	private String ip;
	private final CarparkMainModel model;
	private final CarparkDatabaseServiceProvider sp;
	private final CarparkMainPresenter presenter;
	private final CLabel lbl_outBigImg;
	private final CLabel lbl_outSmallImg;
	private final CLabel lbl_inBigImg;
	
	private final Text text_real;
	
	private byte[] bigImage;
	private byte[] smallImage;
	private final Combo carTypeSelectCombo;
	
	// 保存车牌最近的处理时间
	private final Map<String, Date> mapPlateNoDate = CarparkMainApp.mapPlateNoDate;
	// 保存设备的信息
	private final Map<String, SingleCarparkDevice> mapIpToDevice = CarparkMainApp.mapIpToDevice;
	// 保存设置信息
	private final Map<SystemSettingTypeEnum, String> mapSystemSetting = CarparkMainApp.mapSystemSetting;
	// 保存最近的手动拍照时间
	private final Map<String, Date> mapHandPhotograph = CarparkMainApp.mapHandPhotograph;

	private Float rightSize;

	
	public static Map<String, String> mapTempCharge=CarparkMainApp.mapTempCharge;

	private Date date;

	private SingleCarparkDevice device;

	private SingleCarparkUser user;

	private SingleCarparkInOutHistory ch;
	
	public CarOutTask(String ip, String plateNO, byte[] bigImage, byte[] smallImage,CarparkMainModel model,
			CarparkDatabaseServiceProvider sp, CarparkMainPresenter presenter, CLabel lbl_outBigImg,
			CLabel lbl_outSmallImg,CLabel lbl_inBigImg, Combo carTypeSelectCombo,
			Text text_real,Float rightSize) {
		super();
		this.ip = ip;
		this.plateNO = plateNO;
		this.bigImage = bigImage;
		this.smallImage = smallImage;
		this.model = model;
		this.sp = sp;
		this.presenter = presenter;
		this.lbl_outBigImg = lbl_outBigImg;
		this.lbl_outSmallImg = lbl_outSmallImg;
		this.carTypeSelectCombo=carTypeSelectCombo;
		this.text_real=text_real;
		this.rightSize=rightSize;
		this.lbl_inBigImg=lbl_inBigImg;
	}
	
	@Override
	public void run(){
		try {
			device = mapIpToDevice.get(ip);
			
			
			model.setDisContinue(false);
			model.setHandSearch(false);
			long nanoTime = System.nanoTime();
			date = new Date();
			boolean checkPlateNODiscernGap = presenter.checkPlateNODiscernGap(mapPlateNoDate, plateNO, date);
			if (!checkPlateNODiscernGap) {
				return;
			}
			mapPlateNoDate.put(plateNO, date);
			//
			String folder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
			String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
			String bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
			String smallImgFileName = fileName + "_" + plateNO + "_small.jpg";
			
			presenter.saveImage(folder, smallImgFileName, bigImgFileName, smallImage, bigImage);
			long nanoTime1 = System.nanoTime();
			final String dateString = StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
			// System.out.println(dateString + "==" + ip + "==" + mapDeviceType.get(ip) + "==" + plateNO);
			LOGGER.info(dateString + "==" + ip + "====" + plateNO);

			// 界面图片
			LOGGER.info("车辆出场显示出口图片");
			DEFAULT_DISPLAY.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (StrUtil.isEmpty(lbl_outSmallImg)) {
						return;
					}
					CarparkUtils.setBackgroundImage(bigImage, lbl_outBigImg, DEFAULT_DISPLAY);
					CarparkUtils.setBackgroundImage(smallImage, lbl_outSmallImg, DEFAULT_DISPLAY);
					CarparkUtils.setBackgroundImageName(lbl_outBigImg, folder + "/" + bigImgFileName);
					text_real.setFocus();
					text_real.selectAll();
				}
			});
			model.setOutShowPlateNO(plateNO);
			model.setOutShowTime(dateString);
			//
			if (StrUtil.isEmpty(device)) {
				LOGGER.info("没有找到ip为：" + ip + "的设备");
				return;
			}
			SingleCarparkCarpark carpark = device.getCarpark();
			
			if (StrUtil.isEmpty(carpark)) {
				LOGGER.info("没有找到名字为：" + carpark + "的停车场");
				return;
			}
			model.setIp(ip);
			String bigImgSaveFilePath = folder + "/" + bigImgFileName;
			String smallImgSaveFilePath = folder + "/" + smallImgFileName;
			//
			if (StrUtil.isEmpty(plateNO)) {
				LOGGER.error("空的车牌");
				model.setSearchPlateNo(plateNO);
				model.setSearchBigImage(bigImgSaveFilePath);
				model.setSearchSmallImage(smallImgSaveFilePath);
				model.setHandSearch(true);
				model.setOutPlateNOEditable(true);
				return;
			}
			//锁车判断
			SingleCarparkLockCar findLockCarByPlateNO = sp.getCarparkInOutService().findLockCarByPlateNO(plateNO, true);
			if (!StrUtil.isEmpty(findLockCarByPlateNO)) {
				presenter.showPlateNOToDevice(device, plateNO);
				presenter.showContentToDevice(device, "车辆已锁", false);
				return;
			}
			
			user = sp.getCarparkUserService().findUserByPlateNo(plateNO,device.getCarpark().getId());
			// 没有找到入场记录
			List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(plateNO,carpark);
			if (StrUtil.isEmpty(user)||user.getType().equals("储值")) {
				if (StrUtil.isEmpty(findByNoOut)&&device.getCarpark().getIsCharge()) {
					notFindInHistory(device, bigImgSaveFilePath, smallImgSaveFilePath);
					return;
				}
			}
			ch = StrUtil.isEmpty(findByNoOut)?null:findByNoOut.get(0);
			LOGGER.info("车辆出场显示进口图片");
			DEFAULT_DISPLAY.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (StrUtil.isEmpty(ch)||StrUtil.isEmpty(lbl_inBigImg)) {
						return;
					}
					CarparkUtils.setBackgroundImage(CarparkUtils.getImageByte(ch.getBigImg()), lbl_inBigImg, DEFAULT_DISPLAY);
					CarparkUtils.setBackgroundImageName(lbl_inBigImg, ch.getBigImg());
				}
			});
			
			

			presenter.showPlateNOToDevice(device, plateNO);
			//
			long nanoTime3 = System.nanoTime();
			String carType = "临时车";
			
			if (!StrUtil.isEmpty(user)) {
				carType="固定车";
			}
			String roadType = device.getRoadType();
			LOGGER.info("车辆类型为：{}==通道类型为：{}", carType, roadType);
			long nanoTime2 = System.nanoTime();
			LOGGER.info(dateString + "==" + ip + "==" + device.getInType() + "==" + plateNO + "车辆类型：" + carType + "" + "保存图片：" + (nanoTime1 - nanoTime) + "==查找固定用户：" + (nanoTime2 - nanoTime3)
					+ "==界面操作：" + (nanoTime3 - nanoTime1));
			boolean equals = roadType.equals(DeviceRoadTypeEnum.固定车通道.name());

			if (carType.equals("固定车")) {
				if (!user.getType().equals("储值")) {
					if (fixCarOutProcess(bigImgSaveFilePath, smallImgSaveFilePath)) {
						return;
					}
				}else{
					if(prepaidCarOut(bigImgSaveFilePath, smallImgSaveFilePath)){
						return;
					}
				}
				presenter.plateSubmit(ch, date, device, bigImage);
			} else {// 临时车操作
				//固定车通道
				if (equals) {
					presenter.showContentToDevice(device, CarparkMainApp.FIX_ROAD, false);
					return;
				}
				tempCarOutProcess(ip, plateNO, device, date, bigImgSaveFilePath, smallImgSaveFilePath,null);
			}
			CarparkMainApp.mapCameraLastImage.put(ip, bigImgSaveFilePath);
		} catch (Exception e) {
			LOGGER.error("车辆出场时发生错误",e);
		}
	
	}
	
	/**
	 * @param device
	 * @param date
	 * @param carpark
	 * @param bigImg
	 * @param smallImg
	 * @param user
	 * @param ch
	 * @return 
	 */
	public boolean prepaidCarOut(String bigImg, String smallImg) {
		LOGGER.info("储值车出场");
		SingleCarparkCarpark carpark=device.getCarpark();
		if (CarparkUtils.checkRoadType(device,model, presenter, DeviceRoadTypeEnum.临时车通道,DeviceRoadTypeEnum.固定车通道)) {
			return true;
		}
		Float leftMoney = user.getLeftMoney();
		Float valueOf = Float.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.储值车进出场限制金额));
		String haveNoMoney = model.getMapVoice().get(DeviceVoiceTypeEnum.储值车余额不足语音).getContent();
		if (leftMoney<valueOf) {
			presenter.showContentToDevice(device, haveNoMoney, false);
			return true;
		}
		
		CarTypeEnum parse = user.getCarType();
		Date inTime = ch.getInTime();
		float calculateTempCharge = sp.getCarparkService().calculateTempCharge(carpark.getId(),parse.index(), inTime, date);
		model.setPlateNo(ch.getPlateNo());
		model.setInTime(inTime);
		model.setOutTime(date);
		model.setShouldMony(calculateTempCharge);
		model.setReal(calculateTempCharge);
		model.setTotalTime(StrUtil.MinusTime2(inTime, date));
		model.setCarType("储值车");
		LOGGER.info("等待收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", plateNO, device.getCarpark(), "储值车", model.getInTime(), model.getOutTime(), model.getTotalTime(), model.getShouldMony());
		if (calculateTempCharge>leftMoney) {
			presenter.showContentToDevice(device, CarparkUtils.formatFloatString("请缴费"+calculateTempCharge+"元,"+haveNoMoney), false);
			return true;
		}
		LOGGER.info("准备保存储值车出场数据到数据库");
		ch.setShouldMoney(calculateTempCharge);
		ch.setCarparkId(carpark.getId());
		ch.setCarparkName(carpark.getName());
		ch.setBigImg(bigImg);
		ch.setSmallImg(smallImg);
		ch.setOutTime(date);
		ch.setFactMoney(calculateTempCharge);
		ch.setFreeMoney(0);
		ch.setOutDevice(device.getName());
		ch.setOutSmallImg(smallImg);
		ch.setOutBigImg(bigImg);
		ch.setOutPlateNO(plateNO);
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
		sp.getCarparkInOutService().saveInOutHistory(ch);
		LOGGER.info("保存储值车出场数据到数据库成功，对设备{}进行语音开闸",device);
		String s =",扣费"+calculateTempCharge+"元,剩余"+user.getLeftMoney()+"元";
		s = CarparkUtils.getCarStillTime(model.getTotalTime())+CarparkUtils.formatFloatString(s);
		String content = model.getMapVoice().get(DeviceVoiceTypeEnum.储值车出场语音).getContent();
		presenter.showContentToDevice(device, s+","+content, true);
		return false;
	}
	/**
	 * 未找到进场记录操作
	 * @param device
	 * @param bigImg
	 * @param smallImg
	 */
	private void notFindInHistory(SingleCarparkDevice device, String bigImg, String smallImg) {
		LOGGER.info("没有找到车牌{}的入场记录", plateNO);
		presenter.showPlateNOToDevice(device, plateNO);
		presenter.showContentToDevice(device, "此车未入场", false);
		model.setSearchPlateNo(plateNO);
		model.setSearchBigImage(bigImg);
		model.setSearchSmallImage(smallImg);
		model.setHandSearch(true);
		model.setOutPlateNOEditable(true);
		model.setSearchCarpark(device.getCarpark());
	}
	
	/**
	 * 
	 * @param ip
	 * @param plateNO
	 * @param date
	 * @param device
	 * @param user
	 * @param roadType
	 * @param equals
	 * @param bigImg
	 * @param smallImg
	 * @return 返回true终止操作
	 */
	private boolean fixCarOutProcess(String bigImg,
			String smallImg) throws Exception {
		
		if (!StrUtil.isEmpty(user.getTempCarTime())) {
			tempCarOutProcess(ip, plateNO, device, date, bigImg, smallImg, StrUtil.parse(user.getTempCarTime().split(",")[0], StrUtil.DATETIME_PATTERN));
			model.setUser(user);
			return true;
		}
		String carType;
		carType = "固定车";
		if (CarparkUtils.checkRoadType(device, model, presenter, DeviceRoadTypeEnum.临时车通道,DeviceRoadTypeEnum.储值车通道)) {
			return true;
		}
		String nowPlateNO = plateNO;
		// 固定车出场确认
		Boolean valueOf = Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认) == null ? SystemSettingTypeEnum.固定车出场确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认));

		if (valueOf) {
			presenter.showContentToDevice(device, "固定车等待确认", false);
			model.setOutCheckClick(true);
			model.setOutPlateNOEditable(true);
			while (model.isOutCheckClick()) {
				int i = 0;
				try {
					if (i > 120) {
						return true;
					}
					Thread.sleep(500);
					i++;
				} catch (InterruptedException e) {
				}
			}
			nowPlateNO = model.getOutShowPlateNO();
			if (!nowPlateNO.equals(plateNO)) {
				user = sp.getCarparkUserService().findUserByPlateNo(nowPlateNO, device.getCarpark().getId());
				if (StrUtil.isEmpty(user)) {
					tempCarOutProcess(ip, nowPlateNO, device, date, bigImg, smallImg, null);
					return true;
				}
			}
		}
		//
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByNoCharge = carparkInOutService.findByNoOut(nowPlateNO, device.getCarpark());
		SingleCarparkInOutHistory singleCarparkInOutHistory = StrUtil.isEmpty(findByNoCharge) ? null : findByNoCharge.get(0);
		Date validTo = user.getValidTo();
		Integer delayDays = user.getDelayDays();

		Calendar c = Calendar.getInstance();
		c.setTime(validTo);
		c.add(Calendar.DATE, delayDays);
		Date time = c.getTime();

		if (StrUtil.getTodayBottomTime(time).before(date)) {
			LOGGER.info("车辆:{}已到期", nowPlateNO);
			if (Boolean.valueOf(getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期变临时车))) {
				Date d = null;
				if (StrUtil.isEmpty(singleCarparkInOutHistory) || singleCarparkInOutHistory.getInTime().before(validTo)) {
					d = validTo;
				} else {
					d = singleCarparkInOutHistory.getInTime();
				}
				tempCarOutProcess(ip, nowPlateNO, device, date, bigImg, smallImg, d);
				return true;
			} else if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期所属停车场限制).equals("true")) {
				if (device.getCarpark().equals(user.getCarpark())) {
					presenter.showContentToDevice(device, CarparkMainApp.CAR_IS_ARREARS + StrUtil.formatDate(user.getValidTo(), CarparkMainApp.VILIDTO_DATE), true);
				}
			}
			presenter.showContentToDevice(device, CarparkMainApp.CAR_IS_ARREARS + StrUtil.formatDate(user.getValidTo(), CarparkMainApp.VILIDTO_DATE), false);
			model.setOutShowPlateNO(model.getOutShowPlateNO()+"(已过期)");
			return true;
		} 
		
		boolean fixCarStillCharge = CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车非所属停车场停留收费).equals("true")&&!device.getCarpark().equals(user.getCarpark());
		model.setPlateNo(nowPlateNO);
		model.setCarType(carType);
		model.setOutTime(date);
		model.setShouldMony(0);
		model.setReal(0);
		// 未找到入场记录
		if (StrUtil.isEmpty(singleCarparkInOutHistory)) {
			singleCarparkInOutHistory=new SingleCarparkInOutHistory();
			model.setInTime(null);
			model.setTotalTime("未入场");
			if (fixCarStillCharge) {
				notFindInHistory(device, bigImg, smallImg);
				return true;
			}
		}else{
			Date inTime = singleCarparkInOutHistory.getInTime();
			model.setInTime(inTime);
			model.setTotalTime(StrUtil.MinusTime2(inTime, date));
		}
		singleCarparkInOutHistory.setPlateNo(nowPlateNO);
		singleCarparkInOutHistory.setOutPlateNO(plateNO);
		singleCarparkInOutHistory.setOutTime(date);
		singleCarparkInOutHistory.setOperaName(model.getUserName());
		singleCarparkInOutHistory.setOutDevice(device.getName());
		singleCarparkInOutHistory.setOutPhotographType("自动");
		singleCarparkInOutHistory.setCarType(carType);
		singleCarparkInOutHistory.setOutBigImg(bigImg);
		singleCarparkInOutHistory.setOutSmallImg(smallImg);
		singleCarparkInOutHistory.setUserId(user.getId());
		singleCarparkInOutHistory.setUserName(user.getName());
		Date handPhotographDate = mapHandPhotograph.get(ip);
		if (!StrUtil.isEmpty(handPhotographDate)) {
			DateTime plusSeconds = new DateTime(handPhotographDate).plusSeconds(3);
			boolean after = plusSeconds.toDate().after(date);
			if (after)
				singleCarparkInOutHistory.setOutPhotographType("手动");
		}
		if (fixCarStillCharge) {
			float shouldMoney=presenter.countFixCarShouldMoney(user,device,singleCarparkInOutHistory.getInTime(),date,plateNO);
			if (shouldMoney>0) {
				model.setShouldMony(shouldMoney);
				model.setReal(shouldMoney);
				presenter.showContentToDevice(device, "请缴费"+shouldMoney+"元", false);
				model.setChargeDevice(device);
				model.setChargeHistory(singleCarparkInOutHistory);
				model.setBtnClick(true);
				return true;
			}
		}
		
		c.setTime(validTo);
		c.add(Calendar.DATE, user.getRemindDays() == null ? 0 : user.getRemindDays() * -1);
		time = c.getTime();
		String content = model.getMapVoice().get(DeviceVoiceTypeEnum.固定车出场语音).getContent();
		
		if (StrUtil.getTodayBottomTime(time).before(date)) {
			presenter.showContentToDevice(device, content + ",剩余" + CarparkUtils.countDayByBetweenTime(date, user.getValidTo()) + "天", true);
			LOGGER.info("车辆:{}即将到期", nowPlateNO);
		} else {
			presenter.showContentToDevice(device, content, true);
		}
		carparkInOutService.saveInOutHistory(singleCarparkInOutHistory);
		sp.getCarparkInOutService().updateCarparkStillTime(device.getCarpark(), device, plateNO, bigImg);
		model.setBtnClick(false);
		return false;
	}

	/**
	 * @return
	 */
	private String getSettingValue(Map<SystemSettingTypeEnum,String> map,SystemSettingTypeEnum type) {
		return map.get(type)==null?type.getDefaultValue():mapSystemSetting.get(type);
	}

	private void tempCarOutProcess(final String ip, final String plateNO, SingleCarparkDevice device, Date date, String bigImg, String smallImg, Date reviseInTime) throws Exception{
		model.setPlateInTime(date, 30);
		if (!Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车通道限制))) {
			if (CarparkUtils.checkRoadType(device,model, presenter, DeviceRoadTypeEnum.储值车通道,DeviceRoadTypeEnum.固定车通道)) {
				return;
			}
		}
		Boolean isCharge = device.getCarpark().getIsCharge();
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByNoCharge = carparkInOutService.findByNoOut(plateNO, device.getCarpark());
		if (!StrUtil.isEmpty(findByNoCharge)) {//找到进场记录
			SingleCarparkInOutHistory singleCarparkInOutHistory = findByNoCharge.get(0);
			if (!StrUtil.isEmpty(reviseInTime)) {
				singleCarparkInOutHistory.setReviseInTime(reviseInTime);
			}else{
				singleCarparkInOutHistory.setReviseInTime(singleCarparkInOutHistory.getInTime());
			}
			singleCarparkInOutHistory.setOutTime(date);
			singleCarparkInOutHistory.setOperaName(model.getUserName());
			singleCarparkInOutHistory.setOutDevice(device.getName());
			singleCarparkInOutHistory.setOutPhotographType("自动");
			singleCarparkInOutHistory.setOutBigImg(bigImg);
			singleCarparkInOutHistory.setOutSmallImg(smallImg);
			//
			Date handPhotographDate = mapHandPhotograph.get(ip);
			if (!StrUtil.isEmpty(handPhotographDate)) {
				DateTime plusSeconds = new DateTime(handPhotographDate).plusSeconds(3);
				boolean after = plusSeconds.toDate().after(date);
				if (after)
					singleCarparkInOutHistory.setOutPhotographType("手动");
			}
			Date inTime = singleCarparkInOutHistory.getReviseInTime();

			
			// 临时车操作
			model.setPlateNo(plateNO);
			model.setCarType("临时车");
			model.setOutTime(date);
			model.setInTime(inTime);
			model.setTotalTime(StrUtil.MinusTime2(inTime, date));
			model.setHistory(singleCarparkInOutHistory);
			model.setShouldMony(0);
			model.setReal(0);
			model.setChargedMoney(0F);
			if (StrUtil.isEmpty(isCharge) || !isCharge) {
				sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
				presenter.showContentToDevice(device, CarparkMainApp.CAR_OUT_MSG, true);
			} else {
				CarTypeEnum carType = CarTypeEnum.SmallCar;
				if (mapTempCharge.keySet().size() > 1) {
					model.setComboCarTypeEnable(true);
					CarparkUtils.setFocus(carTypeSelectCombo);
					model.setSelectCarType(true);
					CarparkUtils.setComboSelect(carTypeSelectCombo, 0);
					Boolean autoSelectCarType = Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.自动识别出场车辆类型));
					if (!autoSelectCarType&&!model.isBtnClick()) {
						model.setChargeDevice(device);
						model.setChargeHistory(singleCarparkInOutHistory);
						model.setBtnClick(true);
						return;
//						try {
//							if (model.getDisContinue()) {
//								return;
//							}
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}
					//自动识别大车小车
					if (autoSelectCarType) {
						if (!StrUtil.isEmpty(model.getOutPlateNOColor())) {
							boolean b = !StrUtil.isEmpty(mapTempCharge.get("大车"));
							if (model.getOutPlateNOColor().equals("黄")&&b) {
								model.setCarparkCarType("大车");
								CarparkUtils.setComboSelect(carTypeSelectCombo, 1);
							}else{
								model.setCarparkCarType("小车");
								CarparkUtils.setComboSelect(carTypeSelectCombo, 2);
							}
						}
					}
					carType = getCarparkCarType(model.getCarparkCarType());
				} else if (mapTempCharge.keySet().size() == 1) {
					List<String> list = new ArrayList<>();
					list.addAll(mapTempCharge.keySet());
					carType = getCarparkCarType(list.get(0));
				}
				// model.setComboCarTypeEnable(false);
				float shouldMoney = presenter.countShouldMoney(device.getCarpark().getId(), carType, inTime, date);
				
				//集中收费
				Boolean idConcentrate=Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用集中收费));
				if (idConcentrate) {
					Float chargedMoney = singleCarparkInOutHistory.getFactMoney()==null?0:singleCarparkInOutHistory.getFactMoney();
					if (shouldMoney>0) {
						Date chargeTime = singleCarparkInOutHistory.getChargeTime();
						if (StrUtil.isEmpty(chargeTime)) {
							presenter.showContentToDevice(device, "请到管理处缴费", false);
							return;
						}
						Integer concentrateLateTime = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.集中收费延迟出场时间));
						DateTime plusMinutes = new DateTime(chargeTime).plusMinutes(concentrateLateTime);
						if (plusMinutes.isBeforeNow()) {
							if (shouldMoney>chargedMoney) {
								String content = "应缴费"+shouldMoney+"元,已缴费"+chargedMoney+"元,请到管理处续费";
								presenter.showContentToDevice(device, CarparkUtils.formatFloatString(content), false);
								return;
							}
						}
					}
					singleCarparkInOutHistory.setOutTime(date);
					singleCarparkInOutHistory.setBigImg(bigImg);
					singleCarparkInOutHistory.setSmallImg(smallImg);
					model.setShouldMony(shouldMoney);
					singleCarparkInOutHistory.setShouldMoney(shouldMoney);
					model.setChargedMoney(chargedMoney);
					model.setReal(shouldMoney-chargedMoney);
				}else{
					model.setShouldMony(shouldMoney);
					singleCarparkInOutHistory.setShouldMoney(shouldMoney);
					model.setReal(shouldMoney);
				}
				model.setCartypeEnum(carType);
				LOGGER.info("等待收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", plateNO, device.getCarpark(), carType, model.getInTime(), model.getOutTime(), model.getTotalTime(), shouldMoney);
				String s = "请缴费" + shouldMoney + "元";
				s = CarparkUtils.getCarStillTime(model.getTotalTime())+CarparkUtils.formatFloatString(s);

				String property = System.getProperty(CarparkMainApp.TEMP_CAR_AUTO_PASS);
				Boolean valueOf = Boolean.valueOf(property);
				// 临时车零收费是否自动出场
				Boolean tempCarNoChargeIsPass = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车零收费是否自动出场));
				model.setBtnClick(true);
				LOGGER.info("等待收费");
				if (tempCarNoChargeIsPass) {
					if (model.getReal() > 0) {
						presenter.showContentToDevice(device, s, false);
						model.setChargeDevice(device);
						model.setChargeHistory(singleCarparkInOutHistory);
					} else {
						presenter.chargeCarPass(device, singleCarparkInOutHistory, false);
					}
				} else {
					presenter.showContentToDevice(device, s, false);
					model.setChargeDevice(device);
					model.setChargeHistory(singleCarparkInOutHistory);
				}
				if (valueOf) {
					singleCarparkInOutHistory.setFactMoney(shouldMoney);
					presenter.chargeCarPass(device, singleCarparkInOutHistory, false);
				}
			}
		}else{
			LOGGER.info("车辆{}未入场且停车场是否收费设置为{}",plateNO,isCharge);
			if (!isCharge) {
				SingleCarparkInOutHistory io=new SingleCarparkInOutHistory();
				io.setPlateNo(plateNO);
				io.setOutBigImg(bigImg);
				io.setOutSmallImg(smallImg);
				io.setOutTime(date);
				io.setFactMoney(0);
				io.setShouldMoney(0);
				io.setFreeMoney(0);
				io.setCarparkId(device.getCarpark().getId());
				io.setCarparkName(device.getCarpark().getName());
				io.setCarType("临时车");
				io.setOutDevice(device.getName());
				io.setOperaName(System.getProperty("userName"));
				model.setPlateNo(plateNO);
				model.setOutTime(date);
				model.setCarType("临时车");
				model.setTotalTime("未入场");
				sp.getCarparkInOutService().saveInOutHistory(io);
				LOGGER.info("保存车辆{}的出场记录成功",plateNO);
				presenter.showPlateNOToDevice(device, plateNO);
				presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
			}else{
				notFindInHistory(device, bigImg, smallImg);
			}
		}
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
}
