package com.donglu.carpark.ui.view.card.wizard;

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
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
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

public class AddCardWizardPage extends WizardPage {
	private Text txt_serialNumber;
	SingleCarparkCard model;

	
	/**
	 * Create the wizard.
	 * @param model 
	 */
	public AddCardWizardPage(SingleCarparkCard model) {
		super("wizardPage");
		this.model=model;
		if (StrUtil.isEmpty(model.getId())) {
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
		label.setText("卡片内码");
		
		txt_serialNumber = new Text(composite, SWT.BORDER);
		txt_serialNumber.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				setPageComplete(checkSerialNumber(txt_serialNumber.getText()));
			}
			
		});
		txt_serialNumber.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_txt_serialNumber = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txt_serialNumber.widthHint = 180;
		txt_serialNumber.setLayoutData(gd_txt_serialNumber);
		TextUtils.createPlateNOAutoCompleteField(txt_serialNumber);
		initDataBindings();
	}

	protected boolean checkSerialNumber(String text) {
		int length = text.length();
		String s="([0-9]|[abcdefABCDEF]){"+length+"}";
		if (!text.matches(s)) {
			setErrorMessage("卡片内码格式不正确");
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	@Override
	public AddCardWizard getWizard() {
		
		return (AddCardWizard) super.getWizard();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTxt_serialNumberObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_serialNumber);
		IObservableValue serialNumberModelObserveValue = BeanProperties.value("serialNumber").observe(model);
		bindingContext.bindValue(observeTextTxt_serialNumberObserveWidget, serialNumberModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
