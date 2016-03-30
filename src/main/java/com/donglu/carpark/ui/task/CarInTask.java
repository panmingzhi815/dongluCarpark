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
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser.CarparkSlotTypeEnum;
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


	private final CLabel lbl_inSmallImg;

	private final CLabel lbl_inBigImg;

	//是否为空车牌
	private boolean isEmptyPlateNo = false;
	//修改的车牌
	private String editPlateNo = null;
	//车辆性质
	private String carType = "临时车";
	//车辆记录
	private SingleCarparkInOutHistory cch;
	//进场设备
	private SingleCarparkDevice device;
	//进场停车场
	private SingleCarparkCarpark carpark;
	//进场时间
	private Date date = new Date();
	//查找到的用户，判断是否为固定用户
	private SingleCarparkUser user;
	//小图片保存位置
	private String smallImgSavePath;
	//发送到设备的消息
	private String content=CAR_IN_MSG;
	//是否开门
	private boolean isOpenDoor=false;
	//图片保存文件夹
	private String imageSavefolder;
	//小图片名称
	private String smallImgFileName;
	//大图片名称
	private String bigImgFileName;
	
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
		content=model.getMapVoice().get(DeviceVoiceTypeEnum.临时车进场语音).getContent();
	}

	@Override
	public void run() {
		model.setInCheckClick(false);
		try {
			device = mapIpToDevice.get(ip);

			boolean checkPlateNODiscernGap = presenter.checkPlateNODiscernGap(mapPlateNoDate, plateNO, date);
			if (!checkPlateNODiscernGap) {
				return;
			}
			

			String dateString = StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
			model.setInShowPlateNO(plateNO);
			model.setInShowTime(dateString);

			LOGGER.info(dateString + "==" + ip + "====" + plateNO);

			if (StrUtil.isEmpty(device)) {
				LOGGER.error("没有找到ip:" + ip + "的设备");
				return;
			}
			carpark = sp.getCarparkService().findCarparkById(device.getCarpark().getId());
			if (StrUtil.isEmpty(carpark)) {
				LOGGER.error("没有找到id:" + device.getCarpark().getId() + "的停车场");
				return;
			}
			imageSavefolder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
			String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
			bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
			smallImgFileName = fileName + "_" + plateNO + "_small.jpg";
			LOGGER.debug("开始在界面显示车牌：{}的抓拍图片", plateNO);
			smallImgSavePath = imageSavefolder + "/" + smallImgFileName;
			DEFAULT_DISPLAY.asyncExec(new Runnable() {
				@Override
				public void run() {
					if (StrUtil.isEmpty(lbl_inSmallImg)) {
						return;
					}
					CarparkUtils.setBackgroundImage(smallImage, lbl_inSmallImg, DEFAULT_DISPLAY);
					CarparkUtils.setBackgroundImage(bigImage, lbl_inBigImg, getBigImgSavePath());
				}
			});
			model.setInShowPlateNO(plateNO);
			model.setInShowTime(dateString);
			
			// 空车牌处理
			if (StrUtil.isEmpty(plateNO)) {
				LOGGER.info("空的车牌");
				Boolean valueOf = Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.是否允许无牌车进));
				if (Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认)) || Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认)) || !valueOf) {
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
			List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(plateNO, carpark);
			cch = StrUtil.isEmpty(findByNoOut)?null:findByNoOut.get(0);
			if (StrUtil.isEmpty(cch)) {
				cch = new SingleCarparkInOutHistory();
			}
			
			saveImage();

			showPlateToDevice();
			
			if(checkBlackUser(device, date)){
				return;
			}

			model.setHistory(cch);
			LOGGER.debug("查找是否为固定车");
			user = sp.getCarparkUserService().findUserByPlateNo(plateNO, device.getCarpark().getId());

			checkUser(!isEmptyPlateNo);
		} catch (Exception e) {
			LOGGER.error("车辆进场时发生错误，", e);
		}

	}
	

	/**
	 * 保存图片
	 */
	public void saveImage() {
		LOGGER.debug("开始保存车牌：{}的图片", plateNO);

		mapPlateNoDate.put(plateNO, date);
		presenter.saveImage(imageSavefolder, smallImgFileName,bigImgFileName,smallImage, bigImage);
	}

	/**
	 * 
	 */
	public void showPlateToDevice() {
		LOGGER.debug("显示车牌:{}",plateNO);
		presenter.showPlateNOToDevice(device, plateNO);
	}

	/**
	 * @throws Exception
	 */
	public void checkUser(boolean isInCheck) throws Exception {
		if (isEmptyPlateNo) {
			saveImage();
		}
		if (!StrUtil.isEmpty(user)) {//固定车操作
			if(fixCarShowToDevice(isInCheck)){
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
	 * @param dateString
	 * @param nanoTime1
	 * @param nanoTime
	 * @param nanoTime3
	 */
	public void saveInHistory() {
		LOGGER.info("车辆类型为：{}==t通道类型为：{}", carType, device.getRoadType());
		LOGGER.info(date + "==" + ip + "====" + plateNO + "车辆类型：" + carType + "==" + "保存图片：==查找固定用户：==界面操作：");
		LOGGER.info("把车牌:{}的进场记录保存到数据库", plateNO);
		
		cch.setPlateNo(plateNO);
		cch.setInPlateNO(plateNO);
		if (!StrUtil.isEmpty(editPlateNo)) {
			cch.setPlateNo(editPlateNo);
		}
		cch.setInTime(date);
		cch.setOperaName(System.getProperty("userName"));
		cch.setBigImg(getBigImgSavePath());
		cch.setSmallImg(smallImgSavePath);
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
		sp.getCarparkInOutService().updateCarparkStillTime(carpark,device, StrUtil.isEmpty(editPlateNo)?plateNO:editPlateNo,cch.getBigImg());
		Long saveInOutHistory = sp.getCarparkInOutService().saveInOutHistory(cch);
		presenter.plateSubmit(cch, date, device, bigImage);
		cch.setId(saveInOutHistory);
		model.addInHistorys(cch);
		model.setInHistorySelect(cch);
		LOGGER.info("保存车牌：{}的进场记录到数据库成功", plateNO);
		CarparkMainApp.mapCameraLastImage.put(ip, cch.getBigImg());
		model.setHistory(null);
		presenter.showContentToDevice(device, content, isOpenDoor);
		LOGGER.info("对设备{}，发送消息{}，开门信号：{}",device.getName(),content,isOpenDoor);
	}

	/**
	 * 黑名单判断
	 * @param device 设备
	 * @param date 现在时间
	 * @return 
	 */
	public boolean checkBlackUser(SingleCarparkDevice device, Date date) {
		LOGGER.debug("进行黑名单判断");
		SingleCarparkBlackUser blackUser = sp.getCarparkService().findBlackUserByPlateNO(plateNO);
		
		//黑名单判断
		if (!StrUtil.isEmpty(blackUser)) {
			Holiday findHolidayByDate = sp.getCarparkService().findHolidayByDate(new Date());
			if (!StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(blackUser.getHolidayIn()) && blackUser.getHolidayIn()) {
				model.setInShowMeg("黑名单");
				presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
				return true;
			}
			if (StrUtil.isEmpty(findHolidayByDate) && !StrUtil.isEmpty(blackUser.getWeekDayIn()) && blackUser.getWeekDayIn()) {
				model.setInShowMeg("黑名单");
				presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
				return true;
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
					return true;
				}

			} else {
				LOGGER.info("黑名单车牌：{}不能进入的时间为{}点到{}点", plateNO, hoursStart, hoursEnd);
				if (now.toDate().after(dt.toDate()) && now.toDate().before(de.toDate())) {
					LOGGER.error("车牌：{}为黑名单,现在时间为{}，在{}点到{}点之间", plateNO, now.toString("HH:mm:ss"), hoursStart, hoursEnd);
					model.setInShowMeg("黑名单");
					presenter.showContentToDevice(device, "管制车辆，请联系管理员", false);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param device
	 * @param user
	 * @return 
	 */
	public boolean prepaidCarIn() {
		if (CarparkUtils.checkRoadType(device,model, presenter, DeviceRoadTypeEnum.临时车通道,DeviceRoadTypeEnum.固定车通道)) {
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
		String prepaidCarInMsg = model.getMapVoice().get(DeviceVoiceTypeEnum.储值车进场语音).getContent();
		Float prepaidCarInOutRemind=Float.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.储值车提醒金额));;
		if (leftMoney<prepaidCarInOutRemind) {
			content = prepaidCarInMsg+",剩余"+leftMoney+"元,请及时充值";
			content=CarparkUtils.formatFloatString(content);
			isOpenDoor=true;
//			presenter.showContentToDevice(device, content, true);
		}
		if (leftMoney>100) {
			content = prepaidCarInMsg;
			isOpenDoor=true;
//			presenter.showContentToDevice(device, content, true);
		}
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
	public boolean tempCarShowToDevice( boolean incheck) throws Exception {
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
		LOGGER.debug("判断是否允许临时车进");
		if (device.getCarpark().isTempCarIsIn()) {
			presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定停车场临时车进入语音).getContent(), false);
			return true;
		}
	
		Boolean valueOf2 = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车通道限制));
		if (!valueOf2) {
			if (CarparkUtils.checkRoadType(device,model, presenter, DeviceRoadTypeEnum.固定车通道,DeviceRoadTypeEnum.储值车通道)) {
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
		isOpenDoor=true;
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
		
		if (CarparkUtils.checkRoadType(device,model,presenter,DeviceRoadTypeEnum.储值车通道,DeviceRoadTypeEnum.临时车通道)) {
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
		if (date.after(new DateTime(user.getValidTo()).plusDays(user.getDelayDays() == null ? 0 : user.getDelayDays()).toDate())) {
			if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期变临时车).equals("true")) {
				content="车辆已过期"+content;
				return tempCarShowToDevice(false);
			}
			if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期所属停车场限制).equals("true")) {
				LOGGER.info("固定车到期，固定车到期所属停车场限制:{}。判断是否进入所属停车场",true);
				if (device.getCarpark().equals(user.getCarpark())) {
					content = "月租车辆," + CAR_IN_MSG + ",车辆已过期,请及时续费";
					isOpenDoor=true;
					return false;
				}
			}
			presenter.showContentToDevice(device, "车辆已过期,请联系管理员", false);
			return true;
		}
		if (StrUtil.isEmpty(cch.getId())) {
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
					return tempCarShowToDevice(false);
				}
			} else {
				if (Integer.valueOf(user.getCarparkNo()) <= list.size()) {
					LOGGER.info("固定车车位满作临时车计费设置为{}，用户车位为{}，场内车辆为{}，不允许进入", valueOf2, user.getCarparkNo(), list.size());
					return true;
				}
			}
		}

		// int parseInt = Integer.parseInt(StrUtil.isEmpty(user.getCarparkNo())?"0":user.getCarparkNo());
		String fixCarInMsg = model.getMapVoice().get(DeviceVoiceTypeEnum.固定车进场语音).getContent();
		Date date2 = new DateTime(user.getValidTo()).minusDays(user.getRemindDays() == null ? 0 : user.getRemindDays()).toDate();
		if (StrUtil.getTodayBottomTime(date2).before(date)) {
			content = fixCarInMsg + ",剩余" + CarparkUtils.countDayByBetweenTime(date, user.getValidTo()) + "天";
			isOpenDoor=true;
			LOGGER.info("固定车：{}，{}", plateNO, content);
		} else {
			content = fixCarInMsg;
			isOpenDoor=true;
			LOGGER.info("固定车：{}，{}", plateNO, content);
		}
		Integer carparkSlot = user.getCarparkSlot();
		if (user.getCarparkSlotType().equals(CarparkSlotTypeEnum.固定车位)) {
			LOGGER.info("车辆{}用户固定车位{},不计算车位",cch.getPlateNo(),carparkSlot);
			cch.setIsCountSlot(false);
		}else{
			cch.setIsCountSlot(true);
		}
		return false;
	}

	/**
	 * 
	 */
	public void refreshUserAndHistory() {
//		editPlateNo = model.getInShowPlateNO();
		presenter.showPlateNOToDevice(device, editPlateNo);
		if (!editPlateNo.equals(plateNO)) {
			user = sp.getCarparkUserService().findUserByPlateNo(editPlateNo, device.getCarpark().getId());
			String plateNO=editPlateNo==null?this.plateNO:editPlateNo;
			if (plateNO.length()<6) {
				user=null;
			}
			initInOutHistory(device);
		}
	}

	/**
	 *初始化进场记录
	 * @param device
	 */
	public void initInOutHistory(SingleCarparkDevice device) {
		List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(editPlateNo, device.getCarpark());
		cch = StrUtil.isEmpty(findByNoOut)?null:findByNoOut.get(0);
		if (StrUtil.isEmpty(cch)) {
			cch = new SingleCarparkInOutHistory();
		}
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

	public SingleCarparkUser getUser() {
		return user;
	}

	public void setUser(SingleCarparkUser user) {
		this.user = user;
	}

	public SingleCarparkInOutHistory getCch() {
		return cch;
	}

	public void setCch(SingleCarparkInOutHistory cch) {
		this.cch = cch;
	}

	public SingleCarparkDevice getDevice() {
		return device;
	}

	public CarparkDatabaseServiceProvider getSp() {
		return sp;
	}

	public String getBigImgSavePath() {
		return imageSavefolder + "/" + bigImgFileName;
	}

	public String getEditPlateNo() {
		return editPlateNo;
	}

	public void setEditPlateNo(String editPlateNo) {
		this.editPlateNo = editPlateNo;
	}
}
