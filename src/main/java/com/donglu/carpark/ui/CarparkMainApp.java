package com.donglu.carpark.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Cursor;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractApp;
import com.donglu.carpark.ui.task.CarInTask;
import com.donglu.carpark.ui.task.CarOutTask;
import com.donglu.carpark.ui.view.DevicePresenter;
import com.donglu.carpark.ui.view.InInfoPresenter;
import com.donglu.carpark.ui.view.OutInfoPresenter;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.MyMapCache;
import com.donglu.carpark.util.CarparkFileUtils;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.util.ThreadUtil;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import com.google.inject.Provider;

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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;

public class CarparkMainApp extends AbstractApp{
	private final AtomicInteger refreshTimes = new AtomicInteger(0);
	private final Integer refreshTimeSpeedSecond = 3;

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

	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkMainPresenter presenter;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CarparkMainModel model;


	// 保存设置信息
	public Map<SystemSettingTypeEnum, String> mapSystemSetting = Maps.newHashMap();
	// 保存车牌最近的处理时间
	public Map<String, Date> mapPlateNoDate = new MyMapCache<>(600*1000, 5);

	public Map<String, Boolean> mapOpenDoor = Maps.newHashMap();

	// 保存最近的手动拍照时间
	public Map<String, Date> mapHandPhotograph = Maps.newHashMap();

	public Map<String, CarInTask> mapInTwoCameraTask = Maps.newHashMap();
	public Map<String, CarOutTask> mapOutTwoCameraTask = Maps.newHashMap();

	public Map<String, Boolean> mapIsTwoChanel = Maps.newHashMap();
	public Map<String, List<SingleCarparkDevice>> mapTypeDevices = Maps.newHashMap();
	//保存临时收费车辆类型
	public Map<String, String> mapTempCharge=Maps.newHashMap();;
	// 保存双摄像头处理任务
	public Map<String, Timer> mapTwoChanelTimer = new HashMap<>();

	private String userType;
	private Label lblNewLabel;

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
	private Provider<DevicePresenter> devicePresenterProvider;
	private DevicePresenter inDevicePresenter;
	private DevicePresenter outDevicePresenter;
	private DevicePresenter inDevicePresenter2;
	private DevicePresenter outDevicePresenter2;

	@Inject
	private Provider<InInfoPresenter> inInfoPresenterProvider;
	@Inject
	private Provider<OutInfoPresenter> outInfoPresenterProvider;



