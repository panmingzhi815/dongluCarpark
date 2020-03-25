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
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.ui.view.CarInCheckApp;
import com.donglu.carpark.ui.view.message.MessageUtil;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.FixCarInTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.ScreenTypeEnum;
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
	private static final String SLOT_IS_FULL = "车位已满,请停其它停车场";
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
		boolean checkPlateNODiscernGap = presenter.checkPlateNODiscernGap(mapPlateNoDate, plateNO, date);
		if (!checkPlateNODiscernGap) {
			return;
		}
		model.setInCheckClick(false);
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
		device.setCarpark(carpark);
		initImgPath();

		model.setInShowPlateNO(plateNO);
		model.setInShowTime(dateString);
		model.setInShowSmallImg(smallImage);
		model.setInShowBigImg(bigImage);
		model.setInBigImageName(bigImgFileName);

		// 空车牌处理
		if (StrUtil.isEmpty(plateNO)) {
			LOGGER.warn("空的车牌");
			emptyPlateShowQrCodeIn();
			Boolean valueOf = Boolean
					.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.是否允许无牌车进));
			if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认))
					|| Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认)) || !valueOf) {
				isEmptyPlateNo = true;
				if (model.booleanSetting(SystemSettingTypeEnum.临时车弹窗确认)) {
					new CarInCheckApp(this, sp, model).open();
					return;
				}
				model.setInCheckClick(true);
				model.getMapInCheck().put(plateNO, this);
				return;
			} else {
				if (!valueOf&&carpark.getParent()!=null) {
					return;
				}
			}
		}


		if (checkBlackUser(device, date)) {
			return;
		}
		LOGGER.info("查找是否为固定车");
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
		}else {
			if(user!=null?carpark.getFixCarOneIn():carpark.getTempCarOneIn()) {
				model.setInShowPlateNO(plateNO+"-"+(user!=null?user.getName():"临时车"));
				String content2 = model.getMapVoice().get(DeviceVoiceTypeEnum.未出场在进场语音).getContent();
				presenter.showContentToDevice(editPlateNo, device, content2, false);
				return;
			}
		}
		checkUser(!isEmptyPlateNo);
	}

	/**
	 * 
	 */
	public void emptyPlateShowQrCodeIn() {
		if(device.getScreenType().equals(ScreenTypeEnum.一体机)){
			if (!mapSystemSetting.get(SystemSettingTypeEnum.无车牌时使用二维码进出场).equals("true")) {
				presenter.showContentToDevice(editPlateNo, device, model.getMapVoice().get(DeviceVoiceTypeEnum.无牌车禁止扫码入场语音).getContent(), false);
				return;
			}
			if (model.getTotalSlot()>0) {
				if (tempCarCheckPass()) {
					presenter.qrCodeInOut(plateNO, device, true);
					SingleCarparkInOutHistory inOutHistory = new SingleCarparkInOutHistory();
					inOutHistory.setBigImg(bigImgFileName);
					inOutHistory.setSmallImg(smallImgFileName);
					model.getMapWaitInOutHistory().put(device.getIp(), inOutHistory);
				} 
			}else{
				presenter.showContentToDevice(editPlateNo, device, SLOT_IS_FULL, false);
			}
		}
	}
	/**
	 * 检测通道类型
	 * @return true 通过
	 */
	private boolean tempCarCheckPass() {
		Boolean valueOf2 = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车通道限制));
		if (!valueOf2) {
			if (CarparkUtils.checkRoadType(device, model, presenter, DeviceRoadTypeEnum.固定车通道,
					DeviceRoadTypeEnum.储值车通道)) {
				logger.info("车辆[{}]类型为[{}]无法进入停车场[{}]通道[{}]", editPlateNo, carType, carpark, device.getRoadType());
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	public void showPlateToDevice() {
		LOGGER.info("显示车牌:{}", plateNO);
		presenter.showPlateNOToDevice(device, editPlateNo);
		LOGGER.info("显示车牌完成");
	}

	/**
	 * @throws Exception
	 */
	/**
	 * @param isInCheck
	 * @throws Exception
	 */
	public void checkUser(boolean isInCheck) throws Exception {
		LOGGER.info("处理车牌信息");
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
		LOGGER.info("车牌处理完成");
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
			boolean equals = editPlateNo.equals(string.trim());
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
//		LOGGER.debug("车辆类型为：{}==t通道类型为：{}", carType, device.getRoadType());
//		LOGGER.debug(date + "==" + ip + "====" + plateNO + "车辆类型：" + carType + "==" + "保存图片：==查找固定用户：==界面操作：");
		LOGGER.info("把车牌:{}的进场记录保存到数据库", plateNO);
		if (cch.getId()!=null) {
			LOGGER.info("车辆未正常出场时再进场,自动更新出场时间,时间:{},id:{}",StrUtil.formatDateTime(new Date()),cch.getId());
			cch.setOutTime(cch.getInTime());
			cch.setRemarkString("车辆未正常出场时再进场,自动更新出场时间,时间:"+StrUtil.formatDateTime(new Date())+",id:"+cch.getId());
			cch.setFactMoney(0);
			cch.setShouldMoney(0);
			cch.setFreeMoney(0);
			cch.setOutPlateNO(editPlateNo);
			sp.getCarparkInOutService().saveInOutHistory(cch);
			cch.setId(null);
			cch.setOutTime(null);
			cch.setRemark(null);
			cch.setOutPlateNO(null);
		}
		cch.setPlateNo(plateNO);
		cch.setInPlateNO(plateNO);
		if (!StrUtil.isEmpty(editPlateNo)) {
			cch.setPlateNo(editPlateNo);
		}
		String plateColor = model.getPlateColorCache().asMap().getOrDefault(plateNO, "蓝色");
		if (plateColor.contains("黄")) {
			cch.setUserType("大车");
		}else{
			cch.setUserType("小车");
		}
		cch.setPlateColor(plateColor);
		cch.setLeftSlot(model.getTotalSlot()-1);
		cch.setTotalSlot(model.getCarpark().getTotalNumberOfSlot());
		cch.setInTime(date);
		cch.setOperaName(System.getProperty("userName"));
		cch.setBigImg(bigImgFileName);
		cch.setSmallImg(smallImgFileName);
		cch.setCarType(carType);
		cch.setCarparkId(carpark.getId());
		cch.setCarparkName(carpark.getName());
		if (!StrUtil.isEmpty(user)) {
			cch.setUser(user);
		}
		cch.setInDevice(device);
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
		if (mapSystemSetting.get(SystemSettingTypeEnum.固定车非所属停车场停留收费).equals("true")) {
			sp.getCarparkInOutService().updateCarparkStillTime(carpark, device,
					StrUtil.isEmpty(editPlateNo) ? plateNO : editPlateNo, cch.getBigImg());
		}
		Long saveInOutHistory = sp.getCarparkInOutService().saveInOutHistory(cch);
		LOGGER.info("保存车牌：{}的进场记录到数据库成功:{}", plateNO,saveInOutHistory);
		presenter.plateSubmit(cch, date, device, bigImage);
		cch.setId(saveInOutHistory);
		model.addInHistorys(cch);
		model.setInHistorySelect(cch);
		model.getMapCameraLastImage().put(ip, cch.getBigImg());
		presenter.showContentToDevice(editPlateNo,device, content, isOpenDoor);
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
			if (blackUser.getValid()!=null&&StrUtil.getTodayBottomTime(blackUser.getValid()).before(date)) {
				return false;
			}
			String reason="";
			if (!StrUtil.isEmpty(blackUser.getRemark())) {
				reason="-"+blackUser.getRemark();
			}
			Holiday findHolidayByDate = sp.getCarparkService().findHolidayByDate(new Date());
			String blackInContent = model.getMapVoice().get(DeviceVoiceTypeEnum.黑名单入场语音).getContent();
			if (!StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(blackUser.getHolidayIn())
					&& blackUser.getHolidayIn()) {
				showBlackInfo(device, reason, blackInContent);
				return true;
			}
			if (StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(blackUser.getWeekDayIn())
					&& blackUser.getWeekDayIn()) {
				showBlackInfo(device, reason, blackInContent);
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
					showBlackInfo(device, reason, blackInContent);
					return true;
				}

			} else {
				LOGGER.info("黑名单车牌：{}不能进入的时间为{}点到{}点", plateNO, hoursStart, hoursEnd);
				if (now.toDate().after(dt.toDate()) && now.toDate().before(de.toDate())) {
					LOGGER.error("车牌：{}为黑名单,现在时间为{}，在{}点到{}点之间", plateNO, now.toString("HH:mm:ss"), hoursStart,
							hoursEnd);
					showBlackInfo(device, reason, blackInContent);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param device
	 * @param reason
	 * @param blackInContent
	 */
	public void showBlackInfo(SingleCarparkDevice device, String reason, String blackInContent) {
		model.setInShowPlateNO(model.getInShowPlateNO() + "-黑名单"+reason);
		presenter.showContentToDevice(device, blackInContent, false);
		MessageUtil.info(editPlateNo+"黑名单", editPlateNo+"为黑名单"+(StrUtil.isEmpty(reason)?"":"\n原因"+reason), 60000);
	}

	private boolean visitorCarIn() {
		if(StrUtil.isEmpty(plateNO)) {
			return true;
		}
		SingleCarparkVisitor visitor = sp.getCarparkService().findVisitorByPlateAndCarpark(plateNO, carpark);
		if (visitor == null || visitor.getStatus().equals(VisitorStatus.不可用.name())||(visitor.getStartTime()!=null&&visitor.getStartTime().after(date))) {
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
		String sLeftMoney = "剩余" + leftMoney + "元";
		if (leftMoney<0) {
			sLeftMoney="欠费"+Math.abs(leftMoney)+"元";
		}
		if (leftMoney < prepaidCarInOutLimit) {
			String formatFloatString = CarparkUtils.formatFloatString(sLeftMoney + ",余额不足请联系管理员");
			presenter.showContentToDevice(device, formatFloatString, false);
			return true;
		}
		String prepaidCarInMsg = model.getMapVoice().get(DeviceVoiceTypeEnum.储值车进场语音).getContent();
		Float prepaidCarInOutRemind = Float
				.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.储值车提醒金额));
		;
		if (leftMoney < prepaidCarInOutRemind) {
			content = prepaidCarInMsg + ","+sLeftMoney + ",请及时充值";
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
//		if(new DateTime(date).withTime(14, 0, 0, 0).isBeforeNow()&&new DateTime(date).withTime(15, 0, 0, 0).isAfterNow()) {
//			presenter.showContentToDevice(editPlateNo, device, "外部车辆限时", false);
//			return true;
//		}
//		if(new DateTime(date).withTime(16, 40, 0, 0).isBeforeNow()&&new DateTime(date).withTime(17, 40, 0, 0).isAfterNow()) {
//			presenter.showContentToDevice(editPlateNo, device, "车辆限时", false);
//			return true;
//		}
		if(checkSpecialCar()) {
			model.setInShowPlateNO(editPlateNo+"-特殊车");
			isOpenDoor=true;
			return false;
		}
		if(checkInTime()) {
			presenter.showContentToDevice(editPlateNo, device, "外部车辆限行", false);
			return true;
		}
		LOGGER.info("临时车:{} 进场",editPlateNo);
		if (incheck) {
			// 临时车是否确认
			boolean flag = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认));
			if (!isEmptyPlateNo) {
				if (flag) {
					presenter.showContentToDevice(editPlateNo,device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车入场确认语音).getContent(), false);
					if (model.booleanSetting(SystemSettingTypeEnum.临时车弹窗确认)) {
						new CarInCheckApp(this, sp, model).open();
						return true;
					}
					model.setInCheckClick(true);
					model.getMapInCheck().put(plateNO, this);
					return true;
				}
			}
		}
		model.setInShowPlateNO(model.getInShowPlateNO() + "-临时车");
		LOGGER.info("判断是否允许临时车进");
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
				presenter.showContentToDevice(editPlateNo,device, SLOT_IS_FULL, false);
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
		if (!valueOf&&"普通".equals(user.getType())) {
			if (model.getTotalSlot() <= 0) {
				presenter.showContentToDevice(device, SLOT_IS_FULL, false);
				LOGGER.error("车位已满,不允许普通固定车进入");
				return true;
			}
		}else {
			if (model.getTotalSlot() <= 0&&!model.booleanSetting(SystemSettingTypeEnum.车位满是否允许普通车入场)) {
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
		if("免费".equals(user.getType())) {
			fixCarInMsg = model.getMapVoice().get(DeviceVoiceTypeEnum.免费车进场语音).getContent();
		}
		logger.info("过期判断:{}",validTo);
		// 过期判断
		if (date.after(
				new DateTime(validTo).plusDays(user.getDelayDays() == null ? 0 : user.getDelayDays()).toDate())) {
			boolean isShowContent=false;
			if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期变临时车).equals("true")) {
				String sss=mapSystemSetting.get(SystemSettingTypeEnum.固定车到期变临时车收费自动记费出场);
				if (!StrUtil.isEmpty(sss)&&Arrays.asList(sss.split(",")).contains(user.getName())) {
					
				}else{
					content = "车辆已过期" + content;
				}
				logger.info("固定车：{} 在{} 到期 直接做临时车计算 ",user,validTo);
				return tempCarShowToDevice(false);
			} else {
				Boolean isFixCarToTempCarConfim = Boolean
						.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车转临时车弹窗提示));
				model.setInShowPlateNO(model.getInShowPlateNO() + "-已过期");
				if (isFixCarToTempCarConfim) {
					isShowContent=presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent(), false);
					boolean confirm = new ConfimBox(editPlateNo, "车辆在[" + StrUtil.formatDate(validTo) + "]过期\n是否允许车辆按临时车进入停车场:[" + carpark.getName() + "]").open();
					logger.info("固定车：{} 在{} 到期 等待确认 ", user, validTo);
					if (confirm) {
						cch.setIsOverdue(true);
						cch.setFixCarInType(FixCarInTypeEnum.固定车过期变临时车);
						cch.setReviseInTime(date);
						cch.setRemarkString(editPlateNo + "已过期");
						logger.info("固定车：{} 在{} 到期 经确认后做临时车计算 ", user, validTo);
						return tempCarShowToDevice(false);
					} 
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
			if (!isShowContent) {
				model.setInShowPlateNO(model.getInShowPlateNO() + "-已过期");
				content =  model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent();
				isOpenDoor = true;
//				presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent(), false);
			}
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
					.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车车位满作临时车计费));
			int fixCarInSize = 0;
			String inPlates = "";
			logger.info("固定车车位：{},关联车牌：{}",slot,platesSet);
			for (String pn : platesSet) {
				List<SingleCarparkInOutHistory> findHistoryByChildCarparkInOut = null;
				// 如果找到这俩车的记录，判断这辆车是否为临时车进场，临时车进场则永远为临时车
				CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
				if (pn.equals(editPlateNo)) {
					findHistoryByChildCarparkInOut = carparkInOutService
							.findInOutHistoryByCarparkAndPlateNO(null, pn);
					for (SingleCarparkInOutHistory singleCarparkInOutHistory : findHistoryByChildCarparkInOut) {
						if (singleCarparkInOutHistory.getReviseInTime() != null&&!singleCarparkInOutHistory.getIsOverdue()) {
							if (singleCarparkInOutHistory.getFixCarInType().equals(FixCarInTypeEnum.固定车车位满变临时车)) {
								if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.绑定车辆允许场内换车))) {
									List<SingleCarparkInOutHistory> list = carparkInOutService.findInOutHistoryByCarparkAndPlateNO(carpark, platesSet, true);
									Set<String> plates=new HashSet<>();
									for (SingleCarparkInOutHistory singleCarparkInOutHistory2 : list) {
										plates.add(singleCarparkInOutHistory2.getPlateNo());
									}
									if (plates.size()<slot) {
										fixCarInShowMsg(validTo, fixCarInMsg,null);
										return false;
									}else{
										model.setInShowPlateNO(model.getInShowPlateNO()+"-换车场内有车");
										logger.info("场内换车时停车场：{} 的车辆：{} ,还未出去，禁止进入",carpark,plates);
										return true;
									}
								}
							}
							return tempCarShowToDevice(false);
						}
					}
					continue;
				} else {
					// 查找场内固定车标示的车辆
					findHistoryByChildCarparkInOut = carparkInOutService
							.findInOutHistoryByCarparkAndPlateNO(carpark, pn, true);
				}
				if (StrUtil.isEmpty(findHistoryByChildCarparkInOut)) {
					continue;
				}
				logger.info("场内车：{}",findHistoryByChildCarparkInOut);
				fixCarInSize++;
				inPlates += "[" + pn + "]";
			}
			if (isFixCarSlotFullAutoBeTemp) {
				if (fixCarInSize>0&&slot <= fixCarInSize) {
					setFixCarToTemIn(date, user);
					LOGGER.info("固定车车位满作临时车计费设置为{}，用户车位为{}，场内车辆为{}，作临时车进入", isFixCarSlotFullAutoBeTemp,
							user.getCarparkNo(), fixCarInSize);
					cch.setRemarkString(inPlates);
					return tempCarShowToDevice(false);
				}
			} else {
				if (fixCarInSize>0&&slot <= fixCarInSize) {
					LOGGER.info("固定车车位满作临时车计费设置为{}，用户车位为{}，场内车辆为{}，不允许进入", isFixCarSlotFullAutoBeTemp,
							user.getCarparkNo(), fixCarInSize);
					presenter.showContentToDevice(device,
							model.getMapVoice().get(DeviceVoiceTypeEnum.固定车车位停满禁止进入语音).getContent(), false);
					model.setInShowPlateNO(model.getInShowPlateNO() + "-个人车位满");
					Boolean isFixCarToTempCarConfim = Boolean
							.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车转临时车弹窗提示));
					if (isFixCarToTempCarConfim) {
							boolean confirm = new ConfimBox(editPlateNo, "用户车位已满,车辆" + inPlates + "已进场\n是否作为临时车进入停车场[" + carpark.getName() + "]").open();
							if (confirm) {
								cch.setReviseInTime(date);
								cch.setFixCarInType(FixCarInTypeEnum.固定车车位满变临时车);
								cch.setRemarkString(inPlates + "已在场内");
								return tempCarShowToDevice(false);
							}
					}
					return true;
				}
			}
		}
		logger.info("车牌：{} 为有效固定车，直接进入",editPlateNo);
		// int parseInt =
		// Integer.parseInt(StrUtil.isEmpty(user.getCarparkNo())?"0":user.getCarparkNo());
		fixCarInShowMsg(validTo, fixCarInMsg,null);
		return false;
	}

	/**
	 * @param validTo
	 * @param fixCarInMsg
	 * @param reviseInTime 
	 */
	public void fixCarInShowMsg(Date validTo, String fixCarInMsg, Date reviseInTime) {
		Date date2 = new DateTime(validTo).minusDays(user.getRemindDays() == null ? 0 : user.getRemindDays()).toDate();
		if (StrUtil.getTodayBottomTime(date2).before(date)) {
			int countDayByBetweenTime = CarparkUtils.countDayByBetweenTime(date, validTo);
			if (countDayByBetweenTime>0) {
				content = fixCarInMsg + ",剩余" + countDayByBetweenTime + "天";
			}
			isOpenDoor = true;
			LOGGER.debug("固定车：{}，{}", plateNO, content);
		} else {
			content = fixCarInMsg;
			isOpenDoor = true;
			LOGGER.debug("固定车：{}，{}", plateNO, content);
		}
		Integer carparkSlot = user.getCarparkSlot();
		if (user.getCarparkSlotType().equals(CarparkSlotTypeEnum.固定车位)) {
			LOGGER.info("车辆{}用户固定车位{},不计算车位", cch.getPlateNo(), carparkSlot);
			cch.setIsCountSlot(false);
		} else {
			cch.setIsCountSlot(true);
		}
		model.setInShowPlateNO(model.getInShowPlateNO()+"-"+user.getName());
		cch.setReviseInTime(reviseInTime);
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
		cch.setFixCarInType(FixCarInTypeEnum.固定车车位满变临时车);
	}
}
