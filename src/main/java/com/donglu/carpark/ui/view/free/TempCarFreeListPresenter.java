package com.donglu.carpark.ui.view.free;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkManagePresenter;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.free.wizard.AddTempCarFreeWizard;
import com.donglu.carpark.util.ExcelImportExport;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class TempCarFreeListPresenter extends AbstractListPresenter<SingleCarparkFreeTempCar> {
	TempCarFreeListView view;

	String userName;
	String plateNo;
	@Inject
	CarparkManagePresenter carparkManagePresenter;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;


	@Override
	public void add() {
		try {
			addAndEdit(new SingleCarparkFreeTempCar());
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("", "添加临时车优惠失败", e);
		}

	}

	/**
	 * @param model
	 */
	private void addAndEdit(SingleCarparkFreeTempCar model) {
		AddTempCarFreeWizard wizard = new AddTempCarFreeWizard(model);
		Object showWizard = commonui.showWizard(wizard);
		if (StrUtil.isEmpty(showWizard)) {
			return;
		}
		sp.getCarparkInOutService().saveTempCarFree(model);
		refresh();
	}

	@Override
	public void delete(List<SingleCarparkFreeTempCar> list) {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		boolean confirm = commonui.confirm("提示", "确认删除选中的"+list.size()+"条记录");
		if (!confirm) {
			return;
		}
		try {
			for (SingleCarparkFreeTempCar ft : list) {
				sp.getCarparkInOutService().deleteTempCarFree(ft);
			}
			commonui.info("成功", "删除临时车优惠成功");
			refresh();
		} catch (Exception e) {
			commonui.error("失败", "删除临时车优惠失败", e);
		}
	}
	Map<String, SingleCarparkFreeTempCar> mapPlateFree=new HashMap<>();
	@Override
	public void refresh() {
		mapPlateFree.clear();
		List<SingleCarparkFreeTempCar> findVisitorByLike = sp.getCarparkInOutService().findTempCarFreeByLike(0, Integer.MAX_VALUE,plateNo);
		view.getModel().setList(findVisitorByLike);
		for (SingleCarparkFreeTempCar singleCarparkFreeTempCar : findVisitorByLike) {
			mapPlateFree.put(singleCarparkFreeTempCar.getPlateNo(), singleCarparkFreeTempCar);
		}
	}

	public void search(String plateNo) {
		this.plateNo = plateNo;
		refresh();
	}

	public void edit() {
		try {
			List<SingleCarparkFreeTempCar> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				return;
			}
			SingleCarparkFreeTempCar visitor = selected.get(0);
			addAndEdit(visitor);
		} catch (Exception e) {
			commonui.error("提示", "修改临时车优惠失败", e);
		}
	}

	@Override
	protected View createView(Composite c) {
		view = new TempCarFreeListView(c, c.getStyle());
		return view;
	}
	@Override
	protected void continue_go() {
		view.setTableTitle("临时车永久优惠列表");
		view.setShowMoreBtn(false);
		refresh();
	}

	public void importByUser() {
		String path = commonui.selectToSave();
		if (StrUtil.isEmpty(path)) {
			return;
		}
		ExcelImportExport export=new ExcelImportExportImpl();
		try {
			List<String> list=export.importPlateNOByUser(path);
			if (list.isEmpty()) {
				return;
			}
			AddTempCarFreeWizard w = new AddTempCarFreeWizard(new SingleCarparkFreeTempCar(),true);
			SingleCarparkFreeTempCar m = (SingleCarparkFreeTempCar) commonui.showWizard(w);
			if (m==null) {
				return;
			}
			for (String string : list) {
				if (mapPlateFree.get(string)!=null) {
					continue;
				}
				m.setPlateNo(string);
				sp.getCarparkInOutService().saveTempCarFree(m);
			}
			commonui.info("提示", "导入完成");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "导入时发生错误"+e,e);
		}
	}

}
