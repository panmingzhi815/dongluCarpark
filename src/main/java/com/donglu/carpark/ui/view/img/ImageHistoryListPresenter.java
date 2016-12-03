package com.donglu.carpark.ui.view.img;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkInOutServiceI;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkOffLineHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkImageHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class ImageHistoryListPresenter extends AbstractListPresenter<SingleCarparkImageHistory> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploadServlet.class);
	private ImageHistoryListView v;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;

	private Date start;
	private Date end;
	private String plate;
	private String type;

	@Override
	public void refresh() {
		v.getModel().setList(new ArrayList<>());
		defaultSearch();
	}

	private void defaultSearch() {
		AbstractListView<SingleCarparkImageHistory>.Model model = v.getModel();
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkImageHistory> list = carparkInOutService.findImageHistoryBySearch(model.getList().size(), 500, plate, type, start, end);
		int countByCondition =carparkInOutService.countImageHistoryBySearch(plate, type, start, end);
		model.setCountSearchAll(countByCondition);
		model.AddList(list);
		model.setCountSearch(model.getList().size());
		count(model.getList());
	}


	private void count(List<SingleCarparkImageHistory> list) {
		int nomal=0;
		int t=0;
		int f=0;
		for (SingleCarparkImageHistory ih : list) {
			String type = ih.getType();
			switch (type) {
			case "原始":
				nomal++;
				break;
			case "正确":
				t++;
				break;
			case "错误":
				f++;
				break;
			}
		}
		v.setInfo(nomal, t, f);
	}

	public void searchMore() {
		AbstractListView<SingleCarparkImageHistory>.Model model = v.getModel();
		if (model.getCountSearchAll() <= model.getCountSearch()) {
			return;
		}
		defaultSearch();
	}
	public void exportSearch() {
		List<SingleCarparkImageHistory> list = v.getModel().getList();
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
			LOGGER.info("导出{}条进出场记录成功",list.size());
		} catch (Exception e) {
			commonui.info("操作失败", "操作失败"+e.getMessage());
			LOGGER.info("导出进出场记录失败",e);
		}
	}

	@Override
	protected View createView(Composite c) {
		v = new ImageHistoryListView(c, c.getStyle());
		return v;
	}

	public void search(String plate,String type, Date start, Date end) {
		this.plate = plate;
		this.type = type;
		this.start = start;
		this.end = end;
		refresh();
	}

	@Override
	public void mouseDoubleClick(List<SingleCarparkImageHistory> list) {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		SingleCarparkImageHistory carparkOffLineHistory = list.get(0);
		String bigImage = carparkOffLineHistory.getBigImage();
		if (StrUtil.isEmpty(bigImage)) {
			return;
		}
		ImageDialog d=new ImageDialog(bigImage);
		d.open();
	}

	public void setTrue() {
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkImageHistory> selected = v.getModel().getSelected();
		for (SingleCarparkImageHistory singleCarparkImageHistory : selected) {
			singleCarparkImageHistory.setType("正确");
			carparkInOutService.saveImageHistory(singleCarparkImageHistory);
		}
		refresh();
	}

	public void setFalse() {
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		List<SingleCarparkImageHistory> selected = v.getModel().getSelected();
		for (SingleCarparkImageHistory singleCarparkImageHistory : selected) {
			singleCarparkImageHistory.setType("错误");
			carparkInOutService.saveImageHistory(singleCarparkImageHistory);
		}
		refresh();
	}
	@Override
	public void delete(List<SingleCarparkImageHistory> list) {
		CarparkInOutServiceI carparkInOutService = sp.getCarparkInOutService();
		for (SingleCarparkImageHistory singleCarparkImageHistory : list) {
			carparkInOutService.deleteImageHistory(singleCarparkImageHistory);
		}
		refresh();
	}
}
