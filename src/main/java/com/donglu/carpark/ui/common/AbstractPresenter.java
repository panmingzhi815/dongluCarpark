package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractPresenter implements Presenter {
	View view;
	@Override
	public void go(Composite c) {
		view = createView(c);
		view.setPresenter(this);
		continue_go();
	}

	protected void continue_go(){
		
	}

	protected abstract View createView(Composite c);

	public View getView() {
		return view;
	}
	
}
