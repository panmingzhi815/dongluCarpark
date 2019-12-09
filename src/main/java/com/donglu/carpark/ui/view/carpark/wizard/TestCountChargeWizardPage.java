package com.donglu.carpark.ui.view.carpark.wizard;

import java.util.Date;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TestCountChargeWizardPage extends WizardPage {
	private Text txt_price;

	protected TestCountChargeWizardPage() {
		super("测试收费标准");
		setTitle("测试收费标准");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setText("进场时间");
		
		DateChooserCombo dateChooserCombo_inTime = new DateChooserCombo(composite_1, SWT.BORDER);
		
		DateTime dateTime_inTime = new DateTime(composite_1, SWT.BORDER | SWT.TIME | SWT.LONG);
		dateTime_inTime.setSeconds(0);
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setText("出场时间");
		
		DateChooserCombo dateChooserCombo_outTime = new DateChooserCombo(composite_1, SWT.BORDER);
		
		DateTime dateTime_outTime = new DateTime(composite_1, SWT.BORDER | SWT.TIME | SWT.LONG);
		dateTime_outTime.setSeconds(0);
		
		Label label_2 = new Label(composite_1, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("收费金额");
		
		txt_price = new Text(composite_1, SWT.BORDER);
		txt_price.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);
		
		Button btnNewButton = new Button(composite_1, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Date in = dateChooserCombo_inTime.getValue();
				Date out = dateChooserCombo_outTime.getValue();
				Date inTime = new org.joda.time.DateTime(in).withTime(dateTime_inTime.getHours(), dateTime_inTime.getMinutes(), 0, 0).toDate();
				Date outTime = new org.joda.time.DateTime(out).withTime(dateTime_outTime.getHours(), dateTime_outTime.getMinutes(), 55, 555).toDate();
				float f=getWizard().countMonry(inTime,outTime);
				txt_price.setText(f+"");
			}
		});
		btnNewButton.setText("计算收费");
		new Label(composite_1, SWT.NONE);
	}
	@Override
	public TestCountChargeWizard getWizard() {
		return (TestCountChargeWizard) super.getWizard();
	}
}
