package com.donglu.carpark.ui.view.main;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.AbstractView;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;

public class AboutView extends AbstractView {

	public AboutView(Composite parent) {
		super(parent, parent.getStyle());
		setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		Composite composite1 = new Composite(composite, SWT.BORDER);
		GridData gd_composite1 = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gd_composite1.widthHint = 375;
		composite1.setLayoutData(gd_composite1);
		composite1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_composite = new GridLayout(1, true);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.horizontalSpacing = 0;
		gl_composite.marginHeight = 0;
		composite1.setLayout(gl_composite);
		
		Composite composite_11 = new Composite(composite1, SWT.NONE);
		composite_11.setBackgroundImage(JFaceUtil.getImage("donglu"));
		composite_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_11.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite composite_21 = new Composite(composite1, SWT.NONE);
		composite_21.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.marginLeft = 30;
		gl_composite_2.marginTop = 10;
		gl_composite_2.verticalSpacing = 10;
		composite_21.setLayout(gl_composite_2);
		composite_21.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblNewLabel_11 = new Label(composite_21, SWT.NONE);
		lblNewLabel_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_11.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NORMAL));
		lblNewLabel_11.setText("软件名称：");
		
		Label lblNewLabel_2 = new Label(composite_21, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("宋体", 9, SWT.NORMAL));
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_2.setText("深圳市东陆智能停车场系统");
		
		Label label1 = new Label(composite_21, SWT.NONE);
		label1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label1.setText("开发组织：");
		
		Label label_1 = new Label(composite_21, SWT.NONE);
		label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_1.setText("深圳市东陆高新实业有限公司");
		
		Label label_21 = new Label(composite_21, SWT.NONE);
		label_21.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_21.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_21.setText("软件版本：");
		
		Label lblNewLabel_3 = new Label(composite_21, SWT.NONE);
		lblNewLabel_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel_3.setText(SystemSettingTypeEnum.软件版本.getDefaultValue());
		Label label_31 = new Label(composite_21, SWT.NONE);
		label_31.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_31.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_31.setText("数据库版本：");
		
		Label label_4 = new Label(composite_21, SWT.NONE);
		label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_4.setText(ConstUtil.getMapsettings().getOrDefault(SystemSettingTypeEnum.DateBase_version,SystemSettingTypeEnum.DateBase_version.getDefaultValue()));
		
		Label label_51 = new Label(composite_21, SWT.NONE);
		label_51.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_51.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_51.setText("发布时间：");
		
		Label label_6 = new Label(composite_21, SWT.NONE);
		label_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label_6.setText(SystemSettingTypeEnum.发布时间.getDefaultValue());
		new Label(composite_21, SWT.NONE);
		new Label(composite_21, SWT.NONE);
		
		Composite composite_31 = new Composite(composite1, SWT.NONE);
		composite_31.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		composite_31.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridData gd_composite_3 = new GridData(GridData.FILL_HORIZONTAL);
		gd_composite_3.horizontalAlignment = SWT.RIGHT;
		composite_31.setLayoutData(gd_composite_3);
		
		Image handImg = JFaceUtil.getImage("hand_16");
		CLabel lblNewLabel1 = new CLabel(composite_31, SWT.NONE);
		lblNewLabel1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel1.setText("首页");
		lblNewLabel1.setImage(JFaceUtil.getImage("home_32"));
		lblNewLabel1.setCursor(new org.eclipse.swt.graphics.Cursor(getDisplay(),handImg.getImageData(),0,0));
		
		CLabel lblNewLabel2 = new CLabel(composite_31, SWT.NONE);
		lblNewLabel2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel2.setText("邮箱");
		lblNewLabel2.setImage(JFaceUtil.getImage("email_32"));
		lblNewLabel2.setCursor(new org.eclipse.swt.graphics.Cursor(getDisplay(),handImg.getImageData(),0,0));
		
		lblNewLabel1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				try{
					Runtime.getRuntime().exec("cmd /k start "+"http://www.dongluhitec.com/");
				}catch(Exception ex){}
			}
			
		});
		
		lblNewLabel2.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				try{
					Runtime.getRuntime().exec("cmd /k start "+"mailto:154341736@qq.com");
				}catch(Exception ex){}
			}
			
		});
	}

}
