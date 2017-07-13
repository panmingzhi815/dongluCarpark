package com.donglu.carpark.ui.view.setting.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.widgets.Text;

public class WeixinSettingWizardPage extends WizardPage {
	private Text text;

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
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("停车场编号");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblid = new Label(composite_1, SWT.NONE);
		lblid.setText("公众号ID");
		new Label(composite_1, SWT.NONE);
		
		Label label_2 = new Label(composite_1, SWT.NONE);
		label_2.setText("公众号密钥");
		new Label(composite_1, SWT.NONE);
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setText("商铺号");
		new Label(composite_1, SWT.NONE);
		
		Label label_3 = new Label(composite_1, SWT.NONE);
		label_3.setText("支付密钥");
		new Label(composite_1, SWT.NONE);
		
		Label label_4 = new Label(composite_1, SWT.NONE);
		label_4.setText("证书密码");
		new Label(composite_1, SWT.NONE);

	}
}
