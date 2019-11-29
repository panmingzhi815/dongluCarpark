package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;


public class PresenterWizard extends Wizard implements AbstractWizard{
	private Presenter presenter;
	public PresenterWizard(Presenter presenter) {
		setWindowTitle("人工查找");
		this.presenter=presenter;
	}

	@Override
	public void addPages() {
		addPage(new PresenterWizardPage(presenter));
		getShell().setSize(900, 600);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public Object getModel() {
		
		return "1";
	}

}
