package com.donglu.carpark.server.imgserver;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
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
import com.donglu.carpark.service.WebService;
import com.donglu.carpark.ui.Login;
import com.donglu.carpark.ui.wizard.sn.ImportSNModel;
import com.donglu.carpark.ui.wizard.sn.ImportSNWizard;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.SystemUpdate;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIGuiceModule;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.server.ServerUtil;
import com.dongluhitec.card.ui.util.FileUtils;
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
import com.google.inject.name.Names;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;

public class ImageServerUI {

	private static Logger LOGGER = LoggerFactory.getLogger(Login.class);
	public static final String YYYY_MM_DD = "yyyy-MM-dd";


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
	@Inject
	private WebService webService;

	private TrayItem trayItem;

	private AppVerifier av;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
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
	/**
	 * 启动服务
	 */
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
//			File f = new File(System.getProperty("user.dir"));
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

		    server.setHandler(servletHandler);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		autoDeleteSameInOutHistory();
		if (System.getProperty("autoSendInfoToCloud")!=null&&System.getProperty("autoSendInfoToCloud").equals("true")) {
			autoSendInfoToCloud();
		}
		autoUpdateIpToYunServer();
		
	}
	private void autoUpdateIpToYunServer() {
		String ip="";
		String yunServerAddress = System.getProperty("yunServerAddress");
		String resolveAddress=System.getProperty("resolveAddress");
		LOGGER.info("云服务器地址{},需要解析的地址为：{}",yunServerAddress,resolveAddress);
		if (StrUtil.isEmpty(yunServerAddress)||StrUtil.isEmpty(resolveAddress)) {
			return;
		}
		ScheduledExecutorService deleteExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("没隔10秒解析本地ip"));
		deleteExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					InetAddress server=InetAddress.getByName(resolveAddress);
					String hostIp = server.getHostAddress();
					LOGGER.info("解析后的ip为{}",hostIp);
					if (!StrUtil.isEmpty(ip)&&ip.equals(hostIp)) {
						return;
					}
					String actionUrl=yunServerAddress+"?method=updateIp&updateIp="+Base64.getEncoder().encodeToString(hostIp.getBytes("utf-8"));
					LOGGER.info("准备发送ip{}到{}",hostIp,actionUrl);
					String upload = FileuploadSend.upload(actionUrl, null);
					LOGGER.info("发送结果为{}",upload);
				} catch (Exception e) {
					LOGGER.info("解析域名失败",e);
				}
			}
		}, 10, 10, TimeUnit.SECONDS);
		
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
		int uploadTime = 10;
		String ot = System.getenv("oploadTime");
		if (ot!=null) {
			Integer valueOf = Integer.valueOf(ot);
			uploadTime=valueOf;
		}
		ScheduledExecutorService userExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("上传用户数据到云平台"));
		ScheduledExecutorService inExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("上传进场数据到云平台"));
		ScheduledExecutorService outExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("上传出场数据到云平台"));
		ScheduledExecutorService infoExecutor = Executors.newSingleThreadScheduledExecutor(ThreadUtil.createThreadFactory("上传停车场数据到云平台"));
		userExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("准备上传固定用户记录到云平台");
				Long id=(Long) FileUtils.readObject("userLastUploadId");
				id=id==null?0L:id;
				List<Long> errorIds=(List<Long>) FileUtils.readObject("userErrorUploadId");
				if (StrUtil.isEmpty(errorIds)) {
					errorIds=new ArrayList<>();
				}
				LOGGER.info("上次上传固定用户记录到{},上传失败的为：{}",id,errorIds);
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
					LOGGER.info("上传用户{}的记录结果为{}",singleCarparkUser,sendUser);
				}
				FileUtils.writeObject("userLastUploadId",id);
				FileUtils.writeObject("userErrorUploadId",errorIds);
			}
		}, uploadTime, uploadTime, TimeUnit.SECONDS);
		inExecutor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				LOGGER.info("准备上传进场记录到云平台");
				Long id=(Long) FileUtils.readObject("inLastUploadId");
				id=id==null?0L:id;
				List<Long> errorIds=(List<Long>) FileUtils.readObject("inErrorUploadId");
				if (StrUtil.isEmpty(errorIds)) {
					errorIds=new ArrayList<>();
				}
				LOGGER.info("上次上传进场记录到{},上传失败的为：{}",id,errorIds);
				List<SingleCarparkInOutHistory> list=sp.getCarparkInOutService().findInHistoryThanIdMore(id,errorIds);
				LOGGER.info("还有{}条进场记录等待上传",list.size());
				for (SingleCarparkInOutHistory in : list) {
					LOGGER.info("正在上传车牌{}的进场记录",in.getPlateNo());
					boolean sendInHistory = webService.sendInHistory(in);
					if (!sendInHistory) {
						errorIds.add(in.getId());
					}else{
						errorIds.remove(in.getId());
					if (in.getId()>id) {
						id=in.getId();
					}
					}
					LOGGER.info("上传车牌{}的进场记录结果为{}",sendInHistory);
				}
				FileUtils.writeObject("inLastUploadId",id);
				FileUtils.writeObject("inErrorUploadId",errorIds);
			}
		}, uploadTime, uploadTime, TimeUnit.SECONDS);
		
		outExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				LOGGER.info("准备上传出场记录到云平台");
				Long id=(Long) FileUtils.readObject("outLastUploadId");
				id=id==null?0L:id;
				List<Long> errorIds=(List<Long>) FileUtils.readObject("outErrorUploadId");
				if (StrUtil.isEmpty(errorIds)) {
					errorIds=new ArrayList<>();
				}
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
				}
				FileUtils.writeObject("outLastUploadId",id);
				FileUtils.writeObject("outErrorUploadId",errorIds);
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
					e.printStackTrace();
				}
			}
		}, uploadTime, uploadTime, TimeUnit.SECONDS);

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

}
