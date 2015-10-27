package com.donglu.carpark.ui.wizard.monthcharge;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.carpark.CarparkMonthlyUser;
import com.dongluhitec.card.ui.carpark.pay.storein.wizard.NewCarparkStoreInHistoryModel;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

public class MonthlyUserPayWizard extends Wizard implements AbstractWizard{
	
	private MonthlyUserPayModel model;
	
	
	public MonthlyUserPayWizard(MonthlyUserPayModel model) {
		this.model = model;
	}

	@Override
	public void addPages() {
		addPage(new MonthlyUserPayBasicPage(model));
        setWindowTitle("车牌月租用户缴费");
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public MonthlyUserPayModel getModel() {
		return model;
	}

}
