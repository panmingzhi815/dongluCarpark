package com.donglu.carpark.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.donglu.carpark.model.ConcentrateModel;
import com.donglu.carpark.ui.common.AbstractApp;
import com.donglu.carpark.util.ImageUtils;
import com.donglu.carpark.util.TextUtils;
import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class ConcentrateApp extends AbstractApp {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	@Inject
	private ConcentratePresenter presenter;
	private ConcentrateModel model=new ConcentrateModel();
	private RateLimiter rateLimiter = RateLimiter.create(2);
	
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	private Text text_6;
	private Text text_7;
	private Text text_8;
	private Text text_9;
	private ComboViewer comboViewer;
	private ComboViewer comboViewer_1;

	private Label lbl_inImg;

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
					Injector injector = Guice.createInjector();
					ConcentrateApp window = injector.getInstance(ConcentrateApp.class);
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
		init();
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.setMaximized(true);
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void init() {
		presenter.setView(this);
		presenter.setModel(model);
		presenter.init();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(897, 601);
		shell.setText("集中收费");
		SashForm sashForm = new SashForm(shell, SWT.NONE);
		
		Composite composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		lbl_inImg = new Label(composite, SWT.NONE);
		lbl_inImg.setText("进场图片");
		lbl_inImg.setAlignment(SWT.CENTER);
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.NONE);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("收费");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_1);
		composite_1.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel_3 = new Label(composite_1, SWT.NONE);
		lblNewLabel_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("登录账户");
		
		text_5 = new Text(composite_1, SWT.BORDER);
		text_5.setEditable(false);
		text_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_5 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_5.widthHint = 239;
		text_5.setLayoutData(gd_text_5);
		
		Label lblNewLabel_4 = new Label(composite_1, SWT.NONE);
		lblNewLabel_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setText("登录时间");
		
		text_6 = new Text(composite_1, SWT.BORDER);
		text_6.setEditable(false);
		text_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_6 = new Label(composite_1, SWT.NONE);
		lblNewLabel_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_6.setText("收费金额");
		
		text_7 = new Text(composite_1, SWT.BORDER);
		text_7.setEditable(false);
		text_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_5 = new Label(composite_1, SWT.NONE);
		lblNewLabel_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_5.setText("免费金额");
		
		text_8 = new Text(composite_1, SWT.BORDER);
		text_8.setEditable(false);
		text_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌号码");
		
		text = new Text(composite_1, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode==StrUtil.BIG_KEY_ENTER) {
					if (!rateLimiter.tryAcquire()) {
						return;
					}
					if (StrUtil.isEmpty(text.getText())||model.getPlateNO().length()<2) {
						return;
					}
					presenter.searchAndCount();
				}
			}
		});
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text.setTextLimit(8);
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("进场时间");
		
		text_1 = new Text(composite_1, SWT.BORDER);
		text_1.setEditable(false);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_1 = new Label(composite_1, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("停车时间");
		
		text_2 = new Text(composite_1, SWT.BORDER);
		text_2.setEditable(false);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("应收金额");
		
		text_3 = new Text(composite_1, SWT.BORDER);
		text_3.setEditable(false);
		text_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_8 = new Label(composite_1, SWT.NONE);
		lblNewLabel_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_8.setText("已收金额");
		
		text_9 = new Text(composite_1, SWT.BORDER);
		text_9.setEditable(false);
		text_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("缴费金额");
		
		text_4 = new Text(composite_1, SWT.BORDER);
		text_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_4.setTextLimit(8);
		Label lblNewLabel_9 = new Label(composite_1, SWT.NONE);
		lblNewLabel_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_9.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_9.setText("停 车 场");
		
		comboViewer = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				presenter.getListCarTypeAndSelect();
			}
		});
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_10 = new Label(composite_1, SWT.NONE);
		lblNewLabel_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_10.setText("车辆类型");
		
		comboViewer_1 = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo_1 = comboViewer_1.getCombo();
		combo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		comboViewer_1.setInput(model.getListCarType());
		
		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(3, false);
		gl_composite_2.verticalSpacing = 15;
		gl_composite_2.horizontalSpacing = 15;
		gl_composite_2.marginTop = 15;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		
		Button btnNewButton = new Button(composite_2, SWT.NONE);
		btnNewButton.setToolTipText("输入车牌后查询缴费金额");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					System.out.println(rateLimiter.tryAcquire());
					return;
				}
				presenter.searchAndCount();
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		btnNewButton.setText("计      算");
		
		Button btnNewButton_1 = new Button(composite_2, SWT.NONE);
		btnNewButton_1.setToolTipText("对车辆进行收费收费");
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				presenter.charge();
			}
		});
		btnNewButton_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		btnNewButton_1.setText("收      费");
		Button button_1 = new Button(composite_2, SWT.NONE);
		button_1.setToolTipText("当计算时没有找到车牌，可以人工查找");
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.search();
			}
		});
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		button_1.setText("查      询");
		
		Button btnNewButton_3 = new Button(composite_2, SWT.NONE);
		btnNewButton_3.setToolTipText("对储值、固定用户进行续费、续期");
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				presenter.userRecharge();
			}
		});
		btnNewButton_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		btnNewButton_3.setText("续      费");
		
		Button btnNewButton_4 = new Button(composite_2, SWT.NONE);
		btnNewButton_4.setToolTipText("更换值班人员");
		btnNewButton_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				presenter.changeUser();
			}
		});
		btnNewButton_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		btnNewButton_4.setText("换      班");
		
		Button button = new Button(composite_2, SWT.NONE);
		button.setToolTipText("把钱交给其他人");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.returnAccount();
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		button.setText("归      账");
		sashForm.setWeights(new int[] {2, 1});
		
		m_bindingContext = initDataBindings();
		TextUtils.createPlateNOAutoCompleteField(text);
		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("场内车");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_3);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getCarInPresenter().go(composite_3);
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextText_5ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_5);
		IObservableValue userNameModelObserveValue = BeanProperties.value("userName").observe(model);
		bindingContext.bindValue(observeTextText_5ObserveWidget, userNameModelObserveValue, null, null);
		//
		IObservableValue observeTextText_6ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_6);
		IObservableValue workTimeModelObserveValue = BeanProperties.value("workTime").observe(model);
		bindingContext.bindValue(observeTextText_6ObserveWidget, workTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_7ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_7);
		IObservableValue totalFactModelObserveValue = BeanProperties.value("totalFact").observe(model);
		bindingContext.bindValue(observeTextText_7ObserveWidget, totalFactModelObserveValue, null, null);
		//
		IObservableValue observeTextText_8ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_8);
		IObservableValue totalFreeModelObserveValue = BeanProperties.value("totalFree").observe(model);
		bindingContext.bindValue(observeTextText_8ObserveWidget, totalFreeModelObserveValue, null, null);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue plateNOModelObserveValue = BeanProperties.value("plateNO").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, plateNOModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue inTimeModelObserveValue = BeanProperties.value("inTime").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, inTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue stillTimeModelObserveValue = BeanProperties.value("stillTime").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, stillTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue shouldMoneyModelObserveValue = BeanProperties.value("shouldMoney").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, shouldMoneyModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue factMoneyModelObserveValue = BeanProperties.value("factMoney").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, factMoneyModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SingleCarparkCarpark.class, "labelString");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer.setContentProvider(listContentProvider);
		//
		IObservableList listCarparkModelObserveList = BeanProperties.list("listCarpark").observe(model);
		comboViewer.setInput(listCarparkModelObserveList);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue carparkModelObserveValue = BeanProperties.value("carpark").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, carparkModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties.singleSelection().observe(comboViewer_1);
		IObservableValue carTypeModelObserveValue = BeanProperties.value("carType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_1, carTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_9ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_9);
		IObservableValue paidMoneyModelObserveValue = BeanProperties.value("paidMoney").observe(model);
		bindingContext.bindValue(observeTextText_9ObserveWidget, paidMoneyModelObserveValue, null, null);
		//
		return bindingContext;
	}

	public void setInImage(String bigImg) {
		byte[] imageByte = ImageUtils.getImageByte(bigImg);
		ImageUtils.setBackgroundImage(imageByte, lbl_inImg, shell.getDisplay());
	}
}
