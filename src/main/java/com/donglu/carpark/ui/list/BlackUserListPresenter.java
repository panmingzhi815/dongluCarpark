package com.donglu.carpark.ui.list;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.wizard.AddBlackUserWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class BlackUserListPresenter extends AbstractListPresenter<SingleCarparkBlackUser>{
	BlackUserListView view;
	
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new BlackUserListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("黑名单列表");
	}

	@Override
	public void add() {
		try {
			AddBlackUserWizard wizard=new AddBlackUserWizard(new  SingleCarparkBlackUser());
			SingleCarparkBlackUser b = (SingleCarparkBlackUser) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(b)) {
				return;
			}
			sp.getCarparkService().saveBlackUser(b);
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void delete(List<SingleCarparkBlackUser> list) {
		try {
			List<SingleCarparkBlackUser> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				commonui.info("删除提示", "请选择一个黑名单");
				return;
			}
			for (SingleCarparkBlackUser b : selected) {
				sp.getCarparkService().deleteBlackUser(b);
			}
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh() {
		List<SingleCarparkBlackUser> list=sp.getCarparkService().findAllBlackUser();
		AbstractListView<SingleCarparkBlackUser>.Model model = view.getModel();
		model.setList(list);
		model.setCountSearch(list.size());
		model.setCountSearchAll(list.size());
	}
	
}
