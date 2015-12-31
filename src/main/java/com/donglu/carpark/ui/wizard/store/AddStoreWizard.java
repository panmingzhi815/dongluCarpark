package com.donglu.carpark.ui.wizard.store;

import org.eclipse.jface.wizard.Wizard;
import org.joda.time.DateTime;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.dongluhitec.card.domain.util.StrUtil;


public class AddStoreWizard extends Wizard implements AbstractWizard{
	AddStoreModel model;
	private AddStoreWizardPage page;
	public AddStoreWizard(AddStoreModel model) {
		this.model=model;
		if (StrUtil.isEmpty(model.getId())) {
			setWindowTitle("添加黑名单");
		}else{
			setWindowTitle("修改黑名单");
		}
		
	}

	@Override
	public void addPages() {
		page = new AddStoreWizardPage(model);
		addPage(page);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (StrUtil.isEmpty(model.getStoreName())) {
			page.setErrorMessage("商铺主人不能为空");
			return false;
		}
		if (StrUtil.isEmpty(model.getLoginName())) {
			page.setErrorMessage("账号名称不能为空");
			return false;
		}
		if (StrUtil.isEmpty(model.getLoginPawword())) {
			page.setErrorMessage("账号密码不能为空");
			return false;
		}
		if (!model.getLoginPawword().equals(model.getRePawword())) {
			page.setErrorMessage("两次输入的密码必须一致");
			return false;
		}
		return true;
	}

	public Object getModel() {
		
		return model;
	}

}
