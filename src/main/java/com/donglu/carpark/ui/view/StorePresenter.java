package com.donglu.carpark.ui.view;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.list.StoreListPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.google.inject.Inject;

public class StorePresenter  extends AbstractListPresenter<SingleCarparkStore>{
	private StoreView view;
	@Inject
	private StoreListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new StoreView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public StoreListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String userName, String plateNo, int parseInt, String text2) {
		listPresenter.search(userName,plateNo,parseInt,text2);
	}
	
}
