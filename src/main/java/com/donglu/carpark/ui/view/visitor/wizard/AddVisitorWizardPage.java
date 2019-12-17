package com.donglu.carpark.ui.view.visitor.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.databinding.DataBindingContext;

import com.donglu.carpark.util.TextUtils;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;

public class AddVisitorWizardPage extends WizardPage {
	private Text text;
	private Text text_1;
	private Text txt_carNO;
	private AddVisitorModel model;
	private Text text_2;
	private Text text_4;
	private ComboViewer comboViewer;
	private DateChooserCombo dateChooserCombo;
	private DateTime dateTime;
	private Button button;
	private DateChooserCombo dateChooserCombo_startTime;
	private DateTime dateTime_startTime;

	
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public AddVisitorWizardPage(AddVisitorModel model) {
		super("wizardPage");
		this.model=model;
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
		
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 150;
		text.setLayoutData(gd_text);
		TextUtils.createPlateNOAutoCompleteField(text);
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("用户名字");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_1.widthHint = 150;
		text_1.setLayoutData(gd_text_1);
		
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
		label_4.setText("次数限制");
		
		txt_carNO = new Text(composite, SWT.BORDER);
		txt_carNO.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txt_carNO.selectAll();
			}
		});
		txt_carNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_4 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_4.widthHint = 150;
		txt_carNO.setLayoutData(gd_text_4);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setText("起始时间");
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.marginHeight = 0;
		gl_composite_2.marginWidth = 0;
		gl_composite_2.horizontalSpacing = 0;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		dateChooserCombo_startTime = new DateChooserCombo(composite_2, SWT.BORDER);
		dateChooserCombo_startTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo_startTime.setValue(model.getStartTime());
		
		dateTime_startTime = new DateTime(composite_2, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime_startTime.setMinutes(0);
		dateTime_startTime.setHours(0);
		dateTime_startTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("截止时间");
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.horizontalSpacing = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		dateChooserCombo = new DateChooserCombo(composite_1, SWT.BORDER);
		dateChooserCombo.setValue(model.getValidTo());
		dateChooserCombo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		dateTime = new DateTime(composite_1, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime.setHours(23);
		dateTime.setMinutes(59);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("停 车 场");
		
		comboViewer = new ComboViewer(composite, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Label label_6 = new Label(composite, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("备      注");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_2.heightHint = 60;
		text_2.setLayoutData(gd_text_2);
		new Label(composite, SWT.NONE);
		
		button = new Button(composite, SWT.CHECK);
		button.setText("出场收费");
		initDataBindings();
		if (model.getValidTo()!=null) {
			Date validTo = model.getValidTo();
			Calendar c = Calendar.getInstance();
			c.setTime(validTo);
			int hours = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			dateTime.setHours(hours);
			dateTime.setMinutes(minute);
		}
		if (model.getStartTime()!=null) {
			Date validTo = model.getStartTime();
			Calendar c = Calendar.getInstance();
			c.setTime(validTo);
			int hours = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			dateTime_startTime.setHours(hours);
			dateTime_startTime.setMinutes(minute);
		}
	}
	
	public Date getValidTo(){
		Date date = dateChooserCombo.getValue();
		if (date!=null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.HOUR_OF_DAY, dateTime.getHours());
			c.set(Calendar.MINUTE, dateTime.getMinutes());
			return c.getTime();
		}
		return date;
	}
	public Date getStartTime() {
		Date date = dateChooserCombo_startTime.getValue();
		if (date!=null) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.HOUR_OF_DAY, dateTime_startTime.getHours());
			c.set(Calendar.MINUTE, dateTime_startTime.getMinutes());
			return c.getTime();
		}
		return date;
	}

	@Override
	public AddVisitorWizard getWizard() {
		return (AddVisitorWizard) super.getWizard();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue plateNOModelObserveValue = BeanProperties.value("plateNO").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget, plateNOModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue nameModelObserveValue = BeanProperties.value("name").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, nameModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue telephoneModelObserveValue = BeanProperties.value("telephone").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, telephoneModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_carNOObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_carNO);
		IObservableValue allInModelObserveValue = BeanProperties.value("allIn").observe(model);
		bindingContext.bindValue(observeTextTxt_carNOObserveWidget, allInModelObserveValue, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue remarkModelObserveValue = BeanProperties.value("remark").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, remarkModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SingleCarparkCarpark.class, "labelString");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer.setContentProvider(listContentProvider);
		//
		IObservableList listCarparkModelObserveList = BeanProperties.list("listCarpark").observe(model);
		comboViewer.setInput(listCarparkModelObserveList);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue carparkModelObserveValue = BeanProperties.value("carpark").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, carparkModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButtonObserveWidget = WidgetProperties.selection().observe(button);
		IObservableValue outNeedChargeModelObserveValue = BeanProperties.value("outNeedCharge").observe(model);
		bindingContext.bindValue(observeSelectionButtonObserveWidget, outNeedChargeModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
