package com.donglu.carpark.ui.wizard.monthcharge;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.carpark.CarparkMonthlyUser;
import net.miginfocom.swt.MigLayout;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.nebula.widgets.datechooser.DateChooserComboObservableValue;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.Calendar;
import java.util.Date;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class MonthlyUserPayBasicPage extends WizardPage {
	private DataBindingContext m_bindingContext;

	private MonthlyUserPayModel model;
	private Composite container;
	private Control text_username;
	private Control text_userIdCard;
	private Text text_monthMoney;
	private Text text_createTime;
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

	public MonthlyUserPayBasicPage(MonthlyUserPayModel model) {
		super("WizardPage");
		this.model = model;
		this.initOverdueTime = model.getOverdueTime();

		setTitle("车牌月租用户缴费");
		setDescription("请输入缴费金额、到期时间");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new MigLayout("fill", "[]", "[]"));

		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayoutData("align center,w 200");
		composite.setLayout(new GridLayout(2, false));

		Label label_10 = new Label(composite, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_10.setText("用户名");
		text_username = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		text_username.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_username.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label label_8 = new Label(composite, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_8.setText("身份证号");
		text_userIdCard = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		text_userIdCard.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_userIdCard.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_9 = new Label(composite, SWT.NONE);
		label_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_9.setText("车 牌 号");
		carCode = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		carCode.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		carCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setText("创建日期");
		text_createTime = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		text_createTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_createTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

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
		combo_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SingleCarparkMonthlyCharge selectMonth = model.getSelectMonth();
				if (StrUtil.isEmpty(selectMonth)) {
					return;
				}
				model.setCarType(selectMonth.getCarType());
				model.setMonthamount(selectMonth.getRentingDays());
				model.setMonthCharge(selectMonth.getPrice());
			}
		});
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

		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setText("缴费期数");
		cbv_chargesCount = new ComboViewer(composite, SWT.BORDER | SWT.READ_ONLY);
		cbv_chargesCount.setLabelProvider(new LabelProvider());
		cbv_chargesCount.setContentProvider(new ArrayContentProvider());
		cbv_chargesCount.setInput(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
		combo = cbv_chargesCount.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		combo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Integer chargesCount = Integer.parseInt(combo.getText());
				model.setOldOverDueTime(model.getOverdueTime());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(initOverdueTime == null ? model.getCreateTime() : initOverdueTime);
				calendar.add(Calendar.MONTH, model.getMonthamount() * chargesCount);
				float s=model.getMonthCharge()*chargesCount;
				model.setChargesMoney(s);
				Date time = calendar.getTime();
				Date todayBottomTime = StrUtil.getTodayBottomTime(time);
				model.setOverdueTime(todayBottomTime);
				// TODO bind?
				dateChooserCombo.setValue(todayBottomTime);
//				
//				text_chargesMoney.setText(s + "");
			}
		});
		
		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_6.setText("到期时间");
		dateChooserCombo = new DateChooserCombo(composite, SWT.BORDER);
		dateChooserCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Date value = dateChooserCombo.getValue();
				model.setOverdueTime(StrUtil.getTodayBottomTime(value));
				System.out.println(value);
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
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextText_usernameObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_username);
		IObservableValue userNameModelObserveValue = BeanProperties.value("userName").observe(model);
		bindingContext.bindValue(observeTextText_usernameObserveWidget, userNameModelObserveValue, null, null);
		//
		IObservableValue observeTextText_userIdCardObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_userIdCard);
		IObservableValue userIdCardModelObserveValue = BeanProperties.value("userIdCard").observe(model);
		bindingContext.bindValue(observeTextText_userIdCardObserveWidget, userIdCardModelObserveValue, null, null);
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
		IObservableValue observeTextText_createTimeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_createTime);
		IObservableValue createTimeLabelModelObserveValue = BeanProperties.value("createTimeLabel").observe(model);
		bindingContext.bindValue(observeTextText_createTimeObserveWidget, createTimeLabelModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
