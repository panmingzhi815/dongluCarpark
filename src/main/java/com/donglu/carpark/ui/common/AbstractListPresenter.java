package com.donglu.carpark.ui.common;

import java.util.ArrayList;
import java.util.List;

import com.dongluhitec.card.domain.util.StrUtil;


public abstract class AbstractListPresenter<T> extends AbstractPresenter implements ListPresenter<T> {

	private T entity;
	private SelectedRun<T> selectedRun;
	@Override
	public void add() {
		
	}
	
	@Override
	public void delete(List<T> list) {
	}

	@Override
	public void refresh() {
		getView().getModel().setList(new ArrayList<>());
		search();
	}

	/**
	 * 
	 */
	public void search() {
		List<T> findByNameOrPlateNo = getListInput(getView().getModel().getCountSearch());
		getView().getModel().AddList(findByNameOrPlateNo);
		getView().getModel().setCountSearchAll(getTotalSize());
	}
	public List<T> getListInput(int size){
		return new ArrayList<>();
	};
	public int getTotalSize(){
		return 0;
	}
	@Override
	public void searchMore() {
		search();
	}
	@Override
	public void mouseDoubleClick(List<T> list) {
		if (StrUtil.isEmpty(list)) {
			return;
		}
		entity = list.get(0);
		edit(entity);
	}
	
	@Override
	public void selected(List<T> list) {
		if (selectedRun==null) {
			return;
		}
		selectedRun.run(list);
	}
	

	protected void edit(T t) {
		
	}
	@Override
	public AbstractListView<T> getView() {
		return (AbstractListView<T>) super.getView();
	}
	public void setSelectedRun(SelectedRun<T> selectedRun){
		this.selectedRun = selectedRun;
	}

	public T getEntity() {
		return entity;
	}
	public interface SelectedRun<R>{
		public void run(List<R> list);
	}
}
