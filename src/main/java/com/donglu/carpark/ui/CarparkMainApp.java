package com.donglu.carpark.ui;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.joda.time.DateTime;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.common.AbstractApp;
import com.donglu.carpark.ui.task.CarInTask;
import com.donglu.carpark.ui.task.CarOutTask;
import com.donglu.carpark.ui.view.DevicePresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.xinluwei.XinlutongCallback.XinlutongResult;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.swt.custom.StackLayout;

public class CarparkMainApp extends AbstractApp implements XinlutongResult {

	public static final String MAP_IP_TO_DEVICE = "mapIpToDevice";

	public static final String VILIDTO_DATE = ",有效期至yyyy年MM月dd日";

	public static final String IMAGE_SAVE_SITE = "imageSaveSite";// 图片保存位置

	public static final String TEMP_CAR_AUTO_PASS = "tempCarAutoPass";

	public static final String CAR_IS_ARREARS = "车辆已到期,请联系管理员";

	public static final String CAR_OUT_MSG = "祝您一路平安";

	static final String CAR_IN_MSG = "欢迎光临,请入场停车";

	private static final String NOT_PERMIT_TEMPCAR_IN_MSG = "固定停车场，不容许临时车进入";

	public static final String TEMP_ROAD = "临时车通道";

	public static final String FIX_ROAD = "固定车通道";

	protected static final String CAR_WILL_ARREARS = "车辆即将到期";

	private final AtomicInteger refreshTimes = new AtomicInteger(0);
	private final Integer refreshTimeSpeedSecond = 3;

	private DataBindingContext m_bindingContext;

	private Logger LOGGER = LoggerFactory.getLogger(CarparkMainApp.class);

	protected Shell shell;
	private Text text_total;
	private Text text_hours;
	private Text text_month;
	private Text txt_userName;
	private Text text_worTime;
	private Text text_charge;
	private Text text_free;
	private Text txt_plateNO;
	private Text text_carType;
	private Text text_intime;
	private Text text_outTime;
	private Text text_totalTime;
	private Text text_should;
	private Text text_real;
	private Text txtoutplateNo;
	private Text text_out_time;

	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkMainPresenter presenter;
	@Inject
	private CarparkDatabaseServiceProvider sp;

	private final CarparkMainModel model = new CarparkMainModel();
	private CLabel lbl_outSmallImg;
	private CLabel lbl_outBigImg;

	public static Image inSmallImage;
	public static Image inBigImage;
	public static Image outSmallImage;
	public static Image outBigImage;

	AtomicInteger plateNoTotal = new AtomicInteger(0);

	// 保存设备的进出口信息
	public static final Map<String, String> mapDeviceType = Maps.newHashMap();

	// 保存设备的界面信息
	public static final Map<CTabItem, String> mapDeviceTabItem = Maps.newHashMap();
	// 保存设备的信息
	public static final Map<String, SingleCarparkDevice> mapIpToDevice = Maps.newHashMap();
	// 保存设置信息
	public static final Map<SystemSettingTypeEnum, String> mapSystemSetting = Maps.newHashMap();
	// 保存车牌最近的处理时间
	public static final Map<String, Date> mapPlateNoDate = Maps.newHashMap();

	public static final Map<String, Boolean> mapOpenDoor = Maps.newHashMap();

	// 保存最近的手动拍照时间
	public static final Map<String, Date> mapHandPhotograph = Maps.newHashMap();

	public static final Map<String, CarInTask> mapInTwoCameraTask = Maps.newHashMap();
	public static final Map<String, CarOutTask> mapOutTwoCameraTask = Maps.newHashMap();

	public static final Map<String, Boolean> mapIsTwoChanel = Maps.newHashMap();
	Map<String, List<SingleCarparkDevice>> mapTypeDevices = Maps.newHashMap();

	public static Map<String, String> mapTempCharge;

	private String userType;
	private Label lblNewLabel;

	// 保存出场排队任务信息
	private List<String> listOutTask = new ArrayList<>();
	private Button btnOutCheck;
	private Button btnHandSearch;
	private RateLimiter rateLimiter = RateLimiter.create(1);

	private ExecutorService outTheadPool;

	private ExecutorService inThreadPool;

	private ScheduledExecutorService refreshService;

	private Combo carTypeSelectCombo;
	private Text text_1;
	private ComboViewer comboViewer;
	private Label lbl_charge;
	private Label lbl_free;
	private Label lbl_stop;
	private Table table;
	private TableViewer tableViewer;
	private Text text;

	private Boolean carOutChargeCheck;
	@Inject
	private DevicePresenter inDevicePresenter;
	@Inject
	private DevicePresenter outDevicePresenter;
	@Inject
	private DevicePresenter inDevicePresenter2;
	@Inject
	private DevicePresenter outDevicePresenter2;
	private Text text_2;
	private Text text_3;
	private Button button;

	private CLabel lbl_inSmallImg;

