package com.donglu.carpark.ui;


import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.rcp.databinding.BeansListObservableFactory;
import org.eclipse.wb.rcp.databinding.TreeBeanAdvisor;
import org.eclipse.wb.rcp.databinding.TreeObservableLabelProvider;
import org.eclipse.wb.swt.SWTResourceManager;

import com.beust.jcommander.JCommander;
import com.donglu.carpark.info.CarparkChargeInfo;
import com.donglu.carpark.model.CarparkModel;
import com.donglu.carpark.model.SystemUserModel;
import com.donglu.carpark.ui.common.AbstractApp;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.ui.main.DongluUIAppConfigurator;
import com.dongluhitec.card.ui.main.javafx.DongluJavaFXModule;
import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;



public class CarparkManageApp extends AbstractApp{
	public static final String CLIENT_IMAGE_SAVE_FILE_PATH = "clientImageSaveFilePath";


	protected Shell shell;
	private Table table;
	private SashForm sashForm;
	private Table table_5;
	
	@Inject
	CommonUIFacility commonui;
	private ToolBar carparkConfigToolBar;
	@Inject
	private CarparkManagePresenter presenter;
	
	private TreeViewer treeViewer;
	
	private CarparkModel carparkModel;
	
	
	private SystemUserModel systemUserModel;
	
	private TableViewer tableViewer_1;
	private TableViewer tableViewer;
	
	private Map<SystemSettingTypeEnum, String> mapSystemSetting=Maps.newHashMap();

	private Composite composite_returnAccount_search;
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
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
	public void open() {
		try {
			Display display = Display.getDefault();
			init();
			createContents();
			shell.setImage(JFaceUtil.getImage("carpark_16"));
			shell.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent e) {
					System.exit(0);
					
				}
			});
			shell.open();
			shell.setMaximized(true);
			shell.layout();
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
		carparkModel=new CarparkModel();
		systemUserModel=new SystemUserModel();
		presenter.setCarparkModel(carparkModel);
		presenter.setSystemUserModel(systemUserModel);
		
		for (SystemSettingTypeEnum t : SystemSettingTypeEnum.values()) {
			mapSystemSetting.put(t, null);
		}
		presenter.init();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(896, 621);
		String dbServerIp = CarparkClientConfig.getInstance().getDbServerIp();
		shell.setText("停车场管理界面("+dbServerIp+")");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("停车场管理");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		sashForm = new SashForm(composite, SWT.NONE);
		
		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm_1 = new SashForm(composite_1, SWT.VERTICAL);
		
		Composite composite_3 = new Composite(sashForm_1, SWT.BORDER);
		composite_3.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite_3, SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 88;
		label.setLayoutData(gd_label);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setText("停车场设置");
		
		carparkConfigToolBar = new ToolBar(composite_3, SWT.FLAT | SWT.RIGHT);
		carparkConfigToolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		ToolItem toolItem_add = new ToolItem(carparkConfigToolBar, SWT.NONE);
		toolItem_add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.addCarpark();
			}
		});
		toolItem_add.setText("添加主停车场");
		
		ToolItem toolItem_13 = new ToolItem(carparkConfigToolBar, SWT.NONE);
		toolItem_13.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.addChildCapark();
			}
		});
		toolItem_13.setText("添加子停车场");
		
		ToolItem toolItem_1 = new ToolItem(carparkConfigToolBar, SWT.NONE);
		toolItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.deleteCarpark();
			}
		});
		toolItem_1.setText("删除");
		
		ToolItem toolItem = new ToolItem(carparkConfigToolBar, SWT.NONE);
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.editCarpark();
			}
		});
		toolItem.setText("修改");
		
		ToolItem toolItem_5 = new ToolItem(carparkConfigToolBar, SWT.NONE);
		toolItem_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.refreshCarpark();
			}
		});
		toolItem_5.setText("刷新");
		
		treeViewer = new TreeViewer(composite_3, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.refreshCarparkCharge();
			}
		});
		tree.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Composite composite_4 = new Composite(sashForm_1, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		
		Label label_1 = new Label(composite_4, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setText("收费设置");
		
		ToolBar toolBar_1 = new ToolBar(composite_4, SWT.FLAT | SWT.RIGHT);
		toolBar_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ToolItem toolItem_2 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_2.setToolTipText("添加临时收费设置");
		toolItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.addTempCharge(null);
			}
		});
		toolItem_2.setText("添加临时收费设置");
		
		ToolItem toolItem_14 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_14.setToolTipText("添加固定收费设置");
		toolItem_14.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.addMonthCharge();
			}
		});
		toolItem_14.setText("添加固定收费设置");
		
		ToolItem toolItem_3 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.deleteCarparkCharge();
			}
		});
		toolItem_3.setText("删除");
		
		ToolItem toolItem_8 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.editCarparkChargeSetting();
			}
		});
		toolItem_8.setToolTipText("修改");
		toolItem_8.setText("修改");
		
		ToolItem toolItem_9 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.startUseTempCharge();
			}
		});
		toolItem_9.setToolTipText("启用临时收费设置");
		toolItem_9.setText("启用");
		
		ToolItem toolItem_12 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.stopUseTempCharge();
			}
		});
		toolItem_12.setToolTipText("禁用停车场收费设置");
		toolItem_12.setText("禁用");
		
		ToolItem toolItem_6 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.refreshCarparkCharge();
			}
		});
		toolItem_6.setText("刷新");
		
		tableViewer = new TableViewer(composite_4, SWT.BORDER | SWT.FULL_SELECTION);
