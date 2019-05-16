package com.donglu.carpark.ui.view.inouthistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.google.inject.Inject;

public class CarPayListPresenter extends AbstractListPresenter<CarPayHistory> {
	private CarPayListView view;
	private String plateNo;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private Date start;

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
}
