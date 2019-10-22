package com.donglu.carpark.server.imgserver;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.server.CarparkDBServer;
import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.server.ServerUI;
import com.donglu.carpark.server.module.CarparkServerGuiceModule;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.service.WebService;
import com.donglu.carpark.service.background.IpmsSynchroServiceI;
import com.donglu.carpark.service.background.LvdiSynchroServiceI;
import com.donglu.carpark.service.background.SmsSendServiceI;
import com.donglu.carpark.service.background.haiyu.AsynHaiYuRecordService;
import com.donglu.carpark.ui.Login;
import com.donglu.carpark.ui.wizard.sn.ImportSNModel;
import com.donglu.carpark.ui.wizard.sn.ImportSNWizard;
import com.donglu.carpark.util.CarparkDataBaseUtil;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.MyAppender;
import com.donglu.carpark.util.SystemUpdate;
import com.donglu.carpark.yun.CarparkYunConfig;
import com.dongluhitec.card.blservice.ShangHaiYunCarParkService;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.shanghaiyunpingtai.ShanghaiYunCarparkCfg;
import com.dongluhitec.card.shanghaiyunpingtai.YunCarparkStartService;
import com.dongluhitec.card.util.ThreadUtil;
import com.dongluhitec.core.crypto.appauth.AppAuthorization;
import com.dongluhitec.core.crypto.appauth.AppVerifier;
import com.dongluhitec.core.crypto.appauth.AppVerifierImpl;
import com.dongluhitec.core.crypto.softdog.SoftDogWin;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Link;

public class ImageServerUI {

	private static Logger LOGGER = LoggerFactory.getLogger(ImageServerUI.class);

	protected Shell shell;
	private Text text;

	private String filePath = "";
	private Server server;
	@Inject
	private ServerUI ui;
	@Inject
	private YunConfigUI yunUI;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private WebService webService;
	@Inject
	private Provider<CarparkDBServer> carparkDBServerProvider;

	private AppVerifier av;
	private Button btnStart;
	private boolean autoStartServerCfg = false;

	public static Injector serverInjector;
	// 系统托盘图片
	private TrayIcon icon;
	// 判断是否允许创建系统托盘
	private boolean isSystemTraySupported = true;
	// 判断界面是否打开
	private boolean isOpen = false;
	private String btnStartType = "start";
	private String btnStartText = "启    动";
	private Text text_1;
	private Text txt_log;
	
