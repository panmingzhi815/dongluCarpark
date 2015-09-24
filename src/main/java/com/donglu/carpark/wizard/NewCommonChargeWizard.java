package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

public class NewCommonChargeWizard extends Wizard {

	public NewCommonChargeWizard() {
		setWindowTitle("添加收费");
	}

	@Override
	public void addPages() {
		addPage(new NewCommonChargeBasicPage());
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
