package com.donglu.carpark.ui.view.speed.wizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemSetting;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;

public class OverSpeedSettingWizardPage extends WizardPage {
	private Text txt_fixDay;
	private Text txt_fixSize;
	private Text txt_tempDay;
	private Text txt_tempSize;
	private Text txt_tempBlackDay;
	private Button btn_start;

	protected OverSpeedSettingWizardPage() {
		super("超速车辆设置");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		
		btn_start = new Button(composite_1, SWT.CHECK);
		btn_start.setText("启用车辆车速统计");
		
		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		composite_2.setLayout(new GridLayout(5, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label = new Label(composite_2, SWT.NONE);
		label.setText("固定车");
		
		txt_fixDay = new Text(composite_2, SWT.BORDER);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 30;
		txt_fixDay.setLayoutData(gd_text);
		
		Label label_1 = new Label(composite_2, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("天内超速");
		
		txt_fixSize = new Text(composite_2, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_1.widthHint = 30;
		txt_fixSize.setLayoutData(gd_text_1);
		
		Label label_2 = new Label(composite_2, SWT.NONE);
		label_2.setText("次自动删除");
		
		Composite composite_3 = new Composite(composite_1, SWT.NONE);
		composite_3.setLayout(new GridLayout(7, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_3 = new Label(composite_3, SWT.NONE);
		label_3.setText("临时车");
		
		txt_tempDay = new Text(composite_3, SWT.BORDER);
		GridData gd_text_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_2.widthHint = 30;
		txt_tempDay.setLayoutData(gd_text_2);
		
		Label label_4 = new Label(composite_3, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("天内超速");
		
		txt_tempSize = new Text(composite_3, SWT.BORDER);
		GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_3.widthHint = 30;
		txt_tempSize.setLayoutData(gd_text_3);
		
		Label label_5 = new Label(composite_3, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("次自动拉黑");
		
		txt_tempBlackDay = new Text(composite_3, SWT.BORDER);
		GridData gd_text_4 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_4.widthHint = 30;
		txt_tempBlackDay.setLayoutData(gd_text_4);
		
		Label label_6 = new Label(composite_3, SWT.NONE);
		label_6.setText("天");
		init();
	}
	private void init() {
		CarparkDatabaseServiceProvider sp = getWizard().getSp();
		SingleCarparkSystemSetting setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用测速系统.name());
		btn_start.setSelection(Boolean.valueOf(setting.getSettingValue()));
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车超速自动删除.name());
		String[] split = setting.getSettingValue().split("-");
		txt_fixDay.setText(split[0]);
		txt_fixSize.setText(split[1]);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.临时车超速自动拉黑.name());
		split = setting.getSettingValue().split("-");
		txt_tempDay.setText(split[0]);
		txt_tempSize.setText(split[1]);
		txt_tempBlackDay.setText(split[2]);
	}
	
	protected void save() {
		CarparkDatabaseServiceProvider sp = getWizard().getSp();
		SingleCarparkSystemSetting setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.启用测速系统.name());
		setting.setSettingValue(btn_start.getSelection()+"");
		sp.getCarparkService().saveSystemSetting(setting);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.固定车超速自动删除.name());
		setting.setSettingValue(txt_fixDay.getText()+"-"+txt_fixSize.getText());
		sp.getCarparkService().saveSystemSetting(setting);
		
		setting = sp.getCarparkService().findSystemSettingByKey(SystemSettingTypeEnum.临时车超速自动拉黑.name());
		setting.setSettingValue(txt_tempDay.getText()+"-"+txt_tempSize.getText()+"-"+txt_tempBlackDay.getText());
		sp.getCarparkService().saveSystemSetting(setting);
	}

	@Override
	public OverSpeedSettingWizard getWizard() {
		return (OverSpeedSettingWizard) super.getWizard();
	}
}
