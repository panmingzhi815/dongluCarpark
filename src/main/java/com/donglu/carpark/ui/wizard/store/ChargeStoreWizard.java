package com.donglu.carpark.ui.wizard.store;

import org.eclipse.jface.wizard.Wizard;
import org.joda.time.DateTime;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
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

	public Object getModel() {
		
		return model;
	}

}
