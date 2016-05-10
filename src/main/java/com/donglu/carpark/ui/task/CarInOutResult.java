package com.donglu.carpark.ui.task;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkMainPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.plateDevice.PlateNOResult;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CarInOutResult implements PlateNOResult {
	private static final Logger logger = LoggerFactory.getLogger(CarInOutResult.class);
	private CarparkMainPresenter presenter;
	private CarparkMainModel model;
	private CarparkDatabaseServiceProvider sp;
	private Map<String, Timer> mapTwoChanelTimer;
	private Map<SystemSettingTypeEnum, String> mapSystemSetting;
	private Map<String, Boolean> mapOpenDoor;
	private Map<String, CarInTask> mapInTwoCameraTask;
	private Map<String, CarOutTask> mapOutTwoCameraTask;
	private Map<String, Boolean> mapIsTwoChanel;
	
	private ExecutorService outTheadPool;
	private ExecutorService inThreadPool;
	private ScheduledExecutorService carOutService;
	
	
	@Inject
	public CarInOutResult(CarparkMainPresenter presenter, CarparkMainModel model,CarparkDatabaseServiceProvider sp) {
		this.presenter = presenter;
		this.model = model;
		this.sp=sp;
		mapSystemSetting = model.getMapSystemSetting();
		mapOpenDoor = model.getMapOpenDoor();
		mapInTwoCameraTask = model.getMapInTwoCameraTask();
		mapOutTwoCameraTask = model.getMapOutTwoCameraTask();
		mapIsTwoChanel = model.getMapIsTwoChanel();
		mapTwoChanelTimer = model.getMapTwoChanelTimer();
		outTheadPool = Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("出场任务"));
		inThreadPool = Executors.newCachedThreadPool(ThreadUtil.createThreadFactory("进场任务"));
		autoCheckCarOut();
	}

	@Override
	public void invok(String ip, int channel, String plateNO, byte[] bigImage, byte[] smallImage, float rightSize) {
		model.setPlateInTime(new Date(),10);
		logger.info("车辆{}在设备{}通道{}处进场,可信度：{}", plateNO, ip, channel, rightSize);
		try {
			Preconditions.checkNotNull(model.getMapDeviceType().get(ip), "not monitor device:" + ip);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		Map<String, SingleCarparkDevice> mapIpToDevice = model.getMapIpToDevice();
		if (model.getIsOpenFleet()) {
			logger.info("车队模式，保存车牌{}的进场记录到操作员日志",plateNO);
			presenter.saveFleetInOutHistory(mapIpToDevice.get(ip),plateNO,bigImage);
			return;
		}
		// 开闸
		Boolean boolean1 = mapOpenDoor.get(ip);
		if (boolean1 != null && boolean1) {
			mapOpenDoor.put(ip, null);
			boolean inOrOut = true;
			if (model.getMapDeviceType().get(ip).indexOf("出口")>-1) {
				inOrOut = false;
			}
			presenter.saveOpenDoor(mapIpToDevice.get(ip), bigImage, plateNO, inOrOut);
			return;
		}

		boolean equals = (mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔) == null ? SystemSettingTypeEnum.双摄像头识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔))
				.equals(SystemSettingTypeEnum.双摄像头识别间隔.getDefaultValue());
		String linkAddress = mapIpToDevice.get(ip).getLinkInfo();

		Boolean isTwoChanel = mapIsTwoChanel.get(linkAddress);
		if (model.getMapDeviceType().get(ip).indexOf("出口")>-1) {
			// 是否是双摄像头
			if (!equals && isTwoChanel) {
				CarOutTask carOutTask = mapOutTwoCameraTask.get(linkAddress);
				if (!StrUtil.isEmpty(carOutTask)) {
					if (carOutTask.getRightSize() < rightSize) {
						carOutTask.setBigImage(bigImage);
						carOutTask.setPlateNO(plateNO);
						carOutTask.setSmallImage(smallImage);
						carOutTask.setIp(ip);
						carOutTask.setRightSize(rightSize);
					}
					Timer timer = mapTwoChanelTimer.get(linkAddress);
					if (timer != null) {
						timer.cancel();
						outTaskSubmit(ip, plateNO, linkAddress, carOutTask);
						mapOutTwoCameraTask.remove(linkAddress);
						mapTwoChanelTimer.remove(linkAddress);
					}
					return;
				} else {
					Integer two = Integer.valueOf(
							mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔) == null ? SystemSettingTypeEnum.双摄像头识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔));
					Timer t = new Timer();
					long nanoTime = System.nanoTime();
					t.schedule(new TimerTask() {
						public void run() {
							logger.info("双摄像头等待超时任务处理：{},{}",two,System.nanoTime()-nanoTime);
							CarOutTask carOutTask = mapOutTwoCameraTask.get(linkAddress);
							outTaskSubmit(ip, plateNO, linkAddress, carOutTask);
							mapOutTwoCameraTask.remove(linkAddress);
							Timer timer = mapTwoChanelTimer.get(linkAddress);
							if (timer != null) {
								timer.cancel();
								mapTwoChanelTimer.remove(linkAddress);
							}
						}
					}, two);
					mapTwoChanelTimer.put(linkAddress, t);
				}
			}
			if (model.getListOutTask().size() > 5) {
				logger.info("已经有5个任务正在等待处理暂不添加任务{}", model.getListOutTask());
				return;
			}
			CarOutTask task = new CarOutTask(ip, plateNO, bigImage, smallImage, model, sp, presenter, rightSize);
			mapOutTwoCameraTask.put(linkAddress, task);
			if (!(!equals && isTwoChanel)) {
				outTaskSubmit(ip, plateNO, linkAddress, task);
			}

		} else if (model.getMapDeviceType().get(ip).indexOf("进口")>-1) {
			if (!equals && isTwoChanel) {
				CarInTask carInTask = mapInTwoCameraTask.get(linkAddress);
				if (!StrUtil.isEmpty(carInTask)) {
					if (carInTask.getRightSize() < rightSize) {
						carInTask.setBigImage(bigImage);
						carInTask.setPlateNO(plateNO);
						carInTask.setSmallImage(smallImage);
						carInTask.setIp(ip);
						carInTask.setRightSize(rightSize);
					}
					Timer timer = mapTwoChanelTimer.get(linkAddress);
					if (timer != null) {
						mapInTwoCameraTask.remove(linkAddress);
						timer.cancel();
						inThreadPool.submit(carInTask);
						mapTwoChanelTimer.remove(linkAddress);
					}
					return;
				} else {
					Integer two = Integer.valueOf(
							mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔) == null ? SystemSettingTypeEnum.双摄像头识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔));
					Timer t = new Timer();
					t.schedule(new TimerTask() {
						public void run() {
							logger.info("双摄像头等待超时任务处理：{}",two);
							CarInTask carInTask = mapInTwoCameraTask.get(linkAddress);
							inThreadPool.submit(carInTask);
							mapInTwoCameraTask.remove(linkAddress);
							Timer timer = mapTwoChanelTimer.get(linkAddress);
							if (timer != null) {
								timer.cancel();
								mapTwoChanelTimer.remove(linkAddress);
							}
						}
					}, two);
					mapTwoChanelTimer.put(linkAddress, t);
				}
			}
			CarInTask task = new CarInTask(ip, plateNO, bigImage, smallImage, model, sp, presenter, rightSize);
			if (!(!equals && isTwoChanel)) {
				inThreadPool.submit(task);
			}
			mapInTwoCameraTask.put(linkAddress, task);
		}
	
	}

	@Override
	public void invok(String ip, int channel, String plateNO, byte[] bigImage, byte[] smallImage, float rightSize, String plateColor) {
		try {
			model.setOutPlateNOColor(plateColor);
			invok(ip, channel, plateNO, bigImage, smallImage, rightSize);
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	/**
	 * @param ip
	 * @param plateNO
	 * @param linkAddress
	 * @param carOutTask
	 */
	public void outTaskSubmit(final String ip, final String plateNO, String linkAddress, CarOutTask carOutTask) {
		model.getListOutTask().add(carOutTask);
	}
	private void autoCheckCarOut() {
		carOutService = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("没50毫秒判断是否有车出场"));
		carOutService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					boolean b = !model.isBtnClick();
					if ((b||new Date().getTime()-model.getLastCarOutTime()>120000)&&model.getListOutTask().size()>0) {
						CarOutTask remove = model.getListOutTask().remove(0);
						logger.info("检测到出场任务：{}",remove);
						outTheadPool.submit(remove);
					}
				} catch (Exception e) {
					logger.error("车辆出场服务发生异常",e);
				}
			}
		}, 5000, 50, TimeUnit.MILLISECONDS);
	}
}
