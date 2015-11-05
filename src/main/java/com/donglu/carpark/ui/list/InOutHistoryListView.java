package com.donglu.carpark.ui.list;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.ui.common.AbstractListView;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.view.CarparkPayHistoryPresenter;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;

public class InOutHistoryListView extends AbstractListView<SingleCarparkInOutHistory> implements View{
	private Text txt_shouldMoney;
	private Text txt_factMoney;
	private Text txt_freeMoney;
	
	public InOutHistoryListView(Composite parent, int style) {
		super(parent, style, SingleCarparkInOutHistory.class,
				new String[]{SingleCarparkInOutHistory.Property.plateNo.name(),
						SingleCarparkInOutHistory.Property.carType.name(),
						SingleCarparkInOutHistory.Property.userName.name(),
						SingleCarparkInOutHistory.Property.inDevice.name(),
						SingleCarparkInOutHistory.Label.inTimeLabel.name(),
						SingleCarparkInOutHistory.Property.outDevice.name(),
						SingleCarparkInOutHistory.Label.outTimeLabel.name(),
						SingleCarparkInOutHistory.Property.operaName.name(),
						SingleCarparkInOutHistory.Property.shouldMoney.name(),
						SingleCarparkInOutHistory.Property.factMoney.name(),
						SingleCarparkInOutHistory.Property.freeMoney.name(),
						SingleCarparkInOutHistory.Property.returnAccount.name(),},
				new String[]{"车牌号","车辆类型","用户名","进场设备","进场时间","出场设备","出场时间","操作员","应收金额","实收金额","免费金额","归账编号"},
				new int[]{100,100,100,100,200,100,200,100,90,90,90,90},new int[]{0,0,0,0,0,0,0,0,1,1,1,0});
		this.setTableTitle("归账记录表");
	}

	@Override
	protected void searchMore() {
		getPresenter().searchMore();
	}


	@Override
	public InOutHistoryListPresenter getPresenter() {
		return (InOutHistoryListPresenter) presenter;
	}

	@Override
	protected void createMenuBarToolItem(ToolBar mainToolbar) {
		ToolItem addItem = new ToolItem(mainToolbar, SWT.NONE);
		addItem.setText("查看");
		addItem.setData("type", "look");
		addItem.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
		    	getPresenter().lookDetail();
		    }
		});
		addItem.setToolTipText("查看图片");
	}

	@Override
	protected void createBottomComposite(Composite parent) {
		Composite composite_4 = new Composite(parent, SWT.NONE);
		GridLayout gl_composite_4 = new GridLayout(1, true);
		gl_composite_4.verticalSpacing = 0;
		gl_composite_4.marginWidth = 0;
		gl_composite_4.marginHeight = 0;
		gl_composite_4.horizontalSpacing = 0;
		composite_4.setLayout(gl_composite_4);
		
		Composite composite = new Composite(composite_4, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(6, false));
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("应收");
		
		txt_shouldMoney = new Text(composite, SWT.BORDER);
		txt_shouldMoney.setEditable(false);
		txt_shouldMoney.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("实收");
		
		txt_factMoney = new Text(composite, SWT.BORDER);
		txt_factMoney.setEditable(false);
		txt_factMoney.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("免费");
		
		txt_freeMoney = new Text(composite, SWT.BORDER);
		txt_freeMoney.setEditable(false);
		txt_freeMoney.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
	}
	public void setMoney(String shouldMoney,String factMoney,String freeMoney){
		txt_shouldMoney.setText(shouldMoney);
		txt_factMoney.setText(factMoney);
		txt_freeMoney.setText(freeMoney);
	}
}
