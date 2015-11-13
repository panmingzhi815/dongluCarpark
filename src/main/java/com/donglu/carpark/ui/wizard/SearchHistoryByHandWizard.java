package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.ui.view.SearchErrorCarPresenter;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;


public class SearchHistoryByHandWizard extends Wizard implements AbstractWizard{
	private SearchErrorCarPresenter searchErrorCarPresenter;
	public SearchHistoryByHandWizard(SearchErrorCarPresenter searchErrorCarPresenter) {
		setWindowTitle("添加黑名单");
		this.searchErrorCarPresenter=searchErrorCarPresenter;
	}

	@Override
	public void addPages() {
		addPage(new SearchHistoryByHandWizardPage(searchErrorCarPresenter));
		getShell().setSize(900, 600);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Object getModel() {
		
		return "1";
	}

}
