package com.donglu.carpark.ui.view.card;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class CardPresenter  extends AbstractPresenter{
	private CardView view;
	private CardListPresenter listPresenter;
	
	
	@Inject
	public CardPresenter(CardListPresenter listPresenter) {
		this.listPresenter = listPresenter;
	}
	public CardListPresenter getListPresenter() {
		return listPresenter;
	}
	public void search(String serialNumber,String userName, String plateNo) {
		listPresenter.search(serialNumber,userName,plateNo);
	}
	@Override
	protected View createView(Composite c) {
		view=new CardView(c, c.getStyle());
		listPresenter.go(view.getListComposite());
		return view;
	}
	
}
