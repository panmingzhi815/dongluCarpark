package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPresenter implements Presenter {
	static final Logger logger = LoggerFactory.getLogger(AbstractPresenter.class);
	View view;
	@Override
	public void go(Composite c) {
		try {
			view = createView(c);
			view.setPresenter(this);
			continue_go();
		} catch (Exception e) {
			logger.error(getClass()+"加载失败",e);
		}
	}
	/**
	 * 界面创建后的初始化
	 */
	protected void continue_go(){
		
	}

	protected abstract View createView(Composite c);

	public View getView() {
		return view;
	}
	
}
