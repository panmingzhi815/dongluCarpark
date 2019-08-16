package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemOperaLog;

public class SystemLogListView extends AbstractListView<SingleCarparkSystemOperaLog> implements View {
	public SystemLogListView(Composite parent, int style) {
		super(parent, style,SingleCarparkSystemOperaLog.class,new String[]{SingleCarparkSystemOperaLog.Property.operaName.name(),
				SingleCarparkSystemOperaLog.Property.operaDateLabel.name(),
				SingleCarparkSystemOperaLog.Property.type.name(),
				SingleCarparkSystemOperaLog.Property.content.name(),
				"remarkString"
				}, new String[]{"操作人","操作时间","操作对象","操作内容","详情"},
				new int[]{100,200,100,200,300}, null);
	}

	@Override
	public SystemLogListPresenter getPresenter() {
		return (SystemLogListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		
	}
	
}
