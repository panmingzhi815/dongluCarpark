package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;


public class AddDeviceWizard extends Wizard implements AbstractWizard{
	Object object;
	AddDeviceWizardPage addDeviceWizardPage;
	
	public AddDeviceWizard(Object object) {
		this.object=object;
		addDeviceWizardPage=new AddDeviceWizardPage();
		setWindowTitle("添加固定用户");
	}

	@Override
	public void addPages() {
		addPage(addDeviceWizardPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public Object getModel() {
		
		return addDeviceWizardPage.getText().getText();
	}

}
