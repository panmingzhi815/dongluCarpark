package com.donglu.carpark.ui.view.user;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
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
	
	public Composite getView(Composite parent, int style){
		view=new CarparkPayHistoryListView(parent, style);
		view.setPresenter(this);
		return view;
	}
	
	public void refresh(){
		AbstractListView<SingleCarparkMonthlyUserPayHistory>.Model model = view.getModel();
		List<SingleCarparkMonthlyUserPayHistory> list =sp.getCarparkService().findMonthlyUserPayHistoryByCondition(0,50,userName,operaName,start,end);
		int countSearchAll=sp.getCarparkService().countMonthlyUserPayHistoryByCondition(userName,operaName,start,end);
		model.setList(list);
		model.setCountSearch(list.size());
		model.setCountSearchAll(countSearchAll);
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
			e.printStackTrace();
		}
		
	}
}
