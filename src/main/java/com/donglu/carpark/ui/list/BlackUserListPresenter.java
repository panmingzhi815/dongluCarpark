package com.donglu.carpark.ui.list;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.wizard.AddBlackUserWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkBlackUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class BlackUserListPresenter extends AbstractListPresenter<SingleCarparkBlackUser>{
	BlackUserListView view;
	
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void add() {
		addAndEditBlackUser(new SingleCarparkBlackUser());
	}
	@Override
	public void delete(List<SingleCarparkBlackUser> list) {
		try {
			if (StrUtil.isEmpty(list)) {
				commonui.info("删除提示", "请选择一个黑名单");
				return;
			}
			boolean confirm = commonui.confirm("删除提示", "确认删除选中"+list.size()+"的条记录");
			if (!confirm) {
				return;
			}
			String names="";
			for (SingleCarparkBlackUser b : list) {
				sp.getCarparkService().deleteBlackUser(b);
				names+="["+b.getPlateNO()+"]";
			}
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.黑名单, "删除黑名单:"+names,System.getProperty("userName"));
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

	public void edit() {
		List<SingleCarparkBlackUser> selected = view.getModel().getSelected();
		if (StrUtil.isEmpty(selected)) {
			return;
		}
		SingleCarparkBlackUser singleCarparkBlackUser = selected.get(0);
		addAndEditBlackUser(singleCarparkBlackUser);
		
	}

	/**
	 * @param singleCarparkBlackUser
	 */
	private void addAndEditBlackUser(SingleCarparkBlackUser singleCarparkBlackUser) {
		try {
			AddBlackUserWizard wizard=new AddBlackUserWizard(singleCarparkBlackUser);
			SingleCarparkBlackUser b = (SingleCarparkBlackUser) commonui.showWizard(wizard);
			if (StrUtil.isEmpty(b)) {
				return;
			}
			sp.getCarparkService().saveBlackUser(b);
			String property = System.getProperty("userName");
			if (StrUtil.isEmpty(singleCarparkBlackUser.getPlateNO())) {
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.黑名单, "添加黑名单:"+b.getPlateNO(),property);
			}else{
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.黑名单, "修改黑名单:"+b.getPlateNO(),property);
			}
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected View createView(Composite c) {
		view=new BlackUserListView(c,c.getStyle());
		view.setTableTitle("黑名单列表");
		return view;
	}
	
}
