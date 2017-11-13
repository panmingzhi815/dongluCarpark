package com.donglu.carpark.ui.view.inouthistory;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.joda.time.DateTime;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.google.inject.Inject;

public class CarInHistoryListPresenter extends AbstractListPresenter<SingleCarparkInOutHistory> {
	@Inject
	CarparkDatabaseServiceProvider sp;
	private String carType;
	private int maxStayMinute = 1440;
	private int minStayMinute = 60;

	@Override
	protected View createView(Composite c) {
		InOutHistoryListView inOutHistoryListView = new InOutHistoryListView(c, c.getStyle());
		return inOutHistoryListView;
	}

	@Override
	public InOutHistoryListView getView() {
		return (InOutHistoryListView) super.getView();
	}

	@Override
	protected void continue_go() {
		getView().setTableColumn(new String[] { SingleCarparkInOutHistory.Property.plateNo.name(), SingleCarparkInOutHistory.Property.userName.name(),
				SingleCarparkInOutHistory.Label.inTimeLabel.name(), SingleCarparkInOutHistory.Property.inDevice.name(),
				SingleCarparkInOutHistory.Label.stillTimeLabel.name(),}, 
				new String[] { "车牌号", "用户名", "进场时间", "进场设备","停留时长" },
				new int[] { 100, 100, 180, 100,180 }, null);
		getView().createTable();
		getView().setShowMoreBtn(false);
		getView().setShow(false);
	}

	public List<SingleCarparkInOutHistory> findListInput() {
		Date in = null;
		if (maxStayMinute > 0) {
			in = new DateTime().minusMinutes(maxStayMinute).toDate();
		}
		Date out = null;
		if (minStayMinute >= 0) {
			out = new DateTime().minusMinutes(minStayMinute).toDate();
		}
		return sp.getCarparkInOutService().findHistoryByIn(0,Integer.MAX_VALUE,null,carType,in,out);
	}

	public void search(int userType, int minStayMinute, int maxStayMinute) {
		switch (userType) {
		case 0:
			carType=null;
			break;
		case 1:
			carType="固定车";
			break;
		case 2:
			carType="临时车";
			break;

		}
		this.minStayMinute = minStayMinute;
		this.maxStayMinute = maxStayMinute;
		refresh();
	}

}
