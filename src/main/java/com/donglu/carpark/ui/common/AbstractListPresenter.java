package com.donglu.carpark.ui.common;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.dongluhitec.card.domain.util.StrUtil;


public abstract class AbstractListPresenter<T> implements ListPresenter<T> {
	@Override
	public abstract void go(Composite c);

	@Override
	public void add() {
		
	}
	
	@Override
	public void delete(List<T> list) {
	}

	@Override
	public void refresh() {
		
	}

	@Override
	public void mouseDoubleClick(List<T> list) {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		edit(list.get(0));
	}

	protected void edit(T t) {
		
	}

}
