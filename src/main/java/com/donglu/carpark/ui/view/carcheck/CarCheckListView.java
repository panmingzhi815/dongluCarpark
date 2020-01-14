package com.donglu.carpark.ui.view.carcheck;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

import com.donglu.carpark.ui.common.AbstractListView;
import com.dongluhitec.card.domain.db.singlecarpark.CarCheckHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkFreeTempCar;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;

public class CarCheckListView extends AbstractListView<CarCheckHistory> {
	public CarCheckListView(Composite parent, int style) {
		super(parent, style, CarCheckHistory.class,
				new String[] { CarCheckHistory.Label.plate.name(),
						CarCheckHistory.Label.timeLabel.name(),
						CarCheckHistory.Label.type.name(),
						CarCheckHistory.Label.status.name(),
						CarCheckHistory.Label.operaName.name(),
						CarCheckHistory.Label.editedPlateLabel.name(),
						CarCheckHistory.Label.sourcePlate.name(),
						CarCheckHistory.Label.editedPlateSize.name(),
						CarCheckHistory.Label.shouldMoney.name(),
				},
				new String[] { "车牌号", "时间", "类型","状态","操作员","是否修改车牌","原车牌","修改车牌数","金额" }, new int[] { 100, 180, 100,100,100,100,100,150,100,100,100}, null);
	}

	@Override
	public CarCheckListPresenter getPresenter() {
		return (CarCheckListPresenter) presenter;
	}


	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
//		super.createMenuBarToolItem(toolBar_menu);
		
	}

}
