package com.donglu.carpark.ui.list;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class ReturnAccountListPresenter extends AbstractListPresenter<SingleCarparkReturnAccount> {
	private ReturnAccountListView v;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	private String operaName;
	private String userName;
	private Date start;
	private Date end;

	public void search(String operaName, String userName, Date start, Date end) {
		AbstractListView<SingleCarparkReturnAccount>.Model model = v.getModel();
		model.setList(new ArrayList<>());
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkReturnAccount> findMonthlyUserPayHistoryByCondition = carparkService.findReturnAccountByCondition(model.getList().size(), 50, userName, operaName, start, end);
		int countMonthlyUserPayHistoryByCondition = carparkService.countReturnAccountByCondition(userName, operaName, start, end);
		model.setCountSearchAll(countMonthlyUserPayHistoryByCondition);
		model.AddList(findMonthlyUserPayHistoryByCondition);
		model.setCountSearch(model.getList().size());

	}

	public void searchMore() {
		AbstractListView<SingleCarparkReturnAccount>.Model model = v.getModel();
		if (model.getCountSearchAll() <= model.getCountSearch()) {
			return;
		}
		CarparkService carparkService = sp.getCarparkService();
		List<SingleCarparkReturnAccount> findMonthlyUserPayHistoryByCondition = carparkService.findReturnAccountByCondition(model.getList().size(), 50, userName, operaName, start, end);
		int countMonthlyUserPayHistoryByCondition = carparkService.countReturnAccountByCondition(userName, operaName, start, end);
		model.setCountSearchAll(countMonthlyUserPayHistoryByCondition);
		model.AddList(findMonthlyUserPayHistoryByCondition);
		model.setCountSearch(model.getList().size());
	}

	public void export() {
		List<SingleCarparkReturnAccount> list = v.getModel().getList();
		if (StrUtil.isEmpty(list)) {
			return;
		}
		String selectToSave = commonui.selectToSave();
		if (StrUtil.isEmpty(selectToSave)) {
			return;
		}
		String path = StrUtil.checkPath(selectToSave, new String[] { ".xls", ".xlsx" }, ".xls");
		String[] columnProperties = v.getColumnProperties();
		String[] nameProperties = v.getNameProperties();
		ExcelImportExport excelImportExport = new ExcelImportExportImpl();
		try {
			excelImportExport.export(path, nameProperties, columnProperties, list);
			commonui.info("操作成功", "导出成功");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@Override
	protected View createView(Composite c) {
		v = new ReturnAccountListView(c, c.getStyle());
		return v;
	}

}
