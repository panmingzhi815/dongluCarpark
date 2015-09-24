package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class AddCarparkWizard extends Wizard {
	
	public AddCarparkWizard() {
		setWindowTitle("添加停车场");
	}

	@Override
	public void addPages() {
		addPage(new AddCarparkWizardPage());
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
