package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.model.SystemUserModel;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.util.StrUtil;


public class AddSystemUserWizard extends Wizard implements AbstractWizard{
	SystemUserModel model;
	private AddSystemUserWizardPage page;
	public AddSystemUserWizard(SystemUserModel model) {
		this.model=model;
		setWindowTitle("添加系统用户");
	}

	@Override
	public void addPages() {
		page = new AddSystemUserWizardPage(model);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		if (StrUtil.isEmpty(model.getUserName())) {
			page.setErrorMessage("用户名不能为空");
			return false;
		}
		if (StrUtil.isEmpty(model.getPwd())) {
			page.setErrorMessage("用户密码不能为空");
			return false;
		}
		if (!model.getPwd().equals(model.getRePwd())) {
			page.setErrorMessage("两次输入的密码不一致！");
			return false;
		}
		if (StrUtil.isEmpty(model.getType())) {
			page.setErrorMessage("用户类型不能为空");
			return false;
		}
		return true;
	}

	public Object getModel() {
		
		return model;
	}

}
