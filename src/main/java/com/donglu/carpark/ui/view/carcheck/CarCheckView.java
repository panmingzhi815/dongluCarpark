package com.donglu.carpark.ui.view.carcheck;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import java.util.Date;
import org.eclipse.swt.widgets.Combo;

public class CarCheckView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text txt_plate;
	private Text text;
	private Text text_1;

	public CarCheckView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(9, false));
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("车牌");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		txt_plate = new Text(group, SWT.BORDER);
		txt_plate.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_txt_plate = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_plate.widthHint = 92;
		txt_plate.setLayoutData(gd_txt_plate);
		
		Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setText("起始时间");
		
		CDateTime dateTime_startTime = new CDateTime(group, CDT.BORDER);
		GridData gd_dateTime_startTime = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dateTime_startTime.widthHint = 160;
		dateTime_startTime.setLayoutData(gd_dateTime_startTime);
		dateTime_startTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime_startTime.setSelection(StrUtil.getTodayTopTime(new Date()));
		dateTime_startTime.setPattern("yyyy-MM-dd HH:mm:ss");
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("弹窗类型");
		
		Combo combo_type = new Combo(group, SWT.READ_ONLY);
		combo_type.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_type.setItems(new String[] {"全部", "进场", "出场"});
		combo_type.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_type.select(0);
		
		Label label_6 = new Label(group, SWT.NONE);
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("修改车牌数");
		
		text = new Text(group, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 58;
		text.setLayoutData(gd_text);
		
		Button button = new Button(group, SWT.NONE);
		button.setText("查询");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("状态");
		
		Combo combo_status = new Combo(group, SWT.READ_ONLY);
		combo_status.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_status.setItems(new String[] {"全部", "确认中", "取消确认", "确认放行","手动抬杆"});
		combo_status.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_status.select(0);
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setText("截止时间");
		
		CDateTime dateTime_endTime = new CDateTime(group, CDT.BORDER);
		GridData gd_dateTime_endTime = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dateTime_endTime.widthHint = 160;
		dateTime_endTime.setLayoutData(gd_dateTime_endTime);
		dateTime_endTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime_endTime.setPattern("yyyy-MM-dd HH:mm:ss");
		dateTime_endTime.setSelection(StrUtil.getTodayBottomTime(new Date()));
		
		Label label_5 = new Label(group, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("修改车牌");
		
		Combo combo_editPlate = new Combo(group, SWT.READ_ONLY);
		combo_editPlate.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_editPlate.setItems(new String[] {"全部", "已修改", "未修改"});
		combo_editPlate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_editPlate.select(0);
		
		Label label_7 = new Label(group, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("收费金额");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setText("0");
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(group, SWT.NONE);
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					double money=0;
					try {
						money = Double.valueOf(text_1.getText());
					} catch (Exception e1) {
						
					}
					getPresenter().search(txt_plate.getText(),dateTime_startTime.getSelection(),dateTime_endTime.getSelection(),combo_type.getText(),combo_status.getText(),combo_editPlate.getText(),text.getText(),money);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public CarCheckPresenter getPresenter() {
		return (CarCheckPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
}
