package com.donglu.carpark.ui.view.carcheck;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkManagePresenter;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.free.wizard.AddTempCarFreeWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarCheckHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarCheckListPresenter extends AbstractListPresenter<CarCheckHistory> {
	CarCheckListView view;

	String plateNo;
	@Inject
	CarparkManagePresenter carparkManagePresenter;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;


	private Date start=StrUtil.getTodayTopTime(new Date());

	private Date end;

	private String type;

	private String status;

	private Boolean isEdit;

	private Integer editPlateSize;

	private double money;


	@Override
	public void add() {
		try {
			addAndEdit(new SingleCarparkFreeTempCar());
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("", "添加临时车优惠失败", e);
		}

	}
	@Override
	protected List<CarCheckHistory> findListInput() {
		return sp.getCarparkInOutService().findByMap(current, pageSize, CarCheckHistory.class, getMap());
	}

	@Override
	protected int getTotalSize() {
		return sp.getCarparkInOutService().countByMap(CarCheckHistory.class, getMap()).intValue();
	}
	
	private Map<String,Object> getMap() {
		Map<String, Object> map=new HashMap<String, Object>();
		if (!StrUtil.isEmpty(plateNo)) {
			map.put("plate-like", "%"+plateNo+"%");
		}
		map.put("time-ge", start);
		map.put("time-le", end);
		map.put("type", type);
		map.put("status", status);
		map.put("editedPlate", isEdit);
		map.put(CarCheckHistory.Label.editedPlateSize.name()+"-ge", editPlateSize);
		if (money>0) {
			map.put(CarCheckHistory.Label.shouldMoney.name() + "-ge", money);
		}
		return map;
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
		sp.getCarparkInOutService().saveTempCarFree(model);//
		refresh();
	}

	@Override
	public void delete(List<CarCheckHistory> list) {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		boolean confirm = commonui.confirm("提示", "确认删除选中的"+list.size()+"条记录");
		if (!confirm) {
			return;
		}
		try {
			for (CarCheckHistory ft : list) {
				sp.getCarparkInOutService().deleteEntity(ft);
			}
			commonui.info("成功", "删除临时车优惠成功");
			refresh();
		} catch (Exception e) {
			commonui.error("失败", "删除临时车优惠失败", e);
		}
	}
	

	public void search(String plateNo, Date start, Date end, String type, String status, Boolean isEdit, Integer editPlateSize, double money) {
		this.plateNo = plateNo;
		this.start = start;
		this.end = end;
		this.type = type;
		this.status = status;
		this.isEdit = isEdit;
		this.editPlateSize = editPlateSize;
		this.money = money;
		refresh();
	}


	@Override
	protected View createView(Composite c) {
		view = new CarCheckListView(c, c.getStyle());
		return view;
	}
	@Override
	protected void continue_go() {
		view.setTableTitle("弹窗确认列表");
		view.setShowMoreBtn(false);
		refresh();
	}
	@Override
	public void mouseDoubleClick(List<CarCheckHistory> list) {
		if (list.isEmpty()) {
			return;
		}
		CarCheckHistory history = list.get(0);
		ImageDialog id=new ImageDialog(history.getBigImage());
		id.open();
	}
}