	private CLabel lbl_inBigImg;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				DateTime dt = new DateTime(2015, 11, 1, 0, 00, 00);
				DateTime d = new DateTime(2015, 2, 1, 23, 59, 59);
				System.out.println(dt.minusDays(10).toString("yyyy-MM-dd HH:mm:ss------") + "" + d.toString("yyyyMMddHHmmss"));
			}
		});
	}

	/**
	 * 构造函数
	 */
	public CarparkMainApp() {
		readDevices();
		for (SystemSettingTypeEnum t : SystemSettingTypeEnum.values()) {
			mapSystemSetting.put(t, null);
		}
	}

	/**
	 * 
	 */
	public void readDevices() {
		Object readObject = com.dongluhitec.card.ui.util.FileUtils.readObject(MAP_IP_TO_DEVICE);
		if (readObject != null) {
			Map<String, SingleCarparkDevice> map = (Map<String, SingleCarparkDevice>) readObject;
			for (String key : map.keySet()) {
				SingleCarparkDevice singleCarparkDevice = map.get(key);
				String inType = singleCarparkDevice.getInType();
				if (StrUtil.isEmpty(inType)) {
					continue;
				}
				SingleCarparkCarpark carpark = singleCarparkDevice.getCarpark();
				model.setCarpark(carpark);
				mapDeviceType.put(key, inType);
				mapIpToDevice.put(key, singleCarparkDevice);
				List<SingleCarparkDevice> list = mapTypeDevices.get(inType);
				if (StrUtil.isEmpty(list)) {
					list = new ArrayList<>();
				}
				list.add(singleCarparkDevice);
				mapTypeDevices.put(inType, list);
			}
		}

	}

	/**
	 * Open the window.
	 */
	public void open() {
		try {
			userType = System.getProperty("userType");
			if (StrUtil.isEmpty(userType)) {
				systemExit();
			}
			init();
			Display display = Display.getDefault();
			createContents();
			shell.setMaximized(true);
			shell.setImage(JFaceUtil.getImage("carpark_16"));
			shell.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					System.exit(0);
				}
			});

			shell.open();
			shell.layout();
			antoCheckDevices();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			systemExit();
		}
	}

	/**
	 * 
	 */
	public void systemExit() {
		outTheadPool.shutdownNow();

		inThreadPool.shutdownNow();

		refreshService.shutdownNow();
		System.exit(0);
	}

	/**
	 * 初始化
	 */
	private void init() {
		presenter.setView(this);
		presenter.setModel(model);
		presenter.setIsTwoChanel();
		String userName = System.getProperty("userName");
		model.setUserName(userName);
		model.setWorkTime(new Date());

		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		model.setTotalSlot(carparkInOutService.findTotalSlotIsNow(model.getCarpark()));
		model.setHoursSlot(carparkInOutService.findTempSlotIsNow(model.getCarpark()));
		model.setMonthSlot(carparkInOutService.findFixSlotIsNow(model.getCarpark()));
		model.setTotalCharge(carparkInOutService.findFactMoneyByName(userName));
		model.setTotalFree(carparkInOutService.findFreeMoneyByName(userName));
		List<SingleCarparkSystemSetting> findAllSystemSetting = sp.getCarparkService().findAllSystemSetting();

		for (SingleCarparkSystemSetting ss : findAllSystemSetting) {
			SystemSettingTypeEnum valueOf = null;
			try {
				valueOf = SystemSettingTypeEnum.valueOf(ss.getSettingKey());
			} catch (Exception e) {
				continue;
			}
			mapSystemSetting.put(valueOf, ss.getSettingValue());
		}
		com.dongluhitec.card.ui.util.FileUtils.writeObject(IMAGE_SAVE_SITE, mapSystemSetting.get(SystemSettingTypeEnum.图片保存位置));

		presenter.init();
		mapTempCharge = Maps.newHashMap();
		List<CarparkChargeStandard> listTemp = sp.getCarparkService().findAllCarparkChargeStandard();
		for (CarparkChargeStandard carparkChargeStandard : listTemp) {
			String name = carparkChargeStandard.getCarparkCarType().getName();
			mapTempCharge.put(name, carparkChargeStandard.getCode());
		}

		outTheadPool = Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("进场任务"));
		inThreadPool = Executors.newCachedThreadPool(ThreadUtil.createThreadFactory("出场任务"));
		refreshService = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("每秒刷新停车场全局监控信息"));

		if (StrUtil.isEmpty(System.getProperty("autoSendPositionToDevice"))) {
			autoSendPositionToDevice();
			autoSendTimeToDevice();
		}
		refreshCarparkBasicInfo(refreshTimeSpeedSecond);

		model.setInHistorys(sp.getCarparkInOutService().findCarInHistorys(50));

	}

	/**
	 * 自动更新设备信息
	 */
	private void antoCheckDevices() {
		new Thread(new Runnable() {
			public void run() {
				try {
					WatchService watchService = FileSystems.getDefault().newWatchService();
					Paths.get("temp").register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
					while (true) {
						WatchKey key = watchService.poll();
						if (StrUtil.isEmpty(key)) {
							Thread.sleep(5000);
							continue;
						}
						for (WatchEvent<?> event : key.pollEvents()) {
							System.out.println(event.context().toString().substring(0, MAP_IP_TO_DEVICE.length()) + "发生了" + event.kind() + "事件");
							boolean equals = event.context().toString().substring(0, MAP_IP_TO_DEVICE.length()).equals(MAP_IP_TO_DEVICE);
							if (equals) {
								readDevices();
							}
						}
						if (!key.reset()) {
							System.out.println("temp/mapIpToDevice.temp key.reset()" + key.reset());
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private void autoSendTimeToDevice() {
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				Set<String> keySet = mapIpToDevice.keySet();
				for (String c : keySet) {
					presenter.showNowTimeToDevice(mapIpToDevice.get(c));
				}
			}
		}, 5, 60 * 60, TimeUnit.SECONDS);

	}

	/**
	 * Create contents of the window.
	 * 
	 * @throws IOException
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setMinimumSize(new Point(1024, 768));
		shell.setSize(1036, 889);
		shell.setText("停车场监控-1.0.0.3(" + CarparkClientConfig.getInstance().getDbServerIp() + ")");
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				boolean confirm = commonui.confirm("退出提示", "确定要退出监控界面！！");
				if (!confirm) {
					e.doit = false;
				} else {
					systemExit();
				}
			}
		});
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.verticalSpacing = 2;
		gl_shell.marginWidth = 2;
		gl_shell.marginHeight = 2;
		gl_shell.horizontalSpacing = 2;
		shell.setLayout(gl_shell);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.verticalIndent = 5;
		composite.setLayoutData(gd_composite);

		Composite composite_1 = new Composite(composite, SWT.NONE);
		FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_1.spacing = 5;
		composite_1.setLayout(fl_composite_1);

		Composite composite_9 = new Composite(composite_1, SWT.NONE);
		inDevicePresenter.setPresenter(presenter);
		inDevicePresenter.setType("进口");
		inDevicePresenter.setListDevice(mapTypeDevices.get("进口"));
		inDevicePresenter.go(composite_9);
		composite_9.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_10 = new Composite(composite_1, SWT.NONE);
		outDevicePresenter.setPresenter(presenter);
		outDevicePresenter.setType("出口");
		outDevicePresenter.setListDevice(mapTypeDevices.get("出口"));
		outDevicePresenter.go(composite_10);
		composite_10.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_2 = new Composite(composite, SWT.NONE);
		FillLayout fl_composite_2 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_2.spacing = 5;
		composite_2.setLayout(fl_composite_2);

		Composite composite_5 = new Composite(composite_2, SWT.NONE);
		composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));
		Boolean leftBotttomCamera = Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.左下监控) == null ? SystemSettingTypeEnum.左下监控.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.左下监控));
		if (leftBotttomCamera) {
			inDevicePresenter2.setPresenter(presenter);
			inDevicePresenter2.setType("进口2");
			inDevicePresenter2.setListDevice(mapTypeDevices.get("进口2"));
			inDevicePresenter2.go(composite_5);
		} else {
			Composite composite_20 = new Composite(composite_5, SWT.NONE);
			GridLayout gl_composite_20 = new GridLayout(1, false);
			gl_composite_20.marginWidth = 0;
			gl_composite_20.marginHeight = 0;
			gl_composite_20.horizontalSpacing = 0;
			composite_20.setLayout(gl_composite_20);

			Composite composite_21 = new Composite(composite_20, SWT.NONE);
			GridData gd_composite_21 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			gd_composite_21.widthHint = 419;
			composite_21.setLayoutData(gd_composite_21);
			composite_21.setLayout(new GridLayout(3, false));

			Composite composite_22 = new Composite(composite_21, SWT.NONE);
			GridData gd_composite_22 = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
			gd_composite_22.widthHint = 140;
			composite_22.setLayoutData(gd_composite_22);
			composite_22.setLayout(new GridLayout(2, false));

			Label label_7 = new Label(composite_22, SWT.NONE);
			label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			label_7.setText("车牌号码");
			label_7.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			label_7.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
			label_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));

			text_2 = new Text(composite_22, SWT.BORDER);
			text_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
			text_2.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
			text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			Label label_14 = new Label(composite_22, SWT.NONE);
			label_14.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			label_14.setText("出场时间");
			label_14.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));

			text_3 = new Text(composite_22, SWT.BORDER);
			text_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
			text_3.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
			text_3.setEditable(false);
			text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			Composite composite_23 = new Composite(composite_21, SWT.NONE);
			composite_23.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
			composite_23.setLayout(new GridLayout(1, false));

			button = new Button(composite_23, SWT.NONE);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String inShowPlateNO = model.getInShowPlateNO();
					if (StrUtil.isEmpty(inShowPlateNO)) {
						return;
					}
					if (inShowPlateNO.length() < 3 || inShowPlateNO.length() > 7) {
						return;
					}
					model.setInCheckClick(false);
				}
			});
			GridData gd_button = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_button.exclude = false;
			button.setLayoutData(gd_button);
			button.setText("入场确认");
			button.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
			button.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));

			Composite composite_24 = new Composite(composite_21, SWT.BORDER);
			GridData gd_composite_24 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
			gd_composite_24.widthHint = 120;
			composite_24.setLayoutData(gd_composite_24);
			composite_24.setLayout(new FillLayout(SWT.HORIZONTAL));

			lbl_inSmallImg = new CLabel(composite_24, SWT.NONE);
			lbl_inSmallImg.setText("出场车牌");
			lbl_inSmallImg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
			lbl_inSmallImg.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.BOLD));
			lbl_inSmallImg.setAlignment(SWT.CENTER);

			Composite composite_25 = new Composite(composite_20, SWT.BORDER);
			composite_25.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			composite_25.setLayout(new FillLayout(SWT.HORIZONTAL));

			lbl_inBigImg = new CLabel(composite_25, SWT.NONE);
			lbl_inBigImg.setText("出场车牌");
			lbl_inBigImg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
			lbl_inBigImg.setFont(SWTResourceManager.getFont("微软雅黑", 23, SWT.BOLD));
			lbl_inBigImg.setAlignment(SWT.CENTER);
		}
		
		Composite composite_21 = new Composite(composite_2, SWT.NONE);
		composite_21.setLayout(new FillLayout(SWT.HORIZONTAL));
		Boolean rightBotttomCamera = Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.右下监控) == null ? SystemSettingTypeEnum.右下监控.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.右下监控));
		if (rightBotttomCamera) {
			outDevicePresenter2.setPresenter(presenter);
			outDevicePresenter2.setType("出口2");
			outDevicePresenter2.setListDevice(mapTypeDevices.get("出口2"));
			outDevicePresenter2.go(composite_21);
		} else {
			Composite composite_20 = new Composite(composite_21, SWT.NONE);
			composite_20.setLayout(new GridLayout(1, false));

			Composite composite_3 = new Composite(composite_20, SWT.NONE);
			composite_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));

			Composite composite_6 = new Composite(composite_3, SWT.NONE);
			composite_6.setLayout(new GridLayout(3, false));

			Composite composite_12 = new Composite(composite_6, SWT.NONE);
			composite_12.setLayout(new GridLayout(2, false));
			GridData gd_composite_12 = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
			gd_composite_12.widthHint = 389;
			composite_12.setLayoutData(gd_composite_12);

			Label label_15 = new Label(composite_12, SWT.NONE);
			label_15.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
			label_15.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
			label_15.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			label_15.setText("车牌号码");
			label_15.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));

			txtoutplateNo = new Text(composite_12, SWT.BORDER);
			txtoutplateNo.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
			txtoutplateNo.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
			txtoutplateNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			Label label_16 = new Label(composite_12, SWT.NONE);
			label_16.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			label_16.setText("出场时间");
			label_16.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));

			text_out_time = new Text(composite_12, SWT.BORDER);
			text_out_time.setEditable(false);
			text_out_time.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
			text_out_time.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
			text_out_time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			Composite composite_15 = new Composite(composite_6, SWT.NONE);
			composite_15.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
			composite_15.setLayout(new GridLayout(1, false));

			btnOutCheck = new Button(composite_15, SWT.NONE);
			GridData gd_btnOutCheck = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_btnOutCheck.exclude = false;
			btnOutCheck.setLayoutData(gd_btnOutCheck);
			btnOutCheck.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
			btnOutCheck.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String outShowPlateNO = model.getOutShowPlateNO();
					if (!outShowPlateNO.matches(CarparkUtils.PLATENO_REGEX)) {
						commonui.info("车牌错误", "请输入正确的车牌");
						return;
					}
					model.setOutCheckClick(false);
				}
			});
			btnOutCheck.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
			btnOutCheck.setText("出场确认");

			btnHandSearch = new Button(composite_15, SWT.NONE);
			btnHandSearch.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
			btnHandSearch.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (txtoutplateNo.getText().isEmpty()) {
						commonui.info("提示", "请先输入车牌");
						return;
					}
					model.setBtnClick(false);
					model.setDisContinue(true);
					String data = model.getSearchPlateNo();
					String bigImg = model.getSearchBigImage();
					String smallImg = model.getSearchSmallImage();
					presenter.showManualSearch(data, bigImg, smallImg);
				}
			});
			btnHandSearch.setText("人工查找");
			btnHandSearch.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
			Composite composite_11 = new Composite(composite_6, SWT.BORDER);
			composite_11.setLayout(new FillLayout(SWT.HORIZONTAL));
			GridData gd_composite_11 = new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1);
			gd_composite_11.widthHint = 120;
			composite_11.setLayoutData(gd_composite_11);

			lbl_outSmallImg = new CLabel(composite_11, SWT.NONE);
			lbl_outSmallImg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
			lbl_outSmallImg.setAlignment(SWT.CENTER);
			lbl_outSmallImg.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.BOLD));
			lbl_outSmallImg.setText("出场车牌");

			Composite composite_4 = new Composite(composite_20, SWT.NONE);
			composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			FillLayout fl_composite_4 = new FillLayout(SWT.HORIZONTAL);
			fl_composite_4.spacing = 6;
			composite_4.setLayout(fl_composite_4);

			Composite composite_8 = new Composite(composite_4, SWT.BORDER);
			composite_8.setLayout(new FillLayout(SWT.HORIZONTAL));

			lbl_outBigImg = new CLabel(composite_8, SWT.NONE);
			lbl_outBigImg.setText("出场车牌");
			lbl_outBigImg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
			lbl_outBigImg.setFont(SWTResourceManager.getFont("微软雅黑", 23, SWT.BOLD));
			lbl_outBigImg.setAlignment(SWT.CENTER);

			if (Boolean
					.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认) == null ? SystemSettingTypeEnum.临时车入场是否确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认))
					|| Boolean.valueOf(
							mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认) == null ? SystemSettingTypeEnum.固定车入场是否确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认))
					|| !Boolean.valueOf(
							mapSystemSetting.get(SystemSettingTypeEnum.是否允许无牌车进) == null ? SystemSettingTypeEnum.是否允许无牌车进.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.是否允许无牌车进))) {
			} else {
			}
			if (!Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认) == null ? SystemSettingTypeEnum.固定车出场确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认))) {
				gd_btnOutCheck.exclude = true;
			}
		}
		Group group = new Group(shell, SWT.SHADOW_IN);
		group.setFont(SWTResourceManager.getFont("微软雅黑", 5, SWT.NORMAL));
		GridLayout gl_group = new GridLayout(2, false);
		group.setLayout(gl_group);
		GridData gd_group = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1);
		gd_group.widthHint = 284;
		group.setLayoutData(gd_group);

		TabFolder tabFolder = new TabFolder(group, SWT.NONE);
		GridData gd_tabFolder = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_tabFolder.heightHint = 244;
		gd_tabFolder.widthHint = 272;
		tabFolder.setLayoutData(gd_tabFolder);

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("基本信息");

		Composite composite_13 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_13);
		composite_13.setLayout(new GridLayout(2, false));

		lblNewLabel = new Label(composite_13, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		lblNewLabel.setText("剩余车位");

		text_total = new Text(composite_13, SWT.BORDER | SWT.READ_ONLY);
		text_total.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_total.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_total.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		text_total.setEditable(false);
		text_total.setText("1000");
		text_total.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));

		Label label = new Label(composite_13, SWT.NONE);
		label.setText("临时车位");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_hours = new Text(composite_13, SWT.BORDER | SWT.READ_ONLY);
		text_hours.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text_hours.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_hours.setText("1000");
		text_hours.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_hours.setEditable(false);

		Label label_1 = new Label(composite_13, SWT.NONE);
		label_1.setText("月租车位");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_month = new Text(composite_13, SWT.BORDER | SWT.READ_ONLY);
		text_month.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text_month.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_month.setText("1000");
		text_month.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_month.setEditable(false);

		Label label_2 = new Label(composite_13, SWT.NONE);
		label_2.setText("当前值班");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		txt_userName = new Text(composite_13, SWT.BORDER | SWT.READ_ONLY);
		txt_userName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txt_userName.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		txt_userName.setText("panmingzhi");
		txt_userName.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		txt_userName.setEditable(false);

		Label label_3 = new Label(composite_13, SWT.NONE);
		label_3.setText("上班时间");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_worTime = new Text(composite_13, SWT.BORDER | SWT.READ_ONLY);
		text_worTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text_worTime.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_worTime.setText("2015-8-15 12:30:20");
		text_worTime.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_worTime.setEditable(false);

		Label lblNewLabel_2 = new Label(composite_13, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		lblNewLabel_2.setText("当前时间");

		text_1 = new Text(composite_13, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_1.setEditable(false);

		Label label_4 = new Label(composite_13, SWT.NONE);
		label_4.setText("实收金额");
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_charge = new Text(composite_13, SWT.BORDER | SWT.READ_ONLY);
		text_charge.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text_charge.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		text_charge.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_charge.setText("1000");
		text_charge.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_charge.setEditable(false);

		Label label_5 = new Label(composite_13, SWT.NONE);
		label_5.setText("免费金额");
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_free = new Text(composite_13, SWT.BORDER | SWT.READ_ONLY);
		text_free.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text_free.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_free.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		text_free.setText("1000");
		text_free.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_free.setEditable(false);

		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("进场记录");
		
		Composite composite_18 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_18);
		composite_18.setLayout(new GridLayout(1, false));

		Composite composite_7 = new Composite(composite_18, SWT.NONE);
		composite_7.setLayout(new GridLayout(2, false));
		composite_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		text = new Text(composite_7, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 128;
		text.setLayoutData(gd_text);

		Button btnNewButton = new Button(composite_7, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.carInByHand();
			}
		});
		btnNewButton.setText("手动入场");
		btnNewButton.setEnabled(Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.进场允许手动入场)));
		tableViewer = new TableViewer(composite_18, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				presenter.showHistory(model.getInHistorySelect());
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(113);
		tableColumn.setText("车牌");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
		tableColumn_1.setWidth(143);
		tableColumn_1.setText("进场时间");

		Label lblNewLabel_1 = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lblNewLabel_1.setText("New Label");

		Label label_6 = new Label(group, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("车牌号码");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
//		if (!Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.左下监控))) {
//			tabItem_1.dispose();
//		}
		txt_plateNO = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		txt_plateNO.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		txt_plateNO.setText("京A23456");
		txt_plateNO.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		txt_plateNO.setEditable(false);
		txt_plateNO.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label_8 = new Label(group, SWT.NONE);
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("车辆性质");
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_carType = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_carType.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_carType.setText("临时车");
		text_carType.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_carType.setEditable(false);
		text_carType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label_9 = new Label(group, SWT.NONE);
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("入场时间");
		label_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_intime = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_intime.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_intime.setText("2015-8-15 12:30:20");
		text_intime.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_intime.setEditable(false);
		text_intime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label_10 = new Label(group, SWT.NONE);
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("出场时间");
		label_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_outTime = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_outTime.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_outTime.setText("2015-8-15 14:50:20");
		text_outTime.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_outTime.setEditable(false);
		GridData gd_text_outTime = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_outTime.widthHint = 160;
		text_outTime.setLayoutData(gd_text_outTime);

		Label label_11 = new Label(group, SWT.NONE);
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_11.setText("停车时间");
		label_11.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_totalTime = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_totalTime.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_totalTime.setText("2:20:00");
		text_totalTime.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_totalTime.setEditable(false);
		text_totalTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label_12 = new Label(group, SWT.NONE);
		label_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_12.setText("应收金额");
		label_12.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));

		text_should = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_should.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_should.setText("20.0");
		text_should.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_should.setEditable(false);
		text_should.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label_13 = new Label(group, SWT.NONE);
		label_13.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_13.setText("实收金额");
		label_13.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		carOutChargeCheck = Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.出场确认放行) == null ? SystemSettingTypeEnum.出场确认放行.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.出场确认放行));
		text_real = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_real.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				keyReleasedByControl(carOutChargeCheck, e);
			}
		});
		text_real.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_real.setText("20.0");
		text_real.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_real.setEditable(true);
		text_real.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite composite_14 = new Composite(group, SWT.NONE);
		GridLayout gl_composite_14 = new GridLayout(2, false);
		gl_composite_14.horizontalSpacing = 6;
		gl_composite_14.verticalSpacing = 0;
		gl_composite_14.marginWidth = 0;
		gl_composite_14.marginHeight = 0;
		composite_14.setLayout(gl_composite_14);
		GridData gd_composite_14 = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		if (mapTempCharge.keySet().size() <= 1) {
			gd_composite_14.exclude = true;
		}
		composite_14.setLayoutData(gd_composite_14);

		Label lbl_carType = new Label(composite_14, SWT.RIGHT);
		lbl_carType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_carType.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		lbl_carType.setText("车辆类型");

		comboViewer = new ComboViewer(composite_14, SWT.READ_ONLY);
		carTypeSelectCombo = comboViewer.getCombo();
		carTypeSelectCombo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				keyReleasedByControl(carOutChargeCheck, e);
			}
		});
		carTypeSelectCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		carTypeSelectCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String carparkCarType2 = model.getCarparkCarType();
				if (carparkCarType2.equals("请选择车型")) {
					return;
				}
				if (model.isBtnClick()) {
					SingleCarparkInOutHistory h = model.getChargeHistory();
					SingleCarparkDevice device = model.getChargeDevice();
					if (StrUtil.isEmpty(h)) {
						return;
					}
					Date inTime = h.getInTime();
					Date outTime = h.getOutTime();
					CarTypeEnum carparkCarType = getCarparkCarType(carparkCarType2);
					model.setCartypeEnum(carparkCarType);
					float countShouldMoney = presenter.countShouldMoney(device.getCarpark().getId(), carparkCarType, inTime, outTime);
					LOGGER.info("等待收费：车辆{}，停车场{}，车辆类型{}，进场时间{}，出场时间{}，停车：{}，应收费：{}元", h.getPlateNo(), device.getCarpark(), model.getCarTypeEnum(), model.getInTime(), model.getOutTime(),
							model.getTotalTime(), countShouldMoney);
					presenter.showContentToDevice(mapIpToDevice.get(model.getIp()), CarparkUtils.getCarStillTime(model.getTotalTime()) + CarparkUtils.formatFloatString("请缴费" + countShouldMoney + "元"),
							false);
					model.setShouldMony(countShouldMoney);
					model.setReal(countShouldMoney);
				} else {
					if (StrUtil.isEmpty(carparkCarType2)) {
						return;
					}
					model.setBtnClick(true);
				}
			}
		});
		carTypeSelectCombo.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		List<String> listCarType = new ArrayList<>();
		listCarType.add("请选择车型");
		if (!StrUtil.isEmpty(mapTempCharge.get("大车"))) {
			listCarType.add("大车");
		}
		if (!StrUtil.isEmpty(mapTempCharge.get("小车"))) {
			listCarType.add("小车");
		}
		if (!StrUtil.isEmpty(mapTempCharge.get("摩托车"))) {
			listCarType.add("摩托车");
		}
		comboViewer.setInput(listCarType);
		carTypeSelectCombo.select(0);

		Composite composite_19 = new Composite(group, SWT.NONE);
		composite_19.setLayout(new GridLayout(1, false));
		composite_19.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 2, 1));

		Composite composite_16 = new Composite(composite_19, SWT.NONE);
		composite_16.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_16.setLayout(new GridLayout(2, false));

		lbl_charge = new Label(composite_16, SWT.NONE);
		lbl_charge.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseExit(MouseEvent e) {
				System.out.println("mouse Exit");
			}

			@Override
			public void mouseHover(MouseEvent e) {
				System.out.println("mouse hover");
			}
		});
		lbl_charge.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
		lbl_charge.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				setBoundsY(lbl_charge, -2);
				presenter.charge(carOutChargeCheck);
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setBoundsY(lbl_charge, 2);
			}
		});
		lbl_charge.setImage(CarparkUtils.getSwtImage("charge.png"));
		GridData gd_lbl_charge = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_charge.widthHint = 100;
		gd_lbl_charge.heightHint = 80;
		lbl_charge.setLayoutData(gd_lbl_charge);

		lbl_free = new Label(composite_16, SWT.NONE);
		lbl_free.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				presenter.free(carOutChargeCheck);
				setBoundsY(lbl_free, -2);
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setBoundsY(lbl_free, 2);
			}
		});
		lbl_free.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
		lbl_free.setImage(CarparkUtils.getSwtImage("free.png"));
		GridData gd_lbl_free = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_free.widthHint = 100;
		gd_lbl_free.heightHint = 80;
		lbl_free.setLayoutData(gd_lbl_free);

		lbl_stop = new Label(composite_19, SWT.NONE);
		lbl_stop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				setBoundsY(lbl_stop, -2);
				stop();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setBoundsY(lbl_stop, 2);
			}
		});
		lbl_stop.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
		lbl_stop.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lbl_stop.setImage(CarparkUtils.getSwtImage("stop.png"));

		Composite composite_17 = new Composite(composite_19, SWT.NONE);
		composite_17.setLayout(new GridLayout(2, false));

		Label lbl_change = new Label(composite_17, SWT.NONE);
		lbl_change.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				setBoundsY(lbl_change, -2);
				presenter.changeUser();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setBoundsY(lbl_change, 2);
			}
		});
		lbl_change.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
		GridData gd_label_18 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_18.widthHint = 100;
		gd_label_18.heightHint = 80;
		lbl_change.setLayoutData(gd_label_18);
		lbl_change.setImage(CarparkUtils.getSwtImage("change.png"));

		Label lbl_return = new Label(composite_17, SWT.NONE);
		lbl_return.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				setBoundsY(lbl_return, -2);
				presenter.returnAccount();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setBoundsY(lbl_return, 2);
			}
		});
		lbl_return.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
		GridData gd_label_20 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_20.widthHint = 100;
		gd_label_20.heightHint = 80;
		lbl_return.setLayoutData(gd_label_20);
		lbl_return.setImage(CarparkUtils.getSwtImage("return.png"));

		Label lbl_search = new Label(composite_19, SWT.NONE);
		lbl_search.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				setBoundsY(lbl_search, -2);
				presenter.showSearchInOutHistory();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				setBoundsY(lbl_search, 2);
			}
		});
		lbl_search.setCursor(new Cursor(shell.getDisplay(), SWT.CURSOR_HAND));
		lbl_search.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lbl_search.setImage(CarparkUtils.getSwtImage("search.png"));
		controlToolItem();
		addKeyLisenter(shell);
		m_bindingContext = initDataBindings();
	}

	public void controlToolItem() {
		if (System.getProperty("userType").equals(SystemUserTypeEnum.操作员.name())) {
			inDevicePresenter.controlItem(true);
			outDevicePresenter.controlItem(true);
			inDevicePresenter2.controlItem(true);
			outDevicePresenter2.controlItem(true);
		}

	}

	void addKeyLisenter(Control control) {
		control.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// System.out.println("sdads============"+e.keyCode);
				keyReleasedByControl(carOutChargeCheck, e);
			}

		});
		Control[] children = null;
		if (control instanceof Shell) {
			children = ((Shell) control).getChildren();
		}
		if (control instanceof Composite) {
			children = ((Composite) control).getChildren();
		}
		if (control instanceof Group) {
			children = ((Group) control).getChildren();
		}
		if (!StrUtil.isEmpty(children)) {
			for (Control c : children) {
				addKeyLisenter(c);
			}
		}
	}

	/**
	 * @param i
	 * @param lbl_charge
	 * 
	 */
	private void setBoundsY(Label lbl_charge, int i) {
		Rectangle bounds = lbl_charge.getBounds();
		bounds.y = bounds.y + i;
		lbl_charge.setBounds(bounds);
	}

	/**
	 * 手动抓拍
	 * 
	 * @param ip
	 */
	protected void handPhotograph(String ip) {
		LOGGER.info("对设备{}进行手动拍照", ip);
		presenter.handPhotograph(ip);
		mapHandPhotograph.put(ip, new Date());
	}

	/**
	 * 没隔5秒自动发送车位
	 */
	private void autoSendPositionToDevice() {
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("没隔5秒发送车位数"));
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				presenter.sendPositionToAllDevice(true);
			}
		}, 5000, 5000, TimeUnit.MILLISECONDS);
	}

	/**
	 * 车牌识别监控
	 */
	public void invok(final String ip, int channel, final String plateNO, final byte[] bigImage, final byte[] smallImage, float rightSize) {
		LOGGER.info("车辆{}在设备{}通道{}处进场,可信度：{}", plateNO, ip, channel, rightSize);
		try {
			Preconditions.checkNotNull(mapDeviceType.get(ip), "not monitor device:" + ip);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		boolean equals = (mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔) == null ? SystemSettingTypeEnum.双摄像头识别间隔.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔))
				.equals(SystemSettingTypeEnum.双摄像头识别间隔.getDefaultValue());
		String linkAddress = mapIpToDevice.get(ip).getLinkAddress();

		boolean isNotOpenDoor = mapOpenDoor.get(ip)==null||!mapOpenDoor.get(ip);
		if (mapDeviceType.get(ip).equals("出口")||mapDeviceType.get(ip).equals("出口2")) {
			// 是否是双摄像头
			if (isNotOpenDoor&&!equals && mapIsTwoChanel.get(linkAddress)) {
				CarOutTask carOutTask = mapOutTwoCameraTask.get(linkAddress);
				if (!StrUtil.isEmpty(carOutTask)) {
					if (carOutTask.getRightSize() < rightSize) {
						carOutTask.setBigImage(bigImage);
						carOutTask.setPlateNO(plateNO);
						carOutTask.setSmallImage(smallImage);
						carOutTask.setRightSize(rightSize);
					}
					carOutTask.alreadyFinshWait();
					return;
				}
			}
			if (listOutTask.size() > 5) {
				LOGGER.info("已经有5个任务正在等待处理暂不添加任务{}", listOutTask);
				return;
			}
			String key = new Date() + "current has device:" + ip + " with plate:" + plateNO + " process";
			listOutTask.add(key);
			CarOutTask task = new CarOutTask(ip, plateNO, bigImage, smallImage, model, sp, presenter, lbl_outBigImg, lbl_outSmallImg, carTypeSelectCombo, text_real, shell, rightSize);
			outTheadPool.submit(task);
			if (isNotOpenDoor) {
				mapOutTwoCameraTask.put(linkAddress, task);
			}
			outTheadPool.submit(() -> {
				while (model.isBtnClick()) {
					int i = 0;
					try {
						if (i > 120) {
							model.setBtnClick(false);
						}
						TimeUnit.MILLISECONDS.sleep(500);
						i++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				listOutTask.remove(key);
				plateNoTotal.addAndGet(1);
			});
		} else if (mapDeviceType.get(ip).equals("进口") || mapDeviceType.get(ip).equals("进口2")) {
			if (isNotOpenDoor&&!equals && mapIsTwoChanel.get(linkAddress)) {
				CarInTask carInTask = mapInTwoCameraTask.get(linkAddress);
				if (!StrUtil.isEmpty(carInTask)) {
					if (carInTask.getRightSize() < rightSize) {
						carInTask.setBigImage(bigImage);
						carInTask.setPlateNO(plateNO);
						carInTask.setSmallImage(smallImage);
						carInTask.setIp(ip);
						carInTask.setRightSize(rightSize);
					}
					carInTask.alreadyFinshWait();
					return;
				}
			}
			CarInTask task = new CarInTask(ip, plateNO, bigImage, smallImage, model, sp, presenter, shell, rightSize, lbl_inSmallImg, lbl_inBigImg);
			inThreadPool.submit(task);
			if (isNotOpenDoor) {
				mapInTwoCameraTask.put(linkAddress, task);
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

	@Override
	public boolean isOpen() {
		return false;
	}

	public void refreshCarparkBasicInfo(Integer refreshTimeSpeedSecond) {
		refreshService.scheduleAtFixedRate(() -> {
			try {
				model.setCurrentTime(StrUtil.formatDateTime(new Date()));
				if (refreshTimes.addAndGet(1) % refreshTimeSpeedSecond != 0) {
					return;
				}
				String userName = System.getProperty("userName");
				model.setTotalCharge(sp.getCarparkInOutService().findFactMoneyByName(userName));
				model.setTotalFree(sp.getCarparkInOutService().findFreeMoneyByName(userName));
				model.setTotalSlot(sp.getCarparkInOutService().findTotalSlotIsNow(model.getCarpark()));
				model.setHoursSlot(sp.getCarparkInOutService().findTempSlotIsNow(model.getCarpark()));
				model.setMonthSlot(sp.getCarparkInOutService().findFixSlotIsNow(model.getCarpark()));
			} catch (Exception e) {
				LOGGER.error("刷新停车场出错", e);
			}
		} , 3000, 1000, TimeUnit.MILLISECONDS);
	}

	/**
	 * 
	 */
	public void stop() {
		boolean confirm = commonui.confirm("提示", "确认终止收费？");
		if (!confirm) {
			return;
		}
		model.setDisContinue(true);
		model.setBtnClick(false);
		model.setComboCarTypeEnable(false);
		model.setHandSearch(false);
		model.setOutPlateNOEditable(false);
		model.setChargeDevice(null);
		model.setChargeHistory(null);
		mapOutTwoCameraTask.clear();
		mapInTwoCameraTask.clear();
		// listOutTask.clear();
		// outTheadPool.shutdownNow();
		// btnCharge.setData(BTN_CHARGE, null);
		// btnCharge.setData(BTN_CHARGE_DEVICE, null);
	}

	@Override
	public void invok(String ip, int channel, String plateNO, byte[] bigImage, byte[] smallImage, float rightSize, String plateColor) {
		model.setOutPlateNOColor(plateColor);
		invok(ip, channel, plateNO, bigImage, smallImage, rightSize);
	}

	/**
	 * @param carOutChargeCheck
	 * @param e
	 */
	private void keyReleasedByControl(Boolean carOutChargeCheck, KeyEvent e) {
		if (!rateLimiter.tryAcquire()) {
			return;
		}
		if (e.keyCode == StrUtil.SMAIL_KEY_ENTER || e.keyCode == 13) {
			text_real.setFocus();
			text_real.selectAll();
		}
		// //拍照
		// if (e.keyCode == 16777227) {
		// SingleCarparkDevice singleCarparkDevice = mapIpToDevice.get(mapDeviceTabItem.get(model.getSelectTabSelect()));
		// if (StrUtil.isEmpty(singleCarparkDevice)) {
		// return;
		// }
		// else{
		// handPhotograph(singleCarparkDevice.getIp());
		// }
		// }
		// //抬杆
		// if (e.keyCode == 16777229) {
		// SingleCarparkDevice singleCarparkDevice = mapIpToDevice.get(mapDeviceTabItem.get(model.getSelectTabSelect()));
		// if (StrUtil.isEmpty(singleCarparkDevice)) {
		// return;
		// }
		// else{
		// String ip = singleCarparkDevice.getIp();
		// CarparkMainApp.mapOpenDoor.put(ip, true);
		// handPhotograph(ip);
		// }
		// }

		// 收费放行
		if (e.keyCode == 16777236) {
			presenter.charge(carOutChargeCheck);
		}
		// 免费放行
		if (e.keyCode == 16777237) {
			presenter.free(carOutChargeCheck);
		}
		if (e.keyCode == 16777232) {
			presenter.changeUser();
		}
		if (e.keyCode == 16777233) {
			presenter.returnAccount();
		}
		if (e.keyCode == 16777234) {
			presenter.showSearchInOutHistory();
		}
	}

	@Override
	public Shell getShell() {

		return shell;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextText_totalObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_total);
		IObservableValue totalSlotModelObserveValue = BeanProperties.value("totalSlot").observe(model);
		bindingContext.bindValue(observeTextText_totalObserveWidget, totalSlotModelObserveValue, null, null);
		//
		IObservableValue observeTextText_hoursObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_hours);
		IObservableValue hoursSlotModelObserveValue = BeanProperties.value("hoursSlot").observe(model);
		bindingContext.bindValue(observeTextText_hoursObserveWidget, hoursSlotModelObserveValue, null, null);
		//
		IObservableValue observeTextText_monthObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_month);
		IObservableValue monthSlotModelObserveValue = BeanProperties.value("monthSlot").observe(model);
		bindingContext.bindValue(observeTextText_monthObserveWidget, monthSlotModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_userNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_userName);
		IObservableValue userNameModelObserveValue = BeanProperties.value("userName").observe(model);
		bindingContext.bindValue(observeTextTxt_userNameObserveWidget, userNameModelObserveValue, null, null);
		//
		IObservableValue observeTextText_worTimeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_worTime);
		IObservableValue workTimeModelObserveValue = BeanProperties.value("workTime").observe(model);
		bindingContext.bindValue(observeTextText_worTimeObserveWidget, workTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_chargeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_charge);
		IObservableValue totalChargeModelObserveValue = BeanProperties.value("totalCharge").observe(model);
		bindingContext.bindValue(observeTextText_chargeObserveWidget, totalChargeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_freeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_free);
		IObservableValue totalFreeModelObserveValue = BeanProperties.value("totalFree").observe(model);
		bindingContext.bindValue(observeTextText_freeObserveWidget, totalFreeModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_plateNOObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_plateNO);
		IObservableValue plateNoModelObserveValue = BeanProperties.value("plateNo").observe(model);
		bindingContext.bindValue(observeTextTxt_plateNOObserveWidget, plateNoModelObserveValue, null, null);
		//
		IObservableValue observeTextText_carTypeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_carType);
		IObservableValue carTypeModelObserveValue = BeanProperties.value("carType").observe(model);
		bindingContext.bindValue(observeTextText_carTypeObserveWidget, carTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_intimeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_intime);
		IObservableValue inTimeModelObserveValue = BeanProperties.value("inTime").observe(model);
		bindingContext.bindValue(observeTextText_intimeObserveWidget, inTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_outTimeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_outTime);
		IObservableValue outTimeModelObserveValue = BeanProperties.value("outTime").observe(model);
		bindingContext.bindValue(observeTextText_outTimeObserveWidget, outTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_totalTimeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_totalTime);
		IObservableValue totalTimeModelObserveValue = BeanProperties.value("totalTime").observe(model);
		bindingContext.bindValue(observeTextText_totalTimeObserveWidget, totalTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_shouldObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_should);
		IObservableValue shouldMonyModelObserveValue = BeanProperties.value("shouldMony").observe(model);
		bindingContext.bindValue(observeTextText_shouldObserveWidget, shouldMonyModelObserveValue, null, null);
		//
		IObservableValue observeTextText_realObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_real);
		IObservableValue realModelObserveValue = BeanProperties.value("real").observe(model);
		bindingContext.bindValue(observeTextText_realObserveWidget, realModelObserveValue, null, null);
		if (!StrUtil.isEmpty(txtoutplateNo)) {
			//
			IObservableValue observeTextTxtoutplateNoObserveWidget = WidgetProperties.text(SWT.Modify).observe(txtoutplateNo);
			IObservableValue outShowPlateNOModelObserveValue = BeanProperties.value("outShowPlateNO").observe(model);
			bindingContext.bindValue(observeTextTxtoutplateNoObserveWidget, outShowPlateNOModelObserveValue, null, null);
			//
			IObservableValue observeEnabledButton_2ObserveWidget = WidgetProperties.enabled().observe(btnHandSearch);
			IObservableValue handSearchModelObserveValue = BeanProperties.value("handSearch").observe(model);
			bindingContext.bindValue(observeEnabledButton_2ObserveWidget, handSearchModelObserveValue, null, null);
			//
			IObservableValue observeTextText_out_timeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_out_time);
			IObservableValue outShowTimeModelObserveValue = BeanProperties.value("outShowTime").observe(model);
			bindingContext.bindValue(observeTextText_out_timeObserveWidget, outShowTimeModelObserveValue, null, null);
			//
			IObservableValue observeEditableTxtoutplateNoObserveWidget = WidgetProperties.editable().observe(txtoutplateNo);
			IObservableValue outPlateNOEditableModelObserveValue = BeanProperties.value("outPlateNOEditable").observe(model);
			bindingContext.bindValue(observeEditableTxtoutplateNoObserveWidget, outPlateNOEditableModelObserveValue, null, null);
		}
		//
		IObservableValue observeEnabledComboObserveWidget = WidgetProperties.enabled().observe(carTypeSelectCombo);
		IObservableValue comboCarTypeEnableModelObserveValue = BeanProperties.value("comboCarTypeEnable").observe(model);
		bindingContext.bindValue(observeEnabledComboObserveWidget, comboCarTypeEnableModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue currentTimeModelObserveValue = BeanProperties.value("currentTime").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, currentTimeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue carparkCarTypeModelObserveValue = BeanProperties.value("carparkCarType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, carparkCarTypeModelObserveValue, null, null);
		//
		IObservableValue observeEditableText_realObserveWidget = WidgetProperties.editable().observe(text_real);
		IObservableValue btnClickModelObserveValue = BeanProperties.value("btnClick").observe(model);
		bindingContext.bindValue(observeEditableText_realObserveWidget, btnClickModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap[] observeMaps = BeansObservables.observeMaps(listContentProvider.getKnownElements(), SingleCarparkInOutHistory.class, new String[] { "plateNo", "inTimeLabel" });
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		tableViewer.setContentProvider(listContentProvider);
		//
		IObservableList inHistorysModelObserveList = BeanProperties.list("inHistorys").observe(model);
		tableViewer.setInput(inHistorysModelObserveList);
		//
		IObservableValue observeSingleSelectionTableViewer = ViewerProperties.singleSelection().observe(tableViewer);
		IObservableValue inHistorySelectModelObserveValue = BeanProperties.value("inHistorySelect").observe(model);
		bindingContext.bindValue(observeSingleSelectionTableViewer, inHistorySelectModelObserveValue, null, null);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue handPlateNOModelObserveValue = BeanProperties.value("handPlateNO").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, handPlateNOModelObserveValue, null, null);
		if (!StrUtil.isEmpty(text_2)) {
			//
			IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
			IObservableValue inShowPlateNOModelObserveValue = BeanProperties.value("inShowPlateNO").observe(model);
			bindingContext.bindValue(observeTextText_2ObserveWidget, inShowPlateNOModelObserveValue, null, null);
			//
			IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
			IObservableValue inShowTimeModelObserveValue = BeanProperties.value("inShowTime").observe(model);
			bindingContext.bindValue(observeTextText_3ObserveWidget, inShowTimeModelObserveValue, null, null);
			//
			IObservableValue observeEnabledButtonObserveWidget = WidgetProperties.enabled().observe(button);
			IObservableValue inCheckClickModelObserveValue = BeanProperties.value("inCheckClick").observe(model);
			bindingContext.bindValue(observeEnabledButtonObserveWidget, inCheckClickModelObserveValue, null, null);
			//
			IObservableValue observeEditableText_2ObserveWidget = WidgetProperties.editable().observe(text_2);
			bindingContext.bindValue(observeEditableText_2ObserveWidget, inCheckClickModelObserveValue, null, null);
		}
		//
		return bindingContext;
	}
}
