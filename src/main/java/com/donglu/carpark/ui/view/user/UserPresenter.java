package com.donglu.carpark.ui.view.user;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;
import com.google.inject.Inject;

public class UserPresenter  implements Presenter{
	private UserView view;
	@Inject
	private UserListPresenter listPresenter;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	private List<SingleCarparkMonthlyCharge> listMonthlyCharge=new ArrayList<SingleCarparkMonthlyCharge>();
	@Override
	public void go(Composite c) {
		view=new UserView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
		listMonthlyCharge = sp.getCarparkService().findAllMonthlyCharge();
		view.setMonthlyCharges(listMonthlyCharge);
	}
	public UserListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String userName, String plateNo, String address, int monthlySelected, int parseInt, String text2) {
		SingleCarparkMonthlyCharge monthlyCharge=null;
		if (monthlySelected>0) {
			monthlyCharge = listMonthlyCharge.get(monthlySelected-1);
		}
		listPresenter.search(userName,plateNo,address,monthlyCharge,parseInt,text2);
	}
	
}
