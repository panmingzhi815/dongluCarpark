package com.donglu.carpark.ui.list;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.wizard.AddBlackUserWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class UserListPresenter extends AbstractListPresenter<SingleCarparkUser>{
	UserListView view;
	
	String userName; 
	String plateNo;
	
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new UserListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("黑名单列表");
	}

	@Override
	public void add() {
		
	}

	@Override
	public void delete(List<SingleCarparkUser> list) {
		boolean confirm = commonui.confirm("删除提示", "确定删除选中的"+list.size()+"条记录");
		if (!confirm) {
			return;
		}
		CarparkUserService carparkUserService = sp.getCarparkUserService();
		for (SingleCarparkUser singleCarparkUser : list) {
			carparkUserService.deleteUser(singleCarparkUser);
		}
	}

	@Override
	public void refresh() {
		List<SingleCarparkUser> findByNameOrPlateNo = sp.getCarparkUserService().findByNameOrPlateNo(userName, plateNo, 0, null);
		view.getModel().setList(findByNameOrPlateNo);
		view.getModel().setCountSearch(findByNameOrPlateNo.size());
		view.getModel().setCountSearchAll(findByNameOrPlateNo.size());
	}

	public void search(String userName, String plateNo) {
		this.userName=userName;
		this.plateNo=plateNo;
		refresh();
	}
	
}
