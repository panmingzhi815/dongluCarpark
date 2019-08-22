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
	private Label label_6;
	private Composite composite_2;
	private Text text_7;
	private Text text_8;
	private Text text_9;
	private Text text_10;
	private Label label_11;
	private Text text_11;
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
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 260;
		text.setLayoutData(gd_text);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("停车场名称");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
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
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		label_4 = new Label(composite, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("云项目编号");
		
		text_5 = new Text(composite, SWT.BORDER);
		text_5.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		label_5 = new Label(composite, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("云项目名称");
		
		text_6 = new Text(composite, SWT.BORDER);
		text_6.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		label_11 = new Label(composite, SWT.NONE);
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_11.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_11.setText("停车场地址");
		
		text_11 = new Text(composite, SWT.BORDER);
		text_11.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_6 = new Label(composite, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_6.setText("停车场位置");
		
		composite_2 = new Composite(composite, SWT.BORDER);
		composite_2.setLayout(new GridLayout(4, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_7 = new Label(composite_2, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_7.setText("经度");
		
		text_7 = new Text(composite_2, SWT.BORDER);
		GridData gd_text_7 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_7.widthHint = 80;
		text_7.setLayoutData(gd_text_7);
		text_7.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		
		Label label_8 = new Label(composite_2, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("纬度");
		
		text_8 = new Text(composite_2, SWT.BORDER);
		text_8.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		GridData gd_text_8 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_8.widthHint = 80;
		text_8.setLayoutData(gd_text_8);
		
		Label label_9 = new Label(composite_2, SWT.NONE);
		label_9.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("高度");
		
		text_9 = new Text(composite_2, SWT.BORDER);
		text_9.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_10 = new Label(composite_2, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("楼层");
		
		text_10 = new Text(composite_2, SWT.BORDER);
		text_10.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
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
		IObservableValue observeTextText_7ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_7);
		IObservableValue lonModelObserveValue = BeanProperties.value("lon").observe(model);
		bindingContext.bindValue(observeTextText_7ObserveWidget, lonModelObserveValue, null, null);
		//
		IObservableValue observeTextText_8ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_8);
		IObservableValue latModelObserveValue = BeanProperties.value("lat").observe(model);
		bindingContext.bindValue(observeTextText_8ObserveWidget, latModelObserveValue, null, null);
		//
		IObservableValue observeTextText_9ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_9);
		IObservableValue altModelObserveValue = BeanProperties.value("alt").observe(model);
		bindingContext.bindValue(observeTextText_9ObserveWidget, altModelObserveValue, null, null);
		//
		IObservableValue observeTextText_10ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_10);
		IObservableValue floorModelObserveValue = BeanProperties.value("floor").observe(model);
		bindingContext.bindValue(observeTextText_10ObserveWidget, floorModelObserveValue, null, null);
		//
		IObservableValue observeTextText_11ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_11);
		IObservableValue addressModelObserveValue = BeanProperties.value("address").observe(model);
		bindingContext.bindValue(observeTextText_11ObserveWidget, addressModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
