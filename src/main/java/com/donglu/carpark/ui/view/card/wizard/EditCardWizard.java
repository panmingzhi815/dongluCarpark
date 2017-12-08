package com.donglu.carpark.ui.view.card.wizard;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Strings;

public class EditCardWizard extends Wizard implements AbstractWizard {

	private SingleCarparkCard model;
	private CarparkDatabaseServiceProvider sp;
	private EditCardWizardPage page;

	public EditCardWizard(SingleCarparkCard model, CarparkDatabaseServiceProvider sp) {
		this.model = model;
		this.sp = sp;
	}
	@Override
	public void addPages() {
		page = new EditCardWizardPage(model);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		if(StrUtil.isEmpty(model.getIdentifier())||StrUtil.isEmpty(model.getSerialNumber())){
			page.setErrorMessage("不能为空！");
			return false;
		}
		model.setSerialNumber(Strings.padStart(model.getSerialNumber(), 16, '0'));
		if(!model.getSerialNumber().matches("[0-9A-F]{16}")){
			page.setErrorMessage("卡内码格式不正确");
			return false;
		}
		if(!checkCardExist()){
			page.setErrorMessage("卡片已存在系统中");
			return false;
		}
		return true;
	}

	@Override
	public Object getModel() {
		return model;
	}
	
	public boolean checkCardExist(){
		SingleCarparkCard findCard = sp.getCardService().findCard(model.getIdentifier(), null);
		if(!StrUtil.isEmpty(findCard)&&!model.getId().equals(findCard.getId())){
			return false;
		}
		findCard = sp.getCardService().findCard(null, model.getSerialNumber());
		if(!StrUtil.isEmpty(findCard)&&!model.getId().equals(findCard.getId())){
			return false;
		}
		return true;
	}
	
}
