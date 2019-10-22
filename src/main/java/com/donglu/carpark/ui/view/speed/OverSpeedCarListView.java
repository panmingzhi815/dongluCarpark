package com.donglu.carpark.ui.view.speed;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.OverSpeedCar;

public class OverSpeedCarListView extends AbstractListView<OverSpeedCar> implements View {
	public OverSpeedCarListView(Composite parent, int style) {
		super(parent, style,OverSpeedCar.class,new String[]{OverSpeedCar.Property.plate.name(),
				OverSpeedCar.Label.timeLabel.name(),
				OverSpeedCar.Property.currentSpeed.name(),
				OverSpeedCar.Property.rateLimiting.name(),
				OverSpeedCar.Property.camId.name(),
				OverSpeedCar.Property.place.name(),
				OverSpeedCar.Property.carType.name(),
				OverSpeedCar.Label.statusLabel.name(),
				OverSpeedCar.Label.createTimeLabel.name(),
				}, new String[]{"车牌号","时间","当前速度","限速","设备编号","设备地点","车类型","状态","保存时间"},
				new int[]{100,200,80,80,120,120,100,100,200}, null);
	}

	@Override
	public OverSpeedCarListPresenter getPresenter() {
		return (OverSpeedCarListPresenter) presenter;
	}


	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		ToolItem toolItem = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem.setText("设置");
		toolItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().setting();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		ToolItem toolItem1 = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem1.setText("改为正常");
		toolItem1.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().setNomal();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
