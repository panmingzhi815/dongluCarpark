package com.donglu.carpark.ui.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainApp;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.collect.Maps;

public class CarInTask implements Runnable {
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
	private final Shell shell;
	// 保存车牌最近的处理时间
	private final Map<String, Date> mapPlateNoDate = CarparkMainApp.mapPlateNoDate;
	// 保存设备的信息
	private final Map<String, SingleCarparkDevice> mapIpToDevice = CarparkMainApp.mapIpToDevice;
	// 保存设置信息
	private final Map<SystemSettingTypeEnum, String> mapSystemSetting = CarparkMainApp.mapSystemSetting;
	// 保存最近的手动拍照时间
	private final Map<String, Date> mapHandPhotograph = CarparkMainApp.mapHandPhotograph;

	private final Map<String, Boolean> mapOpenDoor = CarparkMainApp.mapOpenDoor;

	private Float rightSize;

	private long startTime = System.currentTimeMillis();

	private final CLabel lbl_inSmallImg;

	private final CLabel lbl_inBigImg;

	public CarInTask(String ip, String plateNO, byte[] bigImage, byte[] smallImage, CarparkMainModel model, CarparkDatabaseServiceProvider sp, CarparkMainPresenter presenter, Shell shell,
			Float rightSize, CLabel lbl_inSmallImg, CLabel lbl_inBigImg) {
		super();
		this.ip = ip;
		this.plateNO = plateNO;
		this.bigImage = bigImage;
		this.smallImage = smallImage;
		this.model = model;
		this.sp = sp;
		this.presenter = presenter;
		this.shell = shell;
		this.rightSize = rightSize;
		this.lbl_inSmallImg = lbl_inSmallImg;
		this.lbl_inBigImg = lbl_inBigImg;
	}

