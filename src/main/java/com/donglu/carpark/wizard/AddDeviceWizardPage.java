package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ViewerProperties;

public class AddDeviceWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private Text txt_ip;
	private AddDeviceModel model;
	private Text txt_name;
	private Text text_1;
	private Text text_2;
	private Text text_linkAddress;
	private Combo combo_linkAddress;
	private Composite composite_linkAddress;
	private Combo combo;
	private Combo combo_1;
	private Combo combo_carpark;
	private ComboViewer comboViewer_3;
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public AddDeviceWizardPage(AddDeviceModel model) {
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
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("设备编号");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("设备名称");
		
		txt_name = new Text(composite, SWT.BORDER);
		GridData gd_txt_name = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_name.widthHint = 139;
		txt_name.setLayoutData(gd_txt_name);
		txt_name.setText("");
		Label lblIp = new Label(composite, SWT.NONE);
		lblIp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIp.setText("抓拍ip");
		
		txt_ip = new Text(composite, SWT.BORDER);
		txt_ip.setText("192.168.1.139");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 101;
		txt_ip.setLayoutData(gd_text);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("设备类型");
		
		ComboViewer comboViewer = new ComboViewer(composite, SWT.NONE);
		combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = combo.getSelectionIndex();
				System.out.println(selectionIndex);
				GridData layoutData = (GridData) combo_linkAddress.getLayoutData();
				GridData layoutData2 = (GridData) text_linkAddress.getLayoutData();
				if (selectionIndex==0) {
					layoutData.exclude=false;
					layoutData2.exclude=false;
					combo_linkAddress.setLayoutData(layoutData);
					text_linkAddress.setLayoutData(layoutData2);
					composite_linkAddress.layout();
				}else if (selectionIndex==1) {
					layoutData.exclude=false;
					layoutData2.exclude=true;
					combo_linkAddress.setLayoutData(layoutData);
					text_linkAddress.setLayoutData(layoutData2);
					composite_linkAddress.layout();
				}
			}
		});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[]{"TCP","485"});
		combo.select(0);
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("连接类型");
		
		composite_linkAddress = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		composite_linkAddress.setLayout(gl_composite_1);
		composite_linkAddress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ComboViewer comboViewer_1 = new ComboViewer(composite_linkAddress, SWT.NONE);
		combo_linkAddress = comboViewer_1.getCombo();
		GridData gd_combo_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		if (model.getType()==null||model.getType().equals("TCP")) {
			gd_combo_1.exclude = true;
		}
		
		combo_linkAddress.setLayoutData(gd_combo_1);
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		comboViewer_1.setInput(new String[]{"DOM1","DOM2"});
		combo_linkAddress.select(0);
		
		text_linkAddress = new Text(composite_linkAddress, SWT.BORDER);
		GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		if (model.getType()!=null&&model.getType().equals("485")) {
			gd_text_3.exclude = true;
		}
		text_linkAddress.setLayoutData(gd_text_3);
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("语音地址");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("通道类型");
		
		
		ComboViewer comboViewer_2 = new ComboViewer(composite, SWT.NONE);
		combo_1 = comboViewer_2.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_2.setContentProvider(new ArrayContentProvider());
		comboViewer_2.setLabelProvider(new LabelProvider());
		comboViewer_2.setInput(new String[]{"固定车通道","临时车通道","混合车通道"});
		combo_1.select(0);
		
		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("停车场");
		
		comboViewer_3 = new ComboViewer(composite, SWT.NONE);
		combo_carpark = comboViewer_3.getCombo();
		combo_carpark.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m_bindingContext = initDataBindings();
		
	}

	public Text getText() {
		return txt_ip;
	}

	public void setText(Text text) {
		this.txt_ip = text;
	}

	public SingleCarparkDevice getModel() {
		return model;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue identifireModelObserveValue = BeanProperties.value("identifire").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, identifireModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_nameObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_name);
		IObservableValue nameModelObserveValue = BeanProperties.value("name").observe(model);
		bindingContext.bindValue(observeTextTxt_nameObserveWidget, nameModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_ipObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_ip);
		IObservableValue ipModelObserveValue = BeanProperties.value("ip").observe(model);
		bindingContext.bindValue(observeTextTxt_ipObserveWidget, ipModelObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue addressModelObserveValue = BeanProperties.value("address").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, addressModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SingleCarparkCarpark.class, "name");
		comboViewer_3.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer_3.setContentProvider(listContentProvider);
		//
		IObservableList listModelObserveList = BeanProperties.list("list").observe(model);
		comboViewer_3.setInput(listModelObserveList);
		//
		IObservableValue observeTextComboObserveWidget = WidgetProperties.text().observe(combo);
		IObservableValue typeModelObserveValue = BeanProperties.value("type").observe(model);
		bindingContext.bindValue(observeTextComboObserveWidget, typeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_3 = ViewerProperties.singleSelection().observe(comboViewer_3);
		IObservableValue carparkModelObserveValue = BeanProperties.value("carpark").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_3, carparkModelObserveValue, null, null);
		//
		IObservableValue observeTextCombo_1ObserveWidget = WidgetProperties.text().observe(combo_1);
		IObservableValue roadTypeModelObserveValue = BeanProperties.value("roadType").observe(model);
		bindingContext.bindValue(observeTextCombo_1ObserveWidget, roadTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextCombo_linkAddressObserveWidget = WidgetProperties.text().observe(combo_linkAddress);
		IObservableValue linkAddressModelObserveValue = BeanProperties.value("linkAddress").observe(model);
		bindingContext.bindValue(observeTextCombo_linkAddressObserveWidget, linkAddressModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
