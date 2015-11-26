package com.donglu.carpark.ui.wizard;

import java.util.Date;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayBasicPage;
import com.donglu.carpark.ui.wizard.monthcharge.MonthlyUserPayModel;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;

public class AddUserWizard extends Wizard implements AbstractWizard {
	AddUserModel model;
	private AddUserWizardPage page;
	private MonthlyUserPayBasicPage page2;
	CarparkDatabaseServiceProvider sp;

	public AddUserWizard(AddUserModel model, CarparkDatabaseServiceProvider sp) {
		this.model = model;
		this.sp=sp;
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
			page2 = new MonthlyUserPayBasicPage(model.getModel());
			addPage(page2);
		}
		getShell().setSize(450, 650);
		WidgetUtil.center(getShell());
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (model.getType().equals("普通")) {
			MonthlyUserPayModel m = model.getModel();
			if (!StrUtil.isEmpty(m)) {
				if (StrUtil.isEmpty(m.getSelectMonth())) {
					page2.setErrorMessage("请选择月租类型");
					return false;
				}
				if (StrUtil.isEmpty(m.getChargesMoney())) {
					page2.setErrorMessage("请输入充值金额");
					return false;
				}
			}
		}
		if (!StrUtil.isEmpty(model.getModel())) {
			if (StrUtil.isEmpty(model.getModel().getOverdueTime())) {
				page.setErrorMessage("固定用户必须有个有效期");
				return false;
			}
		}
		page.setErrorMessage(null);
		return true;
	}

	/**
	 * 
	 */
	public boolean check() {
		if (StrUtil.isEmpty(model.getPlateNo())) {
			page.setErrorMessage("车牌不正确,请输入正确车牌");
			return false;
		}
		String[] split = model.getPlateNo().split(",");
		for (String string : split) {
			if (string.length()>8) {
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
		m.setAllmonth(sp.getCarparkService().findMonthlyChargeByCarpark(model.getCarpark()));
		if (model.getType().equals("免费")) {
			m.setFree(false);
		}else{
			m.setFree(true);
		}
		return super.getNextPage(page);
	}

	public void loadSlot() {
		model.setTotalSlot(sp.getCarparkInOutService().findFixSlotIsNow(model.getCarpark()));
	}
}
