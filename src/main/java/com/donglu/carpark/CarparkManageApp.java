package com.donglu.carpark;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;

import com.beust.jcommander.JCommander;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.wizard.AddBlackUserWizard;
import com.donglu.carpark.wizard.AddCarparkModel;
import com.donglu.carpark.wizard.AddCarparkWizard;
import com.donglu.carpark.wizard.AddDeviceModel;
import com.donglu.carpark.wizard.AddSystemUserWizard;
import com.donglu.carpark.wizard.AddUserWizard;
import com.donglu.carpark.wizard.NewCommonChargeWizard;
import com.dongluhitec.card.blservice.DatabaseServiceProvider;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.Widget;
import com.dongluhitec.card.common.ui.WidgetContainer;
import com.dongluhitec.card.common.ui.impl.SWTContainer;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.Attendance.realtime.RealTimePresenter;
import com.dongluhitec.card.ui.cache.MonthlyCarparkChargeInfo;
import com.dongluhitec.card.ui.carpark.charge.wizard.NewMonthCardWizard;
import com.dongluhitec.card.ui.main.DongluUIAppConfigurator;
import com.dongluhitec.card.ui.main.javafx.DongluJavaFXModule;
import com.dongluhitec.card.ui.util.WidgetUtil;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.wb.rcp.databinding.BeansListObservableFactory;
import org.eclipse.wb.rcp.databinding.TreeBeanAdvisor;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.wb.rcp.databinding.TreeObservableLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;

public class CarparkManageApp {
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	private Table table;
	private Table table_2;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Table table_3;
	private Text text_3;
	private Text text_4;
	private Table table_4;
	private Text text_5;
	private Text text_6;
	private SashForm sashForm;
	private Table table_5;
	
	@Inject
	CommonUIFacility commonui;
	
	private Table table_1;
	private Text text_7;
	private Text text_8;
	private Table table_6;
	private ToolBar carparkConfigToolBar;
	private Text text_9;
	private Text text_10;
	private Text text_11;
	private Text text_12;
	@Inject
	private CarparkManagePresenter presenter;
	
	private TreeViewer treeViewer;
	
