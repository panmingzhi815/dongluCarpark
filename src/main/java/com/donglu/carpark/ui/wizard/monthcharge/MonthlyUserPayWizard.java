package com.donglu.carpark.ui.wizard.monthcharge;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;

import org.eclipse.jface.wizard.Wizard;

public class MonthlyUserPayWizard extends Wizard implements AbstractWizard{
	
	private MonthlyUserPayModel model;
	
	
	public MonthlyUserPayWizard(MonthlyUserPayModel model) {
		this.model = model;
	}

	@Override
	public void addPages() {
		addPage(new MonthlyUserPayBasicPage(model));
        setWindowTitle("固定月租用户缴费");
        getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public MonthlyUserPayModel getModel() {
		return model;
	}

}
