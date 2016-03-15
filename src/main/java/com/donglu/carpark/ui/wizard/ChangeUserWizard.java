package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.wizard.model.ChangeUserModel;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;


public class ChangeUserWizard extends Wizard implements AbstractWizard{
	private ChangeUserModel model;
	private CarparkDatabaseServiceProvider sp;
	private ChangeUserWizardPage page;
	public ChangeUserWizard(ChangeUserModel model, CarparkDatabaseServiceProvider sp) {
		this.model=model;
		this.sp=sp;
		setWindowTitle("换班");
		
	}

	@Override
	public void addPages() {
		page = new ChangeUserWizardPage(model);
		addPage(page);
		getShell().setSize(450,550);
		WidgetUtil.center(getShell());
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (model.getSystemUser().getUserName().equals(System.getProperty("userName"))) {
			page.setErrorMessage("自己不能跟自己换班");
			return false;
		}
		SingleCarparkSystemUser systemUser=checkLogin();
		if (StrUtil.isEmpty(systemUser)) {
			page.setErrorMessage("用户名或密码错误");
			return false;
		}
		model.setSystemUser(systemUser);
		return true;
	}

	private SingleCarparkSystemUser checkLogin() {
		SingleCarparkSystemUser findByNameAndPassword = sp.getSystemUserService().findByNameAndPassword(model.getSystemUser().getUserName(), model.getPwd());
		return findByNameAndPassword;
	}

	@Override
	public Object getModel() {
		
		return model;
	}

}
