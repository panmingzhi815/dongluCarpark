package com.donglu.carpark.ui;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.beust.jcommander.JCommander;
import com.donglu.carpark.ui.common.AbstractApp;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.ui.main.DongluUIAppConfigurator;
import com.dongluhitec.card.ui.main.javafx.DongluJavaFXModule;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;



public class CarparkManageApp extends AbstractApp{
	protected Shell shell;
	@Inject
	private CarparkManagePresenter presenter;
	
	private Map<SystemSettingTypeEnum, String> mapSystemSetting=Maps.newHashMap();

	private Composite composite_returnAccount_search;
	
	Map<SingleCarparkModuleEnum, TabItem> mapModuleToItem=new HashMap<>();

	private TabFolder tabFolder;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			@Override
			public void run() {
				try {
					 DongluUIAppConfigurator configurator = new DongluUIAppConfigurator();
	         new JCommander(configurator, args);
					Injector createInjector = Guice.createInjector(new DongluJavaFXModule());
					CarparkManageApp window = createInjector.getInstance(CarparkManageApp.class);
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
	@Override
	public void open() {
		try {
			long nanoTime = System.nanoTime();
			Display display = Display.getDefault();
			shell = new Shell();
			shell.setSize(896, 621);
			init();
			createContents();
			shell.open();
			shell.setMaximized(true);
			shell.layout();
			System.out.println("界面创建==="+(System.nanoTime()-nanoTime));
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
	}
	
	@Override
	public void openAsyncExec() {

		try {
			long nanoTime = System.nanoTime();
			Display display = Display.getDefault();
			shell = new Shell();
			shell.setSize(896, 621);
			shell.setEnabled(false);
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					init();
					createContents();
					shell.layout();
					shell.setEnabled(true);
				}
			});
			
			shell.open();
			shell.setMaximized(true);
			shell.layout();
			System.out.println("界面创建==="+(System.nanoTime()-nanoTime));
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
	
	}
	

	private void init() {
		presenter.setView(this);
		
		for (SystemSettingTypeEnum t : SystemSettingTypeEnum.values()) {
			mapSystemSetting.put(t, t.getDefaultValue());
		}
		presenter.init();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		String dbServerIp = CarparkClientConfig.getInstance().getDbServerIp();
		shell.setText("停车场管理界面("+dbServerIp+")");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		shell.setImage(JFaceUtil.getImage("carpark_16"));
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				presenter.systemExit();
			}
		});
		
		tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		TabItem tabItem_9 = new TabItem(tabFolder, SWT.NONE);
		tabItem_9.setText("停车场管理");
		
		Composite composite_carpark = new Composite(tabFolder, SWT.NONE);
		tabItem_9.setControl(composite_carpark);
		presenter.getCarparkPresenter().go(composite_carpark);
		composite_carpark.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("固定车设置");
		mapModuleToItem.put(SingleCarparkModuleEnum.固定车, tabItem);
		
		Composite composite_user = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_user);
		composite_user.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getUserPresenter().go(composite_user);
		TabItem tbtmNewItem_2 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_2.setText("记录查询");
		
		Composite composite_5 = new Composite(tabFolder, SWT.NONE);
		composite_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		tbtmNewItem_2.setControl(composite_5);
		composite_5.setLayout(new GridLayout(1, false));
		
		TabFolder tabFolder_searchHistory = new TabFolder(composite_5, SWT.BOTTOM);
		tabFolder_searchHistory.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		tabFolder_searchHistory.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmNewItem_3 = new TabItem(tabFolder_searchHistory, SWT.NONE);
		tbtmNewItem_3.setText("进出记录查询");
		
		Composite composite_inOutHostory_search = new Composite(tabFolder_searchHistory, SWT.NONE);
		tbtmNewItem_3.setControl(composite_inOutHostory_search);
		presenter.getInOutHostoryPresenter().go(composite_inOutHostory_search);
		composite_inOutHostory_search.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabItem tabItem_7 = new TabItem(tabFolder_searchHistory, SWT.NONE);
		tabItem_7.setText("充值记录查询");
		
		Composite composite_18 =new Composite(tabFolder_searchHistory, SWT.NONE);
		presenter.getCarparkPayHistoryPresenter().go(composite_18);
		tabItem_7.setControl(composite_18);
		composite_18.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabItem tabItem_2 = new TabItem(tabFolder_searchHistory, SWT.NONE);
		tabItem_2.setText("归账记录查询");
		
		composite_returnAccount_search = new Composite(tabFolder_searchHistory, SWT.NONE);
		tabItem_2.setControl(composite_returnAccount_search);
		presenter.getReturnAccountPresenter().go(composite_returnAccount_search);
		composite_returnAccount_search.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabItem tabItem_1 = new TabItem(tabFolder_searchHistory, SWT.NONE);
		tabItem_1.setText("操作员日志");
		
		Composite composite_2 = new Composite(tabFolder_searchHistory, SWT.NONE);
		tabItem_1.setControl(composite_2);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getSystemLogPresenter().go(composite_2);
		
		TabItem tbtmNewItem_4 = new TabItem(tabFolder_searchHistory, SWT.NONE);
		tbtmNewItem_4.setText("手动抬杆记录");
		
		Composite composite_17 = new Composite(tabFolder_searchHistory, SWT.NONE);
		tbtmNewItem_4.setControl(composite_17);
		composite_17.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getOpenDoorLogPresenter().go(composite_17);
		
		TabItem tabItem_6 = new TabItem(tabFolder_searchHistory, SWT.NONE);
		tabItem_6.setText("商铺充值记录");
		
		Composite composite_22 = new Composite(tabFolder_searchHistory, SWT.NONE);
		tabItem_6.setControl(composite_22);
		presenter.getStoreChargePresenter().go(composite_22);
		composite_22.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabItem tbtmS = new TabItem(tabFolder_searchHistory, SWT.NONE);
		tbtmS.setText("商铺免费记录");
		
		Composite composite_23 = new Composite(tabFolder_searchHistory, SWT.NONE);
		tbtmS.setControl(composite_23);
		presenter.getStoreFreePresenter().go(composite_23);
		composite_23.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabItem tabItem_3 = new TabItem(tabFolder_searchHistory, SWT.NONE);
		tabItem_3.setText("储值车消费记录");
		
		Composite composite_6 = new Composite(tabFolder_searchHistory, SWT.NONE);
		tabItem_3.setControl(composite_6);
		composite_6.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getPrepaidUserPayHistoryPresenter().go(composite_6);
		
		TabItem tabItem_10 = new TabItem(tabFolder, SWT.NONE);
		tabItem_10.setText("系统用户");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem_10.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getSystemUserListPresenter().go(composite);
		TabItem tabItem_5 = new TabItem(tabFolder, SWT.NONE);
		tabItem_5.setText("商铺优惠");
		
		Composite composite_20 = new Composite(tabFolder, SWT.NONE);
		tabItem_5.setControl(composite_20);
		composite_20.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getStorePresenter().go(composite_20);
		
		TabItem tabItem_11 = new TabItem(tabFolder, SWT.NONE);
		tabItem_11.setText("访客管理");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tabItem_11.setControl(composite_1);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getVisitorPresenter().go(composite_1);
		
		TabItem tabItem_8 = new TabItem(tabFolder, SWT.NONE);
		tabItem_8.setText("参数设置");
		
		Composite composite_25 = new Composite(tabFolder, SWT.NONE);
		tabItem_8.setControl(composite_25);
		composite_25.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getSettingPresenter().go(composite_25);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabItem[] selection = tabFolder.getSelection();
				if (selection.length<=0) {
					return;
				}
				if (selection[0].getText().equals("停车场管理")) {
					
				}
			}
		});

		
		
		TabItem tabItem_4 = new TabItem(tabFolder, SWT.NONE);
		tabItem_4.setText("关于");
		
		Composite composite_10 = new Composite(tabFolder, SWT.NONE);
		tabItem_4.setControl(composite_10);
		composite_10.setLayout(new GridLayout(1, false));
		Composite composite1 = new Composite(composite_10, SWT.BORDER);
		GridData gd_composite1 = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gd_composite1.widthHint = 375;
		composite1.setLayoutData(gd_composite1);
		composite1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_composite = new GridLayout(1, true);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		gl_composite.marginHeight = 0;
		composite1.setLayout(gl_composite);
		
		Composite composite_11 = new Composite(composite1, SWT.NONE);
		composite_11.setBackgroundImage(JFaceUtil.getImage("donglu"));
		composite_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_11.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite composite_21 = new Composite(composite1, SWT.NONE);
		composite_21.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.marginLeft = 30;
		gl_composite_2.marginTop = 10;
		gl_composite_2.verticalSpacing = 10;
		composite_21.setLayout(gl_composite_2);
		composite_21.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblNewLabel_11 = new Label(composite_21, SWT.NONE);
		lblNewLabel_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_11.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NORMAL));
		lblNewLabel_11.setText("软件名称：");
		
		Label lblNewLabel_2 = new Label(composite_21, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NORMAL));
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_2.setText("东陆一卡通管理平台");
		
		Label label1 = new Label(composite_21, SWT.NONE);
		label1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label1.setText("开发组织：");
		
		Label label_1 = new Label(composite_21, SWT.NONE);
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_1.setText("深圳市东陆高新实业有限公司");
		
		Label label_21 = new Label(composite_21, SWT.NONE);
		label_21.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_21.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_21.setText("软件版本：");
		
		Label lblNewLabel_3 = new Label(composite_21, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_3.setText(SystemSettingTypeEnum.软件版本.getDefaultValue());
		Label label_31 = new Label(composite_21, SWT.NONE);
		label_31.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_31.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_31.setText("数据库版本：");
		
		Label label_4 = new Label(composite_21, SWT.NONE);
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_4.setText(mapSystemSetting.get(SystemSettingTypeEnum.DateBase_version));
		
		Label label_51 = new Label(composite_21, SWT.NONE);
		label_51.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_51.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_51.setText("发布时间：");
		
		Label label_6 = new Label(composite_21, SWT.NONE);
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_6.setText(SystemSettingTypeEnum.发布时间.getDefaultValue());
		new Label(composite_21, SWT.NONE);
		new Label(composite_21, SWT.NONE);
		
		Composite composite_31 = new Composite(composite1, SWT.NONE);
		composite_31.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_31.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd_composite_3 = new GridData(GridData.FILL_HORIZONTAL);
		gd_composite_3.horizontalAlignment = SWT.RIGHT;
		composite_31.setLayoutData(gd_composite_3);
		
		Image handImg = JFaceUtil.getImage("hand_16");
		CLabel lblNewLabel1 = new CLabel(composite_31, SWT.NONE);
		lblNewLabel1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel1.setText("首页");
		lblNewLabel1.setImage(JFaceUtil.getImage("home_32"));
		lblNewLabel1.setCursor(new org.eclipse.swt.graphics.Cursor(shell.getDisplay(),handImg.getImageData(),0,0));
		
		CLabel lblNewLabel2 = new CLabel(composite_31, SWT.NONE);
		lblNewLabel2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel2.setText("邮箱");
		lblNewLabel2.setImage(JFaceUtil.getImage("email_32"));
		lblNewLabel2.setCursor(new org.eclipse.swt.graphics.Cursor(shell.getDisplay(),handImg.getImageData(),0,0));
		
		lblNewLabel1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				try{
					Runtime.getRuntime().exec("cmd /k start "+"http://www.dongluhitec.com/");
				}catch(Exception ex){}
			}
			
		});
		
		lblNewLabel2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				try{
					Runtime.getRuntime().exec("cmd /k start "+"mailto:154341736@qq.com");
				}catch(Exception ex){}
			}
			
		});
		initDataBindings();
	}



	public Map<SystemSettingTypeEnum, String> getMapSystemSetting() {
		return mapSystemSetting;
	}

	@Override
	public boolean isOpen() {
		return false;
	}


	@Override
	public Shell getShell() {
		return this.shell;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		return bindingContext;
	}

	public void select(SingleCarparkModuleEnum module) {
		tabFolder.setSelection(mapModuleToItem.get(module));
		shell.forceFocus();
	}

	
}
