package com.donglu.carpark.ui.view.setting.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;

public class WeixinSettingWizardPage extends WizardPage {

	protected WeixinSettingWizardPage() {
		super("微信支付设置");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		GridData gd_composite_1 = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gd_composite_1.widthHint = 274;
		composite_1.setLayoutData(gd_composite_1);
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setText("停车场");
		
		ComboViewer comboViewer = new ComboViewer(composite_1, SWT.NONE);
		Combo combo = comboViewer.getCombo();

	}
}
