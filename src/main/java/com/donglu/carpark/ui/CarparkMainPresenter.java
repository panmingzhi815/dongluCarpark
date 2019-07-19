package com.donglu.carpark.ui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.NotYetConnectedException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caucho.hessian.client.HessianRuntimeException;
import com.donglu.carpark.hardware.CarparkScreenService;
import com.donglu.carpark.hardware.CarparkScreenServiceImpl;
import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.model.Result;
import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.model.ShowInOutHistoryModel;
import com.donglu.carpark.model.SystemUserModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.service.CarparkQrCodeInOutService;
import com.donglu.carpark.service.CountTempCarChargeI;
import com.donglu.carpark.service.IpmsServiceI;
import com.donglu.carpark.service.PlateSubmitServiceI;
import com.donglu.carpark.service.SystemOperaLogServiceI;
import com.donglu.carpark.service.impl.CarparkQrCodeInOutServiceImpl;
import com.donglu.carpark.service.impl.CountTempCarChargeImpl;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.servlet.OpenDoorServlet;
import com.donglu.carpark.ui.servlet.WebSocketClient;
import com.donglu.carpark.ui.task.CarInOutResult;
import com.donglu.carpark.ui.task.ConfimBox;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.ui.view.account.AccountCarPresenter;
import com.donglu.carpark.ui.view.inouthistory.CarInHistoryPresenter;
import com.donglu.carpark.ui.view.inouthistory.FreeReasonDialog;
import com.donglu.carpark.ui.view.inouthistory.InOutHistoryPresenter;
import com.donglu.carpark.ui.view.message.MessageUtil;
import com.donglu.carpark.ui.view.user.UserPresenter;
import com.donglu.carpark.ui.wizard.AddDeviceModel;
import com.donglu.carpark.ui.wizard.AddDeviceWizard;
import com.donglu.carpark.ui.wizard.ChangeUserWizard;
import com.donglu.carpark.ui.wizard.EditSystemUserPasswordWizard;
import com.donglu.carpark.ui.wizard.InOutHistoryDetailWizard;
import com.donglu.carpark.ui.wizard.ReturnAccountWizard;
import com.donglu.carpark.ui.wizard.SearchHistoryByHandWizard;
import com.donglu.carpark.ui.wizard.model.ChangeUserModel;
import com.donglu.carpark.ui.wizard.model.ReturnAccountModel;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.donglu.carpark.util.ExecutorsUtils;
import com.donglu.carpark.util.HttpRequestUtil;
import com.donglu.carpark.util.ImageUtils;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.LPRInOutType;
import com.dongluhitec.card.domain.LinkProtocolEnum;
import com.dongluhitec.card.domain.LinkTypeEnum;
import com.dongluhitec.card.domain.db.Device;
import com.dongluhitec.card.domain.db.Link;
import com.dongluhitec.card.domain.db.LinkStyleEnum;
import com.dongluhitec.card.domain.db.SerialDeviceAddress;
import com.dongluhitec.card.domain.db.shanghaiyunpingtai.HistoryUseStatus;
import com.dongluhitec.card.domain.db.shanghaiyunpingtai.YunCarparkCarInOut;
import com.dongluhitec.card.domain.db.singlecarpark.CameraTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory.PayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkOffLineHistory;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkStillTime;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceErrorMessage;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.db.singlecarpark.ScreenTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice.DeviceInOutTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkImageHistory;
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
import com.dongluhitec.card.hardware.plateDevice.PastPlateResult;
import com.dongluhitec.card.hardware.plateDevice.PlateNOJNA;
import com.dongluhitec.card.hardware.plateDevice.bean.PlateDownload;
import com.dongluhitec.card.hardware.service.BasicHardwareService;
import com.dongluhitec.card.mapper.BeanUtil;
import com.dongluhitec.card.shanghaiyunpingtai.ShanghaiYunCarparkCfg;
import com.dongluhitec.card.ui.util.FileUtils;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.sun.jna.Native;

import ch.qos.logback.core.joran.conditional.IfAction;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

@Singleton
public class CarparkMainPresenter {
	private static final String MAP_IP_TO_DEVICE = "mapIpToDevice";
	private Logger log = LoggerFactory.getLogger(CarparkMainPresenter.class);
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;

	Map<String, PlateNOJNA> mapIpToJNA = new HashMap<>();

	@Inject
	private WebCameraDevice webCameraDevice;
	@Inject
	private BasicHardwareService hardwareService;
	private CarparkScreenService carparkScreenService=new CarparkScreenServiceImpl();
	private CarparkQrCodeInOutService carparkQrCodeInOutService=new CarparkQrCodeInOutServiceImpl();

	@Inject
	private InOutHistoryPresenter inOutHistoryPresenter;
	@Inject
	private SearchErrorCarPresenter searchErrorCarPresenter;

	// 保存设备的进出口信息
	private Map<String, String> mapDeviceType;

	// 保存设备的界面信息
	private Map<CTabItem, String> mapDeviceTabItem;
	// 保存设备的信息
	private Map<String, SingleCarparkDevice> mapIpToDevice;
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

	private int openDoorDelay = 500;

	private Map<String, Timer> mapCheckChargeTimer=new HashMap<>();
	/**
	 * 删除一个设备tab页
	 * 
	 * @param selection
	 */
	public void deleteDeviceTabItem(CTabItem selection) {
		try {
			if (selection != null) {
				String ip = mapDeviceTabItem.get(selection);
				mapIpToDevice.remove(ip);
				CarparkFileUtils.writeObjectForException(MAP_IP_TO_DEVICE, mapIpToDevice);
				selection.dispose();
				mapIpToJNA.get(ip).closeEx(ip);
				mapDeviceTabItem.remove(selection);
				mapDeviceType.remove(ip);
				model.getMapIpToDeviceStatus().remove(ip);
				if (mapIpToDevice.keySet().size() <= 0) {
					model.setCarpark(null);
				}
				setIsTwoChanel();
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "删除设备："+ip, ConstUtil.getUserName());
			}
		} catch (Exception e) {
			commonui.error("失败", "删除失败", e);
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
			if (tabFolder.getItems().length >= 8) {
				commonui.info("提示", type + "最多只能添加8个设备");
				return;
			}
			CarparkFileUtils.writeObjectForException(MAP_IP_TO_DEVICE, mapIpToDevice);
			AddDeviceModel model = new AddDeviceModel();
			List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();

//			if (!StrUtil.isEmpty(this.model.getCarpark())) {
//				findAllCarpark = sp.getCarparkService().findSameCarpark(this.model.getCarpark());
//			}
			if (StrUtil.isEmpty(findAllCarpark)) {
				commonui.info("提示", "请先添加停车场");
				return;
			}
			model.setList(findAllCarpark);
			model.setCarpark(findAllCarpark.get(0));
			model.setType("tcp");
			model.setInType(type);
			model.setInOutType(DeviceInOutTypeEnum.valueOf(model.getInOrOut()));
			// model.seti
			AddDeviceWizard v = new AddDeviceWizard(model);
			if (type.indexOf("出口") > -1) {
				model.setAdvertise("欢迎再次光临");
			}
			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			final SingleCarparkDevice device = showWizard.getDevice();
			this.model.setCarpark(device.getCarpark());
			addDevice(device);
			addDevice(tabFolder, type, device);
			new Thread(new Runnable() {
				public void run() {
					showUsualContentToDevice(device);
					showPositionToDevice(device, CarparkMainPresenter.this.model.getTotalSlot());
					sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "添加了设备："+device.getIp(), ConstUtil.getUserName());
				}
			}).start();
		} catch (Exception e) {
			log.error("添加设备时发生错误", e);
			commonui.error("失败", "添加失败");
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
		checkDeviceControlTimeStatus(new Date(), device);
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
		for (String c : keySet) {
			SingleCarparkDevice d = mapIpToDevice.get(c);
			if (d.getInType().indexOf("进口") < 0&&!d.getScreenType().equals(ScreenTypeEnum.一体机)) {
				continue;
			}
			Date when = new Date();
			Date plateInTime = model.getPlateInTime();
			if (plateInTime.after(when)) {
				log.info("车辆进出场，在时间：{}后在对设备{}发车位数,现在时间：{}", plateInTime, d, when);
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
	 * @throws Exception 
	 */
	public void addDevice(CTabFolder tabFolder, String type, SingleCarparkDevice device) throws Exception {
		String ip = device.getIp();
		String name = device.getName();

		if (mapDeviceType.get(ip) != null) {
			commonui.error("添加失败", "设备" + ip + "已存在");
			// return;
		}
		CarparkFileUtils.writeObject(MAP_IP_TO_DEVICE, mapIpToDevice);
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		tabItem.setText(name);
		tabItem.setImage(JFaceUtil.getImage("deviceStatus_16"));
		Composite composite = new Composite(tabFolder, SWT.EMBEDDED);
		tabItem.setControl(composite);
		tabItem.setData("device", device);
		composite.setLayout(new FillLayout());
		tabFolder.getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				createCamera(device, composite);
				checkDeviceControlTimeStatus(new Date(), device);
			}
		});
		tabFolder.setSelection(tabItem);
		mapDeviceTabItem.put(tabItem, ip);
		mapDeviceType.put(ip, type);
		model.getMapIpToTabItem().put(ip, tabItem);
	}
	//保存播放地址对应的视频流播放器
	private final Map<String, MediaPlayer> mapPlayer = new HashMap<>();
	//保存播放地址对应的设备
	private Map<String, SingleCarparkDevice> mapCameraToDeviceIp = new HashMap<>();
	//保存播放地址对应的设备
	private Map<String, Frame> mapCameraToFrame = new HashMap<>();
	//保存需要停止播放视频的信息
	private Map<String, Boolean> mapNeedStopPlay=new HashMap<>();
	private int checkPlayerPlayingSize = 0;
	private Map<String, Integer> mapDeviceFailInfo = new HashMap<>();
	private ScheduledExecutorService checkCameraPlayStatus;
	//控制器超时时间
	private long timeOut = 1000L;
	//长颈鹿app服务
	private IpmsServiceI ipmsService;
	private Timer checkIsPayTimer;
	/**
	 * 自动刷新停车场视频监控
	 */
	protected void checkPlayerPlaying() {
		String property = System.getProperty(ConstUtil.AUTO_REFRESH_CAMERA, "true");
		log.info("自动刷新视频监控：{} 设置为：{}",ConstUtil.AUTO_REFRESH_CAMERA,property);
		if (property.equals("false")) {
			return;
		}
		checkCameraPlayStatus = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("每10秒检测摄像机连接状态"));
		checkCameraPlayStatus.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					log.debug("开始第{}次检查摄像机的连接状态", checkPlayerPlayingSize);
					for (String url : mapPlayer.keySet()) {
						if (mapNeedStopPlay.getOrDefault(url, false)) {
							continue;
						}
						MediaPlayer mediaPlayer = mapPlayer.get(url);
						SingleCarparkDevice device = mapCameraToDeviceIp.get(url);
						if (checkPlayerPlayingSize > 0 && checkPlayerPlayingSize % 3 == 0 && device != null && device.getCameraType().equals(CameraTypeEnum.智芯)) {
							log.info("自动刷新华夏智芯摄像机:{}，防止摄像机黑屏", device.getIp());
							mediaPlayer.playMedia(url);
							continue;
						}
						boolean playing = mediaPlayer.isPlaying();
						if (!playing) {
							log.info("设备连接{}已断开", url);
							mediaPlayer.playMedia(url);
						}
					}
					checkPlayerPlayingSize++;
				} catch (Exception e) {
					log.error("检测摄像机连接时发生错误");
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
		autoClearPlaying();
	}
	/**
	 * 自动清理华夏智芯摄像机缓存重新生成新的视频播放器，每天清理一次
	 */
	public void autoClearPlaying(){
		String property = System.getProperty("autoClearCamera", "1");
		log.info("自动清理华夏智芯摄像机缓存重新生成新的视频播放器：{} 设置为：{}","autoClearCamera",property);
		if (property.equals("false")) {
			return;
		}
		int clearTime=1;
		try {
			clearTime=Integer.valueOf(property);
		} catch (NumberFormatException e) {
		}
		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("每天"+clearTime+"重新生成新的视频播放器"));
		Date date = new Date();
		long initialDelay=new DateTime(StrUtil.getTodayBottomTime(date)).plusHours(clearTime).getMillis()- date.getTime();
		scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					log.info("每天重新生成新的华夏智芯摄像机视频播放");
					for (String url : mapPlayer.keySet()) {
						SingleCarparkDevice device = mapCameraToDeviceIp.get(url);
						if (device==null||!device.getCameraType().equals(CameraTypeEnum.智芯)) {
							continue;
						}
						mapPlayer.get(url).release();
						EmbeddedMediaPlayer createPlay = webCameraDevice.createPlay(mapCameraToFrame.get(url), url);
						mapPlayer.put(url, createPlay);
					}
				} catch (Exception e) {
					log.error("每天重新生成新的华夏智芯摄像机视频播放,操作失败",e);
				}
			}
		}, initialDelay, 1000*60*60*24, TimeUnit.MILLISECONDS);
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
		CameraTypeEnum cameraType = device.getCameraType() == null ? CameraTypeEnum.信路威 : device.getCameraType();
		if (cameraType.equals(CameraTypeEnum.智芯)) {
			createHCamera(device, northCamera);
			return;
		}
		PlateNOJNA jna = null;
		jna = setJNA(ip, cameraType);
		Frame new_Frame1 = SWT_AWT.new_Frame(northCamera);
		MyCanvas canvas1 = new MyCanvas();
		canvas1.setBackground(Color.WHITE);
		new_Frame1.add(canvas1);
		new_Frame1.pack();
		new_Frame1.setVisible(true);

		final String url = cameraType.getRtspAddress(ip) == null ? device.getIp() : cameraType.getRtspAddress(ip);
		log.info("准备连接视频{}", url);
		final EmbeddedMediaPlayer createPlayRight = webCameraDevice.createPlay(new_Frame1, url);
		mapPlayer.put(url, createPlayRight);
		mapCameraToDeviceIp.put(url, device);
		mapCameraToFrame.put(url, new_Frame1);

		northCamera.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
				mapPlayer.remove(url);
			}
		});
		PopupMenu popMenu = new PopupMenu();
		MenuItem refreshItem = new MenuItem("重新播放");
		MenuItem closePlayItem = new MenuItem("关闭播放");
		MenuItem refreshSettingItem = new MenuItem("刷新设置");
		MenuItem checkDeviceStatusItem = new MenuItem("检测设备");
		popMenu.add(refreshItem);
		popMenu.add(closePlayItem);
		popMenu.add(refreshSettingItem);
		popMenu.add(checkDeviceStatusItem);
		ImageIcon imageIcon = new ImageIcon(System.getProperty("user.dir")+"\\donglu.png");
		closePlayItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mapPlayer.get(url).stop();
				mapNeedStopPlay.put(url, true);
				device.setIsOpenCamera(false);
				saveDevice(device);
				canvas1.setImage(imageIcon.getImage());
			}
		});
		checkDeviceStatusItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				northCamera.getDisplay().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//						commonui.info("读记录数", "摄像机192.168.1.235拥有记录数2000");
