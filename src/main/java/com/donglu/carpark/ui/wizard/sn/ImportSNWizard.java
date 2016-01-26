package com.donglu.carpark.ui.wizard.sn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.server.imgserver.ImageServerUI;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.domain.db.setting.SNSettingType;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.core.crypto.appauth.AppVerifier;

public class ImportSNWizard extends Wizard implements AbstractWizard {
	private ImportSNModel model;
	private final AppVerifier appVerifier;
	private ImportSNPage importSNPage;

	private CarparkDatabaseServiceProvider sp;

	public ImportSNWizard(AppVerifier appVerifier, CarparkDatabaseServiceProvider sp,ImportSNModel model) {
		this.appVerifier = appVerifier;
		this.sp = sp;
		this.model=model;
		setWindowTitle("导入注册码");
	}

	@Override
	public void addPages() {
		importSNPage = new ImportSNPage(this.appVerifier,model);
		addPage(importSNPage);
		getShell().setSize(400, 500);
	}

	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	@Override
	public Object getModel() {
		return null;
	}

	@Override
	public boolean performFinish() {
		String sn = importSNPage.getText().getText();
		Date validTo = importSNPage.getValidTo();
		if (StrUtil.isEmpty(sn) || StrUtil.isEmpty(validTo)) {
			return false;
		}
		List<SingleCarparkSystemSetting> systemSettingList = getSystemSettingList(sn,validTo);
		for (SingleCarparkSystemSetting singleCarparkSystemSetting : systemSettingList) {
			sp.getCarparkService().saveSystemSetting(singleCarparkSystemSetting);
		}
		return true;
	}

	private List<SingleCarparkSystemSetting> getSystemSettingList(Object... companyName) {
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkSystemSetting> list = new ArrayList<SingleCarparkSystemSetting>();

		SingleCarparkSystemSetting ss = carparkService.findSystemSettingByKey(SNSettingType.sn.name());
		if (StrUtil.isEmpty(ss)) {
			ss = new SingleCarparkSystemSetting();
			ss.setSettingKey(SNSettingType.sn.name());
		}
		ss.setSettingValue(companyName[0] + "");
		list.add(ss);
		

		SingleCarparkSystemSetting ss4 = carparkService.findSystemSettingByKey(SNSettingType.validTo.name());
		if (StrUtil.isEmpty(ss4)) {
			ss4 = new SingleCarparkSystemSetting();
			ss4.setSettingKey(SNSettingType.validTo.name());
		}
		Date d = (Date) companyName[1];
		ss4.setSettingValue(StrUtil.formatDate(d, ImageServerUI.YYYY_MM_DD));
		list.add(ss4);
		return list;
	}

	public void init() {
		// SystemSettingService systemSettingService = sd.getServiceProvider().getSystemSettingService();
		// systemSettingService.findSN(new ArrayList<String>());
	}
}