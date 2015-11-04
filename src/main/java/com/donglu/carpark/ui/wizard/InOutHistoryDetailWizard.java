package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;

public class InOutHistoryDetailWizard extends Wizard implements AbstractWizard{
	private SingleCarparkInOutHistory model;
	private InOutHistoryDetailWizardPage page;
	public InOutHistoryDetailWizard(SingleCarparkInOutHistory model) {
		setWindowTitle("查看进出记录");
		this.model=model;
	}

	@Override
	public void addPages() {
		page = new InOutHistoryDetailWizardPage(model);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return false;
	}

	@Override
	public Object getModel() {
		
		return model;
	}
}
