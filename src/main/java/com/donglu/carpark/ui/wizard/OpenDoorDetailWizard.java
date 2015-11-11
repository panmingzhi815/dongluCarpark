package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkOpenDoorLog;
import com.dongluhitec.card.domain.util.StrUtil;

public class OpenDoorDetailWizard extends Wizard implements AbstractWizard{
	private SingleCarparkOpenDoorLog model;
	private OpenDoorDetailWizardPage page;
	public OpenDoorDetailWizard(SingleCarparkOpenDoorLog model) {
		setWindowTitle("查看进出记录");
		this.model=model;
	}

	@Override
	public void addPages() {
		page = new OpenDoorDetailWizardPage(model);
		addPage(page);
		getShell().setSize(840, 630);
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
