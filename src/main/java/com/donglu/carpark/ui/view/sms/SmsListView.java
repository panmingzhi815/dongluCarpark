package com.donglu.carpark.ui.view.sms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.OverSpeedCar;
import com.dongluhitec.card.domain.db.singlecarpark.SmsInfo;

public class SmsListView extends AbstractListView<SmsInfo> implements View {
	public SmsListView(Composite parent, int style) {
		super(parent, style,SmsInfo.class,new String[]{OverSpeedCar.Property.plate.name(),
				SmsInfo.Property.userName.name(),
				SmsInfo.Property.tel.name(),
				SmsInfo.Property.templateCode.name(),
				SmsInfo.Property.speed.name(),
				SmsInfo.Label.overSpeedTimeLabel.name(),
				SmsInfo.Property.overSpeedSize.name(),
				SmsInfo.Property.address.name(),
				SmsInfo.Label.statusLabel.name(),
				SmsInfo.Property.remark.name(),
				SmsInfo.Label.createTimeLabel.name(),
				}, new String[]{"车牌号","姓名","电话","短信模板","当前速度","超速时间","超速次数","超速地址","状态","备注","保存时间"},
				new int[]{100,100,120,120,100,200,80,120,100,100,200}, null);
	}

	@Override
	public SmsListPresenter getPresenter() {
		return (SmsListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
//		ToolItem toolItem = new ToolItem(toolBar_menu, SWT.NONE);
//		toolItem.setText("设置");
//		toolItem.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				getPresenter().setting();
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
	}
	
}
