package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.wb.swt.SWTResourceManager;

public class AddCarparkWizardPage extends WizardPage {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	private Text text;
	private Text text_1;
	private SingleCarparkCarpark model;
	private Button btnBu;
	private Text text_2;
	private Text text_4;
	private Button button;
	private Label label_3;
	private Text text_3;
	private Label label_4;
	private Label label_5;
	private Text text_5;
	private Text text_6;
	private Button button_1;
	private Button button_2;
	private Composite composite_1;
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public AddCarparkWizardPage(SingleCarparkCarpark model) {
		super("wizardPage");
		this.model=model;
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
		composite.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("停车场编码");
		
		text = new Text(composite, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("停车场名称");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("总共车位数");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("固定车位数");
		
		text_4 = new Text(composite, SWT.BORDER);
		text_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		label_3 = new Label(composite, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("云平台编号");
		
		text_3 = new Text(composite, SWT.BORDER);
		text_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_4 = new Label(composite, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("云项目编号");
		
		text_5 = new Text(composite, SWT.BORDER);
		text_5.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_5 = new Label(composite, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("云项目名称");
		
		text_6 = new Text(composite, SWT.BORDER);
		text_6.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		btnBu = new Button(composite_1, SWT.CHECK);
		btnBu.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnBu.setText("不允许临时车进入");
		
		button = new Button(composite_1, SWT.CHECK);
		button.setToolTipText("选中则表示停车场需要收费");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("停车场需要收费");
		
		button_1 = new Button(composite_1, SWT.CHECK);
		button_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_1.setText("固定车一进一出");
		
		button_2 = new Button(composite_1, SWT.CHECK);
		button_2.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_2.setText("临时车一进一出");
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue codeModelObserveValue = BeanProperties.value("code").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, codeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue nameModelObserveValue = BeanProperties.value("name").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, nameModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButtonObserveWidget = WidgetProperties.selection().observe(btnBu);
		IObservableValue tempCarIsInModelObserveValue = BeanProperties.value("tempCarIsIn").observe(model);
		bindingContext.bindValue(observeSelectionButtonObserveWidget, tempCarIsInModelObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue totalNumberOfSlotModelObserveValue = BeanProperties.value("totalNumberOfSlot").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, totalNumberOfSlotModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue fixNumberOfSlotModelObserveValue = BeanProperties.value("fixNumberOfSlot").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, fixNumberOfSlotModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButtonObserveWidget_1 = WidgetProperties.selection().observe(button);
		IObservableValue isChargeModelObserveValue = BeanProperties.value("isCharge").observe(model);
		bindingContext.bindValue(observeSelectionButtonObserveWidget_1, isChargeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue yunIdentifierModelObserveValue = BeanProperties.value("yunIdentifier").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, yunIdentifierModelObserveValue, null, null);
		//
		IObservableValue observeTextText_5ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_5);
		IObservableValue yunBuildIdentifierModelObserveValue = BeanProperties.value("yunBuildIdentifier").observe(model);
		bindingContext.bindValue(observeTextText_5ObserveWidget, yunBuildIdentifierModelObserveValue, null, null);
		//
		IObservableValue observeTextText_6ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_6);
		IObservableValue yunBuildNameModelObserveValue = BeanProperties.value("yunBuildName").observe(model);
		bindingContext.bindValue(observeTextText_6ObserveWidget, yunBuildNameModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButton_1ObserveWidget = WidgetProperties.selection().observe(button_1);
		IObservableValue fixCarOneInModelObserveValue = BeanProperties.value("fixCarOneIn").observe(model);
		bindingContext.bindValue(observeSelectionButton_1ObserveWidget, fixCarOneInModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButton_2ObserveWidget = WidgetProperties.selection().observe(button_2);
		IObservableValue tempCarOneInModelObserveValue = BeanProperties.value("tempCarOneIn").observe(model);
		bindingContext.bindValue(observeSelectionButton_2ObserveWidget, tempCarOneInModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
