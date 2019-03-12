package com.donglu.carpark.ui.view.carpark.wizard;

import java.util.Date;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;

public class TestCountChargeWizard extends Wizard implements AbstractWizard {

	private CarparkDatabaseServiceProvider sp;
	private CarparkChargeStandard ccs;

	public TestCountChargeWizard(CarparkDatabaseServiceProvider sp,CarparkChargeStandard ccs) {
		this.sp = sp;
		this.ccs = ccs;
	}
	
	@Override
	public Object getModel() {
		return null;
	}
	
	@Override
	public void addPages() {
		super.addPage(new TestCountChargeWizardPage());
	}
	
	@Override
	public boolean performFinish() {
		return false;
	}

	public float countMonry(Date inTime, Date outTime) {
		try {
			float calculateTempCharge = sp.getCarparkService().calculateTempCharge(ccs.getCarpark().getId(), ccs.getCarparkCarType().getId(), inTime, outTime);
			return calculateTempCharge;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

}
