package com.donglu.carpark.ui.view.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarparkPayHistoryListPresenter  extends AbstractListPresenter<SingleCarparkMonthlyUserPayHistory>{
	
	private CarparkPayHistoryListView view;
	@Inject
	private CommonUIFacility commonui;
	
	private String userName, operaName;
	private Date start,  end;
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	

	
	@Override
	public void refresh(){
		List<SingleCarparkMonthlyUserPayHistory> list =sp.getCarparkService().findMonthlyUserPayHistoryByCondition(0,50,userName,operaName,start,end);
		populate(list);
	}
	private void populate(List<SingleCarparkMonthlyUserPayHistory> list) {
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = view.getModel();
		int countSearchAll=sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName,operaName,start,end);
		model.setList(list);
		model.setCountSearch(list.size());
		model.setCountSearchAll(countSearchAll);
		float i=0;
		for (SingleCarparkMonthlyUserPayHistory singleCarparkMonthlyUserPayHistory : list) {
			Float chargesMoney = singleCarparkMonthlyUserPayHistory.getChargesMoney();
			if (chargesMoney!=null) {
				i+=chargesMoney;
			}
		}
		view.setTotalMoney(i);
	}
	public void searMore(){
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = view.getModel();
		if (model.getCountSearchAll()<=model.getCountSearch()) {
			return;
		}
		List<SingleCarparkMonthlyUserPayHistory> list =sp.getCarparkService().findMonthlyUserPayHistoryByCondition(model.getList().size(),50,userName,operaName,start,end);
		int countSearchAll=sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName,operaName,start,end);
		model.AddList(list);
		model.setCountSearch(model.getList().size());
		model.setCountSearchAll(countSearchAll);
	}
	public void searchCharge(String userName, String operaName, Date start, Date end) {
		this.userName=userName;
		this.operaName=operaName;
		this.start=start;
		this.end=end;
		refresh();
	}

	@Override
	public void go(Composite listComposite) {
		view=new CarparkPayHistoryListView(listComposite, listComposite.getStyle());
		view.setPresenter(this);
	}

	public void export() {
		List<SingleCarparkMonthlyUserPayHistory> list = view.getModel().getList();
		if (StrUtil.isEmpty(list)) {
			return;
		}
		String selectToSave = commonui.selectToSave();
		if (StrUtil.isEmpty(selectToSave)) {
			return;
		}
		String path = StrUtil.checkPath(selectToSave, new String[] { ".xls", ".xlsx" }, ".xls");
		String[] columnProperties = view.getColumnProperties();
		String[] nameProperties = view.getNameProperties();
		ExcelImportExport excelImportExport = new ExcelImportExportImpl();
		try {
			excelImportExport.export(path, nameProperties, columnProperties, list);
			commonui.info("操作成功", "导出成功");
		} catch (Exception e) {
			commonui.error("提示","导出时发生错误",e);
		}
		
	}

	@Override
	protected View createView(Composite c) {
		view=new CarparkPayHistoryListView(c, c.getStyle());
		return view;
	}
	public void split(Date start, Date end) {
		try {
			List<SingleCarparkMonthlyUserPayHistory> list = sp.getCarparkService().findMonthlyUserPayHistoryByCondition(0,Integer.MAX_VALUE,userName,operaName,null,end);
			List<SingleCarparkMonthlyUserPayHistory> newList = new ArrayList<>();
			for (SingleCarparkMonthlyUserPayHistory monthlyUserPayHistory : list) {
				Float chargesMoney = monthlyUserPayHistory.getChargesMoney();
				if (chargesMoney==null||chargesMoney==0) {
					continue;
				}
				Date oldOverDueTime = monthlyUserPayHistory.getOldOverDueTime();
				oldOverDueTime=oldOverDueTime==null?monthlyUserPayHistory.getCreateTime():oldOverDueTime;
				Date overdueTime = monthlyUserPayHistory.getOverdueTime();
				int month=0;
				if (oldOverDueTime!=null&&oldOverDueTime.before(start)&&start.getTime()-oldOverDueTime.getTime()>1000) {
					month+=countMonth(new DateTime(oldOverDueTime).plusSeconds(1).toDate(),start);
					monthlyUserPayHistory.setOldOverDueTime(start);
				}
				if (overdueTime.after(end)) {
					month+=countMonth(end,overdueTime);
				}
				if (month>0) {
					Long monthChargeId = monthlyUserPayHistory.getMonthChargeId();
					if (monthChargeId==null) {
						continue;
					}
					SingleCarparkMonthlyCharge mc = sp.getCarparkService().findMonthlyChargeById(monthChargeId);
					if (mc==null) {
						continue;
					}
					float m=mc.getPrice()/mc.getRentingDays()*month;
					chargesMoney=chargesMoney-m;
					if (chargesMoney<0) {
						continue;
					}
					monthlyUserPayHistory.setOverdueTime(end);
					monthlyUserPayHistory.setChargesMoney(chargesMoney);
				}
				newList.add(monthlyUserPayHistory);
			}
			populate(newList);
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("错误", "操作失败", e);
		}
	}
	private static int countMonth(Date oldOverDueTime, Date start) {
		int month=0;
		DateTime s = new DateTime(oldOverDueTime);
		DateTime e = new DateTime(start);
		int year = s.getYear();
		int monthOfYear = s.getMonthOfYear();
		int year2 = e.getYear();
		int monthOfYear2 = e.getMonthOfYear();
		while(year!=year2||monthOfYear!=monthOfYear2){
			month++;
			s=s.plusMonths(1);
			year = s.getYear();
			monthOfYear = s.getMonthOfYear();
		}
		return month;
	}
}
