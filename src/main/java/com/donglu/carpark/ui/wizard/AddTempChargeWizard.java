package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;

public class AddTempChargeWizard extends Wizard {

	public AddTempChargeWizard() {
		setWindowTitle("添加收费");
	}

	@Override
	public void addPages() {
		addPage(new AddTempChargeBasicPage());
		getShell().setSize(610, 650);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
