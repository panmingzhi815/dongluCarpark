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
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.server.module.CarparkClientGuiceModule;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.SystemUserServiceI;
import com.donglu.carpark.service.background.ClientCheckSoftDogServiceI;
import com.donglu.carpark.service.background.ClientSynTimeServiceI;
import com.donglu.carpark.service.background.DeleteImageServiceI;
import com.donglu.carpark.service.background.LoginCheckServiceI;
import com.donglu.carpark.ui.common.App;
import com.donglu.carpark.util.TestMap;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.InjectorUtil;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.main.DongluUIAppConfigurator;
import com.dongluhitec.card.ui.util.WidgetUtil;
import com.google.inject.Injector;

import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class Login {
	private static final String CONCENTRATE_TEMP = "concentrate.temp";

	private static final String USER_NAMES = "userNames";

	private static final String SYSTEM_TEMP = "system.temp";

	private static final String MONITOR_TEMP = "monitor.temp";

	public static final String CHECK_SOFT_DOG = "checkSoftDog";

	private static Logger LOGGER = LoggerFactory.getLogger(Login.class);

	protected Shell shell;
	private Text txt_password;
	private Label lbl_msg;
	private Combo combo;
	private CarparkManageApp carparkManageApp;
	private CarparkMainApp carparkMainApp;
	private ConcentrateApp concentrateApp;
	
	private CarparkDatabaseServiceProvider sp;
	private ClientConfigUI clientConfigUI;
	private CommonUIFacility commonui;
	
	private final String selectType = "LoginSelectType";
	private App app;

	private Button btn_login;

	private Combo cbo_userName;

	private List<String> list;
	public static Injector injector;
	private TestMap<String, String, Boolean> testMap=new TestMap<>();

	private Button btn_savePassword;

	private Label lbl_errorMsg;

	private Composite composite_msg;
	private Label lblNewLabel_2;

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
			@Override
			public void run() {
				try {
					DongluUIAppConfigurator configurator = new DongluUIAppConfigurator();
					new JCommander(configurator, args);
					long nanoTime = System.nanoTime();
					injector = InjectorUtil.startsyn(new CarparkClientGuiceModule());
					System.out.println("依赖注入用时==="+(System.nanoTime()-nanoTime));
					Login window = injector.getInstance(Login.class);
					InjectorUtil.setInjector(injector);
					System.out.println("窗口打开==="+(System.nanoTime()-nanoTime));
					window.open();
				} catch (Throwable e) {
					e.printStackTrace();
					LOGGER.error("main is error",e);
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		long nanoTime = System.nanoTime();
		Display display = Display.getDefault();
		createContents();
		WidgetUtil.center(shell);
		shell.open();
		shell.setImage(JFaceUtil.getImage("carpark_16"));
		composite_msg = new Composite(shell, SWT.NONE);
		composite_msg.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		composite_msg.setLayout(new GridLayout(2, false));
		GridData gd_composite_msg = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_composite_msg.exclude = true;
		composite_msg.setLayoutData(gd_composite_msg);
		
		lbl_errorMsg = new Label(composite_msg, SWT.NONE);
		lbl_errorMsg.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		lbl_errorMsg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lbl_errorMsg.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lbl_errorMsg.setText("用户名或密码错误");
		
		lblNewLabel_2 = new Label(composite_msg, SWT.NONE);
		lblNewLabel_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				GridData gd_composite = (GridData) composite_msg.getLayoutData();
				gd_composite.exclude=true;
				shell.setSize(300,272);
				shell.layout();
			}
		});
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		lblNewLabel_2.setText("∧");
		shell.layout();
		shell.setFocus();
		System.out.println("login open use time is "+(System.nanoTime()-nanoTime));
		init();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		System.out.println("system is exit");
	}

	/**
	 * 
	 */
	public void deleteTempFile() {
		try {
			File f = new File(MONITOR_TEMP);
			if (f.exists()) {
				f.delete();
			}
			f = new File(SYSTEM_TEMP);
			if (f.exists()) {
				f.delete();
			}
			f = new File(CONCENTRATE_TEMP);
			if (f.exists()) {
				f.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		carparkManageApp=injector.getInstance(CarparkManageApp.class);
		carparkMainApp=injector.getInstance(CarparkMainApp.class);
		sp=injector.getInstance(CarparkDatabaseServiceProvider.class);
		clientConfigUI=injector.getInstance(ClientConfigUI.class);
		commonui=injector.getInstance(CommonUIFacility.class);
		concentrateApp=injector.getInstance(ConcentrateApp.class);
		cbo_userName.setFocus();
		LOGGER.info("软件版本：{}-{}",SystemSettingTypeEnum.软件版本.getDefaultValue(),SystemSettingTypeEnum.发布时间.getDefaultValue());
	}

	/**
	 * Create contents of the window.
	 */
	@SuppressWarnings("unchecked")
	protected void createContents() {
		shell = new Shell(SWT.MIN|SWT.CLOSE|SWT.ON_TOP);
		shell.setSize(300, 272);
		shell.setText("用户登录");
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 10;
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
		testMap = (TestMap<String, String, Boolean>) CarparkFileUtils.readObject("testMap");
		if (testMap==null) {
			testMap=new TestMap<>();
		}
		ComboViewer comboViewer_1 = new ComboViewer(composite, SWT.NONE);
		cbo_userName = comboViewer_1.getCombo();
		cbo_userName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		cbo_userName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		list = (List<String>) CarparkFileUtils.readObject(USER_NAMES);
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
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				int i = combo.getSelectionIndex();
				CarparkFileUtils.writeObject(selectType, i);
			}
		});
		combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo.widthHint = 150;
		combo.setLayoutData(gd_combo);
		new Label(composite, SWT.NONE);
		
		btn_savePassword = new Button(composite, SWT.CHECK);
		btn_savePassword.setText("保存密码");
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[] { "监控界面", "管理界面","收费界面" });
		Object readObject = CarparkFileUtils.readObject(selectType);
		if (StrUtil.isEmpty(readObject)) {
			combo.select(0);
		} else {
			combo.select((int) readObject);
		}
		combo.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 13) {
					login();
				}
			}
		});

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
				System.out.println("login over");
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
		cbo_userName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Boolean get1 = testMap.get1(cbo_userName.getText());
				btn_savePassword.setSelection(get1==null?false:get1);
				if (get1!=null&&get1) {
					txt_password.setText(testMap.get(cbo_userName.getText()));
					txt_password.setFocus();
				}else{
					txt_password.setText("");
				}
			}
		});
	}

	/**
	 * 
	 */
	public void login() {
		deleteTempFile();
		new Thread(new Runnable() {
			@Override
			public void run() {
				long nanoTime = System.nanoTime();
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						String userName = null;
						String pwd = null;
						String type = null;
						
						try {
							if (!check()) {
								btn_login.setEnabled(true);
								return;
							}
							sp.start();
							LOGGER.info("服务启动花费时间{}", System.nanoTime() - nanoTime);
							

							SystemUserServiceI systemUserService = sp.getSystemUserService();
							SingleCarparkSystemUser findByNameAndPassword = systemUserService.findByNameAndPassword(userName=cbo_userName.getText(), pwd=txt_password.getText());
							if (StrUtil.isEmpty(findByNameAndPassword)) {
								setErrorMessage("用户名或密码错误！");
								btn_login.setEnabled(true);
								return;
							}
							userName = findByNameAndPassword.getUserName();
							pwd = findByNameAndPassword.getPassword();
							type = findByNameAndPassword.getType();
							systemUserService.login(userName, pwd, StrUtil.getHostIp());
							if (!list.contains(userName)) {
								list.add(userName);
								CarparkFileUtils.writeObject(USER_NAMES, list);
							}
							testMap.put(userName, pwd, btn_savePassword.getSelection());
							CarparkFileUtils.writeObject("testMap", testMap);
							System.setProperty("userName", userName);
							System.setProperty("password", pwd);
							System.setProperty("userType", type);
							sp.stop();
							sp.start();
							SingleCarparkSystemSetting systemSetting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.访客车名称.name());
							if (systemSetting!=null) {
								System.setProperty(ConstUtil.VISITOR_NAME, systemSetting.getSettingValue());
							}else{
								System.setProperty(ConstUtil.VISITOR_NAME, SystemSettingTypeEnum.访客车名称.getDefaultValue());
							}
							SingleCarparkSystemSetting setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.同一账号只能在一个地方登录.name());
							if ((setting==null||setting.getBooleanValue())&&findByNameAndPassword.getSingleLogin()) {
								startCheckLoginStatusService();
							}
							startBackGroundServer();
						} catch (Exception e1) {
							e1.printStackTrace();
							lbl_errorMsg.setText(e1.getMessage());
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
								}else if (loginApp.equals("收费界面")) {
									if (!Boolean.valueOf(sp.getCarparkService().getSystemSettingValue(SystemSettingTypeEnum.启用集中收费))) {
										commonui.info("提示", "未开启集中收费");
										return;
									}
									file = new File(CONCENTRATE_TEMP);
									if (file.exists()) {
										commonui.error("错误", "已经打开了收费界面");
										return;
									}
									file.createNewFile();
									raf = new RandomAccessFile(file, "rw");
									channel = raf.getChannel();
									tryLock = channel.tryLock();

									app = concentrateApp;
								}
							}
							app.openAsyncExec();
							LOGGER.info("界面打开花费时间{}", System.nanoTime() - nanoTime);
						} catch (Exception e) {
							LOGGER.error("main is error" + "界面出错=======", e);
							// app.disponse();
							// app.setShell(new Shell());
							// app.open();
							// shell.setVisible(true);

						} finally {
							try {
								if (tryLock!=null) {
									tryLock.release();
								}
								if (raf!=null) {
									raf.close();
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
							shell.setVisible(true);
							txt_password.forceFocus();
							btn_login.setEnabled(true);
						}
					}
					
				});
			}
		}).start();
	}
	private void setErrorMessage(String string) {
		GridData gd_composite = (GridData) composite_msg.getLayoutData();
		gd_composite.exclude=false;
		shell.setSize(300,301);
		shell.layout();
		lbl_errorMsg.setText(string);
		
	}
	protected boolean check() {
		try {

			String upload = FileuploadSend.upload("http://" + CarparkClientConfig.getInstance().getServerIp() + ":8899/server/", null);
			String[] s = upload.split("/");

			CarparkClientConfig instance = CarparkClientConfig.getInstance();
			instance.setDbServerPort(s[1]);
			instance.setDbServerUsername(s[2]);
			instance.setDbServerPassword(s[3]);
			instance.setDbServerType(s[4]);
			instance.setDbServerIp(s[0]);
			CarparkFileUtils.writeObject(ClientConfigUI.CARPARK_CLIENT_CONFIG, instance);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			setErrorMessage("连接"+CarparkClientConfig.getInstance().getServerIp()+"失败,请检查服务器状态");
			return false;
		}

	}
	
	private void startBackGroundServer() {
		autoDeletePhoto();
		if (Boolean.valueOf(System.getProperty(CHECK_SOFT_DOG) == null ? "true" : "false")) {
			checkSoftDog();
		}
		synchronizedServerTime();
	}

	private void synchronizedServerTime() {
		injector.getInstance(ClientSynTimeServiceI.class).startAsync();
	}

	// 检测加密狗
	private void checkSoftDog() {
		injector.getInstance(ClientCheckSoftDogServiceI.class).startAsync();
	}

	// 自动删除图片
	private void autoDeletePhoto() {
		injector.getInstance(DeleteImageServiceI.class).startAsync();
	}
	
	private void startCheckLoginStatusService(){
		injector.getInstance(LoginCheckServiceI.class).startAsync();
	}
}