	public void run() {
		model.setInCheckClick(false);
		try {
			SingleCarparkDevice device = mapIpToDevice.get(ip);
			// 开闸
			Boolean boolean1 = mapOpenDoor.get(ip);
			if (boolean1 != null && boolean1) {
				mapOpenDoor.put(ip, null);
				presenter.saveOpenDoor(mapIpToDevice.get(ip), bigImage, plateNO, true);
				return;
			}
			// 双摄像头等待
			Boolean isTwoChanel = CarparkMainApp.mapIsTwoChanel.get(device.getLinkAddress()) == null ? false : CarparkMainApp.mapIsTwoChanel.get(device.getLinkAddress());
			if (isTwoChanel) {
				try {
					Integer two = Integer.valueOf(
							mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔) == null ? SystemSettingTypeEnum.双摄像头识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔));
					long l = System.currentTimeMillis() - startTime;
					while (l < two) {
						Thread.sleep(50);
						l = System.currentTimeMillis() - startTime;
					}
					LOGGER.info("双摄像头等待时间{},已过{}",two,l);
					CarparkMainApp.mapInTwoCameraTask.remove(device.getLinkAddress());
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

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
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (StrUtil.isEmpty(lbl_inSmallImg)) {
						return;
					}
					if (inSmallImage != null) {
						LOGGER.info("出口小图片销毁图片");
						inSmallImage.dispose();
						lbl_inSmallImg.setBackgroundImage(null);
					}
					if (inBigImage != null) {
						LOGGER.info("出口大图片销毁图片");
						inBigImage.dispose();
						lbl_inBigImg.setBackgroundImage(null);
					}

					inSmallImage = CarparkUtils.getImage(smallImage, lbl_inSmallImg, shell);
					if (inSmallImage != null) {
						lbl_inSmallImg.setBackgroundImage(inSmallImage);
					}

					inBigImage = CarparkUtils.getImage(bigImage, lbl_inBigImg, shell);
					if (inBigImage != null) {
						lbl_inBigImg.setBackgroundImage(inBigImage);
					}
				}
			});
			model.setInShowPlateNO(plateNO);
			model.setInShowTime(dateString);
			
			String editPlateNo = null;
			boolean isEmptyPlateNo = false;
			// 空车牌处理
			if (StrUtil.isEmpty(plateNO)) {
				LOGGER.info("空的车牌");
				Boolean valueOf = Boolean.valueOf(
						mapSystemSetting.get(SystemSettingTypeEnum.是否允许无牌车进) == null ? SystemSettingTypeEnum.是否允许无牌车进.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.是否允许无牌车进));
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
					if (StrUtil.isEmpty(editPlateNo)) {
						return;
					}
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
			cch.setPlateNo(plateNO);
			cch.setInPlateNO(plateNO);
			
			
			LOGGER.debug("开始保存车牌：{}的图片", plateNO);
			long nanoTime = System.nanoTime();

			mapPlateNoDate.put(plateNO, date);
			String folder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
			String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
			String bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
			String smallImgFileName = fileName + "_" + plateNO + "_small.jpg";
			presenter.saveImage(folder, bigImgFileName, bigImage);
			presenter.saveImage(folder, smallImgFileName, smallImage);

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
			SingleCarparkBlackUser singleCarparkBlackUser = sp.getCarparkService().findBlackUserByPlateNO(plateNO);

			if (!StrUtil.isEmpty(singleCarparkBlackUser)) {
				Holiday findHolidayByDate = sp.getCarparkService().findHolidayByDate(new Date());
				if (!StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(singleCarparkBlackUser.getHolidayIn()) && singleCarparkBlackUser.getHolidayIn()) {
					model.setInShowMeg("黑名单");
					presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
					return;
				}
				if (StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(singleCarparkBlackUser.getWeekDayIn()) && singleCarparkBlackUser.getWeekDayIn()) {
					model.setInShowMeg("黑名单");
					presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
					return;
				}

				int hoursStart = singleCarparkBlackUser.getHoursStart();
				int hoursEnd = singleCarparkBlackUser.getHoursEnd() == 0 ? 23 : singleCarparkBlackUser.getHoursEnd();
				int minuteStart = singleCarparkBlackUser.getMinuteStart();
				int minuteEnd = singleCarparkBlackUser.getMinuteEnd();
				DateTime now = new DateTime(date);
				DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursStart, minuteStart, 00);
				DateTime de = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursEnd, minuteEnd, 59);
				if (singleCarparkBlackUser.getTimeIn()) {
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
			LOGGER.debug("显示车牌");
			presenter.showPlateNOToDevice(device, plateNO);

			model.setHistory(cch);
			LOGGER.debug("查找是否为固定车");
			SingleCarparkUser findByNameOrPlateNo = sp.getCarparkUserService().findUserByPlateNo(plateNO, device.getCarpark().getId());
			SingleCarparkUser user = StrUtil.isEmpty(findByNameOrPlateNo) ? null : findByNameOrPlateNo;

			String carType = "临时车";

			if (!StrUtil.isEmpty(user)) {
				boolean flag = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认));
				if (!isEmptyPlateNo) {
					if (flag) {
						model.setInCheckClick(true);
						presenter.showPlateNOToDevice(device, model.getInShowPlateNO());
						int i=0;
						while (model.isInCheckClick()) {
							if (i>120) {
								return;
							}
							try {
								Thread.sleep(500);
								i++;
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						presenter.showPlateNOToDevice(device, model.getInShowPlateNO());
						editPlateNo = model.getInShowPlateNO();
					}
				}

				// if(carpark.getFixCarOneIn()){
				// List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(plateNO);
				// if (!StrUtil.isEmpty(findByNoOut)) {
				// LOGGER.info("停车场要求固定车同时只能进入一辆,用户{}已有车牌{}进场记录",user.getName(),plateNO);
				// return;
				// }
				// }
				if (flag) {
					SingleCarparkUser findUserByPlateNo = sp.getCarparkUserService().findUserByPlateNo(editPlateNo, device.getCarpark().getId());
					SingleCarparkUser singleCarparkUser = StrUtil.isEmpty(findUserByPlateNo) ? null : findUserByPlateNo;
					if (StrUtil.isEmpty(singleCarparkUser)) {
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
			} else {
				LOGGER.debug("判断是否允许临时车进");
				if (device.getCarpark().isTempCarIsIn()) {
					presenter.showContentToDevice(device, "固定停车场,不允许临时车进", false);
					return;
				}
				boolean flag = false;
				if (!isEmptyPlateNo) {
					if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认))) {
						flag = true;
						model.setInCheckClick(true);
						while (model.isInCheckClick()) {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						presenter.showPlateNOToDevice(device, model.getHistory().getPlateNo());
						editPlateNo = model.getInShowPlateNO();
					}
				}
				if (flag) {
					SingleCarparkUser findUserByPlateNo = sp.getCarparkUserService().findUserByPlateNo(editPlateNo, device.getCarpark().getId());
					SingleCarparkUser singleCarparkUser = StrUtil.isEmpty(findUserByPlateNo) ? null : findUserByPlateNo;
					if (StrUtil.isEmpty(singleCarparkUser)) {
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
					}
				}
				if (shouTempCarToDevice(device)) {
					return;
				}
			}
			LOGGER.debug("车辆类型为：{}==t通道类型为：{}", carType, device.getRoadType());
			// showInDevice(device, plateNO, user);
			long nanoTime2 = System.nanoTime();
			LOGGER.debug(dateString + "==" + ip + "====" + plateNO + "车辆类型：" + carType + "==" + "保存图片：" + (nanoTime1 - nanoTime) + "==查找固定用户：" + (nanoTime2 - nanoTime3) + "==界面操作："
					+ (nanoTime3 - nanoTime1));
			LOGGER.info("把车牌:{}的进场记录保存到数据库", plateNO);
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

			if (carType.equals("临时车")) {
				int total = model.getTotalSlot() - 1;
				model.setTotalSlot(total <= 0 ? 0 : total);
			}
			Long saveInOutHistory = sp.getCarparkInOutService().saveInOutHistory(cch);
			cch.setId(saveInOutHistory);
			model.addInHistorys(cch);
			model.setInHistorySelect(cch);
			LOGGER.debug("保存车牌：{}的进场记录到数据库成功", plateNO);
			model.setHistory(null);
		} catch (Exception e) {
			LOGGER.error("车辆进场时发生错误，", e);
		}

	}

	/**
	 * @param device
	 * @return
	 */
	public boolean shouTempCarToDevice(SingleCarparkDevice device) throws Exception {
		Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许临时车入场));
		if (!valueOf) {
			if (model.getTotalSlot() <= 0) {
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
	 * @return 是否需要退出
	 */
	public boolean fixCarShowToDevice(Date date, SingleCarparkDevice device, SingleCarparkUser user, Long inId) throws Exception {
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
		if (user.getType().equals("免费")) {
			Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许免费车入场));
			if (!valueOf) {
				if (model.getTotalSlot() <= 0) {
					LOGGER.error("车位已满,不允许免费车进入");
					return true;
				}
			}

		}
		if (user.getType().equals("普通")) {
			Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许储值车入场));
			if (!valueOf) {
				if (model.getTotalSlot() <= 0) {
					LOGGER.error("车位已满,不允许储值车进入");
					return true;
				}
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
