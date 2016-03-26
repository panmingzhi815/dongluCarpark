package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractPresenter implements Presenter {

	@Override
	public void go(Composite c) {
		getView(c).setPresenter(this);
		continue_go();
	}

	protected void continue_go(){
		
	}

	protected abstract View getView(Composite c);
	
}
