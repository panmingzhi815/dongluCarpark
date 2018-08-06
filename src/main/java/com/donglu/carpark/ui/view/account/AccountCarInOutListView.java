package com.donglu.carpark.ui.view.account;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractListView;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class AccountCarInOutListView extends AbstractListView<SingleCarparkInOutHistory> {
	private Text text;

	public AccountCarInOutListView(Composite parent) {
		super(parent, parent.getStyle(), SingleCarparkInOutHistory.class, new String[]{
				SingleCarparkInOutHistory.Property.plateNo.name(),
				SingleCarparkInOutHistory.Property.userName.name(),
				SingleCarparkInOutHistory.Label.inTimeLabel.name(),
				SingleCarparkInOutHistory.Label.outTimeLabel.name(),
				SingleCarparkInOutHistory.Property.shouldMoney.name(),}, 
				new String[]{"车牌","名称","进场时间","出场时间","应收金额"},
				new int[]{100,100,200,200,100},new int[]{0,0,0,0,SWT.RIGHT});
	}
	@Override
	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		ToolItem toolItem = new ToolItem(toolBar_menu, SWT.NULL);
		toolItem.setText("导出");
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().export();
			}
		});
	}
	@Override
	public AccountCarInOutListPresenter getPresenter() {
		return (AccountCarInOutListPresenter) super.getPresenter();
	}
	public void setTotal(Double d) {
		text.setText(d==null?"0":d.toString());
	}
	@Override
	protected void createBottomComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite_1.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("总金额");
		
		text = new Text(composite_1, SWT.NONE);
		text.setEditable(false);
		text.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.BOLD));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 150;
		text.setLayoutData(gd_text);
	}

}
