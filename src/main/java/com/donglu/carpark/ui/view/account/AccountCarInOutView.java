package com.donglu.carpark.ui.view.account;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractView;
import com.donglu.carpark.ui.common.Presenter;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;

public class AccountCarInOutView extends AbstractView {
	private Text text;
	private Text text_1;
	private Composite composite_list;

	public AccountCarInOutView(Composite parent) {
		super(parent, parent.getStyle());
		setLayout(new GridLayout(1, false));
		
		Group group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(11, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌");
		
		text = new Text(group, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("名称");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_2.setText("出场时间");
		
		DateChooserCombo dateChooserCombo = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateChooserCombo.setValue(StrUtil.getMonthTopTime(new Date()));
		
		DateTime dateTime = new DateTime(group, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateTime.setTime(0, 0, 0);
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_3.setText("到");
		
		DateChooserCombo dateChooserCombo_1 = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		
		DateTime dateTime_1 = new DateTime(group, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateTime_1.setTime(23, 59, 59);
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().search(text.getText(),text_1.getText(),getTime(dateChooserCombo,dateTime),getTime(dateChooserCombo_1,dateTime_1));
			}
		});
		button.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button.setText("查询");
		
		composite_list = new Composite(this, SWT.NONE);
		composite_list.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}
	protected Date getTime(DateChooserCombo combo, DateTime dateTime) {
		if (combo.getValue()==null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(combo.getValue());
		c.set(Calendar.HOUR_OF_DAY, dateTime.getHours());
		c.set(Calendar.MINUTE, dateTime.getMinutes());
		c.set(Calendar.SECOND, dateTime.getSeconds());
		return c.getTime();
	}
	@Override
	public AccountCarInOutPresenter getPresenter() {
		return (AccountCarInOutPresenter) super.getPresenter();
	}
	public Composite getComposite_list() {
		return composite_list;
	}
}