//					}
//				});
				try {
					String ip = device.getIp();
					String msg = "设备正常";
					String m = checkDeviceStatus(ip);
					if (!StrUtil.isEmpty(m)) {
						msg = m;
					}
					String s = msg;
					Runnable runnable = new Runnable() {
						public void run() {
							commonui.info("结果", s);
						}
					};
					northCamera.getDisplay().asyncExec(runnable);
				} catch (Exception e1) {
					Runnable runnable = new Runnable() {
						public void run() {
							commonui.error("失败", "设备检测时发生错误" + e1, e1);
						}
					};
					northCamera.getDisplay().asyncExec(runnable);
				}
			}
		});
		refreshSettingItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshSystemSetting();
			}
		});
		refreshItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					mapPlayer.get(url).release();
					MediaPlayer reCreate = webCameraDevice.createPlay(new_Frame1, url);
					mapPlayer.put(url, reCreate);
					mapNeedStopPlay.put(url, false);
					device.setIsOpenCamera(true);
					saveDevice(device);
					setDeviceTabItemStatus(ip, null, null, "");
				} catch (Exception e1) {
					log.info("刷新视频流监控时发生错误",e);
				}
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
					System.out.println("打开右键菜单");
					popMenu.show(canvas1, e.getX(), e.getY());
				}
			}

		});
		jna.openEx(ip, carInOutResultProvider.get());
		jna.pastPlate(ip, new PastPlateResult() {
			@Override
			public void invok(String ip, Date time, String plateNO, byte[] bigImage, byte[] smallImage, float rightSize, String plateColor) {
				try {
					log.info("{}断网续传,时间：{}，车牌：{}，颜色：{}", ip, time, plateNO, plateColor);
					CarparkOffLineHistory carparkOffLineHistory = new CarparkOffLineHistory();
					carparkOffLineHistory.setDeviceIp(ip);
					carparkOffLineHistory.setDeviceName(mapIpToDevice.get(ip).getName());
					carparkOffLineHistory.setInTime(time);
					carparkOffLineHistory.setPlateNO(plateNO);
					String bigImagePath = CarparkUtils.FormatImagePath(time, plateNO, true);
					carparkOffLineHistory.setBigImage(bigImagePath);
					String smallImagePath = CarparkUtils.FormatImagePath(time, plateNO, false);
					carparkOffLineHistory.setSmallImage(smallImagePath);
					saveImage(mapIpToDevice.get(ip),smallImagePath, bigImagePath, smallImage, bigImage);
					sp.getCarparkInOutService().saveCarparkOffLineHistory(carparkOffLineHistory);
				} catch (Exception e) {
					log.error(ip + "断网续传出错", e);
				}
			}
		});
		if (!device.getIsOpenCamera()) {
			mapPlayer.get(url).stop();
			mapNeedStopPlay.put(url, true);
			canvas1.setImage(imageIcon.getImage());
		}
	}
	/**
	 * 创建华夏智芯摄像机视频播放
	 * @param device
	 * @param northCamera
	 */
	public void createHCamera(SingleCarparkDevice device, Composite northCamera) {
		String ip = device.getIp();
		PlateNOJNA jna = null;
		CameraTypeEnum cameraType = device.getCameraType() == null ? CameraTypeEnum.信路威 : device.getCameraType();
		jna = setJNA(ip, cameraType);
		Frame new_Frame1 = SWT_AWT.new_Frame(northCamera);
		new_Frame1.setBackground(Color.WHITE);
		MyCanvas canvas=new MyCanvas();
		new_Frame1.add(canvas);
		int handle=(int) Native.getComponentID(canvas);
		PopupMenu popMenu = new PopupMenu();
		MenuItem refreshItem = new MenuItem("重新播放");
		MenuItem closePlayItem = new MenuItem("关闭播放");
		MenuItem refreshSettingItem = new MenuItem("刷新设置");
		MenuItem checkDeviceStatusItem = new MenuItem("检测设备");
		popMenu.add(refreshItem);
		popMenu.add(closePlayItem);
		popMenu.add(refreshSettingItem);
		popMenu.add(checkDeviceStatusItem);
		PlateNOJNA finalJna=jna;
		ImageIcon imageIcon = new ImageIcon(System.getProperty("user.dir")+"\\donglu.png");
		closePlayItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				finalJna.stopPlaying(ip);
				device.setIsOpenCamera(false);
				saveDevice(device);
				canvas.setImage(imageIcon.getImage());
			}
		});
		checkDeviceStatusItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				northCamera.getDisplay().asyncExec(new Runnable() {
//					@Override
//					public void run() {
//						commonui.info("读记录数", "摄像机192.168.1.235拥有记录数2000");
//					}
//				});
				try {
					String ip = device.getIp();
					String msg = "设备正常";
					String m = checkDeviceStatus(ip);
					if (!StrUtil.isEmpty(m)) {
						msg = m;
					}
					String s = msg;
					Runnable runnable = new Runnable() {
						public void run() {
							commonui.info("结果", s);
						}
					};
					northCamera.getDisplay().asyncExec(runnable);
				} catch (Exception e1) {
					Runnable runnable = new Runnable() {
						public void run() {
							commonui.error("失败", "设备检测时发生错误" + e1, e1);
						}
					};
					northCamera.getDisplay().asyncExec(runnable);
				}
			}
		});
		refreshSettingItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshSystemSetting();
			}
		});
		refreshItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					finalJna.startPlaying(ip, handle);
					device.setIsOpenCamera(true);
					saveDevice(device);
				} catch (Exception e1) {
					log.info("刷新视频流监控时发生错误",e);
				}
			}
		});
		createAutoMenuItem(popMenu);
		canvas.add(popMenu);
		canvas.addMouseListener(new java.awt.event.MouseAdapter() {
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
					System.out.println("打开右键菜单");
					popMenu.show(canvas, e.getX(), e.getY());
				}
			}

		});
		jna.openEx(ip, handle,carInOutResultProvider.get());
		if (StrUtil.isEmpty(device.getCameraVersion())) {
			String version = jna.getVersion(ip);
			device.setCameraVersion(version);
			saveDevice(device);
		}
		model.getMapHCameraPlayHandle().put(ip, handle);
		jna.pastPlate(ip, new PastPlateResult() {
			@Override
			public void invok(String ip, Date time, String plateNO, byte[] bigImage, byte[] smallImage, float rightSize, String plateColor) {
				try {
					log.info("{}断网续传,时间：{}，车牌：{}，颜色：{}", ip, time, plateNO, plateColor);
					CarparkOffLineHistory carparkOffLineHistory = new CarparkOffLineHistory();
					carparkOffLineHistory.setDeviceIp(ip);
					carparkOffLineHistory.setDeviceName(mapIpToDevice.get(ip).getName());
					carparkOffLineHistory.setInTime(time);
					carparkOffLineHistory.setPlateNO(plateNO);
					String bigImagePath = CarparkUtils.FormatImagePath(time, plateNO, true);
					carparkOffLineHistory.setBigImage(bigImagePath);
					String smallImagePath = CarparkUtils.FormatImagePath(time, plateNO, false);
					carparkOffLineHistory.setSmallImage(smallImagePath);
					saveImage(mapIpToDevice.get(ip),smallImagePath, bigImagePath, smallImage, bigImage);
					sp.getCarparkInOutService().saveCarparkOffLineHistory(carparkOffLineHistory);
				} catch (Exception e) {
					log.error(ip + "断网续传出错", e);
				}
			}
		});
		if (!device.getIsOpenCamera()) {
			finalJna.stopPlaying(ip);
			canvas.setImage(imageIcon.getImage());
		}
	}
	
	public void startPlayingDevice(final String ip){
		new Thread(new Runnable() {
			public void run() {
				SingleCarparkDevice device = mapIpToDevice.get(ip);
				if (device == null) {
					return;
				}
				if (!device.getIsOpenCamera()) {
					return;
				}
				CameraTypeEnum cameraType = device.getCameraType();
				if (cameraType.equals(CameraTypeEnum.智芯)) {
					PlateNOJNA jna = mapIpToJNA.get(ip);
					jna.startPlaying(ip, model.getMapHCameraPlayHandle().get(ip));
				} else {
					String url = cameraType.getRtspAddress(ip) == null ? device.getIp() : cameraType.getRtspAddress(ip);
					mapPlayer.get(url).play();
					mapNeedStopPlay.put(url, false);
				}
			}
		}).start();
	}
	public void stopPlayingDevice(final String ip){
		new Thread(new Runnable() {
			public void run() {
				SingleCarparkDevice device = mapIpToDevice.get(ip);
				if (device == null) {
					return;
				}
				CameraTypeEnum cameraType = device.getCameraType();
				if (cameraType.equals(CameraTypeEnum.智芯)) {
					PlateNOJNA jna = mapIpToJNA.get(ip);
					jna.stopPlaying(ip);
				} else if (cameraType.equals(CameraTypeEnum.信路威)) {
					String url = cameraType.getRtspAddress(ip) == null ? device.getIp() : cameraType.getRtspAddress(ip);
					mapPlayer.get(url).stop();
					mapNeedStopPlay.put(url, true);
				}
			}
		}).start();
	}

	protected void saveDevice(SingleCarparkDevice device) {
		mapIpToDevice.put(device.getIp(), device);
		CarparkFileUtils.writeObject(MAP_IP_TO_DEVICE, mapIpToDevice);
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
		initVioce();
		
		Set<Long> ss=new HashSet<>();
		for (SingleCarparkDevice device : mapIpToDevice.values()) {
			SingleCarparkCarpark carpark = device.getCarpark();
			if(ss.contains(carpark.getId())) {
				continue;
			}
			ss.add(carpark.getId());
			SingleCarparkCarpark findCarparkById = sp.getCarparkService().findCarparkById(carpark.getId());
			device.setCarpark(findCarparkById);
		}
	}

	private void createAutoMenuItem(PopupMenu pop) {
		String property = System.getProperty("autoTiggerWithTest");
		log.info("自动测试{}设置为：{}", "autoTiggerWithTest", property);
		if (property == null || property.equals("")) {
			return;
		}
		MenuItem refreshItem = new MenuItem("自动拍照");
		pop.add(refreshItem);
		refreshItem.addActionListener(new ActionListener() {
			private ScheduledExecutorService autoTiggerWithTest;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(refreshItem.getLabel());
				if (autoTiggerWithTest == null) {
					autoTiggerWithTest = autoTiggerWithTest(property);
					refreshItem.setLabel("停止拍照");
				} else {
					autoTiggerWithTest.shutdownNow();
					refreshItem.setLabel("自动拍照");
					autoTiggerWithTest = null;
				}
			}
		});

	}

	/**
	 * 设置摄像机操作的jna
	 * 
	 * @param ip
	 * @param cameraType
	 * @return
	 */
	public PlateNOJNA setJNA(String ip, CameraTypeEnum cameraType) {
		PlateNOJNA jna = cameraType.getJNA(Login.injector);
		mapIpToJNA.put(ip, jna);
		return jna;
	}

	/**
	 * @param type
	 * @param tabFolder
	 * 
	 */
	public void editDevice(CTabFolder tabFolder, String type) {
		try {
			CarparkFileUtils.writeObjectForException(MAP_IP_TO_DEVICE, mapIpToDevice);
			CTabItem selection = tabFolder.getSelection();
			if (StrUtil.isEmpty(selection)) {
				return;
			}
			String oldIp = mapDeviceTabItem.get(selection);
			final SingleCarparkDevice device = mapIpToDevice.get(oldIp);
			CameraTypeEnum oldCameraType = device.getCameraType();
			AddDeviceModel model = new AddDeviceModel();
			model.setDevice(device);
			AddDeviceWizard v = new AddDeviceWizard(model);
			List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();
//			if (!StrUtil.isEmpty(this.model.getCarpark()) && mapIpToDevice.keySet().size() > 1) {
//				findAllCarpark = sp.getCarparkService().findSameCarpark(this.model.getCarpark());
//			}
			model.setList(findAllCarpark);
			AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
			if (showWizard == null) {
				return;
			}
			String ip = showWizard.getIp();
			setJNA(ip, showWizard.getCameraType());
			final SingleCarparkDevice device2 = showWizard.getDevice();
			device2.setCarpark(sp.getCarparkService().findCarparkById(device2.getCarpark().getId()));
			String string =  checkDeviceEditInfo(device,device2);
			if (ip.equals(oldIp) && showWizard.getCameraType().equals(oldCameraType)) {
				selection.setText(showWizard.getName());
				mapIpToDevice.put(ip, device2);
				CarparkFileUtils.writeObjectForException(MAP_IP_TO_DEVICE, mapIpToDevice);
				new Thread(new Runnable() {
					public void run() {
						setIsTwoChanel();
						log.info("发送平时显示内容");
						showUsualContentToDevice(device2);
						showPositionToDevice(device2, CarparkMainPresenter.this.model.getTotalSlot());
						// sendPositionToAllDevice(true);
						checkDeviceControlTimeStatus(new Date(), device2);
						sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "修改了设备:" + device.getIp() + string, ConstUtil.getUserName());
					}
				}).start();
				commonui.info("修改成功", "修改设备" + ip + "成功");
				return;
			} else {
				if (mapDeviceType.get(ip) != null) {
					commonui.error("修改失败", "设备" + ip + "已存在");
					// return;
				}
				deleteDeviceTabItem(selection);
				addDevice(device2);
				addDevice(tabFolder, type, showWizard.getDevice());
				this.model.setCarpark(device2.getCarpark());
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "修改了设备:"+device2.getIp()+string, ConstUtil.getUserName());
			}
		} catch (Exception e1) {
			log.error("修改设备时发生错误", e1);
			commonui.error("修改成功", "修改失败",e1);
		} finally {

		}
	}
	/**
	 * 检测设备修改的信息
	 * @param oldDevice
	 * @param newDevice
	 * @return
	 */
	private String checkDeviceEditInfo(SingleCarparkDevice oldDevice, SingleCarparkDevice newDevice) {
		StringBuilder sb=new StringBuilder();
		String roadType = oldDevice.getRoadType();
		String roadType2 = newDevice.getRoadType();
		if (!roadType.equals(roadType2)) {
			sb.append("修改了车道类型:["+roadType+"]为["+roadType2+"];");
		}
		String inOrOut = oldDevice.getInOrOut();
		String inOrOut2 = newDevice.getInOrOut();
		if (!inOrOut.equals(inOrOut2)) {
			sb.append("修改了进出口类型：["+inOrOut+"]>["+inOrOut2+"];");
		}
		String linkAddress = oldDevice.getLinkAddress();
		String linkAddress2 = newDevice.getLinkAddress();
		if ((linkAddress==null)!=(linkAddress2==null)) {
			sb.append("修改了控制器地址：["+linkAddress+"]>["+linkAddress2+"];");
		}else{
			if (linkAddress!=null&&linkAddress2!=null&&!linkAddress.equals(linkAddress2)) {
				sb.append("修改了控制器地址：["+linkAddress+"]>["+linkAddress2+"];");
			}
		}
		
		return sb.toString();
	}

	public CarparkMainApp getView() {
		return view;
	}

	public void setView(CarparkMainApp view) {
		this.view = view;
		System.out.println("presenter" + model);
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
			List<Double> list=carparkInOutService.countReturnMoney(userName);
//			List<SingleCarparkInOutHistory> listFact = carparkInOutService.findHistoryFactMoneyNotReturn(userName);
//			List<SingleCarparkInOutHistory> listFree = carparkInOutService.findHistoryFreeMoneyNotReturn(userName);
			float factMoney = list.size()<3?0:list.get(2).floatValue();
			float freeMoney = list.size()<4?0:list.get(3).floatValue();
			if (factMoney<=0&&freeMoney<=0) {
				commonui.info("提示", "没有需要归账的金额！");
				return;
			}
//			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFact) {
//				Float factMoney2 = singleCarparkInOutHistory.getFactMoney();
//				if (StrUtil.isEmpty(factMoney2)) {
//					factMoney2 = 0F;
//				}
//				factMoney += factMoney2;
//			}
//			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFree) {
//				Float factMoney2 = singleCarparkInOutHistory.getFreeMoney();
//				if (StrUtil.isEmpty(factMoney2)) {
//					factMoney2 = 0F;
//				}
//				freeMoney += factMoney2;
//			}
			model.setShouldReturn(factMoney);
			model.setFactReturn(freeMoney);
			List<SingleCarparkSystemUser> findAllSystemUser = sp.getSystemUserService().findAllSystemUser();
			model.setListSystemUser(findAllSystemUser);
			model.setOperaUser(findAllSystemUser.get(0));
			model.setFree(true);
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
			
			carparkInOutService.updateRecount(list.get(0).longValue(),saveReturnAccount,model.isFree(),userName);

//			Map<Long, Long> map = new HashMap<Long, Long>();
//			for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFact) {
//				singleCarparkInOutHistory.setReturnAccount(saveReturnAccount);
//				map.put(singleCarparkInOutHistory.getId(), saveReturnAccount);
//			}
//
//			carparkInOutService.saveInOutHistoryOfList(listFact);
//			if (model.isFree()) {
//				for (SingleCarparkInOutHistory singleCarparkInOutHistory : listFree) {
//					if (!StrUtil.isEmpty(map.get(singleCarparkInOutHistory.getId()))) {
//						singleCarparkInOutHistory.setReturnAccount(saveReturnAccount);
//					}
//					singleCarparkInOutHistory.setFreeReturnAccount(saveReturnAccount);
//					// singleCarparkInOutHistory.setOperaName(model.getOperaName());
//				}
//				carparkInOutService.saveInOutHistoryOfList(listFree);
//			}
			this.model.setTotalCharge(carparkInOutService.findFactMoneyByName(userName));
			this.model.setTotalFree(carparkInOutService.findFreeMoneyByName(userName));
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "归账："+a.getOperaName()+"归给"+a.getReturnUser(), a.getOperaName());
			commonui.info("成功", "归账成功");
		} catch (Exception e) {
			log.error("归账时发生错误", e);
			commonui.error("失败", "归账是发生错误！"+e);
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
			if(device.getScreenType().equals(ScreenTypeEnum.一体机)){
				return true;
			}
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
	
	public boolean showContentToAllInOneDevice(String plate,SingleCarparkDevice device, String content, boolean isOpenDoor){
		return carparkScreenService.carIn(getDevice(device), plate, content, isOpenDoor);
	}
	public boolean showContentToDevice(SingleCarparkDevice device, String content, boolean isOpenDoor){
		return showContentToDevice("", device, content, isOpenDoor);
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
	public boolean showContentToDevice(String plate,SingleCarparkDevice device, String content, boolean isOpenDoor) {
		boolean b = showContentToDeviceOnce(plate, device, content, isOpenDoor);
		if (!b) {
			b = showContentToDeviceOnce(plate, device, content, isOpenDoor);
		}
		return b;
	}
	public boolean showContentToDeviceOnce(String plate,SingleCarparkDevice device, String content, boolean isOpenDoor){
		if (StrUtil.isEmpty(content)) {
			return true;
		}
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Device d = getDevice(device);
			log.info("对设备：ip:{},kip:{} 开闸:{},发送车牌：[{}]语音：[{}]", device.getIp(), device.getLinkAddress(),isOpenDoor,plate, content);	
			if (isOpenDoor) {
				if (d != null) {
					if(device.getScreenType().equals(ScreenTypeEnum.一体机)){
						return showContentToAllInOneDevice(plate, device, content, isOpenDoor);
					}
					Boolean carparkContentVoiceAndOpenDoor = hardwareService.carparkContentVoiceAndOpenDoorWithDelay(d, content, device.getVolume() == null ? 1 : device.getVolume(), openDoorDelay);
					return carparkContentVoiceAndOpenDoor;
				} else {
					openDoor(device);
				}
			} else {
				if (d != null) {
					if(device.getScreenType().equals(ScreenTypeEnum.一体机)){
						return showContentToAllInOneDevice(plate, device, content, isOpenDoor);
					}
					return hardwareService.carparkContentVoice(d, content, device.getVolume() == null ? 1 : device.getVolume());
				}
			}
			return true;
		} catch (Exception e) {
			log.error("对设备" + device.getIp() + "的控制器"+device.getLinkAddress()+"开闸失败", e);
			return false;
		}finally{
			if (isOpenDoor) {
				openDoorToPhotograph(device.getIp());
			}
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
				if(device.getScreenType().equals(ScreenTypeEnum.一体机)){
					return carparkScreenService.showCarparkPosition(d, position);
				}
				String inType = device.getInType();
				log.debug("向{}设备{}：{}发送车位数:{}",inType,device.getIp(),device.getLinkInfo(),position);
				if (inType.indexOf("出口")>-1) {
					inType = "出口";
				}
				if (inType.equals("进口2")) {
					inType = "进口";
				}
				int type = device.getScreenType().getType();
				System.out.println(type);
				return hardwareService.carparkPosition(d, position, LPRInOutType.valueOf(inType), (byte) type);
			} else {
				return true;
			}
		} catch (Exception e) {
			log.error("显示车位时发生错误", e);
			return false;
		} finally {
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
		if (device.getInType().indexOf("进口") < 0) {
			return;
		}
		if (checkDeviceLinkStatus(device)) {
			return;
		}
		try {
			Device d = getDevice(device);
			if (d != null) {
				hardwareService.carparkPosition(d, position);
			}
		} catch (Exception e) {
			log.error("发送车位(无返回)时发生错误", e);
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
		link.setTimeOut(timeOut);
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
			log.info("对设备：{} 的控制器：{} 进行开闸",device.getIp(),device.getLinkAddress());
			Device d = getDevice(device);
			boolean carparkOpenDoor = true;
			if (d != null) {
				if (device.getScreenType().equals(ScreenTypeEnum.一体机)) {
					carparkOpenDoor = carparkScreenService.screenOpenDoor(d, 0);
				}else{
    				carparkOpenDoor = hardwareService.carparkOpenDoor(d);
    				// carparkOpenDoor = hardwareService.carparkControlDoor(getDevice(device), 0, -1, -1, -1);
				}
			}
			openDoorToPhotograph(device.getIp());
			return carparkOpenDoor;
		} catch (Exception e) {
			return false;
		}
		// return true;
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
			Boolean carparkOpenDoor = true;
			if (d != null) {
				if (device.getScreenType().equals(ScreenTypeEnum.一体机)) {
					return carparkScreenService.screenOpenDoor(d, 1);
				}
				carparkOpenDoor = hardwareService.carparkControlDoor(d, -1, 0, -1, -1);
			}
			return carparkOpenDoor;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 设备车队操作
	 * 
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
			Boolean carparkOpenDoor = true;
			if (d != null) {
				int carTeamTime = 0;
				String content = "开启了车队";
				model.setIsOpenFleet(isopen);
				if (!isopen) {
					carTeamTime = 1;
					content = "停止了车队";
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
	 * 
	 * @param ip
	 */
	public void openDoorToPhotograph(String ip) {
		String property = System.getProperty(PlateNOJNA.CAMERA_OPEN_DOOR);
		if (property != null && property.equals("false")) {
			log.info("软件触发摄像机开闸：{}", false);
			return;
		}
		log.info("软件触发摄像机{}开闸",ip);
		mapIpToJNA.get(ip).openDoor(ip);
	}

	public boolean showUsualContentToDevice(SingleCarparkDevice device) {
		if (checkDeviceLinkStatus(device)) {
			return false;
		}
		try {
			Device d = getDevice(device);
			Boolean carparkUsualContent = true;

			if (d != null) {
				if(device.getScreenType().equals(ScreenTypeEnum.一体机)){
					return carparkScreenService.showCarparkUsualContent(d, device.getAdvertise());
				}
				carparkUsualContent = hardwareService.carparkUsualContent(d, device.getAdvertise());
			}
			return carparkUsualContent;
		} catch (Exception e) {
			log.error("发送广告语时发生错误", e);
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
	public float countShouldMoney(Long carparkId, String carType, Date startTime, Date endTime,SingleCarparkInOutHistory data) {
		if (endTime.before(startTime)) {
			throw new RuntimeException(StrUtil.formatString("车辆：{}出场失败,出场时间：{}在进场时间：{}前", data.getPlateNo(),StrUtil.formatDateTime(endTime),StrUtil.formatDateTime(endTime)));
		}
		if(data!=null&&Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.优先使用云平台计费))&&model.booleanSetting(SystemSettingTypeEnum.启用CJLAPP支付)){
			Result result = getPayResult(data);
			log.info("云平台计费：{}",result.getObj());
			if (result!=null&&result.getObj()!=null) {
				if(3004!=result.getCode()&&9999!=result.getCode()){
					model.setChargedMoney(result.getPayedFee());
					model.setShouldMony(result.getDeptFee()+result.getPayedFee());
					data.setShouldMoney(result.getDeptFee()+result.getPayedFee());
					model.setReal(result.getDeptFee());
					if (result.getOutTime()!=null) {
						model.setOutTime(result.getOutTime());
						data.setOutTime(result.getOutTime());
					}
					data.setFreeMoney(result.getCouponValue());
					if(result.getCode()==2005){
						model.setReal(0);
						model.setChargedMoney(result.getPayedFee());
						model.setShouldMony(result.getPayedFee());
						data.setFreeMoney(result.getCouponValue());
						data.setShouldMoney(result.getPayedFee());
						data.setFactMoney(result.getPayedFee()-result.getCouponValue());
						data.setChargeOperaName("在线支付");
						data.setRemarkString("在线缴费完成，在规定时间内出场！");
						data.setChargedType(1);
						model.setPlateNo(data.getPlateNo()+"-已在线支付");
					}
					return result.getDeptFee()+result.getPayedFee();
				}
			}
		}
		float charge = 0;
		try {
			charge = countCharge(carparkId, carType, startTime, endTime, data);
			float charged=0;
			float freed=0;
			List<CarPayHistory> list=sp.getCarPayService().findCarPayHistoryByHistoryId(data.getId());
			for (CarPayHistory pay : list) {
				charged+=(pay.getBalanceAmount()+pay.getCashCost()+pay.getOnlineCost());
				freed+=pay.getCouponValue();
				data.setChargedType(pay.getPayType().toType());
				data.setRemarkString(pay.getRemark());
			}
			data.setFactMoney(charged);
			data.setFreeMoney(freed);
			model.setChargedMoney(charged+freed);
		} catch (Exception e1) {
			log.error("计算收费时发生错误", e1);
		}

		return charge;
	}

	/**
	 * @param carparkId
	 * @param carType
	 * @param startTime
	 * @param endTime
	 * @param data
	 * @return
	 */
	public float countCharge(Long carparkId, String carType, Date startTime, Date endTime, SingleCarparkInOutHistory data) {
		float charge;
		int freeMinute = 0;
		int freeMoney = 0;
		// 获取临时车优惠
		SingleCarparkFreeTempCar findTempCarFreeByPlateNO = sp.getCarparkInOutService().findTempCarFreeByPlateNO(data.getPlateNo());
		if (findTempCarFreeByPlateNO != null && findTempCarFreeByPlateNO.getStatus()) {
			freeMinute = findTempCarFreeByPlateNO.getFreeMinute();
			freeMoney = findTempCarFreeByPlateNO.getFreeMoney();
		}
		startTime = new DateTime(startTime).plusMinutes(freeMinute).toDate();
		charge = countTempCarCharge.charge(carparkId, model.getMapTempCharge().get(carType).getId(), startTime, endTime, sp, model, Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.停车场重复计费)));
		log.info("{} 计费成功，进场时间：{} 出场时间：{} 费用：{}",data.getPlateNo(),startTime,endTime,charge);
		charge = charge - freeMoney;
		if(charge>0&&model.booleanSetting(SystemSettingTypeEnum.启用CJLAPP支付)&&!model.booleanSetting(SystemSettingTypeEnum.优先使用云平台计费)) {
			double fee=Double.valueOf(charge+"");
			new Thread(new Runnable() {
				public void run() {
					try {
						log.info("通知平台车辆：{} 本地算出费用：{} ",data.getPlateNo(),fee);
						ipmsService.pustFee(model.getCarpark().getYunIdentifier() + data.getId(), fee);
					} catch (Exception e) {
						log.error("通知平台车辆："+data.getPlateNo()+" 本地算出费用失败 ",e);
						MessageUtil.info("外网故障,请检查网络");
					}
				}
			}).start();
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
		String oldUserName = ConstUtil.getUserName();
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
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "换班："+oldUserName+"换成"+userName, userName);
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
		showApp.setPresenter(Login.injector.getInstance(UserPresenter.class));
		showApp.setPresenter(Login.injector.getInstance(AccountCarPresenter.class));
		app = showApp;
		app.open();
	}

	/**
	 * 手动抓拍
	 */
	public void handPhotograph(String ip) {
		mapIpToJNA.get(ip).tigger(ip);
//		byte[] bs = FileUtils.readFile("D:\\img\\20161122111651128_粤BD021W_big.jpg");
//		//贵A56G17贵JRJ927
//		carInOutResultProvider.get().invok(ip, 0, "粤BD021W", bs, null, 11);
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
							log.error("图片上传到服务器" + ip + "失败", e);
						} finally {
							log.info("上传图片花费时间：{}", System.nanoTime() - nanoTime);
						}
					}
				} catch (IOException e) {
					log.error("上传图片出错", e);
				}
			}
		};

		saveImageTheadPool.submit(runnable);
	}

	// 初始化
	public void init() {
		ipmsService = sp.getIpmsService();
		mapDeviceType = model.getMapDeviceType();
		mapDeviceTabItem = model.getMapDeviceTabItem();
		mapIpToDevice = model.getMapIpToDevice();
		mapSystemSetting = model.getMapSystemSetting();
		refreshSystemSetting();
//		initVioce();
		saveImageTheadPool = Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("保存图片任务"));
		openDoorTheadPool = Executors.newCachedThreadPool(ThreadUtil.createThreadFactory("开门任务"));
