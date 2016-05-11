package com.donglu.carpark.ui;

import java.awt.Canvas;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.model.ShowInOutHistoryModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.service.CountTempCarChargeI;
import com.donglu.carpark.service.PlateSubmitServiceI;
import com.donglu.carpark.service.impl.CountTempCarChargeImpl;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.common.ShowDialog;
import com.donglu.carpark.ui.task.CarInOutResult;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.ui.view.inouthistory.FreeReasonPresenter;
import com.donglu.carpark.ui.view.inouthistory.InOutHistoryPresenter;
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
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ImageUtils;
import com.donglu.carpark.util.ImgCompress;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.LPRInOutType;
import com.dongluhitec.card.domain.LinkProtocolEnum;
import com.dongluhitec.card.domain.LinkTypeEnum;
import com.dongluhitec.card.domain.db.Device;
import com.dongluhitec.card.domain.db.Link;
import com.dongluhitec.card.domain.db.LinkStyleEnum;
import com.dongluhitec.card.domain.db.SerialDeviceAddress;
import com.dongluhitec.card.domain.db.singlecarpark.CameraTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkStillTime;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStoreFreeHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.device.WebCameraDevice;
import com.dongluhitec.card.hardware.plateDevice.PlateNOJNA;
import com.dongluhitec.card.hardware.service.BasicHardwareService;
import com.dongluhitec.card.mapper.BeanUtil;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
@Singleton
public class CarparkMainPresenter {
	private Logger log = LoggerFactory.getLogger(CarparkMainPresenter.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	
	Map<String, PlateNOJNA> mapIpToJNA= new HashMap<>();
	
	@Inject
	private WebCameraDevice webCameraDevice;
	@Inject
	private BasicHardwareService hardwareService;

	@Inject
	private InOutHistoryPresenter inOutHistoryPresenter;
	@Inject
	private SearchErrorCarPresenter searchErrorCarPresenter;

	// 保存设备的进出口信息
	Map<String, String> mapDeviceType;

	// 保存设备的界面信息
	Map<CTabItem, String> mapDeviceTabItem;
	// 保存设备的信息
	Map<String, SingleCarparkDevice> mapIpToDevice;
	// 保存设置信息
	private Map<SystemSettingTypeEnum, String> mapSystemSetting;
	@Inject
	private CarparkMainModel model;
	// 收费计算类
	private CountTempCarChargeI countTempCarCharge;

	private CarparkMainApp view;
	@Inject
	private Provider<CarInOutResult> carInOutResultProvider;

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
			mapIpToJNA.get(ip).closeEx(ip);
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
			if (type.indexOf("出口")>-1) {
				model.setAdvertise("欢迎再次光临");
			}
			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			showWizard.setInType(type);
			SingleCarparkDevice device = showWizard.getDevice();
			this.model.setCarpark(device.getCarpark());
			addDevice(device);
			addDevice(tabFolder, type, device);
			showUsualContentToDevice(device);
			showPositionToDevice(device,this.model.getTotalSlot());
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
		// sendPositionToAllDevice(true);
	}

	public void setIsTwoChanel() {
		Map<String, Boolean> mapIsTwoChanel = model.getMapIsTwoChanel();
		mapIsTwoChanel.clear();
		Map<String, SingleCarparkDevice> map = new HashMap<>();
		Collection<SingleCarparkDevice> values = mapIpToDevice.values();
		for (SingleCarparkDevice singleCarparkDevice : values) {
			String linkAddress = singleCarparkDevice.getLinkInfo();
			SingleCarparkDevice singleCarparkDevice2 = map.get(linkAddress);
			if (StrUtil.isEmpty(singleCarparkDevice2)) {
				map.put(linkAddress, singleCarparkDevice);
				mapIsTwoChanel.put(linkAddress, false);
			} else {
				mapIsTwoChanel.put(linkAddress, true);
			}
		}
		log.info("双摄像头信息：{}", mapIsTwoChanel);
	}

