package com.donglu.carpark.ui.wizard;

import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayBasicPage;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayModel;
import com.donglu.carpark.util.CarparkUtils;
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
		
		if (StrUtil.isEmpty(model.getPlateNo())) {
			page.setErrorMessage("车牌不正确,请输入正确车牌");
			return false;
		}
		String[] split = model.getPlateNo().split(",");
		for (String string : split) {
			if (!string.matches(CarparkUtils.PLATENO_REGEX)) {
				page.setErrorMessage("车牌:"+string+"不正确,请输入正确车牌");
				return false;
			}
		}
		if (StrUtil.isEmpty(model.getName())) {
			page.setErrorMessage("用户名不能为空");
			return false;
		}
		page.setErrorMessage(null);
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
	public static void main(String[] args) {
		String s="^[\u4e00-\u9fa5][A-Za-z0-9]{6}$";
		boolean matches = "月sssssss".matches(s);
		System.out.println(matches);
	}
}
