package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;


public class AddUserWizard extends Wizard implements AbstractWizard{
	AddUserModel model;
	public AddUserWizard(AddUserModel model) {
		this.model=model;
		setWindowTitle("添加固定用户");
	}

	@Override
	public void addPages() {
		addPage(new AddUserWizardPage(model));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Object getModel() {
		
		return model;
	}

}
