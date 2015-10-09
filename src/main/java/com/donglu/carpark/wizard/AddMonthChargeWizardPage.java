package com.donglu.carpark.wizard;

import net.miginfocom.swt.MigLayout;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.wizard.model.AddMonthChargeModel;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.CardTypeEnum;
import com.dongluhitec.card.domain.db.carpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.carpark.ParkingLotTypeEnum;
import com.dongluhitec.card.ui.cache.MonthlyCarparkChargeInfo;
import com.dongluhitec.card.ui.init.InitModel;
import org.eclipse.core.databinding.beans.BeanProperties;

public class AddMonthChargeWizardPage extends WizardPage{
	private DataBindingContext m_bindingContext;

	private final String Txt_column_layout = "wrap,grow y,w 100:110:130";
	private AddMonthChargeModel model;
	private Composite container;
	private Text txt_chargeCode;
	private Text txt_chargeName;
	private Combo combo_carType;
	private ComboViewer cbv_carType;
	private Text txt_rentingDays;
	private Text txt_expiringDays;
	private Text txt_price;
	private ComboViewer cbv_parkType;
	private Combo combo_parkType;
	private Text txt_note;
	private Text txt_delayDays;

	public AddMonthChargeWizardPage(AddMonthChargeModel model2) {
		super("wizardPage");
		this.model = model2;
		setTitle("添加月租收费信息");
		setDescription("请输入月租用户收费标准的信息");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		setControl(container);
		container.setLayout(new MigLayout("fill", "", ""));

		Composite mainComposite = new Composite(container, SWT.BORDER);
		mainComposite.setLayoutData("align center");
		mainComposite.setLayout(new MigLayout("", "[]20[grow]", "[][][][][][][]"));
		
		Label lbl_chargeCode = new Label(mainComposite,SWT.NONE);
		lbl_chargeCode.setText("月租类型编码");
		txt_chargeCode = new Text(mainComposite,SWT.BORDER);
		txt_chargeCode.setLayoutData(Txt_column_layout);
		
		Label lbl_chargeName = new Label(mainComposite,SWT.NONE);
		lbl_chargeName.setText("月租类型名称");
		txt_chargeName = new Text(mainComposite,SWT.BORDER);
		txt_chargeName.setLayoutData(Txt_column_layout);
		
		Label lbl_carType = new Label(mainComposite,SWT.NONE);
		lbl_carType.setText("车辆类型");
		cbv_carType = new ComboViewer(mainComposite,SWT.BORDER|SWT.READ_ONLY);
		combo_carType = cbv_carType.getCombo();
		combo_carType.setLayoutData(Txt_column_layout);
		
		Label lbl_rentingDays = new Label(mainComposite,SWT.NONE);
		lbl_rentingDays.setText("月租月数");
		txt_rentingDays = new Text(mainComposite,SWT.BORDER);
		txt_rentingDays.setLayoutData(Txt_column_layout);
		
		Label lbl_expiringDays = new Label(mainComposite,SWT.NONE);
		lbl_expiringDays.setText("到期提醒天数");
		txt_expiringDays = new Text(mainComposite,SWT.BORDER);
		txt_expiringDays.setLayoutData(Txt_column_layout);

		Label lbl_delayDays = new Label(mainComposite,SWT.NONE);
		lbl_delayDays.setText("到期延迟天数");
		txt_delayDays = new Text(mainComposite,SWT.BORDER);
		txt_delayDays.setLayoutData(Txt_column_layout);
		
		Label lbl_price = new Label(mainComposite,SWT.NONE);
		lbl_price.setText("租赁金额");
		txt_price = new Text(mainComposite,SWT.BORDER);
		txt_price.setLayoutData(Txt_column_layout);
		
		Label lbl_parkType = new Label(mainComposite,SWT.NONE);
		lbl_parkType.setText("车位类型");
		cbv_parkType = new ComboViewer(mainComposite,SWT.BORDER|SWT.READ_ONLY);
		combo_parkType = cbv_parkType.getCombo();
		combo_parkType.setLayoutData(Txt_column_layout);
		
		Label lbl_note = new Label(mainComposite,SWT.NONE);
		lbl_note.setText("备注");
		txt_note = new Text(mainComposite,SWT.BORDER | SWT.MULTI);
		txt_note.setLayoutData(Txt_column_layout);
		
		//绑定enum
		cbv_carType.setLabelProvider(new LabelProvider());
		cbv_carType.setContentProvider(new ArrayContentProvider());
		cbv_carType.setInput(new String[]{"摩托车","小车","大车"});
		
		cbv_parkType.setLabelProvider(new LabelProvider());
		cbv_parkType.setContentProvider(new ArrayContentProvider());
		cbv_parkType.setInput(new String[]{"固定车位","非固定车位"});
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxt_chargeCodeObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_chargeCode);
		IObservableValue chargeCodeModelObserveValue = BeanProperties.value("chargeCode").observe(model);
		bindingContext.bindValue(observeTextTxt_chargeCodeObserveWidget, chargeCodeModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_chargeNameObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_chargeName);
		IObservableValue chargeNameModelObserveValue = BeanProperties.value("chargeName").observe(model);
		bindingContext.bindValue(observeTextTxt_chargeNameObserveWidget, chargeNameModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionCbv_carType = ViewerProperties.singleSelection().observe(cbv_carType);
		IObservableValue carTypeModelObserveValue = BeanProperties.value("carType").observe(model);
		bindingContext.bindValue(observeSingleSelectionCbv_carType, carTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_rentingDaysObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_rentingDays);
		IObservableValue rentingDaysModelObserveValue = BeanProperties.value("rentingDays").observe(model);
		bindingContext.bindValue(observeTextTxt_rentingDaysObserveWidget, rentingDaysModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_expiringDaysObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_expiringDays);
		IObservableValue expiringDaysModelObserveValue = BeanProperties.value("expiringDays").observe(model);
		bindingContext.bindValue(observeTextTxt_expiringDaysObserveWidget, expiringDaysModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_delayDaysObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_delayDays);
		IObservableValue delayDaysModelObserveValue = BeanProperties.value("delayDays").observe(model);
		bindingContext.bindValue(observeTextTxt_delayDaysObserveWidget, delayDaysModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_priceObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_price);
		IObservableValue priceModelObserveValue = BeanProperties.value("price").observe(model);
		bindingContext.bindValue(observeTextTxt_priceObserveWidget, priceModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionCbv_parkType = ViewerProperties.singleSelection().observe(cbv_parkType);
		IObservableValue parkTypeModelObserveValue = BeanProperties.value("parkType").observe(model);
		bindingContext.bindValue(observeSingleSelectionCbv_parkType, parkTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_noteObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_note);
		IObservableValue noteModelObserveValue = BeanProperties.value("note").observe(model);
		bindingContext.bindValue(observeTextTxt_noteObserveWidget, noteModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
