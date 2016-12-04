package com.donglu.carpark.ui.task;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser.CarparkSlotTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor.VisitorStatus;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

public class CarInTask extends AbstractTask {
	private static final String SLOT_IS_FULL = "车位已满";
	private static Logger LOGGER = LoggerFactory.getLogger(CarInTask.class);
	private static final String CAR_IN_MSG = "欢迎光临,请入场停车";

	// 保存车牌最近的处理时间
	// 保存设置信息
	private final Map<SystemSettingTypeEnum, String> mapSystemSetting;
	// 保存最近的手动拍照时间
	private final Map<String, Date> mapHandPhotograph;
	// 是否为空车牌
	private boolean isEmptyPlateNo = false;
	// 发送到设备的消息
	private String content = CAR_IN_MSG;
	// 是否开门
	private boolean isOpenDoor = false;
	private boolean isFixCarverdueCheck = false;

	public CarInTask(String ip, String plateNO, byte[] bigImage, byte[] smallImage, CarparkMainModel model,
			CarparkDatabaseServiceProvider sp, CarparkMainPresenter presenter, Float rightSize) {
		super(model, sp, presenter, ip, plateNO, bigImage, smallImage, rightSize);
		content = model.getMapVoice().get(DeviceVoiceTypeEnum.临时车进场语音).getContent();
		mapSystemSetting = model.getMapSystemSetting();
		mapHandPhotograph = model.getMapHandPhotograph();
	}

