package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;


public class AddBlackUserWizard extends Wizard implements AbstractWizard{
	Object object;
	public AddBlackUserWizard(Object object) {
		this.object=object;
		setWindowTitle("添加黑名单");
	}

	@Override
	public void addPages() {
		addPage(new AddBlackUserWizardPage());
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Object getModel() {
		
		return object;
	}

}
