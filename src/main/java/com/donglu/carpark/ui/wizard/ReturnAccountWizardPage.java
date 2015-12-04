package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;

import com.donglu.carpark.ui.wizard.model.ReturnAccountModel;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Button;

public class ReturnAccountWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private Text text;
	private Text text_1;
	private Text text_3;
	private Text text_4;
	ReturnAccountModel model;
	private Text text_2;
	private Button button;
	private Text text_5;

	
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public ReturnAccountWizardPage(ReturnAccountModel model) {
		super("wizardPage");
		setDescription("请输入归账人的用户名和密码");
		this.model=model;
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(container, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 10;
		composite.setLayout(gridLayout);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("用户名称");
		
		text = new Text(composite, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 150;
		text.setLayoutData(gd_text);
		new Label(composite, SWT.NONE);
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("用户密码");
		
		text_1 = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_1.widthHint = 150;
		text_1.setLayoutData(gd_text_1);
		new Label(composite, SWT.NONE);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("归账用户");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setEditable(false);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("实收金额");
		
		text_3 = new Text(composite, SWT.BORDER);
		text_3.setEditable(false);
		text_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_3.widthHint = 150;
		text_3.setLayoutData(gd_text_3);
		new Label(composite, SWT.NONE);
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("免费金额");
		
		text_4 = new Text(composite, SWT.BORDER);
		text_4.setEditable(false);
		text_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_4 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_4.widthHint = 150;
		text_4.setLayoutData(gd_text_4);
		
		button = new Button(composite, SWT.CHECK);
		button.setToolTipText("选中之后同时归账免费金额");
		
		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setText("备      注");
		
		text_5 = new Text(composite, SWT.BORDER);
		GridData gd_text_5 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_5.heightHint = 68;
		text_5.setLayoutData(gd_text_5);
		new Label(composite, SWT.NONE);
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue operaNameModelObserveValue = BeanProperties.value("operaName").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, operaNameModelObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue returnUserModelObserveValue = BeanProperties.value("returnUser").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, returnUserModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue shouldReturnModelObserveValue = BeanProperties.value("shouldReturn").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, shouldReturnModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue factReturnModelObserveValue = BeanProperties.value("factReturn").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, factReturnModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue pwdModelObserveValue = BeanProperties.value("pwd").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, pwdModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButtonObserveWidget = WidgetProperties.selection().observe(button);
		IObservableValue freeModelObserveValue = BeanProperties.value("free").observe(model);
		bindingContext.bindValue(observeSelectionButtonObserveWidget, freeModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
