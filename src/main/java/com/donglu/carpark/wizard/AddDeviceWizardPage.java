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
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AddDeviceWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	private Text txt_ip;
	AddDeviceModel model;
	private Text txt_name;
	private Text text_1;
	private Text text_2;
	private Text text_3;
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
		txt_name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txt_name.setText("");
		Label lblIp = new Label(composite, SWT.NONE);
		lblIp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIp.setText("ip");
		
		txt_ip = new Text(composite, SWT.BORDER);
		txt_ip.setText("192.168.1.139");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 101;
		txt_ip.setLayoutData(gd_text);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("设备类型");
		
		ComboViewer comboViewer = new ComboViewer(composite, SWT.NONE);
		final Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(combo.getSelectionIndex());
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
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(1, false);
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ComboViewer comboViewer_1 = new ComboViewer(composite_1, SWT.NONE);
		Combo combo_1 = comboViewer_1.getCombo();
		GridData gd_combo_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo_1.exclude = true;
		combo_1.setLayoutData(gd_combo_1);
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		comboViewer_1.setInput(new String[]{"TCP","485"});
		combo_1.select(0);
		
		text_3 = new Text(composite_1, SWT.BORDER);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("设备地址");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("通道类型");
		
		
		ComboViewer comboViewer_2 = new ComboViewer(composite, SWT.NONE);
		Combo combo_2 = comboViewer_2.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_2.setContentProvider(new ArrayContentProvider());
		comboViewer_2.setLabelProvider(new LabelProvider());
		comboViewer_2.setInput(new String[]{"固定车通道","临时车通道","混合车通道"});
		combo_2.select(0);
		
		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("停车场");
		
		ComboViewer comboViewer_3 = new ComboViewer(composite, SWT.NONE);
		Combo combo_3 = comboViewer_3.getCombo();
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_3.setContentProvider(new ArrayContentProvider());
		comboViewer_3.setLabelProvider(new LabelProvider());
		comboViewer_3.setInput(new String[]{"TCP","485"});
		combo_3.select(0);
		
		m_bindingContext = initDataBindings();
	}

	public Text getText() {
		return txt_ip;
	}

	public void setText(Text text) {
		this.txt_ip = text;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_name);
		IObservableValue nameModelObserveValue = PojoProperties.value("name").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, nameModelObserveValue, null, null);
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_ip);
		IObservableValue linkModelObserveValue = PojoProperties.value("link").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, linkModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
