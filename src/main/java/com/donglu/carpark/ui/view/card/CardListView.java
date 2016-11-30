package com.donglu.carpark.ui.view.card;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListView;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCard;

public class CardListView extends AbstractListView<SingleCarparkCard>{

	public CardListView(Composite parent, int style) {
		super(parent, style, SingleCarparkCard.class, new String[]{"serialNumber","user"}, new String[]{"内码","用户"}, new int[]{100,200},  new int[]{0,0});
	}

}
