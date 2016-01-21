package com.donglu.carpark.ui.view.user;

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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;

public class PrepaidUserPayHistoryView extends Composite implements View {
	private Text text_pay_userName;

	private Composite listComposite;
	private Presenter presenter;
	private Text txt_plateno;

	public PrepaidUserPayHistoryView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));

		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setText("查询");
		group.setLayout(new GridLayout(10, false));

		Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("用户");

		text_pay_userName = new Text(group, SWT.BORDER);
		text_pay_userName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_pay_userName = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_pay_userName.widthHint = 90;
		text_pay_userName.setLayoutData(gd_text_pay_userName);

		Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("车牌");
		
		txt_plateno = new Text(group, SWT.BORDER);
		txt_plateno.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_plateno.setText("");
		GridData gd_txt_plateno = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txt_plateno.widthHint = 110;
		txt_plateno.setLayoutData(gd_txt_plateno);

		Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("起始时间");

		DateChooserCombo dateChooserCombo = new DateChooserCombo(group, SWT.BORDER | SWT.READ_ONLY);
		dateChooserCombo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo.setValue(new org.joda.time.DateTime(new Date()).minusDays(1).toDate());

		Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setText("终止时间");

		DateChooserCombo dateChooserCombo_1 = new DateChooserCombo(group, SWT.BORDER | SWT.READ_ONLY);
		dateChooserCombo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo_1.setValue(new Date());
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String text = txt_plateno.getText();
					getPresenter().searchCharge(text_pay_userName.getText(), text, dateChooserCombo.getValue(), dateChooserCombo_1.getValue());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("查询");

		Button button_1 = new Button(group, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().export();
			}
		});
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.setText("导出");

		listComposite = new Composite(this, SWT.NONE);
		GridData gd_listComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_listComposite.widthHint = 439;
		listComposite.setLayoutData(gd_listComposite);
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	public void setCarparkPayHistoryPresenter(PrepaidUserPayHistoryPresenter carparkPayHistoryPresenter) {
		this.presenter = carparkPayHistoryPresenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public PrepaidUserPayHistoryPresenter getPresenter() {
		return (PrepaidUserPayHistoryPresenter) this.presenter;
	}
}
