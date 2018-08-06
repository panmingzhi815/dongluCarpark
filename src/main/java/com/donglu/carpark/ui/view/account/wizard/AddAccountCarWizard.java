package com.donglu.carpark.ui.view.account.wizard;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkAccountCar;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;
import com.dongluhitec.card.domain.util.StrUtil;

public class AddAccountCarWizard extends Wizard implements AbstractWizard {
	private CarparkAccountCar model;
	private CarparkDatabaseServiceProvider sp;
	private AddAccountCarWizardPage page;
	public AddAccountCarWizard(CarparkAccountCar model,CarparkDatabaseServiceProvider sp) {
		this.model = model;
		this.sp = sp;
	}
	@Override
	public void addPages() {
		page = new AddAccountCarWizardPage(model);
		addPage(page);
	}
	
	@Override
	public Object getModel() {
		return model;
	}

	@Override
	public boolean performFinish() {
		List<CarparkAccountCar> list = sp.getCarparkUserService().findAccountCard(Arrays.asList(QueryParameter.eq("plateNo", model.getPlateNo())));
		if (model.getId()==null&&!StrUtil.isEmpty(list)) {
			page.setErrorMessage("车牌已在系统中存在!");
			return false;
		}
		if(model.getId()!=null&&!StrUtil.isEmpty(list)){
			if (!model.getId().equals(list.get(0).getId())) {
				page.setErrorMessage("车牌已在系统中存在!");
				return false;
			}
		}
		return true;
	}

}