	public ImageServerUI() {
		MyAppender.list.add(new MyAppender.LogCallback() {
			@Override
			public void log(String s) {
				if (txt_log==null) {
					return;
				}
				txt_log.getDisplay().asyncExec(new Runnable() {
					public void run() {
						try {
							if(!isOpen) {
								return;
							}
							if (txt_log.getLineCount() > 10000) {
								txt_log.setText("");
							}
							txt_log.append(s);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

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
				try {
					long nanoTime = System.nanoTime();
					serverInjector = Guice.createInjector(new CarparkServerGuiceModule());
					LOGGER.info("依赖注入用时：{}", (System.nanoTime() - nanoTime));
					ImageServerUI window = serverInjector.getInstance(ImageServerUI.class);
					LOGGER.info("获取界面用时：{}", (System.nanoTime() - nanoTime));
					window.open();
				} catch (Exception e) {
					LOGGER.error("启动时发生错误", e);
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		try {
			File file = new File(CarparkServerConfig.configFileName);
			System.out.println(file.exists());
			long nanoTime = System.nanoTime();
			createTrayIcon();
			shell = new Shell(SWT.MAX | SWT.MIN | SWT.CLOSE | SWT.ON_TOP | SWT.RESIZE);
			Object readObject = CarparkFileUtils.readObject("autoStartServer");
			if (readObject != null) {
				autoStartServerCfg = (boolean) readObject;
			}
			Display display = Display.getDefault();
			createContents();
			
			LOGGER.info("界面加载用时：{}", System.nanoTime() - nanoTime);
			if (autoStartServerCfg) {
				autoStartServer();
			}else {
				shell.open();
				isOpen = true;
				shell.layout();
			}
			btnStart.setData("type", btnStartType);
			btnStart.setText(btnStartText);
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			LOGGER.error("服务器发送错误,系统退出", e);
		}
		System.exit(0);
	}

	private void autoStartServer() {
		LOGGER.info("自动启动服务器服务");
		try {
			icon.displayMessage("服务器启动", "正在启动停车场服务器\n请稍后。。。。。。", MessageType.INFO);
			startServer();
			icon.displayMessage("服务器启动", "停车场服务器启动成功！", MessageType.INFO);
		} catch (Exception e) {
			if (e.getMessage().indexOf("服务器") > -1) {
				icon.displayMessage("服务器启动", "停车场服务器启动失败！", MessageType.ERROR);
			}
		}
	}

	protected void importSN() {
		try {
			sp.start();
			CarparkService carparkService = sp.getCarparkService();
			Map<SNSettingType, SingleCarparkSystemSetting> mapSN = carparkService.findAllSN();

			ImportSNModel m = new ImportSNModel();
			if (!StrUtil.isEmpty(mapSN)) {
				SingleCarparkSystemSetting sn = mapSN.get(SNSettingType.sn);
				m.setSn(StrUtil.isEmpty(sn) ? "" : sn.getSettingValue());
			}
			av = new AppVerifierImpl(new SoftDogWin());
			ImportSNWizard importSNWizard = new ImportSNWizard(av, sp, m);
			commonui.showWizard(importSNWizard);
		} catch (Exception e) {
			LOGGER.error("导入注册码时发生错误", e);
		}
	}

	private void init() {
		try {
			sp.start();
			SingleCarparkSystemSetting s = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.图片保存位置.name());
			filePath = StrUtil.isEmpty(s) ? System.getProperty("user.dir") : s.getSettingValue();
			CarparkFileUtils.writeObject(ConstUtil.IMAGE_SAVE_DIRECTORY, filePath);
		} catch (Exception e) {
			LOGGER.error("初始化时发生错误", e);
		} finally {
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell.setSize(578, 364);
		shell.setText("服务器");
		shell.setLayout(new GridLayout(1, false));
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION | SWT.APPLICATION_MODAL);

				box.setText("退出提示");
				box.setMessage("确认退出服务器？退出服务器后客户端将不能在使用！");
				int open = box.open();
				if (open == SWT.YES) {
				} else {
					e.doit = false;
				}
			}

			@Override
			public void shellIconified(ShellEvent e) {
				if (isSystemTraySupported) {
					shell.setVisible(false);
					isOpen=false;
				}
			}

		});
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				System.exit(0);
			}
		});
		Object readObject = CarparkFileUtils.readObject(ConstUtil.IMAGE_SAVE_DIRECTORY);

		shell.setImage(JFaceUtil.getImage("carpark_16"));

		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);

		MenuItem mntmNewSubmenu = new MenuItem(menu, SWT.CASCADE);
		mntmNewSubmenu.setText("配置");

		Menu menu_1 = new Menu(mntmNewSubmenu);
		mntmNewSubmenu.setMenu(menu_1);

		MenuItem menuItem = new MenuItem(menu_1, SWT.NONE);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ui.open();
					shell.setEnabled(false);
					init();
					shell.setEnabled(true);
					text.setText(filePath);
					text.setFocus();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		menuItem.setText("服务器配置");

		MenuItem menuItem_1 = new MenuItem(menu_1, SWT.NONE);
		menuItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					importSN();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		menuItem_1.setText("注册码");

		MenuItem menuItem_2 = new MenuItem(menu_1, SWT.NONE);
		menuItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					yunUI.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		menuItem_2.setText("云上传配置");
		MenuItem menuItem_3 = new MenuItem(menu_1, SWT.NONE);
		menuItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					new StoreServerUI().open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		menuItem_3.setText("商铺配置");

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(5, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setText("图片保存路径");

		text = new Text(composite, SWT.BORDER);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 200;
		text.setLayoutData(gd_text);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text.setEditable(false);
		text.setText(readObject == null ? System.getProperty("user.dir") : (String) readObject);
		Button button = new Button(composite, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(shell, SWT.SINGLE);
				String open = directoryDialog.open();
				if (StrUtil.isEmpty(open)) {
					return;
				}
				CarparkFileUtils.writeObject(ConstUtil.IMAGE_SAVE_DIRECTORY, open);
				text.setText(open);
			}
		});
		button.setText("...");

