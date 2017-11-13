package com.donglu.carpark.ui.common;

import java.util.ArrayList;
import java.util.List;


import com.dongluhitec.card.domain.util.StrUtil;


public abstract class AbstractListPresenter<T> extends AbstractPresenter implements ListPresenter<T> {
	int current=0;
	@Override
	public void add() {
		
	}
	
	@Override
	public void delete(List<T> list) {
	}

	@Override
	public void refresh() {
		current=0;
		getView().getModel().setList(new ArrayList<>());
		populate();
	}
	public void populate(){
		List<T> findListInput = findListInput();
		getView().getModel().AddList(findListInput);
		getView().getModel().setCountSearchAll(getTotalSize());
		getView().getModel().setCountSearch(getView().getModel().getList().size());
	}
	public void populate(List<T> listInput){
		getView().getModel().setList(listInput);
		getView().getModel().setCountSearchAll(listInput.size());
		getView().getModel().setCountSearch(getView().getModel().getList().size());
	}

	protected List<T> findListInput(){
		return new ArrayList<>();
	}
	protected int getTotalSize(){
		return 0;
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
	
	@Override
	public AbstractListView<T> getView() {
		return (AbstractListView<T>) super.getView();
	}

}
