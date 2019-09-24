package com.donglu.carpark.ui.view.inouthistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CarPayListPresenter extends AbstractListPresenter<CarPayHistory> {
	private CarPayListView view;
	private String plateNo;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Inject
	private CommonUIFacility commonui;
	
	private Date start=StrUtil.getTodayTopTime(new Date());

	private Date end;
	@Override
	public void refresh() {
		view.getModel().setList(new ArrayList<>());
		popule();
	}

	/**
	 * 
	 */
	public void popule() {
		String plateNo2 = "%"+plateNo+"%";
		if (plateNo==null) {
			plateNo2=null;
		}
		List<CarPayHistory> findVisitorByLike = sp.getCarPayService().findCarPayHistoryByLike(view.getModel().getList().size(), 500,plateNo2,start,end);
		int count=sp.getCarPayService().countCarPayHistoryByLike(plateNo2,start,end);
		view.getModel().AddList(findVisitorByLike);
		view.getModel().setCountSearch(view.getModel().getList().size());
		view.getModel().setCountSearchAll(count);
		List<Double> historyMoney = sp.getCarPayService().countCarPayHistoryMoney(plateNo2, start, end);
		view.setLabelText(historyMoney);
	}

	public void search(String plateNo, Date start, Date end) {
		this.plateNo = plateNo;
		this.start = start;
		this.end = end;
		refresh();
	}

	@Override
	protected View createView(Composite c) {
		view = new CarPayListView(c, c.getStyle());
		return view;
	}
	@Override
	protected void continue_go() {
		view.setTableTitle("临时车缴费记录");
		view.setShowMoreBtn(true);
	}

	public void loadMore() {
		popule();
	}

	public void export() {
		String save = commonui.selectToSave();
		if (save==null) {
			return;
		}
		String path = StrUtil.checkPath(save, new String[] {".xls",".xlsx"}, ".xls");
		
		String plateNo2 = "%"+plateNo+"%";
		if (plateNo==null) {
			plateNo2=null;
		}
		List<CarPayHistory> findVisitorByLike = sp.getCarPayService().findCarPayHistoryByLike(0, Integer.MAX_VALUE,plateNo2,start,end);
		List<Double> historyMoney = sp.getCarPayService().countCarPayHistoryMoney(plateNo2, start, end);
		List<Object> list = new ArrayList<>();
		list.add("总计:");
		list.add(historyMoney.get(0).floatValue());
		list.add(historyMoney.get(1));
		list.add(historyMoney.get(2));
		list.add(historyMoney.get(3));
		ArrayList<Object> arrayList = new ArrayList<>(findVisitorByLike);
		arrayList.add(list);
		try {
			new ExcelImportExportImpl().export(path, view.getNameProperties(), view.getColumnProperties(), arrayList);
			commonui.info("提示", "导出成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
