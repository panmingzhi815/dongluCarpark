package com.donglu.carpark.ui;

import java.awt.Canvas;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.model.ShowInOutHistoryModel;
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.service.CountTempCarChargeI;
import com.donglu.carpark.service.impl.CountTempCarChargeImpl;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.ui.view.InOutHistoryPresenter;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.ui.wizard.AddDeviceModel;
import com.donglu.carpark.ui.wizard.AddDeviceWizard;
import com.donglu.carpark.ui.wizard.ChangeUserWizard;
import com.donglu.carpark.ui.wizard.InOutHistoryDetailWizard;
import com.donglu.carpark.ui.wizard.ReturnAccountWizard;
import com.donglu.carpark.ui.wizard.SearchHistoryByHandWizard;
import com.donglu.carpark.ui.wizard.model.ChangeUserModel;
import com.donglu.carpark.ui.wizard.model.ReturnAccountModel;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ImgCompress;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.LPRInOutType;
import com.dongluhitec.card.domain.LinkProtocolEnum;
import com.dongluhitec.card.domain.LinkTypeEnum;
import com.dongluhitec.card.domain.db.Device;
import com.dongluhitec.card.domain.db.Link;
import com.dongluhitec.card.domain.db.LinkStyleEnum;
import com.dongluhitec.card.domain.db.SerialDeviceAddress;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.device.WebCameraDevice;
import com.dongluhitec.card.hardware.service.BasicHardwareService;
import com.dongluhitec.card.hardware.xinluwei.XinlutongJNA;
import com.dongluhitec.card.mapper.BeanUtil;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CarparkMainPresenter {
	private static final String COUNT_TEMP_CAR_CHARGE = "countTempCarCharge";
	private Logger log = LoggerFactory.getLogger(CarparkMainPresenter.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private XinlutongJNA xinlutongJNA;

	@Inject
	private WebCameraDevice webCameraDevice;
	@Inject
	private BasicHardwareService hardwareService;

	@Inject
	private InOutHistoryPresenter inOutHistoryPresenter;
	@Inject
	private SearchErrorCarPresenter searchErrorCarPresenter;

	// 保存设备的进出口信息
	Map<String, String> mapDeviceType = CarparkMainApp.mapDeviceType;

	// 保存设备的界面信息
	Map<CTabItem, String> mapDeviceTabItem = CarparkMainApp.mapDeviceTabItem;
	// 保存设备的信息
	Map<String, SingleCarparkDevice> mapIpToDevice = CarparkMainApp.mapIpToDevice;
	// 保存设置信息
	private Map<SystemSettingTypeEnum, String> mapSystemSetting = CarparkMainApp.mapSystemSetting;

	private CarparkMainModel model;
	// 收费计算类
	private CountTempCarChargeI countTempCarCharge;

	private CarparkMainApp view;

	private App app;

	private ExecutorService saveImageTheadPool;
	private ExecutorService openDoorTheadPool;

	/**
	 * 删除一个设备tab页
	 * 
	 * @param selection
	 */
	public void deleteDeviceTabItem(CTabItem selection) {
		if (selection != null) {
			String ip = mapDeviceTabItem.get(selection);
			System.out.println("删除设备" + ip);
			selection.dispose();
			xinlutongJNA.closeEx(ip);
			mapDeviceTabItem.remove(selection);
			mapDeviceType.remove(ip);
			mapIpToDevice.remove(ip);
			if (mapIpToDevice.keySet().size() <= 0) {
				model.setCarpark(null);
			}
			CarparkFileUtils.writeObject("mapIpToDevice", mapIpToDevice);
			setIsTwoChanel();
		}
	}

	/**
	 * 弹窗添加设备
	 * 
	 * @param tabFolder
	 * @param type
	 */
	public void addDevice(CTabFolder tabFolder, String type) {
		try {
			if (tabFolder.getItems().length >= 4) {
				commonui.info("提示", type + "最多只能添加4个设备");
				return;
			}
			AddDeviceModel model = new AddDeviceModel();
			List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();

			if (!StrUtil.isEmpty(this.model.getCarpark())) {
				findAllCarpark = sp.getCarparkService().findSameCarpark(this.model.getCarpark());
			}
			if (StrUtil.isEmpty(findAllCarpark)) {
				commonui.info("提示", "请先添加停车场");
				return;
			}
			model.setList(findAllCarpark);
			model.setCarpark(findAllCarpark.get(0));
			model.setType("tcp");
			AddDeviceWizard v = new AddDeviceWizard(model);

			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			String ip = showWizard.getIp();
			String name = showWizard.getName();
			showWizard.setInType(type);
			SingleCarparkDevice device = showWizard.getDevice();
			this.model.setCarpark(device.getCarpark());
			addDevice(device);
			addDevice(tabFolder, type, ip, name);
			showUsualContentToDevice(device);
		} catch (Exception e) {
			log.error("添加设备时发生错误", e);
		}
	}

	void addDevice(SingleCarparkDevice device) throws Exception {
		String ip = device.getIp();
		SingleCarparkDevice singleCarparkDevice = mapIpToDevice.get(ip);
		if (!StrUtil.isEmpty(singleCarparkDevice)) {
			commonui.error("提示", "ip" + ip + "的设备已存在");
			// throw new Exception("ip" + ip + "的设备已存在");
		}
		mapIpToDevice.put(ip, device);
		setIsTwoChanel();
		CarparkFileUtils.writeObject("mapIpToDevice", mapIpToDevice);
		// sendPositionToAllDevice(true);
	}

	public void setIsTwoChanel() {
		CarparkMainApp.mapIsTwoChanel.clear();
		Map<String, SingleCarparkDevice> map = new HashMap<>();
		Collection<SingleCarparkDevice> values = mapIpToDevice.values();
		for (SingleCarparkDevice singleCarparkDevice : values) {
			String linkAddress = singleCarparkDevice.getLinkAddress() + singleCarparkDevice.getAddress();
			SingleCarparkDevice singleCarparkDevice2 = map.get(linkAddress);
			if (StrUtil.isEmpty(singleCarparkDevice2)) {
				map.put(linkAddress, singleCarparkDevice);
				CarparkMainApp.mapIsTwoChanel.put(linkAddress, false);
			} else {
				CarparkMainApp.mapIsTwoChanel.put(linkAddress, true);
			}
		}
		log.info("双摄像头信息：{}", CarparkMainApp.mapIsTwoChanel);
	}

	/**
	 * 
	 */
	public void sendPositionToAllDevice(boolean isreturn) {
		Set<String> keySet = mapIpToDevice.keySet();
		for (String c : keySet) {
			if (isreturn) {
				showPositionToDevice(mapIpToDevice.get(c), model.getTotalSlot());
			} else {
				showPositionToDeviceNoReturn(mapIpToDevice.get(c), model.getTotalSlot());
			}
		}
	}

	/**
	 * 普通添加设备
	 * 
	 * @param tabFolder
	 * @param type
	 * @param ip
	 * @param name
	 */
	public void addDevice(CTabFolder tabFolder, String type, String ip, String name) {
		if (mapDeviceType.get(ip) != null) {
			commonui.error("添加失败", "设备" + ip + "已存在");
			// return;
		}
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		tabItem.setText(name);
		Composite composite = new Composite(tabFolder, SWT.BORDER | SWT.EMBEDDED);
		tabItem.setControl(composite);
		composite.setLayout(new FillLayout());
		createCamera(ip, composite);
		tabFolder.setSelection(tabItem);
		mapDeviceTabItem.put(tabItem, ip);
		mapDeviceType.put(ip, type);
	}

	Map<String, MediaPlayer> mapPlayer = Maps.newHashMap();
	private Map<String, Integer> mapDeviceFailInfo = new HashMap<>();

	private void checkPlayerPlaying() {
		// ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("每分钟检测摄像机连接状态"));
		// newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
		//
		// @Override
		// public void run() {
		// for (String url : mapPlayer.keySet()) {
		// MediaPlayer mediaPlayer = mapPlayer.get(url);
		// if (!mediaPlayer.isPlaying()) {
		// LOGGER.info("设备连接{}已断开", url);
		// mediaPlayer.playMedia(url);
		// }
		// }
		// }
		// }, 60, 60, TimeUnit.SECONDS);
	}

	/**
	 * 创建出口监控
	 * 
	 * @param ip
	 * @param northCamera
	 * 
	 */
	public void createCamera(String ip, Composite northCamera) {
		Frame new_Frame1 = SWT_AWT.new_Frame(northCamera);
		Canvas canvas1 = new Canvas();
		new_Frame1.add(canvas1);
		new_Frame1.pack();
		new_Frame1.setVisible(true);
		final String url = "rtsp://" + ip + ":554/h264ESVideoTest";
		final EmbeddedMediaPlayer createPlayRight = webCameraDevice.createPlay(new_Frame1, url);
		mapPlayer.put(url, createPlayRight);
		createPlayRight.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				new Runnable() {
					public void run() {
						while (!mediaPlayer.isPlaying()) {
							log.info("设备连接{}已断开", url);
							mediaPlayer.playMedia(url);
							Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
						}
					}
				}.run();
			}
		});

		getView().shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
			}
		});
		northCamera.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
			}
		});
		xinlutongJNA.openEx(ip, getView());
	}

	/**
	 * @param type
	 * @param tabFolder
	 * 
	 */
	public void editDevice(CTabFolder tabFolder, String type) {
		try {
			CTabItem selection = tabFolder.getSelection();
			if (StrUtil.isEmpty(selection)) {
				return;
			}
			String oldIp = mapDeviceTabItem.get(selection);
			SingleCarparkDevice device = mapIpToDevice.get(oldIp);
			AddDeviceModel model = new AddDeviceModel();
			model.setDevice(device);
			AddDeviceWizard v = new AddDeviceWizard(model);
			List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();
			if (!StrUtil.isEmpty(this.model.getCarpark()) && mapIpToDevice.keySet().size() > 1) {
				findAllCarpark = sp.getCarparkService().findSameCarpark(this.model.getCarpark());
			}
			model.setList(findAllCarpark);
			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			String ip = showWizard.getIp();

			SingleCarparkDevice device2 = showWizard.getDevice();
			device2.setCarpark(sp.getCarparkService().findCarparkById(device2.getCarpark().getId()));
			if (ip.equals(oldIp)) {
				selection.setText(showWizard.getName());
				mapIpToDevice.put(ip, device2);
				CarparkFileUtils.writeObject("mapIpToDevice", mapIpToDevice);
				commonui.info("修改成功", "修改设备" + ip + "成功");
				log.info("发送平时显示类容");
				showUsualContentToDevice(device2);
				setIsTwoChanel();
				// sendPositionToAllDevice(true);
				return;
			} else {
				if (mapDeviceType.get(ip) != null) {
					commonui.error("修改失败", "设备" + ip + "已存在");
					// return;
				}
				deleteDeviceTabItem(selection);
				addDevice(device2);
				addDevice(tabFolder, type, ip, showWizard.getName());
				this.model.setCarpark(device2.getCarpark());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {

		}
	}

	public CarparkMainApp getView() {
		return view;
	}

	public void setView(CarparkMainApp view) {
		this.view = view;
	}

	public CarparkMainModel getModel() {
		return model;
	}

	public void setModel(CarparkMainModel model) {
		this.model = model;
	}

	/**
	 * 归账
	 */
	public void returnAccount() {
		try {
			ReturnAccountModel model = new ReturnAccountModel();
			String userName = this.model.getUserName();
			model.setReturnUser(userName);
			CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
			List<SingleCarparkInOutHistory> listFact = carparkInOutService.findHistoryFactMoneyNotReturn(userName);
			List<SingleCarparkInOutHistory> listFree = carparkInOutService.findHistoryFreeMoneyNotReturn(userName);
			float factMoney = 0;
			float freeMoney = 0;
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFact) {
				Float factMoney2 = singleCarparkInOutHistory.getFactMoney();
				if (StrUtil.isEmpty(factMoney2)) {
					factMoney2 = 0F;
				}
				factMoney += factMoney2;
			}
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFree) {
				Float factMoney2 = singleCarparkInOutHistory.getFreeMoney();
				if (StrUtil.isEmpty(factMoney2)) {
					factMoney2 = 0F;
				}
				freeMoney += factMoney2;
			}
			model.setShouldReturn(factMoney);
			model.setFactReturn(freeMoney);
			ReturnAccountWizard wizard = new ReturnAccountWizard(model, sp);
			ReturnAccountModel m = (ReturnAccountModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			SingleCarparkReturnAccount a = new SingleCarparkReturnAccount();
			BeanUtil.copyProperties(m, a, "returnUser", "factReturn", "shouldReturn", "operaName");
			a.setReturnTime(new Date());
			if (model.isFree()) {
				a.setFactReturn(model.getFactReturn());
			} else {
				if (a.getShouldReturn() <= 0) {
					return;
				}
				a.setFactReturn(0);
			}

			Long saveReturnAccount = sp.getCarparkService().saveReturnAccount(a);

			Map<Long, Long> map = new HashMap<Long, Long>();
			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFact) {
				singleCarparkInOutHistory.setReturnAccount(saveReturnAccount);
				map.put(singleCarparkInOutHistory.getId(), saveReturnAccount);
			}

			carparkInOutService.saveInOutHistoryOfList(listFact);
			if (model.isFree()) {
				for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFree) {
					if (!StrUtil.isEmpty(map.get(singleCarparkInOutHistory.getId()))) {
						singleCarparkInOutHistory.setReturnAccount(saveReturnAccount);
					}
					singleCarparkInOutHistory.setFreeReturnAccount(saveReturnAccount);
					// singleCarparkInOutHistory.setOperaName(model.getOperaName());
				}
				carparkInOutService.saveInOutHistoryOfList(listFree);
			}
			this.model.setTotalCharge(carparkInOutService.findFactMoneyByName(userName));
			this.model.setTotalFree(carparkInOutService.findFreeMoneyByName(userName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示屏显示车牌
	 * 
	 * @param device
	 *            设备
	 * @param plateNO
	 *            显示车牌
	 */
	public boolean showPlateNOToDevice(SingleCarparkDevice device, String plateNO) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Device d = getDevice(device);
			Boolean carparkPlate = hardwareService.carparkPlate(d, plateNO);
			return carparkPlate;
		} catch (Exception e) {
			setDeviceLinkFail(device);
			return false;
		}
	}

	/**
	 * 检查设备是否失败过长
	 * 
	 * @param device
	 * @return
	 */
	private boolean checkDeviceLinkStatus(SingleCarparkDevice device) {
		return mapDeviceFailInfo.get(device.getLinkInfo()) != null && mapDeviceFailInfo.get(device.getLinkInfo()) > 10;
	}

	/**
	 * 设置设备连接失败信息
	 * 
	 * @param device
	 */
	private void setDeviceLinkFail(SingleCarparkDevice device) {
		String key = device.getLinkInfo();
		Integer num = mapDeviceFailInfo.get(key);
		if (StrUtil.isEmpty(num)) {
			mapDeviceFailInfo.put(key, 0);
		} else {
			mapDeviceFailInfo.put(key, num++);
		}
	}

	/**
	 * 发送语音
	 * 
	 * @param device
	 *            设备
	 * @param content语音
	 * @param opDoor是否需要开门
	 * @return
	 */
	public boolean showContentToDevice(SingleCarparkDevice device, String content, boolean opDoor) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			if (opDoor) {
				Device d = getDevice(device);
				Boolean carparkContentVoiceAndOpenDoor = hardwareService.carparkContentVoiceAndOpenDoor(d, content, device.getVolume() == null ? 1 : device.getVolume());
				openDoorToPhotograph(device.getIp());
				return carparkContentVoiceAndOpenDoor;
			} else {
				Device d = getDevice(device);
				return hardwareService.carparkContentVoice(d, content, device.getVolume() == null ? 1 : device.getVolume());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 显示车为数
	 * 
	 * @param device
	 * @param content
	 * @param voice
	 * @return
	 */
	public boolean showPositionToDevice(SingleCarparkDevice device, int position) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Device d = getDevice(device);

			String inType = device.getInType();
			if (inType.equals("进口2")) {
				inType = "进口";
			}
			if (inType.equals("出口2")) {
				inType = "出口";
			}

			return hardwareService.carparkPosition(d, position, LPRInOutType.valueOf(inType), (byte) (device.getScreenType().getType()));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 显示车为数
	 * 
	 * @param device
	 * @param content
	 * @param voice
	 */
	public void showPositionToDeviceNoReturn(SingleCarparkDevice device, int position) {
		if (checkDeviceLinkStatus(device)) {
			return;
		}
		try {
			Device d = getDevice(device);
			hardwareService.carparkPosition(d, position);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Device getDevice(SingleCarparkDevice device) {
		Device d = new Device();
		Link link = new Link();
		link.setId((long) device.getLinkAddress().hashCode());
		link.setLinkStyleEnum(LinkStyleEnum.直连设备);
		link.setType(device.getType().equals("485") ? LinkTypeEnum.COM : LinkTypeEnum.TCP);
		link.setAddress(device.getLinkAddress());
		link.setProtocol(LinkProtocolEnum.Carpark);
		SerialDeviceAddress address = new SerialDeviceAddress();
		address.setAddress(device.getAddress());
		d.setAddress(address);
		d.setLink(link);
		return d;
	}

	/**
	 * 开门，设备开闸
	 * 
	 * @param device
	 */
	public boolean openDoor(SingleCarparkDevice device) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Boolean carparkOpenDoor = hardwareService.carparkOpenDoor(getDevice(device));
			// Boolean carparkOpenDoor = hardwareService.carparkControlDoor(getDevice(device), 0, -1, -1, -1);
			openDoorToPhotograph(device.getIp());
			return carparkOpenDoor;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设备落杆
	 * 
	 * @param singleCarparkDevice
	 */
	public boolean closeDoor(SingleCarparkDevice device) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Boolean carparkOpenDoor = hardwareService.carparkControlDoor(getDevice(device), -1, 0, -1, -1);
			return carparkOpenDoor;
		} catch (Exception e) {
			return false;
		}
	}

	public void openDoorToPhotograph(String ip) {
		xinlutongJNA.openDoor(ip);
	}

	public boolean showUsualContentToDevice(SingleCarparkDevice device) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			System.out.println("====" + device.getAdvertise().length());
			Boolean carparkUsualContent = hardwareService.carparkUsualContent(getDevice(device), device.getAdvertise());
			// showNowTimeToDevice(device);
			return carparkUsualContent;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 计算收费
	 * 
	 * @param carparkId
	 * 
	 * @param endTime
	 * @param startTime
	 * 
	 * @return
	 */
	public float countShouldMoney(Long carparkId, CarTypeEnum carType, Date startTime, Date endTime) {
		float charge = 0;
		try {
			charge = countTempCarCharge.charge(carparkId, carType, startTime, endTime, sp, model);
			System.out.println("charge===========" + charge);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return charge;
	}

	/**
	 * 换班
	 */
	public void changeUser() {
		if (model.isBtnClick()) {
			commonui.info("提示", "正在收费，请收完在换班");
			return;
		}
		ChangeUserWizard wizard = new ChangeUserWizard(new ChangeUserModel(), sp);
		ChangeUserModel showWizard = (ChangeUserModel) commonui.showWizard(wizard);
		if (StrUtil.isEmpty(showWizard)) {
			return;
		}
		SingleCarparkSystemUser systemUser = showWizard.getSystemUser();
		String userName = systemUser.getUserName();
		System.setProperty("userName", userName);
		System.setProperty("userType", systemUser.getType());

		model.setBtnClick(false);
		model.setTotalSlot(sp.getCarparkInOutService().findTotalSlotIsNow(model.getCarpark()));
		model.setUserName(userName);
		model.setWorkTime(new Date());
		model.setTotalCharge(sp.getCarparkInOutService().findShouldMoneyByName(userName));
		model.setTotalFree(sp.getCarparkInOutService().findFreeMoneyByName(userName));
		model.setMonthSlot(sp.getCarparkInOutService().findFixSlotIsNow(model.getCarpark()));
		model.setHoursSlot(sp.getCarparkInOutService().findTempSlotIsNow(model.getCarpark()));
		view.controlToolItem();
	}

	/**
	 * 打开记录查询页面
	 */
	public void showSearchInOutHistory() {

		if (app != null) {
			if (app.isOpen()) {
				app.focus();
			} else {
				app.open();
			}
			return;
		}
		ShowApp showApp = new ShowApp();
		showApp.setPresenter(inOutHistoryPresenter);
		app = showApp;
		app.open();
	}

	/**
	 * 手动抓拍
	 */
	public void handPhotograph(String ip) {
		xinlutongJNA.tigger(ip);
	}

	/**
	 * 保存车牌识别的图片
	 * 
	 * @param f
	 *            文件夹
	 * @param fileName
	 *            文件名
	 * @param bigImage
	 *            图片字节
	 */
	public void saveImage(final String f, final String fileName, final byte[] bigImage1) {

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					byte[] bigImage = bigImage1 == null ? new byte[0] : bigImage1;
					String fl = "/img/" + f;
					if (!StrUtil.isEmpty(CarparkFileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH))) {
						String string = (String) CarparkFileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
						fl = string + fl;
					}
					File file = new File(fl);
					if (!file.exists() && !file.isDirectory()) {
						Files.createParentDirs(file);
						file.mkdir();
					}
					String finalFileName = fl + "/" + fileName;
					File file2 = new File(finalFileName);
					file2.createNewFile();
					Files.write(bigImage, file2);
					String ip = CarparkClientConfig.getInstance().getDbServerIp();
					if (true) {
						long nanoTime = System.nanoTime();
						log.info("准备将图片{}上传到服务器{}", finalFileName, ip);
						try {
							String upload = FileuploadSend.upload("http://" + ip + ":8899/carparkImage/", finalFileName);

							log.info("图片上传到服务器{}成功,{}", ip, upload);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("图片上传到服务器{}失败", ip);
						} finally {
							log.info("上传图片花费时间：{}", System.nanoTime() - nanoTime);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					log.error("上传图片出错", e);
				}
			}
		};

		saveImageTheadPool.submit(runnable);
	}

	public void init() {
		saveImageTheadPool = Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("保存图片任务"));
		openDoorTheadPool = Executors.newCachedThreadPool(ThreadUtil.createThreadFactory("开门任务"));
		checkPlayerPlaying();

		countTempCarCharge = (CountTempCarChargeI) CarparkFileUtils.readObject(COUNT_TEMP_CAR_CHARGE);
		if (StrUtil.isEmpty(countTempCarCharge)) {
			countTempCarCharge = new CountTempCarChargeImpl();
			CarparkFileUtils.writeObject(COUNT_TEMP_CAR_CHARGE, countTempCarCharge);
		}
		autoCheckDeviceLinkInfo();
	}

	/**
	 * 自动检测设备的连接状态
	 */
	private void autoCheckDeviceLinkInfo() {
		Map<String, SingleCarparkDevice> map = new HashMap<>();
		for (SingleCarparkDevice d : mapIpToDevice.values()) {
			map.put(d.getLinkInfo(), d);
		}
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("自动检测设备的连接状态"));
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				for (String l : mapDeviceFailInfo.keySet()) {
					if (mapDeviceFailInfo.get(l) > 10) {
						try {
							hardwareService.setDate(getDevice(map.get(l)), new Date());
							mapDeviceFailInfo.put(l, 0);
						} catch (Exception e) {
							log.info("尝试连接设备地址{}失败", l);
						}
					}
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	/**
	 * 人工查找
	 * 
	 * @param plateNO
	 * @param smallImg
	 * @param bigImg
	 */
	public void showManualSearch(String plateNO, String bigImg, String smallImg) {
		try {

			searchErrorCarPresenter.getModel().setPlateNo(model.getOutShowPlateNO());
			searchErrorCarPresenter.getModel().setHavePlateNoSelect(null);
			searchErrorCarPresenter.getModel().setNoPlateNoSelect(null);
			searchErrorCarPresenter.getModel().setSaveBigImg(bigImg);
			searchErrorCarPresenter.getModel().setSaveSmallImg(smallImg);
			searchErrorCarPresenter.getModel().setCarpark(model.getCarpark());
			SearchHistoryByHandWizard wizard = new SearchHistoryByHandWizard(searchErrorCarPresenter);
			Object showWizard = commonui.showWizard(wizard);
			if (StrUtil.isEmpty(showWizard)) {
				return;
			}
			model.setOutPlateNOEditable(false);
			SingleCarparkInOutHistory select = searchErrorCarPresenter.getModel().getHavePlateNoSelect() == null ? searchErrorCarPresenter.getModel().getNoPlateNoSelect()
					: searchErrorCarPresenter.getModel().getHavePlateNoSelect();
			if (StrUtil.isEmpty(select)) {
				return;
			}
			SearchErrorCarModel m = searchErrorCarPresenter.getModel();
			select.setOutPlateNO(plateNO);
			if (!m.isInOrOut()) {
				select.setPlateNo(m.getPlateNo());
				// List<SingleCarparkUser> findUserByPlateNo = sp.getCarparkUserService().findUserByPlateNo(m.getPlateNo(),model.getCarpark().getId());
				// if (StrUtil.isEmpty(findUserByPlateNo)) {
				// select.setCarType("临时车");
				// } else {
				// select.setCarType("固定车");
				// }
			}
			sp.getCarparkInOutService().saveInOutHistory(select);
			view.invok(model.getIp(), 0, select.getPlateNo(), m.getBigImg(), m.getSmallImg(), 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查车牌识别间隔,现在时间在间隔时间内返回false
	 * 
	 * @param plateNO
	 */
	public boolean checkPlateNODiscernGap(Map<String, Date> mapPlateNoDate, String plateNO, Date nowDate) {
		Date date = mapPlateNoDate.get(plateNO);
		if (date != null) {
			String s = mapSystemSetting.get(SystemSettingTypeEnum.同一车牌识别间隔) == null ? SystemSettingTypeEnum.同一车牌识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.同一车牌识别间隔);
			log.info("同一车牌识别间隔为：{}", s);
			Integer timeGap = Integer.valueOf(s);
			DateTime plusSeconds = new DateTime(date).plusSeconds(timeGap);
			boolean after = plusSeconds.toDate().after(nowDate);
			if (after) {
				log.info("车牌{}在{}做过处理，暂不做处理", plateNO, StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss"));
				return false;
			}
		}
		return true;
	}

	/**
	 * 手动开闸并且保存开闸记录
	 * 
	 * @param device
	 * @param image
	 * @param plateNO
	 * @param inOrOut
	 */
	public void saveOpenDoor(final SingleCarparkDevice device, final byte[] image, final String plateNO, final boolean inOrOut) {
		Runnable runnable = new Runnable() {
			public void run() {
				Date date = new Date();
				String folder = StrUtil.formatDate(date, "yyyy/MM/dd/HH");
				String fileName = StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
				String bigImgFileName = fileName + "_" + plateNO + "_big.jpg";
				saveImage(folder, bigImgFileName, image);
				SingleCarparkOpenDoorLog openDoor = new SingleCarparkOpenDoorLog();
				openDoor.setOperaName(CarparkUtils.getUserName());
				openDoor.setOperaDate(date);
				openDoor.setImage(bigImgFileName);
				openDoor.setDeviceName(device.getName());
				sp.getCarparkInOutService().saveOpenDoorLog(openDoor);
				log.info("对设备{}，地址{}-{}开闸", device.getName(), device.getLinkAddress(), device.getAddress());
				showPlateNOToDevice(device, "");
				if (inOrOut) {
					showContentToDevice(device, CarparkMainApp.CAR_IN_MSG, false);
				} else {
					showContentToDevice(device, CarparkMainApp.CAR_OUT_MSG, false);
				}
			}
		};
		openDoorTheadPool.submit(runnable);
	}

	public void showNowTimeToDevice(SingleCarparkDevice singleCarparkDevice) {
		hardwareService.setDate(getDevice(singleCarparkDevice), new Date());
	}

	/**
	 * 进行收费
	 * 
	 * @param carOutChargeCheck
	 *            是否需要确认
	 */
	public void charge(Boolean carOutChargeCheck) {
		SingleCarparkInOutHistory data = model.getChargeHistory();
		if (StrUtil.isEmpty(data)) {
			return;
		}
		SingleCarparkDevice device = model.getChargeDevice();
		data.setFactMoney(model.getReal());
		if (!chargeCarPass(device, data, carOutChargeCheck)) {
			return;
		}
	}

	/**
	 * 进行免费
	 * 
	 * @param carOutChargeCheck
	 *            是否需要确认
	 */
	public void free(Boolean carOutChargeCheck) {
		SingleCarparkInOutHistory data = model.getChargeHistory();
		SingleCarparkDevice device = model.getChargeDevice();
		if (StrUtil.isEmpty(data) || StrUtil.isEmpty(device)) {
			return;
		}
		model.setReal(0);
		if (!chargeCarPass(device, data, carOutChargeCheck)) {
			model.setReal(model.getShouldMony());
			return;
		}
	}

	/**
	 * 收费操作
	 * 
	 * @param device
	 * @param singleCarparkInOutHistory
	 * @param check
	 * @return
	 */
	public boolean chargeCarPass(SingleCarparkDevice device, SingleCarparkInOutHistory singleCarparkInOutHistory, boolean check) {

		try {
			if (!StrUtil.isEmpty(model.getUser())) {
				SingleCarparkUser user = model.getUser();
				user.setTempCarTime(CarparkUtils.removeString(user.getTempCarTime(), model.getInTime()));
				sp.getCarparkUserService().saveUser(user);
			}
			Float shouldMoney = model.getShouldMony();
			float factMoney = model.getReal();
			if (factMoney > shouldMoney) {
				commonui.error("收费提示", "实收不能超过应收" + shouldMoney + "元");
				return false;
			}
			if (factMoney < 0) {
				commonui.error("收费提示", "实收不能小于0");
				return false;
			}
			if (check) {
				boolean confirm = commonui.confirm("收费确认", "车牌：" + singleCarparkInOutHistory.getPlateNo() + "应收：" + shouldMoney + "实收：" + factMoney);
				if (!confirm) {
					return false;
				}
			}
			log.info("车辆收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", singleCarparkInOutHistory.getPlateNo(), device.getCarpark(), model.getCarTypeEnum(), model.getInTime(), model.getOutTime(),
					model.getTotalTime(), shouldMoney);
			float freeMoney = shouldMoney - factMoney;
			singleCarparkInOutHistory.setShouldMoney(shouldMoney);
			singleCarparkInOutHistory.setFactMoney(factMoney);
			singleCarparkInOutHistory.setFreeMoney(freeMoney);
			singleCarparkInOutHistory.setCarType("临时车");
			sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
			Boolean tempCarNoChargeIsPass = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车零收费是否自动出场));
			if (tempCarNoChargeIsPass) {
				if (shouldMoney > 0) {
					showContentToDevice(device, CarparkMainApp.CAR_OUT_MSG, true);
				} else {
					showContentToDevice(device, CarparkUtils.formatFloatString("请缴费" + shouldMoney + "元") + "," + CarparkMainApp.CAR_OUT_MSG, true);
				}
			} else {
				showContentToDevice(device, CarparkMainApp.CAR_OUT_MSG, true);
			}
			if (!StrUtil.isEmpty(model.getStroeFrees())) {
				for (SingleCarparkStoreFreeHistory free : model.getStroeFrees()) {
					free.setUsed("已使用");
					sp.getStoreService().saveStoreFree(free);
				}
			}
			model.setStroeFrees(null);
			model.setBtnClick(false);
			model.setHandSearch(false);
			model.setComboCarTypeEnable(false);
			model.setChargeDevice(null);
			model.setChargeHistory(null);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void showHistory(SingleCarparkInOutHistory h) {
		try {
			if (h == null) {
				return;
			}
			Boolean valueOf = Boolean
					.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.进场允许修改车牌) == null ? SystemSettingTypeEnum.进场允许修改车牌.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.进场允许修改车牌));
			log.info("系统设置：[进场允许修改车牌]为:[{}]", valueOf);
			ShowInOutHistoryModel model = new ShowInOutHistoryModel();
			model.setInfo(h);
			model.setNowPlateNo(h.getPlateNo());
			InOutHistoryDetailWizard wizard = new InOutHistoryDetailWizard(model, valueOf, sp, commonui);
			ShowInOutHistoryModel m = (ShowInOutHistoryModel) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(m)) {
				return;
			}
			if (valueOf) {
				SingleCarparkInOutHistory findInOutById = sp.getCarparkInOutService().findInOutById(m.getId());
				if (StrUtil.isEmpty(findInOutById)) {
					return;
				}
				if (!StrUtil.isEmpty(findInOutById.getOutTime())) {
					commonui.info("修改失败", "该车已经出场");
					return;
				}
				if (StrUtil.isEmpty(m)) {
					return;
				}
				findInOutById.setPlateNo(m.getNowPlateNo());
				sp.getCarparkInOutService().saveInOutHistory(findInOutById);
				h.setPlateNo(model.getNowPlateNo());
				CarparkUtils.cleanSameInOutHistory();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void carInByHand() {
		String handPlateNO = model.getHandPlateNO();
		if (StrUtil.isEmpty(handPlateNO)) {
			return;
		}
		boolean confirm = commonui.confirm("提示", "确认让车牌" + handPlateNO + "入场");
		if (!confirm) {
			return;
		}
		SingleCarparkInOutHistory h = sp.getCarparkInOutService().findInOutHistoryByPlateNO(handPlateNO);
		if (StrUtil.isEmpty(h)) {
			h = new SingleCarparkInOutHistory();
		}
		h.setPlateNo(handPlateNO);
		h.setInPhotographType("手动");
		h.setCarparkId(model.getCarpark().getId());
		h.setCarparkName(model.getCarpark().getName());
		h.setInTime(new Date());
		Long saveInOutHistory = sp.getCarparkInOutService().saveInOutHistory(h);
		h.setId(saveInOutHistory);
		h.setOperaName(System.getProperty("userName"));
		model.addInHistorys(h);
		model.setInHistorySelect(h);
		model.setHandPlateNO(null);
	}

	public void saveImage(String f, String smallImgFileName, String bigImgFileName, byte[] smallImage1, byte[] bigImage1) {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					byte[] bigImage = bigImage1 == null ? new byte[0] : bigImage1;
					byte[] smallImage = smallImage1 == null ? new byte[0] : smallImage1;
					String fl = "/img/" + f;
					if (!StrUtil.isEmpty(CarparkFileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH))) {
						String string = (String) CarparkFileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
						fl = string + fl;
					}
					File file = new File(fl);
					if (!file.exists() && !file.isDirectory()) {
						Files.createParentDirs(file);
						file.mkdir();
					}
					String finalBigFileName = fl + "/" + bigImgFileName;
					String finalSmallFileName = fl + "/" + smallImgFileName;

					File bigFile = new File(finalBigFileName);
					bigFile.createNewFile();
					Files.write(bigImage, bigFile);

					File smallFile = new File(finalSmallFileName);
					smallFile.createNewFile();
					Files.write(smallImage, smallFile);
					ImgCompress.compress(smallFile.getPath());
					String ip = CarparkClientConfig.getInstance().getServerIp();
					if (true) {
						long nanoTime = System.nanoTime();
						log.info("准备将图片{}上传到服务器{}", finalBigFileName, ip);
						try {
							String bigUpload = FileuploadSend.upload("http://" + ip + ":8899/carparkImage/", finalBigFileName);
							String smallUpload = FileuploadSend.upload("http://" + ip + ":8899/carparkImage/", finalSmallFileName);
							log.info("图片上传到服务器{}成功,{}", ip, bigUpload + "==" + smallUpload);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("图片上传到服务器{}失败", ip);
						} finally {
							log.info("上传图片花费时间：{}", System.nanoTime() - nanoTime);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					log.error("上传图片出错", e);
				}
			}
		};
		saveImageTheadPool.submit(runnable);
	}

	/**
	 * 释放资源
	 */
	public void systemExit() {
		openDoorTheadPool.shutdownNow();
		saveImageTheadPool.shutdownNow();
	}

}