	private CarparkModel carparkModel;
	
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
		Display display = Display.getDefault();
		init();
		createContents();
		shell.open();
		shell.setMaximized(true);
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		System.exit(0);
	}

	private void init() {
		presenter.setView(this);
		carparkModel=new CarparkModel();
		presenter.setCarparkModel(carparkModel);
		presenter.init();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(781, 621);
		shell.setText("停车场管理界面");
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
		toolItem_add.setText("+");
		
		ToolItem toolItem_1 = new ToolItem(carparkConfigToolBar, SWT.NONE);
		toolItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.deleteCarpark();
			}
		});
		toolItem_1.setText("-");
		
		treeViewer = new TreeViewer(composite_3, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		TreeColumn treeColumn = treeViewerColumn.getColumn();
		treeColumn.setWidth(100);
		treeColumn.setText("停车场");
		
		Composite composite_4 = new Composite(sashForm_1, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		
		Label label_1 = new Label(composite_4, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setText("收费设置");
		
		ToolBar toolBar_1 = new ToolBar(composite_4, SWT.FLAT | SWT.RIGHT);
		toolBar_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ToolItem toolItem_2 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewCommonChargeWizard v=new NewCommonChargeWizard();
				WizardDialog dialog=new WizardDialog(new Shell(), v);
				dialog.open();
			}
		});
		toolItem_2.setText("+");
		
		ToolItem toolItem_14 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_14.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MonthlyCarparkChargeInfo wizardModel = new MonthlyCarparkChargeInfo();
				NewMonthCardWizard newWizard =new NewMonthCardWizard(wizardModel);
				commonui.showWizard(newWizard);
			}
		});
		toolItem_14.setText("#");
		
		ToolItem toolItem_3 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_3.setText("-");
		
		ToolItem toolItem_8 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_8.setToolTipText("修改");
		toolItem_8.setText("/");
		
		TableViewer tableViewer = new TableViewer(composite_4, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		TableViewerColumn tableViewerColumn_20 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_5 = tableViewerColumn_20.getColumn();
		tblclmnNewColumn_5.setWidth(100);
		tblclmnNewColumn_5.setText("类型");
		
		TableViewerColumn tableViewerColumn_21 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumn_6 = tableViewerColumn_21.getColumn();
		tblclmnNewColumn_6.setWidth(100);
		tblclmnNewColumn_6.setText("收费");
		sashForm_1.setWeights(new int[] {1, 1});
		sashForm.setWeights(new int[] {1});
		
		TabItem tabItem_5 = new TabItem(tabFolder, SWT.NONE);
		tabItem_5.setText("固定车设置");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		composite_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		tabItem_5.setControl(composite_2);
		composite_2.setLayout(new GridLayout(2, false));
		
		Group group_3 = new Group(composite_2, SWT.NONE);
		group_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		group_3.setLayout(new GridLayout(5, false));
		
		Label lblNewLabel_9 = new Label(group_3, SWT.NONE);
		lblNewLabel_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_9.setText("姓名");
		
		text_7 = new Text(group_3, SWT.BORDER);
		text_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_10 = new Label(group_3, SWT.NONE);
		lblNewLabel_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_10.setText("车牌");
		
		text_8 = new Text(group_3, SWT.BORDER);
		text_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button button_2 = new Button(group_3, SWT.NONE);
		button_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_2.setText("查询");
		
		Label label_2 = new Label(composite_2, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setText("固定用户设置");
		
		ToolBar toolBar_2 = new ToolBar(composite_2, SWT.FLAT | SWT.RIGHT);
		toolBar_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ToolItem toolItem_7 = new ToolItem(toolBar_2, SWT.NONE);
		toolItem_7.setText("$");
		
		ToolItem toolItem_4 = new ToolItem(toolBar_2, SWT.NONE);
		toolItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddUserWizard v=new AddUserWizard(new AddDeviceModel());
//				WizardDialog dialog=new WizardDialog(new Shell(), v);
//				dialog.open();
				Object showWizard = commonui.showWizard(v);
				System.out.println(showWizard);
			}
		});
		toolItem_4.setText("+");
		
		ToolItem toolItem_5 = new ToolItem(toolBar_2, SWT.NONE);
		toolItem_5.setText("-");
		
		ToolItem toolItem_6 = new ToolItem(toolBar_2, SWT.NONE);
		toolItem_6.setText("/");
		
		TableViewer tableViewer_1 = new TableViewer(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_1 = tableViewer_1.getTable();
		table_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		TableViewerColumn tableViewerColumn_28 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_22 = tableViewerColumn_28.getColumn();
		tableColumn_22.setWidth(78);
		tableColumn_22.setText("编号");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
		tableColumn_1.setWidth(70);
		tableColumn_1.setText("姓名");
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(73);
		tableColumn.setText("车牌号");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_2 = tableViewerColumn_2.getColumn();
		tableColumn_2.setWidth(67);
		tableColumn_2.setText("住址");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_3 = tableViewerColumn_3.getColumn();
		tableColumn_3.setWidth(81);
		tableColumn_3.setText("用户类型");
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_12 = tableViewerColumn_4.getColumn();
		tableColumn_12.setWidth(78);
		tableColumn_12.setText("有效期");
		
		TableViewerColumn tableViewerColumn_17 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_21 = tableViewerColumn_17.getColumn();
		tableColumn_21.setWidth(74);
		tableColumn_21.setText("车位");
		
		TableViewerColumn tableViewerColumn_29 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tableColumn_23 = tableViewerColumn_29.getColumn();
		tableColumn_23.setWidth(100);
		tableColumn_23.setText("备注");
		
		TabItem tbtmNewItem_2 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_2.setText("记录查询");
		
		Composite composite_5 = new Composite(tabFolder, SWT.NONE);
		composite_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		tbtmNewItem_2.setControl(composite_5);
		composite_5.setLayout(new GridLayout(1, false));
		
		TabFolder tabFolder_1 = new TabFolder(composite_5, SWT.BOTTOM);
		tabFolder_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tabItem = new TabItem(tabFolder_1, SWT.NONE);
		tabItem.setText("进出记录查询");
		
		Composite composite_7 = new Composite(tabFolder_1, SWT.NONE);
		tabItem.setControl(composite_7);
		composite_7.setLayout(new GridLayout(1, false));
		
		Group group = new Group(composite_7, SWT.NONE);
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(9, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		group.setText("查询");
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("车牌");
		
		text = new Text(group, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 71;
		text.setLayoutData(gd_text);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("用户");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setText("开始时间");
		
		DateTime dateTime = new DateTime(group, SWT.BORDER);
		dateTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("操作员");
		
		text_2 = new Text(group, SWT.BORDER);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button = new Button(group, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("查询");
		
		Label label_9 = new Label(group, SWT.NONE);
		label_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("车辆类型");
		
		ComboViewer comboViewer = new ComboViewer(group, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[]{"全部","固定车","临时车"});
		combo.select(0);
		Label label_10 = new Label(group, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("是否出场");
		
		ComboViewer comboViewer_1 = new ComboViewer(group, SWT.NONE);
		Combo combo_1 = comboViewer_1.getCombo();
		combo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		comboViewer_1.setInput(new String[]{"无","是","否"});
		combo_1.select(0);
		
		Label lblNewLabel_7 = new Label(group, SWT.NONE);
		lblNewLabel_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_7.setText("结束时间");
		
		DateTime dateTime_2 = new DateTime(group, SWT.BORDER);
		dateTime_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label label_13 = new Label(group, SWT.NONE);
		label_13.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_13.setText("统计金额");
		
		text_12 = new Text(group, SWT.BORDER);
		text_12.setEnabled(false);
		text_12.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button button_10 = new Button(group, SWT.NONE);
		button_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_12.setText("11111");
			}
		});
		button_10.setText("统计");
		
		TableViewer tableViewer_2 = new TableViewer(composite_7, SWT.BORDER | SWT.FULL_SELECTION);
		table_2 = tableViewer_2.getTable();
		table_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table_2.setLinesVisible(true);
		table_2.setHeaderVisible(true);
		table_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer_2, SWT.NONE);
		TableColumn tblclmnNewColumn_1 = tableViewerColumn_5.getColumn();
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("车牌号");
		
		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer_2, SWT.NONE);
		TableColumn tblclmnNewColumn_2 = tableViewerColumn_6.getColumn();
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("进时间");
		
		TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(tableViewer_2, SWT.NONE);
		TableColumn tblclmnNewColumn_3 = tableViewerColumn_7.getColumn();
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText("出时间");
		
		TableViewerColumn tableViewerColumn_8 = new TableViewerColumn(tableViewer_2, SWT.NONE);
		TableColumn tblclmnNewColumn_4 = tableViewerColumn_8.getColumn();
		tblclmnNewColumn_4.setWidth(100);
		tblclmnNewColumn_4.setText("操作员");
		
		TableViewerColumn tableViewerColumn_13 = new TableViewerColumn(tableViewer_2, SWT.NONE);
		TableColumn tableColumn_8 = tableViewerColumn_13.getColumn();
		tableColumn_8.setWidth(100);
		tableColumn_8.setText("收费");
		
		TableViewerColumn tableViewerColumn_14 = new TableViewerColumn(tableViewer_2, SWT.NONE);
		TableColumn tableColumn_9 = tableViewerColumn_14.getColumn();
		tableColumn_9.setWidth(100);
		tableColumn_9.setText("时间");
		
		TabItem tabItem_1 = new TabItem(tabFolder_1, SWT.NONE);
		tabItem_1.setText("归账查询");
		
		Composite composite_6 = new Composite(tabFolder_1, SWT.NONE);
		tabItem_1.setControl(composite_6);
		composite_6.setLayout(new GridLayout(1, false));
		
		Group group_1 = new Group(composite_6, SWT.NONE);
		group_1.setLayout(new GridLayout(9, false));
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		group_1.setText("查询");
		
		Label lblNewLabel_3 = new Label(group_1, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("操作员");
		
		text_3 = new Text(group_1, SWT.BORDER);
		text_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_4 = new Label(group_1, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setText("归账人");
		
		text_4 = new Text(group_1, SWT.BORDER);
		text_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_5 = new Label(group_1, SWT.NONE);
		lblNewLabel_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_5.setText("时间");
		
		DateTime dateTime_1 = new DateTime(group_1, SWT.BORDER);
		dateTime_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label lblNewLabel_8 = new Label(group_1, SWT.NONE);
		lblNewLabel_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_8.setText("终止时间");
		
		DateTime dateTime_3 = new DateTime(group_1, SWT.BORDER);
		dateTime_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button btnNewButton = new Button(group_1, SWT.NONE);
		btnNewButton.setText("查询");
		
		TableViewer tableViewer_3 = new TableViewer(composite_6, SWT.BORDER | SWT.FULL_SELECTION);
		table_3 = tableViewer_3.getTable();
		table_3.setHeaderVisible(true);
		table_3.setLinesVisible(true);
		table_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn_9 = new TableViewerColumn(tableViewer_3, SWT.NONE);
		TableColumn tableColumn_4 = tableViewerColumn_9.getColumn();
		tableColumn_4.setWidth(100);
		tableColumn_4.setText("归账人");
		
		TableViewerColumn tableViewerColumn_10 = new TableViewerColumn(tableViewer_3, SWT.NONE);
		TableColumn tableColumn_5 = tableViewerColumn_10.getColumn();
		tableColumn_5.setWidth(100);
		tableColumn_5.setText("操作员");
		
		TableViewerColumn tableViewerColumn_11 = new TableViewerColumn(tableViewer_3, SWT.NONE);
		TableColumn tableColumn_6 = tableViewerColumn_11.getColumn();
		tableColumn_6.setWidth(100);
		tableColumn_6.setText("金额");
		
		TableViewerColumn tableViewerColumn_12 = new TableViewerColumn(tableViewer_3, SWT.NONE);
		TableColumn tableColumn_7 = tableViewerColumn_12.getColumn();
		tableColumn_7.setWidth(100);
		tableColumn_7.setText("时间");
		
		TabItem tabItem_2 = new TabItem(tabFolder_1, SWT.NONE);
		tabItem_2.setText("充值查询");
		
		Composite composite_8 = new Composite(tabFolder_1, SWT.NONE);
		tabItem_2.setControl(composite_8);
		composite_8.setLayout(new GridLayout(1, false));
		
		Group group_2 = new Group(composite_8, SWT.NONE);
		group_2.setLayout(new GridLayout(9, false));
		group_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		group_2.setText("查询");
		
		Label label_4 = new Label(group_2, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("用户");
		
		text_5 = new Text(group_2, SWT.BORDER);
		text_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_5 = new Label(group_2, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("操作员");
		
		text_6 = new Text(group_2, SWT.BORDER);
		text_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_6 = new Label(group_2, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("时间");
		
		DateTime dateTime_5 = new DateTime(group_2, SWT.BORDER);
		dateTime_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label label_7 = new Label(group_2, SWT.NONE);
		label_7.setText("终止时间");
		
		DateTime dateTime_4 = new DateTime(group_2, SWT.BORDER);
		dateTime_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button_1 = new Button(group_2, SWT.NONE);
		button_1.setText("查询");
		
		TableViewer tableViewer_4 = new TableViewer(composite_8, SWT.BORDER | SWT.FULL_SELECTION);
		table_4 = tableViewer_4.getTable();
		table_4.setLinesVisible(true);
		table_4.setHeaderVisible(true);
		table_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn_15 = new TableViewerColumn(tableViewer_4, SWT.NONE);
		TableColumn tableColumn_10 = tableViewerColumn_15.getColumn();
		tableColumn_10.setWidth(100);
		tableColumn_10.setText("用户姓名");
		
		TableViewerColumn tableViewerColumn_16 = new TableViewerColumn(tableViewer_4, SWT.NONE);
		TableColumn tableColumn_11 = tableViewerColumn_16.getColumn();
		tableColumn_11.setWidth(100);
		tableColumn_11.setText("充值金额");
		
		TableViewerColumn tableViewerColumn_18 = new TableViewerColumn(tableViewer_4, SWT.NONE);
		TableColumn tableColumn_13 = tableViewerColumn_18.getColumn();
		tableColumn_13.setWidth(100);
		tableColumn_13.setText("操作人");
		
		TableViewerColumn tableViewerColumn_19 = new TableViewerColumn(tableViewer_4, SWT.NONE);
		TableColumn tableColumn_14 = tableViewerColumn_19.getColumn();
		tableColumn_14.setWidth(100);
		tableColumn_14.setText("时间");
		
		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("系统用户");
		
		Composite composite_9 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(composite_9);
		composite_9.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel_6 = new Label(composite_9, SWT.NONE);
		lblNewLabel_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_6.setText("系统用户设置");
		
		ToolBar toolBar_3 = new ToolBar(composite_9, SWT.FLAT | SWT.RIGHT);
		toolBar_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ToolItem toolItem_9 = new ToolItem(toolBar_3, SWT.NONE);
		toolItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddSystemUserWizard v=new AddSystemUserWizard(this);
				commonui.showWizard(v);
			}
		});
		toolItem_9.setText("+");
		
		ToolItem toolItem_10 = new ToolItem(toolBar_3, SWT.NONE);
		toolItem_10.setText("-");
		
		ToolItem toolItem_11 = new ToolItem(toolBar_3, SWT.NONE);
		toolItem_11.setText("/");
		
		TableViewer tableViewer_5 = new TableViewer(composite_9, SWT.BORDER | SWT.FULL_SELECTION);
		table_5 = tableViewer_5.getTable();
		table_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table_5.setLinesVisible(true);
		table_5.setHeaderVisible(true);
		table_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		TableViewerColumn tableViewerColumn_27 = new TableViewerColumn(tableViewer_5, SWT.NONE);
		TableColumn tableColumn_20 = tableViewerColumn_27.getColumn();
		tableColumn_20.setWidth(100);
		tableColumn_20.setText("账号");
		
		TableViewerColumn tableViewerColumn_22 = new TableViewerColumn(tableViewer_5, SWT.NONE);
		TableColumn tableColumn_15 = tableViewerColumn_22.getColumn();
		tableColumn_15.setWidth(100);
		tableColumn_15.setText("用户名称");
		
		TableViewerColumn tableViewerColumn_23 = new TableViewerColumn(tableViewer_5, SWT.NONE);
		TableColumn tableColumn_16 = tableViewerColumn_23.getColumn();
		tableColumn_16.setWidth(100);
		tableColumn_16.setText("用户类型");
		
		TableViewerColumn tableViewerColumn_24 = new TableViewerColumn(tableViewer_5, SWT.NONE);
		TableColumn tableColumn_17 = tableViewerColumn_24.getColumn();
		tableColumn_17.setWidth(100);
		tableColumn_17.setText("创建时间");
		
		TableViewerColumn tableViewerColumn_25 = new TableViewerColumn(tableViewer_5, SWT.NONE);
		TableColumn tableColumn_18 = tableViewerColumn_25.getColumn();
		tableColumn_18.setWidth(100);
		tableColumn_18.setText("最后修改时间");
		
		TableViewerColumn tableViewerColumn_26 = new TableViewerColumn(tableViewer_5, SWT.NONE);
		TableColumn tableColumn_19 = tableViewerColumn_26.getColumn();
		tableColumn_19.setWidth(100);
		tableColumn_19.setText("最后修改人");
		
		TabItem tabItem_3 = new TabItem(tabFolder, SWT.NONE);
		tabItem_3.setText("设置");
		
		Composite composite_12 = new Composite(tabFolder, SWT.NONE);
		tabItem_3.setControl(composite_12);
		composite_12.setLayout(new GridLayout(2, false));
		
		Group group_4 = new Group(composite_12, SWT.NONE);
		group_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group_4.setLayout(new GridLayout(3, false));
		GridData gd_group_4 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_group_4.heightHint = 106;
		gd_group_4.widthHint = 461;
		group_4.setLayoutData(gd_group_4);
		group_4.setText("停车场设置");
		
		Button btnCheckButton = new Button(group_4, SWT.CHECK);
		btnCheckButton.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnCheckButton.setText("车位满是否允许临时车入场");
		
		Button btnCheckButton_1 = new Button(group_4, SWT.CHECK);
		btnCheckButton_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnCheckButton_1.setText("车位满是否允许免费车入场");
		
		Button button_3 = new Button(group_4, SWT.CHECK);
		button_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_3.setText("车位满是否允许储值车入场");
		
		Button button_4 = new Button(group_4, SWT.CHECK);
		button_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_4.setText("临时车入场是否确认");
		
		Button button_6 = new Button(group_4, SWT.CHECK);
		button_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_6.setText("临时车零收费是否自动出场");
		new Label(group_4, SWT.NONE);
		
		Button button_5 = new Button(group_4, SWT.CHECK);
		button_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_5.setText("固定车入场是否确认");
		
		Button button_7 = new Button(group_4, SWT.CHECK);
		button_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_7.setText("固定车出场确认");
		new Label(group_4, SWT.NONE);
		
		Composite composite_14 = new Composite(group_4, SWT.NONE);
		composite_14.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		composite_14.setLayout(new GridLayout(3, false));
		
		Label label_11 = new Label(composite_14, SWT.NONE);
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_11.setText("数据库备份位置");
		
		text_9 = new Text(composite_14, SWT.BORDER);
		GridData gd_text_9 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_9.widthHint = 239;
		text_9.setLayoutData(gd_text_9);
		
		Button button_8 = new Button(composite_14, SWT.NONE);
		button_8.setText("备份");
		
		Composite composite_15 = new Composite(group_4, SWT.NONE);
		composite_15.setLayout(new GridLayout(4, false));
		composite_15.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		
		Label label_12 = new Label(composite_15, SWT.NONE);
		GridData gd_label_12 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_label_12.widthHint = 83;
		label_12.setLayoutData(gd_label_12);
		label_12.setText("图片存放位置");
		
		text_10 = new Text(composite_15, SWT.BORDER);
		GridData gd_text_10 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_10.widthHint = 241;
		text_10.setLayoutData(gd_text_10);
		
		Button button_9 = new Button(composite_15, SWT.CHECK);
		button_9.setText("是否自动删除");
		
		text_11 = new Text(composite_15, SWT.BORDER);
		text_11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(group_4, SWT.NONE);
		new Label(group_4, SWT.NONE);
		new Label(group_4, SWT.NONE);
		new Label(group_4, SWT.NONE);
		new Label(group_4, SWT.NONE);
		new Label(group_4, SWT.NONE);
		
		Composite composite_13 = new Composite(composite_12, SWT.NONE);
		composite_13.setLayout(new GridLayout(2, false));
		GridData gd_composite_13 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_composite_13.heightHint = 360;
		composite_13.setLayoutData(gd_composite_13);
		
		Label label_8 = new Label(composite_13, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_8.setText("黑名单");
		
		ToolBar toolBar_4 = new ToolBar(composite_13, SWT.FLAT | SWT.RIGHT);
		toolBar_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ToolItem toolItem_12 = new ToolItem(toolBar_4, SWT.NONE);
		toolItem_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddBlackUserWizard v=new AddBlackUserWizard(this);
				commonui.showWizard(v);
			}
		});
		toolItem_12.setText("+");
		
		ToolItem toolItem_13 = new ToolItem(toolBar_4, SWT.NONE);
		toolItem_13.setText("-");
		
		TableViewer tableViewer_6 = new TableViewer(composite_13, SWT.BORDER | SWT.FULL_SELECTION);
		table_6 = tableViewer_6.getTable();
		table_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table_6.setLinesVisible(true);
		table_6.setHeaderVisible(true);
		GridData gd_table_6 = new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1);
		gd_table_6.widthHint = 170;
		table_6.setLayoutData(gd_table_6);
		
		TableViewerColumn tableViewerColumn_30 = new TableViewerColumn(tableViewer_6, SWT.NONE);
		TableColumn tableColumn_24 = tableViewerColumn_30.getColumn();
		tableColumn_24.setAlignment(SWT.CENTER);
		tableColumn_24.setWidth(100);
		tableColumn_24.setText("车牌");
		
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
		
		lblNewLabel_2 = new Label(composite_21, SWT.NONE);
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
		
		lblNewLabel_3 = new Label(composite_21, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_3.setText(System.getProperty("version","读取失败"));
		
		Label label_31 = new Label(composite_21, SWT.NONE);
		label_31.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_31.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_31.setText("数据库版本：");
		
		label_4 = new Label(composite_21, SWT.NONE);
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_4.setText("1.2.0.1");
		
		Label label_51 = new Label(composite_21, SWT.NONE);
		label_51.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_51.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_51.setText("发布时间：");
		
		label_6 = new Label(composite_21, SWT.NONE);
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
		
		TabItem tbtmNewItem_3 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_3.setText("New Item");
		
		Composite composite_16 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_3.setControl(composite_16);

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
		m_bindingContext = initDataBindings();
	}

	private void controlDispay() {
		String type = System.getProperty("userType");
		if (type==null) {
			System.exit(0);
		}
		if (!type.equals("admin")) {
			carparkConfigToolBar.dispose();
		}
		
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		BeansListObservableFactory treeObservableFactory = new BeansListObservableFactory(SingleCarparkCarpark.class, "childs");
		TreeBeanAdvisor treeAdvisor = new TreeBeanAdvisor(SingleCarparkCarpark.class, "parent", "childs", null);
		ObservableListTreeContentProvider treeContentProvider = new ObservableListTreeContentProvider(treeObservableFactory, treeAdvisor);
		treeViewer.setLabelProvider(new TreeObservableLabelProvider(treeContentProvider.getKnownElements(), SingleCarparkCarpark.class, "name", null));
		treeViewer.setContentProvider(treeContentProvider);
		//
		IObservableList listCarparkCarparkModelObserveList = BeanProperties.list("listCarpark").observe(carparkModel);
		treeViewer.setInput(listCarparkCarparkModelObserveList);
		//
		IObservableValue observeSingleSelectionTreeViewer = ViewerProperties.singleSelection().observe(treeViewer);
		IObservableValue carparkCarparkModelObserveValue = BeanProperties.value("carpark").observe(carparkModel);
		bindingContext.bindValue(observeSingleSelectionTreeViewer, carparkCarparkModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
