package com.donglu.carpark.ui.view.store;


import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.list.store.StoreChargeListPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.google.inject.Inject;

public class StoreChargePresenter  extends AbstractListPresenter<SingleCarparkStore>{
	private StoreChargeView view;
	@Inject
	private StoreChargeListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new StoreChargeView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public StoreChargeListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String storeName, String operaName, Date start, Date end) {
		listPresenter.search(storeName,operaName,start,end);
	}
	
}
