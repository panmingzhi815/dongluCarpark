package com.donglu.carpark.ui.view.lockcar;

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
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkLockCar;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;

import java.util.Date;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;

public class LockCarView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_plateNO;

	public LockCarView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		Font font = SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL);
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(9, false));
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("车牌");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_plateNO = new Text(group, SWT.BORDER);
		text_plateNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_plateNO.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("状态");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Combo combo_1 = new Combo(group, SWT.NONE);
		combo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_1.add("全部");
		combo_1.add(SingleCarparkLockCar.Status.已锁定.name());
		combo_1.add(SingleCarparkLockCar.Status.已解锁.name());
		combo_1.select(0);
		GridData gd_combo_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo_1.widthHint = 74;
		combo_1.setLayoutData(gd_combo_1);
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		label_3.setText("时间");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		DateChooserCombo dateChooserCombo = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo.setValue(new Date());
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setText("-");
		
		DateChooserCombo dateChooserCombo_1 = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo_1.setValue(new Date());
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					
					getPresenter().search(text_plateNO.getText(), null, combo_1.getText(), dateChooserCombo.getValue(), dateChooserCombo_1.getValue());
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
	public LockCarPresenter getPresenter() {
		return (LockCarPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
}
