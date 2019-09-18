package com.donglu.carpark.ui.view.inouthistory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;
import com.google.inject.Inject;

public class InOutHistoryPresenter  implements Presenter{
	private InOutHistoryView view;
	@Inject
	private InOutHistoryListPresenter listPresenter;
	private InOutHistoryModel model=new InOutHistoryModel();
	@Inject
	private CarparkDatabaseServiceProvider sp;
	
	@Override
	public void go(Composite c) {
		view=new InOutHistoryView(c, c.getStyle(),model);
		view.setPresenter(this);
		listPresenter.go(view.getListComposite());
		List<SingleCarparkCarpark> findAllCarpark = new ArrayList<>();
		SingleCarparkCarpark scc=new SingleCarparkCarpark();
		scc.setCode("");
		scc.setName("全部");
		findAllCarpark.add(scc);
		findAllCarpark.addAll(sp.getCarparkService().findAllCarpark());
		view.setCarparkList(findAllCarpark);
		model.setListCarpark(findAllCarpark);
		
		List<SingleCarparkSystemUser> findAll = new ArrayList<>();
		SingleCarparkSystemUser e = new SingleCarparkSystemUser();
		if (System.getProperty("userType").equals(SystemUserTypeEnum.操作员.name())) {
			e.setUserName(System.getProperty("userName"));
		}else{
			e.setUserName("全部");
		}
		findAll.add(e);
		findAll.addAll(sp.getSystemUserService().findAllSystemUser());
		view.setComboValue(findAll);
	}
	public void search(String plateNo, String returnUser, Date start, Date end, Date outStart, Date outEnd, String operaName, String carType, String inout, String inDevice, String outDevice, String returnAccount, SingleCarparkCarpark carpark, float... shouldMoney) {
//		if (inout.equals("否")) {
//			listPresenter.search( plateNo,  null,  start,  end,outStart,outEnd,  operaName,  carType,  inout,  null,  null,  null,carpark,shouldMoney);
//		}else
		listPresenter.search( plateNo,  returnUser,  start,  end,outStart,outEnd,  operaName,  carType,  inout,  inDevice,  outDevice,  returnAccount,carpark,shouldMoney);
	}
	
	public InOutHistoryListPresenter getListPresenter() {
		return listPresenter;
	}
	public void exportSearch() {
		listPresenter.exportSearch();	
	}
	
	@Override
	public String getTitle() {
		return "进出记录查询";
	}
}
