package com.donglu.carpark.ui.view.main;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;

public class AboutPresenter extends AbstractPresenter {

	@Override
	protected View createView(Composite c) {
		return new AboutView(c);
	}

}
