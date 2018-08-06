package com.donglu.carpark.ui.view.history;


import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;

public class HistoryPresenter extends AbstractPresenter {

	@Override
	protected HistoryViewer createView(Composite c) {
		return new HistoryViewer(c);
	}
	@Override
	public HistoryViewer getView() {
		return (HistoryViewer) super.getView();
	}
	@Override
	protected void continue_go() {
		getView().init(SingleCarparkModuleEnum.getByParent(SingleCarparkModuleEnum.记录查询));
	}

}
