package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.wizard.model.AddMonthChargeModel;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkChargeStandard;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.util.StrUtil;

public class AddMonthChargeWizard extends Wizard implements AbstractWizard {

	private AddMonthChargeModel model;
	private AddMonthChargeWizardPage page;
	private CarparkDatabaseServiceProvider sp;
	
	public AddMonthChargeWizard(AddMonthChargeModel model,CarparkDatabaseServiceProvider sp) {
		this.model = model;
		this.sp=sp;
		setWindowTitle("添加固定月租收费");
	}

	@Override
	public void addPages() {
		page = new AddMonthChargeWizardPage(model);
		this.addPage(page);
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public AddMonthChargeModel getModel() {
		return model;
	}
	/**
	 * 查找编码是否存在
	 * @param code
	 * @return 找到则返回true
	 */
	private boolean checkCode(String code) {
		
		SingleCarparkMonthlyCharge m=sp.getCarparkService().findMonthlyChargeByCode(code);
		if (!StrUtil.isEmpty(m)&&m.getId()!=model.getId()) {
			return true;
		}
		
		CarparkChargeStandard findCarparkChargeStandardByCode = sp.getCarparkService().findCarparkChargeStandardByCode(code);
		if (!StrUtil.isEmpty(findCarparkChargeStandardByCode)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean performFinish() {
		String code = model.getChargeCode();
		String name = model.getChargeName();
		Integer rentingDays = model.getRentingDays();
		if (StrUtil.isEmpty(code)||StrUtil.isEmpty(name)||StrUtil.isEmpty(rentingDays)) {
			page.setErrorMessage("请填写完整信息");
			return false;
		}
		try {
			int parseInt = Integer.parseInt(code);
			if (parseInt<0||parseInt>99) {
				page.setErrorMessage("编码只能是0-99的数字");
				return false;
			}
			if (parseInt>=0&&parseInt<=99) {
				model.setChargeCode("0"+parseInt);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			page.setErrorMessage("编码只能是0-99的数字");
			return false;
		}
		if (rentingDays<=0) {
			page.setErrorMessage("月租月数必须大于0");
			return false;
		}
		if (checkCode(model.getChargeCode())) {
			page.setErrorMessage("编码已存在");
			return false;
		}
		return true;
	}

}
