package com.donglu.carpark.ui.common;

import java.util.List;

import org.eclipse.swt.widgets.Composite;


public abstract class AbstractListPresenter<T> implements Presenter {

	@Override
	public abstract void go(Composite c);

	@Override
	public void add() {
		
	}
	
	@Override
	public void delete() {
	}

	@Override
	public void refresh() {
		
	}

}
