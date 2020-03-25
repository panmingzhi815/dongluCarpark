package com.donglu.carpark.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.ui.util.FileUtils;

public class ClientConfigWizardPage extends WizardPage {

	private Button button;

	protected ClientConfigWizardPage() {
		super("客户端参数设置");
		setMessage("修改后参数将不再使用服务器设置的参数");
		setTitle("客户端参数设置");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		
		button = new Button(composite_1, SWT.CHECK);
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("临时车入场确认");
		load();
	}

	public void save() {
		FileUtils.writeObject(SystemSettingTypeEnum.临时车入场是否确认.name(), button.getSelection());
	}
	public void load() {
		Object object = FileUtils.readObject(SystemSettingTypeEnum.临时车入场是否确认.name());
		if (object!=null) {
			button.setSelection((boolean) object);
		}
	}
}
