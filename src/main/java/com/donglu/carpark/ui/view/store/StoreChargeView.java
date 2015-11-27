package com.donglu.carpark.ui.view.store;

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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;

public class StoreChargeView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_name;
	private Text text;

	public StoreChargeView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		Font font = SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL);
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(10, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("商铺名称");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_name = new Text(group, SWT.BORDER);
		text_name.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("操作员");
		
		text = new Text(group, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setText("起始时间");
		
		DateChooserCombo dateChooserCombo = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_2.setText("结束时间");
		
		DateChooserCombo dateChooserCombo_1 = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().search(text_name.getText(),text.getText(),dateChooserCombo.getValue(),dateChooserCombo_1.getValue());
			}
		});
		button.setText("查询");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button_1 = new Button(group, SWT.NONE);
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.setText("导出");
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public StoreChargePresenter getPresenter() {
		return (StoreChargePresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
}