//		tableViewer.setSorter(new TableViewerColumnSorter((TableViewerColumn) null));
		table = tableViewer.getTable();
		table.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//		tableViewer.setSorter(new ViewerSorter());
		TableViewerColumn tvc_code = new TableViewerColumn(tableViewer, SWT.RIGHT);
		TableColumn tc_code = tvc_code.getColumn();
		tc_code.setResizable(false);
		tc_code.setWidth(101);
		tc_code.setText("编码");
		TableViewerColumn tableViewerColumn_21 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_6 = tableViewerColumn_21.getColumn();
		tblclmnNewColumn_6.setWidth(150);
		tblclmnNewColumn_6.setText("名称");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn_1.getColumn();
		tblclmnNewColumn.setWidth(114);
		tblclmnNewColumn.setText("收费类型");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_2.getColumn();
		tableColumn_1.setWidth(100);
		tableColumn_1.setText("车辆类型");
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_3 = tableViewerColumn_4.getColumn();
		tableColumn_3.setWidth(100);
		tableColumn_3.setText("节假日类型");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_2 = tableViewerColumn_3.getColumn();
		tableColumn_2.setWidth(100);
		tableColumn_2.setText("是否启用");
		sashForm_1.setWeights(new int[] {1, 1});
		sashForm.setWeights(new int[] {1});
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("固定车设置");
		
		Composite composite_13 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_13);
		composite_13.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getUserPresenter().go(composite_13);
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
		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("系统用户");
		
		Composite composite_9 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(composite_9);
		composite_9.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel_6 = new Label(composite_9, SWT.NONE);
		lblNewLabel_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_6.setText("系统用户设置");
		
		ToolBar toolBar_systemUser = new ToolBar(composite_9, SWT.FLAT | SWT.RIGHT);
		toolBar_systemUser.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.NORMAL));
		toolBar_systemUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ToolItem toolItem_addSystemUser = new ToolItem(toolBar_systemUser, SWT.NONE);
		toolItem_addSystemUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.addSystemUser();
			}
		});
		toolItem_addSystemUser.setText("添加");
		
		ToolItem toolItem_10 = new ToolItem(toolBar_systemUser, SWT.NONE);
		toolItem_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.deleteSystemUser();
			}
		});
		toolItem_10.setText("删除");
		
		ToolItem toolItem_11 = new ToolItem(toolBar_systemUser, SWT.NONE);
		toolItem_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.editSystemUser();
			}
		});
		toolItem_11.setText("修改");
		
		tableViewer_1 = new TableViewer(composite_9, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table_5 = tableViewer_1.getTable();
		table_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table_5.setLinesVisible(true);
		table_5.setHeaderVisible(true);
		table_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		TableViewerColumn tableViewerColumn_22 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_15 = tableViewerColumn_22.getColumn();
		tableColumn_15.setWidth(100);
		tableColumn_15.setText("用户名称");
		
		TableViewerColumn tableViewerColumn_23 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_16 = tableViewerColumn_23.getColumn();
		tableColumn_16.setWidth(100);
		tableColumn_16.setText("用户类型");
		
		TableViewerColumn tableViewerColumn_24 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_17 = tableViewerColumn_24.getColumn();
		tableColumn_17.setWidth(200);
		tableColumn_17.setText("创建时间");
		
		TableViewerColumn tableViewerColumn_25 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_18 = tableViewerColumn_25.getColumn();
		tableColumn_18.setWidth(200);
		tableColumn_18.setText("最后修改时间");
		
		TableViewerColumn tableViewerColumn_26 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_19 = tableViewerColumn_26.getColumn();
		tableColumn_19.setWidth(111);
		tableColumn_19.setText("最后修改人");
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(100);
		tableColumn.setText("备注");
		
		TabItem tabItem_5 = new TabItem(tabFolder, SWT.NONE);
		tabItem_5.setText("商铺优惠");
		
		Composite composite_20 = new Composite(tabFolder, SWT.NONE);
		tabItem_5.setControl(composite_20);
		composite_20.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getStorePresenter().go(composite_20);
		
		TabItem tabItem_8 = new TabItem(tabFolder, SWT.NONE);
		tabItem_8.setText("参数设置");
		
		Composite composite_25 = new Composite(tabFolder, SWT.NONE);
		tabItem_8.setControl(composite_25);
		composite_25.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getSettingPresenter().go(composite_25);
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
		
		label_1 = new Label(composite_21, SWT.NONE);
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_1.setText("深圳市东陆高新实业有限公司");
		
		Label label_21 = new Label(composite_21, SWT.NONE);
		label_21.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_21.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_21.setText("软件版本：");
		
		Label lblNewLabel_3 = new Label(composite_21, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_3.setText("1.0.0.0");
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
		label_6.setText("2014-08-15 15：00：00");
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
		controlDispay();
		addTableListener();
		initDataBindings();
	}

	private void addTableListener() {}

	private void controlDispay() {
		String type = System.getProperty("userType");
		if (type==null) {
			System.exit(0);
		}
		if (!type.equals("系统管理员")) {
			carparkConfigToolBar.dispose();
		}
		
	}


	public Map<SystemSettingTypeEnum, String> getMapSystemSetting() {
		return mapSystemSetting;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	public void expandAllCarpark() {
		if(shell == null || shell.isDisposed()){
			return;
		}
		shell.getDisplay().asyncExec(()->{
			treeViewer.expandAll();
		});
	}

	@Override
	public Shell getShell() {
		return this.shell;
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		BeansListObservableFactory treeObservableFactory = new BeansListObservableFactory(SingleCarparkCarpark.class, "childs");
		TreeBeanAdvisor treeAdvisor = new TreeBeanAdvisor(SingleCarparkCarpark.class, "parent", "childs", null);
		ObservableListTreeContentProvider treeContentProvider = new ObservableListTreeContentProvider(treeObservableFactory, treeAdvisor);
		treeViewer.setLabelProvider(new TreeObservableLabelProvider(treeContentProvider.getKnownElements(), SingleCarparkCarpark.class, "labelString", null));
		treeViewer.setContentProvider(treeContentProvider);
		//
		IObservableList listCarparkCarparkModelObserveList = BeanProperties.list("listCarpark").observe(carparkModel);
		treeViewer.setInput(listCarparkCarparkModelObserveList);
		//
		IObservableValue observeSingleSelectionTreeViewer = ViewerProperties.singleSelection().observe(treeViewer);
		IObservableValue carparkCarparkModelObserveValue = BeanProperties.value("carpark").observe(carparkModel);
		bindingContext.bindValue(observeSingleSelectionTreeViewer, carparkCarparkModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		IObservableMap[] observeMaps_1 = BeansObservables.observeMaps(listContentProvider_1.getKnownElements(), SingleCarparkSystemUser.class, new String[]{"userName", "type", "createDateLabel", "lastEditDateLabel", "lastEditUser", "remark"});
		tableViewer_1.setLabelProvider(new ObservableMapLabelProvider(observeMaps_1));
		tableViewer_1.setContentProvider(listContentProvider_1);
		//
		IObservableList listSystemUserModelObserveList = BeanProperties.list("list").observe(systemUserModel);
		tableViewer_1.setInput(listSystemUserModelObserveList);
		//
		IObservableList observeMultiSelectionTableViewer_1 = ViewerProperties.multipleSelection().observe(tableViewer_1);
		IObservableList selectListSystemUserModelObserveList = BeanProperties.list("selectList").observe(systemUserModel);
		bindingContext.bindList(observeMultiSelectionTableViewer_1, selectListSystemUserModelObserveList, null, null);
		//
		ObservableListContentProvider listContentProvider_2 = new ObservableListContentProvider();
		IObservableMap[] observeMaps_2 = BeansObservables.observeMaps(listContentProvider_2.getKnownElements(), CarparkChargeInfo.class, new String[]{"code", "name", "type", "carType", "holidayType", "useType"});
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps_2));
		tableViewer.setContentProvider(listContentProvider_2);
		//
		IObservableList listCarparkChargeCarparkModelObserveList = BeanProperties.list("listCarparkCharge").observe(carparkModel);
		tableViewer.setInput(listCarparkChargeCarparkModelObserveList);
		//
		IObservableValue observeSingleSelectionTableViewer = ViewerProperties.singleSelection().observe(tableViewer);
		IObservableValue carparkChargeInfoCarparkModelObserveValue = BeanProperties.value("carparkChargeInfo").observe(carparkModel);
		bindingContext.bindValue(observeSingleSelectionTableViewer, carparkChargeInfoCarparkModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