//		checkPlayerPlaying();
		countTempCarCharge = new CountTempCarChargeImpl();
		autoCheckDeviceLinkInfo();
		setIsTwoChanel();
		String userName = System.getProperty("userName");
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.登录登出, "登录了监控界面", userName);
		model.setUserName(userName);
		model.setWorkTime(new Date());
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		model.setHoursSlot(carparkInOutService.findTempSlotIsNow(model.getCarpark()));
		model.setMonthSlot(carparkInOutService.findFixSlotIsNow(model.getCarpark()));
		model.setTotalCharge(carparkInOutService.findFactMoneyByName(userName));
		model.setTotalFree(carparkInOutService.findFreeMoneyByName(userName));
		model.setTotalSlot(getSlotOfLeft());
		
		log.info("查找停车场:{} 的收费设置",model.getCarpark());
		List<CarparkChargeStandard> listTemp = sp.getCarparkService().findAllCarparkChargeStandard(model.getCarpark(), true);
		for (CarparkChargeStandard carparkChargeStandard : listTemp) {
			String name = carparkChargeStandard.getCarparkCarType().getName();
			model.getMapTempCharge().put(name, carparkChargeStandard.getCarparkCarType());
		}
		try {
			openDoorDelay = Integer.valueOf(System.getProperty(ConstUtil.OPEN_DOOR_DELAY));
		} catch (NumberFormatException e) {
		}
		try {
			String property = System.getProperty("timeOut");
			timeOut = Long.valueOf(property);
		} catch (Exception e) {
		}
		log.debug("超时时间timeOut为：{}", timeOut);
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					log.debug("检测设备状态");
					if(System.getProperty("checkDeviceStatus", "true").equals("false")) {
						return;
					}
					for (String ip : mapIpToDevice.keySet()) {
						checkDeviceStatus(ip);
					}
				} catch (Exception e) {
					log.error("检测设备时发生错误",e);
				}
			}
		}, 20, 20, TimeUnit.SECONDS,"自动检测设备连接状态");
		// 车牌自动下载
		startAutoDownloadPlatesService();
		
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				checkAllDeviceControlTimeStatus();
			}
		}, 60 - DateTime.now().getSecondOfMinute(), 60, TimeUnit.SECONDS,"检查设备是否限时");
		
		if (mapSystemSetting.get(SystemSettingTypeEnum.保存遥控开闸记录).equals("true")) {
			CarparkUtils.startServer(10002, "/*", new OpenDoorServlet(this));
		}
		
		startShowInHistoryTask();
		startQrCodeInOutService();