		btnStart = new Button(composite, SWT.NONE);
		btnStart.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnStart.setData("type", btnStartType);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String data = (String) btnStart.getData("type");
				if (data.equals("start")) {
					try {
						btnStart.setText("启动中");
						btnStart.setEnabled(false);
						startServer();
						String open = text.getText();
						CarparkFileUtils.writeObject(ConstUtil.IMAGE_SAVE_DIRECTORY, open);
						SingleCarparkSystemSetting s = new SingleCarparkSystemSetting();
						s.setSettingKey(SystemSettingTypeEnum.图片保存位置.name());
						s.setSettingValue(open);
						sp.getCarparkService().saveSystemSetting(s);
						btnStart.setEnabled(true);
						btnStart.setText(btnStartText);
						btnStart.setData("type", btnStartType);
					} catch (Exception e1) {
						if (e1.getMessage().indexOf("服务器") > -1) {
							commonui.error("启动失败", "" + e1);
							btnStart.setText("启    动");
							btnStart.setEnabled(true);
						}
					}
				}
				if (data.equals("stop")) {
					MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION | SWT.APPLICATION_MODAL);

					box.setText("退出提示");
					box.setMessage("确认退出服务器？退出服务器后客户端将不能在使用！");
					int open = box.open();
					if (open != SWT.YES) {
						return;
					}
					System.exit(0);
				}
			}
		});
		btnStart.setText(btnStartText);

		Button button_1 = new Button(composite, SWT.CHECK);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = button_1.getSelection();
				CarparkFileUtils.writeObject("autoStartServer", selection);
			}
		});
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.setText("自动启动");
		button_1.setSelection(autoStartServerCfg);

		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		txt_log = new Text(composite_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		txt_log.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayout(new GridLayout(3, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_1 = new Label(composite_2, SWT.NONE);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_1.widthHint = 50;
		label_1.setLayoutData(gd_label_1);

		Label lblip = new Label(composite_2, SWT.NONE);
		lblip.setText("本机ip:");

		text_1 = new Text(composite_2, SWT.NONE);
		text_1.setEditable(false);
		text_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_1.widthHint = 110;
		text_1.setLayoutData(gd_text_1);
		text_1.setText(StrUtil.getHostIp());
	}

	/**
	 * 
	 */
	private void createTrayIcon() {
		isSystemTraySupported = SystemTray.isSupported();
		if (!isSystemTraySupported) {
			return;
		}
		SystemTray systemTray = SystemTray.getSystemTray();

		URL resource = getClass().getResource("/carpark_16.png");
		Image image = Toolkit.getDefaultToolkit().getImage(resource);
		icon = new TrayIcon(image);
		icon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					openView();
				}
			}
		});
		icon.setToolTip("停车场服务器");
		PopupMenu popup = new PopupMenu();
		java.awt.MenuItem close = new java.awt.MenuItem("关闭");
		close.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		java.awt.MenuItem open = new java.awt.MenuItem("打开");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openView();
			}
		});
		popup.add(open);
		popup.add(close);
		icon.setPopupMenu(popup);
		try {
			systemTray.add(icon);
		} catch (Exception e) {
			LOGGER.error("添加托盘图标时发生错误", e);
		}
	}

	/**
	 * 启动服务
	 * 
	 * @throws Exception
	 */
	protected void startServer() throws Exception {
		try {
			sp.start();
			SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.DateBase_version.name());
			if (StrUtil.isEmpty(findSystemSettingByKey)) {
				findSystemSettingByKey = new SingleCarparkSystemSetting();
				findSystemSettingByKey.setSettingKey(SystemSettingTypeEnum.DateBase_version.name());
				findSystemSettingByKey.setSettingValue(SystemSettingTypeEnum.DateBase_version.getDefaultValue());
				sp.getCarparkService().saveSystemSetting(findSystemSettingByKey);
			}
			updateSql(findSystemSettingByKey.getSettingValue());
			if (!findSystemSettingByKey.getSettingValue().equals(SystemSettingTypeEnum.软件版本.getDefaultValue())) {
				SystemUpdate update = new SystemUpdate();
				update.systemUpdate(findSystemSettingByKey.getSettingValue(), SystemSettingTypeEnum.软件版本.getDefaultValue());
				updateDatabase(findSystemSettingByKey);
				findSystemSettingByKey.setSettingValue(SystemSettingTypeEnum.DateBase_version.getDefaultValue());
				sp.getCarparkService().saveSystemSetting(findSystemSettingByKey);
			}
			if (Boolean.valueOf(System.getProperty(Login.CHECK_SOFT_DOG) == null ? "true" : "false")) {
				autoCheckSoftDog();
			}
			SingleCarparkSystemSetting findSystemSettingByKey2 = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启动HTTP对外服务.name());
			if (findSystemSettingByKey2 != null) {
				System.setProperty("startHttpService", findSystemSettingByKey2.getSettingValue());
			}
			int port = 8899;
			String property = System.getProperty("ServerPort");
			if (property != null) {
				port = Integer.valueOf(property);
			}

			this.server = new Server(port);
			ServletHandler servletHandler = new ServletHandler();
			CarparkDBServer carparkDBServer = carparkDBServerProvider.get();
			carparkDBServer.startDbServlet(servletHandler);
			server.setHandler(servletHandler);
			server.start();
			btnStartText = "退出";
			btnStartType = "stop";
			carparkDBServer.startBackgroudService();
		} catch (Exception e) {
			LOGGER.error("启动失败", e);
			throw new Exception("服务器启动失败" + e);
		}
		autoDeleteSameInOutHistory();
		autoSendInfoToCloud();
		startBackGroudService();
	}

	private void updateSql(String databaseVersion) {
		CarparkServerConfig instance = CarparkServerConfig.getInstance();
		List<String> list = new ArrayList<>();
		if (checkVercionIsLess(databaseVersion, "1.0.0.23")) {
			String sql = "alter table SingleCarparkInOutHistory alter column plateNo varchar(30)";
			list.add(sql);
		}
		if (checkVercionIsLess(databaseVersion, "1.0.0.24")) {
			// list.add("update SingleCarparkInOutHistory set carRecordStatus='在场内' where outTime is
			// null and carRecordStatus is null");
			// list.add("update SingleCarparkInOutHistory set carRecordStatus='已出场' where outTime is
			// not null and carRecordStatus is null");
		}
		if (checkVercionIsLess(databaseVersion, "1.0.0.30")) {
			String sql = "update SingleCarparkInOutHistory set onlineMoney=factMoney where chargedType=1 and onlineMoney is null";
			list.add(sql);
		}
		for (String sql : list) {
			CarparkDataBaseUtil.executeSQL(instance.getDbServerIp(), instance.getDbServerPort(), "carpark", instance.getDbServerUsername(), instance.getDbServerPassword(), sql,
					instance.getDbServerType());
		}
	}

	private boolean checkVercionIsLess(String databaseVersion, String updateVersion) {
		Integer now = Integer.valueOf(databaseVersion.replaceAll("\\.", ""));
		Integer c = Integer.valueOf(updateVersion.replaceAll("\\.", ""));
		return now < c;
	}

	private void startBackGroudService() {
		try {
			LOGGER.info("上海云停车场服务启动设置：{}", ShanghaiYunCarparkCfg.getInstance().isStart());
			if (ShanghaiYunCarparkCfg.getInstance().isStart()) {
				try {
					Injector injector = serverInjector.createChildInjector(new AbstractModule() {
						@Override
						protected void configure() {
							bind(ShangHaiYunCarParkService.class).toInstance(sp.getYunCarparkService());
						}
					});
					ShangHaiYunCarParkService yunCarparkService = injector.getInstance(ShangHaiYunCarParkService.class);
					YunCarparkStartService service = new YunCarparkStartService(yunCarparkService);
					service.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// YunCarparkStartService service=new YunCarparkStartService(sp.getYunCarparkService());
				// service.start();
			}
			checkHaiYunService();
			serverInjector.getInstance(SmsSendServiceI.class).startAsync();
		} catch (Exception e) {
			LOGGER.error("启动后台服务时发生错误",e);
			
		}
	}

	/**
	 * 贵州海誉推送服务
	 */
	private void checkHaiYunService() {
		Boolean valueOf = Boolean.valueOf(System.getProperty(ConstUtil.PUSH_HAIYU_RECORD, "false"));
		LOGGER.info("贵州海誉云平台服务启动设置为：{}", valueOf);
		if (valueOf) {
			serverInjector.getInstance(AsynHaiYuRecordService.class).startAsync();
		}
	}

	private void updateDatabase(SingleCarparkSystemSetting dbVersion) {
		try {
			dbVersion.setSettingValue(SystemSettingTypeEnum.DateBase_version.getDefaultValue());
			sp.getCarparkService().saveSystemSetting(dbVersion);
			List<SingleCarparkDeviceVoice> findAllVoiceInfo = sp.getCarparkService().findAllVoiceInfo();
			Map<DeviceVoiceTypeEnum, SingleCarparkDeviceVoice> map = new HashMap<>();
			for (SingleCarparkDeviceVoice singleCarparkDeviceVoice : findAllVoiceInfo) {
				map.put(singleCarparkDeviceVoice.getType(), singleCarparkDeviceVoice);
			}
			List<SingleCarparkDeviceVoice> list = new ArrayList<>();
			for (DeviceVoiceTypeEnum vt : DeviceVoiceTypeEnum.values()) {
				if (map.get(vt) != null) {
					continue;
				}
				SingleCarparkDeviceVoice dv = new SingleCarparkDeviceVoice();
				dv.setContent(vt.getContent());
				dv.setVolume(vt.getVolume());
				dv.setType(vt);
				list.add(dv);
			}
			sp.getCarparkService().saveDeviceVoice(list);
		} catch (Exception e) {
			LOGGER.error("更新数据库时发生错误", e);
		}
	}

	private void autoDeleteSameInOutHistory() {
//		ScheduledExecutorService deleteExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("每隔10分钟清除数据库的重复进出场记录"));
//		deleteExecutor.scheduleWithFixedDelay(new Runnable() {
//			@Override
//			public void run() {
//				CarparkUtils.cleanSameInOutHistory();
//			}
//		}, 10, 10, TimeUnit.MINUTES);

	}

	/**
	 * 自动上传停车场信息到云平台
	 */
	@SuppressWarnings("unchecked")
	private void autoSendInfoToCloud() {
		IpmsSynchroServiceI instance = serverInjector.getInstance(IpmsSynchroServiceI.class);
		instance.startAsync();
		
		SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.上传数据到绿地平台.name());
		if (findSystemSettingByKey!=null&&findSystemSettingByKey.getBooleanValue()) {
    		LvdiSynchroServiceI instance2 = serverInjector.getInstance(LvdiSynchroServiceI.class);
    		instance2.startAsync();
		}

		CarparkYunConfig cf = (CarparkYunConfig) CarparkFileUtils.readObject(YunConfigUI.CARPARK_YUN_CONFIG);
		if (cf == null) {
			return;
		}
		if (!cf.getAutoStartServer()) {
			return;
		}
		webService.init();
		int uploadTime = 10;
		String ot = System.getenv("uploadTime");
		if (ot != null) {
			Integer valueOf = Integer.valueOf(ot);
			uploadTime = valueOf;
		}
		ScheduledExecutorService userExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("上传用户数据到云平台"));
		ScheduledExecutorService inExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("上传进场数据到云平台"));
		ScheduledExecutorService outExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("上传出场数据到云平台"));
		ScheduledExecutorService infoExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("上传停车场数据到云平台"));
		ScheduledExecutorService lockCarExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("获取云平台上的锁车数据"));
		userExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("准备上传固定用户记录到云平台");
				Long id = (Long) CarparkFileUtils.readObject("userLastUploadId");
				id = id == null ? 0L : id;
				List<Long> errorIds = (List<Long>) CarparkFileUtils.readObject("userErrorUploadId");
				if (StrUtil.isEmpty(errorIds)) {
					errorIds = new ArrayList<>();
				}
				LOGGER.info("上次上传固定用户记录到{},上传失败的为：{}", id, errorIds);
				try {
					List<SingleCarparkUser> list = sp.getCarparkUserService().findUserThanIdMore(id, errorIds);
					LOGGER.info("还有{}条固定用户记录等待上传", list.size());
					for (SingleCarparkUser singleCarparkUser : list) {
						LOGGER.info("正在上传用户{}的记录", singleCarparkUser);
						boolean sendUser = webService.sendUser(singleCarparkUser);
						if (!sendUser) {
							if (!errorIds.contains(singleCarparkUser.getId())) {
								errorIds.add(singleCarparkUser.getId());
							}
						} else {
							errorIds.remove(singleCarparkUser.getId());
							if (singleCarparkUser.getId() > id) {
								id = singleCarparkUser.getId();
							}
						}
						LOGGER.info("上传用户{}的记录结果为{}", singleCarparkUser.getPlateNo(), sendUser);
					}
				} catch (Exception e) {
					LOGGER.error("上传固定用户信息时发生错误", e);
				} finally {
					CarparkFileUtils.writeObject("userLastUploadId", id);
					CarparkFileUtils.writeObject("userErrorUploadId", errorIds);
				}
			}
		}, uploadTime, uploadTime, TimeUnit.SECONDS);
		inExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("准备上传进场记录到云平台");
				Long id = (Long) CarparkFileUtils.readObject("inLastUploadId");
				id = id == null ? 0L : id;
				List<Long> errorIds = (List<Long>) CarparkFileUtils.readObject("inErrorUploadId");
				if (StrUtil.isEmpty(errorIds)) {
					errorIds = new ArrayList<>();
				}
				LOGGER.info("上次上传进场记录到{},上传失败的为：{}", id, errorIds);
				try {
					List<SingleCarparkInOutHistory> list = sp.getCarparkInOutService().findInHistoryThanIdMore(id, errorIds);
					LOGGER.info("还有{}条进场记录等待上传", list.size());
					for (SingleCarparkInOutHistory in : list) {
						LOGGER.info("正在上传车牌{}的进场记录", in.getPlateNo());
						boolean sendInHistory = webService.sendInHistory(in);
						if (!sendInHistory) {
							errorIds.add(in.getId());
						} else {
							errorIds.remove(in.getId());
							if (in.getId() > id) {
								id = in.getId();
							}
						}
						LOGGER.info("上传车牌{}的进场记录结果为{}", in.getPlateNo(), sendInHistory);
					}
				} catch (Exception e) {
					LOGGER.error("上传进场记录时发生错误", e);
				} finally {
					CarparkFileUtils.writeObject("inLastUploadId", id);
					CarparkFileUtils.writeObject("inErrorUploadId", errorIds);
				}
			}
		}, uploadTime, uploadTime, TimeUnit.SECONDS);

		outExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("准备上传出场记录到云平台");
				Long id = (Long) CarparkFileUtils.readObject("outLastUploadId");
				id = id == null ? 0L : id;
				List<Long> errorIds = (List<Long>) CarparkFileUtils.readObject("outErrorUploadId");
				if (StrUtil.isEmpty(errorIds)) {
					errorIds = new ArrayList<>();
				}
				try {
					LOGGER.info("上次上传出场记录到{},上传失败的为：{}", id, errorIds);
					List<SingleCarparkInOutHistory> list = sp.getCarparkInOutService().findOutHistoryThanIdMore(id, errorIds);
					LOGGER.info("还有{}条出场记录等待上传", list.size());
					for (SingleCarparkInOutHistory in : list) {
						LOGGER.info("正在上传车牌{}的出场记录", in.getPlateNo());
						boolean sendOutHistory = webService.sendOutHistory(in);
						if (!sendOutHistory) {
							if (!errorIds.contains(in.getId())) {
								errorIds.add(in.getId());
							}
						} else {
							errorIds.remove(in.getId());
							if (in.getId() > id) {
								id = in.getId();
							}
						}
						LOGGER.info("上传车牌{}的出场记录结果为{}", in.getPlateNo(), sendOutHistory);
					}
				} catch (Exception e) {
					LOGGER.error("上传出场记录失败", e);
				} finally {
					CarparkFileUtils.writeObject("outLastUploadId", id);
					CarparkFileUtils.writeObject("outErrorUploadId", errorIds);
				}
			}
		}, uploadTime, uploadTime, TimeUnit.SECONDS);

		infoExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					LOGGER.info("准备上传停车场信息");
					List<SingleCarparkCarpark> findCarparkToLevel = sp.getCarparkService().findCarparkToLevel();
					for (SingleCarparkCarpark singleCarparkCarpark : findCarparkToLevel) {
						LOGGER.info("准备上传停车场{}信息", singleCarparkCarpark);
						boolean sendCarparkInfo = webService.sendCarparkInfo(singleCarparkCarpark);
						LOGGER.info("上传停车场{}信息结果：{}", singleCarparkCarpark, sendCarparkInfo);
					}
				} catch (Exception e) {
					LOGGER.error("上传停车场信息时发生错误", e);
				}
			}
		}, uploadTime, uploadTime, TimeUnit.SECONDS);
		lockCarExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					webService.getLockCarInfo();
				} catch (Exception e) {
					LOGGER.error("锁车时发生错误", e);
				}

			}
		}, 5, 5, TimeUnit.SECONDS);

	}

	// 定时检测加密狗
	private void autoCheckSoftDog() {
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					SingleCarparkSystemSetting sn = sp.getCarparkService().findSystemSettingByKey(SNSettingType.sn.name());
					LOGGER.info("从数据库获取注册码信息{}", sn);
					if (StrUtil.isEmpty(sn)) {
						return;
					}
					Date dateOfExpire = null;
					for (int i = 0; i < 3; i++) {
						try {
							LOGGER.info("第{}次解析从数据库获取注册码信息", i + 1);
							av = new AppVerifierImpl(new SoftDogWin());
							AppAuthorization decrypt = av.decrypt(sn.getSettingValue());
							if (decrypt == null) {
								continue;
							}
							dateOfExpire = decrypt.getDateOfExpire();
							// dateOfExpire=new DateTime(2015,12,8,1,1).toDate();
							if (StrUtil.getTodayBottomTime(dateOfExpire).before(new Date())) {
								LOGGER.info("解析从数据库获取注册码信息成功,已过期{}", dateOfExpire);
								dateOfExpire = null;
							}
							LOGGER.info("解析从数据库获取注册码信息成功");
							break;
						} catch (Exception e) {
							LOGGER.error("解析从数据库获取注册码信息失败," + e.getMessage());
						}
						Thread.sleep(10000);
					}
					SingleCarparkSystemSetting vilidTo = new SingleCarparkSystemSetting();
					vilidTo.setSettingKey(SNSettingType.validTo.name());
					vilidTo.setSettingValue(StrUtil.formatDate(dateOfExpire, ConstUtil.YYYY_MM_DD));
					sp.getCarparkService().saveSystemSetting(vilidTo);
					LOGGER.info("把解析到的信息保存到数据库");
				} catch (Exception e) {
					LOGGER.error("解析注册码信息失败," + e.getMessage());
				}
			}
		}, 30, 60 * 30, TimeUnit.SECONDS);
	}

	/**
	 * 
	 */
	private void openView() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!isOpen) {
					shell.open();
					isOpen = true;
					shell.layout();
				}
				if (shell.isVisible()) {
					return;
				}
				shell.setVisible(false);
				shell.setMaximized(true);
				shell.setMaximized(false);
				shell.setVisible(true);
			}
		});
	}
}
