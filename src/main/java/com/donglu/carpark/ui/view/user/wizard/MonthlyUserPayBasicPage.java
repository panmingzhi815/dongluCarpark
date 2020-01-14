package com.donglu.carpark.ui.view.user.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.Calendar;
import java.util.Date;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;

import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MonthlyUserPayBasicPage extends WizardPage {

	private MonthlyUserPayModel model;
	private Composite container;
	private Control text_username;
	private Text text_monthMoney;
	private Text text_chargesMoney;
	private DateChooserCombo dateChooserCombo;
	private ComboViewer cbv_chargesCount;
	private Text text_rentingDays;
	private Combo combo;

	private Date initOverdueTime;
	private Text carCode;
	private Text carType;
	private Combo combo_1;
	private ComboViewer comboViewer;
	private Composite composite_1;
	private Combo combo_2;
	private Label label_1;
	private DateChooserCombo dateChooserCombo_start;

	public MonthlyUserPayBasicPage(MonthlyUserPayModel model) {
		super("WizardPage");
		this.model = model;
		this.initOverdueTime = model.getOverdueTime();

		setDescription("请输入缴费金额、到期时间");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		composite.setLayout(new GridLayout(2, false));

		Label label_10 = new Label(composite, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_10.setText("用户名称");
		text_username = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		text_username.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_username = new GridData(GridData.FILL_HORIZONTAL);
		gd_text_username.widthHint = 150;
		text_username.setLayoutData(gd_text_username);

		Label label_9 = new Label(composite, SWT.NONE);
		label_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_9.setText("车牌号码");
		carCode = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		carCode.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		carCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setText("月租类型");

		comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
//		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
//			public void selectionChanged(SelectionChangedEvent event) {
//				SingleCarparkMonthlyCharge selectMonth = model.getSelectMonth();
//				if (StrUtil.isEmpty(selectMonth)) {
//					return;
//				}
//				model.setCarType(selectMonth.getCarType());
//				model.setMonthamount(selectMonth.getDelayDays());
//				model.setMonthCharge(selectMonth.getPrice());
//			}
//		});
		combo_1 = comboViewer.getCombo();
		combo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
//		combo_1.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				SingleCarparkMonthlyCharge selectMonth = model.getSelectMonth();
//				if (StrUtil.isEmpty(selectMonth)) {
//					return;
//				}
//				model.setCarType(selectMonth.getCarType());
//				model.setMonthamount(selectMonth.getRentingDays());
//				model.setMonthCharge(selectMonth.getPrice());
//			}
//		});
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setText("车辆类型");
		carType = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		carType.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		carType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setText("月租月数");
		text_rentingDays = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		text_rentingDays.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_rentingDays.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_4.setText("月租金额");
		text_monthMoney = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		text_monthMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_monthMoney.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_1.setText("起始时间");
		
		dateChooserCombo_start = new DateChooserCombo(composite, SWT.BORDER);
		dateChooserCombo_start.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setOldOverDueTime(dateChooserCombo_start.getValue());
				countMoneyOrTime();
			}
		});
		dateChooserCombo_start.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateChooserCombo_start.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		dateChooserCombo_start.setValue(model.getOldOverDueTime());
		dateChooserCombo_start.setEnabled((ConstUtil.checkPrivilege(SystemUserTypeEnum.超级管理员)||model.getUserId()==null)||model.getOldOverDueTime().before(new Date()));

		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setText("缴费期数");
		
		composite_1 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		cbv_chargesCount = new ComboViewer(composite_1, SWT.NONE);
		cbv_chargesCount.setLabelProvider(new LabelProvider());
		cbv_chargesCount.setContentProvider(new ArrayContentProvider());
		cbv_chargesCount.setInput(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12});
		combo = cbv_chargesCount.getCombo();
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo.widthHint = 70;
		combo.setLayoutData(gd_combo);
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				System.out.println("model.getPaySize()===="+model.getPaySize());
				countMoneyOrTime();
			}
		});
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		combo_2 = new Combo(composite_1, SWT.READ_ONLY);
		combo_2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				countMoneyOrTime();
			}
		});
		combo_2.setItems(new String[] {"期", "天"});
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_2.select(0);

		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_6.setText("到期时间");
		dateChooserCombo = new DateChooserCombo(composite, SWT.BORDER);
		dateChooserCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Date value = dateChooserCombo.getValue();
				model.setOverdueTime(StrUtil.getTodayBottomTime(value));
			}
		});
		dateChooserCombo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		Label label_7 = new Label(composite, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_7.setText("本次缴费");
		text_chargesMoney = new Text(composite, SWT.BORDER);
		text_chargesMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_chargesMoney.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if (!StrUtil.isEmpty(model.getOverdueTime())) {
			dateChooserCombo.setValue(model.getOverdueTime());
		}
		initDataBindings();
	}

	private void countMoneyOrTime() {
		String text = combo.getText();
		if(StrUtil.isEmpty(text)){
			return;
		}
		Integer chargesCount=null;
		try {
			chargesCount = Integer.valueOf(text);
		} catch (NumberFormatException e) {
		}
		if (chargesCount==null) {
			return;
		}
		model.setPaySize(chargesCount);
		int selectionIndex = combo_2.getSelectionIndex();
		model.setPayType(selectionIndex);
		Calendar calendar = Calendar.getInstance();
		Date date = model.getOldOverDueTime();
		if (date==null) {
			date = initOverdueTime == null ? model.getCreateTime() : initOverdueTime;
		}
		if (date==null) {
			date=new Date();
		}
		model.setOldOverDueTime(date);
		calendar.setTime(date);
		float s=0;
		if (selectionIndex==0) {
			calendar.add(Calendar.MONTH, model.getMonthamount() * chargesCount);
			s = model.getMonthCharge() * chargesCount;
		}else{
			calendar.add(Calendar.DATE, chargesCount);
			s = model.getMonthCharge()*12f/365f * chargesCount;
			s=Math.round(s*10)/10f;
		}
		s=s*model.getCarparkSlot();
		model.setChargesMoney(s);
		Date time = calendar.getTime();
		if (StrUtil.getMonthBottomTime(date).getTime()-date.getTime()<1000*60*60*24&&selectionIndex==0) {
			time=StrUtil.getMonthBottomTime(time);
		}
		Date todayBottomTime = StrUtil.getTodayBottomTime(time);
		model.setOverdueTime(todayBottomTime);
		// TODO bind?
		dateChooserCombo.setValue(todayBottomTime);
//				
//				text_chargesMoney.setText(s + "");
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextText_usernameObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_username);
		IObservableValue userNameModelObserveValue = BeanProperties.value("userName").observe(model);
		bindingContext.bindValue(observeTextText_usernameObserveWidget, userNameModelObserveValue, null, null);
		//
		IObservableValue observeTextCarCodeObserveWidget = WidgetProperties.text(SWT.Modify).observe(carCode);
		IObservableValue plateNOModelObserveValue = BeanProperties.value("plateNO").observe(model);
		bindingContext.bindValue(observeTextCarCodeObserveWidget, plateNOModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SingleCarparkMonthlyCharge.class, "chargeName");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer.setContentProvider(listContentProvider);
		//
		IObservableList allmonthModelObserveList = BeanProperties.list("allmonth").observe(model);
		comboViewer.setInput(allmonthModelObserveList);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue selectMonthModelObserveValue = BeanProperties.value("selectMonth").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, selectMonthModelObserveValue, null, null);
		//
		IObservableValue observeTextCarTypeObserveWidget = WidgetProperties.text(SWT.Modify).observe(carType);
		IObservableValue carTypeModelObserveValue = BeanProperties.value("carType").observe(model);
		bindingContext.bindValue(observeTextCarTypeObserveWidget, carTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_rentingDaysObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_rentingDays);
		IObservableValue monthamountModelObserveValue = BeanProperties.value("monthamount").observe(model);
		bindingContext.bindValue(observeTextText_rentingDaysObserveWidget, monthamountModelObserveValue, null, null);
		//
		IObservableValue observeTextText_monthMoneyObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_monthMoney);
		IObservableValue monthChargeModelObserveValue = BeanProperties.value("monthCharge").observe(model);
		bindingContext.bindValue(observeTextText_monthMoneyObserveWidget, monthChargeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_chargesMoneyObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_chargesMoney);
		IObservableValue chargesMoneyModelObserveValue = BeanProperties.value("chargesMoney").observe(model);
		bindingContext.bindValue(observeTextText_chargesMoneyObserveWidget, chargesMoneyModelObserveValue, null, null);
		//
		IObservableValue observeEnabledCombo_1ObserveWidget = WidgetProperties.enabled().observe(combo_1);
		IObservableValue freeModelObserveValue = BeanProperties.value("free").observe(model);
		bindingContext.bindValue(observeEnabledCombo_1ObserveWidget, freeModelObserveValue, null, null);
		//
		IObservableValue observeEnabledDateChooserComboObserveWidget = WidgetProperties.enabled().observe(dateChooserCombo);
		IObservableValue payDateModelObserveValue = BeanProperties.value("payDate").observe(model);
		bindingContext.bindValue(observeEnabledDateChooserComboObserveWidget, payDateModelObserveValue, null, null);
		//
		IObservableValue observeEditableText_chargesMoneyObserveWidget = WidgetProperties.editable().observe(text_chargesMoney);
		IObservableValue payMoneyModelObserveValue = BeanProperties.value("payMoney").observe(model);
		bindingContext.bindValue(observeEditableText_chargesMoneyObserveWidget, payMoneyModelObserveValue, null, null);
		//
		IObservableValue observeEnabledComboObserveWidget = WidgetProperties.enabled().observe(combo);
		IObservableValue selectedSizeModelObserveValue = BeanProperties.value("selectedSize").observe(model);
		bindingContext.bindValue(observeEnabledComboObserveWidget, selectedSizeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionCbv_chargesCount = ViewerProperties.singleSelection().observe(cbv_chargesCount);
		IObservableValue paySizeModelObserveValue = BeanProperties.value("paySize").observe(model);
		bindingContext.bindValue(observeSingleSelectionCbv_chargesCount, paySizeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionIndexCombo_2ObserveWidget = WidgetProperties.singleSelectionIndex().observe(combo_2);
		IObservableValue payTypeModelObserveValue = BeanProperties.value("payType").observe(model);
		bindingContext.bindValue(observeSingleSelectionIndexCombo_2ObserveWidget, payTypeModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
