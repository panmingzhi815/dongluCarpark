package com.donglu.carpark.ui.view.img;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.Presenter;
import com.google.inject.Inject;

public class ImageHistoryPresenter  implements Presenter{
	private ImageHistoryView view;
	@Inject
	private ImageHistoryListPresenter listPresenter;
	
	@Override
	public void go(Composite c) {
		view=new ImageHistoryView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public ImageHistoryListPresenter getListPresenter() {
		return listPresenter;
	}
	public void exportSearch() {
		listPresenter.exportSearch();	
	}
	public void search(String plate,String type, Date start, Date end) {
		listPresenter.search(plate,type,  start, end);
	}
}
