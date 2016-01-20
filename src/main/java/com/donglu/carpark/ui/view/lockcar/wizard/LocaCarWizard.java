package com.donglu.carpark.ui.view.lockcar.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;
import com.dongluhitec.card.domain.util.StrUtil;

public class LocaCarWizard extends Wizard implements AbstractWizard{
	
	private SingleCarparkLockCar model;
	private LocaCarWizardPage page;
	public LocaCarWizard(SingleCarparkLockCar model) {
		this.model=model;
		setWindowTitle("锁车");
	}

	@Override
	public void addPages() {
		page = new LocaCarWizardPage(model);
		addPage(page);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (StrUtil.isEmpty(model.getPlateNO())) {
			page.setErrorMessage("请填写完整信息");
			return false;
		}
		return true;
	}
	
	@Override
	public Object getModel() {
		return model;
	}

}
