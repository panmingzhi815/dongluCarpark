package com.donglu.carpark.ui.view.inouthistory.event;

import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.TableSort;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkEvent;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;

public class EventListView extends AbstractListView<CarparkEvent> {
	//SingleCarparkUser.Property.id.name(),"编号",60,
	public EventListView(Composite parent, int style) {
		super(parent, style,CarparkEvent.class,new String[]{
				"deviceId","entranceCode","channel","plate","plateType","carType","plateColor","eventTimeLabel","eventName",
		}, new String[]{"设备编号","出入口编码","通道号","车牌号码","车牌类型","车辆类型","车牌颜色","触发时间","事件类型"},
				new int[]{100,100,100,100,100,100,150,100,100,100,120,100,100,100,100,100}, null);
	}

	@Override
	public EventListPresenter getPresenter() {
		return (EventListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
		getPresenter().populate();
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		
	}
	
}
