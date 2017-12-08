package com.donglu.carpark.ui.view.card.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;

public class EditCardWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private SingleCarparkCard model;
	
	
	private Text txt_identifier;
	private Text txt_serialNumber;

	protected EditCardWizardPage(SingleCarparkCard model) {
		super("添加卡片");
		setTitle("修改卡片");
		setDescription("");
		this.model = model;
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, NONE);
		setControl(composite);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		GridData gd_composite_1 = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gd_composite_1.widthHint = 303;
		composite_1.setLayoutData(gd_composite_1);
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("卡片内码");
		
		txt_serialNumber = new Text(composite_1, SWT.BORDER);
		txt_serialNumber.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		GridData gd_txt_serialNumber = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_serialNumber.widthHint = 219;
		txt_serialNumber.setLayoutData(gd_txt_serialNumber);
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label.setText("卡片编号");
		
		txt_identifier = new Text(composite_1, SWT.BORDER);
		txt_identifier.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		txt_identifier.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		m_bindingContext = initDataBindings();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxt_identifierObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_identifier);
		IObservableValue identifierModelObserveValue = BeanProperties.value("identifier").observe(model);
		bindingContext.bindValue(observeTextTxt_identifierObserveWidget, identifierModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_serialNumberObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_serialNumber);
		IObservableValue serialNumberModelObserveValue = BeanProperties.value("serialNumber").observe(model);
		bindingContext.bindValue(observeTextTxt_serialNumberObserveWidget, serialNumberModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
