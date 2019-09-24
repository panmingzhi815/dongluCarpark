package com.donglu.carpark.ui.view.inouthistory;


import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.dongluhitec.card.domain.db.singlecarpark.CarPayHistory;
import com.google.inject.Inject;

public class CarPayPresenter  extends AbstractPresenter{
	@Inject
	private CarPayListPresenter listPresenter;
	private ScheduledExecutorService scheduledExecutorService;
	long lastId=0;
	
	public CarPayListPresenter getListPresenter() {
		return listPresenter;
	}
	
	public void search(String plateNo, Date start, Date end) {
		listPresenter.search(plateNo,start,end);
	}
	@Override
	protected CarPayView createView(Composite c) {
		return new CarPayView(c, c.getStyle());
	}

	@Override
	protected void continue_go() {
		listPresenter.go(getView().getListComposite());
	}

	@Override
	public CarPayView getView() {
		return (CarPayView) super.getView();
	}

	public void startAutoRefresh(boolean selection) {
		lastId=0;
		for (CarPayHistory carPayHistory : listPresenter.getView().getModel().getList()) {
			if (carPayHistory.getId()>lastId) {
				lastId=carPayHistory.getId();
			}
		}
		if (selection) {
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						listPresenter.refresh();
						if (listPresenter.getView().getModel().getList().size()>0) {
							CarPayHistory history = listPresenter.getView().getModel().getList().get(0);
							if (history.getId()>lastId) {
								getView().setShellFocus();
								lastId=history.getId();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 2, 2, TimeUnit.SECONDS);
		}else{
			scheduledExecutorService.shutdown();
		}
	}

	public void export() {
		listPresenter.export();
	}
	
}
