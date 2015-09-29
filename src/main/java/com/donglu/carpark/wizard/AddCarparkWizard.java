package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.util.StrUtil;

public class AddCarparkWizard extends Wizard implements AbstractWizard{
	
	private SingleCarparkCarpark model;
	public AddCarparkWizard(SingleCarparkCarpark model) {
		setWindowTitle("添加停车场");
		this.model=model;
	}

	@Override
	public void addPages() {
		addPage(new AddCarparkWizardPage(model));
	}

	@Override
	public boolean performFinish() {
		if (StrUtil.isEmpty(model.getCode())||StrUtil.isEmpty(model.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public Object getModel() {
		
		return model;
	}

}
