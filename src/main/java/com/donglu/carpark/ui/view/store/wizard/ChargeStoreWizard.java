package com.donglu.carpark.ui.view.store.wizard;

import org.eclipse.jface.wizard.Wizard;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.util.StrUtil;


public class ChargeStoreWizard extends Wizard implements AbstractWizard{
	ChargeStoreModel model;
	private ChargeStoreWizardPage page;
	public ChargeStoreWizard(ChargeStoreModel model) {
		this.model=model;
		if (StrUtil.isEmpty(model.getId())) {
			setWindowTitle("添加黑名单");
		}else{
			setWindowTitle("修改黑名单");
		}
		
	}

	@Override
	public void addPages() {
		page = new ChargeStoreWizardPage(model);
		addPage(page);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		
		return true;
	}

	@Override
	public Object getModel() {
		
		return model;
	}

}
