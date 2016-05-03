package com.donglu.carpark.ui.view.visitor;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkManagePresenter;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class VisitorListPresenter extends AbstractListPresenter<SingleCarparkVisitor>{
	private static final Logger log = LoggerFactory.getLogger(VisitorListPresenter.class);
	VisitorListView view;
	
	String userName; 
	String plateNo;
	@Inject
	CarparkManagePresenter carparkManagePresenter;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new VisitorListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("固定用户列表");
		view.setShowMoreBtn(false);
		refresh();
	}
	


	@Override
	public void add() {}

	@Override
	public void delete(List<SingleCarparkVisitor> list) {}

	@Override
	public void refresh() {}

	public void search(String userName, String plateNo) {
		this.userName=userName;
		this.plateNo=plateNo;
		refresh();
	}

	public void pay() {}

	public void importAll() {
		try {
			String path = commonui.selectToSave();
			if (StrUtil.isEmpty(path)) {
				return;
			}
			ExcelImportExport export=new ExcelImportExportImpl();
			int excelRowNum = export.getExcelRowNum(path);
			if (excelRowNum<2) {
				return;
			}
			int importUser = export.importUser(path, sp);
			if (importUser>0) {
				commonui.info("导入提示", "导入完成。有"+importUser+"条数据导入失败");
			}else{
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.固定用户, "导入了"+(excelRowNum-3)+"条记录",System.getProperty("userName"));
				commonui.info("导入提示", "导入成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("导入提示", "导入失败");
		}finally{
			refresh();
		}
		
	}

	public void exportAll() {}

	public void edit() {}
	
}
