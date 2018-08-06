package com.donglu.carpark.ui.view.account;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkUserService;
import com.donglu.carpark.ui.common.AbstractListPresenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.account.wizard.AddAccountCarWizard;
import com.donglu.carpark.util.ExcelImportExportImpl;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkAccountCar;
import com.dongluhitec.card.domain.db.singlecarpark.QueryParameter;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.inject.Inject;

public class AccountCarListPresenter extends AbstractListPresenter<CarparkAccountCar> {
	private CarparkDatabaseServiceProvider sp;
	CommonUIFacility commonui;
	private CarparkUserService userService;
	private String plateNo;
	private String name;
	@Inject
	public AccountCarListPresenter(CarparkDatabaseServiceProvider sp,CommonUIFacility commonui) {
		super();
		this.sp = sp;
		this.commonui = commonui;
		userService=sp.getCarparkUserService();
	}
	@Override
	protected View createView(Composite c) {
		return new AccountCarListView(c);
	}
	
	@Override
	public void add() {
		CarparkAccountCar model = new CarparkAccountCar();
		AddAccountCarWizard wizard = new AddAccountCarWizard(model,sp);
		Object showWizard = commonui.showWizard(wizard);
		if (showWizard==null) {
			return;
		}
		sp.getCarparkUserService().save(model);
		refresh();
	}
	@Override
	public void delete(List<CarparkAccountCar> list) {
		try {
			if (StrUtil.isEmpty(list)) {
				return;
			}
			boolean confirm = commonui.confirm("删除提示", "确认删除选中的"+list.size()+"条记录");
			if (!confirm) {
				return;
			}
			for (CarparkAccountCar carparkAccountCar : list) {
				sp.getCarparkUserService().delete(carparkAccountCar);
			}
			commonui.info("提示", "删除成功");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("失败", "删除失败！");
		}
	}
	
	@Override
	protected int getTotalSize() {
		List<QueryParameter> parameters = getParameters();
		return userService.countAccountCar(parameters);
	}
    	
	private List<QueryParameter> getParameters() {
		ArrayList<QueryParameter> list = new ArrayList<>();
		list.add(QueryParameter.like("plateNo", "%" + plateNo + "%"));
		list.add(QueryParameter.like("name", "%" + name + "%"));
		return list;
	}
	@Override
	protected List<CarparkAccountCar> findListInput() {
		List<QueryParameter> parameters = getParameters();
		parameters.add(QueryParameter.firstResult(current));
		parameters.add(QueryParameter.maxResult(pageSize));
		return userService.findAccountCard(parameters);
	}
	public void search(String plateNo, String name) {
		this.plateNo = plateNo;
		this.name = name;
		refresh();
	}
	public void importPlate() {
		ExcelImportExportImpl excelImport = new ExcelImportExportImpl();
		String path=commonui.selectToOpen();
		if (path==null) {
			return;
		}
		try {
			int errorSize = excelImport.importExcel(path,new int[]{1,2},new Function<String[], String>(){
				@Override
				public String apply(String[] t) {
					String name = t[0];
					String ps=t[1];
					for (String plateNo : ps.split(",")) {
						if (StrUtil.isEmpty(name)||StrUtil.isEmpty(plateNo)) {
							throw new RuntimeException("名称和车牌不能为空");
						}
						List<CarparkAccountCar> list = sp.getCarparkUserService().findAccountCard(Arrays.asList(QueryParameter.eq(CarparkAccountCar.Preperty.plateNo.name(), plateNo)));
						if (!StrUtil.isEmpty(list)) {
							throw new RuntimeException("车牌已存在");
						}
						CarparkAccountCar car = new CarparkAccountCar();
						car.setPlateNo(plateNo);
						car.setName(name);
						sp.getCarparkUserService().save(car);
					}
					return null;
				}
				
			});
			commonui.info("成功", "导入成功，有"+errorSize+"条导出失败！");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.info("错误", "导出是发生错误！"+e);
		}
	}
	public void exportPlate() {
		String path = commonui.selectToSave();
		if (StrUtil.isEmpty(path)) {
			return;
		}
		try {
			path=StrUtil.checkPath(path,new String[]{".xls",".xlsx"},".xls");
			if (!new File(path).exists()) {
				new File(path).createNewFile();
			}
			new ExcelImportExportImpl().export(path, new String[]{"名称","车牌"}, new String[]{CarparkAccountCar.Preperty.name.name(),
					CarparkAccountCar.Preperty.plateNo.name()},sp.getCarparkUserService().findAccountCard(getParameters()));
			commonui.info("提示", "导出到："+path+" 成功");
		}catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "导出失败"+e);
		}
	}
	public void edit() {
		try {
			List<CarparkAccountCar> list = getView().getModel().getSelected();
			if (StrUtil.isEmpty(list)) {
				return;
			}
			CarparkAccountCar carparkAccountCar = list.get(0);
			AddAccountCarWizard w = new AddAccountCarWizard(carparkAccountCar, sp);
			Object showWizard = commonui.showWizard(w);
			if (showWizard==null) {
				return;
			}
			sp.getCarparkUserService().save(carparkAccountCar);
			commonui.info("提示", "修改成功");
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
			commonui.error("提示", "修改失败"+e);
		}
	}
}