	@Override
	public void start() throws Exception {
		model.setInCheckClick(false);
		boolean checkPlateNODiscernGap = presenter.checkPlateNODiscernGap(mapPlateNoDate, plateNO, date);
		if (!checkPlateNODiscernGap) {
			return;
		}
		String dateString = StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
		model.setInShowPlateNO(plateNO);
		model.setInShowTime(dateString);
		LOGGER.debug(dateString + "==" + ip + "====" + plateNO);
		if (StrUtil.isEmpty(device)) {
			LOGGER.error("没有找到ip:" + ip + "的设备");
			return;
		}
		carpark = sp.getCarparkService().findCarparkById(device.getCarpark().getId());
		if (StrUtil.isEmpty(carpark)) {
			LOGGER.error("没有找到id:" + device.getCarpark().getId() + "的停车场");
			return;
		}
		initImgPath();

		model.setInShowPlateNO(plateNO);
		model.setInShowTime(dateString);
		model.setInShowSmallImg(smallImage);
		model.setInShowBigImg(bigImage);
		model.setInBigImageName(bigImgFileName);

		// 空车牌处理
		if (StrUtil.isEmpty(plateNO)) {
			LOGGER.warn("空的车牌");
			Boolean valueOf = Boolean
					.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.是否允许无牌车进));
			if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认))
					|| Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认)) || !valueOf) {
				model.setInCheckClick(true);
				isEmptyPlateNo = true;
				model.getMapInCheck().put(plateNO, this);
				return;
			} else {
				if (!valueOf) {
					return;
				}
			}
		}


		if (checkBlackUser(device, date)) {
			return;
		}
		LOGGER.debug("查找是否为固定车");
		user = sp.getCarparkUserService().findUserByPlateNo(plateNO, carpark.getId());
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
					model.setInShowPlateNO(editPlateNo);
				}
			}
		}
		showPlateToDevice();
		List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(editPlateNo, carpark);
		cch = StrUtil.isEmpty(findByNoOut) ? null : findByNoOut.get(0);
		if (StrUtil.isEmpty(cch)) {
			cch = new SingleCarparkInOutHistory();
		}
		checkUser(!isEmptyPlateNo);
	}

	/**
	 * 
	 */
	public void showPlateToDevice() {
		LOGGER.debug("显示车牌:{}", plateNO);
		presenter.showPlateNOToDevice(device, editPlateNo);
	}

	/**
	 * @throws Exception
	 */
	/**
	 * @param isInCheck
	 * @throws Exception
	 */
	public void checkUser(boolean isInCheck) throws Exception {
		if (isEmptyPlateNo) {
			saveImage();
		}
		checkFixCar(editPlateNo);
		if (!StrUtil.isEmpty(user) && !isFixCarverdueCheck) {// 固定车操作
			if (fixCarShowToDevice(isInCheck)) {
				return;
			}
		} else {
			if (tempCarShowToDevice(isInCheck)) {
				return;
			}
		}
		saveInHistory();
	}

	/**
	 * @return
	 */
	private boolean checkFixCar(String editPlateNo) {
		if (user == null) {
			return true;
		}
		String[] split = user.getPlateNo().split(",");
		boolean isFixCar = false;
		for (String string : split) {
			boolean equals = editPlateNo.equals(string);
			if (equals) {
				isFixCar = equals;
				break;
			}
		}
		if (!isFixCar) {
			user = null;
		}
		return isFixCar;
	}

	/**
	 * @param dateString
	 * @param nanoTime1
	 * @param nanoTime
	 * @param nanoTime3
	 */
	public void saveInHistory() {
		LOGGER.debug("车辆类型为：{}==t通道类型为：{}", carType, device.getRoadType());
		LOGGER.debug(date + "==" + ip + "====" + plateNO + "车辆类型：" + carType + "==" + "保存图片：==查找固定用户：==界面操作：");
		LOGGER.info("把车牌:{}的进场记录保存到数据库", plateNO);

		cch.setPlateNo(plateNO);
		cch.setInPlateNO(plateNO);
		if (!StrUtil.isEmpty(editPlateNo)) {
			cch.setPlateNo(editPlateNo);
		}
		cch.setInTime(date);
		cch.setOperaName(System.getProperty("userName"));
		cch.setBigImg(bigImgFileName);
		cch.setSmallImg(smallImgFileName);
		cch.setCarType(carType);
		cch.setCarparkId(carpark.getId());
		cch.setCarparkName(carpark.getName());
		if (!StrUtil.isEmpty(user)) {
			cch.setUserName(user.getName());
			cch.setUserId(user.getId());
		}
		cch.setInDevice(device.getName());
		cch.setInPhotographType("自动");
		cch.setChargeTime(null);
		cch.setChargeOperaName(null);
		cch.setFactMoney(0);
		Date handPhotographDate = mapHandPhotograph.get(ip);
		if (!StrUtil.isEmpty(handPhotographDate)) {
			DateTime plusSeconds = new DateTime(handPhotographDate).plusSeconds(3);
			boolean after = plusSeconds.toDate().after(date);
			if (after)
				cch.setInPhotographType("手动");
		}
		sp.getCarparkInOutService().updateCarparkStillTime(carpark, device,
				StrUtil.isEmpty(editPlateNo) ? plateNO : editPlateNo, cch.getBigImg());
		Long saveInOutHistory = sp.getCarparkInOutService().saveInOutHistory(cch);
		presenter.plateSubmit(cch, date, device, bigImage);
		cch.setId(saveInOutHistory);
		model.addInHistorys(cch);
		model.setInHistorySelect(cch);
		LOGGER.info("保存车牌：{}的进场记录到数据库成功", plateNO);
		model.getMapCameraLastImage().put(ip, cch.getBigImg());
		presenter.showContentToDevice(device, content, isOpenDoor);
		presenter.updatePosition(carpark, cch, true);
		LOGGER.info("对设备{}，发送消息{}，开门信号：{}", device.getName(), content, isOpenDoor);
	}

	/**
	 * 黑名单判断
	 * 
	 * @param device
	 *            设备
	 * @param date
	 *            现在时间
	 * @return true为黑名单
	 */
	public boolean checkBlackUser(SingleCarparkDevice device, Date date) {
		LOGGER.debug("进行黑名单判断");
		SingleCarparkBlackUser blackUser = sp.getCarparkService().findBlackUserByPlateNO(plateNO);

		// 黑名单判断
		if (!StrUtil.isEmpty(blackUser)) {
			Holiday findHolidayByDate = sp.getCarparkService().findHolidayByDate(new Date());
			if (!StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(blackUser.getHolidayIn())
					&& blackUser.getHolidayIn()) {
				model.setInShowPlateNO(model.getInShowPlateNO() + "-黑名单");
				presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
				return true;
			}
			if (StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(blackUser.getWeekDayIn())
					&& blackUser.getWeekDayIn()) {
				model.setInShowPlateNO(model.getInShowPlateNO() + "-黑名单");
				presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
				return true;
			}

			int hoursStart = blackUser.getHoursStart();
			int hoursEnd = blackUser.getHoursEnd() == 0 ? 23 : blackUser.getHoursEnd();
			int minuteStart = blackUser.getMinuteStart();
			int minuteEnd = blackUser.getMinuteEnd();
			DateTime now = new DateTime(date);
			DateTime dt = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursStart,
					minuteStart, 00);
			DateTime de = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hoursEnd, minuteEnd,
					59);
			if (blackUser.getTimeIn()) {
				LOGGER.info("黑名单车牌：{}允许进入的时间为{}点到{}点", plateNO, hoursStart, hoursEnd);
				if (now.isBefore(dt.getMillis()) || now.isAfter(de.getMillis())) {
					model.setInShowPlateNO(model.getInShowPlateNO() + "-黑名单");
					presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
					return true;
				}

			} else {
				LOGGER.info("黑名单车牌：{}不能进入的时间为{}点到{}点", plateNO, hoursStart, hoursEnd);
				if (now.toDate().after(dt.toDate()) && now.toDate().before(de.toDate())) {
					LOGGER.error("车牌：{}为黑名单,现在时间为{}，在{}点到{}点之间", plateNO, now.toString("HH:mm:ss"), hoursStart,
							hoursEnd);
					model.setInShowPlateNO(model.getInShowPlateNO() + "-黑名单");
					presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
					return true;
				}
			}
		}
		return false;
	}

	private boolean visitorCarIn() {
		SingleCarparkVisitor visitor = sp.getCarparkService().findVisitorByPlateAndCarpark(plateNO, carpark);
		if (visitor == null || visitor.getStatus().equals(VisitorStatus.不可用.name())) {
			return true;
		}
		Date validTo = visitor.getValidTo();
		if (validTo != null) {
			if (validTo.before(date)) {
				visitor.setStatus(VisitorStatus.不可用.name());
				sp.getCarparkService().saveVisitor(visitor);
				return true;
			}
			Integer allIn = visitor.getAllIn();
			if (allIn!=null&&allIn>0) {
				int inCount = visitor.getInCount();
				if (allIn <= inCount) {
					return true;
				}
				visitor.setInCount(inCount + 1);
				if (Boolean.valueOf(model.getMapSystemSetting().getOrDefault(SystemSettingTypeEnum.访客车进场次数用完不能随便出, "false"))) {
					if (visitor.getOutCount()>=allIn) {
						visitor.setStatus(VisitorStatus.不可用.name());
					}
				}
				sp.getCarparkService().saveVisitor(visitor);
			}
			
		} else {
			Integer allIn = visitor.getAllIn();
			if (allIn != null && allIn > 0) {
				int inCount = visitor.getInCount();
				if (allIn <= inCount) {
					return true;
				}
				visitor.setInCount(inCount + 1);
				if (Boolean.valueOf(model.getMapSystemSetting().getOrDefault(SystemSettingTypeEnum.访客车进场次数用完不能随便出, "false"))) {
					if (visitor.getOutCount()>=allIn) {
						visitor.setStatus(VisitorStatus.不可用.name());
					}
				}
				sp.getCarparkService().saveVisitor(visitor);
			}
		}
		model.setInShowPlateNO(model.getInShowPlateNO() + "-"+ConstUtil.getVisitorName());
		isOpenDoor = true;
		return false;
	}

	/**
	 * @param device
	 * @param user
	 * @return
	 */
	public boolean prepaidCarIn() {
		if (CarparkUtils.checkRoadType(device, model, presenter, DeviceRoadTypeEnum.临时车通道, DeviceRoadTypeEnum.固定车通道)) {
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
		Float prepaidCarInOutLimit = Float
				.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.储值车进出场限制金额));
		if (leftMoney < prepaidCarInOutLimit) {
			String formatFloatString = CarparkUtils.formatFloatString("剩余" + leftMoney + "元,余额不足请联系管理员");
			presenter.showContentToDevice(device, formatFloatString, false);
			return true;
		}
		String prepaidCarInMsg = model.getMapVoice().get(DeviceVoiceTypeEnum.储值车进场语音).getContent();
		Float prepaidCarInOutRemind = Float
				.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.储值车提醒金额));
		;
		if (leftMoney < prepaidCarInOutRemind) {
			content = prepaidCarInMsg + ",剩余" + leftMoney + "元,请及时充值";
			content = CarparkUtils.formatFloatString(content);
			isOpenDoor = true;
			// presenter.showContentToDevice(device, content, true);
		}
		if (leftMoney > 100) {
			content = prepaidCarInMsg;
			isOpenDoor = true;
			// presenter.showContentToDevice(device, content, true);
		}
		model.setInShowPlateNO(model.getInShowPlateNO() + "-储值车");
		return false;
	}

	/**
	 * @param device
	 * @param user
	 * @param cch
	 * @param date
	 * @param incheck
	 * @return
	 */
	public boolean tempCarShowToDevice(boolean incheck) throws Exception {
		if (!visitorCarIn()) {
			return false;
		}
		if (incheck) {
			// 临时车是否确认
			boolean flag = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认));
			if (!isEmptyPlateNo) {
				if (flag) {
					model.setInCheckClick(true);
					presenter.showContentToDevice(device, "临时车等待确认", false);
					model.getMapInCheck().put(plateNO, this);
					return true;
				}
			}
		}
		model.setInShowPlateNO(model.getInShowPlateNO() + "-临时车");
		LOGGER.debug("判断是否允许临时车进");
		if (device.getCarpark().isTempCarIsIn()) {
			presenter.showContentToDevice(device,
					model.getMapVoice().get(DeviceVoiceTypeEnum.固定停车场临时车进入语音).getContent(), false);
			return true;
		}

		Boolean valueOf2 = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车通道限制));
		if (!valueOf2) {
			if (CarparkUtils.checkRoadType(device, model, presenter, DeviceRoadTypeEnum.固定车通道,
					DeviceRoadTypeEnum.储值车通道)) {
				logger.info("车辆[{}]类型为[{}]无法进入停车场[{}]通道[{}]", editPlateNo, carType, carpark, device.getRoadType());
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
		isOpenDoor = true;
		return false;
	}

	/**
	 * @param date
	 * @param device
	 * @param user
	 * @param incheck
	 * @param cch
	 * @return 是否需要退出 true退出
	 */
	public boolean fixCarShowToDevice(boolean incheck) throws Exception {
		logger.info("固定车:{} 进场",user);
		if (incheck) {
			// 固定车入场确认
			boolean flag = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认));
			if (flag) {
				model.setInCheckClick(true);
				presenter.showContentToDevice(device, "固定车等待确认", false);
				model.getMapInCheck().put(plateNO, this);
				return true;
			}
		}
		carType = "固定车";
		if (user.getType().equals("储值")) {
			return prepaidCarIn();
		}

		if (CarparkUtils.checkRoadType(device, model, presenter, DeviceRoadTypeEnum.储值车通道, DeviceRoadTypeEnum.临时车通道)) {
			return true;
		}
		Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许免费车入场));
		if (!valueOf) {
			if (model.getTotalSlot() <= 0) {
				presenter.showContentToDevice(device, SLOT_IS_FULL, false);
				LOGGER.error("车位已满,不允许免费车进入");
				return true;
			}
		}
		// 用户所有信息
		List<SingleCarparkUser> listUser = sp.getCarparkUserService().findUserByNameAndCarpark(editPlateNo, carpark,
				null);
		Date validTo = user.getValidTo();
		// 获取最晚过期时间
		for (SingleCarparkUser singleCarparkUser : listUser) {
			Date validTo2 = singleCarparkUser.getValidTo();
			if (validTo2.after(validTo)) {
				user = singleCarparkUser;
				validTo = validTo2;
			}
		}
		String fixCarInMsg = model.getMapVoice().get(DeviceVoiceTypeEnum.固定车进场语音).getContent();
		logger.info("过期判断");
		// 过期判断
		if (date.after(
				new DateTime(validTo).plusDays(user.getDelayDays() == null ? 0 : user.getDelayDays()).toDate())) {
			if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期变临时车).equals("true")) {
				content = "车辆已过期" + content;
				logger.info("固定车：{} 在{} 到期 直接做临时车计算 ",user,validTo);
				return tempCarShowToDevice(false);
			} else {
				boolean confirm = new ConfimBox(editPlateNo, "车辆在[" + StrUtil.formatDate(validTo) + "]过期\n是否允许车辆按临时车进入停车场:["+carpark.getName()+"]")
						.open();
				logger.info("固定车：{} 在{} 到期 等待确认 ",user,validTo);
				if (confirm) {
					cch.setIsOverdue(true);
					cch.setReviseInTime(date);
					logger.info("固定车：{} 在{} 到期 经确认后做临时车计算 ",user,validTo);
					return tempCarShowToDevice(false);
				}
			}
			if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期所属停车场限制).equals("true")) {
				LOGGER.info("固定车到期，固定车到期所属停车场限制:{}。判断是否进入所属停车场", true);
				if (device.getCarpark().equals(user.getCarpark())) {
					content = fixCarInMsg + ",车辆已过期,请及时续费";
					isOpenDoor = true;
					return false;
				}
			}
			model.setInShowPlateNO(model.getInShowPlateNO() + "-已过期");
			presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent(),
					false);
			model.getMapInCheck().put(plateNO, this);
			model.setInCheckClick(true);
			isFixCarverdueCheck = true;
			return true;
		}
		logger.info("车位判断");
		// 车位判断
		if (StrUtil.isEmpty(cch.getId())) {
			Integer slot = 0;
			Set<String> platesSet = new HashSet<>();
			for (SingleCarparkUser singleCarparkUser : listUser) {
				if (singleCarparkUser.getValidTo().after(date)) {
					slot += singleCarparkUser.getCarparkSlot();
					platesSet.addAll(Arrays.asList(singleCarparkUser.getPlateNo().split(",")));
				}
			}
			Boolean isFixCarSlotFullAutoBeTemp = Boolean
					.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车车位满作临时车计费) == null ? "false"
							: mapSystemSetting.get(SystemSettingTypeEnum.固定车车位满作临时车计费));
			int fixCarInSize = 0;
			String inPlates = "";
			for (String pn : platesSet) {
				List<SingleCarparkInOutHistory> findHistoryByChildCarparkInOut = null;
				// 如果找到这俩车的记录，判断这辆车是否为临时车进场，临时车进场则永远为临时车
				if (pn.equals(editPlateNo)) {
					findHistoryByChildCarparkInOut = sp.getCarparkInOutService()
							.findInOutHistoryByCarparkAndPlateNO(null, pn);
					for (SingleCarparkInOutHistory singleCarparkInOutHistory : findHistoryByChildCarparkInOut) {
						if (singleCarparkInOutHistory.getReviseInTime() != null) {
							logger.info("车牌：{} 在其他停车场是临时身份进入，直接做临时车计算",editPlateNo);
							return tempCarShowToDevice(false);
						}
					}
					continue;
				} else {
					// 查找场内固定车标示的车辆
					findHistoryByChildCarparkInOut = sp.getCarparkInOutService()
							.findInOutHistoryByCarparkAndPlateNO(carpark, pn, true);
				}
				if (StrUtil.isEmpty(findHistoryByChildCarparkInOut)) {
					continue;
				}
				fixCarInSize++;
				inPlates += "[" + pn + "]";
			}
			if (isFixCarSlotFullAutoBeTemp) {
				if (slot <= fixCarInSize) {
					setFixCarToTemIn(date, user);
					LOGGER.info("固定车车位满作临时车计费设置为{}，用户车位为{}，场内车辆为{}，作临时车进入", isFixCarSlotFullAutoBeTemp,
							user.getCarparkNo(), fixCarInSize);
					return tempCarShowToDevice(false);
				}
			} else {
				if (slot <= fixCarInSize) {
					LOGGER.info("固定车车位满作临时车计费设置为{}，用户车位为{}，场内车辆为{}，不允许进入", isFixCarSlotFullAutoBeTemp,
							user.getCarparkNo(), fixCarInSize);
					presenter.showContentToDevice(device,
							model.getMapVoice().get(DeviceVoiceTypeEnum.固定车车位停满禁止进入语音).getContent(), false);
					model.setInShowPlateNO(model.getInShowPlateNO() + "-个人车位满");
					boolean confirm = new ConfimBox(editPlateNo, "用户车位已满,车辆" + inPlates + "已进场\n是否作为临时车进入停车场[" + carpark.getName()
							+ "]").open();
					if (confirm) {
						cch.setReviseInTime(date);
						return tempCarShowToDevice(false);
					}
					return true;
				}
			}
		}
		logger.info("车牌：{} 为有效固定车，直接进入",editPlateNo);
		// int parseInt =
		// Integer.parseInt(StrUtil.isEmpty(user.getCarparkNo())?"0":user.getCarparkNo());
		Date date2 = new DateTime(validTo).minusDays(user.getRemindDays() == null ? 0 : user.getRemindDays()).toDate();
		if (StrUtil.getTodayBottomTime(date2).before(date)) {
			content = fixCarInMsg + ",剩余" + CarparkUtils.countDayByBetweenTime(date, validTo) + "天";
			isOpenDoor = true;
			LOGGER.debug("固定车：{}，{}", plateNO, content);
		} else {
			content = fixCarInMsg;
			isOpenDoor = true;
			LOGGER.debug("固定车：{}，{}", plateNO, content);
		}
		Integer carparkSlot = user.getCarparkSlot();
		if (user.getCarparkSlotType().equals(CarparkSlotTypeEnum.固定车位)) {
			LOGGER.debug("车辆{}用户固定车位{},不计算车位", cch.getPlateNo(), carparkSlot);
			cch.setIsCountSlot(false);
		} else {
			cch.setIsCountSlot(true);
		}
		model.setInShowPlateNO(model.getInShowPlateNO()+"-"+user.getName());
		cch.setReviseInTime(null);
		return false;
	}

	/**
	 * 
	 */
	public void refreshUserAndHistory() {
		// editPlateNo = model.getInShowPlateNO();
		presenter.showPlateNOToDevice(device, editPlateNo);
		if (!editPlateNo.equals(plateNO)) {
			user = sp.getCarparkUserService().findUserByPlateNo(editPlateNo, device.getCarpark().getId());
			String plateNO = editPlateNo == null ? this.plateNO : editPlateNo;
			if (plateNO.length() < 6) {
				user = null;
			}
			initInOutHistory(device);
		}
	}

	/**
	 * 初始化进场记录
	 * 
	 * @param device
	 */
	public void initInOutHistory(SingleCarparkDevice device) {
		List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(editPlateNo,
				device.getCarpark());
		cch = StrUtil.isEmpty(findByNoOut) ? null : findByNoOut.get(0);
		if (StrUtil.isEmpty(cch)) {
			cch = new SingleCarparkInOutHistory();
		}
		cch.setReviseInTime(null);
	}

	/**
	 * @param date
	 * @param user
	 */
	private synchronized void setFixCarToTemIn(Date date, SingleCarparkUser user) {
		// user.setTempCarTime((StrUtil.isEmpty(user.getTempCarTime()) ? "" :
		// user.getTempCarTime() + ",") + StrUtil.formatDate(date,
		// StrUtil.DATETIME_PATTERN));
		// sp.getCarparkUserService().saveUser(user);
		cch.setReviseInTime(date);
	}
}
