package com.donglu.carpark.ui.view.card;

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

public class CardView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text txt_identifier;
	private Text txt_serialNumber;

	public CardView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(5, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("卡片编号");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		txt_identifier = new Text(group, SWT.BORDER);
		txt_identifier.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_txt_identifier = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_identifier.widthHint = 100;
		txt_identifier.setLayoutData(gd_txt_identifier);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("内码");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		txt_serialNumber = new Text(group, SWT.BORDER);
		txt_serialNumber.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_txt_serialNumber = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_serialNumber.widthHint = 150;
		txt_serialNumber.setLayoutData(gd_txt_serialNumber);
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getPresenter().search(txt_identifier.getText(), txt_serialNumber.getText());
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
	public CardPresenter getPresenter() {
		return (CardPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
}
