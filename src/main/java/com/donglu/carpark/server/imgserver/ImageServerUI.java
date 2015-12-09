package com.donglu.carpark.server.imgserver;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.server.ServerUI;
import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.server.servlet.ServerServlet;
import com.donglu.carpark.server.servlet.StoreServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkLocalVMServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.ui.Login;
import com.donglu.carpark.ui.wizard.sn.ImportSNModel;
import com.donglu.carpark.ui.wizard.sn.ImportSNWizard;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.SystemUpdate;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIGuiceModule;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.exception.DongluAppException;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.server.ServerUtil;
import com.dongluhitec.card.ui.util.FileUtils;
import com.dongluhitec.core.crypto.appauth.AppAuthorization;
import com.dongluhitec.core.crypto.appauth.AppVerifier;
import com.dongluhitec.core.crypto.appauth.AppVerifierImpl;
import com.dongluhitec.core.crypto.softdog.SoftDogWin;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.wb.swt.SWTResourceManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;

public class ImageServerUI {

	private static Logger LOGGER = LoggerFactory.getLogger(Login.class);
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	private DataBindingContext m_bindingContext;

	public static final String IMAGE_SAVE_DIRECTORY = "directory";
	protected Shell shell;
	private Text text;

	private Server server;
	@Inject
	private ServerUI ui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;

	private String filePath = "";
	@Inject
	private Provider<ImageUploadServlet> imageServletProvider;
	@Inject
	private Provider<StoreServlet> storeServletProvider;
	@Inject
	private Provider<ServerServlet> serverServletProvider;

	private TrayItem trayItem;

	private AppVerifier av;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
//		try {
//			Server s=new Server(8888);
//			WebAppContext context = new WebAppContext(); 
//			context.setContextPath("/store");  
//			context.setDescriptor("store/WEB-INF/web.xml");  
//			context.setResourceBase("store");  
//			context.setParentLoaderPriority(true);
//			s.setHandler(context);
//			s.start();
//			System.out.println("started");
//		} catch (Exception e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
		
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Injector createInjector = Guice.createInjector(new CommonUIGuiceModule(), new AbstractModule() {
						@Override
						protected void configure() {
							this.bindConstant().annotatedWith(Names.named("HBM2DDL")).to("update");
							bind(CarparkServerConfig.class).toInstance(CarparkServerConfig.getInstance());
							bind(CarparkDatabaseServiceProvider.class).to(CarparkLocalVMServiceProvider.class);
						}
					});
					ImageServerUI window = createInjector.getInstance(ImageServerUI.class);
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		// init();
		createContents();
		m_bindingContext = initDataBindings();
		shell.open();
		shell.setImage(JFaceUtil.getImage("carpark_16"));

