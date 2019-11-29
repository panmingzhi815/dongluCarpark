package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.core.databinding.DataBindingContext;

public class PresenterWizardPage extends WizardPage {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	Presenter presenter;

	/**
	 * Create the wizard.
	 * @param presenter 
	 */
	public PresenterWizardPage(Presenter presenter) {
		super("wizardPage");
		setTitle("手动识别车牌");
		setDescription("请选择一个进场记录");
		this.presenter=presenter;
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
		presenter.go(container);
		
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		
		return bindingContext;
	}
}
