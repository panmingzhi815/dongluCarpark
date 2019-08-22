package com.donglu.carpark.ui.view.inouthistory.event;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.google.inject.Inject;

public class EventPresenter  implements Presenter{
	private EventView view;
	@Inject
	private EventListPresenter listPresenter;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private List<SingleCarparkMonthlyCharge> listMonthlyCharge=new ArrayList<SingleCarparkMonthlyCharge>();
	@Override
	public void go(Composite c) {
		view=new EventView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public EventListPresenter getListPresenter() {
		return listPresenter;
	}
	@Override
	public String getTitle() {
		return "固定车查询";
	}
	public void search(String text, Date selection, Date selection2) {
		listPresenter.search(text,selection,selection2);
	}
}
