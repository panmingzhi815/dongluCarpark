package com.donglu.carpark.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.DialogAction;
import org.controlsfx.dialog.Dialogs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkLocalVMServiceProvider;
import com.dongluhitec.card.blservice.DatabaseServiceProvider;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIFacility.Progress;
import com.dongluhitec.card.server.util.DatabaseUtil;
import com.dongluhitec.card.service.DbServiceConfigurator;
import com.dongluhitec.card.service.impl.LocalVMServiceProvider;
import com.dongluhitec.card.ui.util.FileUtils;
import com.dongluhitec.card.ui.util.ProcessBarMonitor;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import javafx.concurrent.Task;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

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

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		 CarparkServerConfig config=(CarparkServerConfig) FileUtils.readObject(ClientConfigUI.CARPARK_CLIENT_CONFIG);
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
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 88);
		shell.setText("数据库配置");
		shell.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("地址");
		
		text_ip = new Text(composite, SWT.BORDER);
		text_ip.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_ip.setText("127.0.0.1");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 176;
		text_ip.setLayoutData(gd_text);
		text_ip.setText(CarparkClientConfig.getInstance().getDbServerIp());
		btn_checkDataBase = new Button(composite, SWT.NONE);
		btn_checkDataBase.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btn_checkDataBase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					check();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btn_checkDataBase.setText("测试");

	}

	protected void check() {
		try {
			String dbServer_ip = text_ip.getText().trim();
			String upload = FileuploadSend.upload("http://"+dbServer_ip+":8899/server/", null);
			String[] s = upload.split("/");
			
			CarparkServerConfig instance = CarparkServerConfig.getInstance();
			instance.setDbServerIp(s[0]);
			instance.setDbServerPort(s[1]);
			instance.setDbServerUsername(s[2]);
			instance.setDbServerPassword(s[3]);
			System.out.println(instance);
			FileUtils.writeObject(CARPARK_CLIENT_CONFIG, instance);
			writeToConfig();
			commonui.info("连接成功", "连接成功");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("连接失败", "连接失败");
		}
        
	}

	private void writeToConfig() {
		CarparkClientConfig instance = CarparkClientConfig.getInstance();
		instance.setDbServerIp(text_ip.getText());
		
	}
}
