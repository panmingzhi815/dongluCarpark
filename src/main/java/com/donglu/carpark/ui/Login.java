package com.donglu.carpark.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkLocalVMServiceProvider;
import com.donglu.carpark.ui.common.App;
import com.dongluhitec.card.blservice.DatabaseServiceProvider;
import com.dongluhitec.card.blservice.HardwareFacility;
import com.dongluhitec.card.common.ui.CommonUIGuiceModule;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

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
	private static Logger LOGGER = LoggerFactory.getLogger(Login.class);
	
	protected Shell shell;
	private Text txt_userName;
	private Text txt_password;
	private Label lbl_msg;
	private Combo combo;
	
	private final CarparkManageApp carparkManageApp;
	private final CarparkMainApp carparkMainApp;
	private final ServerUI serverUI;
	private final CarparkDatabaseServiceProvider sp;
	private final String selectType="LoginSelectType";
	private App app;
	@Inject
	private ClientConfigUI clientConfigUI;
	
	@Inject
	public Login(CarparkManageApp carparkManageApp,CarparkMainApp carparkMainApp,ServerUI serverUI,CarparkDatabaseServiceProvider sp) {
		this.carparkManageApp = carparkManageApp;
		this.carparkMainApp = carparkMainApp;
		this.serverUI = serverUI;
		this.sp = sp;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		Display display = Display.getDefault();
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
                            this.bind(CarparkDatabaseServiceProvider.class).to(CarparkLocalVMServiceProvider.class).in(Singleton.class);
                            this.bind(CarparkServerConfig.class).toInstance(CarparkServerConfig.getInstance());
//                            this.bind(LocalSecurityManager.class).to(SecurityManagerImpl.class);
                            
                        }
                    }, new CarparkHardwareGuiceModule(),new CommonUIGuiceModule());
					
					Login window = createInjector.getInstance(Login.class);
					window.open();
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("main is error");
					System.exit(0);
				}
			}
		});
	}

	/**
	 * Open the window.
	 */
	public void open() {
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
		shell.setSize(300, 280);
		shell.setText("用户登录");
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 15;
		shell.setLayout(gridLayout);

		Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));

		lbl_msg = new Label(composite_2, SWT.NONE);
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

		txt_userName = new Text(composite, SWT.BORDER);
		txt_userName.setText("admin");
		txt_userName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_txt_userName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txt_userName.widthHint = 150;
		txt_userName.setLayoutData(gd_txt_userName);

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("密    码");

		txt_password = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txt_password.setText("admin");
		txt_password.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode==13) {
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
				int i=combo.getSelectionIndex();
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
		}else{
			combo.select((int) readObject);
		}
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));

		Button button = new Button(composite_1, SWT.NONE);
		button.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
					login();
				}
		});
		button.setText("登录");

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
		String userName = null ;
		String pwd = null ;
		String type = null;
		try {
			sp.start();
			SingleCarparkSystemUser findByNameAndPassword = sp.getSystemUserService().findByNameAndPassword(txt_userName.getText(), txt_password.getText());
			if (StrUtil.isEmpty(findByNameAndPassword)) {
				lbl_msg.setText("用户名或密码错误");
				return;
			}
			userName=findByNameAndPassword.getUserName();
			pwd=findByNameAndPassword.getPassword();
			type=findByNameAndPassword.getType();
			System.setProperty("userName",userName );
			System.setProperty("password",pwd );
			System.setProperty("userType", type);
		} catch (Exception e1) {
			e1.printStackTrace();
			lbl_msg.setText(e1.getMessage());
			return;
		}
		try {
			String loginApp=combo.getText();
			if (type.equals("操作员")) {
				shell.setVisible(false);
				app=carparkMainApp;
				app.open();
			}
			else{
				shell.setVisible(false);
				if (loginApp.equals("监控界面")) {
					app=carparkMainApp;
					app.open();
				}else if(loginApp.equals("管理界面")){
					app=carparkManageApp;
					app.open();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("main is error"+"界面出错======="+e.getMessage());
//			app.disponse();
//			app.setShell(new Shell());
//			app.open();
//			shell.setVisible(true);
			System.exit(0);
		}
	}
}
