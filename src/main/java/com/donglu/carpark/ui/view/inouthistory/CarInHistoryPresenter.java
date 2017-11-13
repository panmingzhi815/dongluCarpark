package com.donglu.carpark.ui.view.inouthistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.CarparkFileUtils;
import com.google.inject.Inject;

public class CarInHistoryPresenter extends AbstractPresenter {
	
	private static final String SETTING_NAME="CarInHistoryPresenterSetting";
	@Inject
	private CarInHistoryListPresenter carInHistoryListPresenter;
	private CarInHistoryViewer carInHistoryViewer;
	int[] setting=null;
	private ScheduledExecutorService executorService;
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private int scheduleTime=5;
	
	
	public CarInHistoryPresenter() {
		setting= (int[]) CarparkFileUtils.readObject(SETTING_NAME);
		if(setting==null){
			setting=new int[]{2,0,1440,0,5};
		}
		System.out.println(setting[3]);
	}
	
	@Override
	protected View createView(Composite c) {
		carInHistoryViewer = new CarInHistoryViewer(c, 0);
		return carInHistoryViewer;
	}
	@Override
	public String getTitle() {
		return "场内车记录";
	}
	
	@Override
	protected void continue_go() {
		carInHistoryListPresenter.go(carInHistoryViewer.getComposite_listView());
		startRefreshThread(setting[3]==0,setting[4]);
		carInHistoryListPresenter.search(setting[0], setting[1],setting[2]);
		carInHistoryViewer.setSetting(setting);
		carInHistoryViewer.setMsg("最后更新时间:"+sdf.format(new Date()));
		
	}
	private void startRefreshThread(boolean start,int time) {
		if (start) {
			if(executorService!=null){
				if(scheduleTime==time){
					return;
				}
				executorService.shutdown();
				executorService=null;
			}
			scheduleTime=time;
			executorService = Executors.newSingleThreadScheduledExecutor();
			executorService.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					try {
						if(carInHistoryViewer.isDisposed()){
							return;
						}
						carInHistoryListPresenter.search(setting[0], setting[1], setting[2]);
						carInHistoryViewer.setMsg("最后更新时间:"+sdf.format(new Date()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, time, time, TimeUnit.SECONDS);
			carInHistoryViewer.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					if (executorService!=null) {
						executorService.shutdown();
						executorService=null;
					}
				}
			});
		}else{
			if (executorService!=null) {
				executorService.shutdown();
				executorService = null;
			}
		}
	}
	
	public void setSetting(int[] setting) {
		this.setting = setting;
		CarparkFileUtils.writeObject(SETTING_NAME, setting);
		startRefreshThread(setting[3]==0,setting[4]);
	}

	public int[] getSetting() {
		return setting;
	}
	

}
