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

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.DialogAction;
import org.controlsfx.dialog.Dialogs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkLocalVMServiceProvider;
import com.dongluhitec.card.blservice.DatabaseServiceProvider;
import com.dongluhitec.card.blservice.DongluServiceException;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.CommonUIFacility.Progress;
import com.dongluhitec.card.server.ui.DongluServerConfig;
import com.dongluhitec.card.server.ui.DongluServerInit;
import com.dongluhitec.card.server.util.DatabaseUtil;
import com.dongluhitec.card.service.DbServiceConfigurator;
import com.dongluhitec.card.service.impl.LocalVMServiceProvider;
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
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("地址");
		
		text_ip = new Text(composite, SWT.BORDER);
		text_ip.setText("127.0.0.1");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 103;
		text_ip.setLayoutData(gd_text);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("端口");
		
		text_port = new Text(composite, SWT.BORDER);
		text_port.setText("1433");
		text_port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("用户名");
		
		txt_name = new Text(composite, SWT.BORDER);
		txt_name.setText("sa");
		txt_name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("密码");
		
		txt_pwd = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		txt_pwd.setText("a123456");
		txt_pwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		btn_checkDataBase = new Button(composite_1, SWT.NONE);
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
		
		Button btn_checkTable = new Button(composite_1, SWT.NONE);
		btn_checkTable.setText("检查表");
		
		Button btn_init = new Button(composite_1, SWT.NONE);
		btn_init.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		btn_init.setText("初始化数据库");

	}

	protected void check() {
//		CarparkServerConfig instance = CarparkServerConfig.getInstance();
//		DongluServerInit init=new DongluServerInit();
//		boolean checkDatabaseExist = init.checkDatabaseExist();
//		if (checkDatabaseExist) {
//			commonui.info("提示", "数据库创建成功！");
//		}
		String dbServer_ip = text_ip.getText().trim();
        String dbServer_port = text_port.getText().trim();
        String dbServer_username = txt_name.getText().trim();
        String dbServer_password = txt_pwd.getText().trim();
        writeToConfig();
        boolean checkLink = DatabaseUtil.checkPortAvailable(dbServer_ip, dbServer_port);
        if (!checkLink){
            commonui.error("错误", "测试数据库端口失败，请检查端口"+dbServer_port+"是否己打开或防火墙限制等因素");
            return;
        }

        //如果原始的数据库都连接不成功，则用户名或密码可能不确
        boolean databaseAvailable = DatabaseUtil.checkoutDatabaseAvailable(dbServer_ip, dbServer_port, ORIGINAL_DATABASE, dbServer_username, dbServer_password, DEFAULT_DATABASE_TYPE);
        if(!databaseAvailable){
            commonui.error("错误", "连接主数据库失败，请检查是用户名或密码是否正确");
            return;
        }

        //检查carpark数据库是否可用
        boolean default_database_available = DatabaseUtil.checkoutDatabaseAvailable(dbServer_ip, dbServer_port, DEFAULT_DATABASE, dbServer_username, dbServer_password, DEFAULT_DATABASE_TYPE);
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

        String defaultCreateDatabaseSql = getDefaultCreateDatabaseSql(DEFAULT_DATABASE_TYPE, databaseFolder);
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

					boolean b = DatabaseUtil.executeSQL(dbServer_ip, dbServer_port, ORIGINAL_DATABASE, dbServer_username, dbServer_password, defaultCreateDatabaseSql, DEFAULT_DATABASE_TYPE);
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
//        Task<Boolean> task = new Task<Boolean>() {
//            @Override
//            protected Boolean call() throws Exception {
//                Path path = Paths.get(databaseFolder);
//                if(!Files.exists(path)){
//                    Files.createDirectory(path);
//                    System.out.println("创建数据库目录成功："+defaultCreateDatabaseSql);
//                }
//
//                boolean b = DatabaseUtil.executeSQL(dbServer_ip, dbServer_port, ORIGINAL_DATABASE, dbServer_username, dbServer_password, defaultCreateDatabaseSql, DEFAULT_DATABASE_TYPE);
//                if(!b){
//                    throw new DongluServiceException("创建基础的数据库文件失败");
//                }
//                boolean createTableSuccess = createTable();
//                if(!createTableSuccess){
//                    throw new DongluServiceException("初始化数据库数据时发生严重错误，请检查软件初始化脚本是否完整");
//                }
//                return true;
//            }
//
//            @Override
//            protected void succeeded() {
//                commonui.info("提示", "数据创建成功！系统将数据库文件默认保存在该目录下：\n" + databaseFolder);
//                writeToConfig();
//            }
//
//            @Override
//            protected void failed() {
//                commonui.error("错误", "数据库创建失败，请检查在默认路径下是否己经存在未关联的数据库文件："+ databaseFolder);
//            }
//
//            @Override
//            protected void cancelled() {
//                commonui.error("错误", "数据库创建时被中断！");
//            }
//        };

//        commonui.error("重要提示", "检查到默认的carpark数据库不存在,正在创建数据，请暂时不执行其他操作，这可能会影响数据初始化进程");
//        commonui.showProgressBar("创建提示", 1, 2);
//        System.out.println(task);
//        new Thread(task).start();
	}

	private void writeToConfig() {
		CarparkServerConfig instance = CarparkServerConfig.getInstance();
		instance.setDbServerIp(text_ip.getText());
		instance.setDbServerPort(text_port.getText());
		instance.setDbServerUsername(txt_name.getText());
		instance.setDbServerPassword(txt_pwd.getText());
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
            serviceProvider.stop();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	public String getDefaultCreateDatabaseSql(String type,String databaseFolder){
        switch (type) {
            case "SQLSERVER 2008":
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
