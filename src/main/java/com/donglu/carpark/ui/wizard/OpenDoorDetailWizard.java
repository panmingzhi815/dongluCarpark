package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;

public class OpenDoorDetailWizard extends Wizard implements AbstractWizard{
	private SingleCarparkOpenDoorLog model;
	private OpenDoorDetailWizardPage page;
	public OpenDoorDetailWizard(SingleCarparkOpenDoorLog model) {
		setWindowTitle("查看抬杆详情记录");
		this.model=model;
	}

	@Override
	public void addPages() {
		page = new OpenDoorDetailWizardPage(model);
		addPage(page);
		getShell().setSize(840, 630);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		return false;
	}

	@Override
	public Object getModel() {
		
		return model;
	}
}
