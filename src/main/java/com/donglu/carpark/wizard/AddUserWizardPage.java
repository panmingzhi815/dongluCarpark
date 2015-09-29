package com.donglu.carpark.wizard;

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
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;

public class AddUserWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private Text text;
	private Text text_1;
	private Text text_3;
	private Text text_4;
	AddUserModel model;
	private ComboViewer comboViewer_carpark;
	private Combo combo_carpark;
	private Combo combo;

	
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public AddUserWizardPage(AddUserModel model) {
		super("wizardPage");
		setTitle("添加固定用户");
		setDescription("添加固定用户");
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
		composite.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌");
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("用户名");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("用户类型");
		
		ComboViewer comboViewer = new ComboViewer(composite, SWT.NONE);
		combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[]{"普通"});
		combo.select(0);
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("住址");
		
		text_3 = new Text(composite, SWT.BORDER);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("车位");
		
		text_4 = new Text(composite, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("停车场");
		
		comboViewer_carpark = new ComboViewer(composite, SWT.NONE);
		combo_carpark = comboViewer_carpark.getCombo();
		combo_carpark.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SingleCarparkCarpark.class, "name");
		comboViewer_carpark.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer_carpark.setContentProvider(listContentProvider);
		//
		IObservableList allListModelObserveList = BeanProperties.list("allList").observe(model);
		comboViewer_carpark.setInput(allListModelObserveList);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue plateNoModelObserveValue = BeanProperties.value("plateNo").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, plateNoModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue nameModelObserveValue = BeanProperties.value("name").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, nameModelObserveValue, null, null);
		//
		IObservableValue observeTextComboObserveWidget = WidgetProperties.text().observe(combo);
		IObservableValue typeModelObserveValue = BeanProperties.value("type").observe(model);
		bindingContext.bindValue(observeTextComboObserveWidget, typeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue addressModelObserveValue = BeanProperties.value("address").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, addressModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue carparkNoModelObserveValue = BeanProperties.value("carparkNo").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, carparkNoModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_carpark = ViewerProperties.singleSelection().observe(comboViewer_carpark);
		IObservableValue carparkModelObserveValue = BeanProperties.value("carpark").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_carpark, carparkModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