	/**
	 * 给所有设备发送车位数
	 */
	public void sendPositionToAllDevice(boolean isreturn) {
		Set<String> keySet = mapIpToDevice.keySet();
		Map<String, SingleCarparkDevice> mapLinkAndDevice=new HashMap<>();
		for (String c : keySet) {
			SingleCarparkDevice singleCarparkDevice = mapIpToDevice.get(c);
			if (singleCarparkDevice.getInType().indexOf("进口")<0) {
				continue;
			}
			mapLinkAndDevice.put(singleCarparkDevice.getLinkInfo(), singleCarparkDevice);
		}
		for (SingleCarparkDevice d : mapLinkAndDevice.values()) {
			Date when = new Date();
			Date plateInTime = model.getPlateInTime();
			if (plateInTime.after(when)) {
				log.info("车辆进出场，在时间：{}后在对设备{}发车位数,现在时间：{}",plateInTime,d,when);
				return;
			}
			if (d.getInType().indexOf("进口")<0) {
				return;
			}
			if (isreturn) {
				showPositionToDevice(d, model.getTotalSlot());
			} else {
				showPositionToDeviceNoReturn(d, model.getTotalSlot());
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
	public void addDevice(CTabFolder tabFolder, String type,SingleCarparkDevice device) {
		 String ip=device.getIp();
		 String name=device.getName();
		 
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
		tabFolder.getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				createCamera(device, composite);
			}
		});
		tabFolder.setSelection(tabItem);
		mapDeviceTabItem.put(tabItem, ip);
		mapDeviceType.put(ip, type);
		CarparkFileUtils.writeObject("mapIpToDevice", mapIpToDevice);
	}

	Map<String, MediaPlayer> mapPlayer = Maps.newHashMap();
	Map<String, SingleCarparkDevice> mapCameraToDeviceIp=Maps.newHashMap();
	int checkPlayerPlayingSize=0;
	private Map<String, Integer> mapDeviceFailInfo = new HashMap<>();
	private ScheduledExecutorService checkCameraPlayStatus;

