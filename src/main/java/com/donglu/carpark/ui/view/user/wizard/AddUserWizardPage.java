package com.donglu.carpark.ui.view.user.wizard;

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
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;

import com.donglu.carpark.util.TextUtils;
import com.dongluhitec.card.domain.db.singlecarpark.CarTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser.CarparkSlotTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser.UserType;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AddUserWizardPage extends WizardPage {
	private Text text;
	private Text text_1;
	private Text text_3;
	private Text txt_carNO;
	AddUserModel model;
	private ComboViewer comboViewer_carpark;
	private Combo combo_carpark;
	private Combo combo;
	private ComboViewer comboViewer;
	private Text text_2;
	private ComboViewer comboViewer_1;
	private Text text_4;
	private Text text_5;
	private ComboViewer comboViewer_2;
	private Text text_6;

	
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public AddUserWizardPage(AddUserModel model) {
		super("wizardPage");
		this.model=model;
		if (StrUtil.isEmpty(model.getPlateNo())) {
			setDescription("添加固定用户");
		}else{
			setDescription("修改固定用户");
		}
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		if (StrUtil.isEmpty(model.getPlateNo())) {
			setPageComplete(false);
		}
		setControl(container);
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.verticalSpacing = 0;
		gl_container.marginWidth = 0;
		gl_container.marginHeight = 0;
		gl_container.horizontalSpacing = 0;
		container.setLayout(gl_container);
		
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
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				setPageComplete(getWizard().check());
			}
			
		});
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 150;
		text.setLayoutData(gd_text);
		TextUtils.createPlateNOAutoCompleteField(text);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("车位编号");
		
		text_5 = new Text(composite, SWT.BORDER);
		text_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("用户名字");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setTextLimit(30);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_1.widthHint = 150;
		text_1.setLayoutData(gd_text_1);
		text_1.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				setPageComplete(getWizard().check());
			}
			
		});
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("证件号码");
		
		text_6 = new Text(composite, SWT.BORDER);
		text_6.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("用户类型");
		
		comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo.widthHint = 150;
		combo.setLayoutData(gd_combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(UserType.values());
		combo.select(0);
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("用户住址");
		
		text_3 = new Text(composite, SWT.BORDER);
		text_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_3.widthHint = 150;
		text_3.setLayoutData(gd_text_3);
		
		Label label_7 = new Label(composite, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("用户电话");
		
		text_4 = new Text(composite, SWT.BORDER);
		text_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_4 = new Label(composite, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("车位数量");
		
		txt_carNO = new Text(composite, SWT.BORDER);
		txt_carNO.addKeyListener(new KeyAdapter() {
			String txt="0";
			@Override
			public void keyReleased(KeyEvent e) {
				String text2 = txt_carNO.getText();
				try {
					Integer valueOf = Integer.valueOf(text2);
					
					if (valueOf<=model.getTotalSlot()) {
						txt=text2;
					}else{
						setTxt();
					}
				} catch (NumberFormatException e1) {
					setTxt();
				}
			}
			/**
			 * 
			 */
			private void setTxt() {
				txt_carNO.setText("");
				txt_carNO.append(txt);
			}
		});
		txt_carNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_4 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_4.widthHint = 150;
		txt_carNO.setLayoutData(gd_text_4);
		
		Label label_8 = new Label(composite, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("车位类型");
		
		comboViewer_2 = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo_2 = comboViewer_2.getCombo();
		combo_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_2.setContentProvider(new ArrayContentProvider());
		comboViewer_2.setLabelProvider(new LabelProvider());
		comboViewer_2.setInput(CarparkSlotTypeEnum.values());
		
		Label label_5 = new Label(composite, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("停 车 场");
		
		comboViewer_carpark = new ComboViewer(composite, SWT.READ_ONLY);
		combo_carpark = comboViewer_carpark.getCombo();
		combo_carpark.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				getWizard().loadSlot();
			}
		});
		combo_carpark.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo_carpark = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo_carpark.widthHint = 150;
		combo_carpark.setLayoutData(gd_combo_carpark);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("车辆类型");
		
		comboViewer_1 = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo_1 = comboViewer_1.getCombo();
		combo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		comboViewer_1.setInput(CarTypeEnum.values());
		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("备      注");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_2.heightHint = 60;
		text_2.setLayoutData(gd_text_2);
		initDataBindings();
	}

	@Override
	public AddUserWizard getWizard() {
		
		return (AddUserWizard) super.getWizard();
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
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_3);
		IObservableValue addressModelObserveValue = BeanProperties.value("address").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, addressModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_carNO);
		IObservableValue carparkNoModelObserveValue = BeanProperties.value("carparkNo").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, carparkNoModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_carpark = ViewerProperties.singleSelection().observe(comboViewer_carpark);
		IObservableValue carparkModelObserveValue = BeanProperties.value("carpark").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_carpark, carparkModelObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue remarkModelObserveValue = BeanProperties.value("remark").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, remarkModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties.singleSelection().observe(comboViewer_1);
		IObservableValue carTypeModelObserveValue = BeanProperties.value("carType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_1, carTypeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_2 = ViewerProperties.singleSelection().observe(comboViewer_2);
		IObservableValue carparkSlotTypeModelObserveValue = BeanProperties.value("carparkSlotType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer_2, carparkSlotTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget_1 = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue telephoneModelObserveValue = BeanProperties.value("telephone").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget_1, telephoneModelObserveValue, null, null);
		//
		IObservableValue observeTextText_5ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_5);
		IObservableValue parkingSpaceModelObserveValue = BeanProperties.value("parkingSpace").observe(model);
		bindingContext.bindValue(observeTextText_5ObserveWidget, parkingSpaceModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue userTypeModelObserveValue = BeanProperties.value("userType").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, userTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_6ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_6);
		IObservableValue idCardModelObserveValue = BeanProperties.value("idCard").observe(model);
		bindingContext.bindValue(observeTextText_6ObserveWidget, idCardModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
