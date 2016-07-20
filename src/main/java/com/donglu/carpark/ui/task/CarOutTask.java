package com.donglu.carpark.ui.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ImageUtils;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
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
	
	public static Map<String, String> mapTempCharge;
	
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
			model.setDisContinue(false);
			model.setHandSearch(false);
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
				model.setSearchPlateNo(plateNO);
				model.setSearchBigImage(bigImgFileName);
				model.setSearchSmallImage(smallImgFileName);
				model.setHandSearch(true);
				model.setOutPlateNOEditable(true);
				model.setOutShowPlateNO("-无牌车");
				return;
			}
			presenter.saveImage(smallImgFileName, bigImgFileName, smallImage, bigImage);
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
		}
	
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
			presenter.updatePosition(carpark, user.getId(), false);
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
			presenter.showContentToDevice(device, "车辆已锁", false);
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
			presenter.showContentToDevice(device, haveNoMoney, false);
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
			presenter.showContentToDevice(device, CarparkUtils.formatFloatString("请缴费"+calculateTempCharge+"元,"+haveNoMoney), false);
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
	private void notFindInHistory() {
		LOGGER.info("没有找到车牌{}的入场记录", plateNO);
		presenter.showPlateNOToDevice(device, plateNO);
		presenter.showContentToDevice(device, "此车未入场", false);
		model.setOutShowPlateNO(model.getOutShowPlateNO()+"-未入场");
		model.setSearchPlateNo(plateNO);
		model.setSearchBigImage(bigImgFileName);
		model.setSearchSmallImage(smallImgFileName);
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
	 * @param check 
	 * @param roadType
	 * @param equals
	 * @param bigImg
	 * @param smallImg
	 * @return 返回true终止操作
	 */
	private boolean fixCarOutProcess(boolean check) throws Exception {
		carType = "固定车";
		logger.info("固定车出场");
		if (check) {
			model.setOutShowPlateNO(model.getOutShowPlateNO()+"-固定车");
			if (CarparkUtils.checkRoadType(device, model, presenter, DeviceRoadTypeEnum.临时车通道,DeviceRoadTypeEnum.储值车通道)) {
				return true;
			}
			// 固定车出场确认
			Boolean valueOf = Boolean
					.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认) == null ? SystemSettingTypeEnum.固定车出场确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认));
			if (valueOf) {
				presenter.showContentToDevice(device, "固定车等待确认", false);
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
			if (cch!=null||cch.getReviseInTime()!=null) {
				tempCarOutProcess(cch.getReviseInTime());
				return true;
			}
			if (Boolean.valueOf(getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期变临时车))) {
				tempCarOutProcess(d);
				return true;
			} else if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期所属停车场限制).equals("true")) {
				if (device.getCarpark().equals(user.getCarpark())) {
					presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent() + StrUtil.formatDate(user.getValidTo(), ConstUtil.VILIDTO_DATE), true);
				}
			}
			presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.固定车到期语音).getContent() + StrUtil.formatDate(user.getValidTo(), ConstUtil.VILIDTO_DATE), false);
			model.setOutShowPlateNO(model.getOutShowPlateNO()+"-已过期");
			return true;
		}
		//车位判断
		if (cch!=null&&!StrUtil.isEmpty(cch.getReviseInTime())&&!cch.getIsOverdue()) {
			LOGGER.info("固定车做临时车计费：{}",user.getTempCarTime());
			tempCarOutProcess(cch.getReviseInTime());
			model.setUser(user);
			return true;
		}
		boolean fixCarStillCharge = CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车非所属停车场停留收费).equals("true")&&!device.getCarpark().equals(user.getCarpark());
		model.setPlateNo(editPlateNo);
		model.setCarType(carType);
		model.setOutTime(date);
		model.setShouldMony(0);
		model.setReal(0);
		// 未找到入场记录
		if (StrUtil.isEmpty(cch)) {
			logger.warn("为找到固定车{}的入场记录",editPlateNo);
			cch=new SingleCarparkInOutHistory();
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
				presenter.showContentToDevice(device, "请缴费"+shouldMoney+"元", false);
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
			presenter.showContentToDevice(device, content + ",剩余" + CarparkUtils.countDayByBetweenTime(date, user.getValidTo()) + "天", true);
			LOGGER.info("车辆:{}即将到期", editPlateNo);
		} else {
			presenter.showContentToDevice(device, content, true);
		}
		saveOutHistory();
		sp.getCarparkInOutService().updateCarparkStillTime(device.getCarpark(), device, plateNO, bigImgFileName);
		model.setBtnClick(false);
		return false;
	}

	/**
	 * @return
	 */
	private String getSettingValue(Map<SystemSettingTypeEnum,String> map,SystemSettingTypeEnum type) {
		return map.get(type)==null?type.getDefaultValue():mapSystemSetting.get(type);
	}
	private boolean visitorCarOut(){
		SingleCarparkVisitor visitor = sp.getCarparkService().findVisitorByPlateAndCarpark(plateNO, carpark);
		if (visitor==null||visitor.getStatus().equals(VisitorStatus.不可用.name())) {
			return true;
		}
		boolean flag=false;
		Date validTo = visitor.getValidTo();
		if (validTo!=null) {
			if (validTo.before(date)) {
				flag= true;
			}
		}else{
			Integer allIn = visitor.getAllIn();
			int inCount = visitor.getInCount();
			if (allIn!=null&&allIn>0) {
				if(allIn<=inCount){
					flag=true;
				}
			}
		}
		if (flag) {
			visitor.setStatus(VisitorStatus.不可用.name());
			sp.getCarparkService().saveVisitor(visitor);
		}
		if (cch==null) {
			cch=new SingleCarparkInOutHistory();
			model.setTotalTime("未入场");
		}else{
			model.setTotalTime(StrUtil.MinusTime2(cch.getInTime(), date));
		}
		model.setPlateNo(plateNO);
		model.setCarType("访客车");
		model.setInTime(cch.getInTime());
		model.setOutTime(date);
		cch.setPlateNo(plateNO);
		cch.setCarType("临时车");
		cch.setRemarkString("访客车");
		saveOutHistory();
		presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
		model.setOutShowPlateNO(model.getOutShowPlateNO()+"-访客车");
		presenter.updatePosition(carpark, null, false);
		return false;
	}
	public void refreshUserAndHistory(boolean isNew) {
		if (isNew||!editPlateNo.equals(plateNO)) {
			user = sp.getCarparkUserService().findUserByPlateNo(editPlateNo,device.getCarpark().getId());
			List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(editPlateNo,carpark);
			LOGGER.debug("查找固定用户为{}",user);
			cch = StrUtil.isEmpty(findByNoOut)?null:findByNoOut.get(0);
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
			singleCarparkInOutHistory.setOutPhotographType("自动");
			singleCarparkInOutHistory.setOutBigImg(bigImgFileName);
			singleCarparkInOutHistory.setOutSmallImg(smallImgFileName);
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
				presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
			} else {
				CarTypeEnum carType = CarTypeEnum.SmallCar;
				if (mapTempCharge.keySet().size() > 1) {
					model.setComboCarTypeEnable(true);
					model.setSelectCarType(true);
					model.setCarparkCarType("请选择车型");
					Boolean autoSelectCarType = Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.自动识别出场车辆类型));
					if (!autoSelectCarType&&!model.isBtnClick()) {
						model.setChargeDevice(device);
						model.setChargeHistory(singleCarparkInOutHistory);
						model.setBtnClick(true);
						return;
					}
					//自动识别大车小车
					if (autoSelectCarType) {
						if (!StrUtil.isEmpty(model.getOutPlateNOColor())) {
							boolean b = !StrUtil.isEmpty(mapTempCharge.get("大车"));
							if (model.getOutPlateNOColor().equals("黄")&&b) {
								model.setCarparkCarType("大车");
							}else{
								model.setCarparkCarType("小车");
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
					singleCarparkInOutHistory.setBigImg(bigImgFileName);
					singleCarparkInOutHistory.setSmallImg(smallImgFileName);
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

				String property = System.getProperty(ConstUtil.TEMP_CAR_AUTO_PASS);
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
				model.setPlateNo(plateNO);
				model.setInTime(null);
				model.setOutTime(date);
				model.setCarType("临时车");
				model.setTotalTime("未入场");
				sp.getCarparkInOutService().saveInOutHistory(io);
				LOGGER.info("保存车辆{}的出场记录成功",plateNO);
				presenter.showPlateNOToDevice(device, plateNO);
				presenter.updatePosition(carpark, null, false);
				presenter.showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
			}else{
				notFindInHistory();
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
