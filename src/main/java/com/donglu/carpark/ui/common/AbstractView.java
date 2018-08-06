package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractView extends Composite implements View{

	private Presenter presenter;
	public AbstractView(Composite parent) {
		this(parent,parent.getStyle());
	}
	public AbstractView(Composite parent, int style) {
		super(parent, style);
	}
	@Override
	public Presenter getPresenter() {
		return presenter;
	}
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
}
