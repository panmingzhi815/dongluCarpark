package com.donglu.carpark.ui.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.list.ReturnAccountListPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.google.inject.Inject;

public class ReturnAccountPresenter implements Presenter{
	private ReturnAccountView view;
	@Inject
	private ReturnAccountListPresenter listPresenter;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new ReturnAccountView(c, c.getStyle());
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
		
		List<SingleCarparkSystemUser> findAll = new ArrayList<>();
		SingleCarparkSystemUser e = new SingleCarparkSystemUser();
		e.setUserName("全部");
		findAll.add(e);
		findAll.addAll(sp.getSystemUserService().findAllSystemUser());
		view.setCombo(findAll);
	}
	public void search(String operaName, String returnUser, Date start, Date end) {
		listPresenter.search(operaName,returnUser,start,end);
	}
	public void export() {
		listPresenter.export();
	}
	
}
