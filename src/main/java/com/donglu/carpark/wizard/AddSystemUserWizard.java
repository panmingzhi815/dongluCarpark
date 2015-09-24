package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;


public class AddSystemUserWizard extends Wizard implements AbstractWizard{
	Object object;
	public AddSystemUserWizard(Object object) {
		this.object=object;
		setWindowTitle("添加系统用户");
	}

	@Override
	public void addPages() {
		addPage(new AddSystemUserWizardPage());
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Object getModel() {
		
		return object;
	}

}
