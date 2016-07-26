package com.donglu.carpark.ui.view.free.wizard;


import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;

public class AddTempCarFreeWizard extends Wizard implements AbstractWizard {
	private SingleCarparkFreeTempCar model;
	private AddTempCarFreeWizardPage page;
	private boolean isImport=false;
	public AddTempCarFreeWizard(SingleCarparkFreeTempCar model) {
		this.model = model;
		if (StrUtil.isEmpty(model.getId())) {
			setWindowTitle("添加临时车优惠");
		} else {
			setWindowTitle("修改临时车优惠");
		}
	}
	public AddTempCarFreeWizard(SingleCarparkFreeTempCar model,boolean isImport) {
		this.model = model;
		this.isImport = isImport;
		setWindowTitle("填写导入数据的优惠信息");
	}

	@Override
	public void addPages() {
		if (isImport) {
			page = new AddTempCarFreeWizardPage(model,isImport);
		}else{
			page = new AddTempCarFreeWizardPage(model);
		}
		addPage(page);
		getShell().setSize(400, 450);
		WidgetUtil.center(getShell());
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (!isImport) {
			if (StrUtil.isEmpty(model.getPlateNo())) {
				page.setErrorMessage("车牌不能为空");
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
			page.setErrorMessage("车牌不能为空");
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
