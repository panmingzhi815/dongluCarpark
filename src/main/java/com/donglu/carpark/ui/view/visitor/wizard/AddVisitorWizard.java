package com.donglu.carpark.ui.view.visitor.wizard;


import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;

public class AddVisitorWizard extends Wizard implements AbstractWizard {
	SingleCarparkVisitor model;
	private AddVisitorWizardPage page;
	CarparkDatabaseServiceProvider sp;

	public AddVisitorWizard(SingleCarparkVisitor model, CarparkDatabaseServiceProvider sp) {
		this.model = model;
		this.sp=sp;
		if (StrUtil.isEmpty(model.getId())) {
			setWindowTitle("添加固定用户");
		} else {
			setWindowTitle("修改固定用户");
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