	private int sendPositionToDeviceTime = 5;
	private Text txt_chargedMoney;
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			@Override
			public void run() {
				DateTime dt = new DateTime(2015, 11, 1, 0, 00, 00);
				DateTime d = new DateTime(2015, 2, 1, 23, 59, 59);
				System.out.println(dt.minusDays(10).toString("yyyy-MM-dd HH:mm:ss------") + "" + d.toString("yyyyMMddHHmmss"));
			}
		});
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void readDevices() {
		Object readObject = CarparkFileUtils.readObject(ConstUtil.MAP_IP_TO_DEVICE);
		if (readObject != null) {
			Map<String, SingleCarparkDevice> map = (Map<String, SingleCarparkDevice>) readObject;
			Map<Long, SingleCarparkCarpark> mapCarparkWithId=new HashMap<>();
			for (String key : map.keySet()) {
				SingleCarparkDevice singleCarparkDevice = map.get(key);
				String inType = singleCarparkDevice.getInType();
				if (StrUtil.isEmpty(inType)) {
					continue;
				}
				SingleCarparkCarpark carpark=mapCarparkWithId.get(singleCarparkDevice.getCarpark().getId());
				if (carpark==null) {
					try {
						carpark = sp.getCarparkService().findCarparkById(singleCarparkDevice.getCarpark().getId());
					} catch (Exception e) {
						LOGGER.error("没有找到停车场：{}",singleCarparkDevice.getCarpark());
						continue;
					}
				}
				singleCarparkDevice.setCarpark(carpark);
				model.setCarpark(carpark);
				model.getMapDeviceType().put(key, inType);
				model.getMapIpToDevice().put(key, singleCarparkDevice);
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
	@Override
	public void open() {
		try {
			userType = System.getProperty("userType");
			if (StrUtil.isEmpty(userType)) {
				systemExit();
			}
			Display display = Display.getDefault();
			init();
			createContents();
			shell.setImage(JFaceUtil.getImage("carpark_16"));

			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("系统发生异常", e);
			// shell.dispose();
			// open();
		} finally {
			LOGGER.error("系统退出");
			systemExit();
		}
	}
	
	public void openAsyncExec(){
		try {
			userType = System.getProperty("userType");
			if (StrUtil.isEmpty(userType)) {
				systemExit();
			}
			Display display = Display.getDefault();
			shell = new Shell();
			shell.setMinimumSize(new Point(1024, 768));
			shell.setSize(1036, 889);
			shell.setEnabled(false);
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					init();
					createContents();
					shell.layout();
					shell.setEnabled(true);
				}
			});
			shell.setMaximized(true);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("系统发生异常", e);
			// shell.dispose();
			// open();
		} finally {
			LOGGER.error("系统退出");
			systemExit();
		}
	}

	/**
	 * 
	 */
	public void systemExit() {
		try {
			outTheadPool.shutdownNow();

			inThreadPool.shutdownNow();

			refreshService.shutdownNow();
			presenter.systemExit();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
	}

	/**
	 * 初始化
	 */
	private void init() {
		mapSystemSetting = model.getMapSystemSetting();
		mapPlateNoDate = model.getMapPlateNoDate();
		mapOpenDoor = model.getMapOpenDoor();
		mapHandPhotograph = model.getMapHandPhotograph();
		mapInTwoCameraTask = model.getMapInTwoCameraTask();
		mapOutTwoCameraTask = model.getMapOutTwoCameraTask();
		mapIsTwoChanel = model.getMapIsTwoChanel();
		mapTypeDevices = model.getMapTypeDevices();
		mapTempCharge = model.getMapTempCharge();
		mapTwoChanelTimer = model.getMapTwoChanelTimer();
		presenter.setView(this);
		readDevices();
		presenter.init();
		initVioce();

		outTheadPool = Executors.newSingleThreadExecutor(ThreadUtil.createThreadFactory("出场任务"));
		inThreadPool = Executors.newCachedThreadPool(ThreadUtil.createThreadFactory("进场任务"));
		if (StrUtil.isEmpty(System.getProperty(ConstUtil.AUTO_SEND_POSITION_TO_DEVICE))) {
			autoSendPositionToDevice();
		}
		autoSendTimeToDevice();
		refreshCarparkBasicInfo(refreshTimeSpeedSecond);

		model.setInHistorys(sp.getCarparkInOutService().findCarInHistorys(50));

		try {
			sendPositionToDeviceTime = Integer.parseInt(System.getProperty("SendPositionToDeviceTime"));
		} catch (NumberFormatException e) {

		} finally {
			LOGGER.info("发送车位数间隔SendPositionToDeviceTime为:{}", sendPositionToDeviceTime);
		}
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
		
		List<SingleCarparkDeviceVoice> findAllVoiceInfo = sp.getCarparkService().findAllVoiceInfo();
		for (SingleCarparkDeviceVoice dv : findAllVoiceInfo) {
			model.getMapVoice().put(dv.getType(), dv);
		}
	}
	/**
	 * 自动下发时间
	 */
	private void autoSendTimeToDevice() {
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("发送时间任务"));
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				Set<String> keySet = model.getMapIpToDevice().keySet();
				for (String c : keySet) {
					presenter.showNowTimeToDevice(model.getMapIpToDevice().get(c));
				}
			}
		}, 1, 60 * 60, TimeUnit.SECONDS);

	}

	/**
	 * Create contents of the window.
	 * 
	 * @throws IOException
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		//if判断后无法进行拖拽编辑
		if (shell==null) {
			shell = new Shell();
			shell.setMinimumSize(new Point(1024, 768));
			shell.setSize(1036, 889);
			shell.setMaximized(true);
		}
		shell.setImage(JFaceUtil.getImage("carpark_16"));
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				systemExit();
			}
		});
		shell.setText("停车场监控-" + SystemSettingTypeEnum.软件版本.getDefaultValue() + "(" + CarparkClientConfig.getInstance().getDbServerIp() + ")");
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				boolean confirm = commonui.confirm("退出提示", "确定要退出监控界面！！");
				if (!confirm) {
					e.doit = false;
				}
			}
		});
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.verticalSpacing = 2;
		gl_shell.marginWidth = 2;
		gl_shell.marginHeight = 2;
		gl_shell.horizontalSpacing = 2;
		shell.setLayout(gl_shell);

		SashForm sashForm = new SashForm(shell, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		SashForm sashForm_1 = new SashForm(sashForm, SWT.NONE);

		Composite composite_9 = new Composite(sashForm_1, SWT.NONE);
		inDevicePresenter = devicePresenterProvider.get();
		inDevicePresenter.setPresenter(presenter);
		inDevicePresenter.setType("进口");
		inDevicePresenter.setListDevice(mapTypeDevices.get("进口"));
		inDevicePresenter.go(composite_9);
		composite_9.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_10 = new Composite(sashForm_1, SWT.NONE);
		outDevicePresenter = devicePresenterProvider.get();
		outDevicePresenter.setPresenter(presenter);
		outDevicePresenter.setType("出口");
		outDevicePresenter.setListDevice(mapTypeDevices.get("出口"));
		outDevicePresenter.go(composite_10);
		composite_10.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm_1.setWeights(new int[] { 1, 1 });

		SashForm sashForm_2 = new SashForm(sashForm, SWT.NONE);

		Composite composite_5 = new Composite(sashForm_2, SWT.NONE);
		composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite composite_21_1 = new Composite(sashForm_2, SWT.NONE);
		composite_21_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm_2.setWeights(new int[] { 1, 1 });
		sashForm.setWeights(new int[] { 1, 1 });

		Boolean leftBotttomCamera = Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.左下监控) == null ? SystemSettingTypeEnum.左下监控.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.左下监控));
		if (leftBotttomCamera) {
			inDevicePresenter2 = devicePresenterProvider.get();
			System.out.println("inDevicePresenter2==" + inDevicePresenter2);
			inDevicePresenter2.setPresenter(presenter);
			inDevicePresenter2.setType("进口2");
			inDevicePresenter2.setListDevice(mapTypeDevices.get("进口2"));
			inDevicePresenter2.go(composite_5);
		} else {
			InInfoPresenter inInfoPresenter = inInfoPresenterProvider.get();
			inInfoPresenter.setModel(model);
			inInfoPresenter.go(composite_5);
		}
		Boolean rightBotttomCamera = Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.右下监控) == null ? SystemSettingTypeEnum.右下监控.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.右下监控));
		if (rightBotttomCamera) {
			outDevicePresenter2 = devicePresenterProvider.get();
			System.out.println("outDevicePresenter2==" + outDevicePresenter2);
			outDevicePresenter2.setPresenter(presenter);
			outDevicePresenter2.setType("出口2");
			outDevicePresenter2.setListDevice(mapTypeDevices.get("出口2"));
			outDevicePresenter2.go(composite_21_1);
		} else {
			OutInfoPresenter outInfoPresenter = outInfoPresenterProvider.get();
			outInfoPresenter.setModel(model);
			outInfoPresenter.setPresenter(presenter);
			outInfoPresenter.setCommonui(commonui);
			outInfoPresenter.go(composite_21_1);
		}

		Boolean isCarHandIn = Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.进场允许手动入场));
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
		model.setCarparkCarType(listCarType.size() > 1 ? listCarType.get(0) : "小车");
		controlToolItem();

		ScrolledComposite scrolledComposite = new ScrolledComposite(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		Group group = new Group(scrolledComposite, SWT.SHADOW_IN);
		group.setFont(SWTResourceManager.getFont("微软雅黑", 5, SWT.NORMAL));
		GridLayout gl_group = new GridLayout(2, false);
		gl_group.marginHeight = 0;
		gl_group.marginWidth = 0;
		group.setLayout(gl_group);

		TabFolder tabFolder = new TabFolder(group, SWT.NONE);
		GridData gd_tabFolder = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
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
		text_total.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				presenter.editPosition();
			}
		});

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
		composite_7.setLayout(new GridLayout(3, false));
		composite_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		text = new Text(composite_7, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 128;
		text.setLayoutData(gd_text);
		text.setEditable(isCarHandIn);
		Button btnNewButton = new Button(composite_7, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.carInByHand();
			}
		});
		btnNewButton.setText("手动入场");
		btnNewButton.setEnabled(isCarHandIn);

		Button button_1 = new Button(composite_7, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.refreshCarWithIn();
			}
		});
		button_1.setText("刷新");
		tableViewer = new TableViewer(composite_18, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				SingleCarparkInOutHistory inHistorySelect = model.getInHistorySelect();
				presenter.showHistory(inHistorySelect);
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

		// if (!Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.左下监控))) {
		// tabItem_1.dispose();
		// }
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

		Composite composite = new Composite(group, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
		gd_composite.exclude = false;
		composite.setLayoutData(gd_composite);

		Label lblNewLabel_3 = new Label(composite, SWT.NONE);
		lblNewLabel_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("已收金额");

		txt_chargedMoney = new Text(composite, SWT.BORDER);
		txt_chargedMoney.setEditable(false);
		txt_chargedMoney.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		txt_chargedMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		txt_chargedMoney.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label_13 = new Label(group, SWT.NONE);
		label_13.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_13.setText("实收金额");
		label_13.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		text_real = new Text(group, SWT.BORDER | SWT.READ_ONLY);
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
		gd_composite_14.exclude = false;
		composite_14.setLayoutData(gd_composite_14);

		Label lbl_carType = new Label(composite_14, SWT.RIGHT);
		lbl_carType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lbl_carType.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		lbl_carType.setText("车辆类型");

		comboViewer = new ComboViewer(composite_14, SWT.READ_ONLY);
		carTypeSelectCombo = comboViewer.getCombo();
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
					presenter.showContentToDevice(model.getMapIpToDevice().get(model.getIp()), CarparkUtils.getCarStillTime(model.getTotalTime()) + CarparkUtils.formatFloatString("请缴费" + countShouldMoney + "元"),
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
		comboViewer.setInput(listCarType);
		if (!Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用集中收费))) {
			gd_composite.exclude = true;
		}
		carOutChargeCheck = Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.出场确认放行) == null ? SystemSettingTypeEnum.出场确认放行.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.出场确认放行));
		if (mapTempCharge.keySet().size() <= 1) {
			gd_composite_14.exclude = true;
		}

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
			}

			@Override
			public void mouseHover(MouseEvent e) {
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
			@Override
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
				if (!model.isBtnClick()) {
					return;
				}
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
		scrolledComposite.setContent(group);
		scrolledComposite.setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		initDataBindings();
		addKeyLisenter(shell);
	}

	public void controlToolItem() {
		int level = SystemUserTypeEnum.getLevel(System.getProperty("userType"));
		if (level<3) {
			inDevicePresenter.controlItem(true);
			outDevicePresenter.controlItem(true);
			if (inDevicePresenter2!=null) {
				inDevicePresenter2.controlItem(true);
			}
			if (outDevicePresenter2!=null) {
				outDevicePresenter2.controlItem(true);
			}
		}

	}

	void addKeyLisenter(Control control) {
		control.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
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
				try {
					if(model.getPlateInTime().after(new Date())){
						LOGGER.info("车辆进出场时间为{}，暂时不发送车位",model.getPlateInTime());
						return;
					}
					presenter.sendPositionToAllDevice(true);
				} catch (Exception e) {
					LOGGER.info("发送车位时发生错误",e);
				}
			}
		}, sendPositionToDeviceTime, sendPositionToDeviceTime, TimeUnit.SECONDS);
	}
	/**
	 * 获取车辆类型
	 * 
	 * @param carparkCarType
	 * @return
	 */
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
	/**
	 * 刷新停车场全局信息
	 * @param refreshTimeSpeedSecond
	 */
	public void refreshCarparkBasicInfo(Integer refreshTimeSpeedSecond) {
		refreshService = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("每秒刷新停车场全局监控信息"));
		refreshService.scheduleAtFixedRate(() -> {
			try {
				model.setCurrentTime(StrUtil.formatDateTime(new Date()));
				if (refreshTimes.addAndGet(1) % refreshTimeSpeedSecond != 0) {
					return;
				}
				String userName = System.getProperty("userName");
				model.setTotalCharge(sp.getCarparkInOutService().findFactMoneyByName(userName));
				model.setTotalFree(sp.getCarparkInOutService().findFreeMoneyByName(userName));
				model.setTotalSlot(presenter.getSlotOfLeft());
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


	/**
	 * @param carOutChargeCheck
	 * @param e
	 */
	private void keyReleasedByControl(Boolean carOutChargeCheck, KeyEvent e) {
		if (!rateLimiter.tryAcquire()) {
			return;
		}
		if (e.keyCode == StrUtil.SMAIL_KEY_ENTER || e.keyCode == 13) {
			if (e.getSource()!=null) {
				Control c = (Control) e.getSource();
				if (c.getData(ConstUtil.NO_CHANGE_FOCUS)!=null) {
					return;
				}
			}
			text_real.setFocus();
			text_real.selectAll();
		}
		// 进口落杆
		if (e.keyCode == 16777226) {
			inDevicePresenter.closeDoor();
		}
		// 进口抬杆
		if (e.keyCode == 16777227) {
			inDevicePresenter.openDoor();
		}
		// 出口落杆
		if (e.keyCode == 16777228) {
			outDevicePresenter.closeDoor();
		}
		// 出口抬杆
		if (e.keyCode == 16777229) {
			outDevicePresenter.openDoor();
		}

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
		
		IObservableValue observeTooltipTextText_totalObserveWidget = WidgetProperties.tooltipText().observe(text_total);
		IObservableValue totalLeftSlotModelObserveValue = BeanProperties.value("totalSlotTooltip").observe(model);
		bindingContext.bindValue(observeTooltipTextText_totalObserveWidget, totalLeftSlotModelObserveValue, null, null);
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
		model.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (!e.getPropertyName().equals("real")) {
					return;
				}
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						text_real.setFocus();
						text_real.selectAll();
					}
				});
			}
		});
		model.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (!e.getPropertyName().equals("carparkCarType")) {
					return;
				}
				CarparkUtils.setFocus(carTypeSelectCombo);
			}
		});
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
		//
		IObservableValue observeTextText_chargedMoneybserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_chargedMoney);
		IObservableValue chargedMoneyModelObserveValue = BeanProperties.value("chargedMoney").observe(model);
		bindingContext.bindValue(observeTextText_chargedMoneybserveWidget, chargedMoneyModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
