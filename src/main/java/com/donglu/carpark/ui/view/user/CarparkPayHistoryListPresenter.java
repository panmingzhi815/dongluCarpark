package com.donglu.carpark.ui.view.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	boolean isSplit=false;
	

	
	@Override
	public void refresh(){
		List<SingleCarparkMonthlyUserPayHistory> list =sp.getCarparkService().findMonthlyUserPayHistoryByCondition(0,500,userName,operaName,start,end);
		populate(list);
		isSplit=false;
	}
	private void populate(List<SingleCarparkMonthlyUserPayHistory> list) {
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = view.getModel();
		int countSearchAll=sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName,operaName,start,end);
		model.setList(list);
		model.setCountSearch(list.size());
		model.setCountSearchAll(countSearchAll);
		setTotalMoney(list);
	}
	/**
	 * @param list
	 */
	public void setTotalMoney(List<SingleCarparkMonthlyUserPayHistory> list) {
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
		if (isSplit) {
			return;
		}
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = view.getModel();
		if (model.getCountSearchAll()<=model.getCountSearch()) {
			return;
		}
		List<SingleCarparkMonthlyUserPayHistory> list =sp.getCarparkService().findMonthlyUserPayHistoryByCondition(model.getList().size(),500,userName,operaName,start,end);
		int countSearchAll=sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName,operaName,start,end);
		model.AddList(list);
		model.setCountSearch(model.getList().size());
		model.setCountSearchAll(countSearchAll);
		setTotalMoney(model.getList());
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
			start=StrUtil.getYearTopTime(end);
			end=StrUtil.getYearBottomTime(end);
			List<SingleCarparkMonthlyUserPayHistory> list = sp.getCarparkService().findMonthlyUserPayHistoryByValidTo(0,Integer.MAX_VALUE,userName,operaName,start);
			//缓存收费设置
			Map<Long, SingleCarparkMonthlyCharge> map=new HashMap<Long, SingleCarparkMonthlyCharge>();
			List<SingleCarparkMonthlyUserPayHistory> newList = new ArrayList<>();
			int i=1;
			for (SingleCarparkMonthlyUserPayHistory monthlyUserPayHistory : list) {
				System.out.println(i+++"=正在处理记录"+monthlyUserPayHistory.getId()+"=="+monthlyUserPayHistory.getUserName());
				Float chargesMoney = monthlyUserPayHistory.getChargesMoney();
				if (chargesMoney==null||chargesMoney.floatValue()==0||monthlyUserPayHistory.getPayType()==1) {
					continue;
				}
				Long monthChargeId = monthlyUserPayHistory.getMonthChargeId();
				if (monthChargeId==null) {
					continue;
				}
				SingleCarparkMonthlyCharge mc=map.get(monthChargeId);
				if (mc==null) {
					mc = sp.getCarparkService().findMonthlyChargeById(monthChargeId);
					if (mc == null) {
						continue;
					}
					map.put(monthChargeId, mc);
				}
				Date overdueTime = monthlyUserPayHistory.getOverdueTime();
				if (mc.getPrice()<=0) {
					if (overdueTime.before(end)) {
						newList.add(monthlyUserPayHistory);
					}
					continue;
				}
				int chargeMonth=(int) (chargesMoney/mc.getPrice()*mc.getRentingDays());
				Date oldOverDueTime=new DateTime(overdueTime).minusMonths(chargeMonth).toDate();
				long l = oldOverDueTime.getTime()-end.getTime();
				if (Math.abs(l)<1000||l>1000) {
					continue;
				}
				
				if (oldOverDueTime!=null&&overdueTime.getTime()-oldOverDueTime.getTime()<1000*60*60&&chargesMoney%mc.getPrice()==0) {
					monthlyUserPayHistory.setOldOverDueTime(oldOverDueTime);
					sp.getCarparkService().saveMonthlyUserPayHistory(monthlyUserPayHistory);
				}
				if (oldOverDueTime.after(start)&&overdueTime.before(end)) {
					monthlyUserPayHistory.setRemark(new DateTime(end).getYear()+"分账");
					newList.add(monthlyUserPayHistory);
					continue;
				}
				
				int month=0;
				if (overdueTime.after(start)&&overdueTime.before(end)) {
					month=countMonth(start,overdueTime);
					monthlyUserPayHistory.setOldOverDueTime(start);
					float m=mc.getPrice()/mc.getRentingDays()*month;
					monthlyUserPayHistory.setChargesMoney(m);
				}
				if (overdueTime.after(end)) {
					month=countMonth(end,overdueTime);
					float m=mc.getPrice()/mc.getRentingDays()*month;
					monthlyUserPayHistory.setChargesMoney(chargesMoney-m);
				}
				monthlyUserPayHistory.setRemark(new DateTime(end).getYear()+"分账");
				newList.add(monthlyUserPayHistory);
			}
			System.out.println("记录处理完成");
			populate(newList);
			int size = newList.size();
			view.getModel().setCountSearch(size);
			view.getModel().setCountSearchAll(size);
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
		int size=0;
		if (e.getYear()-s.getYear()==1&&s.plusSeconds(1).getYear()==e.getYear()) {
			size=1;
		}
		return (year2-year-size)*12+e.getMonthOfYear();
//		int monthOfYear2 = e.getMonthOfYear();
//		while(year!=year2||monthOfYear!=monthOfYear2){
//			month++;
//			s=s.plusMonths(1);
//			year = s.getYear();
//			monthOfYear = s.getMonthOfYear();
//		}
//		return month;
	}
}
