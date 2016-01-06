package com.donglu.carpark.server;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkLocalVMServiceProvider;
import com.donglu.carpark.util.CarparkDataBaseUtil;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIFacility.Progress;
import com.dongluhitec.card.ui.util.ProcessBarMonitor;
import com.dongluhitec.card.util.DatabaseUtil;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;


import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;

public class ServerUI {

	public static final String DEFAULT_DATABASE = "carpark";
    public static final String ORIGINAL_DATABASE = "master";
    public static final String DEFAULT_DATABASE_TYPE = "SQLSERVER 2008";
    
	protected Shell shell;
	private Text text_ip;
	private Text text_port;
	private Text txt_name;
	private Text txt_pwd;
	
	@Inject
	private CommonUIFacility commonui;
	private Button btn_checkDataBase;
	private Combo combo_dbServerType;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerUI window = new ServerUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		shell.setSize(450, 300);
		shell.setText("数据库配置");
		shell.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("数据库");
		
		ComboViewer comboViewer = new ComboViewer(composite, SWT.NONE);
		combo_dbServerType = comboViewer.getCombo();
		combo_dbServerType.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_dbServerType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[]{"SQLSERVER2008","SQLSERVER2005","MYSQL"});
		switch (CarparkServerConfig.getInstance().getDbServerType()) {
		case "SQLSERVER2008":
			combo_dbServerType.select(0);
			break;
		case "SQLSERVER2005":
			combo_dbServerType.select(1);
			break;
		case "MYSQL":
			combo_dbServerType.select(2);
			break;
		default:
			combo_dbServerType.select(0);
			break;
		}
		
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
		text_ip.setText(CarparkServerConfig.getInstance().getDbServerIp());
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("端口");
		
		text_port = new Text(composite, SWT.BORDER);
		text_port.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_port.setText("1433");
		text_port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_port.setText(CarparkServerConfig.getInstance().getDbServerPort());
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("用户名");
		
		txt_name = new Text(composite, SWT.BORDER);
		txt_name.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_name.setText("sa");
		txt_name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txt_name.setText(CarparkServerConfig.getInstance().getDbServerUsername());
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("密码");
		
		txt_pwd = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txt_pwd.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_pwd.setText("1");
		txt_pwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txt_pwd.setText(CarparkServerConfig.getInstance().getDbServerPassword());
		new Label(composite, SWT.NONE);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		btn_checkDataBase = new Button(composite_1, SWT.NONE);
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
//		CarparkServerConfig instance = CarparkServerConfig.getInstance();
//		DongluServerInit init=new DongluServerInit();
//		boolean checkDatabaseExist = init.checkDatabaseExist();
//		if (checkDatabaseExist) {
//			commonui.info("提示", "数据库创建成功！");
//		}
		String dbServer_Type = combo_dbServerType.getText();
		String dbServer_ip = text_ip.getText().trim();
        String dbServer_port = text_port.getText().trim();
        String dbServer_username = txt_name.getText().trim();
        String dbServer_password = txt_pwd.getText().trim();
        writeToConfig();
        boolean checkLink = CarparkDataBaseUtil.checkPortAvailable(dbServer_ip, dbServer_port);
        if (!checkLink){
            commonui.error("错误", "测试数据库端口失败，请检查端口"+dbServer_port+"是否己打开或防火墙限制等因素");
            return;
        }

        //如果原始的数据库都连接不成功，则用户名或密码可能不确
        boolean databaseAvailable = CarparkDataBaseUtil.checkoutDatabaseAvailable(dbServer_ip, dbServer_port, ORIGINAL_DATABASE, dbServer_username, dbServer_password, dbServer_Type);
        if(!databaseAvailable){
            commonui.error("错误", "连接主数据库失败，请检查是用户名或密码是否正确");
            return;
        }

        //检查carpark数据库是否可用
        boolean default_database_available = CarparkDataBaseUtil.checkoutDatabaseAvailable(dbServer_ip, dbServer_port, DEFAULT_DATABASE, dbServer_username, dbServer_password, dbServer_Type);
        if(default_database_available){
            commonui.info("提示", "数据库连接信息测试成功！");
            return;
        }

        boolean confirm = commonui.confirm("确认", "检查到默认的carpark数据库不存在,是否立即创建数据库?");
        if(!confirm){
            return;
        }
        writeToConfig();
        String databaseFolder = Paths.get(System.getProperty("user.dir")).getParent().getParent().toString() + File.separator + "database" + File.separator;

        String defaultCreateDatabaseSql = getDefaultCreateDatabaseSql(dbServer_Type, databaseFolder);
        Progress showProgressBar = commonui.showProgressBar("初始化数据库", 0, 2);
        ProcessBarMonitor monitor = showProgressBar.getMonitor();
        
        new Runnable() {
			public void run() {
				try {
					Path path = Paths.get(databaseFolder);
					if(!Files.exists(path)){
					    Files.createDirectory(path);
					    System.out.println("创建数据库目录成功："+defaultCreateDatabaseSql);
					    monitor.showMessage("创建数据库目录成功");
					}
					monitor.showMessage("准备创建基础的数据库文件");
					boolean b = CarparkDataBaseUtil.executeSQL(dbServer_ip, dbServer_port, ORIGINAL_DATABASE, dbServer_username, dbServer_password, defaultCreateDatabaseSql, dbServer_Type);
					if(!b){
					    throw new DongluServiceException("创建基础的数据库文件失败");
					}
					monitor.showMessage("创建基础的数据库文件成功");
					monitor.dowork(1);
					boolean createTableSuccess = createTable();
					if(!createTableSuccess){
					    throw new DongluServiceException("初始化数据库数据时发生严重错误，请检查软件初始化脚本是否完整");
					}
					monitor.finish();
					commonui.info("提示", "数据创建成功！系统将数据库文件默认保存在该目录下：\n" + databaseFolder);
				} catch (IOException e) {
					monitor.showMessage(e.getMessage());
					commonui.error("错误", "数据库创建失败，请检查在默认路径下是否己经存在未关联的数据库文件："+ databaseFolder);
					
				}
			}
		}.run();
	}

	private void writeToConfig() {
		CarparkServerConfig instance = CarparkServerConfig.getInstance();
		instance.setDbServerIp(text_ip.getText());
		instance.setDbServerPort(text_port.getText());
		instance.setDbServerUsername(txt_name.getText());
		instance.setDbServerPassword(txt_pwd.getText());
		instance.setDbServerType(combo_dbServerType.getText());
	}
	public boolean createTable() {
        try {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    this.bindConstant().annotatedWith(Names.named("HBM2DDL")).to("create");
                    bind(CarparkServerConfig.class).toInstance(CarparkServerConfig.getInstance());
                    bind(CarparkDatabaseServiceProvider.class).to(CarparkLocalVMServiceProvider.class);
                }
            });
            CarparkDatabaseServiceProvider serviceProvider = injector.getInstance(CarparkDatabaseServiceProvider.class);
            serviceProvider.start();
            serviceProvider.getSystemUserService().init();
            serviceProvider.stop();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
        	shell.forceFocus();
        }
    }
	public String getDefaultCreateDatabaseSql(String type,String databaseFolder){
        switch (type) {
            case "SQLSERVER2008":
                String mdfFilePath = databaseFolder + "carpark.mdf";
                String ldfFilePath = databaseFolder + "carpark.ldf";
                return "IF NOT EXISTS(SELECT * FROM sysDatabases WHERE name='carpark') CREATE DATABASE carpark ON PRIMARY (NAME= carpark_data, FILENAME='"+mdfFilePath+"', SIZE=10, FILEGROWTH= 10%) LOG ON (NAME=carpark_log, FILENAME='"+ldfFilePath+"', SIZE=10, FILEGROWTH= 10% )";
            case "MYSQL":
                return "CREATE DATABASE IF NOT EXISTS carpark CHARACTER SET 'utf8'";
            default:
                return null;
        }
    }
}
