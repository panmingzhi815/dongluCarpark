package com.donglu.carpark.ui.view.user.wizard;

import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.util.StrUtil;

import java.util.Date;

import org.eclipse.jface.wizard.Wizard;

public class MonthlyUserPayWizard extends Wizard implements AbstractWizard{
	
	private MonthlyUserPayModel model;
	private MonthlyUserPayBasicPage page;
	
	
	public MonthlyUserPayWizard(MonthlyUserPayModel model) {
		this.model = model;
	}

	@Override
	public void addPages() {
		page = new MonthlyUserPayBasicPage(model);
		addPage(page);
        setWindowTitle("固定月租用户缴费");
        getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (model.isFree()) {
			SingleCarparkMonthlyCharge selectMonth = model.getSelectMonth();
			if (StrUtil.isEmpty(selectMonth)) {
				page.setErrorMessage("请选择月租");
				return false;
			}
//			if (StrUtil.isEmpty(model.getOverdueTime())||model.getOverdueTime().before(new Date())) {
//				page.setErrorMessage("过期时间必须在现在时间之后");
//				return false;
//			}
			if (StrUtil.isEmpty(model.getChargesMoney())) {
				page.setErrorMessage("请输入收费金额");
				return false;
			}
		}
		return true;
	}

	@Override
	public MonthlyUserPayModel getModel() {
		return model;
	}
}