	private void checkPlayerPlaying() {
		checkCameraPlayStatus = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("每10秒检测摄像机连接状态"));
		checkCameraPlayStatus.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				log.debug("开始第{}次检查摄像机的连接状态",checkPlayerPlayingSize);
				for (String url : mapPlayer.keySet()) {
					MediaPlayer mediaPlayer = mapPlayer.get(url);
					boolean playing = mediaPlayer.isPlaying();
					if (!playing) {
						log.info("设备连接{}已断开", url);
						mediaPlayer.playMedia(url);
					}
				}
				checkPlayerPlayingSize++;
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	/**
	 * 创建出口监控
	 * 
	 * @param ip
	 * @param northCamera
	 * 
	 */
	public void createCamera(SingleCarparkDevice device, Composite northCamera) {
		String ip = device.getIp();
		PlateNOJNA jna = null;
		CameraTypeEnum cameraType = device.getCameraType() == null ? CameraTypeEnum.信路威 : device.getCameraType();
		jna = setJNA(ip, cameraType);
		Frame new_Frame1 = SWT_AWT.new_Frame(northCamera);
		Canvas canvas1 = new Canvas();
		new_Frame1.add(canvas1);
		new_Frame1.pack();
		new_Frame1.setVisible(true);

		final String url = cameraType.getRtspAddress(ip) == null ? device.getIp() : cameraType.getRtspAddress(ip);
		log.info("准备连接视频{}", url);
		final EmbeddedMediaPlayer createPlayRight = webCameraDevice.createPlay(new_Frame1, url);
		mapPlayer.put(url, createPlayRight);
		mapCameraToDeviceIp.put(url, device);

		getView().shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
			}
		});
		northCamera.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
				mapPlayer.remove(url);
			}
		});
		PopupMenu popMenu = new PopupMenu();
		MenuItem refreshItem = new MenuItem("重新播放");
		MenuItem refreshSettingItem = new MenuItem("刷新设置");
		popMenu.add(refreshItem);
		popMenu.add(refreshSettingItem);
		refreshSettingItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshSystemSetting();
			}
		});
		refreshItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createPlayRight.playMedia(url);
			}
		});
		createAutoMenuItem(popMenu);
		canvas1.add(popMenu);
		canvas1.addMouseListener(new java.awt.event.MouseAdapter() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 2) {
					String img = model.getMapCameraLastImage().get(ip);
					if (StrUtil.isEmpty(img)) {
						return;
					}
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							ImageDialog imageDialog = new ImageDialog(img);
							imageDialog.open();
						}
					});
				}
				if (e.getButton() == 3 && e.getClickCount() == 1) {
					System.out.println("打开刷新菜单");
					popMenu.show(canvas1, e.getX(), e.getY());
				}
			}

		});
		jna.openEx(ip, carInOutResultProvider.get());
	}

	protected void refreshSystemSetting() {
		// 获取设置信息设置
		List<SingleCarparkSystemSetting> findAllSystemSetting = sp.getCarparkService().findAllSystemSetting();
		for (SystemSettingTypeEnum systemSetting : SystemSettingTypeEnum.values()) {
			mapSystemSetting.put(systemSetting, systemSetting.getDefaultValue());
		}
		for (SingleCarparkSystemSetting ss : findAllSystemSetting) {
			SystemSettingTypeEnum valueOf = null;
			try {
				valueOf = SystemSettingTypeEnum.valueOf(ss.getSettingKey());
				mapSystemSetting.put(valueOf, ss.getSettingValue());
			} catch (Exception e) {
				continue;
			}
		}
	}

	private void createAutoMenuItem(PopupMenu pop) {
		String property = System.getProperty("autoTiggerWithTest");
		log.info("自动测试{}设置为：{}","autoTiggerWithTest",property);
		if (property==null||property.equals("")) {
			return;
		}
		MenuItem refreshItem = new MenuItem("自动拍照");
		pop.add(refreshItem);
		refreshItem.addActionListener(new ActionListener() {
			private ScheduledExecutorService autoTiggerWithTest;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(refreshItem.getLabel());
				if (autoTiggerWithTest==null) {
					autoTiggerWithTest = autoTiggerWithTest(property);
					refreshItem.setLabel("停止拍照");
				}else{
					autoTiggerWithTest.shutdownNow();
					refreshItem.setLabel("自动拍照");
					autoTiggerWithTest=null;
				}
			}
		});
		
	}

	/**
	 * 设置摄像机操作的jna
	 * @param ip
	 * @param cameraType
	 * @return
	 */
	public PlateNOJNA setJNA(String ip, CameraTypeEnum cameraType) {
		PlateNOJNA jna=cameraType.getJNA(Login.injector);
		mapIpToJNA.put(ip,jna);
		return jna;
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
			setJNA(ip, showWizard.getCameraType());
			SingleCarparkDevice device2 = showWizard.getDevice();
			device2.setCarpark(sp.getCarparkService().findCarparkById(device2.getCarpark().getId()));
			if (ip.equals(oldIp)) {
				selection.setText(showWizard.getName());
				mapIpToDevice.put(ip, device2);
				CarparkFileUtils.writeObject("mapIpToDevice", mapIpToDevice);
				commonui.info("修改成功", "修改设备" + ip + "成功");
				log.info("发送平时显示类容");
				showUsualContentToDevice(device2);
				showPositionToDevice(device,this.model.getTotalSlot());
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
				addDevice(tabFolder, type,showWizard.getDevice());
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
		System.out.println("presenter"+model);
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
			List<SingleCarparkSystemUser> findAllSystemUser = sp.getSystemUserService().findAllSystemUser();
			model.setListSystemUser(findAllSystemUser);
			model.setOperaUser(findAllSystemUser.get(0));
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
		if (StrUtil.isEmpty(device.getLinkAddress())) {
			return false;
		}
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
	public boolean showContentToDevice(SingleCarparkDevice device, String content, boolean isOpenDoor) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Device d = getDevice(device);
			if (isOpenDoor) {
				if (d!=null) {
					Boolean carparkContentVoiceAndOpenDoor = hardwareService.carparkContentVoiceAndOpenDoor(d, content, device.getVolume() == null ? 1 : device.getVolume());
					openDoorToPhotograph(device.getIp());
					return carparkContentVoiceAndOpenDoor;
				}else{
					openDoor(device);
				}
			} else {
				if (d!=null) {
					return hardwareService.carparkContentVoice(d, content, device.getVolume() == null ? 1 : device.getVolume());
				}
			}
			return true;
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
	public synchronized boolean showPositionToDevice(SingleCarparkDevice device, int position) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Device d = getDevice(device);
			if (d != null) {
				String inType = device.getInType();
				if (inType.equals("进口2")) {
					inType = "进口";
				}
				if (inType.equals("出口2")) {
					inType = "出口";
				}

				return hardwareService.carparkPosition(d, position, LPRInOutType.valueOf(inType), (byte) (device.getScreenType().getType()));
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}
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
		if (device.getInType().indexOf("进口")<0) {
			return;
		}
		if (checkDeviceLinkStatus(device)) {
			return;
		}
		try {
			Device d = getDevice(device);
			if (d!=null) {
				hardwareService.carparkPosition(d, position);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private Device getDevice(SingleCarparkDevice device) {
		Device d = new Device();
		if (StrUtil.isEmpty(device.getLinkAddress())) {
			return null;
		}
		Link link = new Link();
		link.setId((long) device.getLinkAddress().hashCode());
		link.setLinkStyleEnum(LinkStyleEnum.直连设备);
		link.setType(device.getType().equals("485") ? LinkTypeEnum.COM : LinkTypeEnum.TCP);
		link.setAddress(device.getLinkAddress());
		link.setProtocol(LinkProtocolEnum.Carpark);
		link.setTimeOut(400L);
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
			Device d = getDevice(device);
			boolean carparkOpenDoor=true;
			if (d!=null) {
				carparkOpenDoor = hardwareService.carparkOpenDoor(d);
				// carparkOpenDoor = hardwareService.carparkControlDoor(getDevice(device), 0, -1, -1, -1);
			}
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
			Device d = getDevice(device);
			Boolean carparkOpenDoor=true;
			if (d!=null) {
				carparkOpenDoor = hardwareService.carparkControlDoor(d, -1, 0, -1, -1);
			}
			return carparkOpenDoor;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 设备车队操作
	 * @param device
	 * @param isopen 
	 * @return
	 */
	public boolean fleetDoor(SingleCarparkDevice device, boolean isopen) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Device d = getDevice(device);
			Boolean carparkOpenDoor=true;
			if (d!=null) {
				int carTeamTime = 0;
				String content="开启了车队";
				model.setIsOpenFleet(isopen);
				if (!isopen) {
					carTeamTime=1;
					content="停止了车队";
				}
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.车队操作, content, System.getProperty("userName"));
				carparkOpenDoor = hardwareService.carparkControlDoor(d, -1, -1, -1, carTeamTime);
			}
			return carparkOpenDoor;
		} catch (Exception e) {
			return false;
		}
		
	}
	/**
	 * 摄像机开闸
	 * @param ip
	 */
	public void openDoorToPhotograph(String ip) {
		String property = System.getProperty(PlateNOJNA.CAMERA_OPEN_DOOR);
		if (property!=null&&property.equals("false")) {
			log.info("软件触发摄像机开闸：{}",false);
			return;
		}
		mapIpToJNA.get(ip).openDoor(ip);
	}

	public boolean showUsualContentToDevice(SingleCarparkDevice device) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Device d = getDevice(device);
			Boolean carparkUsualContent=true;
			
			if (d!=null) {
				carparkUsualContent = hardwareService.carparkUsualContent(d, device.getAdvertise());
			}
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
			charge = countTempCarCharge.charge(carparkId, carType, startTime, endTime, sp, model,Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.停车场重复计费)));
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
		ChangeUserModel changeUserModel = new ChangeUserModel();
		List<SingleCarparkSystemUser> findAllSystemUser = sp.getSystemUserService().findAllSystemUser();
		changeUserModel.setAllSystemUserList(findAllSystemUser);
		changeUserModel.setSystemUser(findAllSystemUser.get(0));
		ChangeUserWizard wizard = new ChangeUserWizard(changeUserModel, sp);
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
		mapIpToJNA.get(ip).tigger(ip);
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
			@Override
			public void run() {
				try {
					byte[] bigImage = bigImage1 == null ? new byte[0] : bigImage1;
					String fl = "/img/" + f;
					Object readObject = CarparkFileUtils.readObject(ConstUtil.CLIENT_IMAGE_SAVE_FILE_PATH);
					if (!StrUtil.isEmpty(readObject)) {
						String string = (String) readObject;
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
							String upload = sp.getImageService().saveImageInServer(bigImage, finalFileName);
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
		mapDeviceType = model.getMapDeviceType();
		mapDeviceTabItem = model.getMapDeviceTabItem();
		mapIpToDevice = model.getMapIpToDevice();
		mapSystemSetting = model.getMapSystemSetting();
		refreshSystemSetting();
		saveImageTheadPool = Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("保存图片任务"));
		openDoorTheadPool = Executors.newCachedThreadPool(ThreadUtil.createThreadFactory("开门任务"));
		checkPlayerPlaying();
		countTempCarCharge = new CountTempCarChargeImpl();
		
		autoCheckDeviceLinkInfo();
		setIsTwoChanel();
		String userName = System.getProperty("userName");
		model.setUserName(userName);
		model.setWorkTime(new Date());
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		model.setHoursSlot(carparkInOutService.findTempSlotIsNow(model.getCarpark()));
		model.setMonthSlot(carparkInOutService.findFixSlotIsNow(model.getCarpark()));
		model.setTotalCharge(carparkInOutService.findFactMoneyByName(userName));
		model.setTotalFree(carparkInOutService.findFreeMoneyByName(userName));
		model.setTotalSlot(getSlotOfLeft());
		
		List<CarparkChargeStandard> listTemp = sp.getCarparkService().findAllCarparkChargeStandard(model.getCarpark(), true);
		for (CarparkChargeStandard carparkChargeStandard : listTemp) {
			String name = carparkChargeStandard.getCarparkCarType().getName();
			model.getMapTempCharge().put(name, carparkChargeStandard.getCode());
		}
	}
	/**
	 * 用来测试进出场
	 * @param property 
	 */
	private ScheduledExecutorService autoTiggerWithTest(String property) {
		Map<String, List<SingleCarparkDevice>> map=new HashMap<>();
		for (SingleCarparkDevice d : mapIpToDevice.values()) {
			String inType = d.getInType();
			List<SingleCarparkDevice> list = map.get(inType);
			if (list==null) {
				list=new ArrayList<>();
			}
			list.add(d);
			map.put(inType, list);
		}
		List<List<SingleCarparkDevice>> list=new ArrayList<>();
		for (String s : map.keySet()) {
			list.add(map.get(s));
		}
		Integer testDelayTime=10;
		try {
			testDelayTime = Integer.valueOf(property);
		} catch (NumberFormatException e) {
			
		}
		
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			int inTypeSize = list.size();
			int nowSize=0;
			@Override
			public void run() {
				if (nowSize>=inTypeSize) {
					nowSize=0;
				}
				List<SingleCarparkDevice> list2 = list.get(nowSize);
				for (SingleCarparkDevice d : list2) {
					handPhotograph(d.getIp());
				}
				nowSize++;
			}
		}, testDelayTime, testDelayTime, TimeUnit.SECONDS);
		return newSingleThreadScheduledExecutor;
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

			searchErrorCarPresenter.getModel().setPlateNo(model.getOutShowPlateNO().split("-")[0]);
			searchErrorCarPresenter.getModel().setHavePlateNoSelect(null);
			searchErrorCarPresenter.getModel().setNoPlateNoSelect(null);
			searchErrorCarPresenter.getModel().setSaveBigImg(bigImg);
			searchErrorCarPresenter.getModel().setSaveSmallImg(smallImg);
			searchErrorCarPresenter.getModel().setCarpark(model.getSearchCarpark());
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
			carInOutResultProvider.get().invok(model.getIp(), 0, select.getPlateNo(), m.getBigImg(), m.getSmallImg(), 1);
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
			log.debug("同一车牌识别间隔为：{}", s);
			Integer timeGap = Integer.valueOf(s);
			long abs = Math.abs(date.getTime()-nowDate.getTime());
			if (abs<timeGap*1000) {
				log.info("车牌{}在{}做过处理，暂不做处理", plateNO, StrUtil.formatDateTime(date));
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
			@Override
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
					showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.进口开闸语音).getContent(), false);
				} else {
					showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.出口开闸语音).getContent(), false);
				}
			}
		};
		openDoorTheadPool.submit(runnable);
	}

	public void showNowTimeToDevice(SingleCarparkDevice singleCarparkDevice) {
		hardwareService.setCarparkDate(getDevice(singleCarparkDevice), new Date());
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
				if (shouldMoney>factMoney) {
					FreeReasonPresenter p=new FreeReasonPresenter();
					p.setModel(singleCarparkInOutHistory);
					String reasons = mapSystemSetting.get(SystemSettingTypeEnum.免费原因);
					p.setReasons(reasons);
					ShowDialog s=new ShowDialog("免费原因");
					s.setPresenter(p);
					SingleCarparkInOutHistory open = (SingleCarparkInOutHistory) s.open();
					if (open==null) {
						return false;
					}
					singleCarparkInOutHistory.setFreeReason(open.getFreeReason());
				}
			}
			log.info("车辆收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", singleCarparkInOutHistory.getPlateNo(), device.getCarpark(), model.getCarTypeEnum(), model.getInTime(), model.getOutTime(),
					model.getTotalTime(), shouldMoney);
			boolean isConcentrate = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用集中收费));
			if (isConcentrate) {
				Float factMoney2 = singleCarparkInOutHistory.getFactMoney()==null?0:singleCarparkInOutHistory.getFactMoney();
				factMoney=factMoney2+factMoney;
				if (factMoney > shouldMoney) {
					commonui.error("收费提示", "总收费不能超过应收" + shouldMoney + "元");
					return false;
				}
			}
			float freeMoney = shouldMoney - factMoney;
			singleCarparkInOutHistory.setShouldMoney(shouldMoney);
			singleCarparkInOutHistory.setFactMoney(factMoney);
			singleCarparkInOutHistory.setFreeMoney(freeMoney);
			singleCarparkInOutHistory.setCarType("临时车");
			sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
			Boolean tempCarNoChargeIsPass = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车零收费是否自动出场));
			String carOutMsg = model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent();
			if (tempCarNoChargeIsPass) {
				if (shouldMoney > 0) {
					showContentToDevice(device, carOutMsg, true);
				} else {
					showContentToDevice(device, CarparkUtils.formatFloatString("请缴费" + shouldMoney + "元") + "," + carOutMsg, true);
				}
			} else {
				showContentToDevice(device, carOutMsg, true);
			}
			if (!StrUtil.isEmpty(model.getStroeFrees())) {
				for (SingleCarparkStoreFreeHistory free : model.getStroeFrees()) {
					free.setUsed("已使用");
					sp.getStoreService().saveStoreFree(free);
				}
			}
			if (CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车非所属停车场停留收费).equals("true")) {
				sp.getCarparkInOutService().updateCarparkStillTime(device.getCarpark(), device, singleCarparkInOutHistory.getPlateNo(), singleCarparkInOutHistory.getOutBigImg());
			}
			model.setStroeFrees(null);
			model.setBtnClick(false);
			model.setHandSearch(false);
			model.setComboCarTypeEnable(false);
			model.setChargeDevice(null);
			model.setChargeHistory(null);
			model.setOutCheckClick(false);
			model.setPlateInTime(new Date(), 5);
			plateSubmit(singleCarparkInOutHistory, singleCarparkInOutHistory.getOutTime(), device, ImageUtils.getImageByte(singleCarparkInOutHistory.getOutBigImg()));
			updatePosition(device.getCarpark(), singleCarparkInOutHistory.getUserId(), false);
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

	public void saveImage(String smallImgFileName, String bigImgFileName, byte[] smallImage1, byte[] bigImage1) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					byte[] bigImage = bigImage1 == null ? new byte[0] : bigImage1;
					byte[] smallImage = smallImage1 == null ? new byte[0] : smallImage1;
					String fl = "/img";
					Object readObject = CarparkFileUtils.readObject(ConstUtil.CLIENT_IMAGE_SAVE_FILE_PATH);
					if (!StrUtil.isEmpty(readObject)) {
						String string = (String) readObject;
						fl = string + fl;
					}else{
						fl = System.getProperty("user.dir") + fl;
					}
					File file = new File(fl);
					if (!file.exists() && !file.isDirectory()) {
						Files.createParentDirs(file);
						file.mkdir();
					}
					String finalBigFileName = fl + "/" + bigImgFileName;
					String finalSmallFileName = fl + "/" + smallImgFileName;

					File bigFile = new File(finalBigFileName);
					Files.createParentDirs(bigFile);
					bigFile.createNewFile();
					Files.write(bigImage, bigFile);
					log.debug("保存大图片到本地：{}",bigFile);
					File smallFile = new File(finalSmallFileName);
					smallFile.createNewFile();
					Files.write(smallImage, smallFile);
					log.debug("保存小图片到本地：{}",smallFile);
					ImgCompress.compress(smallFile.getPath());
					String ip = CarparkClientConfig.getInstance().getServerIp();
					if (true) {
						long nanoTime = System.nanoTime();
						log.debug("准备将图片{}上传到服务器{}", finalBigFileName, ip);
						try {
							String bigUpload = sp.getImageService().saveImageInServer(bigImage1, finalBigFileName);
							String smallUpload = sp.getImageService().saveImageInServer(Files.toByteArray(smallFile), finalSmallFileName);
							log.info("图片上传到服务器{}成功,{}", ip, bigUpload + "==" + smallUpload);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("图片上传到服务器{}失败", ip);
						} finally {
							log.debug("上传图片花费时间：{}", System.nanoTime() - nanoTime);
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
		checkCameraPlayStatus.shutdownNow();
	}

	public void refreshCarWithIn() {
		try {
			model.setInHistorys(sp.getCarparkInOutService().findCarInHistorys(50));
		} catch (Exception e) {
			log.error("刷新进场记录是发生错误",e);
		}
	}
	/**
	 * 固定车在非所属停车场停留超时收费
	 * @param user
	 * @param device 
	 * @param inTime
	 * @param date
	 * @param plateNO 
	 * @return
	 */
	public float countFixCarShouldMoney(SingleCarparkUser user, SingleCarparkDevice device, Date inTime, Date outTime, String plateNO) {
		List<CarparkStillTime> list=sp.getCarparkInOutService().findCarparkStillTime(plateNO,inTime);
		int minute=0;
		Date date = new Date();
		int canStillMinute =Integer.valueOf( CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车非所属停车场停留时间));
		
		for (CarparkStillTime cs : list) {
			if (cs.getCarparkId().equals(user.getCarpark().getId())) {
				continue;
			}
			Date outTime2 = cs.getOutTime();
			int minusMinute=cs.getStillSecond();
			if (outTime2==null) {
				minusMinute = CarparkUtils.countTime(cs.getInTime(), date, TimeUnit.MINUTES);
				System.out.println(inTime+"-"+date+"===="+minusMinute);
			}
			if (minusMinute>canStillMinute) {
				minute+=(minusMinute-canStillMinute);
			}
			System.out.println(cs+"======"+minusMinute);
		}
		log.info("车牌：{}，在所属停车场外停留时间：{}",plateNO,minute);
		float calculateTempCharge = sp.getCarparkService().calculateTempCharge(device.getCarpark().getId(), user.getCarType().index(), inTime, new DateTime(inTime).plusMinutes(minute).toDate());
		log.info("固定车{}，缴费：{}",plateNO,calculateTempCharge);
		return calculateTempCharge;
	}

	/**
	 * 车牌报送
	 * @param cch
	 * @param date
	 * @param carpark
	 * @param device
	 * @param bigImage 
	 */
	public void plateSubmit(SingleCarparkInOutHistory cch, Date date,SingleCarparkDevice device, byte[] bigImage) {
		if ("false".equals(mapSystemSetting.get(SystemSettingTypeEnum.启用车牌报送))) {
			return;
		}
		PlateSubmitServiceI plateSubmitService = sp.getPlateSubmitService();
		plateSubmitService.submitPlate(cch.getPlateNo(), date, bigImage,device);
	}
	/**
	 * 获取剩余车位数
	 * @return
	 */
	public int getSlotOfLeft() {
		Integer findTotalSlotIsNow = 0;
		String slotShowType = mapSystemSetting.get(SystemSettingTypeEnum.车位数显示方式);
		switch (slotShowType) {
		case "0":
			findTotalSlotIsNow = sp.getCarparkInOutService().findTotalSlotIsNow(model.getCarpark());
			model.setTotalSlotTooltip("临时车位");
			break;
		case "1":
			findTotalSlotIsNow=getFixSlotWithChange();
			model.setTotalSlotTooltip("固定车位");
			break;
		case "2":
			findTotalSlotIsNow=getTotalSlotWithChange();
			model.setTotalSlotTooltip("总车位");
			break;
		case "3":
			findTotalSlotIsNow = getRealTineSlot(1);
			
			break;
		case "4":
			findTotalSlotIsNow = getRealTineSlot(2);
			break;
		case "5":
			findTotalSlotIsNow = getRealTineSlot(3);
			break;
		}
		
		return findTotalSlotIsNow;
	}

	/**
	 * @param findTotalSlotIsNow
	 * @return
	 */
	private Integer getRealTineSlot(int type) {
		Integer findTotalSlotIsNow =0;
		try {
			SingleCarparkCarpark carpark = model.getCarpark().getMaxParent();
			SingleCarparkCarpark findCarparkById = sp.getCarparkService().findCarparkById(carpark.getId());
			if (type==2) {
				findTotalSlotIsNow=findCarparkById.getLeftFixNumberOfSlot();
				model.setTotalSlotTooltip("实时固定车位，双击进行修改");
			}else if(type==1){
				findTotalSlotIsNow=findCarparkById.getLeftTempNumberOfSlot();
				model.setTotalSlotTooltip("实时临时车位，双击进行修改");
			}else{
				findTotalSlotIsNow=findCarparkById.getLeftTempNumberOfSlot()+findCarparkById.getLeftFixNumberOfSlot();
				model.setTotalSlotTooltip("实时总车位，双击进行修改");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return findTotalSlotIsNow;
	}
	/**
	 * 获取剩余的总车位数
	 * @return
	 */
	private Integer getTotalSlotWithChange() {
		int findTotalCarIn = sp.getCarparkInOutService().findTotalCarIn(model.getCarpark());
		
		int i = model.getHoursSlot()+model.getMonthSlot()-findTotalCarIn;
		return i<0?0:i;
	}

	/**
	 * 获取固定车剩余位数
	 * @return
	 */
	private Integer getFixSlotWithChange() {
		int findTotalFixCarIn = sp.getCarparkInOutService().findTotalFixCarIn(model.getCarpark());
		int monthSlot = model.getMonthSlot();
		int i = monthSlot-findTotalFixCarIn;
		return i<0?0:i;
	}

	public void saveFleetInOutHistory(SingleCarparkDevice device, String plateNO, byte[] bigImage) {
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.车队操作, "车辆：{}在车队期间从设备[{}]{}场",bigImage, System.getProperty("userName"),plateNO,device.getName(),device.getInType().substring(0, 1));
	}

	public void updatePosition(SingleCarparkCarpark carpark, Long userId, boolean inOrOut) {
		sp.getPositionUpdateService().updatePosion(carpark, userId, inOrOut);
	}

	public void editPosition() {
		SingleCarparkCarpark carpark = model.getCarpark().getMaxParent();
		Integer slotShowType = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位数显示方式));
		if (StrUtil.isEmpty(model.getTotalSlotTooltip())||slotShowType<3) {
			return;
		}
		SingleCarparkCarpark c = sp.getCarparkService().findCarparkById(carpark.getId());
		Integer fixSlot = c.getTrueLeftFixNumberOfSlot()==null?c.getFixNumberOfSlot():c.getTrueLeftFixNumberOfSlot();
		Integer tempSlot = c.getTrueLeftTempNumberOfSlot()==null?c.getTempNumberOfSlot():c.getTrueLeftTempNumberOfSlot();
		
		String fix = null;
		if (slotShowType==4||slotShowType==5) {
			fix = commonui.input("车位数修改", "输入新的剩余固定车位数", "" + fixSlot);
			if (fix == null) {
				return;
			} 
			
		}
		String temp = null;
		if (slotShowType==3||slotShowType==5) {
			temp = commonui.input("车位数修改", "输入新的剩余临时车位数", "" + tempSlot);
			if (temp == null) {
				return;
			} 
		}
		try {
			fixSlot =Integer.valueOf(fix);
			tempSlot =Integer.valueOf(temp);
		} catch (NumberFormatException e) {
			commonui.error("错误", "请设置正确的车位数");
			return;
		}
		c.setLeftFixNumberOfSlot(fixSlot);
		c.setLeftTempNumberOfSlot(tempSlot);
		sp.getCarparkService().saveCarpark(c);
		commonui.info("成功", "修改车位数成功");
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "", System.getProperty("userName"));
	}
	

}
