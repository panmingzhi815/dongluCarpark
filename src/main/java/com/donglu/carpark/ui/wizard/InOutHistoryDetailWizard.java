package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;

public class InOutHistoryDetailWizard extends Wizard implements AbstractWizard{
	private SingleCarparkInOutHistory model;
	private InOutHistoryDetailWizardPage page;
	private Boolean isEdit=false;
	public InOutHistoryDetailWizard(SingleCarparkInOutHistory model) {
		setWindowTitle("查看进出记录");
		this.model=model;
	}

	public InOutHistoryDetailWizard(SingleCarparkInOutHistory h, Boolean isEdit) {
		this(h);
		this.isEdit=isEdit;
	}

	@Override
	public void addPages() {
		page = new InOutHistoryDetailWizardPage(model);
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
