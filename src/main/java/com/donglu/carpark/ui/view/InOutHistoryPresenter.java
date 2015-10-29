package com.donglu.carpark.ui.view;

import java.util.Date;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.InOutHistoryListPresenter;
import com.donglu.carpark.ui.list.ReturnAccountListPresenter;
import com.google.inject.Inject;

public class InOutHistoryPresenter  extends AbstractListPresenter{
	private InOutHistoryView view;
	@Inject
	private InOutHistoryListPresenter listPresenter;
	
	@Override
	public void go(Composite c) {
		view=new InOutHistoryView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
	}
	public void search(String plateNo, String returnUser, Date start, Date end, String operaName, String carType, String inout, String inDevice, String outDevice, String returnAccount) {
		listPresenter.search( plateNo,  returnUser,  start,  end,  operaName,  carType,  inout,  inDevice,  outDevice,  returnAccount);
	}
	public int[] countMoney() {
		
		return listPresenter.countMoney();
	}
	
	public InOutHistoryListPresenter getListPresenter() {
		return listPresenter;
	}
}
