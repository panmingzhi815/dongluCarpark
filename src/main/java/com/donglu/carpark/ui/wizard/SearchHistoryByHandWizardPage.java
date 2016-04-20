package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.donglu.carpark.util.ImageUtils;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.core.databinding.DataBindingContext;

public class SearchHistoryByHandWizardPage extends WizardPage {
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	SearchErrorCarPresenter presenter;

	/**
	 * Create the wizard.
	 * @param searchErrorCarPresenter 
	 */
	public SearchHistoryByHandWizardPage(SearchErrorCarPresenter searchErrorCarPresenter) {
		super("wizardPage");
		setTitle("手动识别车牌");
		setDescription("请选择一个进场记录");
		this.presenter=searchErrorCarPresenter;
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
		presenter.getModel().setBigImg(ImageUtils.getImageByte(presenter.getModel().getSaveBigImg()));
		presenter.getModel().setSmallImg(ImageUtils.getImageByte(presenter.getModel().getSaveSmallImg()));
		
		m_bindingContext = initDataBindings();
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		
		return bindingContext;
	}
}
