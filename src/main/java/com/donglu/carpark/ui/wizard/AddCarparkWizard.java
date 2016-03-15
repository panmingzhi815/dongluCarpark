package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.util.StrUtil;

public class AddCarparkWizard extends Wizard implements AbstractWizard{
	
	private SingleCarparkCarpark model;
	private AddCarparkWizardPage page;
	CarparkDatabaseServiceProvider sp;
	public AddCarparkWizard(SingleCarparkCarpark model, CarparkDatabaseServiceProvider sp) {
		this.model=model;
		this.sp=sp;
		if (StrUtil.isEmpty(model.getCode())) {
			setWindowTitle("添加停车场");
		}else{
			setWindowTitle("修改停车场");
		}
		
	}

	@Override
	public void addPages() {
		page = new AddCarparkWizardPage(model);
		addPage(page);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (StrUtil.isEmpty(model.getCode())||StrUtil.isEmpty(model.getName())) {
			page.setErrorMessage("请填写完整信息");
			return false;
		}
		String code = model.getCode();
		if(code.length()>32) {
			page.setErrorMessage("编码不能太长");
			return false;
		}
		if (checkCode()) {
			page.setErrorMessage("编码已存在");
			return false;
		}
		if (model.getName().length()>100) {
			page.setErrorMessage("名称不能太长");
			return false;
		}
		if (model.getFixNumberOfSlot()>model.getTotalNumberOfSlot()) {
			page.setErrorMessage("固定车位不能大于总车位");
			return false;
		}
		return true;
	}

	private boolean checkCode() {
		SingleCarparkCarpark findCarparkById = sp.getCarparkService().findCarparkByCode(model.getCode());
		if (!StrUtil.isEmpty(findCarparkById)&&findCarparkById.getId()!=model.getId()) {
			return true;
		}
		return false;
	}

	@Override
	public Object getModel() {
		
		return model;
	}

}
