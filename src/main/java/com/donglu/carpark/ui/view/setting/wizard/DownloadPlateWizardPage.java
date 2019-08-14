package com.donglu.carpark.ui.view.setting.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.domain.db.singlecarpark.CameraTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;


import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;

public class DownloadPlateWizardPage extends WizardPage {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	private DownloadPlateModel model;
	private Text text;
	private ComboViewer comboViewer;
	private ListViewer listViewer;
	private ComboViewer comboViewer_1;
	/**
	 * Create the wizard.
	 * @param model 
	 * @param map 
	 */
	public DownloadPlateWizardPage(DownloadPlateModel model) {
		super("wizardPage");
		this.model=model;
		setTitle("将车牌信息下载到设备的白名单中");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Group group = new Group(composite, SWT.NONE);
		group.setText("操作");
		group.setLayout(new GridLayout(10, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("ip");
		
		text = new Text(group, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode==StrUtil.BIG_KEY_ENTER) {
					add();
				}
			}
		});
		text.setText("192.168.1.233");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 127;
		text.setLayoutData(gd_text);
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("类型");
		
		comboViewer = new ComboViewer(group, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(CameraTypeEnum.values());
		combo.select(0);
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("停车场");
		
		comboViewer_1 = new ComboViewer(group, SWT.READ_ONLY);
		Combo combo_1 = comboViewer_1.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.setToolTipText("添加一个新的设备");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				add();
			}
		});
		btnNewButton.setText("添加");
		
		Button btnNewButton_1 = new Button(group, SWT.NONE);
		btnNewButton_1.setToolTipText("删除选中设备");
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.removeInfo(model.getListSelected());
			}
		});
		btnNewButton_1.setText("删除");
		
		Button button = new Button(group, SWT.NONE);
		button.setToolTipText("下载车牌数据到选中的设备");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizard().downloadPlate();
			}
		});
		button.setText("下载");
		
		Button button_1 = new Button(group, SWT.NONE);
		button_1.setVisible(false);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getWizard().readPlateSize();
				listViewer.refresh();
			}
		});
		button_1.setText("读白名单数量");
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		listViewer = new ListViewer(composite_1, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		List list = listViewer.getList();
		m_bindingContext = initDataBindings();
		getWizard().init();
		setPageComplete(false);
	}
	@Override
	public DownloadPlateWizard getWizard() {
		return (DownloadPlateWizard) super.getWizard();
	}

	/**
	 * 
	 */
	public void add() {
		String ip = model.getIp();
		CameraTypeEnum type = model.getType();
		if (StrUtil.isEmpty(ip)||StrUtil.isEmpty(type)) {
			return;
		}
		DownloadDeviceInfo d=new DownloadDeviceInfo();
		d.setIp(ip);
		d.setType(type);
		d.setCarpark(model.getCarpark());
		model.addInfo(d);
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue ipModelObserveValue = BeanProperties.value("ip").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, ipModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue typeModelObserveValue = BeanProperties.value("type").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, typeModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), DownloadDeviceInfo.class, "labelString");
		listViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		listViewer.setContentProvider(listContentProvider);
		//
		IObservableList listModelObserveList = BeanProperties.list("list").observe(model);
		listViewer.setInput(listModelObserveList);
		//
		IObservableList observeMultiSelectionListViewer = ViewerProperties.multipleSelection().observe(listViewer);
		IObservableList listSelectedModelObserveList = BeanProperties.list("listSelected").observe(model);
		bindingContext.bindList(observeMultiSelectionListViewer, listSelectedModelObserveList, null, null);
		//
		ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		IObservableMap observeMap_1 = BeansObservables.observeMap(listContentProvider_1.getKnownElements(), SingleCarparkCarpark.class, "name");
		comboViewer_1.setLabelProvider(new ObservableMapLabelProvider(observeMap_1));
		comboViewer_1.setContentProvider(listContentProvider_1);
		//
		IObservableList listCarparkModelObserveList = BeanProperties.list("listCarpark").observe(model);
		comboViewer_1.setInput(listCarparkModelObserveList);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties.singleSelection().observe(comboViewer_1);
		IObservableValue carparkModelObserveValue = BeanProperties.value("carpark").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_1, carparkModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
