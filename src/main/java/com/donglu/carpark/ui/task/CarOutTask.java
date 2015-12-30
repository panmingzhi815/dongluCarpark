package com.donglu.carpark.ui.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
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
	private final Shell shell;
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

	private long startTime=System.currentTimeMillis();
	
	public static Map<String, String> mapTempCharge=CarparkMainApp.mapTempCharge;
	
	public CarOutTask(String ip, String plateNO, byte[] bigImage, byte[] smallImage,CarparkMainModel model,
			CarparkDatabaseServiceProvider sp, CarparkMainPresenter presenter, CLabel lbl_outBigImg,
			CLabel lbl_outSmallImg,CLabel lbl_inBigImg, Combo carTypeSelectCombo,
			Text text_real,Shell shell,Float rightSize) {
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
		this.shell = shell;
		this.rightSize=rightSize;
		this.lbl_inBigImg=lbl_inBigImg;
	}
	public static void main(String[] args) {
		try {
			String s="东陆高新";
			System.out.println(CarparkUtils.encod(s));
			System.out.println(CarparkUtils.decod(CarparkUtils.encod(s)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void run(){
		try {
			SingleCarparkDevice device = mapIpToDevice.get(ip);
			
			// 双摄像头等待
			twoChanelControl(device);
			
			
			model.setDisContinue(false);
			model.setHandSearch(false);
			long nanoTime = System.nanoTime();
			Date date = new Date();
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
				public void run() {
					if (StrUtil.isEmpty(lbl_outSmallImg)) {
						return;
					}
					CarparkUtils.setBackgroundImage(bigImage, lbl_outBigImg, DEFAULT_DISPLAY);
					CarparkUtils.setBackgroundImage(smallImage, lbl_outSmallImg, DEFAULT_DISPLAY);

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
			SingleCarparkCarpark carpark = sp.getCarparkService().findCarparkById(device.getCarpark().getId());
			
			if (StrUtil.isEmpty(carpark)) {
				LOGGER.info("没有找到名字为：" + carpark + "的停车场");
				return;
			}
			model.setIp(ip);
			String bigImg = folder + "/" + bigImgFileName;
			String smallImg = folder + "/" + smallImgFileName;
			//
			if (StrUtil.isEmpty(plateNO)) {
				LOGGER.error("空的车牌");
				model.setSearchPlateNo(plateNO);
				model.setSearchBigImage(bigImg);
				model.setSearchSmallImage(smallImg);
				model.setHandSearch(true);
				model.setOutPlateNOEditable(true);
				return;
			}
			SingleCarparkUser user = sp.getCarparkUserService().findUserByPlateNo(plateNO,device.getCarpark().getId());
			// 没有找到入场记录
			List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(plateNO,carpark);
			if (StrUtil.isEmpty(user)) {
				if (StrUtil.isEmpty(findByNoOut)) {
					notFindInHistory(device, bigImg, smallImg);
					return;
				}
			}
			SingleCarparkInOutHistory ch = StrUtil.isEmpty(findByNoOut)?null:findByNoOut.get(0);
			LOGGER.info("车辆出场显示进口图片");
			DEFAULT_DISPLAY.asyncExec(new Runnable() {
				public void run() {
					if (StrUtil.isEmpty(ch)||StrUtil.isEmpty(lbl_inBigImg)) {
						return;
					}
					CarparkUtils.setBackgroundImage(CarparkUtils.getImageByte(ch.getBigImg()), lbl_inBigImg, DEFAULT_DISPLAY);
				}
			});
			
			

			presenter.showPlateNOToDevice(device, plateNO);
			//
			long nanoTime3 = System.nanoTime();
			String carType = "临时车";
			
			if (!StrUtil.isEmpty(user)) {
				Date userOutTime = new DateTime(user.getValidTo()).plusDays(user.getDelayDays()==null?0:user.getDelayDays()).toDate();
				if (userOutTime.after(date)) {
					carType="固定车";
				}
			}
			String roadType = device.getRoadType();
			LOGGER.info("车辆类型为：{}==通道类型为：{}", carType, roadType);
			// System.out.println("=====车辆类型为："+carType+"通道类型为："+roadType);
			long nanoTime2 = System.nanoTime();
			LOGGER.info(dateString + "==" + ip + "==" + device.getInType() + "==" + plateNO + "车辆类型：" + carType + "" + "保存图片：" + (nanoTime1 - nanoTime) + "==查找固定用户：" + (nanoTime2 - nanoTime3)
					+ "==界面操作：" + (nanoTime3 - nanoTime1));
			boolean equals = roadType.equals(DeviceRoadTypeEnum.固定车通道.name());

			if (carType.equals("固定车")) {
				if (fixCarOutProcess(ip, plateNO, date, device, user, roadType, equals, bigImg, smallImg)) {
					return;
				}
			} else {// 临时车操作
				// 固定车通道
				if (equals) {
					presenter.showContentToDevice(device, CarparkMainApp.FIX_ROAD, false);
					return;
				}
				tempCarOutProcess(ip, plateNO, device, date, bigImg, smallImg,null);
			}
		} catch (Exception e) {
			LOGGER.error("车辆出场时发生错误",e);
		}
	
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
	private boolean fixCarOutProcess(final String ip, final String plateNO, Date date, SingleCarparkDevice device, SingleCarparkUser user, String roadType, boolean equals, String bigImg,
			String smallImg) throws Exception {
		if (!StrUtil.isEmpty(user.getTempCarTime())) {
			tempCarOutProcess(ip, plateNO, device, date, bigImg, smallImg,StrUtil.parse(user.getTempCarTime().split(",")[0], StrUtil.DATETIME_PATTERN));
			model.setUser(user);
			return true;
		}
		String carType;
		carType = "固定车";
		if (!equals) {
			if (roadType.equals(DeviceRoadTypeEnum.临时车通道.name())) {
				presenter.showContentToDevice(device, CarparkMainApp.TEMP_ROAD, false);
				return true;
			}
		}
		String nowPlateNO = plateNO;
		// 固定车出场确认
		Boolean valueOf = Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认) == null ? SystemSettingTypeEnum.固定车出场确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认));

		if (valueOf) {
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
				SingleCarparkUser findUserByPlateNo = sp.getCarparkUserService().findUserByPlateNo(nowPlateNO, device.getCarpark().getId());
				if (StrUtil.isEmpty(findUserByPlateNo)) {
					tempCarOutProcess(ip, nowPlateNO, device, date, bigImg, smallImg,null);
					return true;
				}
			}
		}
		//
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByNoCharge = carparkInOutService.findByNoOut(nowPlateNO, device.getCarpark());
		SingleCarparkInOutHistory singleCarparkInOutHistory =StrUtil.isEmpty(findByNoCharge)?null: findByNoCharge.get(0);
		Date validTo = user.getValidTo();
		Integer delayDays = user.getDelayDays() == null ? 0 : user.getDelayDays();

		Calendar c = Calendar.getInstance();
		c.setTime(validTo);
		c.add(Calendar.DATE, delayDays);
		Date time = c.getTime();

		if (StrUtil.getTodayBottomTime(time).before(date)) {
			presenter.showContentToDevice(device, CarparkMainApp.CAR_IS_ARREARS + StrUtil.formatDate(user.getValidTo(), CarparkMainApp.VILIDTO_DATE), false);
			LOGGER.info("车辆:{}已到期", nowPlateNO);
			if (Boolean.valueOf(getSettingValue(mapSystemSetting,SystemSettingTypeEnum.固定车到期变临时车))) {
				Date d=null;
				if (StrUtil.isEmpty(singleCarparkInOutHistory)||singleCarparkInOutHistory.getInTime().before(validTo)) {
					d=validTo;
				}else{
					d=singleCarparkInOutHistory.getInTime();
				}
				tempCarOutProcess(ip, nowPlateNO, device, date, bigImg, smallImg,d);
			}
			return true;
		} else {
			c.setTime(validTo);
			c.add(Calendar.DATE, user.getRemindDays() == null ? 0 : user.getRemindDays() * -1);
			time = c.getTime();
			if (StrUtil.getTodayBottomTime(time).before(date)) {
				presenter.showContentToDevice(device, "月租车辆,"+CarparkMainApp.CAR_OUT_MSG + ",剩余"+CarparkUtils.countDayByBetweenTime(date, user.getValidTo())+"天", true);
				LOGGER.info("车辆:{}即将到期", nowPlateNO);
			} else {
				presenter.showContentToDevice(device, "月租车辆,"+CarparkMainApp.CAR_OUT_MSG, true);
			}
		}

		model.setPlateNo(nowPlateNO);
		model.setCarType(carType);
		model.setOutTime(date);
		model.setShouldMony(0);
		model.setInTime(null);
		model.setTotalTime("未入场");
		model.setReal(0);
		//未找到入场记录
		if (!StrUtil.isEmpty(singleCarparkInOutHistory)) {
			Date inTime = singleCarparkInOutHistory.getInTime();
			model.setInTime(inTime);
			model.setTotalTime(StrUtil.MinusTime2(inTime, date));
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
			carparkInOutService.saveInOutHistory(singleCarparkInOutHistory);
		}
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
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkInOutHistory> findByNoCharge = carparkInOutService.findByNoOut(plateNO, device.getCarpark());
		if (!StrUtil.isEmpty(findByNoCharge)) {

			SingleCarparkInOutHistory singleCarparkInOutHistory = findByNoCharge.get(0);
			if (!StrUtil.isEmpty(reviseInTime)) {
				singleCarparkInOutHistory.setReviseInTime(reviseInTime);
			}else{
				singleCarparkInOutHistory.setReviseInTime(singleCarparkInOutHistory.getInTime());
			}
//			List<SingleCarparkInOutHistory> listChildCarparkInOutHistory= carparkInOutService.findHistoryByChildCarparkInOut(singleCarparkInOutHistory.getCarparkId(),plateNO,singleCarparkInOutHistory.getInTime(),date);
//			if (StrUtil.isEmpty(listChildCarparkInOutHistory)) {
//				Map<Long, SingleCarparkInOutHistory> map=new HashMap<>();
//				for (SingleCarparkInOutHistory singleCarparkInOutHistory2 : listChildCarparkInOutHistory) {
//					map.put(singleCarparkInOutHistory2.getId(), singleCarparkInOutHistory2);
//				}
//				model.setChildCarparkInOut(map);
//			}
		
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

			Boolean isCharge = device.getCarpark().getIsCharge();
			if (StrUtil.isEmpty(isCharge) || !isCharge) {
				sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
				presenter.showContentToDevice(device, CarparkMainApp.CAR_OUT_MSG, true);
			} else {
				CarTypeEnum carType = CarTypeEnum.SmallCar;
				if (mapTempCharge.keySet().size() > 1) {
					model.setComboCarTypeEnable(true);
					CarparkUtils.setFocus(carTypeSelectCombo);
					model.setSelectCarType(true);
					CarparkUtils.setComboSelect(carTypeSelectCombo, 2);
					Boolean autoSelectCarType = Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.自动识别出场车辆类型));
					while (!autoSelectCarType&&!model.isBtnClick()) {
						try {
							if (model.getDisContinue()) {
								return;
							}
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
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
				model.setShouldMony(shouldMoney);
				singleCarparkInOutHistory.setShouldMoney(shouldMoney);
				model.setReal(shouldMoney);
				singleCarparkInOutHistory.setFactMoney(shouldMoney);
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
					if (shouldMoney > 0) {
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
			notFindInHistory(device, bigImg, smallImg);
		}
	}
	/**
	 * 双摄像头控制
	 * @param device
	 */
	private void twoChanelControl(SingleCarparkDevice device) {
		
		String key = device.getLinkAddress()+device.getAddress();
		Boolean isTwoChanel = CarparkMainApp.mapIsTwoChanel.get(key) == null ? false : CarparkMainApp.mapIsTwoChanel.get(key);
		LOGGER.info("控制器{}双摄像头设置为{},等待间隔为{}",key,isTwoChanel);
		if (isTwoChanel) {
			try {
				Integer two = Integer.valueOf(
						mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔) == null ? SystemSettingTypeEnum.双摄像头识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔));
				long l = System.currentTimeMillis() - startTime;
				LOGGER.info("双摄像头等待时间{},已过{}",two,l);
				while (l < two) {
					Thread.sleep(100);
					l = System.currentTimeMillis() - startTime;
				}
				LOGGER.info("双摄像头等待时间{},已过{}",two,l);
				CarparkMainApp.mapInTwoCameraTask.remove(key);
			} catch (Exception e1) {
				LOGGER.error("双摄像头出错",e1);
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
	
	public void alreadyFinshWait(){
		this.startTime=0l;
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
