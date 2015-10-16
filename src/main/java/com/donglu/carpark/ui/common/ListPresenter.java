package com.donglu.carpark.ui.common;

import java.util.List;

public interface ListPresenter<T> extends Presenter {
	public void add();
	void delete(List<T> list);
	void refresh();
}
