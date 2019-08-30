package com.donglu.carpark.ui.view.speed.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;

public class OverSpeedSettingWizard extends Wizard implements AbstractWizard {
	
	private CarparkDatabaseServiceProvider sp;
	private OverSpeedSettingWizardPage page;

	public OverSpeedSettingWizard(CarparkDatabaseServiceProvider sp) {
		this.sp = sp;
	}
	@Override
	public void addPages() {
		page = new OverSpeedSettingWizardPage();
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		page.save();
		return true;
	}

	@Override
	public Object getModel() {
		return null;
	}
	public String getSetting(SystemSettingTypeEnum systemSettingTypeEnum) {
		SingleCarparkSystemSetting key = sp.getCarparkService().findSystemSettingByKey(systemSettingTypeEnum.name());
		if (key==null) {
			return systemSettingTypeEnum.getDefaultValue();
		}
		return key.getSettingValue();
	}
	public CarparkDatabaseServiceProvider getSp() {
		return sp;
	}

}
