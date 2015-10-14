package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;

import com.dongluhitec.card.common.ui.WidgetContainer;
import com.dongluhitec.card.common.ui.control.AbstractListViewer;
import com.dongluhitec.card.common.ui.impl.SWTContainer;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;

public class ListView extends AbstractListViewer<SingleCarparkReturnAccount> {

	public ListView(Composite parent, int style) {
		super(parent, style, new String[]{"编号","归账人","操作员","应归账金额","实归账金额","归账实际时间"},
				SingleCarparkReturnAccount.class,
				new String[]{SingleCarparkReturnAccount.Property.id.name(),
						SingleCarparkReturnAccount.Property.returnUser.name(),
						SingleCarparkReturnAccount.Property.operaName.name(),
						SingleCarparkReturnAccount.Property.shouldReturn.name(),
						SingleCarparkReturnAccount.Property.factReturn.name(),
						SingleCarparkReturnAccount.Property.returnTime.name(),},
				new int[]{100,100,100,100,100,200});
	}

	public ListView(WidgetContainer container) {
		this(((SWTContainer) container).getContainer(), container.getWidgetStyle());
		container.add(getWidget());
	}
}
