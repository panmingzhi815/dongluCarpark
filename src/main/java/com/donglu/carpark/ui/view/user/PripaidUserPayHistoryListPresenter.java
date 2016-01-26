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
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkPrepaidUserPayHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class PripaidUserPayHistoryListPresenter  extends AbstractListPresenter<SingleCarparkPrepaidUserPayHistory>{
	
	private PrepaidUserPayHistoryListView view;
	@Inject
	private CommonUIFacility commonui;
	
	private String userName, operaName;
	private Date start,  end;
	
	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	public Composite getView(Composite parent, int style){
		view=new PrepaidUserPayHistoryListView(parent, style);
		view.setPresenter(this);
		return view;
	}
	
	@Override
	public void refresh(){
		AbstractListView<SingleCarparkPrepaidUserPayHistory>.Model model = view.getModel();
		List<SingleCarparkPrepaidUserPayHistory> list =sp.getCarparkUserService().findPrepaidUserPayHistoryList(0,50,userName,operaName,start,end);
		int countSearchAll=sp.getCarparkUserService().countPrepaidUserPayHistoryList(userName,operaName,start,end);
		model.setList(list);
		model.setCountSearch(list.size());
		model.setCountSearchAll(countSearchAll);
	}
	public void searMore(){
		AbstractListView<SingleCarparkPrepaidUserPayHistory>.Model model = view.getModel();
		if (model.getCountSearchAll()<=model.getCountSearch()) {
			return;
		}
		List<SingleCarparkPrepaidUserPayHistory> list =sp.getCarparkUserService().findPrepaidUserPayHistoryList(model.getList().size(),50,userName,operaName,start,end);
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
		view=new PrepaidUserPayHistoryListView(listComposite, listComposite.getStyle());
		view.setPresenter(this);
	}

	public void export() {
		List<SingleCarparkPrepaidUserPayHistory> list = view.getModel().getList();
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
