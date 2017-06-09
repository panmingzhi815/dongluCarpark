package com.donglu.carpark.ui.view.history;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;

public class HistoryPresenter extends AbstractPresenter {

	@Override
	protected HistoryViewer createView(Composite c) {
		return new HistoryViewer(c);
	}
	@Override
	protected void continue_go() {
		List<SingleCarparkModuleEnum> list = SingleCarparkModuleEnum.getByParent(null);
		
	}

}
