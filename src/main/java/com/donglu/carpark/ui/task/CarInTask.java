package com.donglu.carpark.ui.task;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.CarparkMainApp;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

public class CarInTask {
	private Logger LOGGER = LoggerFactory.getLogger(CarInTask.class);
	String plateNO;
	String ip;
	private CarparkMainModel model;
//	public void run() {
//
//		Date date = new Date();
//		boolean checkPlateNODiscernGap = checkPlateNODiscernGap(plateNO, date);
//		if (!checkPlateNODiscernGap) {
//			return;
//		}
//		SingleCarparkInOutHistory cch = new SingleCarparkInOutHistory();
//		cch.setPlateNo(plateNO);
//		cch.setInPlateNO(plateNO);
//
//		String dateString = StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
//		model.setInShowPlateNO(plateNO);
//		model.setInShowTime(dateString);
//
//		long nanoTime1 = System.nanoTime();
//
//		LOGGER.info(dateString + "==" + ip + "==" + mapDeviceType.get(ip) + "==" + plateNO);
//		SingleCarparkDevice device = mapIpToDevice.get(ip);
//		if (StrUtil.isEmpty(device)) {
//			LOGGER.error("没有找到ip:" + ip + "的设备");
//			return;
//		}
//
//		LOGGER.debug("开始在界面显示车牌：{}的抓拍图片", plateNO);
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				if (inSmallImage != null) {
//					LOGGER.info("进场小图片销毁图片");
//					inSmallImage.dispose();
//					inSmallImage = null;
//					inSmallImg.setBackgroundImage(null);
//				}
//				if (inBigImage != null) {
//					LOGGER.info("进场大图片销毁图片");
//					inBigImage.dispose();
//					inBigImage = null;
//					inBigImg.setBackgroundImage(null);
//				}
//
//				inSmallImage = getImage(smallImage, inSmallImg, shell);
//				if (inSmallImage != null) {
//					inSmallImg.setBackgroundImage(inSmallImage);
//				}
//
//				inBigImage = getImage(bigImage, inBigImg, shell);
//				if (inBigImage != null) {
//					inBigImg.setBackgroundImage(inBigImage);
//				}
//
//				// txtinplateNo.setText(plateNO);
//				// text_in_time.setText(dateString);
//				plateNoTotal.addAndGet(1);
//			}
//		});
//		String editPlateNo = null;
//		// 空车牌处理
//		if (StrUtil.isEmpty(plateNO)) {
//			LOGGER.info("空的车牌");
//			if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认)) || Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认))) {
//				model.setInCheckClick(true);
//				while (model.isInCheckClick()) {
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				editPlateNo = model.getInShowPlateNO();
//			} else {
//				return;
//			}
//		}
//
//		LOGGER.debug("开始保存车牌：{}的图片", plateNO);
//		long nanoTime = System.nanoTime();
//
//		mapPlateNoDate.put(plateNO, date);
//		String folder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
//		String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
//		String bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
//		String smallImgFileName = fileName + "_" + plateNO + "_small.jpg";
//		presenter.saveImage(folder, bigImgFileName, bigImage);
//		presenter.saveImage(folder, smallImgFileName, smallImage);
//
//		// model.setInShowBigImg(inBigImage);
//		// model.setInShowSmallImg(inSmallImage);
//		// model.setInShowPlateNO(plateNO);
//		// model.setInShowTime(dateString);
//		// plateNoTotal.addAndGet(1);
//
//		long nanoTime3 = System.nanoTime();
//		LOGGER.debug("进行黑名单判断");
//		// SingleCarparkBlackUser singleCarparkBlackUser = mapBlackUser.get(plateNO);
//		SingleCarparkBlackUser singleCarparkBlackUser = sp.getCarparkService().findBlackUserByPlateNO(plateNO);
//		if (!StrUtil.isEmpty(singleCarparkBlackUser)) {
//			int hoursStart = singleCarparkBlackUser.getHoursStart();
//			int hoursEnd = singleCarparkBlackUser.getHoursEnd() == 0 ? 23 : singleCarparkBlackUser.getHoursEnd();
//			int minuteStart = singleCarparkBlackUser.getMinuteStart();
//			int minuteEnd = singleCarparkBlackUser.getMinuteEnd();
//			DateTime now = new DateTime(date);
//			DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursStart, minuteStart, 00);
//			DateTime de = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursEnd, minuteEnd, 59);
//			LOGGER.info("黑名单车牌：{}不能进入的时间为{}点到{}点", plateNO, hoursStart, hoursEnd);
//			if (now.toDate().after(dt.toDate()) && now.toDate().before(de.toDate())) {
//				LOGGER.error("车牌：{}为黑名单,现在时间为{}，在{}点到{}点之间", plateNO, now.toString("HH:mm:ss"), hoursStart, hoursEnd);
//				model.setInShowMeg("黑名单");
//				return;
//			}
//		}
//		LOGGER.debug("显示车牌");
//		presenter.showPlateNOToDevice(device, plateNO);
//
//		model.setHistory(cch);
//		LOGGER.debug("查找是否为固定车");
//		List<SingleCarparkUser> findByNameOrPlateNo = sp.getCarparkUserService().findUserByPlateNo(plateNO);
//		SingleCarparkUser user = StrUtil.isEmpty(findByNameOrPlateNo) ? null : findByNameOrPlateNo.get(0);
//
//		String carType = "临时车";
//
//		if (!StrUtil.isEmpty(user)) {
//			carType = "固定车";
//			if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认))) {
//				model.setInCheckClick(true);
//				presenter.showPlateNOToDevice(device, model.getInShowPlateNO());
//				while (model.isInCheckClick()) {
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				presenter.showPlateNOToDevice(device, model.getInShowPlateNO());
//				editPlateNo = model.getInShowPlateNO();
//			}
//			if (user.getType().equals("免费")) {
//				Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许免费车入场));
//				if (!valueOf) {
//					if (model.getTotalSlot() <= 0) {
//						LOGGER.error("车位已满,不允许免费车进入");
//						return;
//					}
//				}
//
//			}
//			if (user.getType().equals("普通")) {
//				Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许储值车入场));
//				if (!valueOf) {
//					if (model.getTotalSlot() <= 0) {
//						LOGGER.error("车位已满,不允许储值车进入");
//						return;
//					}
//				}
//			}
//			Date date2 = new DateTime(user.getValidTo()).minusDays(user.getRemindDays() == null ? 0 : user.getRemindDays()).toDate();
//			if (StrUtil.getTodayBottomTime(date2).before(date)) {
//				String content = CAR_IN_MSG + StrUtil.formatDate(user.getValidTo(), VILIDTO_DATE);
//				presenter.showContentToDevice(device, content, true);
//				LOGGER.info("固定车：{}，{}", plateNO, content);
//			} else {
//				String content = CAR_IN_MSG;
//				presenter.showContentToDevice(device, content, true);
//				LOGGER.info("固定车：{}，{}", plateNO, content);
//			}
//		} else {
//			LOGGER.debug("判断是否允许临时车进");
//			if (device.getCarpark().isTempCarIsIn()) {
//				presenter.showContentToDevice(device, "固定停车场,不允许临时车进", false);
//				presenter.showUsualContentToDevice(device, USUAL_MSG);
//				return;
//			}
//
//			if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认))) {
//				model.setInCheckClick(true);
//				while (model.isInCheckClick()) {
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				presenter.showPlateNOToDevice(device, model.getHistory().getPlateNo());
//				editPlateNo = model.getInShowPlateNO();
//			}
//
//			Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许临时车入场));
//			if (!valueOf) {
//				if (model.getTotalSlot() <= 0) {
//					LOGGER.error("车位已满,不允许临时车进入");
//					return;
//				}
//			}
//			presenter.showContentToDevice(device, CAR_IN_MSG, true);
//		}
//		LOGGER.debug("车辆类型为：{}==t通道类型为：{}", carType, device.getRoadType());
//		// showInDevice(device, plateNO, user);
//		long nanoTime2 = System.nanoTime();
//		LOGGER.debug(dateString + "==" + ip + "==" + mapDeviceType.get(ip) + "==" + plateNO + "车辆类型：" + carType + "==" + "保存图片：" + (nanoTime1 - nanoTime) + "==查找固定用户：" + (nanoTime2 - nanoTime3)
//				+ "==界面操作：" + (nanoTime3 - nanoTime1));
//		LOGGER.info("把车牌:{}的进场记录保存到数据库", plateNO);
//		if (!StrUtil.isEmpty(editPlateNo)) {
//			cch.setPlateNo(editPlateNo);
//		}
//		cch.setInTime(date);
//		cch.setOperaName(System.getProperty("userName"));
//		cch.setBigImg(folder + "/" + bigImgFileName);
//		cch.setSmallImg(folder + "/" + smallImgFileName);
//		cch.setCarType(carType);
//		if (!StrUtil.isEmpty(user)) {
//			cch.setUserName(user.getName());
//			cch.setUserId(user.getId());
//		}
//		cch.setInDevice(device.getName());
//		cch.setInPhotographType("自动");
//		Date handPhotographDate = mapHandPhotograph.get(ip);
//		if (!StrUtil.isEmpty(handPhotographDate)) {
//			DateTime plusSeconds = new DateTime(handPhotographDate).plusSeconds(3);
//			boolean after = plusSeconds.toDate().after(date);
//			if (after)
//				cch.setInPhotographType("手动");
//		}
//
//		if (carType.equals("临时车")) {
//			int total = model.getTotalSlot() - 1;
//			model.setTotalSlot(total <= 0 ? 0 : total);
//		}
//		sp.getCarparkInOutService().saveInOutHistory(cch);
//		LOGGER.debug("保存车牌：{}的进场记录到数据库成功", plateNO);
//		model.setHistory(null);
//	
//
//	}

}
