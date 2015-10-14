package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;


public class AddBlackUserWizard extends Wizard implements AbstractWizard{
	SingleCarparkBlackUser model;
	public AddBlackUserWizard(SingleCarparkBlackUser model) {
		setWindowTitle("添加黑名单");
		this.model=model;
	}

	@Override
	public void addPages() {
		addPage(new AddBlackUserWizardPage(model));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Object getModel() {
		
		return model;
	}

}
