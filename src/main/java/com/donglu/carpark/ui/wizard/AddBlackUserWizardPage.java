package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.util.TextUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.widgets.Button;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.nebula.widgets.datechooser.DateChooserComboObservableValue;
import org.eclipse.nebula.widgets.datechooser.DateChooserObservableValue;

public class AddBlackUserWizardPage extends WizardPage {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	private Text text;
	private Text text_1;
	SingleCarparkBlackUser model;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	private Button button;
	private Button button_1;
	private Button btnCheckButton;
	private Label label_6;
	private DateChooserCombo dateChooserCombo;

	/**
	 * Create the wizard.
	 * @param model 
	 */
	public AddBlackUserWizardPage(SingleCarparkBlackUser model) {
		super("wizardPage");
		this.model=model;
		if (StrUtil.isEmpty(model.getPlateNO())) {
			setDescription("添加黑名单");
		}else{
			setDescription("修改黑名单");
		}
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌");
		
		text = new Text(composite, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 131;
		text.setLayoutData(gd_text);
		TextUtils.createPlateNOAutoCompleteField(text);
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setText("起始时间");
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_1.setLayout(new GridLayout(7, false));
		
		text_2 = new Text(composite_1, SWT.BORDER);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_2.widthHint = 19;
		text_2.setLayoutData(gd_text_2);
		
		Label label_3 = new Label(composite_1, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText(":");
		
		text_3 = new Text(composite_1, SWT.BORDER);
		text_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_3 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_3.widthHint = 21;
		text_3.setLayoutData(gd_text_3);
		
		Label label_4 = new Label(composite_1, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("--");
		
		text_4 = new Text(composite_1, SWT.BORDER);
		text_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_4 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_4.widthHint = 20;
		text_4.setLayoutData(gd_text_4);
		
		Label label_5 = new Label(composite_1, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText(":");
		
		text_5 = new Text(composite_1, SWT.BORDER);
		text_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_5 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_5.widthHint = 21;
		text_5.setLayoutData(gd_text_5);
		
		label_6 = new Label(composite, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_6.setText("有效期");
		
		dateChooserCombo = new DateChooserCombo(composite, SWT.BORDER);
		dateChooserCombo.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateChooserCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		label_1.setText("备注");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_1.heightHint = 99;
		text_1.setLayoutData(gd_text_1);
		new Label(composite, SWT.NONE);
		
		button = new Button(composite, SWT.CHECK);
		button.setToolTipText("选中后，限制车辆只允许在限制时间进入");
		button.setText("只允许限制时段进场");
		new Label(composite, SWT.NONE);
		
		button_1 = new Button(composite, SWT.CHECK);
		button_1.setToolTipText("选中后，限制车辆节假日不允许进入");
		button_1.setText("节假日不允许进入");
		new Label(composite, SWT.NONE);
		
		btnCheckButton = new Button(composite, SWT.CHECK);
		btnCheckButton.setToolTipText("选中后，限制车辆工作日不允许进入");
		btnCheckButton.setText("工作日不允许进入");
		m_bindingContext = initDataBindings();
		initBind();
	}
	private void initBind() {
		DateChooserComboObservableValue dateChooserObservableValue=new DateChooserComboObservableValue(dateChooserCombo, SWT.Modify);
		IObservableValue validModelObserveValue = BeanProperties.value("valid").observe(model);
		m_bindingContext.bindValue(dateChooserObservableValue, validModelObserveValue);
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue plateNOModelObserveValue = BeanProperties.value("plateNO").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, plateNOModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue remarkModelObserveValue = BeanProperties.value("remark").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, remarkModelObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue hoursStartLabelModelObserveValue = BeanProperties.value("hoursStartLabel").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, hoursStartLabelModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue minuteStartLabelModelObserveValue = BeanProperties.value("minuteStartLabel").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, minuteStartLabelModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue hoursEndLabelModelObserveValue = BeanProperties.value("hoursEndLabel").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, hoursEndLabelModelObserveValue, null, null);
		//
		IObservableValue observeTextText_5ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_5);
		IObservableValue minuteEndLabelModelObserveValue = BeanProperties.value("minuteEndLabel").observe(model);
		bindingContext.bindValue(observeTextText_5ObserveWidget, minuteEndLabelModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButtonObserveWidget = WidgetProperties.selection().observe(button);
		IObservableValue timeInModelObserveValue = BeanProperties.value("timeIn").observe(model);
		bindingContext.bindValue(observeSelectionButtonObserveWidget, timeInModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButton_1ObserveWidget = WidgetProperties.selection().observe(button_1);
		IObservableValue holidayInModelObserveValue = BeanProperties.value("holidayIn").observe(model);
		bindingContext.bindValue(observeSelectionButton_1ObserveWidget, holidayInModelObserveValue, null, null);
		//
		IObservableValue observeSelectionBtnCheckButtonObserveWidget = WidgetProperties.selection().observe(btnCheckButton);
		IObservableValue weekDayInModelObserveValue = BeanProperties.value("weekDayIn").observe(model);
		bindingContext.bindValue(observeSelectionBtnCheckButtonObserveWidget, weekDayInModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
