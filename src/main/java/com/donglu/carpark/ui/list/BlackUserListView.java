package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;

import com.dongluhitec.card.common.ui.WidgetContainer;
import com.dongluhitec.card.common.ui.control.AbstractListViewer;
import com.dongluhitec.card.common.ui.impl.SWTContainer;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;

public class BlackUserListView extends AbstractListViewer<SingleCarparkBlackUser> {

	public BlackUserListView(Composite parent, int style) {
		super(parent, style, new String[]{"车牌号","备注"},
				SingleCarparkBlackUser.class,
				new String[]{SingleCarparkBlackUser.Property.plateNO.name(),
						SingleCarparkBlackUser.Property.remark.name()},
				new int[]{100,200});
	}

	public BlackUserListView(WidgetContainer container) {
		this(((SWTContainer) container).getContainer(), container.getWidgetStyle());
		container.add(getWidget());
	}
}
