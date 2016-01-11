package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CDT;

public class OpenDoorLogView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_device;
	private ComboViewer comboViewer;

	public OpenDoorLogView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(9, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("操作员");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		comboViewer = new ComboViewer(group, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 65;
		combo.setLayoutData(gd_combo);
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("操作设备");
		
		text_device = new Text(group, SWT.BORDER);
		text_device.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_device = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_device.widthHint = 88;
		text_device.setLayoutData(gd_text_device);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("开始时间");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		CDateTime dateTime = new CDateTime(group, CDT.BORDER);
		dateTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime.setPattern("yyyy-MM-dd HH:mm");
		dateTime.setSelection(new org.joda.time.DateTime(new Date()).minusDays(1).toDate());
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("结束时间");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		CDateTime dateTime_1 = new CDateTime(group, CDT.BORDER);
		dateTime_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime_1.setPattern("yyyy-MM-dd HH:mm");
		dateTime_1.setSelection(new Date());
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String text = comboViewer.getCombo().getText();
					if (text.equals("全部")) {
						text=null;
					}
					getPresenter().search(text, dateTime.getSelection(), dateTime_1.getSelection(),text_device.getText());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setText("查询");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public OpenDoorLogPresenter getPresenter() {
		return (OpenDoorLogPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
	public void setComboValue(List<SingleCarparkSystemUser> list){
		comboViewer.setInput(list);
		comboViewer.getCombo().select(0);
	}
}
