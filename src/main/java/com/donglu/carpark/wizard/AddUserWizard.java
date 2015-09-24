package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;


public class AddUserWizard extends Wizard implements AbstractWizard{
	Object object;
	public AddUserWizard(Object object) {
		this.object=object;
		setWindowTitle("添加固定用户");
	}

	@Override
	public void addPages() {
		addPage(new AddUserWizardPage());
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Object getModel() {
		
		return object;
	}

}
