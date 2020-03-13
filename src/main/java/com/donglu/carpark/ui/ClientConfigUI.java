package com.donglu.carpark.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.ui.keybord.SWTKeySettingApp;
import com.donglu.carpark.util.CarparkFileUtils;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class ClientConfigUI {

	public static final String CARPARK_CLIENT_CONFIG = "CarparkClientConfig";
	public static final String DEFAULT_DATABASE = "carpark";
	public static final String ORIGINAL_DATABASE = "master";
	public static final String DEFAULT_DATABASE_TYPE = "SQLSERVER 2008";

	protected Shell shell;
	private Text text_ip;

	@Inject
	private CommonUIFacility commonui;
	private Button btn_checkDataBase;
	private Button button;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CarparkServerConfig config = (CarparkServerConfig) CarparkFileUtils.readObject(ClientConfigUI.CARPARK_CLIENT_CONFIG);
		System.out.println(config);
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
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
	 * 
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(SWT.CLOSE|SWT.ON_TOP);
		shell.setSize(408, 96);
		shell.setText("数据库配置");
		shell.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));

		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("地址");

		text_ip = new Text(composite, SWT.BORDER);
		text_ip.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 13 || e.keyCode == StrUtil.SMAIL_KEY_ENTER) {
					check();
				}
			}
		});
		text_ip.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_ip.setText("127.0.0.1");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 176;
		text_ip.setLayoutData(gd_text);
		text_ip.setText(CarparkClientConfig.getInstance().getServerIp());
		btn_checkDataBase = new Button(composite, SWT.NONE);
		btn_checkDataBase.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btn_checkDataBase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				check();
			}
		});
		btn_checkDataBase.setText("测试");
		btn_checkDataBase.setImage(JFaceUtil.getImage("add_small"));
		
		button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SWTKeySettingApp app = new SWTKeySettingApp();
				app.open();
			}
		});
		button.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button.setText("按键设置");

	}

	protected void check() {
		try {
			String dbServer_ip = text_ip.getText().trim();
			String upload = FileuploadSend.upload("http://" + dbServer_ip + ":8899/server/", null);
			System.out.println(upload);
			CarparkClientConfig instance = CarparkClientConfig.getInstance();
			instance.setServerIp(dbServer_ip);
			commonui.info("连接成功", "连接成功");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("连接失败", "连接失败");
		}

	}
}
