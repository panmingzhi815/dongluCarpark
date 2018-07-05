package com.donglu.carpark.ui.view.user;

import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.TableSort;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;

public class UserListView extends AbstractListView<SingleCarparkUser> {
	//SingleCarparkUser.Property.id.name(),"编号",60,
	public UserListView(Composite parent, int style) {
		super(parent, style,SingleCarparkUser.class,new String[]{
				SingleCarparkUser.Property.plateNo.name(),
				SingleCarparkUser.Property.name.name(),
				SingleCarparkUser.Property.address.name(),
				SingleCarparkUser.Property.telephone.name(),
				SingleCarparkUser.Property.type.name(),
				SingleCarparkUser.Property.carType.name(),
				SingleCarparkUser.Property.leftMoney.name(),
				SingleCarparkUser.Label.valitoLabel.name(),
				SingleCarparkUser.Property.carpark.name(),
				SingleCarparkUser.Property.monthChargeName.name(),
				SingleCarparkUser.Property.carparkNo.name(),
				SingleCarparkUser.Property.parkingSpace.name(),
				SingleCarparkUser.Property.remark.name()}, new String[]{"车牌号","姓名","住址","电话","用户类型","车辆类型","账号余额","有效期","停车场","收费标准","车位数量","车位号","备注"},
				new int[]{100,100,100,100,100,100,100,120,100,100,100,100,100}, null);
		TableSort.mapComparator.put(SingleCarparkUser.Property.parkingSpace.name(), new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				if (o1!=null&&o2!=null) {
					try {
						Long l1 = Long.valueOf(o1.toString());
						Long l2 = Long.valueOf(o2.toString());
						return l1.compareTo(l2);
					} catch (Exception e) {
					}
				}
				return (o1+"").compareTo(o2+"");
			}
		});
	}

	@Override
	public UserListPresenter getPresenter() {
		return (UserListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
		getPresenter().populate();
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		if (!ConstUtil.checkPrivilege(SystemUserTypeEnum.普通管理员)) {
			return;
		}
		ToolItem toolItem_pay = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_pay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().pay();
			}
		});
		toolItem_pay.setText("续费");
		super.createMenuBarToolItem(toolBar_menu);
		ToolItem toolItem_impot = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_impot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().importAll();
			}
		});
		toolItem_impot.setText("导入");
		ToolItem toolItem_export = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_export.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().exportAll();
			}
		});
		toolItem_export.setText("导出");
		
		ToolItem toolItem_edit = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().edit();
			}
		});
		toolItem_edit.setText("修改");
	}
	
}
