package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.model.ShowInOutHistoryModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;

public class InOutHistoryDetailWizard extends Wizard implements AbstractWizard{
	private ShowInOutHistoryModel model;
	private InOutHistoryDetailWizardPage page;
	private Boolean isEdit=false;
	public InOutHistoryDetailWizard(ShowInOutHistoryModel model) {
		setWindowTitle("查看进出记录");
		this.model=model;
	}

	public InOutHistoryDetailWizard(ShowInOutHistoryModel h, Boolean isEdit, CarparkDatabaseServiceProvider sp, CommonUIFacility commonui) {
		this(h);
		this.isEdit=isEdit;
	}

	@Override
	public void addPages() {
		page = new InOutHistoryDetailWizardPage(model,isEdit);
		addPage(page);
		getShell().setSize(840, 630);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public Object getModel() {
		
		return model;
	}
}
