package com.donglu.carpark.ui.view.card.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.ui.Login;
import com.donglu.carpark.ui.view.user.UserPresenter;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;
import com.google.common.base.Strings;

public class AddCardWizard extends Wizard implements AbstractWizard {
	private SingleCarparkCard model;
	private AddCardWizardPage page;
	private SelectUserWizardPage page2;

	public AddCardWizard(SingleCarparkCard model) {
		this.model = model;
		if (StrUtil.isEmpty(model.getId())) {
			setWindowTitle("添加卡片");
		} else {
			setWindowTitle("修改卡片");
		}
	}

	@Override
	public void addPages() {
		page = new AddCardWizardPage(model);
		addPage(page);
		UserPresenter userPresenter = Login.injector.getInstance(UserPresenter.class);
		page2 = new SelectUserWizardPage(model, userPresenter);
		addPage(page2);
		getShell().setSize(450, 650);
		WidgetUtil.center(getShell());
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (model.getSerialNumber()==null) {
			setErrorMessage("卡片内码不能为空");
			return false;
		}
		if (model.getUser()==null) {
			setErrorMessage("请选择一个用户");
			return false;
		}
		String padStart = Strings.padStart(model.getSerialNumber().toUpperCase(), 16, '0');
		model.setSerialNumber(padStart);
		return true;
	}
	
	
	private void setErrorMessage(String string) {
		page.setErrorMessage(string);
		if (page2!=null) {
			page2.setErrorMessage(string);
		}
	}

	/**
	 * 
	 */
	public boolean check() {
		page.setErrorMessage(null);
		return true;
	}

	@Override
	public Object getModel() {
		return model;
	}
}
