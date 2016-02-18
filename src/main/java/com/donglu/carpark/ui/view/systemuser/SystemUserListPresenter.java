package com.donglu.carpark.ui.view.systemuser;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.model.SystemUserModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.SystemUserServiceI;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.wizard.AddSystemUserWizard;
import com.donglu.carpark.ui.wizard.EditSystemUserWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemOperaLogTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class SystemUserListPresenter extends AbstractListPresenter<SingleCarparkSystemUser>{
	private static final String OPERANAME = System.getProperty("userName");

	SystemUserListView view;
	
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkDatabaseServiceProvider sp;
	@Override
	public void go(Composite c) {
		view=new SystemUserListView(c,c.getStyle());
		view.setPresenter(this);
		view.setTableTitle("系统用户列表");
		view.setShowMoreBtn(false);
		refresh();
	}

	@Override
	public void add() {
		try {
			SystemUserModel s = new SystemUserModel();
			AddSystemUserWizard wizard = new AddSystemUserWizard(s);
			SystemUserModel m = (SystemUserModel) commonui.showWizard(wizard);
			if (m == null) {
				return;
			}
			if (!check(System.getProperty("userType"), m.getType())) {
				commonui.error("操作终止", "您没有权限添加" + m.getType() + "账号");
				return;
			}
			SingleCarparkSystemUser systemUser = new SingleCarparkSystemUser();
			systemUser.setCreateDate(new Date());
			systemUser.setPassword(m.getPwd());
			systemUser.setRemark(m.getRemark());
			systemUser.setType(m.getType());
			systemUser.setUserName(m.getUserName());
			SystemUserServiceI systemUserService = sp.getSystemUserService();
			systemUserService.saveSystemUser(systemUser);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.系统用户, "添加了系统用户:" + systemUser.getUserName(),OPERANAME);
			commonui.info("操作成功", "添加用户成功！");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("操作失败", "添加用户失败！");
		}
	}
	@Override
	public void delete(List<SingleCarparkSystemUser> list) {
		List<SingleCarparkSystemUser> selectList = list;
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		boolean confirm = commonui.confirm("删除提示", "是否删除选中的" + selectList.size() + "个系统用户");
		if (!confirm) {
			return;
		}
		String property = System.getProperty("userType");
		SystemUserServiceI systemUserService = sp.getSystemUserService();
		for (SingleCarparkSystemUser singleCarparkSystemUser : selectList) {
			boolean check = check(property, singleCarparkSystemUser.getType());
			if (!check) {
				commonui.error("操作终止", "您没有权限去删除系统用户：" + singleCarparkSystemUser.getUserName());
				break;
			}
			try {
				systemUserService.removeSystemUser(singleCarparkSystemUser);
				sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.系统用户, "删除了系统用户:" + singleCarparkSystemUser.getUserName(),OPERANAME);
				commonui.info("操作成功", "删除系统用户成功");
			} catch (Exception e) {
				e.printStackTrace();
				commonui.error("操作失败", "删除系统用户" + singleCarparkSystemUser.getUserName() + "失败");
				break;
			}
		}
		refresh();
	}
	
	/**
	 * 检测登录用户权限
	 * 
	 * @param loginType
	 * @param type
	 * @return
	 */
	private boolean check(String loginType, String type) {
		if (loginType.equals("系统管理员")) {
			return true;
		}
		if (loginType.equals("普通管理员")) {
			if (type.equals("操作员")) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void refresh() {
		SystemUserServiceI systemUserService = sp.getSystemUserService();
		List<SingleCarparkSystemUser> findAll = systemUserService.findAllSystemUser();
		view.getModel().setList(findAll);
	}

	public void edit() {
		List<SingleCarparkSystemUser> selectList = view.getModel().getSelected();
		if (StrUtil.isEmpty(selectList)) {
			return;
		}
		SingleCarparkSystemUser singleCarparkSystemUser = selectList.get(0);
		String property = System.getProperty("userType");
		boolean result = check(property, singleCarparkSystemUser.getType());
		if (!result) {
			commonui.error("操作终止", "您没有权限修改系统用户：" + singleCarparkSystemUser.getUserName());
			return;
		}
		SystemUserModel model = new SystemUserModel();
		model.setUserName(singleCarparkSystemUser.getUserName());
		model.setRemark(singleCarparkSystemUser.getRemark());
		EditSystemUserWizard wizard = new EditSystemUserWizard(model);
		SystemUserModel m = (SystemUserModel) commonui.showWizard(wizard);
		if (m == null) {
			return;
		}
		singleCarparkSystemUser.setPassword(m.getPwd());
		singleCarparkSystemUser.setLastEditDate(new Date());
		singleCarparkSystemUser.setLastEditUser(OPERANAME);
		singleCarparkSystemUser.setRemark(m.getRemark());
		try {
			sp.getSystemUserService().saveSystemUser(singleCarparkSystemUser);
			sp.getSystemOperaLogService().saveOperaLog(SystemOperaLogTypeEnum.系统用户, "修改了系统用户:" + singleCarparkSystemUser.getUserName(),OPERANAME);
			commonui.info("提示", "修改成功！");
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "修改失败！");
		}

	}
}
