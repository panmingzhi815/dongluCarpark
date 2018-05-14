package com.donglu.carpark.ui.wizard;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jface.wizard.Wizard;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.wizard.model.ChangeUserModel;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;


public class ChangeUserWizard extends Wizard implements AbstractWizard{
	private ChangeUserModel model;
	private CarparkDatabaseServiceProvider sp;
	private ChangeUserWizardPage page;
	public ChangeUserWizard(ChangeUserModel model, CarparkDatabaseServiceProvider sp) {
		this.model=model;
		this.sp=sp;
		setWindowTitle("换班");
		
	}

	@Override
	public void addPages() {
		page = new ChangeUserWizardPage(model);
		addPage(page);
		getShell().setSize(450,550);
		WidgetUtil.center(getShell());
		getShell().setImage(JFaceUtil.getImage("carpark_32"));
	}

	@Override
	public boolean performFinish() {
		if (model.getSystemUser().getUserName().equals(System.getProperty("userName"))) {
			page.setErrorMessage("自己不能跟自己换班");
			return false;
		}
		SingleCarparkSystemUser systemUser=checkLogin();
		if (StrUtil.isEmpty(systemUser)) {
			page.setErrorMessage("用户名或密码错误");
			return false;
		}
		model.setSystemUser(systemUser);
		return true;
	}

	private SingleCarparkSystemUser checkLogin() {
		SingleCarparkSystemUser findByNameAndPassword = sp.getSystemUserService().findByNameAndPassword(model.getSystemUser().getUserName(), model.getPwd());
		return findByNameAndPassword;
	}

	@Override
	public Object getModel() {
		
		return model;
	}

	public void printHistory(Date start, Date end) {
		List<SingleCarparkInOutHistory> list = sp.getCarparkInOutService().findByCondition(0, Integer.MAX_VALUE, null, null, "临时车", null, null, null, start, end, ConstUtil.getUserName()	, null, null,null, null, 0);
		list = list.stream().filter(new Predicate<SingleCarparkInOutHistory>() {
			@Override
			public boolean test(SingleCarparkInOutHistory t) {
				String remarkString = t.getRemarkString();
				return t.getFactMoney()>0&&(!(remarkString!=null&&(remarkString.contains("缴费完成")||remarkString.contains("扫码缴费出场"))));
			}
		}).collect(Collectors.toList());
		ExcelImportExportImpl excelImportExport = new ExcelImportExportImpl();
		try {
			String title="当前值班："+ConstUtil.getUserName()+" "+StrUtil.formatDate(start, "yyyy年MM月dd日HH点mm")+"到"+StrUtil.formatDate(end, "yyyy年MM月dd日HH点mm")+"收费报表";
			String path = System.getProperty("user.dir")+"\\收费报表.xls";
			excelImportExport.exportOperaCharge(path,title, list);
			excelImportExport.printExcel(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
