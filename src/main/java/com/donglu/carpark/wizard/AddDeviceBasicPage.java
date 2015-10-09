package com.donglu.carpark.wizard;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.LinkProtocolEnum;
import com.dongluhitec.card.domain.LinkTypeEnum;
import com.dongluhitec.card.domain.db.Link;
import com.dongluhitec.card.domain.db.LinkStyleEnum;
import com.dongluhitec.card.ui.hardware.linkwizard.LinkWizardModel;
import com.google.common.base.Strings;
import net.miginfocom.swt.MigLayout;

import org.apache.mina.transport.serial.SerialAddress;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.util.Arrays;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;

public class AddDeviceBasicPage extends WizardPage {

	private AddDeviceModel model;
	private Text text_tcpip;
	private Button radio_tcpip;
	private ComboViewer serialNameComboViewer;
	private Button radio_serial;

	private StackLayout address_stack;
	private Combo combo_serialName;

	private Composite address_stack_container;
	private Composite container;
	private DataBindingContext bindingContext;
	private Composite serialComposite;
	private Label label_1;
	private Text text;
	private Label label_2;
	private Label label_3;
	private Label label;
	private Label label_4;
	private Label label_5;
	private Label label_6;
	private Label label_7;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Combo combo;
	private ComboViewer comboViewer;
	private Combo combo_1;
	private ComboViewer comboViewer_1;

	/**
	 * Create the wizard.
	 *
	 * @param model
	 */
	public AddDeviceBasicPage(AddDeviceModel model) {
		super("wizardPage");
		setTitle("添加设备");
		setDescription("请输入设备的相关信息");
		setImageDescriptor(JFaceUtil.getImageDescriptor("link_72"));
		this.model=model;
	}

	/**
	 * Create contents of the wizard.
	 *
	 * @param parent
	 */
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		setControl(container);

		container.setLayout(new MigLayout("", "[grow]", "[grow]"));

		Composite composite = new Composite(container, SWT.BORDER|SWT.CENTER|SWT.VIRTUAL);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 15;
		composite.setLayout(gridLayout);
		composite.setLayoutData("align center");
		
		label_1 = new Label(composite, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("设备编号");
		
		text = new Text(composite, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 133;
		text.setLayoutData(gd_text);
		
		label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("设备名称");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setText("");
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_3 = new Label(composite, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("抓拍ip");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setText("192.168.1.139");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label = new Label(composite, SWT.NONE);
		label.setText("设备类型");

		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		radio_tcpip = new Button(composite_1, SWT.RADIO);
		radio_tcpip.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				address_stack.topControl = text_tcpip;
				model.setType("tcp");
//				model.setTcpAddress("127.0.0.1:10001");
				address_stack_container.layout();
//				bindingContext.updateModels();
				model.setLinkAddress(model.getTcpAddress());
			}
		});
		radio_tcpip.setText("TCP");

		radio_serial = new Button(composite_1, SWT.RADIO);
		radio_serial.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				address_stack.topControl = serialComposite;
				model.setType("485");
				address_stack_container.layout();
//				bindingContext.updateTargets();
			}
		});
		radio_serial.setText("485");
		model.setType("tcp");
		
		label_4 = new Label(composite, SWT.NONE);
		label_4.setText("连接类型");

		address_stack_container = new Composite(composite, SWT.NONE);
		address_stack = new StackLayout();
		address_stack_container.setLayout(address_stack);
		address_stack_container.setLayoutData(new GridData(SWT.FILL,
				SWT.CENTER, true, false, 1, 1));

		serialComposite = new Composite(address_stack_container, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		serialComposite.setLayout(layout);

		serialNameComboViewer = new ComboViewer(serialComposite, SWT.NONE);
		combo_serialName = serialNameComboViewer.getCombo();
		GridData serialNameGridData = new GridData();
		serialNameGridData.widthHint = 40;
		combo_serialName.setLayoutData(serialNameGridData);
		text_tcpip = new Text(address_stack_container, SWT.BORDER);
		serialNameComboViewer.setContentProvider(new ArrayContentProvider());
		serialNameComboViewer.setLabelProvider(new LabelProvider());
		serialNameComboViewer.setInput(new String[]{"DOM1","DOM2","DOM3","DOM4","DOM5","DOM6","DOM7","DOM8","DOM9","DOM10",});

		address_stack.topControl = text_tcpip;
		radio_tcpip.setSelection(true);
        
        label_5 = new Label(composite, SWT.NONE);
        label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_5.setText("语音地址");
		
		text_3 = new Text(composite, SWT.BORDER);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_6 = new Label(composite, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("通道类型");
		
		comboViewer = new ComboViewer(composite, SWT.NONE);
		combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[]{"固定车通道","临时车通道","混合车通道"});
		combo.select(0);
		
		label_7 = new Label(composite, SWT.NONE);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("停车场");
		
		comboViewer_1 = new ComboViewer(composite, SWT.NONE);
		combo_1 = comboViewer_1.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		bindingContext = initDataBindings();

	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue identifireModelObserveValue = BeanProperties.value("identifire").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, identifireModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue nameModelObserveValue = BeanProperties.value("name").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, nameModelObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue ipModelObserveValue = BeanProperties.value("ip").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, ipModelObserveValue, null, null);
		//
		IObservableValue observeTextText_tcpipObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_tcpip);
		IObservableValue tcpAddressModelObserveValue = BeanProperties.value("tcpAddress").observe(model);
		bindingContext.bindValue(observeTextText_tcpipObserveWidget, tcpAddressModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionSerialNameComboViewer = ViewerProperties.singleSelection().observe(serialNameComboViewer);
		IObservableValue serialAddressModelObserveValue = BeanProperties.value("serialAddress").observe(model);
		bindingContext.bindValue(observeSingleSelectionSerialNameComboViewer, serialAddressModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue addressModelObserveValue = BeanProperties.value("address").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, addressModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue roadTypeModelObserveValue = BeanProperties.value("roadType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, roadTypeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties.singleSelection().observe(comboViewer_1);
		IObservableValue carparkModelObserveValue = BeanProperties.value("carpark").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_1, carparkModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SingleCarparkCarpark.class, "name");
		comboViewer_1.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer_1.setContentProvider(listContentProvider);
		//
		IObservableList listModelObserveList = BeanProperties.list("list").observe(model);
		comboViewer_1.setInput(listModelObserveList);
		//
		return bindingContext;
	}
}
