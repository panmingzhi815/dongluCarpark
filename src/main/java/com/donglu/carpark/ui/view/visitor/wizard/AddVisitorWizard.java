package com.donglu.carpark.ui.view.visitor.wizard;


import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;

public class AddVisitorWizard extends Wizard implements AbstractWizard {
	private AddVisitorModel model;
	private AddVisitorWizardPage page;
	public AddVisitorWizard(AddVisitorModel model) {
		this.model = model;
		if (StrUtil.isEmpty(model.getId())) {
			setWindowTitle("添加访客用户");
		} else {
			setWindowTitle("修改访客用户");
		}
	}

	@Override
	public void addPages() {
		page = new AddVisitorWizardPage(model);
		addPage(page);
		getShell().setSize(450, 650);
		WidgetUtil.center(getShell());
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		page.setErrorMessage(null);
		return true;
	}

	/**
	 * 
	 */
	public boolean check() {
		if (StrUtil.isEmpty(model.getName())) {
			page.setErrorMessage("用户名不能为空");
			return false;
		}
		page.setErrorMessage(null);
		return true;
	}

	@Override
	public Object getModel() {
		return model;
	}
}
