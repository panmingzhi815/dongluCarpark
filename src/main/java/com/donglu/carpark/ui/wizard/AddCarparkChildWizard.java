package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.util.StrUtil;

public class AddCarparkChildWizard extends Wizard implements AbstractWizard{
	
	private SingleCarparkCarpark model;
	private AddCarparkChildWizardPage page;
	private  CarparkDatabaseServiceProvider sp;
	public AddCarparkChildWizard(SingleCarparkCarpark model, CarparkDatabaseServiceProvider sp) {
		this.model=model;
		this.sp=sp;
		if (StrUtil.isEmpty(model.getCode())) {
			setWindowTitle("添加子停车场");
		}else{
			setWindowTitle("修改子停车场");
		}
		
	}

	@Override
	public void addPages() {
		page = new AddCarparkChildWizardPage(model);
		addPage(page);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (StrUtil.isEmpty(model.getCode())||StrUtil.isEmpty(model.getName())) {
			page.setErrorMessage("请填写完整信息");
			return false;
		}
		try {
			String code = model.getCode();
			int parseInt = Integer.parseInt(code);
//			if (parseInt<0||parseInt>99) {
//				page.setErrorMessage("编码只能是0-99的数字");
//				return false;
//			}
//			if (parseInt>=0&&parseInt<=9) {
//				model.setCode("0"+parseInt);
//			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			page.setErrorMessage("编码只能是数字");
			return false;
		}
		if (model.getCode().length()>20) {
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
