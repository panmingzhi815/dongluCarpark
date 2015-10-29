package com.donglu.carpark.ui.wizard;

import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayBasicPage;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayModel;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;

public class AddUserWizard extends Wizard implements AbstractWizard {
	AddUserModel model;
	private AddUserWizardPage page;

	public AddUserWizard(AddUserModel model) {
		this.model = model;
		if (StrUtil.isEmpty(model.getPlateNo())) {
			setWindowTitle("添加固定用户");
		} else {
			setWindowTitle("修改固定用户");
		}
	}

	@Override
	public void addPages() {
		page = new AddUserWizardPage(model);
		addPage(page);
		if (!StrUtil.isEmpty(model.getModel())) {
			addPage(new MonthlyUserPayBasicPage(model.getModel()));
		}
		getShell().setSize(450, 650);
		WidgetUtil.center(getShell());
	}

	@Override
	public boolean performFinish() {
		if (!StrUtil.isEmpty(model.getModel())) {
			if (StrUtil.isEmpty(model.getModel().getOverdueTime())) {
				page.setErrorMessage("固定用户必须有个有效期");
				return false;
			}
		}
		if (model.getPlateNo().length()>8) {
			page.setErrorMessage("请输入正确车牌");
			return false;
		}
		return true;
	}

	public Object getModel() {

		return model;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		Date createDate = new Date();
		model.setCreateDate(createDate);
		model.setValidTo(createDate);
		MonthlyUserPayModel m = model.getModel();
		m.setPlateNO(model.getPlateNo());
		m.setUserName(model.getName());
		m.setCreateTime(model.getCreateDate());
		m.setCreateTimeLabel(m.getCreateTimeLabel());
		return super.getNextPage(page);
	}

}
