package com.donglu.carpark.ui.wizard;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import net.miginfocom.swt.MigLayout;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
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

import org.eclipse.core.databinding.observable.map.IObservableMap;

import com.dongluhitec.card.domain.db.singlecarpark.CameraTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceRoadTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.ScreenTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice.DeviceInOutTypeEnum;

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
	@SuppressWarnings("unused")
	private DataBindingContext bindingContext;
	private Composite serialComposite;
	private Label label_1;
	private Text text;
	private Label label_2;
	private Label lblip;
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
	private Label label_8;
	private Combo combo_2;
	private ComboViewer comboViewer_2;
	private Label label_3;
	private Text text_4;
	private Label label_9;
	private Combo combo_3;
	private ComboViewer comboViewer_3;
	private Label label_10;
	private Combo combo_4;
	private ComboViewer comboViewer_4;
	private Label lblNewLabel;
	private Combo combo_5;
	private ComboViewer comboViewer_5;
	private Label lblNewLabel_1;
	private Text text_5;
	
	private boolean controlTime=true;
	private Label label_11;
	private Text text_6;
	private Label label_12;
	private Text text_7;

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
	 * @wbp.parser.constructor
	 */
	public AddDeviceBasicPage(AddDeviceModel model,boolean controlTime) {
		super("wizardPage");
		this.controlTime = controlTime;
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
	@Override
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
		
		lblip = new Label(composite, SWT.NONE);
		lblip.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblip.setText("摄像机IP");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setText("192.168.1.139");
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_10 = new Label(composite, SWT.NONE);
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("摄像机类型");
		
		comboViewer_4 = new ComboViewer(composite, SWT.READ_ONLY);
		combo_4 = comboViewer_4.getCombo();
		combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_4.setContentProvider(new ArrayContentProvider());
		comboViewer_4.setLabelProvider(new LabelProvider());
		comboViewer_4.setInput(CameraTypeEnum.values());
		
		label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("控制器类型");

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
				text_3.setEditable(false);
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
				text_3.setEditable(true);
			}
		});
		radio_serial.setText("485");
		
		
		label_4 = new Label(composite, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("控制器连接");

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
		serialNameComboViewer.setInput(new String[]{"COM1","COM2","COM3","COM4","COM5","COM6","COM7","COM8","COM9","COM10",});

		address_stack.topControl = text_tcpip;
		radio_tcpip.setSelection(true);
        
        label_5 = new Label(composite, SWT.NONE);
        label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        label_5.setText("控制器地址");
		
		text_3 = new Text(composite, SWT.BORDER);
		if (model.getType().equals("tcp")) {
			text_3.setEditable(false);
		}
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_8 = new Label(composite, SWT.NONE);
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("语音音量");
		
		comboViewer_2 = new ComboViewer(composite, SWT.READ_ONLY);
		combo_2 = comboViewer_2.getCombo();
		GridData gd_combo_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo_2.widthHint = 78;
		combo_2.setLayoutData(gd_combo_2);
		comboViewer_2.setContentProvider(new ArrayContentProvider());
		comboViewer_2.setLabelProvider(new LabelProvider());
		comboViewer_2.setInput(new String[]{"0","1","2","3","4","5","6","7","8","9"});
		label_6 = new Label(composite, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("通道类型");
		
		comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(DeviceRoadTypeEnum.values());
		combo.select(0);
		
		lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("进出类型");
		
		comboViewer_5 = new ComboViewer(composite, SWT.READ_ONLY);
		combo_5 = comboViewer_5.getCombo();
		combo_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_5.setContentProvider(new ArrayContentProvider());
		comboViewer_5.setLabelProvider(new LabelProvider());
		comboViewer_5.setInput(DeviceInOutTypeEnum.values());
		
		label_7 = new Label(composite, SWT.NONE);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("所属停车场");
		
		comboViewer_1 = new ComboViewer(composite, SWT.READ_ONLY);
		combo_1 = comboViewer_1.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_3 = new Label(composite, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("显示屏内容");
		
		text_4 = new Text(composite, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_9 = new Label(composite, SWT.NONE);
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("显示屏类型");
		
		comboViewer_3 = new ComboViewer(composite, SWT.READ_ONLY);
		combo_3 = comboViewer_3.getCombo();
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		label_12 = new Label(composite, SWT.NONE);
		label_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_12.setText("车位显示屏");
		
		text_7 = new Text(composite, SWT.BORDER);
		text_7.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lblNewLabel_1 = new Label(composite, SWT.NONE);
		GridData gd_lblNewLabel_1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel_1.exclude = controlTime;
		lblNewLabel_1.setLayoutData(gd_lblNewLabel_1);
		lblNewLabel_1.setText("启用时段");
		
		text_5 = new Text(composite, SWT.BORDER);
		GridData gd_text_5 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_5.exclude = controlTime;
		text_5.setLayoutData(gd_text_5);
		
		label_11 = new Label(composite, SWT.NONE);
		GridData layoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		layoutData.exclude = controlTime;
		label_11.setLayoutData(layoutData);
		label_11.setText("节假日限制");
		
		text_6 = new Text(composite, SWT.BORDER);
		GridData layoutData2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		layoutData2.exclude = controlTime;
		text_6.setLayoutData(layoutData2);
		comboViewer_3.setContentProvider(new ArrayContentProvider());
		comboViewer_3.setLabelProvider(new LabelProvider());
		comboViewer_3.setInput(ScreenTypeEnum.values());
		if (model.getType().equals("tcp")) {
			address_stack.topControl = text_tcpip;
			address_stack_container.layout();
			radio_tcpip.setSelection(true);
			radio_serial.setSelection(false);
		}
		if (model.getType().equals("485")) {
			address_stack.topControl = serialComposite;
			address_stack_container.layout();
			radio_tcpip.setSelection(false);
			radio_serial.setSelection(true);
		}
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
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue roadTypeModelObserveValue = BeanProperties.value("deviceRoadType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, roadTypeModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SingleCarparkCarpark.class, "name");
		comboViewer_1.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer_1.setContentProvider(listContentProvider);
		//
		IObservableList listModelObserveList = BeanProperties.list("list").observe(model);
		comboViewer_1.setInput(listModelObserveList);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties.singleSelection().observe(comboViewer_1);
		IObservableValue carparkModelObserveValue = BeanProperties.value("carpark").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_1, carparkModelObserveValue, null, null);
		//
		IObservableValue observeTextCombo_serialNameObserveWidget = WidgetProperties.text().observe(combo_serialName);
		IObservableValue serialAddressModelObserveValue = BeanProperties.value("serialAddress").observe(model);
		bindingContext.bindValue(observeTextCombo_serialNameObserveWidget, serialAddressModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_2 = ViewerProperties.singleSelection().observe(comboViewer_2);
		IObservableValue voiceModelObserveValue = BeanProperties.value("voice").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_2, voiceModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue addressModelObserveValue = BeanProperties.value("address").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, addressModelObserveValue, null, null);
		//
		IObservableValue observeTextText_tcpipObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_tcpip);
		IObservableValue tcpAddressModelObserveValue = BeanProperties.value("tcpAddress").observe(model);
		bindingContext.bindValue(observeTextText_tcpipObserveWidget, tcpAddressModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue advertiseModelObserveValue = BeanProperties.value("advertise").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, advertiseModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_3 = ViewerProperties.singleSelection().observe(comboViewer_3);
		IObservableValue screenTypeModelObserveValue = BeanProperties.value("screenType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_3, screenTypeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_4 = ViewerProperties.singleSelection().observe(comboViewer_4);
		IObservableValue cameraTypeModelObserveValue = BeanProperties.value("cameraType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_4, cameraTypeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_5 = ViewerProperties.singleSelection().observe(comboViewer_5);
		IObservableValue inOutTypeModelObserveValue = BeanProperties.value("inOutType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_5, inOutTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_5ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_5);
		IObservableValue controlTimeModelObserveValue = BeanProperties.value("controlTime").observe(model);
		bindingContext.bindValue(observeTextText_5ObserveWidget, controlTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_6ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_6);
		IObservableValue holidayControlTimeModelObserveValue = BeanProperties.value("holidayControlTime").observe(model);
		bindingContext.bindValue(observeTextText_6ObserveWidget, holidayControlTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_7ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_7);
		IObservableValue positionAddressModelObserveValue = BeanProperties.value("positionAddress").observe(model);
		bindingContext.bindValue(observeTextText_7ObserveWidget, positionAddressModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
