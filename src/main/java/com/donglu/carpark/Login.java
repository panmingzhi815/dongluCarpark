package com.donglu.carpark;

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
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.main.DongluUIAppConfigurator;
import com.dongluhitec.card.ui.main.javafx.DongluJavaFXModule;
import com.dongluhitec.card.ui.util.WidgetUtil;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class Login {

	protected Shell shell;
	private Text txtAdmin;
	private Text txtAdmin_1;
	private Label lblNewLabel_msg;
	private Combo combo;
	@Inject
	private CarparkManageApp carparkManageApp;
	@Inject
	private CarparkMainApp carparkMainApp;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DongluUIAppConfigurator configurator = new DongluUIAppConfigurator();
			new JCommander(configurator, args);
			Injector createInjector = Guice.createInjector(new DongluJavaFXModule());

			Login window = createInjector.getInstance(Login.class);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		WidgetUtil.center(shell);
		shell.open();
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
		shell.setSize(293, 226);
		shell.setText("用户登录");
		shell.setLayout(new GridLayout(1, false));

		Composite composite_2 = new Composite(shell, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_composite_2 = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		gd_composite_2.heightHint = 29;
		gd_composite_2.widthHint = 191;
		composite_2.setLayoutData(gd_composite_2);

		lblNewLabel_msg = new Label(composite_2, SWT.NONE);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1);
		gd_composite.heightHint = 101;
		gd_composite.widthHint = 188;
		composite.setLayoutData(gd_composite);

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("用户名");

		txtAdmin = new Text(composite, SWT.BORDER);
		txtAdmin.setText("admin");
		txtAdmin.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txtAdmin.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("密    码");

		txtAdmin_1 = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txtAdmin_1.setText("admin");
		txtAdmin_1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode==13) {
					login();
				}
			}
		});
		txtAdmin_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txtAdmin_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(composite, SWT.NONE);

		ComboViewer comboViewer = new ComboViewer(composite, SWT.NONE);
		combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[] { "监控界面", "管理界面" });
		combo.select(0);
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		GridData gd_composite_1 = new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1);
		gd_composite_1.widthHint = 190;
		composite_1.setLayoutData(gd_composite_1);

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

	}

	/**
	 * 
	 */
	public void login() {
		String userName = txtAdmin.getText();
		String pwd = txtAdmin_1.getText();
		String type = combo.getText();
			if (!StrUtil.isEmpty(userName) && !StrUtil.isEmpty(pwd)) {
				if (userName.equals("admin")) {
					System.setProperty("userType", "admin");
					if (type.equals("管理界面")) {
						carparkManageApp.commonui.info("提示", "管理员进入管理界面");
						shell.setVisible(false);
						carparkManageApp.open();
						System.out.println("exit");
					}else{
						carparkManageApp.commonui.info("提示", "管理员进入监控界面");
						shell.setVisible(false);
						carparkMainApp.open();
						System.out.println("exit");
					}
					
				} else {
					System.setProperty("userType", "noadmin");
					carparkManageApp.commonui.info("提示", "进入监控界面界面");
					shell.setVisible(false);
					carparkMainApp.open();
					System.out.println("exit");
				}
				

			} else {
				lblNewLabel_msg.setText("用户名或密码错误！");
				return;
			}
	}
}
