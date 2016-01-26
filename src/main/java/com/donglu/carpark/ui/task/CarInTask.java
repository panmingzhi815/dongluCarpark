package com.donglu.carpark.ui.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainApp;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

public class CarInTask implements Runnable {
	private static final String SLOT_IS_FULL = "车位已满";
	private static final Display DEFAULT_DISPLAY = Display.getDefault();
	private static Logger LOGGER = LoggerFactory.getLogger(CarInTask.class);
	private static final String CAR_IN_MSG = "欢迎光临,请入场停车";
	static Image inSmallImage;
	static Image inBigImage;

	private String plateNO;
	private String ip;
	private final CarparkMainModel model;
	private final CarparkDatabaseServiceProvider sp;
	private final CarparkMainPresenter presenter;
	private byte[] bigImage;
	private byte[] smallImage;
	// 保存车牌最近的处理时间
	private final Map<String, Date> mapPlateNoDate = CarparkMainApp.mapPlateNoDate;
	// 保存设备的信息
	private final Map<String, SingleCarparkDevice> mapIpToDevice = CarparkMainApp.mapIpToDevice;
	// 保存设置信息
	private final Map<SystemSettingTypeEnum, String> mapSystemSetting = CarparkMainApp.mapSystemSetting;
	// 保存最近的手动拍照时间
	private final Map<String, Date> mapHandPhotograph = CarparkMainApp.mapHandPhotograph;


	private Float rightSize;

	private long startTime = System.currentTimeMillis();

	private final CLabel lbl_inSmallImg;

	private final CLabel lbl_inBigImg;

	public CarInTask(String ip, String plateNO, byte[] bigImage, byte[] smallImage, CarparkMainModel model, CarparkDatabaseServiceProvider sp, CarparkMainPresenter presenter,
			Float rightSize, CLabel lbl_inSmallImg, CLabel lbl_inBigImg) {
		super();
		this.ip = ip;
		this.plateNO = plateNO;
		this.bigImage = bigImage;
		this.smallImage = smallImage;
		this.model = model;
		this.sp = sp;
		this.presenter = presenter;
		this.rightSize = rightSize;
		this.lbl_inSmallImg = lbl_inSmallImg;
		this.lbl_inBigImg = lbl_inBigImg;
	}

