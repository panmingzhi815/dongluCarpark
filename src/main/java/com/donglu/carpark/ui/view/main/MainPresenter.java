package com.donglu.carpark.ui.view.main;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;

public class MainPresenter extends AbstractPresenter {
	
	public MainPresenter() {
	}

	@Override
	protected View createView(Composite c) {
		return new MainView(c);
	}
	@Override
	public MainView getView() {
		return (MainView) super.getView();
	}
	@Override
	protected void continue_go() {
		getView().addModules(Stream.of(SingleCarparkModuleEnum.values()).filter(t->t.getParent()==null).collect(Collectors.toList()));
	}
}
