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
import com.donglu.carpark.service.background.haiyu.AsynHaiYuRecordService;
import com.donglu.carpark.ui.Login;
import com.donglu.carpark.ui.wizard.sn.ImportSNModel;
import com.donglu.carpark.ui.wizard.sn.ImportSNWizard;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.SystemUpdate;
import com.donglu.carpark.yun.CarparkYunConfig;
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
import com.dongluhitec.card.util.ThreadUtil;
import com.dongluhitec.core.crypto.appauth.AppAuthorization;
import com.dongluhitec.core.crypto.appauth.AppVerifier;
import com.dongluhitec.core.crypto.appauth.AppVerifierImpl;
import com.dongluhitec.core.crypto.softdog.SoftDogWin;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

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
	private boolean autoStartServerCfg=false;

	public static Injector serverInjector;
	//系统托盘图片
	private TrayIcon icon;
	//判断是否允许创建系统托盘
	private boolean isSystemTraySupported=true;
	//判断界面是否打开
	private boolean isOpen=false;
	private String btnStartType="start";
	private String btnStartText="启    动";
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
					LOGGER.info("依赖注入用时：{}",(System.nanoTime()-nanoTime));
					ImageServerUI window = serverInjector.getInstance(ImageServerUI.class);
					LOGGER.info("获取界面用时：{}",(System.nanoTime()-nanoTime));
					window.open();
				} catch (Exception e) {
					LOGGER.error("启动时发生错误",e);
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
			shell = new Shell(SWT.MAX|SWT.MIN|SWT.CLOSE|SWT.ON_TOP|SWT.RESIZE);
			Object readObject = CarparkFileUtils.readObject("autoStartServer");
			if (readObject != null) {
				autoStartServerCfg = (boolean) readObject;
			}
			if (autoStartServerCfg) {
				autoStartServer();
			}
			Display display = Display.getDefault();
			if (autoStartServerCfg) {
				shell.setVisible(false);
			}else{
				createContents();
				shell.open();
				isOpen=true;
				shell.layout();
			}
			LOGGER.info("界面加载用时：{}", System.nanoTime() - nanoTime);
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			LOGGER.error("服务器发送错误,系统退出",e);
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
			if (e.getMessage().indexOf("服务器")>-1) {
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
			LOGGER.error("导入注册码时发生错误",e);
		}
	}

	private void init() {
		try {
			sp.start();
			SingleCarparkSystemSetting s = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.图片保存位置.name());
			filePath = StrUtil.isEmpty(s) ? System.getProperty("user.dir") : s.getSettingValue();
			CarparkFileUtils.writeObject(ConstUtil.IMAGE_SAVE_DIRECTORY, filePath);
		} catch (Exception e) {
			LOGGER.error("初始化时发生错误",e);
		} finally {
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell.setSize(535, 99);
		shell.setText("服务器");
		shell.setLayout(new GridLayout(5, false));
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION | SWT.APPLICATION_MODAL);

				box.setText("退出提示");
				box.setMessage("确认退出服务器？退出服务器后客户端的图片将不会在服务器端备份！");
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
				}
			}

		});
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				System.exit(0);
			}
		});
		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("图片保存路径");

		text = new Text(shell, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text.setEditable(false);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 214;
		text.setLayoutData(gd_text);
		Object readObject = CarparkFileUtils.readObject(ConstUtil.IMAGE_SAVE_DIRECTORY);
		text.setText(readObject == null ? System.getProperty("user.dir") : (String) readObject);
		Button button = new Button(shell, SWT.NONE);
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

		btnStart = new Button(shell, SWT.NONE);
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
						if (e1.getMessage().indexOf("服务器")>-1) {
							commonui.error("启动失败", ""+e1);
							btnStart.setText("启    动");
							btnStart.setEnabled(true);
						}
					}
				}
				if (data.equals("stop")) {
					System.exit(0);
				}
			}
		});
		btnStart.setText(btnStartText);
		
		shell.setImage(JFaceUtil.getImage("carpark_16"));
		
		Button button_1 = new Button(shell, SWT.CHECK);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = button_1.getSelection();
				CarparkFileUtils.writeObject("autoStartServer", selection);
			}
		});
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.setText("自动启动");
		
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
				ui.open();
				shell.setEnabled(false);
				init();
				shell.setEnabled(true);
				text.setText(filePath);
				text.setFocus();
			}
		});
		menuItem.setText("服务器配置");
		
		MenuItem menuItem_1 = new MenuItem(menu_1, SWT.NONE);
		menuItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importSN();
			}
		});
		menuItem_1.setText("注册码");
		
		MenuItem menuItem_2 = new MenuItem(menu_1, SWT.NONE);
		menuItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				yunUI.open();
			}
		});
		menuItem_2.setText("云上传配置");
		MenuItem menuItem_3 = new MenuItem(menu_1, SWT.NONE);
		menuItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new StoreServerUI().open();
			}
		});
		menuItem_3.setText("商铺配置");
		button_1.setSelection(autoStartServerCfg);
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
				if (e.getClickCount()==2) {
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
			LOGGER.error("添加托盘图标时发生错误",e);
		}
	}
	/**
	 * 启动服务
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
			if (!findSystemSettingByKey.getSettingValue().equals(SystemSettingTypeEnum.软件版本.getDefaultValue())) {
				SystemUpdate update = new SystemUpdate();
				try {
					update.systemUpdate(findSystemSettingByKey.getSettingValue(), SystemSettingTypeEnum.软件版本.getDefaultValue());
					updateDatabase(findSystemSettingByKey);
				} catch (Exception e) {
					LOGGER.error("数据库更新时发生错误",e);
				}
			}
			if (Boolean.valueOf(System.getProperty(Login.CHECK_SOFT_DOG) == null ? "true" : "false")) {
				autoCheckSoftDog();
			}
			
			int port = 8899;
			String property = System.getProperty("ServerPort");
			if (property!=null) {
				port=Integer.valueOf(property);
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
			throw new Exception("服务器启动失败");
		}
		autoDeleteSameInOutHistory();
		autoSendInfoToCloud();
		checkHaiYunService();
	}
	/**
	 * 贵州海誉推送服务
	 */
	private void checkHaiYunService(){
		if (Boolean.valueOf(System.getProperty(ConstUtil.PUSH_HAIYU_RECORD) == null ? "false" : "true")) {
			serverInjector.getInstance(AsynHaiYuRecordService.class).startAsync();
		}
	}

	private void updateDatabase(SingleCarparkSystemSetting dbVersion) {
		try {
			dbVersion.setSettingValue(SystemSettingTypeEnum.DateBase_version.getDefaultValue());
			sp.getCarparkService().saveSystemSetting(dbVersion);
			List<SingleCarparkDeviceVoice> findAllVoiceInfo = sp.getCarparkService().findAllVoiceInfo();
			Map<DeviceVoiceTypeEnum, SingleCarparkDeviceVoice> map=new HashMap<>();
			for (SingleCarparkDeviceVoice singleCarparkDeviceVoice : findAllVoiceInfo) {
				map.put(singleCarparkDeviceVoice.getType(), singleCarparkDeviceVoice);
			}
			List<SingleCarparkDeviceVoice> list=new ArrayList<>();
			for (DeviceVoiceTypeEnum vt : DeviceVoiceTypeEnum.values()) {
				if (map.get(vt)!=null) {
					continue;
				}
				SingleCarparkDeviceVoice dv=new SingleCarparkDeviceVoice();
				dv.setContent(vt.getContent());
				dv.setVolume(vt.getVolume());
				dv.setType(vt);
				list.add(dv);
			}
			sp.getCarparkService().saveDeviceVoice(list);
		} catch (Exception e) {
			LOGGER.error("更新数据库时发生错误",e);
		}
	}

	private void autoDeleteSameInOutHistory() {
		ScheduledExecutorService deleteExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("每隔一小时清除数据库的重复进出场记录"));
		deleteExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				CarparkUtils.cleanSameInOutHistory();
			}
		}, 10, 10, TimeUnit.MINUTES);
		
	}

	/**
	 * 自动上传停车场信息到云平台
	 */
	@SuppressWarnings("unchecked")
	private void autoSendInfoToCloud() {
		new Thread(new Runnable() {
			public void run() {
				try {
					SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用CJLAPP支付.name());
					if (findSystemSettingByKey != null && findSystemSettingByKey.getBooleanValue()) {
						IpmsSynchroServiceI instance = serverInjector.getInstance(IpmsSynchroServiceI.class);
						instance.startAsync();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		CarparkYunConfig cf = (CarparkYunConfig) CarparkFileUtils.readObject(YunConfigUI.CARPARK_YUN_CONFIG);
		if (cf==null) {
			return;
		}
		if (!cf.getAutoStartServer()) {
			return;
		}
		webService.init();
		int uploadTime = 10;
		String ot = System.getenv("uploadTime");
		if (ot!=null) {
			Integer valueOf = Integer.valueOf(ot);
			uploadTime=valueOf;
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
				Long id=(Long) CarparkFileUtils.readObject("userLastUploadId");
				id=id==null?0L:id;
				List<Long> errorIds=(List<Long>) CarparkFileUtils.readObject("userErrorUploadId");
				if (StrUtil.isEmpty(errorIds)) {
					errorIds=new ArrayList<>();
				}
				LOGGER.info("上次上传固定用户记录到{},上传失败的为：{}",id,errorIds);
				try {
					List<SingleCarparkUser> list=sp.getCarparkUserService().findUserThanIdMore(id,errorIds);
					LOGGER.info("还有{}条固定用户记录等待上传",list.size());
					for (SingleCarparkUser singleCarparkUser : list) {
						LOGGER.info("正在上传用户{}的记录",singleCarparkUser);
						boolean sendUser = webService.sendUser(singleCarparkUser);
						if (!sendUser) {
							if (!errorIds.contains(singleCarparkUser.getId())) {
								errorIds.add(singleCarparkUser.getId());
							}
						}else{
							errorIds.remove(singleCarparkUser.getId());
							if (singleCarparkUser.getId()>id) {
								id=singleCarparkUser.getId();
							}
						}
						LOGGER.info("上传用户{}的记录结果为{}",singleCarparkUser.getPlateNo(),sendUser);
					}
				} catch (Exception e) {
					LOGGER.error("上传固定用户信息时发生错误",e);
				}finally{
					CarparkFileUtils.writeObject("userLastUploadId",id);
					CarparkFileUtils.writeObject("userErrorUploadId",errorIds);
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
						LOGGER.info("上传车牌{}的进场记录结果为{}",in.getPlateNo(), sendInHistory);
					}
				} catch (Exception e) {
					LOGGER.error("上传进场记录时发生错误",e);
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
				Long id=(Long) CarparkFileUtils.readObject("outLastUploadId");
				id=id==null?0L:id;
				List<Long> errorIds=(List<Long>) CarparkFileUtils.readObject("outErrorUploadId");
				if (StrUtil.isEmpty(errorIds)) {
					errorIds=new ArrayList<>();
				}
				try {
					LOGGER.info("上次上传出场记录到{},上传失败的为：{}",id,errorIds);
					List<SingleCarparkInOutHistory> list=sp.getCarparkInOutService().findOutHistoryThanIdMore(id,errorIds);
					LOGGER.info("还有{}条出场记录等待上传",list.size());
					for (SingleCarparkInOutHistory in : list) {
						LOGGER.info("正在上传车牌{}的出场记录",in.getPlateNo());
						boolean sendOutHistory = webService.sendOutHistory(in);
						if (!sendOutHistory) {
							if (!errorIds.contains(in.getId())) {
								errorIds.add(in.getId());
							}
						}else{
							errorIds.remove(in.getId());
							if (in.getId()>id) {
								id=in.getId();
							}
						}
						LOGGER.info("上传车牌{}的出场记录结果为{}",in.getPlateNo(), sendOutHistory);
					}
				} catch (Exception e) {
					LOGGER.error("上传出场记录失败",e);
				}finally{
					CarparkFileUtils.writeObject("outLastUploadId",id);
					CarparkFileUtils.writeObject("outErrorUploadId",errorIds);
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
						LOGGER.info("准备上传停车场{}信息",singleCarparkCarpark);
						boolean sendCarparkInfo = webService.sendCarparkInfo(singleCarparkCarpark);
						LOGGER.info("上传停车场{}信息结果：{}",singleCarparkCarpark,sendCarparkInfo);
					}
				} catch (Exception e) {
					LOGGER.error("上传停车场信息时发生错误",e);
				}
			}
		}, uploadTime, uploadTime, TimeUnit.SECONDS);
		lockCarExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					webService.getLockCarInfo();
				} catch (Exception e) {
					LOGGER.error("锁车时发生错误",e);
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
							LOGGER.info("第{}次解析从数据库获取注册码信息",i+1);
							av = new AppVerifierImpl(new SoftDogWin());
							AppAuthorization decrypt = av.decrypt(sn.getSettingValue());
							if (decrypt==null) {
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
							LOGGER.error("解析从数据库获取注册码信息失败",e);
						}
						Thread.sleep(1000);
					}
					SingleCarparkSystemSetting vilidTo = new SingleCarparkSystemSetting();
					vilidTo.setSettingKey(SNSettingType.validTo.name());
					vilidTo.setSettingValue(StrUtil.formatDate(dateOfExpire, ConstUtil.YYYY_MM_DD));
					sp.getCarparkService().saveSystemSetting(vilidTo);
					LOGGER.info("把解析到的信息保存到数据库");
				} catch (Exception e) {
					LOGGER.error("解析注册码信息失败",e);
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
					createContents();
					shell.open();
					isOpen=true;
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
