package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;

public class ClientConfigWizard extends Wizard implements AbstractWizard {
	
	private ClientConfigWizardPage page;

	@Override
	public void addPages() {
		page = new ClientConfigWizardPage();
		addPage(page);
	}
	@Override
	public Object getModel() {
		return null;
	}

	@Override
	public boolean performFinish() {
		page.save();
		return true;
	}

}
