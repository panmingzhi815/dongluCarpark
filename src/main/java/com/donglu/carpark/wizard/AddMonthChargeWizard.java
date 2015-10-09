package com.donglu.carpark.wizard;

import com.dongluhitec.card.ui.carpark.pay.storein.wizard.NewCarparkStoreInHistoryModel;
import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.wizard.model.AddMonthChargeModel;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.ui.cache.MonthlyCarparkChargeInfo;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class AddMonthChargeWizard extends Wizard implements AbstractWizard {

	private AddMonthChargeModel model;
	
	public AddMonthChargeWizard(AddMonthChargeModel model) {
		this.model = model;
		setWindowTitle("添加停车场");
	}

	@Override
	public void addPages() {
		this.addPage(new AddMonthChargeWizardPage(model));
	}

	@Override
	public AddMonthChargeModel getModel() {
		return model;
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}

}
