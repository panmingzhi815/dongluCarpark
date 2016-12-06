package com.donglu.carpark.ui.view.visitor;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.CarparkManagePresenter;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.visitor.wizard.AddVisitorModel;
import com.donglu.carpark.ui.view.visitor.wizard.AddVisitorWizard;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkVisitor.VisitorStatus;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class VisitorListPresenter extends AbstractListPresenter<SingleCarparkVisitor> {
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
	public void add() {
		try {
			AddVisitorModel model = new AddVisitorModel();
			List<SingleCarparkVisitor> selected = view.getModel().getSelected();
			if (!StrUtil.isEmpty(selected)) {
				SingleCarparkVisitor visitor = selected.get(0);
				model.setVisitor(visitor);
				model.setId(null);
				model.setInCount(0);
				model.setOutCount(0);
				model.setValidTo(StrUtil.getTodayBottomTime(new Date()));
			}
			addAndEdit(model);
		} catch (Exception e) {
			commonui.error("", "添加"+ConstUtil.getVisitorName()+"失败", e);
		}

	}

	/**
	 * @param model
	 */
	private void addAndEdit(AddVisitorModel model) {
		AddVisitorWizard wizard = new AddVisitorWizard(model);
		List<SingleCarparkCarpark> findAllCarpark = sp.getCarparkService().findAllCarpark();
		if (StrUtil.isEmpty(findAllCarpark)) {
			commonui.info("", "请先创建停车场");
			return;
		}
		if (model.getId() == null) {
			model.setCarpark(findAllCarpark.get(0));
		}
		model.setListCarpark(findAllCarpark);
		Object showWizard = commonui.showWizard(wizard);
		if (StrUtil.isEmpty(showWizard)) {
			return;
		}
		SingleCarparkVisitor visitor = model.getVisitor();
		if (visitor.getAllIn()!=null&&visitor.getAllIn()>0) {
			if (visitor.getAllIn()>visitor.getInCount()) {
				visitor.setStatus(VisitorStatus.可用.name());
			}else{
				visitor.setStatus(VisitorStatus.不可用.name());
			}
		}
		if (visitor.getValidTo()!=null) {
			if (StrUtil.getTodayBottomTime(visitor.getValidTo()).after(new Date())) {
				visitor.setStatus(VisitorStatus.可用.name());
			}else{
				visitor.setStatus(VisitorStatus.不可用.name());
			}
		}
		sp.getCarparkService().saveVisitor(visitor);
		if (model.getId() == null) {
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.访客, "添加访客"+visitor.getPlateNO(), System.getProperty(ConstUtil.USER_NAME));
			commonui.info("成功", "添加"+ConstUtil.getVisitorName()+"成功");
		} else {
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.访客, "修改访客"+model.getPlateNO()+">"+visitor.getPlateNO(), System.getProperty(ConstUtil.USER_NAME));
			commonui.info("成功", "修改"+ConstUtil.getVisitorName()+"成功");
		}
		refresh();
	}

	@Override
	public void delete(List<SingleCarparkVisitor> list) {
		try {
			if (StrUtil.isEmpty(list)) {
				return;
			}
			boolean confirm = commonui.confirm("提示", "确认删除选中的"+list.size()+"个"+ConstUtil.getVisitorName()+"信息吗？");
			if (!confirm) {
				return;
			}
			for (SingleCarparkVisitor visitor : list) {
				sp.getCarparkService().deleteVisitor(visitor);
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.访客, "删除访客："+visitor.getPlateNO(), CarparkUtils.getLoginUserName());
			}
			refresh();
		} catch (Exception e) {
			commonui.error("成功", "删除"+ConstUtil.getVisitorName()+"成功", e);
		}
	}

	@Override
	public void refresh() {
		List<SingleCarparkVisitor> findVisitorByLike = sp.getCarparkService().findVisitorByLike(0, 500, userName, plateNo);
		int total=sp.getCarparkService().countVisitorByLike(userName, plateNo);
		view.getModel().setList(findVisitorByLike);
		view.getModel().setCountSearch(findVisitorByLike.size());
		view.getModel().setCountSearchAll(total);
	}

	public void search(String userName, String plateNo) {
		this.userName = userName;
		this.plateNo = plateNo;
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
			commonui.error("", "修改"+ConstUtil.getVisitorName()+"失败", e);
		}
	}

	@Override
	protected View createView(Composite c) {
		view = new VisitorListView(c, c.getStyle());
		return view;
	}
	@Override
	protected void continue_go() {
		view.setTableTitle(ConstUtil.getVisitorName()+"列表");
		view.setShowMoreBtn(true);
		refresh();
	}

	public void searchMore() {
		AbstractListView<SingleCarparkVisitor>.Model model = view.getModel();
		if (model.getCountSearch()>=model.getCountSearchAll()) {
			return;
		}
		List<SingleCarparkVisitor> list = model.getList();
		List<SingleCarparkVisitor> findVisitorByLike = sp.getCarparkService().findVisitorByLike(list.size(), 500, userName, plateNo);
		int total=sp.getCarparkService().countVisitorByLike(userName, plateNo);
		view.getModel().AddList(findVisitorByLike);
		view.getModel().setCountSearch(model.getList().size());
		view.getModel().setCountSearchAll(total);
	}

}
