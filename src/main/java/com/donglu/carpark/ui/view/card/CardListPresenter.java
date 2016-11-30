package com.donglu.carpark.ui.view.card;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;

public class CardListPresenter extends AbstractListPresenter<SingleCarparkCard> {

	@Override
	protected View createView(Composite c) {
		return new CardListView(c, c.getStyle());
	}
	@Override
	public void refresh() {
		
	}
}
