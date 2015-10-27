package com.donglu.carpark.ui.wizard.holiday;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.datechooser.DateChooser;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.dongluhitec.card.blservice.DatabaseServiceDaemon;
import com.dongluhitec.card.blservice.SettingService;
import com.dongluhitec.card.common.ui.AbstractWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.Holiday;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class AddYearHolidayWizard extends Wizard implements AbstractWizard{

	private AddYearHolidayModel model;
	private CommonUIFacility commonui;
	private DatabaseServiceDaemon sd;
	private AddYearHolidayPage addYearHolidayPage;
	private CarparkDatabaseServiceProvider sp;
	
	public AddYearHolidayWizard(AddYearHolidayModel model,CarparkDatabaseServiceProvider sp,CommonUIFacility commonui) {
		this.model = model;
		this.sp=sp;
		this.commonui=commonui;
		setWindowTitle("批量添加节假日");
	}

	@Override
	public void addPages() {
		this.addYearHolidayPage=new AddYearHolidayPage(model);
		addPage(addYearHolidayPage);
		getShell().setImage(JFaceUtil.getImage("attendanceholidaygrup_16"));
		getShell().setSize(600, 760);
	}

	@Override
	public boolean performFinish() {
		List<Date> list=Lists.newArrayList();
	    for(DateChooser dc:this.addYearHolidayPage.getDc()){
	    	for (Date d : (List<Date>)dc.getSelectedDates()) {
				list.add(d);
			}
	    }
	    model.setSelect(list);
	    model.setAllh(new ArrayList<Holiday>());
	    
	    boolean flag=commonui.confirm("提示", "确认修改？");
	    
		return flag;
	}
	
	@Override
	public Object getModel() {
		return model;
	}

	public void loadHoliday(int year) {
		// TODO 自动生成的方法存根2015年5月29日，Michael
//		SettingService ss=sd.getServiceProvider().getSettingService();
//		model.setAllh(ss.findHolidayByYearAndMonth(year,0));
		CarparkService carparkService = sp.getCarparkService();
		List<Holiday> list=carparkService.findHolidayByYear(year);
		List<Date> listDate=new ArrayList<>();
		for (Holiday h : list) {
			System.out.println(StrUtil.formatDate(h.getStart(), "yyyy-MM-dd"));
			listDate.add(h.getStart());
		}
		model.setSelect(listDate);
	}
	
}
