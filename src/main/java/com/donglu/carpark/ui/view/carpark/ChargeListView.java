package com.donglu.carpark.ui.view.carpark;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;

public class ChargeListView extends AbstractListView<CarparkChargeInfo> implements View {
	//SingleCarparkUser.Property.id.name(),"编号",60,
	public ChargeListView(Composite parent, int style) {
		super(parent, style,CarparkChargeInfo.class,new String[]{
				CarparkChargeInfo.Property.code.name(),
				CarparkChargeInfo.Property.name.name(),
				CarparkChargeInfo.Property.type.name(),
				CarparkChargeInfo.Property.carType.name(),
				CarparkChargeInfo.Property.holidayType.name(),
				CarparkChargeInfo.Property.useType.name()}, new String[]{"编码","名称","收费类型","车辆类型","节假日类型","是否启用"},
				new int[]{100,100,130,100,100,100}, null);
		setShowMoreBtn(false);
	}

	@Override
	public ChargeListPresenter getPresenter() {
		return (ChargeListPresenter) presenter;
	}

	@Override
	protected void searchMore() {
	}

	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		toolBar_menu.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		if (!ConstUtil.checkPrivilege(SystemUserTypeEnum.系统管理员)) {
			return;
		}
		ToolItem toolItem_5 = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_5.setToolTipText("添加临时收费设置");
		toolItem_5.setText("添加临时收费设置");
		toolItem_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().addTempCharge(null);
			}
			
		});
		
		ToolItem toolItem_6 = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_6.setToolTipText("添加固定收费设置");
		toolItem_6.setText("添加固定收费设置");
		toolItem_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().addMonthCharge();
			}
			
		});
		
		ToolItem toolItem_7 = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_7.setText("删除");
		toolItem_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().deleteCarparkCharge();
			}
			
		});
		
		ToolItem toolItem_8 = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_8.setToolTipText("修改");
		toolItem_8.setText("修改");
		toolItem_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().editCarparkChargeSetting();
			}
			
		});
		
		ToolItem toolItem_9 = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_9.setToolTipText("启用临时收费设置");
		toolItem_9.setText("启用");
		toolItem_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().startUseTempCharge();
			}
		});
		
		ToolItem toolItem_10 = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_10.setToolTipText("禁用停车场收费设置");
		toolItem_10.setText("禁用");
		toolItem_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().stopUseTempCharge();
			}
		});
	}
	
}
