package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;

public class SearchHistoryByHandWizardPage extends WizardPage {
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