	@Override
	public void run() {
		model.setInCheckClick(false);
		try {
			SingleCarparkDevice device = mapIpToDevice.get(ip);
			// 双摄像头等待
			twoChanelControl(device);

			Date date = new Date();
			boolean checkPlateNODiscernGap = presenter.checkPlateNODiscernGap(mapPlateNoDate, plateNO, date);
			if (!checkPlateNODiscernGap) {
				return;
			}
			

			String dateString = StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
			model.setInShowPlateNO(plateNO);
			model.setInShowTime(dateString);

			long nanoTime1 = System.nanoTime();

			LOGGER.info(dateString + "==" + ip + "====" + plateNO);

			if (StrUtil.isEmpty(device)) {
				LOGGER.error("没有找到ip:" + ip + "的设备");
				return;
			}
			SingleCarparkCarpark carpark = sp.getCarparkService().findCarparkById(device.getCarpark().getId());
			if (StrUtil.isEmpty(carpark)) {
				LOGGER.error("没有找到id:" + device.getCarpark().getId() + "的停车场");
				return;
			}
			LOGGER.debug("开始在界面显示车牌：{}的抓拍图片", plateNO);
			// 界面图片
			DEFAULT_DISPLAY.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (StrUtil.isEmpty(lbl_inSmallImg)) {
						return;
					}
					CarparkUtils.setBackgroundImage(smallImage, lbl_inSmallImg, DEFAULT_DISPLAY);
					CarparkUtils.setBackgroundImage(bigImage, lbl_inBigImg, DEFAULT_DISPLAY);
				}
			});
			model.setInShowPlateNO(plateNO);
			model.setInShowTime(dateString);
			
			String editPlateNo = null;
			boolean isEmptyPlateNo = false;
			// 空车牌处理
			if (StrUtil.isEmpty(plateNO)) {
				LOGGER.info("空的车牌");
				Boolean valueOf = Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.是否允许无牌车进));
				if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认)) || Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认)) || !valueOf) {
					model.setInCheckClick(true);
					isEmptyPlateNo = true;
					int i=0;
					while (model.isInCheckClick()) {
						try {
							i++;
							if (i>120) {
								return;
							}
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					editPlateNo = model.getInShowPlateNO();
					if (!model.isInCheckIsClick()||StrUtil.isEmpty(editPlateNo)) {
						return;
					}
					model.setInCheckIsClick(false);
				} else {
					if (!valueOf) {
						return;
					}
				}
			}
			SingleCarparkInOutHistory cch = sp.getCarparkInOutService().findInOutHistoryByPlateNO(plateNO);
			if (StrUtil.isEmpty(cch)) {
				cch = new SingleCarparkInOutHistory();
			}
			
			
			
			LOGGER.debug("开始保存车牌：{}的图片", plateNO);
			long nanoTime = System.nanoTime();

			mapPlateNoDate.put(plateNO, date);
			String folder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
			String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
			String bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
			String smallImgFileName = fileName + "_" + plateNO + "_small.jpg";
			presenter.saveImage(folder, smallImgFileName,bigImgFileName,smallImage, bigImage);

			// model.setInShowBigImg(inBigImage);
			// model.setInShowSmallImg(inSmallImage);
			// model.setInShowPlateNO(plateNO);
			// model.setInShowTime(dateString);
			// plateNoTotal.addAndGet(1);

			long nanoTime3 = System.nanoTime();
			LOGGER.debug("进行黑名单判断");
			// SingleCarparkBlackUser singleCarparkBlackUser = mapBlackUser.get(plateNO);
			LOGGER.debug("显示车牌");
			presenter.showPlateNOToDevice(device, plateNO);
			SingleCarparkBlackUser blackUser = sp.getCarparkService().findBlackUserByPlateNO(plateNO);
			
			//黑名单判断
			if (!StrUtil.isEmpty(blackUser)) {
				Holiday findHolidayByDate = sp.getCarparkService().findHolidayByDate(new Date());
				if (!StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(blackUser.getHolidayIn()) && blackUser.getHolidayIn()) {
					model.setInShowMeg("黑名单");
					presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
					return;
				}
				if (StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(blackUser.getWeekDayIn()) && blackUser.getWeekDayIn()) {
					model.setInShowMeg("黑名单");
					presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
					return;
				}

				int hoursStart = blackUser.getHoursStart();
				int hoursEnd = blackUser.getHoursEnd() == 0 ? 23 : blackUser.getHoursEnd();
				int minuteStart = blackUser.getMinuteStart();
				int minuteEnd = blackUser.getMinuteEnd();
				DateTime now = new DateTime(date);
				DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursStart, minuteStart, 00);
				DateTime de = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursEnd, minuteEnd, 59);
				if (blackUser.getTimeIn()) {
					LOGGER.info("黑名单车牌：{}允许进入的时间为{}点到{}点", plateNO, hoursStart, hoursEnd);
					if (now.isBefore(dt.getMillis()) || now.isAfter(de.getMillis())) {
						model.setInShowMeg("黑名单");
						presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
						return;
					}

				} else {
					LOGGER.info("黑名单车牌：{}不能进入的时间为{}点到{}点", plateNO, hoursStart, hoursEnd);
					if (now.toDate().after(dt.toDate()) && now.toDate().before(de.toDate())) {
						LOGGER.error("车牌：{}为黑名单,现在时间为{}，在{}点到{}点之间", plateNO, now.toString("HH:mm:ss"), hoursStart, hoursEnd);
						model.setInShowMeg("黑名单");
						presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
						return;
					}
				}
			}

			model.setHistory(cch);
			LOGGER.debug("查找是否为固定车");
			SingleCarparkUser user = sp.getCarparkUserService().findUserByPlateNo(plateNO, device.getCarpark().getId());

			String carType = "临时车";

			if (!StrUtil.isEmpty(user)) {//固定车操作
				//固定车入场确认
				boolean flag = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认));
				if (!isEmptyPlateNo) {
					if (flag) {
						model.setInCheckClick(true);
						presenter.showContentToDevice(device, "固定车等待确认", false);
						int i = 0;
						while (model.isInCheckClick()) {
							if (i > 120) {
								return;
							}
							try {
								Thread.sleep(500);
								i++;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						editPlateNo = model.getInShowPlateNO();
						presenter.showPlateNOToDevice(device, editPlateNo);
						if (!editPlateNo.equals(plateNO)) {
							user = sp.getCarparkUserService().findUserByPlateNo(editPlateNo, device.getCarpark().getId());
						}
					}
				}
				//非储值车
				if (!user.getType().equals("储值")) {
					if (flag) {
						if (StrUtil.isEmpty(user)) {
							LOGGER.debug("判断是否允许临时车进");
							if (device.getCarpark().isTempCarIsIn()) {
								presenter.showContentToDevice(device, "固定停车场,不允许临时车进", false);
								return;
							}
							if (shouTempCarToDevice(device)) {
								return;
							}
						} else {
							if (fixCarShowToDevice(date, device, user, cch.getId())) {
								return;
							}
							carType = "固定车";
						}
					} else {
						if (fixCarShowToDevice(date, device, user, cch.getId())) {
							return;
						}
						carType = "固定车";
					} 
				}else{//储值车
					if(prepaidCarIn(device, user)){
						return;
					}
				}
			} else {
				
				boolean flag = false; //临时车是否确认
				if (!isEmptyPlateNo) {
					if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认))) {
						flag = true;
						model.setInCheckClick(true);
						presenter.showContentToDevice(device, "临时车等待确认", false);
						int i=0;
						while (model.isInCheckClick()) {
							try {
								if (i>120) {
									model.setInCheckClick(false);
									return;
								}
								Thread.sleep(500);
							} catch (InterruptedException e) {
								LOGGER.error("临时车入场是否确认发生错误",e);
							}finally{
								i++;
							}
						}
						editPlateNo=model.getInShowPlateNO();
						if (StrUtil.isEmpty(editPlateNo)||!model.isInCheckIsClick()) {
							return;
						}
						model.setInCheckIsClick(false);
						presenter.showPlateNOToDevice(device,editPlateNo);
						if (!editPlateNo.equals(plateNO)) {
							cch=sp.getCarparkInOutService().findInOutHistoryByPlateNO(editPlateNo);
							if (StrUtil.isEmpty(cch)) {
								cch = new SingleCarparkInOutHistory();
							}
						}
					}
				}
				if (flag&&!editPlateNo.equals(plateNO)) {
					user = sp.getCarparkUserService().findUserByPlateNo(editPlateNo, device.getCarpark().getId());
					if (StrUtil.isEmpty(user)) {
						
						if (shouTempCarToDevice(device)) {
							return;
						}
					} else {
						if (!user.getType().equals("储值")) {
							if (fixCarShowToDevice(date, device, user, cch.getId())) {
								return;
							}
						}else{
							if (prepaidCarIn(device, user)) {
								return;
							}
						}
					}
				} else {
					LOGGER.debug("判断是否允许临时车进");
					if (device.getCarpark().isTempCarIsIn()) {
						presenter.showContentToDevice(device, "固定停车场,不允许临时车进", false);
						return;
					}
					if (shouTempCarToDevice(device)) {
						return;
					}
				}
			}
			LOGGER.info("车辆类型为：{}==t通道类型为：{}", carType, device.getRoadType());
			// showInDevice(device, plateNO, user);
			long nanoTime2 = System.nanoTime();
			LOGGER.info(dateString + "==" + ip + "====" + plateNO + "车辆类型：" + carType + "==" + "保存图片：" + (nanoTime1 - nanoTime) + "==查找固定用户：" + (nanoTime2 - nanoTime3) + "==界面操作："
					+ (nanoTime3 - nanoTime1));
			LOGGER.info("把车牌:{}的进场记录保存到数据库", plateNO);
			cch.setPlateNo(plateNO);
			cch.setInPlateNO(plateNO);
			if (!StrUtil.isEmpty(editPlateNo)) {
				cch.setPlateNo(editPlateNo);
			}
			cch.setInTime(date);
			cch.setOperaName(System.getProperty("userName"));
			cch.setBigImg(folder + "/" + bigImgFileName);
			cch.setSmallImg(folder + "/" + smallImgFileName);
			cch.setCarType(carType);
			cch.setCarparkId(carpark.getId());
			cch.setCarparkName(carpark.getName());
			if (!StrUtil.isEmpty(user)) {
				cch.setUserName(user.getName());
				cch.setUserId(user.getId());
			}
			cch.setInDevice(device.getName());
			cch.setInPhotographType("自动");
			Date handPhotographDate = mapHandPhotograph.get(ip);
			if (!StrUtil.isEmpty(handPhotographDate)) {
				DateTime plusSeconds = new DateTime(handPhotographDate).plusSeconds(3);
				boolean after = plusSeconds.toDate().after(date);
				if (after)
					cch.setInPhotographType("手动");
			}
			Long saveInOutHistory = sp.getCarparkInOutService().saveInOutHistory(cch);
			cch.setId(saveInOutHistory);
			model.addInHistorys(cch);
			model.setInHistorySelect(cch);
			LOGGER.info("保存车牌：{}的进场记录到数据库成功", plateNO);
			model.setHistory(null);
		} catch (Exception e) {
			LOGGER.error("车辆进场时发生错误，", e);
		}

	}

	/**
	 * @param device
	 * @param user
	 * @return 
	 */
	public boolean prepaidCarIn(SingleCarparkDevice device, SingleCarparkUser user) {
		if (CarparkUtils.checkRoadType(device, presenter, DeviceRoadTypeEnum.临时车通道,DeviceRoadTypeEnum.固定车通道)) {
			return true;
		}
		Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许储值车入场));
		if (!valueOf) {
			if (model.getTotalSlot() <= 0) {
				presenter.showContentToDevice(device, SLOT_IS_FULL, false);
				LOGGER.error("车位已满,不允许储值车进入");
				return true;
			}
		}
		Float leftMoney = user.getLeftMoney();
		Float prepaidCarInOutLimit=Float.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.储值车进出场限制金额));
		if (leftMoney<prepaidCarInOutLimit) {
			String formatFloatString = CarparkUtils.formatFloatString("剩余"+leftMoney+"元,余额不足请联系管理员");
			presenter.showContentToDevice(device,formatFloatString, false);
			return true;
		}
		
		Float prepaidCarInOutRemind=Float.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.储值车提醒金额));;
		if (leftMoney<prepaidCarInOutRemind) {
			String content = CAR_IN_MSG+",剩余"+leftMoney+"元,请及时充值";
			content=CarparkUtils.formatFloatString(content);
			presenter.showContentToDevice(device, content, true);
		}
		if (leftMoney>100) {
			String content = "储值车辆,"+CAR_IN_MSG;
			presenter.showContentToDevice(device, content, true);
		}
		return false;
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

	/**
	 * @param device
	 * @return
	 */
	public boolean shouTempCarToDevice(SingleCarparkDevice device) throws Exception {
		Boolean valueOf2 = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车通道限制));
		if (!valueOf2) {
			if (CarparkUtils.checkRoadType(device, presenter, DeviceRoadTypeEnum.固定车通道,DeviceRoadTypeEnum.储值车通道)) {
				return true;
			}
		}
		Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许临时车入场));
		if (!valueOf) {
			if (model.getTotalSlot() <= 0) {
				presenter.showContentToDevice(device, SLOT_IS_FULL, false);
				LOGGER.error("车位已满,不允许临时车进入");
				return true;
			}
		}
		presenter.showContentToDevice(device, CAR_IN_MSG, true);
		return false;
	}

	/**
	 * @param date
	 * @param device
	 * @param user
	 * @return 是否需要退出 true退出
	 */
	public boolean fixCarShowToDevice(Date date, SingleCarparkDevice device, SingleCarparkUser user, Long inId) throws Exception {
		if (CarparkUtils.checkRoadType(device,presenter,DeviceRoadTypeEnum.储值车通道,DeviceRoadTypeEnum.临时车通道)) {
			return true;
		}
		if (StrUtil.isEmpty(inId)) {
			Boolean valueOf2 = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车车位满作临时车计费) == null ? "false" : mapSystemSetting.get(SystemSettingTypeEnum.固定车车位满作临时车计费));
			List<SingleCarparkInOutHistory> list = new ArrayList<>();
			String[] plateNos = user.getPlateNo().split(",");
			for (String pn : plateNos) {
				List<SingleCarparkInOutHistory> findHistoryByChildCarparkInOut = sp.getCarparkInOutService().findInOutHistoryByCarparkAndPlateNO(user.getCarpark().getId(), pn);
				list.addAll(findHistoryByChildCarparkInOut);
			}
			if (valueOf2) {
				if (Integer.valueOf(user.getCarparkNo()) <= list.size()) {
					setFixCarToTemIn(date, user);
					LOGGER.info("固定车车位满作临时车计费设置为{}，用户车位为{}，场内车辆为{}，作临时车进入", valueOf2, user.getCarparkNo(), list.size());
				}
			} else {
				if (Integer.valueOf(user.getCarparkNo()) <= list.size()) {
					LOGGER.info("固定车车位满作临时车计费设置为{}，用户车位为{}，场内车辆为{}，不允许进入", valueOf2, user.getCarparkNo(), list.size());
					return true;
				}
			}
		}
		Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许免费车入场));
			if (!valueOf) {
				if (model.getTotalSlot() <= 0) {
					presenter.showContentToDevice(device, SLOT_IS_FULL, false);
					LOGGER.error("车位已满,不允许免费车进入");
					return true;
				}
			}

		// int parseInt = Integer.parseInt(StrUtil.isEmpty(user.getCarparkNo())?"0":user.getCarparkNo());

		Date date2 = new DateTime(user.getValidTo()).minusDays(user.getRemindDays() == null ? 0 : user.getRemindDays()).toDate();
		if (StrUtil.getTodayBottomTime(date2).before(date)) {
			String content = "月租车辆," + CAR_IN_MSG + ",剩余" + CarparkUtils.countDayByBetweenTime(date, user.getValidTo()) + "天";
			presenter.showContentToDevice(device, content, true);
			LOGGER.info("固定车：{}，{}", plateNO, content);
		} else {
			String content = "月租车辆," + CAR_IN_MSG;
			presenter.showContentToDevice(device, content, true);
			LOGGER.info("固定车：{}，{}", plateNO, content);
		}
		return false;
	}

	/**
	 * @param date
	 * @param user
	 */
	private synchronized void setFixCarToTemIn(Date date, SingleCarparkUser user) {
		user.setTempCarTime((StrUtil.isEmpty(user.getTempCarTime()) ? "" : user.getTempCarTime() + ",") + StrUtil.formatDate(date, StrUtil.DATETIME_PATTERN));
		sp.getCarparkUserService().saveUser(user);
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setRightSize(Float rightSize) {
		this.rightSize = rightSize;
	}

	public void alreadyFinshWait() {
		this.startTime = 0l;
	}
}
