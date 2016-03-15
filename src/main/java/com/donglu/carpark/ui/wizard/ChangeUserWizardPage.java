package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.core.databinding.DataBindingContext;
import com.donglu.carpark.ui.wizard.model.ChangeUserModel;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ViewerProperties;

public class ChangeUserWizardPage extends WizardPage {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	private Text text_1;
	ChangeUserModel model;
	private ComboViewer comboViewer;

	
	/**
	 * Create the wizard.
	 * @param model2 
	 */
	public ChangeUserWizardPage(ChangeUserModel model2) {
		super("wizardPage");
		setDescription("输入用户名，密码进行换班");
		this.model=model2;
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
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("用户名称");
		
		comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("用户密码");
		
		text_1 = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_1.widthHint = 150;
		text_1.setLayoutData(gd_text_1);
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue pwdModelObserveValue = BeanProperties.value("pwd").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, pwdModelObserveValue, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider.getKnownElements(), SingleCarparkSystemUser.class, "userName");
		comboViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		comboViewer.setContentProvider(listContentProvider);
		//
		IObservableList allSystemUserListModelObserveList = BeanProperties.list("allSystemUserList").observe(model);
		comboViewer.setInput(allSystemUserListModelObserveList);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue systemUserModelObserveValue = BeanProperties.value("systemUser").observe(model);
		bindingContext.bindValue(observeSingleSelectionComboViewer, systemUserModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