//		startPlatePayRemindService();
		startUpdateScreenQrCodeColorServie();
		startHttpOpenDoorService();
		startRefreshSettingService();
		startUploadLogService();
		startBroadcastService();
		
		if (System.getProperty("testInOutAndPay", "false").equals("true")) {
			testInOut("粤", "192.168.3.243");
			testInOut("贵", "192.168.3.244");
		}
		
	}
	private void startUploadLogService() {
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				uploadLogsToServer();
			}
		}, 10, 10, TimeUnit.MINUTES, "上传日志到服务器");
	}

	private void startRefreshSettingService() {
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				log.info("自动更新停车场设置");
				refreshSystemSetting();
			}
		}, 5, 5, TimeUnit.MINUTES, "自动更新设置");
	}

	/**
	 * 初始化语音信息
	 */
	private void initVioce() {
		for (DeviceVoiceTypeEnum vt : DeviceVoiceTypeEnum.values()) {
			SingleCarparkDeviceVoice value = new SingleCarparkDeviceVoice();
			value.setContent(vt.getContent());
			value.setVolume(vt.getVolume());
			model.getMapVoice().put(vt, value);
		}
		
		try {
			List<SingleCarparkDeviceVoice> findAllVoiceInfo = sp.getCarparkService().findAllVoiceInfo(DeviceVoiceTypeEnum.values());
			for (SingleCarparkDeviceVoice dv : findAllVoiceInfo) {
				model.getMapVoice().put(dv.getType(), dv);
			}
		} catch (Exception e) {
			log.error("初始化语音时发生错误",e);
		}
	}
	/**
	 * 提供http开闸服务器
	 */
	private void startHttpOpenDoorService() {
		if (mapSystemSetting.get(SystemSettingTypeEnum.启动HTTP对外服务).equals("false")) {
			return;
		}
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				String ip = sp.getCarparkDeviceService().getOpenDoorDevice(mapIpToDevice.keySet());
				if (ip==null||mapIpToDevice.get(ip)==null) {
					return;
				}
				log.info("检测到http请求{}开闸",ip);
				SingleCarparkDevice device = mapIpToDevice.get(ip);
				openDoor(device);
			}
		}, 1000, 1000, TimeUnit.MILLISECONDS, "检测http请求开闸");
	}

	/**
	 * 自动设置一体机二维码颜色
	 */
	private void startUpdateScreenQrCodeColorServie() {
		long delay=60*1000*60;
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				DateTime dateTime = new DateTime();
				DateTime startTime = dateTime.withTime(7, 0, 0, 0);
				DateTime endTime = dateTime.withTime(19, 0, 0, 0);
				log.info("设置一体机二维码颜色："+dateTime+"=="+startTime+"============="+endTime);
				int dayColor = 3;
				int nightColor = 2;
				try {
					List<String> list = java.nio.file.Files.readAllLines(Paths.get("screenQrColor.txt"),Charset.forName("GBK"));
					log.info("读取到屏幕颜色配置文件：{}",list);
					if(list.size()>0) {
						dayColor=Integer.valueOf(list.get(0));
					}
					if(list.size()>1) {
						nightColor=Integer.valueOf(list.get(1));
					}
				} catch (Exception e) {
					log.error("读取二维码颜色配置文件失败",e);
				}
				if (dateTime.plusMinutes(1).isAfter(startTime)&&dateTime.plusMinutes(1).isBefore(endTime)) {
					setAllScreenQrCodeColor(dayColor);
				}else{
					setAllScreenQrCodeColor(nightColor);
				}
			}
		}, 10000, delay, TimeUnit.MILLISECONDS, "自动更新一体机屏幕二维码颜色");
	}

	/**
	 * 
	 */
	public void startAutoDownloadPlatesService() {
		if (mapSystemSetting.get(SystemSettingTypeEnum.自动下载车牌).equals("true")) {
			Date todayBottomTime = StrUtil.getTodayBottomTime(new Date());
			ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						log.info("[自动下载车牌]准备下载车牌到所有摄像机");
						for (String ip : mapIpToDevice.keySet()) {
							List<SingleCarparkUser> findAll = sp.getCarparkUserService().findUserByNameOrCarpark(null, mapIpToDevice.get(ip).getCarpark(), null);
							if (StrUtil.isEmpty(findAll)) {
								continue;
							}
							List<PlateDownload> list = new ArrayList<>();
							for (SingleCarparkUser user : findAll) {
								String[] split = user.getPlateNo().split(",");
								if (split.length > 1) {
									continue;
								}
								PlateDownload pd = new PlateDownload();
								Date validTo = user.getValidTo();
								if (validTo == null || validTo.before(new Date())) {
									pd.setUse(false);
								}
								pd.setDate(validTo);
								pd.setPlate(user.getPlateNo());
								list.add(pd);
							}
							PlateNOJNA plateNOJNA = mapIpToJNA.get(ip);
							plateNOJNA.plateDownload(list, ip);
						}
					} catch (Exception e) {
						log.error("自动下载车牌时发生错误", e);
					}
				}
			}, todayBottomTime.getTime() - System.currentTimeMillis(), 1000 * 60 * 60 * 24, TimeUnit.MILLISECONDS,"每天晚上12点下载车牌");
		}
	}
	/**
	 * 车辆缴费提示服务。车辆缴费后会在客户端提醒
	 */
	protected void startPlatePayRemindService() {
		if(!mapSystemSetting.get(SystemSettingTypeEnum.启用CJLAPP支付).equals("true")||!mapSystemSetting.get(SystemSettingTypeEnum.监控界面提示网络故障).equals("true")){
			return;
		}
		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("云平台缴费记录推送服务"));
		scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				List<CarPayHistory> list=sp.getCarPayService().getCarPayHistoryWithNew();
				for (CarPayHistory carPayHistory : list) {
					String msg = carPayHistory.getPlateNO()+carPayHistory.getPayType()+carPayHistory.getPayedMoney()+"元";
					MessageUtil.info("车辆缴费提示",msg,10000,null);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	/**
	 * 二维码进出服务
	 */
	private void startQrCodeInOutService() {
		if(!mapSystemSetting.get(SystemSettingTypeEnum.启用CJLAPP支付).equals("true")){
			return;
		}
		try {
			SingleCarparkCarpark carpark = model.getCarpark().getMaxParent();
			String buildId = carpark.getYunBuildIdentifier();
			if (StrUtil.isEmpty(buildId)||StrUtil.isEmpty(carpark.getYunIdentifier())) {
				log.info("停车场云项目编号为：[{}]，不启动二维码进出服务",buildId);
				return;
			}
			try {
				ipmsService.startQrCodeInOutService(buildId);
//				MessageUtil.info("提示", "二维码进出场服务启动成功！", 5000);
			} catch (Exception e2) {
				MessageUtil.info("启动二维码实时进出场服务失败！请检查停车场云编号是否正确或检查网络!软件将在后台重连！");
			}
			ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
				String lastPlateInfo="";
				@Override
				public void run() {
					try {
						ipmsService.startQrCodeInOutService(buildId);
						List<String> arrayList = new ArrayList<>();
						Set<String> keySet = model.getMapWaitInOutHistory().keySet();
						for (String string : keySet) {
							SingleCarparkInOutHistory history = model.getMapWaitInOutHistory().get(string);
							arrayList.add(history.getPlateNo());
						}
						if (arrayList.size()>0&&!lastPlateInfo.equals(arrayList.toString())) {
							log.info("准备获取车牌:{} 的缴费记录",arrayList);
							lastPlateInfo=arrayList.toString();
						}
						String qrCodeInOutInfo = ipmsService.getQrCodeInOutInfo(mapIpToDevice.keySet(),arrayList);
						if (qrCodeInOutInfo==null) {
							return;
						}
						qrCodeInOutTask(qrCodeInOutInfo);
						if (checkIsPayTimer!=null) {
							checkIsPayTimer.cancel();
						}
					} catch (Exception e) {
						log.error("二维码进出场时发生错误！"+e);
						if (e instanceof HessianRuntimeException) {
							try {
								ipmsService.startQrCodeInOutService(buildId);
								log.info("二维码进出服务重连成功！");
							} catch (Exception e1) {
								
							}
							return;
						}
					}
				}
			}, 3000, 3000, TimeUnit.MILLISECONDS,"获取二维码进出场信息服务");
//			carparkQrCodeInOutService.initService(buildId,new CarparkQrCodeInOutService.CarparkQrCodeInOutCallback() {
//				@Override
//				public void call(String info) {
//					try {
//						info=info.trim();
//						JSONObject jsonObject = JSONObject.parseObject(info);
//						String type = jsonObject.getString("type");
//						if (type.equals("PONG_MSG")) {
//							return;
//						}
//						qrCodeInOutTask(info);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			});
		} catch (Exception e) {
			log.error("启动二维码进出场服务失败！",e);
		}
	}

	/**
	 * 启动场内车显示服务
	 */
	private void startShowInHistoryTask() {
		if (mapSystemSetting.get(SystemSettingTypeEnum.显示指定停留时间的场内车).equals("true")) {
			CarInHistoryPresenter presenter = Login.injector.getInstance(CarInHistoryPresenter.class);
			ShowApp showApp = new ShowApp();
			showApp.setPresenter(presenter);
			showApp.setMaximized(false);
			AtomicBoolean isFirstOpen=new AtomicBoolean(true);
			ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					if(showApp.isOpen()){
						return;
					}
					int[] setting = presenter.getSetting();
					String carType=null;
					switch (setting[0]) {
					case 0:
						carType=null;
						break;
					case 1:
						carType="固定车";
						break;
					case 2:
						carType="临时车";
						break;

					}
					Date startTime = null;
					
					int maxStayMinute=setting[2];
					if (maxStayMinute > 0) {
						startTime = new DateTime().minusMinutes(maxStayMinute).toDate();
					}
					Date endTime = null;
					int minStayMinute=setting[1];
					if (minStayMinute >= 0) {
						endTime = new DateTime().minusMinutes(minStayMinute).toDate();
					}
					List<SingleCarparkInOutHistory> list = sp.getCarparkInOutService().findHistoryByIn(0, 1, null, carType, startTime, endTime);
					
					if(StrUtil.isEmpty(list)&&!isFirstOpen.get()){
						return;
					}
					
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							showApp.open();
						}
					});
					isFirstOpen.set(false);

				}
			}, 5, 5, TimeUnit.SECONDS,"场内车停留时间显示任务");
		}
	}

	/**
	 * 检测单个设备的时间限制情况
	 * 
	 * @param d
	 * @param device
	 * @return
	 */
	public boolean checkDeviceControlTimeStatus(Date d, SingleCarparkDevice device) {
		if (mapSystemSetting.get(SystemSettingTypeEnum.允许设备限时).equals("false")) {
			return true;
		}
		log.debug("检测设备{}限时状态", device.getIp());
		String controlTime = device.getControlTime();
		Date date = new Date();
		Holiday findHolidayByDate = sp.getCarparkService().findHolidayByDate(date);
		if (findHolidayByDate!=null&&!StrUtil.isEmpty(device.getHolidayControlTime())) {
			controlTime=device.getHolidayControlTime();
			log.info("{}为节假日,限制时间为：{}",date,controlTime);
		}
		String ip = device.getIp();
		if (StrUtil.isEmpty(controlTime)||!model.booleanSetting(SystemSettingTypeEnum.允许设备限时)) {
			model.getMapIpToDeviceStatus().put(ip, true);
			log.debug("设备{}未做限时设置:{}", device.getIp(),model.booleanSetting(SystemSettingTypeEnum.允许设备限时));
			return true;
		}
		String[] split = controlTime.split(",");
		boolean status = true;
		for (String string : split) {
			String[] split2 = string.split("-");
			if (split2.length != 2) {
				continue;
			}
			try {
				String st = StrUtil.formatDate(d) + " " + split2[0];
				String et = StrUtil.formatDate(d) + " " + split2[1];
				Date s = StrUtil.parse(st, "yyyy-MM-dd HH:mm");
				Date e = StrUtil.parse(et, "yyyy-MM-dd HH:mm");
				if (s == null || e == null) {
					status = true;
					continue;
				}
				if (d.after(s) && d.before(e)) {
					status = true;
					break;
				} else {
					status = false;
				}
			} catch (Exception e) {
				log.debug("设备{}的限时设置为：{}，格式不正确", ip, device.getControlTime());
				status = true;
				continue;
			}
		}
		if (status) {
//			setDeviceTabItemStatus(ip, "deviceStatus_16", "正在使用");
		} else {
			setDeviceTabItemStatus(ip, "distribution_device_16", "设备已限时停用,限制时间："+controlTime);
		}
		model.getMapIpToDeviceStatus().put(ip, status);
		log.debug("设备{}限时{}状态{}", ip, controlTime, status);
		return status;
	}

	/**
	 * 用来测试进出场
	 * 
	 * @param property
	 */
	private ScheduledExecutorService autoTiggerWithTest(String property) {
		Map<String, List<SingleCarparkDevice>> map = new HashMap<>();
		for (SingleCarparkDevice d : mapIpToDevice.values()) {
			String inType = d.getInType();
			List<SingleCarparkDevice> list = map.get(inType);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(d);
			map.put(inType, list);
		}
		List<List<SingleCarparkDevice>> list = new ArrayList<>();
		for (String s : map.keySet()) {
			list.add(map.get(s));
		}
		Integer testDelayTime = 10;
		try {
			testDelayTime = Integer.valueOf(property);
		} catch (NumberFormatException e) {

		}

		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			int inTypeSize = list.size();
			int nowSize = 0;

			@Override
			public void run() {
				try {
					if (nowSize >= inTypeSize) {
						nowSize = 0;
					}
					List<SingleCarparkDevice> list2 = list.get(nowSize);
					for (SingleCarparkDevice d : list2) {
						System.out.println(d.getIp());
						handPhotograph(d.getIp());
					}
					nowSize++;
				} catch (Exception e) {
				}
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
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
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
		}, 10, 10, TimeUnit.SECONDS,"自动检测设备的连接状态");
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
			log.info("人工查找车牌：{},",plateNO);
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
				sp.getCarparkInOutService().saveInOutHistory(select);
			}
			log.info("人工查找到设备：{} 车牌：{}",model.getIp(),select.getPlateNo());
			carInOutResultProvider.get().invok(model.getIp(), 0, select.getPlateNo(), m.getBigImg(), m.getSmallImg(), 1);
		} catch (Exception e) {
			log.error("人工查找时发生错误", e);
		}
	}

	/**
	 * 检查车牌识别间隔,现在时间在间隔时间内返回false
	 * 
	 * @param plateNO
	 */
	public boolean checkPlateNODiscernGap(Map<String, Date> mapPlateNoDate, String plateNO, Date nowDate) {
		if (StrUtil.isEmpty(plateNO)) {
			return true;
		}
		Date date = mapPlateNoDate.get(plateNO);
		if (date != null) {
			String s = mapSystemSetting.get(SystemSettingTypeEnum.同一车牌识别间隔) == null ? SystemSettingTypeEnum.同一车牌识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.同一车牌识别间隔);
			log.debug("同一车牌识别间隔为：{}", s);
			Integer timeGap = Integer.valueOf(s);
			long abs = Math.abs(date.getTime() - nowDate.getTime());
			if (abs < timeGap * 1000) {
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
//				log.info("对设备{}，地址{}-{}开闸", device.getName(), device.getLinkAddress(), device.getAddress());
//				showPlateNOToDevice(device, "手动开闸");
//				if (inOrOut) {
//					showContentToDevice("手动开闸",device, model.getMapVoice().get(DeviceVoiceTypeEnum.进口开闸语音).getContent(), false);
//				} else {
//					showContentToDevice("手动开闸",device, model.getMapVoice().get(DeviceVoiceTypeEnum.出口开闸语音).getContent(), false);
//				}
			}
		};
		openDoorTheadPool.submit(runnable);
	}

	public Boolean showNowTimeToDevice(SingleCarparkDevice singleCarparkDevice) {
		Device device = getDevice(singleCarparkDevice);
		if(device==null) {
			return true;
		}
		if(singleCarparkDevice.getScreenType().equals(ScreenTypeEnum.一体机)){
			return carparkScreenService.setDeviceDateTime(device, new Date());
		}
		return hardwareService.setCarparkDate(device, new Date());
	}
	
	public void charge(Boolean carOutChargeCheck) {
		charge(carOutChargeCheck, false);
	}
	/**
	 * 进行收费
	 * 
	 * @param carOutChargeCheck
	 *            是否需要确认
	 */
	public void charge(Boolean carOutChargeCheck,boolean savePay) {
		SingleCarparkInOutHistory data = model.getChargeHistory();
		SingleCarparkDevice device = model.getChargeDevice();
		if (StrUtil.isEmpty(data)) {
			return;
		}
		data.setSavePayHistory(savePay);
//		boolean checkIsPay = checkIsPay(data,model.getReal(),true);
//		if (!checkIsPay) {
//			return;
//		}
		if (!chargeCarPass(device, data, carOutChargeCheck)) {
			return;
		}
	}
	public void charge(Boolean carOutChargeCheck,boolean savePay,SingleCarparkInOutHistory data,SingleCarparkDevice device){
		if (StrUtil.isEmpty(data)) {
			return;
		}
		data.setSavePayHistory(savePay);
//		boolean checkIsPay = checkIsPay(data,model.getReal(),true);
//		if (!checkIsPay) {
//			return;
//		}
		if (!chargeCarPass(device, data, carOutChargeCheck)) {
			return;
		}
	}

	/**
	 * @param data
	 * @param real 
	 */
	public boolean checkIsPay(SingleCarparkInOutHistory data,float real, boolean check) {
		if(checkIsPayTimer!=null){
			checkIsPayTimer.cancel();
		}
		log.info("检测车辆：{}是否缴费",data.getPlateNo());
		float totalCharge=real+model.getChargedMoney();
		if (totalCharge<model.getShouldMony()&&model.getMapSystemSetting().get(SystemSettingTypeEnum.启用CJLAPP支付).equals("true")) {
			List<CarPayHistory> list = sp.getCarPayService().findCarPayHistoryByLike(0, 10, data.getPlateNo(), data.getInTime(), new Date());
			Double chargeMoney=0d;
			Double freeMoney=0d;
			String ctype=PayTypeEnum.现金支付.name();
			for (CarPayHistory carPayHistory : list) {
				chargeMoney+=(carPayHistory.getBalanceAmount()+carPayHistory.getCashCost()+carPayHistory.getOnlineCost());
				freeMoney+=carPayHistory.getCouponValue();
				ctype=carPayHistory.getPayType().name();
			}
			if (chargeMoney+freeMoney<model.getShouldMony()) {
				Result result = new Result();
				
//				if (!model.booleanSetting(SystemSettingTypeEnum.优先使用云平台计费)) {
//					result = getPayResult(data);
//				}
				if (result!=null&&result.getCode()!=3004) {
					log.info("请求 车辆缴费状态结果：{}",result.getObj());
					float payedFee = result.getPayedFee();
					float shouldMoney=data.getShouldMoney();
//					if(model.booleanSetting(SystemSettingTypeEnum.优先使用云平台计费)) {
//						shouldMoney=result.getPayedFee()+result.getDeptFee();
//					}
					if (payedFee>= shouldMoney) {
						model.setReal(0);
						model.setChargedMoney(result.getPayedFee());
						model.setShouldMony(result.getPayedFee());
						data.setFreeMoney(result.getCouponValue());
						data.setShouldMoney(payedFee>data.getShouldMoney()?payedFee:data.getShouldMoney());
						data.setFactMoney(result.getPayedFee()-result.getCouponValue());
						data.setChargeOperaName("在线支付");
						data.setRemarkString("在线缴费完成，在规定时间内出场！");
						data.setChargedType(1);
						model.setPlateNo(data.getPlateNo()+"-已在线支付");
						return true;
					}else{
						model.setPlateNo(model.getPlateNo()+"-未在线支付");
						data.setShouldMoney(shouldMoney);
						if (mapIpToDevice.get(data.getOutDeviceIp())!=null) {
    						checkIsPayTimer = new Timer();
    						int delay = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.出场时检测云平台缴费间隔))*1000;
    						int timeOut = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.出场时等待云平台缴费超时时长))*1000;
    						if (delay<=0||timeOut<=0) {
    							return true;
    						}
    						int start=delay;
    						if (!model.booleanSetting(SystemSettingTypeEnum.优先使用云平台计费)) {
    							start=500;
							}
							checkIsPayTimer.schedule(new TimerTask() {
								long startTime = System.currentTimeMillis();

								@Override
								public void run() {
									if (System.currentTimeMillis() - startTime > timeOut || !model.isBtnClick()) {
										checkIsPayTimer.cancel();
										return;
									}
									Result result = getPayResult(data);
									log.info("车辆：{} 自动查询车辆缴费信息，结果：{}",data.getPlateNo(), result.getObj());
									if (!model.getPlateNo().contains(data.getPlateNo())) {
										checkIsPayTimer.cancel();
										return;
									}
									if (result != null) {
										int code = result.getCode();
										if (code == 3004) {
												checkIsPayTimer.cancel();
												return;
										}
										 float payedFee = result.getPayedFee();
										if (payedFee>= data.getShouldMoney()) {
											model.setReal(0);
											model.setChargedMoney(result.getPayedFee());
											model.setShouldMony(result.getPayedFee());
											data.setFreeMoney(result.getCouponValue());
											data.setShouldMoney(result.getPayedFee());
											data.setFactMoney(result.getPayedFee()-result.getCouponValue());
											data.setChargeOperaName("在线支付");
											data.setRemarkString("在线缴费完成，在规定时间内出场！");
											data.setChargedType(1);
											model.setPlateNo(data.getPlateNo() + "-已在线支付");
											charge(false);
											checkIsPayTimer.cancel();
										}else {
											float totelFee = result.getPayedFee() + result.getDeptFee();
											SingleCarparkDevice device;
											if(totelFee <= model.getShouldMony()||data.getOutDeviceIp() == null||(device=mapIpToDevice.get(data.getOutDeviceIp())) == null
													||(model.booleanSetting(SystemSettingTypeEnum.使用二维码缴费)&&device.getScreenType().equals(ScreenTypeEnum.一体机))||
													!model.booleanSetting(SystemSettingTypeEnum.优先使用云平台计费)){
												
											}else {
												model.setShouldMony(totelFee);
												data.setShouldMoney(totelFee);
												model.setChargedMoney(result.getPayedFee());
												model.setReal(result.getDeptFee());
												Date outTime = new Date();
												if (result.getOutTime() != null) {
													outTime=result.getOutTime();
												}
												data.setOutTime(outTime);
												model.setOutTime(outTime);
												model.setTotalTime(StrUtil.MinusTime2(data.getInTime(), outTime));
												showContentToDevice(data.getPlateNo(), device, "请缴费" + CarparkUtils.formatFloatString(result.getDeptFee() + "") + "元",
														false);
											}
										}
									}
								}
							}, start, delay);
						}
					}
				}
			}else{
				model.setChargedMoney(chargeMoney.floatValue());
				if (chargeMoney.floatValue()+freeMoney.floatValue()>=model.getShouldMony()) {
					data.setShouldMoney(chargeMoney.floatValue() + freeMoney.floatValue());
					data.setFactMoney(chargeMoney.floatValue());
					model.setShouldMony(data.getShouldMoney());
				}
				data.setFreeMoney(freeMoney.floatValue());
				model.setReal(model.getShouldMony()-model.getChargedMoney());
				data.setChargeOperaName(ctype);
				data.setRemarkString(ctype+chargeMoney+"元");
				model.setPlateNo(model.getPlateNo()+"-"+ctype);
			}
		}
		return true;
	}

	/**
	 * 进行免费
	 * 
	 * @param carOutChargeCheck
	 *            是否需要确认
	 */
	public void free(Boolean carOutChargeCheck) {
		free(carOutChargeCheck, false);
	}
	public void free(Boolean carOutChargeCheck,boolean savePay) {
		carOutChargeCheck=mapSystemSetting.get(SystemSettingTypeEnum.出场免费确认放行).equals("true");
		SingleCarparkInOutHistory data = model.getChargeHistory();
		SingleCarparkDevice device = model.getChargeDevice();
		if (StrUtil.isEmpty(data) || StrUtil.isEmpty(device)) {
			return;
		}
		data.setSavePayHistory(savePay);
		model.setReal(0);
		boolean checkIsPay = checkIsPay(data,model.getReal(),true);
		if (!checkIsPay) {
			return;
		}
		if (!chargeCarPass(device, data, carOutChargeCheck)) {
			model.setReal(model.getShouldMony());
			return;
		}
	}
	public boolean chargeCarPass(SingleCarparkDevice device, SingleCarparkInOutHistory singleCarparkInOutHistory, boolean check){
		float shouldMoney = model.getShouldMony();
		float chargedMoney = model.getChargedMoney();
		float factMoney = model.getReal();
		return chargeCarPass(device, singleCarparkInOutHistory, check, shouldMoney, chargedMoney, factMoney, true);
	}
	/**
	 * 收费操作
	 * 
	 * @param device
	 * @param singleCarparkInOutHistory
	 * @param check
	 * @return
	 */
	public boolean chargeCarPass(SingleCarparkDevice device, SingleCarparkInOutHistory singleCarparkInOutHistory, boolean check,float shouldMoney,float chargedMoney,float factMoney,boolean updateui) {
		try {

//			if (!StrUtil.isEmpty(model.getUser())) {
//				SingleCarparkUser user = model.getUser();
//				user.setTempCarTime(CarparkUtils.removeString(user.getTempCarTime(), model.getInTime()));
//				sp.getCarparkUserService().saveUser(user);
//			}
			float shouldChargeMoney=shouldMoney-chargedMoney;
			
			if (factMoney > shouldChargeMoney) {
				commonui.error("收费提示", "实收不能超过应收" + shouldMoney + "元");
				return false;
			}
			if (factMoney < 0) {
				commonui.error("收费提示", "实收不能小于0");
				return false;
			}
			if (check) {
				if (mapSystemSetting.get(SystemSettingTypeEnum.出场确认放行).equals("true")) {
					boolean confirm = commonui.confirm("收费确认", "车牌：" + singleCarparkInOutHistory.getPlateNo() + "应收：" + shouldMoney + "实收：" + factMoney);
					if (!confirm) {
						return false;
					}
				}
				if (mapSystemSetting.get(SystemSettingTypeEnum.出场免费确认放行).equals("true")) {
					if (shouldChargeMoney > factMoney) {
						String reasons = mapSystemSetting.get(SystemSettingTypeEnum.免费原因);
						FreeReasonDialog d = new FreeReasonDialog(reasons.split(","));
						String open = d.open();
						if (open == null) {
							return false;
						}
						singleCarparkInOutHistory.setFreeReason(open);
					}
				}
			}
			log.info("车辆收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", singleCarparkInOutHistory.getPlateNo(), device.getCarpark(), model.getCarTypeEnum(), model.getInTime(), model.getOutTime(),
					model.getTotalTime(), shouldMoney);
			boolean isConcentrate = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用集中收费));
			if (isConcentrate) {
				Float factMoney2 = singleCarparkInOutHistory.getFactMoney() == null ? 0 : singleCarparkInOutHistory.getFactMoney();
				factMoney = factMoney2 + factMoney;
				if (factMoney > shouldChargeMoney) {
					commonui.error("收费提示", "总收费不能超过应收" + shouldMoney + "元");
					return false;
				}
			}
			String plateNo = singleCarparkInOutHistory.getPlateNo();
			float freeMoney = shouldChargeMoney - factMoney;
			singleCarparkInOutHistory.setShouldMoney(shouldMoney);
			singleCarparkInOutHistory.setFactMoney(chargedMoney+factMoney-singleCarparkInOutHistory.getFreeMoney());
			singleCarparkInOutHistory.setFreeMoney(singleCarparkInOutHistory.getFreeMoney()+freeMoney);
			singleCarparkInOutHistory.setCarType("临时车");
			singleCarparkInOutHistory.setLeftSlot(model.getTotalSlot()+1);
			sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
			model.getMapWaitInOutHistory().remove(device.getIp());
			Boolean tempCarNoChargeIsPass = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车零收费是否自动出场));
			String carOutMsg = model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent();
			if (tempCarNoChargeIsPass) {
				if (shouldMoney > 0) {
					showContentToDevice(plateNo,device, carOutMsg, true);
				} else {
					showContentToDevice(plateNo,device, CarparkUtils.formatFloatString("请缴费" + shouldMoney + "元") + "," + carOutMsg, true);
				}
			} else {
				showContentToDevice(plateNo,device, carOutMsg, true);
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
			if(model.getVisitor()!=null){
				sp.getCarparkService().saveVisitor(model.getVisitor());
			}
			if (updateui) {
				model.setStroeFrees(null);
				model.setBtnClick(false);
				model.setHandSearch(false);
				model.setComboCarTypeEnable(false);
				model.setChargeDevice(null);
				model.setChargeHistory(null);
				model.setOutCheckClick(false);
				model.setPlateInTime(new Date(), 5);
			}
			plateSubmit(singleCarparkInOutHistory, singleCarparkInOutHistory.getOutTime(), device, ImageUtils.getImageByte(singleCarparkInOutHistory.getOutBigImg()));
			updatePosition(device.getCarpark(), singleCarparkInOutHistory, false);
			cancelCheckChargeTimer(device);
			if(singleCarparkInOutHistory.isSaveHistory()&&singleCarparkInOutHistory.getFactMoney()>0) {
				if(model.booleanSetting(SystemSettingTypeEnum.收费放行打印小票)) {
					ExcelImportExportImpl excelImportExportImpl = new ExcelImportExportImpl();
					String path = excelImportExportImpl.exportChargeInfo(singleCarparkInOutHistory);
					excelImportExportImpl.printExcel(path);
				}
			}
			return true;
		} catch (Exception e) {
			log.error("收费时发生错误", e);
			return false;
		}
	}

	public void showHistory(SingleCarparkInOutHistory h) {
		try {
			if (h == null) {
				return;
			}
			Boolean valueOf = Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.进场允许修改车牌));
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
				String plateNo = findInOutById.getPlateNo();
				if (StrUtil.isEmpty(m)) {
					return;
				}
				findInOutById.setPlateNo(m.getNowPlateNo());
				sp.getCarparkInOutService().saveInOutHistory(findInOutById);
				h.setPlateNo(model.getNowPlateNo());
				CarparkUtils.cleanSameInOutHistory();
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "修改车牌："+findInOutById.getInTimeLabel()+"进的车辆"+plateNo+"修改为"+m.getNowPlateNo(), ConstUtil.getUserName());
			}
		} catch (Exception e) {
			log.error("修改车牌时发生错误", e);
		}
	}

	public void carInByHand() {
		try {
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
			h.setCarType("临时车");
			h.setRemarkString("手动入场");
			h.setOperaName(ConstUtil.getUserName());
			Long saveInOutHistory = sp.getCarparkInOutService().saveInOutHistory(h);
			h.setId(saveInOutHistory);
			model.addInHistorys(h);
			model.setInHistorySelect(h);
			model.setHandPlateNO(null);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "车辆手动入场："+h.getInTimeLabel()+"进的车辆"+handPlateNO, ConstUtil.getUserName());
		} catch (Exception e) {
			log.info("车辆手动入场失败：{}",e);
		}
	}

	public void saveImage(SingleCarparkDevice device,String smallImgFileName, String bigImgFileName, byte[] smallImage1, byte[] bigImage1) {
		Runnable runnable = new Runnable() {
			private int errorSize=0;
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
					} else {
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
					log.debug("保存大图片到本地：{}", bigFile);
					File smallFile = new File(finalSmallFileName);
					smallFile.createNewFile();
					Files.write(smallImage, smallFile);
					log.debug("保存小图片到本地：{}", smallFile);
//					ImgCompress.compress(smallFile.getPath());
					String ip = CarparkClientConfig.getInstance().getServerIp();
					if (true) {
						long nanoTime = System.nanoTime();
						log.debug("准备将图片{}上传到服务器{}", finalBigFileName, ip);
						try {
							String bigUpload = sp.getImageService().saveImageInServer(bigImage1, finalBigFileName);
							String smallUpload = sp.getImageService().saveImageInServer(Files.toByteArray(smallFile), finalSmallFileName);
							log.info("图片上传到服务器{}成功,{}", ip, bigUpload + "==" + smallUpload);
						} catch (Exception e) {
							log.error("图片上传到服务器" + ip + "失败", e);
						} finally {
							log.debug("上传图片花费时间：{}", System.nanoTime() - nanoTime);
						}
					}
					saveImageHistory(device,bigImgFileName,smallImgFileName);
				} catch (IOException e) {
					log.error("上传图片出错", e);
					if (errorSize<3) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						saveImageTheadPool.submit(this);
					}
					errorSize++;
				}
			}
		};
		saveImageTheadPool.submit(runnable);
	}

	protected void saveImageHistory(SingleCarparkDevice device, String bigImgFileName, String smallImgFileName) {
		try {
			int indexOf = bigImgFileName.indexOf("_");
			String plate=bigImgFileName.substring(indexOf+1, bigImgFileName.lastIndexOf("_"));
			SingleCarparkImageHistory ih=new SingleCarparkImageHistory();
			ih.setBigImage(bigImgFileName);
			ih.setSmallImage(smallImgFileName);
			ih.setPlateNO(plate);
			if(device!=null){
				ih.setDeviceIp(device.getIp());
				ih.setDeviceName(device.getName());
			}
			String sTime = bigImgFileName.substring(bigImgFileName.lastIndexOf("/")+1, indexOf);
			DateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
			Date date = df.parse(sTime);
			ih.setTime(date);
			ih.setType("原始");
			sp.getCarparkInOutService().saveImageHistory(ih);
		} catch (Exception e) {
			log.error("保存图片记录时发生错误"+e,e);
		}
		
	}

	/**
	 * 释放资源
	 */
	public void systemExit() {
		if (app!=null) {
			app.close();
		}
		openDoorTheadPool.shutdownNow();
		saveImageTheadPool.shutdownNow();
//		checkCameraPlayStatus.shutdownNow();
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.登录登出, "退出了监控界面", System.getProperty(ConstUtil.USER_NAME));
	}

	public void refreshCarWithIn() {
		try {
			model.setInHistorys(new ArrayList<>());
			model.setInHistorys(sp.getCarparkInOutService().findCarInHistorys(50));
		} catch (Exception e) {
			log.error("刷新进场记录是发生错误", e);
		}
	}

	/**
	 * 固定车在非所属停车场停留超时收费
	 * 
	 * @param user
	 * @param device
	 * @param inTime
	 * @param date
	 * @param plateNO
	 * @return
	 */
	public float countFixCarShouldMoney(SingleCarparkUser user, SingleCarparkDevice device, Date inTime, Date outTime, String plateNO) {
		List<CarparkStillTime> list = sp.getCarparkInOutService().findCarparkStillTime(plateNO, inTime);
		int minute = 0;
		Date date = new Date();
		int canStillMinute = Integer.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车非所属停车场停留时间));

		for (CarparkStillTime cs : list) {
			if (cs.getCarparkId().equals(user.getCarpark().getId())) {
				continue;
			}
			Date outTime2 = cs.getOutTime();
			int minusMinute = cs.getStillSecond();
			if (outTime2 == null) {
				minusMinute = CarparkUtils.countTime(cs.getInTime(), date, TimeUnit.MINUTES);
				System.out.println(inTime + "-" + date + "====" + minusMinute);
			}
			if (minusMinute > canStillMinute) {
				minute += (minusMinute - canStillMinute);
			}
			System.out.println(cs + "======" + minusMinute);
		}
		if (minute > StrUtil.countTime(inTime, outTime, TimeUnit.MINUTES)) {
			log.info("计算失败返回0");
			return 0;
		}
		log.info("车牌：{}，在所属停车场外停留时间：{}", plateNO, minute);
		float calculateTempCharge = sp.getCarparkService().calculateTempCharge(device.getCarpark().getId(), user.getCarType().index(), inTime, new DateTime(inTime).plusMinutes(minute).toDate());
		if (calculateTempCharge > 0) {
			Boolean open = new ConfimBox("提示", "固定车[" + plateNO + "]在所属停车场外停留" + minute + "分钟，收费" + calculateTempCharge + "元,是否收费？").open();
			if (!open) {
				return 0;
			}
		}
		log.info("固定车{}，缴费：{}", plateNO, calculateTempCharge);
		return calculateTempCharge;
	}

	/**
	 * 车牌报送
	 * 
	 * @param cch
	 * @param date
	 * @param carpark
	 * @param device
	 * @param bigImage
	 */
	public void plateSubmit(SingleCarparkInOutHistory cch, Date date, SingleCarparkDevice device, byte[] bigImage) {
		if ("false".equals(mapSystemSetting.get(SystemSettingTypeEnum.启用车牌报送))) {
			return;
		}
		PlateSubmitServiceI plateSubmitService = sp.getPlateSubmitService();
		plateSubmitService.submitPlate(cch.getPlateNo(), date, bigImage, device);
	}

	/**
	 * 获取剩余车位数
	 * 
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
			findTotalSlotIsNow = getFixSlotWithChange();
			model.setTotalSlotTooltip("固定车位");
			break;
		case "2":
			findTotalSlotIsNow = getTotalSlotWithChange();
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
		case "6":
			findTotalSlotIsNow = getLocalSlot();
			break;
		}

		return findTotalSlotIsNow;
	}

	private Integer getLocalSlot() {
		model.setTotalSlotTooltip("本地实时车位数，双击修改！");
		Integer localSlot=(Integer) FileUtils.readObject("localSlot");
		if (localSlot==null) {
			localSlot=0;
		}
		return localSlot;
	}

	/**
	 * @param findTotalSlotIsNow
	 * @return
	 */
	private Integer getRealTineSlot(int type) {
		Integer findTotalSlotIsNow = 0;
		try {
			SingleCarparkCarpark carpark = model.getCarpark().getMaxParent();
			SingleCarparkCarpark findCarparkById = sp.getCarparkService().findCarparkById(carpark.getId());
			if (type == 2) {
				findTotalSlotIsNow = findCarparkById.getLeftFixNumberOfSlot();
				model.setTotalSlotTooltip("实时固定车位，双击进行修改");
			} else if (type == 1) {
				findTotalSlotIsNow = findCarparkById.getLeftTempNumberOfSlot();
				model.setTotalSlotTooltip("实时临时车位，双击进行修改");
			} else {
				findTotalSlotIsNow = findCarparkById.getLeftTempNumberOfSlot() + findCarparkById.getLeftFixNumberOfSlot();
				model.setTotalSlotTooltip("实时总车位，双击进行修改");
			}
		} catch (Exception e) {
			log.error("获取车位时发生错误", e);
		}
		return findTotalSlotIsNow;
	}

	/**
	 * 获取剩余的总车位数
	 * 
	 * @return
	 */
	private Integer getTotalSlotWithChange() {
		try {
			int findTotalCarIn = sp.getCarparkInOutService().findTotalCarIn(model.getCarpark());

			int i = model.getHoursSlot() + model.getMonthSlot() - findTotalCarIn;
			return i < 0 ? 0 : i;
		} catch (Exception e) {
			log.error("获取停车场总车位是发生错误", e);
		}
		return 0;
	}

	/**
	 * 获取固定车剩余位数
	 * 
	 * @return
	 */
	private Integer getFixSlotWithChange() {
		int findTotalFixCarIn = sp.getCarparkInOutService().findTotalFixCarIn(model.getCarpark());
		int monthSlot = model.getMonthSlot();
		int i = monthSlot - findTotalFixCarIn;
		return i < 0 ? 0 : i;
	}

	public void saveFleetInOutHistory(SingleCarparkDevice device, String plateNO, byte[] bigImage) {
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.车队操作, "车辆：{}在车队期间从设备[{}]{}场", bigImage, System.getProperty("userName"), plateNO, device.getName(),
				device.getInType().substring(0, 1));
	}
	/**
	 * 进出后的操作
	 * @param carpark
	 * @param cch
	 * @param inOrOut
	 */
	public void updatePosition(final SingleCarparkCarpark carpark, final SingleCarparkInOutHistory cch, final boolean inOrOut) {
		new Thread(new Runnable() {
			public void run() {
				try {
					sp.getPositionUpdateService().updatePosion(carpark, cch == null ? null : cch.getUserId(), inOrOut);
					Integer slotShowType = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位数显示方式));
					if (!StrUtil.isEmpty(model.getTotalSlotTooltip()) && slotShowType == 6) {
						if (inOrOut) {
							setLocalSlot(getLocalSlot() - 1);
						} else {
							setLocalSlot(getLocalSlot() + 1);
						}
					}
					if (cch != null) {
						boolean start = ShanghaiYunCarparkCfg.getInstance().isStart();
						log.info("上海云停车场服务:{}", start);
						if (start) {
							if (inOrOut) {
								YunCarparkCarInOut inout = new YunCarparkCarInOut();
								inout.setCarNumber(cch.getPlateNo());
								inout.setCarType(1);
								inout.setParkingActType(StrUtil.isEmpty(cch.getUserName()) ? 1 : 0);
								inout.setParkingTime(cch.getInTime());
								inout.setParkingBatchCode(StrUtil.formatDate(cch.getInTime(), "yyyyMMddHHmm"));
								inout.setInType(HistoryUseStatus.待处理);
								sp.getYunCarparkService().saveCarparkCarInOut(inout);
							} else {
								YunCarparkCarInOut inOut = sp.getYunCarparkService().findInCarHistoryByPlate(cch.getPlateNo());
								if (inOut == null) {
									return;
								}
								inOut.setLeavingActType(StrUtil.isEmpty(cch.getUserName()) ? 1 : 0);
								inOut.setLeavingTime(cch.getOutTime());
								inOut.setFactMoney((int) (cch.getShouldMoney() * 100));
								inOut.setDueMoney(inOut.getFactMoney());
								inOut.setPayMoney((int) (cch.getFactMoney() * 100));
								inOut.setPayDiscount((int) (cch.getFreeMoney() * 100));
								inOut.setParkingTimeLength(StrUtil.countTime(cch.getInTime(), cch.getOutTime(), TimeUnit.SECONDS));
								inOut.setLeavingBatchCode(StrUtil.formatDate(cch.getOutTime(), "yyyyMMddHHmm"));
								inOut.setOutType(HistoryUseStatus.待处理);
								inOut.setFullType(HistoryUseStatus.待处理);
								sp.getYunCarparkService().saveCarparkCarInOut(inOut);
							}
						}
					}
				} catch (Exception e) {
					log.error("更新车位数时发生错误", e);
				}
			}
		}).start();
		
	}

	public void editPosition() {
		SingleCarparkCarpark carpark = model.getCarpark().getMaxParent();
		Integer slotShowType = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位数显示方式));
		if (StrUtil.isEmpty(model.getTotalSlotTooltip()) || slotShowType < 3) {
			return;
		}
		
		String local = null;
		if (slotShowType == 6) {
			Integer fixSlot=getLocalSlot();
			local = commonui.input("车位数修改", "输入剩余车位数", "" + fixSlot);
			if (local == null) {
				return;
			}
			try {
				fixSlot = Integer.valueOf(local);
				setLocalSlot(fixSlot);
				return;
			} catch (NumberFormatException e) {
				commonui.error("错误", "请设置正确的车位数");
				return;
			}

		}
		SingleCarparkCarpark c = sp.getCarparkService().findCarparkById(carpark.getId());
		Integer fixSlot = c.getTrueLeftFixNumberOfSlot() == null ? c.getFixNumberOfSlot() : c.getTrueLeftFixNumberOfSlot();
		Integer tempSlot = c.getTrueLeftTempNumberOfSlot() == null ? c.getTempNumberOfSlot() : c.getTrueLeftTempNumberOfSlot();
		
		
		String fix = null;
		if (slotShowType == 4 || slotShowType == 5) {
			fix = commonui.input("车位数修改", "输入新的剩余固定车位数", "" + fixSlot);
			if (fix == null) {
				return;
			}
			try {
				fixSlot = Integer.valueOf(fix);
			} catch (NumberFormatException e) {
				commonui.error("错误", "请设置正确的车位数");
				return;
			}

		}
		String temp = null;
		if (slotShowType == 3 || slotShowType == 5) {
			temp = commonui.input("车位数修改", "输入新的剩余临时车位数", "" + tempSlot);
			if (temp == null) {
				return;
			}
			try {
				tempSlot = Integer.valueOf(temp);
			} catch (NumberFormatException e) {
				commonui.error("错误", "请设置正确的车位数");
				return;
			}
		}
		c.setLeftFixNumberOfSlot(fixSlot);
		c.setLeftTempNumberOfSlot(tempSlot);
		sp.getCarparkService().saveCarpark(c);
		commonui.info("成功", "修改车位数成功");
		sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.停车场, "", System.getProperty("userName"));
	}

	private synchronized void setLocalSlot(Integer fixSlot) {
		if (fixSlot==null||fixSlot<0) {
			fixSlot=0;
		}
		FileUtils.writeObject("localSlot", fixSlot);
	}

	public CommonUIFacility getCommonui() {
		return commonui;
	}

	/**
	 * 检测设备状态
	 * @param ip
	 */
	public String checkDeviceStatus(final String ip) {
		log.debug("检测设备{}状态", ip);
		boolean ping = CarparkUtils.ping(ip);
		String msg = "";
		if (!ping) {
			msg = "摄像机[" + ip + "]连接失败\n";
		} else {
			if (mapPlayer.get(ip) != null && !mapPlayer.get(ip).isPlaying()) {
				msg = "摄像机[" + ip + "]播放失败\n";
			}
		}
		SingleCarparkDevice device = model.getMapIpToDevice().get(ip);
		DeviceErrorMessage dem = sp.getCarparkInOutService().findDeviceErrorMessageByDevice(device);
		String linkAddress = device.getLinkAddress();
		String controlIp = null;
		if (linkAddress != null && linkAddress.indexOf(":") > -1) {
			controlIp = linkAddress.substring(0, linkAddress.indexOf(":"));
			boolean ping2 = CarparkUtils.ping(controlIp);
			if (!ping2) {
				if (!StrUtil.isEmpty(msg)) {
					msg += ",";
				}
				msg += "控制器[" + controlIp + "]连接失败";
			}else{
				//TODO 不知道需不需要测试连接
//				Boolean showNowTimeToDevice = showNowTimeToDevice(device);
//				if (showNowTimeToDevice!=null&&!showNowTimeToDevice) {
//					msg += "控制器[" + controlIp + "]失败";
//				}
			}
		}
		Date date = new Date();
		boolean status = true;
		if (!StrUtil.isEmpty(msg)) {
			if (dem == null) {
				dem = new DeviceErrorMessage();
				dem.setDeviceName(device.getName());
				dem.setIp(ip);
				dem.setControlIp(controlIp);
				dem.setCheckDate(date);
			}
			dem.setErrorMsg(msg);
			sp.getCarparkInOutService().saveDeviceErrorMessage(dem);
			status = false;
		} else {
			if (dem != null) {
				dem.setNomalTime(date);
				sp.getCarparkInOutService().saveDeviceErrorMessage(dem);
			}
		}
		if (status) {
			Boolean boolean1 = model.getMapIpToDeviceStatus().getOrDefault(ip, true);
			if (boolean1) {
				setDeviceTabItemStatus(ip, "deviceStatus_16", "正在使用");
			}
		} else {
			setDeviceTabItemStatus(ip, "disconnect_16", msg);
		}
		return msg;
	}
	private void setDeviceTabItemStatus(String ip, String image, String msg) {
		setDeviceTabItemStatus(ip, image, msg, null);
	}
	private void setDeviceTabItemStatus(String ip, String image, String msg,String title) {
		CTabItem cTabItem = model.getMapIpToTabItem().get(ip);
		if (cTabItem==null||cTabItem.isDisposed()) {
			return;
		}
		Runnable runnable = new Runnable() {
			public void run() {
				if ( !cTabItem.isDisposed()) {
					if (image!=null) {
						cTabItem.setImage(JFaceUtil.getImage(image));
					}
					if (msg!=null) {
						cTabItem.setToolTipText(msg);
					}
					String text = cTabItem.getText();
					if (title!=null) {
						if (!title.equals("")) {
							text =text.split("-")[0]+ "-" + title;
						} else {
							text = text.split("-")[0];
						}
						cTabItem.setText(text);
					}
				}
			}
		};
		cTabItem.getDisplay().asyncExec(runnable);
	}

	/**
	 * 
	 */
	public void checkAllDeviceControlTimeStatus() {
		Date d = new Date();
		for (String ip : mapIpToDevice.keySet()) {
			SingleCarparkDevice device = mapIpToDevice.get(ip);
			checkDeviceControlTimeStatus(d, device);
		}
	}

	public int appPay(SingleCarparkInOutHistory cch, float shouldMoney) {
		int pay=9999;
		if (mapSystemSetting.get(SystemSettingTypeEnum.启用CJLAPP支付).equals("true")) {
			pay = ipmsService.pay(cch, shouldMoney);
			if (pay == 2005 || pay == 0) {

			} else {
				String plateNo = model.getPlateNo() == null ? "" : model.getPlateNo().split("-")[0];
				model.setPlateNo(plateNo + "-app支付失败(" + pay + ")");
			}
			return pay;
		}
		return pay;
	}

	public CarparkMainModel getModel() {
		return model;
	}

	public void editUserPassword() {
		String userName = ConstUtil.getUserName();
		SingleCarparkSystemUser systemUser = sp.getSystemUserService().findByNameAndPassword(userName, null);
		if (StrUtil.isEmpty(systemUser)) {
			return;
		}
		SingleCarparkSystemUser singleCarparkSystemUser = systemUser;
		SystemUserModel model = new SystemUserModel();
		model.setUserName(singleCarparkSystemUser.getUserName());
		model.setRemark(singleCarparkSystemUser.getRemark());
		model.setPassword(systemUser.getPassword());
		EditSystemUserPasswordWizard wizard = new EditSystemUserPasswordWizard(model);
		SystemUserModel m = (SystemUserModel) commonui.showWizard(wizard);
		if (m == null) {
			return;
		}
		singleCarparkSystemUser.setPassword(m.getPwd());
		singleCarparkSystemUser.setLastEditDate(new Date());
		singleCarparkSystemUser.setLastEditUser(userName);
		singleCarparkSystemUser.setRemark(m.getRemark());
		try {
			sp.getSystemUserService().saveSystemUser(singleCarparkSystemUser);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.系统用户, "修改了系统用户:" + singleCarparkSystemUser.getUserName(),userName);
			commonui.info("提示", "修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "修改失败！");
		}
	}

	public Result getPayResult(SingleCarparkInOutHistory cch) {
		long currentTimeMillis = System.currentTimeMillis();
		Result result =null ;
		if (mapSystemSetting.get(SystemSettingTypeEnum.启用CJLAPP支付).equals("true")) {
			log.info("准备获取:{} 云平台支付结果",cch.getPlateNo());
			result = ipmsService.getPayResult(cch);
			if(result.getObj()==null){
				for (int i = 0; i < 2; i++) {
					result = ipmsService.getPayResult(cch);
					if(result.getObj()!=null){
						break;
					}
				}
			}
			if (result.getObj()==null&&mapSystemSetting.get(SystemSettingTypeEnum.监控界面提示网络故障).equals("true")) {
				MessageUtil.info("网络故障", "外网故障！请检查网络");
				
			}
			log.info("获取云平台支付结果花费时间：{}",System.currentTimeMillis()-currentTimeMillis);
			return result;
		}
		return result;
	}
	public void qrCodeInOut(String plate,SingleCarparkDevice device,boolean inOrOut) {
		qrCodeInOut(plate, device, inOrOut, null,"请扫码出场");
	}
	/**
	 * 无牌车二维码进出场
	 * @param plate
	 * @param device
	 * @param inOrOut
	 */
	public void qrCodeInOut(String plate,SingleCarparkDevice device,boolean inOrOut,SingleCarparkInOutHistory data,String content) {
		if(!device.getScreenType().equals(ScreenTypeEnum.一体机)){
			return;
		}
		log.info("向设备发送二维码和语音：{}",content);
		String yunIdentifier = device.getCarpark().getYunIdentifier();
		String buildId = device.getCarpark().getYunBuildIdentifier();
		if(yunIdentifier==null||buildId==null){
			MessageUtil.info("停车场信息不完整，获取二维码失败！",5000);
			return;
		}
		String qrCodeUrl = carparkQrCodeInOutService.getQrCodeUrl(yunIdentifier,plate, device.getIp(), inOrOut?0:1);
		int type=0;
		if(inOrOut){
			type=2;
		}else{
			if(StrUtil.isEmpty(plate)){
				type=0;
			}else{
				type=1;
				if (data!=null&&data.getId()!=null) {
					//
					int f = (int) (data.getShouldMoney()*100);
					qrCodeUrl=qrCodeUrl+yunIdentifier+data.getId()+"&fee="+f+"&channelId="+buildId;
				}
			}
		}
//		ipmsService.long2ShortUrl(qrCodeUrl);
		String shortUrl=ipmsService.long2ShortUrl(qrCodeUrl);
		log.info("获取到二维码：{} 短连接：{}",qrCodeUrl,shortUrl);
		boolean showCarparkQrCode = carparkScreenService.showCarparkQrCode(getDevice(device), type, shortUrl,content);
		log.info("发送二维码：{} 到设备：{} 结果：{}",shortUrl,device,showCarparkQrCode);
	}

	/**
	 * @param sdf
	 * @param info
	 * @param jsonObject
	 * @param type
	 */
	public void qrCodeInOutTask(String info) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.info("云平台推送消息：[{}]",info);
		JSONObject jsonObject = JSONObject.parseObject(info);
		String type = jsonObject.getString("type");
		if (type==null) {
			return;
		}
		JSONObject data = jsonObject.getJSONObject("data");
		SingleCarparkDevice device = mapIpToDevice.get(data.getString("deviceId"));
		String plate = data.getString("carNum");
		if(type.equals("inQRCode")){//进场
			List<SingleCarparkInOutHistory> list = sp.getCarparkInOutService().findByNoOut(plate, device.getCarpark());
			if (!StrUtil.isEmpty(list)) {
//				log.info("车牌{}未出场,入场终止",plate);
//				MessageUtil.info("入场提示","车牌"+plate+"未出场,入场终止");
//				return;
				for (SingleCarparkInOutHistory singleCarparkInOutHistory : list) {
					singleCarparkInOutHistory.setOutTime(singleCarparkInOutHistory.getInTime());
					singleCarparkInOutHistory.setShouldMoney(0);
					singleCarparkInOutHistory.setFactMoney(0);
					singleCarparkInOutHistory.setFreeMoney(0);
					singleCarparkInOutHistory.setSaveHistory(false);
					sp.getCarparkInOutService().saveInOutHistory(singleCarparkInOutHistory);
				}
			}
			SingleCarparkInOutHistory inOutHistory = model.getMapWaitInOutHistory().get(device.getIp());
			inOutHistory.setInDevice(device);
			inOutHistory.setCarparkId(device.getCarpark().getId());
			inOutHistory.setCarparkName(device.getCarpark().getName());
			inOutHistory.setPlateNo(plate);
			inOutHistory.setInPlateNO(plate);
			inOutHistory.setCarType("临时车");
			Date parse=new Date();
			try {
				parse = sdf.parse(data.getString("inTime"));
			} catch (Exception e) {
				
			}
			try {
				parse = new Date(data.getJSONObject("inTime").getLongValue("time"));
			} catch (Exception e) {
				
			}
			inOutHistory.setInTime(parse);
			inOutHistory.setOperaName(ConstUtil.getUserName());
			inOutHistory.setCarpark(device.getCarpark());
			Long saveInOutHistory = sp.getCarparkInOutService().saveInOutHistory(inOutHistory);
			inOutHistory.setId(saveInOutHistory);
			model.addInHistorys(inOutHistory);
			showContentToDevice(device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车进场语音).getContent(), true);
			updatePosition(device.getCarpark(), inOutHistory, true);
		}else if(type.contains("outQRCode")){//出场
			SingleCarparkInOutHistory inOutHistory=null;
			if (device!=null) {
				List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(plate,device.getCarpark());
				inOutHistory = StrUtil.isEmpty(findByNoOut)?new SingleCarparkInOutHistory():findByNoOut.get(0);
			}else{
				for (String ip : new ArrayList<>(model.getMapWaitInOutHistory().keySet())) {
					SingleCarparkInOutHistory singleCarparkInOutHistory = model.getMapWaitInOutHistory().get(ip);
					if (singleCarparkInOutHistory==null) {
						continue;
					}
					if (plate.equals(singleCarparkInOutHistory.getPlateNo())) {
						if (inOutHistory!=null) {
							if (singleCarparkInOutHistory.getOutTime().before(inOutHistory.getOutTime())) {
								model.getMapWaitInOutHistory().remove(ip);
								continue;
							}
							model.getMapWaitInOutHistory().remove(device.getIp());
						}
						inOutHistory=singleCarparkInOutHistory;
						device=mapIpToDevice.get(ip);
					}
				}
			}
			Date outTime=new Date();
			try {
				outTime = sdf.parse(data.getString("outTime"));
			} catch (Exception e) {
				
			}
			try {
				outTime = new Date(data.getJSONObject("outTime").getLongValue("time"));
			} catch (Exception e) {
				
			}
			inOutHistory.setOutTime(outTime);
			if (device!=null) {
				inOutHistory.setCarparkId(device.getCarpark().getId());
				inOutHistory.setCarparkName(device.getCarpark().getName());
				inOutHistory.setOutDevice(device);
				inOutHistory.setOutDeviceIp(device.getIp());
				SingleCarparkInOutHistory history = model.getMapWaitInOutHistory().get(device.getIp());
				if (history!=null) {
					inOutHistory.setOutBigImg(history.getOutBigImg());
					inOutHistory.setOutSmallImg(history.getOutSmallImg());
				}
			}
			inOutHistory.setPlateNo(plate);
			float shouldMoney = Float.valueOf(data.getString("fee"))/100f;
			inOutHistory.setShouldMoney(shouldMoney);
			inOutHistory.setFactMoney(shouldMoney);
			inOutHistory.setFreeMoney(0f);
			inOutHistory.setRemarkString("扫码缴费出场");
			inOutHistory.setChargedType(1);
			sp.getCarparkInOutService().saveInOutHistory(inOutHistory);
			if (plate.length()>8) {
				plate="";
			}
			MessageUtil.close(plate);
			model.getMapWaitInOutHistory().remove(device.getIp());
			showContentToDevice(plate,device, model.getMapVoice().get(DeviceVoiceTypeEnum.临时车出场语音).getContent(), true);
			updatePosition(device.getCarpark(), inOutHistory, false);
			cancelCheckChargeTimer(device);
			if (model.getChargeDevice()!=null&&model.getChargeDevice().getIp().equals(device.getIp())) {
				model.setBtnClick(false);
				model.setComboCarTypeEnable(false);
			}
		}else if(type.contains("searchFee")) {
			String parkingRecordId = data.getString("parkingRecordId");
			SingleCarparkInOutHistory history=null;
			if(parkingRecordId.contains(model.getCarpark().getYunIdentifier())) {
				history = sp.getCarparkInOutService().findInOutById(Long.valueOf(parkingRecordId.replaceAll(model.getCarpark().getYunIdentifier(), "")));
			}else {
				List<SingleCarparkInOutHistory> findByNoOut = sp.getCarparkInOutService().findByNoOut(plate, model.getCarpark());
				if (!StrUtil.isEmpty(findByNoOut)) {
					history=findByNoOut.get(0);
				}
			}
			if(history==null) {
				log.info("停车记录：{}-{}不存在",plate,parkingRecordId);
				return;
			}
			countCharge(model.getCarpark().getId(), "小车", history.getInTime(), new Date(), history);
		}
	}

	private void cancelCheckChargeTimer(SingleCarparkDevice device) {
		Timer timer = mapCheckChargeTimer.get(device.getIp());
		if (timer != null) {
			timer.cancel();
		}
		if (checkIsPayTimer!=null) {
			checkIsPayTimer.cancel();
		}
	}

	/**
	 * @param color 
	 * 
	 */
	public boolean setAllScreenQrCodeColor(int color) {
		log.info("设置所有一体机设备的二维码颜色：{}",color);
		boolean result=true;
		for (SingleCarparkDevice device : mapIpToDevice.values()) {
			if (!device.getScreenType().equals(ScreenTypeEnum.一体机)) {
				continue;
			}
			try {
				carparkScreenService.setQrCodeColor(getDevice(device), color);
			} catch (Exception e) {
				log.error("设置屏幕：{} 颜色：{} 时发生错误：{}",device,color,e);
				result=false;
			}
		}
		return result;
	}

	public float countChargedMoney(SingleCarparkInOutHistory cch) {
		List<CarPayHistory> list = sp.getCarPayService().findCarPayHistoryByLike(0, 100, cch.getPlateNo(), cch.getInTime(), new Date());
		float chargedMoney=0;
		for (CarPayHistory carPayHistory : list) {
			chargedMoney+=carPayHistory.getPayedMoney();
		}
		return chargedMoney;
	}
	public void checkCharge(SingleCarparkDevice device, SingleCarparkInOutHistory data) {
		checkCharge(device, data,false);
	}
	public void checkCharge(SingleCarparkDevice device, SingleCarparkInOutHistory data,boolean checkBtn) {
		if (mapCheckChargeTimer.get(device.getIp()) != null) {
			mapCheckChargeTimer.get(device.getIp()).cancel();
		}
		Timer timer=new Timer();
		mapCheckChargeTimer.put(device.getIp(), timer);
		int delay = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.出场时检测云平台缴费间隔))*1000;
		int timeOut = Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.出场时等待云平台缴费超时时长))*1000;
		if (model.booleanSetting(SystemSettingTypeEnum.优先使用云平台计费)) {
			if (delay<=0||timeOut<=0) {
				return;
			}
		}else{
			if (delay<=0||timeOut<=0) {
				delay=500;
				timeOut=800;
			}
		}
		int totalRuntime=timeOut;
		timer.schedule(new TimerTask() {
			long startTime = System.currentTimeMillis();
			@Override
			public void run() {
				if (System.currentTimeMillis() - startTime > totalRuntime||(checkBtn&&!model.isBtnClick())) {
					timer.cancel();
					return;
				}
				Result result = getPayResult(data);
				log.info("车辆:{}先抓拍在缴费，自动查询车辆缴费信息，结果：{}",data.getPlateNo(), result.getObj());
				if (model.getMapWaitInOutHistory().get(device.getIp())!=null&&!model.getMapWaitInOutHistory().get(device.getIp()).getPlateNo().equals(data.getPlateNo())) {
					timer.cancel();
					return;
				}
				if (result != null) {
					int code = result.getCode();
					if (result.getPayedFee()>=data.getShouldMoney()) {
						data.setFreeMoney(0);
						data.setShouldMoney(result.getPayedFee());
						data.setFactMoney(result.getPayedFee());
						data.setChargeOperaName("在线支付");
						data.setRemarkString("在线缴费完成，在规定时间内出场！");
						data.setChargedType(1);
						if (model.getPlateNo().contains(data.getPlateNo())) {
							model.setPlateNo(data.getPlateNo() + "-已在线支付");
							model.setReal(0);
							model.setChargedMoney(result.getPayedFee());
							model.setShouldMony(result.getPayedFee());
						}
						MessageUtil.close(data.getPlateNo());
						chargeCarPass(device, data, false, result.getPayedFee(), result.getPayedFee(), 0, false);
						
						timer.cancel();
					} else if (code == 3004) {
						timer.cancel();
					} else {
						
					}
				}
			}
		}, delay, delay);
	}
	
	public String getDeviceVersion(SingleCarparkDevice device){
		try {
			Device d = getDevice(device);
			if (d==null) {
				return null;
			}
			String readProductID = hardwareService.readProductID(d);
			return readProductID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void showNowTimeToCamera(SingleCarparkDevice device) {
		try {
			boolean setTime = mapIpToJNA.get(device.getIp()).setTime(device.getIp());
			log.info("设置摄像机：{} 时间：{}",device,setTime);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("设置摄像机：{} 时间发送错误：{}",device,e);
		}
	}
	
	public void startBroadcastService() {
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			WebSocketClient ds=null;
			private long messageId=System.currentTimeMillis();
			@Override
			public void run() {
				if(ds==null) {
					try {
						WebSocketClient client=new WebSocketClient("ws://"+CarparkClientConfig.getInstance().getServerIp()+":16666") {
							@Override
							public void onMessage(String string) {
								try {
									log.info("监听到广播：{}",string);
									messageId=System.currentTimeMillis();
									JSONObject jo = JSON.parseObject(string);
									String type = jo.getString("type");
									if (type==null) {
										return;
									}
									if("test".equals("test")) {
										return;
									}
									log.info("当前等待出场车辆：{}",model.getMapWaitInOutHistory());
									log.info("当前出场:{}-{}",model.getChargeDevice(),model.getChargeHistory());
									if(type.contains("QRCode")) {
										JSONObject jsonObject2 = jo.getJSONObject("data");
										String deviceId=jsonObject2.getString("deviceId");
										
										if(!StrUtil.isEmpty(deviceId)) {
											if(model.getMapWaitInOutHistory().get(deviceId)==null) {
												return;
											}
										}else {
											String carNum=jsonObject2.getString("carNum").trim();
											if(carNum==null) {
												return;
											}
											boolean flag=false;
											
											for (SingleCarparkInOutHistory history : model.getMapWaitInOutHistory().values()) {
												if(carNum.equals(history.getPlateNo())) {
													flag=true;
													break;
												}
											}
											if (!flag) {
												return;
											}
										}
										qrCodeInOutTask(string);
										ds.send("");
									}else if(type.equals("uploadLog")) {
										uploadLogsToServer();
									}else if(type.equals("searchFee")) {
										qrCodeInOutTask(string);
									}
								}catch (Exception e) {
									log.info("监听广播时发生错误",e);
								}
							}
							@Override
							public void onClose(int code, String reason, boolean remote) {
								log.info("连接断开：{}",reason);
								ds=null;
							}
							@Override
							public void onError(Exception ex) {
								log.info("webserver连接错误",ex);
							}
						};
						boolean connectBlocking = client.connectBlocking();
						if (connectBlocking) {
							ds=client;
						}
					} catch (Exception e) {
						log.error("启动广播监听服务失败",e);
//						MessageUtil.info("启动二维码监听服务","启动二维码支付监听服务失败:"+e);
						return;
					}
				}else {
					long currentTimeMillis = System.currentTimeMillis();
					if (currentTimeMillis-messageId>31000) {
						ds.close();
						ds=null;
						messageId=System.currentTimeMillis();
						return;
					}
					if (currentTimeMillis-messageId>=15000) {
						try {
							ds.send("{\"type\":\"test\",\"messageId\":\""+messageId+"\"}");
						} catch (Exception e) {
							log.info("发送测试消息时发生错误");
							ds.close();
							ds=null;
						}finally {
						}
					}
				}
			}
		}, 1000, 1000, TimeUnit.MILLISECONDS, "广播监听服务");
	}

	/**
	 * 
	 */
	public void uploadLogsToServer() {
		File ui = new File("log"+File.separator+"ui"+File.separator);
		String[] list = ui.list((dir, name) -> name.endsWith(".txt"));
		String s="";
		for (SingleCarparkDevice device : mapIpToDevice.values()) {
			s+=device.getName();
		}
		for (String string : list) {
			File file = new File(ui, string);
			try {
				log.info("上传日志：{}到服务器",file);
				long length = file.length();
				File config = new File(ui,string+".config");
				long pos=0;
				if(config.exists()) {
					String line = Files.readFirstLine(config, Charset.forName("GBK"));
					System.out.println(config+"==="+line+"==="+file.length());
					if(!StrUtil.isEmpty(line)) {
						try {
							pos=Long.valueOf(line);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
				}else {
					config.createNewFile();
				}
				if(pos==length) {
					continue;
				}else if(pos>length) {
					pos=0;
				}
				try(RandomAccessFile raf=new RandomAccessFile(file, "r")) {
					
					raf.seek(pos);
					int read = -1;
					byte[] bs = new byte[1024];
					
					String name=StrUtil.getHostIp()+s;
					SystemOperaLogServiceI systemOperaLogService = sp.getSystemOperaLogService();
					String name2 =file.getName();
					while((read = raf.read(bs))!=-1) {
						systemOperaLogService.saveLog(name, name2, pos, read, bs);
						pos+=read;
						Files.write((""+pos).getBytes(), config);
					}
				}
				log.info("上传日志：{} 成功",file);
			} catch (Exception e) {
				e.printStackTrace();
				log.info("上传日志:{}时发生错误:{}",file,e);
			}
		}
	}
	
	public void testInOut(String platetestindex,String deviceIp) {
		ExecutorsUtils.scheduleWithFixedDelay(new Runnable() {
			int totalSize=0;
			int errorSize=0;
			int outFailSize=0;
			@Override
			public void run() {
				totalSize++;
				SingleCarparkInOutHistory history = new SingleCarparkInOutHistory();
				history.setInTime(new DateTime().minusMinutes(2).toDate());
				String plateNo = platetestindex+Strings.padStart(totalSize+"", 6, '0');
				String time = StrUtil.formatDate(history.getInTime(), "yyyyMMddHHmmss");
				log.info("车辆：{} -{}测试出场",plateNo,time);
				log.info("=========================================================================================================");
				history.setPlateNo(plateNo);
				history.setPlateColor("蓝色");
				history.setCarType("临时车");
				history.setUserType("小车");
				history.setCarpark(model.getCarpark());
				try {
    				Long long1 = sp.getCarparkInOutService().saveInOutHistory(history);
    				Thread.sleep(8000);
    				byte[] bs = FileUtils.readFile("D:\\img\\20161122111651128_粤BD021W_big.jpg");
    				carInOutResultProvider.get().invok(deviceIp, 0, plateNo, bs, null, 1);
					Thread.sleep(3000);
					boolean flag=true;
					for (SingleCarparkInOutHistory inOutHistory : model.getMapWaitInOutHistory().values()) {
						if(inOutHistory.getPlateNo().equals(plateNo)) {
							flag=false;
							break;
						}
					}
					if (flag) {
						outFailSize++;
						log.info("{}-{} 出场记录未保存",plateNo,time);
						return;
					}
					log.info("{}-{} 测试回调返回",plateNo,time);
					String string = HttpRequestUtil.get("http://119.23.26.114/DongYunPayAction_createOrder.action?parkingRecordId=8f4cec1316224f17bb4aab5da62bc415"+long1);
					log.info("{}-{} 测试回调返回：{}",plateNo,time,string);
					if(string==null) {
						log.info("测试下单失败,进行下一轮测试");
						return;
					}
					Thread.sleep(6000);
					flag=true;
					for (SingleCarparkInOutHistory inOutHistory : model.getMapWaitInOutHistory().values()) {
						if(inOutHistory.getPlateNo().equals(plateNo)) {
							flag=false;
							break;
						}
					}
					if(!flag) {
						log.info("车辆：{} -{}可能未收到mqtt通知",plateNo,time);
						outFailSize++;
						
					}
				} catch (Exception e) {
					errorSize++;
					log.error("测试出场时发生错误",e);
				}finally {
					log.info("====================================================================================================");
					log.info("车辆：{} ：{}测试出场完成一轮，总次数,：{}出场失败次数,：{}执行异常错误：{}",plateNo,time,totalSize,outFailSize,errorSize);
				}
			}
		}, 10, 5, TimeUnit.SECONDS, "自动测试"+deviceIp);
	}

	public void notifyDeviceCarIn(final SingleCarparkDevice device, final String plate) {
		if (StrUtil.isEmpty(plate)||plate.contains("无牌车")) {
			return;
		}
		if (model.booleanSetting(SystemSettingTypeEnum.启用CJLAPP支付)&&model.booleanSetting(SystemSettingTypeEnum.使用设备二维码)) {
			new Thread(new Runnable() {
				public void run() {
					ipmsService.notifyDeviceCarIn(device.getIdentifire(), plate);
				}
			}).start();
		}
	}
	
}
