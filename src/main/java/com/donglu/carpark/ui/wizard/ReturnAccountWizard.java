package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.wizard.model.ReturnAccountModel;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;


public class ReturnAccountWizard extends Wizard implements AbstractWizard{
	private ReturnAccountModel model;
	private CarparkDatabaseServiceProvider sp;
	private ReturnAccountWizardPage page;
	public ReturnAccountWizard(ReturnAccountModel model, CarparkDatabaseServiceProvider sp) {
		this.model=model;
		this.sp=sp;
		setWindowTitle("归账");
	}

	@Override
	public void addPages() {
		page = new ReturnAccountWizardPage(model);
		addPage(page);
		getShell().setSize(450,550);
		WidgetUtil.center(getShell());
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		SingleCarparkSystemUser systemUser=checkLogin();
		if (StrUtil.isEmpty(systemUser)) {
			page.setErrorMessage("用户名或密码错误");
			return false;
		}
		String property = System.getProperty("userType");
		if (System.getProperty("userName").equals(systemUser.getUserName())) {
			page.setErrorMessage("不能归账给自己");
			return false;
		}
//		if (SystemUserTypeEnum.valueOf(systemUser.getType()).getLevel()>2||SystemUserTypeEnum.valueOf(property).getLevel()>SystemUserTypeEnum.valueOf(property).getLevel()) {
//			page.setErrorMessage("用户名权限不足");
//			return false; 
//		}
		return true;
	}

	private SingleCarparkSystemUser checkLogin() {
		SingleCarparkSystemUser findByNameAndPassword = sp.getSystemUserService().findByNameAndPassword(model.getOperaName(), model.getPwd());
		return findByNameAndPassword;
	}

	@Override
	public Object getModel() {
		
		return model;
	}

}
