package com.donglu.carpark.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.beust.jcommander.JCommander;
import com.donglu.carpark.server.CarparkHardwareGuiceModule;
import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.server.ServerUI;
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.service.CarparkClientLocalVMServiceProvider;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkLocalVMServiceProvider;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.blservice.DatabaseServiceProvider;
import com.dongluhitec.card.blservice.HardwareFacility;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIGuiceModule;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.security.LocalSecurityManager;
import com.dongluhitec.card.domain.security.impl.SecurityManagerImpl;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.HardwareGuiceModule;
import com.dongluhitec.card.hardware.service.BasicHardwareService;
import com.dongluhitec.card.hardware.service.impl.BasicHardwareServiceSyncImpl;
import com.dongluhitec.card.hardware.util.HardwareFacilityImpl;
import com.dongluhitec.card.server.ServerConfigurator;
import com.dongluhitec.card.service.ServiceGuiceModule;
import com.dongluhitec.card.service.impl.LocalVMServiceProvider;
import com.dongluhitec.card.ui.main.DongluUIAppConfigurator;
import com.dongluhitec.card.ui.main.guice.CardUIViewerGuiceModule;
import com.dongluhitec.card.ui.main.javafx.DongluJavaFXModule;
import com.dongluhitec.card.ui.util.FileUtils;
import com.dongluhitec.card.ui.util.WidgetUtil;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import org.eclipse.wb.swt.SWTResourceManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.graphics.Point;

public class Login {
	private static final String USER_NAMES = "userNames";

	private static final String SYSTEM_TEMP = "system.temp";

	private static final String MONITOR_TEMP = "monitor.temp";

	public static final String CHECK_SOFT_DOG = "checkSoftDog";

	private static Logger LOGGER = LoggerFactory.getLogger(Login.class);

	protected Shell shell;
	private Text txt_password;
	private Label lbl_msg;
	private Combo combo;
	@Inject
	private CarparkManageApp carparkManageApp;
	@Inject
	private CarparkMainApp carparkMainApp;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private final String selectType = "LoginSelectType";
	private App app;
	@Inject
	private ClientConfigUI clientConfigUI;
	@Inject
	private CommonUIFacility commonui;

	private Button btn_login;

	private Combo cbo_userName;

	private List<String> list;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		Display display = Display.getDefault();
		Tray systemTray = display.getSystemTray();
		TrayItem[] items = systemTray.getItems();
		for (TrayItem trayItem : items) {
			System.out.println(trayItem.getText());
		}
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					DongluUIAppConfigurator configurator = new DongluUIAppConfigurator();
					new JCommander(configurator, args);
					Injector createInjector = Guice.createInjector(new AbstractModule() {
						@Override
						protected void configure() {
							this.bindConstant().annotatedWith(Names.named("HBM2DDL")).to("update");
							this.bind(HardwareFacility.class).to(HardwareFacilityImpl.class);
							this.bind(CarparkDatabaseServiceProvider.class).to(CarparkClientLocalVMServiceProvider.class).in(Singleton.class);
							this.bind(CarparkServerConfig.class).toInstance(CarparkServerConfig.getInstance());
							// this.bind(LocalSecurityManager.class).to(SecurityManagerImpl.class);

						}
					}, new CarparkHardwareGuiceModule(), new CommonUIGuiceModule());

