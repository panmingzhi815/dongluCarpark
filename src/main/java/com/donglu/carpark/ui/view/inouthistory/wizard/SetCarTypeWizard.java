package com.donglu.carpark.ui.view.inouthistory.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkPlateCarType;

public class SetCarTypeWizard extends Wizard implements AbstractWizard {
	private SetCarTypeModel model;
	
	public SetCarTypeWizard(SetCarTypeModel model) {
		this.model = model;
	}

	@Override
	public void addPages() {
		addPage(new SetCarTypeWizardPage(model));
	}

	@Override
	public Object getModel() {
		return model;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
