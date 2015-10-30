package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;

public class InOutHistoryView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_plateNO;
	private Text text_userName;
	private Text text_operaName;
	private Text text_shouldMoney;
	private DateChooserCombo dateChooserCombo_end;
	private Combo combo_carType;
	private Combo combo_inorout;
	private Text text_inDevice;
	private Text text_outDevice;
	private Text text_returnAccount;
	private Text text_factMoney;

	public InOutHistoryView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		Font font = SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL);
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		group.setText("查询");
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(14, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_plateNO = new Text(group, SWT.BORDER);
		text_plateNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 71;
		text_plateNO.setLayoutData(gd_text);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("用户");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_userName = new Text(group, SWT.BORDER);
		text_userName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_userName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("开始时间");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		DateChooserCombo dateChooserCombo_start = new DateChooserCombo(group, SWT.BORDER);
		GridData gd_dateChooserCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dateChooserCombo.widthHint = 117;
		dateChooserCombo_start.setLayoutData(gd_dateChooserCombo);
		dateChooserCombo_start.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo_start.setValue(new Date());
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("进场设备");
		
		text_inDevice = new Text(group, SWT.BORDER);
		text_inDevice.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_inDevice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("操作员");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_operaName = new Text(group, SWT.BORDER);
		text_operaName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_operaName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		if (System.getProperty("userType").equals(SystemUserTypeEnum.操作员.name())) {
			text_operaName.setEditable(false);
			text_operaName.setText(System.getProperty("userName"));
		}
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("归账编号");
		
		text_returnAccount = new Text(group, SWT.BORDER);
		text_returnAccount.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_returnAccount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().search(text_plateNO.getText(),text_userName.getText(),dateChooserCombo_start.getValue(),dateChooserCombo_end.getValue(),
						text_operaName.getText(),combo_carType.getText(),combo_inorout.getText(),text_inDevice.getText(),text_outDevice.getText(),text_returnAccount.getText());
			}
		});
		button.setText("查询");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button_2 = new Button(group, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().exportSearch();
			}
		});
		button_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_2.setText("导出");
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("车辆类型");
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		ComboViewer comboViewer_carType = new ComboViewer(group, SWT.READ_ONLY);
		comboViewer_carType.setContentProvider(new ArrayContentProvider());
		comboViewer_carType.setLabelProvider(new LabelProvider());
		comboViewer_carType.setInput(new String[]{"全部","临时车","固定车"});
		combo_carType = comboViewer_carType.getCombo();
		combo_carType.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_carType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_carType.select(0);
		
		Label label_5 = new Label(group, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("是否出场");
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		ComboViewer comboViewer_inout = new ComboViewer(group, SWT.READ_ONLY);
		comboViewer_inout.setContentProvider(new ArrayContentProvider());
		comboViewer_inout.setLabelProvider(new LabelProvider());
		comboViewer_inout.setInput(new String[]{"无","是","否"});
		combo_inorout = comboViewer_inout.getCombo();
		combo_inorout.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_inorout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_inorout.select(0);
		
		Label label_6 = new Label(group, SWT.NONE);
		label_6.setText("结束时间");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		dateChooserCombo_end = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo_end.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		dateChooserCombo_end.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo_end.setValue(new Date());
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("出场设备");
		
		text_outDevice = new Text(group, SWT.BORDER);
		text_outDevice.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_outDevice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_7 = new Label(group, SWT.NONE);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("应收金额");
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_shouldMoney = new Text(group, SWT.BORDER);
		text_shouldMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_shouldMoney.setEnabled(false);
		text_shouldMoney.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_8 = new Label(group, SWT.NONE);
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("实收金额");
		
		text_factMoney = new Text(group, SWT.BORDER);
		text_factMoney.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_factMoney.setEditable(false);
		text_factMoney.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button button_1 = new Button(group, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int[] i=getPresenter().countMoney();
				if (StrUtil.isEmpty(i)) {
					return;
				}
				text_shouldMoney.setText(i[0]+"");
				text_factMoney.setText(i[1]+"");
			}
		});
		button_1.setText("统计");
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		new Label(group, SWT.NONE);
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public InOutHistoryPresenter getPresenter() {
		return (InOutHistoryPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
}