					Login window = createInjector.getInstance(Login.class);
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("main is error");
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		try {
			File f = new File(MONITOR_TEMP);
			if (f.exists()) {
				f.delete();
			}
			f = new File(SYSTEM_TEMP);
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Display display = Display.getDefault();
		createContents();
		WidgetUtil.center(shell);
		shell.open();
		shell.setImage(JFaceUtil.getImage("carpark_16"));
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(300, 291);
		shell.setText("用户登录");
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 15;
		shell.setLayout(gridLayout);

		Composite composite_2 = new Composite(shell, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(1, false);
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		gl_composite_2.verticalSpacing = 0;
		gl_composite_2.horizontalSpacing = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		lbl_msg = new Label(composite_2, SWT.NONE);
		lbl_msg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		lbl_msg.setAlignment(SWT.CENTER);
		lbl_msg.setFont(SWTResourceManager.getFont("微软雅黑", 16, SWT.BOLD));
		lbl_msg.setText("停车场用户登录");

		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 10;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("用户名");
		
		ComboViewer comboViewer_1 = new ComboViewer(composite, SWT.NONE);
		cbo_userName = comboViewer_1.getCombo();
		cbo_userName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		cbo_userName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		list = (List<String>) FileUtils.readObject(USER_NAMES);
		if (list==null) {
			list=new ArrayList<>();
		}
		comboViewer_1.setInput(list);
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("密    码");

		txt_password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txt_password.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 13) {
					login();
				}
			}
		});
		txt_password.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_txt_password = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txt_password.widthHint = 150;
		txt_password.setLayoutData(gd_txt_password);

		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("类    型");

		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				int i = combo.getSelectionIndex();
				FileUtils.writeObject(selectType, i);
			}
		});
		combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo.widthHint = 150;
		combo.setLayoutData(gd_combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[] { "监控界面", "管理界面" });
		Object readObject = FileUtils.readObject(selectType);
		if (StrUtil.isEmpty(readObject)) {
			combo.select(0);
		} else {
			combo.select((int) readObject);
		}

		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));

		btn_login = new Button(composite_1, SWT.NONE);
		btn_login.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btn_login.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btn_login.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				btn_login.setEnabled(false);
				login();
			}
		});
		btn_login.setText("登录");

		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);
			}
		});
		button_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.setText("取消");

		Button button_2 = new Button(composite_1, SWT.NONE);
		button_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clientConfigUI.open();
			}
		});
		button_2.setText("配置");

	}

	/**
	 * 
	 */
	public void login() {
		new Thread(new Runnable() {
			public void run() {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						String userName = null;
						String pwd = null;
						String type = null;
						long nanoTime = System.nanoTime();
						try {
							if (!check()) {
								return;
							}
							sp.start();

							LOGGER.info("服务启动花费时间{}", System.nanoTime() - nanoTime);
							autoDeletePhoto();

							SingleCarparkSystemUser findByNameAndPassword = sp.getSystemUserService().findByNameAndPassword(cbo_userName.getText(), txt_password.getText());
							if (StrUtil.isEmpty(findByNameAndPassword)) {
								commonui.error("提示", "用户名或密码错误！");
								btn_login.setEnabled(true);
								return;
							}
							userName = findByNameAndPassword.getUserName();
							if (!list.contains(userName)) {
								list.add(userName);
								FileUtils.writeObject(USER_NAMES, list);
							}
							pwd = findByNameAndPassword.getPassword();
							type = findByNameAndPassword.getType();
							System.setProperty("userName", userName);
							System.setProperty("password", pwd);
							System.setProperty("userType", type);
						} catch (Exception e1) {
							e1.printStackTrace();
							lbl_msg.setText(e1.getMessage());
							return;
						}
						LOGGER.info("用户验证花费时间{}", System.nanoTime() - nanoTime);
						File file = null;
						RandomAccessFile raf = null;
						FileChannel channel = null;
						FileLock tryLock = null;
						try {
							shell.setVisible(false);
							String loginApp = combo.getText();
							if (type.equals("操作员")) {
								//
								file = new File(MONITOR_TEMP);
								if (file.exists()) {
									commonui.error("错误", "已经打开了监控界面");
									return;
								}
								file.createNewFile();
								raf = new RandomAccessFile(file, "rw");
								channel = raf.getChannel();
								tryLock = channel.tryLock();
								//
								app = carparkMainApp;
								app.open();
							} else {
								if (loginApp.equals("监控界面")) {
									file = new File(MONITOR_TEMP);
									if (file.exists()) {
										commonui.error("错误", "已经打开了监控界面");
										return;
									}
									file.createNewFile();
									raf = new RandomAccessFile(file, "rw");
									channel = raf.getChannel();
									tryLock = channel.tryLock();
									app = carparkMainApp;
									app.open();
								} else if (loginApp.equals("管理界面")) {
									file = new File(SYSTEM_TEMP);
									if (file.exists()) {
										commonui.error("错误", "已经打开了管理界面");
										return;
									}
									file.createNewFile();
									raf = new RandomAccessFile(file, "rw");
									channel = raf.getChannel();
									tryLock = channel.tryLock();

									app = carparkManageApp;
									app.open();
								}
							}
							LOGGER.info("界面打开花费时间{}", System.nanoTime() - nanoTime);
						} catch (Exception e) {
							LOGGER.error("main is error" + "界面出错=======", e);
							// app.disponse();
							// app.setShell(new Shell());
							// app.open();
							// shell.setVisible(true);

						} finally {
							try {
								tryLock.release();
								raf.close();
								boolean delete = false;
								while (!delete) {
									delete = file.delete();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							System.exit(0);
						}
					}
				});
			}
		}).start();
		if (Boolean.valueOf(System.getProperty(CHECK_SOFT_DOG) == null ? "true" : "false")) {
			checkSoftDog();
		}
	}

	protected boolean check() {
		try {

			String upload = FileuploadSend.upload("http://" + CarparkClientConfig.getInstance().getDbServerIp() + ":8899/server/", null);
			String[] s = upload.split("/");

			CarparkClientConfig instance = CarparkClientConfig.getInstance();
			instance.setDbServerPort(s[1]);
			instance.setDbServerUsername(s[2]);
			instance.setDbServerPassword(s[3]);
			FileUtils.writeObject(ClientConfigUI.CARPARK_CLIENT_CONFIG, instance);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("连接失败", "连接失败,请检查服务器状态");
			return false;
		}

	}

	// 检测加密狗
	private void checkSoftDog() {
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					LOGGER.info("客户端从数据库获取注册信息");
					Map<SNSettingType, SingleCarparkSystemSetting> findAllSN = sp.getCarparkService().findAllSN();
					String sn = findAllSN.get(SNSettingType.sn).getSettingValue();
					String validTo = findAllSN.get(SNSettingType.validTo).getSettingValue();

					if (StrUtil.isEmpty(sn) || StrUtil.isEmpty(validTo)) {
						LOGGER.info("没有检测到注册码，请检测服务器加密狗");
						commonui.error("检查失败", "没有检测到注册码，请检测服务器加密狗");
						System.exit(0);
						return;
					}
					LOGGER.info("检查注册码成功,有效期至{}", validTo);
				} catch (Exception e) {
					e.printStackTrace();
					commonui.error("检查失败", "没有检测到注册码，请检测服务器加密狗");
					System.exit(0);
				}
			}
		}, 5, 60 * 3, TimeUnit.MINUTES);
	}

	// 自动删除图片
	private void autoDeletePhoto() {
		ScheduledExecutorService newSingleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		newSingleThreadScheduledExecutor.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				SingleCarparkSystemSetting ss1 = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.是否自动删除图片.name());
				if (StrUtil.isEmpty(ss1) || ss1.getSettingValue().equals(SystemSettingTypeEnum.是否自动删除图片.getDefaultValue())) {
					LOGGER.info("是否自动删除图片设置为{}", StrUtil.isEmpty(ss1)?null:ss1.getSettingValue());
					return;
				}
				SingleCarparkSystemSetting ss2 = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.图片保存多少天.name());
				int saveMonth = Integer.valueOf(ss2 == null ? SystemSettingTypeEnum.图片保存多少天.getDefaultValue() : ss2.getSettingValue());
				LOGGER.info("图片保存多少天设置为{}", saveMonth);
				String imgSavePath = (String) FileUtils.readObject(CarparkManageApp.CLIENT_IMAGE_SAVE_FILE_PATH);
				String savePath = (imgSavePath == null ? System.getProperty("user.dir") : imgSavePath) + "/img/";
				Date d = new Date();
				DateTime deleteTime = new DateTime(d).minusDays(saveMonth + 1);
				String nowMonth=deleteTime.toString("MM");
				String day = deleteTime.toString("dd");
				String month = deleteTime.toString("MM");
				int year = deleteTime.getYear();
				int nowYear=deleteTime.getYear();
				File file;
				while (true) {
					String pathname = savePath + year + "/" + month;
					LOGGER.info("检测文件夹{}是否存在", pathname);
					file = new File(pathname);
					if (file.isDirectory()) {
						LOGGER.info("文件夹{}存在,准备删除文件夹", pathname);
						if (month.equals(nowMonth)) {
							for (File f : file.listFiles()) {
								try {
									if (Integer.valueOf(f.getName())<=Integer.valueOf(day)) {
										CarparkUtils.deleteDir(f);
									}
								} catch (NumberFormatException e) {
									continue;
								}
							}
						}else{
							CarparkUtils.deleteDir(file);
						}
					} else {
						while (true) {
    						String pathname1 = savePath + (year);
    						LOGGER.info("检测文件夹{}是否存在", pathname1);
    						file = new File(pathname1);
    						if (file.isDirectory()) {
    							LOGGER.info("文件夹{}存在,准备删除文件夹", pathname1);
    							if (year==nowYear) {
    								for (File f : file.listFiles()) {
    									try {
    										if (Integer.valueOf(f.getName())<=Integer.valueOf(month)) {
    											CarparkUtils.deleteDir(f);
    										}
    									} catch (NumberFormatException e) {
    										continue;
    									}
    								}
    							}else{
    								CarparkUtils.deleteDir(file);
    							}
    						}else{
        						LOGGER.info("文件夹{}不存在,退出任务", pathname1);
        						break;
    						}
    						year-=1;
						}
						break;
					}
					month=deleteTime.minusMonths(1).toString("MM");
				}
			}
		}, 5, 60 * 24, TimeUnit.MINUTES);
	}
}