		Button btnTest = new Button(shell, SWT.NONE);
		btnTest.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ui.open();
				init();
				text.setText(filePath);
			}
		});
		btnTest.setText("配    置");

		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importSN();
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("注册码");
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		System.exit(0);
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
			e.printStackTrace();
		}
	}

	private void init() {
		try {
			sp.start();
			SingleCarparkSystemSetting s = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.图片保存位置.name());
			filePath = StrUtil.isEmpty(s) ? System.getProperty("user.dir") : s.getSettingValue();
			FileUtils.writeObject(IMAGE_SAVE_DIRECTORY, filePath);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(589, 86);
		shell.setText("服务器");
		shell.setLayout(new GridLayout(6, false));
		shell.addShellListener(new ShellAdapter() {

			@Override
			public void shellClosed(ShellEvent e) {
				shell.forceActive();
				MessageBox box = new MessageBox(shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION | SWT.APPLICATION_MODAL);

				box.setText("退出提示");
				box.setMessage("确认退出服务器？退出服务器后客户端的图片将不会在服务器端备份！");
				int open = box.open();
				if (open == SWT.YES) {
					trayItem.dispose();
					System.exit(0);
				} else {
					e.doit = false;
				}
			}

			@Override
			public void shellIconified(ShellEvent e) {
				shell.setVisible(false);
			}

		});
		Display default1 = Display.getDefault();
		Tray systemTray = default1.getSystemTray();
		trayItem = new TrayItem(systemTray, SWT.NONE);
		trayItem.setToolTipText("服务器");
		trayItem.setImage(JFaceUtil.getImage("carpark_16"));
		trayItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(true);
				shell.setFocus();
				text.setFocus();
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
		Object readObject = FileUtils.readObject(IMAGE_SAVE_DIRECTORY);
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
				FileUtils.writeObject(IMAGE_SAVE_DIRECTORY, open);
				text.setText(open);
			}
		});
		button.setText("...");

		Button btnStart = new Button(shell, SWT.NONE);
		btnStart.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnStart.setData("type", "start");
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String data = (String) btnStart.getData("type");
				if (data.equals("start")) {
					startServer();
					btnStart.setText("退出");
					btnStart.setData("type", "stop");
				}
				if (data.equals("stop")) {
					System.exit(0);
				}
			}
		});
		btnStart.setText("启    动");

	}

	protected void startServer() {
		try {
			sp.start();
			SingleCarparkSystemSetting findSystemSettingByKey = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.DateBase_version.name());
			if (StrUtil.isEmpty(findSystemSettingByKey)) {
				findSystemSettingByKey = new SingleCarparkSystemSetting();
				findSystemSettingByKey.setSettingKey(SystemSettingTypeEnum.DateBase_version.name());
				findSystemSettingByKey.setSettingValue(SystemSettingTypeEnum.DateBase_version.getDefaultValue());
				sp.getCarparkService().saveSystemSetting(findSystemSettingByKey);
			}
			File f = new File(System.getProperty("user.dir"));
			// System.out.println(f.getPath());
			// String[] list = f.list();
			// for (String string : list) {
			// boolean matches = string.matches("^[0-9]{8}.txt$");
			// if (matches) {
			// System.out.println("========="+matches);
			//// CarparkUtils.loadIniFromFile(new File("../升级.ini"));
			// }
			// }
			if (!findSystemSettingByKey.getSettingValue().equals(SystemSettingTypeEnum.软件版本.getDefaultValue())) {
				SystemUpdate update = new SystemUpdate();
				try {
					update.systemUpdate(findSystemSettingByKey.getSettingValue(), SystemSettingTypeEnum.软件版本.getDefaultValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String open = text.getText();
			FileUtils.writeObject(IMAGE_SAVE_DIRECTORY, open);
			SingleCarparkSystemSetting s = new SingleCarparkSystemSetting();
			s.setSettingKey(SystemSettingTypeEnum.图片保存位置.name());
			s.setSettingValue(open);
			sp.getCarparkService().saveSystemSetting(s);
			if (Boolean.valueOf(System.getProperty(Login.CHECK_SOFT_DOG) == null ? "true" : "false")) {
				autoCheckSoftDog();
			}
			// sp.stop();
			this.server = new Server(8899);
			ServletHandler servletHandler = new ServletHandler();
			ServerUtil.startServlet("/carparkImage/*", servletHandler, imageServletProvider);

			ServerUtil.startServlet("/server/*", servletHandler, serverServletProvider);
			ServerUtil.startServlet("/store/*", servletHandler, storeServletProvider);

			// startWeb();
//			ContextHandlerCollection contexts = new ContextHandlerCollection();
//			ServletContextHandler hand = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		    hand.setContextPath("/store");
//		    hand.addServlet(new ServletHolder(storeServletProvider.get()),"/*");
//		    SessionManager sm = new HashSessionManager();
//	        hand.setSessionHandler(new SessionHandler(sm));
//
//		    contexts.setHandlers(new Handler[] { servletHandler, hand });
			
		    server.setHandler(servletHandler);
			server.start();
//			startWeb();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startWeb() {
		try {
			String cmdline ="cmd.exe /c "+ System.getProperty("user.dir")+"\\tomcat\\bin\\startup.bat";
			LOGGER.info("准备执行文件{}",cmdline);
			CmdExec(cmdline);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("启动tomcat时发生错误");
		}

	}
	
	public void CmdExec(String cmdline) {
		try {
			String line;
			Process p = Runtime.getRuntime().exec(cmdline);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
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
					try {
						LOGGER.info("解析从数据库获取注册码信息");
						av = new AppVerifierImpl(new SoftDogWin());
						AppAuthorization decrypt = av.decrypt(sn.getSettingValue());
						dateOfExpire = decrypt.getDateOfExpire();
						// dateOfExpire=new DateTime(2015,12,8,1,1).toDate();
						if (StrUtil.getTodayBottomTime(dateOfExpire).before(new Date())) {
							LOGGER.info("解析从数据库获取注册码信息成功,已过期{}", dateOfExpire);
							dateOfExpire = null;
						}
						LOGGER.info("解析从数据库获取注册码信息成功");
					} catch (Exception e) {
						LOGGER.info("解析从数据库获取注册码信息失败");
					}
					SingleCarparkSystemSetting vilidTo = new SingleCarparkSystemSetting();
					vilidTo.setSettingKey(SNSettingType.validTo.name());
					vilidTo.setSettingValue(StrUtil.formatDate(dateOfExpire, YYYY_MM_DD));
					sp.getCarparkService().saveSystemSetting(vilidTo);
					LOGGER.info("把解析到的信息保存到数据库");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 30, 60 * 30, TimeUnit.SECONDS);

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}
}
