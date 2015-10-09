package com.donglu.carpark.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.model.SystemUserModel;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.util.StrUtil;


public class EditSystemUserWizard extends Wizard implements AbstractWizard{
	SystemUserModel model;
	private EditSystemUserWizardPage page;
	public EditSystemUserWizard(SystemUserModel model) {
		this.model=model;
		setWindowTitle("添加系统用户");
	}

	@Override
	public void addPages() {
		page = new EditSystemUserWizardPage(model);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		if (StrUtil.isEmpty(model.getPwd())) {
			page.setErrorMessage("用户密码不能为空");
			return false;
		}
		if (!model.getPwd().equals(model.getRePwd())) {
			page.setErrorMessage("两次输入的密码不一致！");
			return false;
		}
		return true;
	}

	public Object getModel() {
		
		return model;
	}

}
