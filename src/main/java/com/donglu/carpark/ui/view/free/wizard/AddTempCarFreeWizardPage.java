package com.donglu.carpark.ui.view.free.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.core.databinding.DataBindingContext;

import com.donglu.carpark.util.TextUtils;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AddTempCarFreeWizardPage extends WizardPage {
	private Text text;
	private Text text_1;
	private SingleCarparkFreeTempCar model;
	private Text text_4;
	private boolean isImport=false;

	
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public AddTempCarFreeWizardPage(SingleCarparkFreeTempCar model) {
		super("wizardPage");
		this.model=model;
	}

	public AddTempCarFreeWizardPage(SingleCarparkFreeTempCar model, boolean isImport) {
		this(model);
		this.isImport = isImport;
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
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		composite.setLayout(gridLayout);
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌号码");
		
		text = new Text(composite, SWT.BORDER);
		if (isImport) {
			text.setEditable(false);
		}
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 150;
		text.setLayoutData(gd_text);
		TextUtils.createPlateNOAutoCompleteField(text);
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("优惠金额");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_1.widthHint = 150;
		text_1.setLayoutData(gd_text_1);
		
		Label label_7 = new Label(composite, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("优惠时间");
		
		text_4 = new Text(composite, SWT.BORDER);
		text_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("状      态");
		
		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setStatus(combo.getText().equals("启用"));
			}
		});
		combo.setItems(new String[] {"启用", "停用"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		combo.select(0);
		if (!model.getStatus()) {
			combo.select(1);
		}
		initDataBindings();
	}

	@Override
	public AddTempCarFreeWizard getWizard() {
		return (AddTempCarFreeWizard) super.getWizard();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue plateNoModelObserveValue = BeanProperties.value("plateNo").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, plateNoModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue freeMoneyModelObserveValue = BeanProperties.value("freeMoney").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, freeMoneyModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue freeMinuteModelObserveValue = BeanProperties.value("freeMinute").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, freeMinuteModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
