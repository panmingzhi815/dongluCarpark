package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;

public class CarInListView extends AbstractListView<CarInInfo> implements View{
	
	public CarInListView(Composite parent, int style) {
		super(parent, style, CarInInfo.class,
				new String[]{CarInInfo.Property.plateNO.name(),
						CarInInfo.Property.inTime.name(),
						CarInInfo.Property.userType.name(),
						CarInInfo.Property.status.name(),
						},
				new String[]{"车牌号","进场时间","用户类型","状态",},
				new int[]{100,200,100,100});
		this.setTableTitle("场内车");
	}

	@Override
	protected void searchMore() {
		getPresenter().searchMore();
	}


	@Override
	public CarInListPresenter getPresenter() {
		return (CarInListPresenter) presenter;
	}

	@Override
	protected void createMenuBarToolItem(ToolBar mainToolbar) {
		ToolItem addItem = new ToolItem(mainToolbar, SWT.NONE);
		addItem.setText("修正车牌");
		addItem.setData("type", "look");
		addItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().editPlateNO(null);
		    }
		});
		addItem.setToolTipText("修正识别错误的车牌");
		
		ToolItem lockItem = new ToolItem(mainToolbar, SWT.NONE);
		lockItem.setText("锁车/解锁");
		lockItem.setData("type", "look");
		lockItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().locakCar();
		    }
		});
		lockItem.setToolTipText("锁车后车辆必须解锁才能出去");
	}
}
