package com.donglu.carpark.server.imgserver;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.yun.CarparkYunConfig;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class YunConfigUI {

	public static final String CARPARK_YUN_CONFIG = "CarparkYunConfig";
	protected Shell shell;
	private Text text_company;
	private Text text_area;
	private Text text_companyCode;
	private Text text_areaCode;
	private Text text_url;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			YunConfigUI window = new YunConfigUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.MIN|SWT.CLOSE|SWT.ON_TOP);
		shell.setSize(450, 300);
		shell.setText("云服务上传配置");
		shell.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gd_composite.heightHint = 227;
		gd_composite.widthHint = 319;
		composite.setLayoutData(gd_composite);
		CarparkYunConfig cf = (CarparkYunConfig) CarparkFileUtils.readObject(CARPARK_YUN_CONFIG);
		if (cf==null) {
			cf=CarparkYunConfig.getInstance();
		}
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setText("服务地址");
		
		text_url = new Text(composite, SWT.BORDER);
		text_url.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_url.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_url.setText(cf.getUrl());
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("物业编号");
		
		text_companyCode = new Text(composite, SWT.BORDER);
		text_companyCode.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_companyCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_companyCode.setText(cf.getCompanyCode());
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("物业名称");
		
		text_company = new Text(composite, SWT.BORDER);
		text_company.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_company.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_company.setText(cf.getCompany());
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("停车场编号");
		
		text_areaCode = new Text(composite, SWT.BORDER);
		text_areaCode.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_areaCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_areaCode.setText(cf.getAreaCode());
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("停车场名称");
		
		text_area = new Text(composite, SWT.BORDER);
		text_area.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		text_area.setText(cf.getArea());
		Button btnCheckButton = new Button(composite, SWT.CHECK);
		btnCheckButton.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnCheckButton.setText("启动云上传服务(连云平台不要点)");
		new Label(composite, SWT.NONE);
		btnCheckButton.setSelection(cf.getAutoStartServer());
		Button button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String company = text_company.getText();
				String area = text_area.getText();
				boolean selection = btnCheckButton.getSelection();
				String companyCode = text_companyCode.getText();
				String areaCode=text_areaCode.getText();
				String url = text_url.getText();
				CarparkYunConfig instance = CarparkYunConfig.getInstance();
				instance.setCompany(company);
				instance.setArea(area);
				instance.setAutoStartServer(selection);
				instance.setCompanyCode(companyCode);
				instance.setAreaCode(areaCode);
				instance.setUrl(url);
				CarparkFileUtils.writeObject(CARPARK_YUN_CONFIG, instance);
				System.out.println(company+"===="+area+"====="+selection);
				shell.dispose();
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("确定");

	}
}
