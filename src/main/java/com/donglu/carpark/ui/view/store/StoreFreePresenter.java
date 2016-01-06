package com.donglu.carpark.ui.view.store;


import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.list.store.StoreFreeListPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkStore;
import com.google.inject.Inject;

public class StoreFreePresenter  extends AbstractListPresenter<SingleCarparkStore>{
	private StoreFreeView view;
	@Inject
	private StoreFreeListPresenter listPresenter;
	@Override
	public void go(Composite c) {
		view=new StoreFreeView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public StoreFreeListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String storeName, String operaName, String used, Date start, Date end) {
		listPresenter.search(storeName,operaName,used,start,end);
	}
	public void export() {
		listPresenter.exportAll();
		
	}
	
}
