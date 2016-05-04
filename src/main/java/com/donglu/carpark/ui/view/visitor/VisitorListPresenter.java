package com.donglu.carpark.ui.view.visitor;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkManagePresenter;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.view.visitor.wizard.AddVisitorModel;
import com.donglu.carpark.ui.view.visitor.wizard.AddVisitorWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class VisitorListPresenter extends AbstractListPresenter<SingleCarparkVisitor>{
	private static final Logger log = LoggerFactory.getLogger(VisitorListPresenter.class);
	VisitorListView view;
	
	String userName; 
	String plateNo;
	@Inject
	CarparkManagePresenter carparkManagePresenter;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new VisitorListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("固定用户列表");
		view.setShowMoreBtn(false);
		refresh();
	}
	


	@Override
	public void add() {
		try {
			AddVisitorModel model = new AddVisitorModel();
			addAndEdit(model);
		} catch (Exception e) {
			commonui.error("", "添加访客失败",e);
		}
	}



	/**
	 * @param model
	 */
	private void addAndEdit(AddVisitorModel model) {
		AddVisitorWizard wizard=new AddVisitorWizard(model);
		List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();
		if (StrUtil.isEmpty(findAllCarpark)) {
			commonui.info("", "请先创建停车场");
			return;
		}
		if (model.getId()==null) {
			model.setCarpark(findAllCarpark.get(0));
		}
		model.setListCarpark(findAllCarpark);
		Object showWizard = commonui.showWizard(wizard);
		if (StrUtil.isEmpty(showWizard)) {
			return;
		}
		SingleCarparkVisitor visitor=model.getVisitor();
		sp.getCarparkService().saveVisitor(visitor);
		if (model.getId()==null) {
			commonui.info("成功", "添加访客成功");
		}else{
			commonui.info("成功", "修改访客成功");
		}
	}

	@Override
	public void delete(List<SingleCarparkVisitor> list) {
		for (SingleCarparkVisitor visitor : list) {
			sp.getCarparkService().deleteVisitor(visitor);
		}
		
		commonui.info("成功", "删除访客成功");
	}

	@Override
	public void refresh() {
		List<SingleCarparkVisitor> findVisitorByLike = sp.getCarparkService().findVisitorByLike(0,500,userName,plateNo);
		view.getModel().setList(findVisitorByLike);
	}

	public void search(String userName, String plateNo) {
		this.userName=userName;
		this.plateNo=plateNo;
		refresh();
	}

	public void edit() {
		try {
			List<SingleCarparkVisitor> selected = view.getModel().getSelected();
			if (StrUtil.isEmpty(selected)) {
				return;
			}
			SingleCarparkVisitor visitor = selected.get(0);
			AddVisitorModel model = new AddVisitorModel();
			model.setVisitor(visitor);
			addAndEdit(model);
		} catch (Exception e) {
			commonui.error("", "修改访客失败",e);
		}
	}
	
}
