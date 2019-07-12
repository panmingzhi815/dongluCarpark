package com.donglu.carpark.ui.view.setting;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class SettingView extends Composite implements View {
	private String cLIENT_IMAGE_SAVE_FILE_PATH = "clientImageSaveFilePath";
	private Presenter presenter;
	private Text text;
	private Text text_1;
	private Text text_setting_dataBaseSave;
	private Text text_setting_imgSave;
	private Text text_setting_imgSaveDays;
	private Text text_5;
	private Text text_6;
	private Composite listComposite;
	private Text text_7;

	Map<SystemSettingTypeEnum, String> mapSystemSetting = new HashMap<>();
	private Text text_2;
	private Group group_childCarparkSetting;
	private ScrolledComposite scrolledComposite;
	private Text text_carparkChangeCarTime;
	private Text text_3;
	private Text text_4;
	private Text text_8;
	protected boolean isRestart=false;

	public SettingView(Composite parent, int style) {
		super(parent, style);
		createView();
	}

	/**
	 * 
	 */
	public void createView() {
		setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(this, SWT.NONE);
		String readObject = (String) CarparkFileUtils.readObject(cLIENT_IMAGE_SAVE_FILE_PATH);
		String string = readObject == null ? System.getProperty("user.dir") + "/img" : readObject;

		Composite composite_3 = new Composite(sashForm, SWT.NONE);
		GridLayout gl_composite_3 = new GridLayout(1, false);
		gl_composite_3.verticalSpacing = 10;
		gl_composite_3.marginWidth = 0;
		gl_composite_3.marginHeight = 0;
		gl_composite_3.horizontalSpacing = 0;
		composite_3.setLayout(gl_composite_3);

		Composite composite_10 = new Composite(composite_3, SWT.NONE);
		composite_10.setLayout(new GridLayout(2, false));
		composite_10.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		Label label_9 = new Label(composite_10, SWT.NONE);
		label_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		label_9.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		label_9.setText("停车场设置");

		Button btn_save = new Button(composite_10, SWT.NONE);
		btn_save.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		btn_save.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.BOLD));
		btn_save.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().saveAll(mapSystemSetting,true);
			}
		});
		btn_save.setText("保存设置");
		btn_save.setImage(JFaceUtil.getImage("save_16"));

		scrolledComposite = new ScrolledComposite(composite_3, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginHeight = 0;
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);

		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.BOLD));
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("停车场基本设置");

		Group group = new Group(tabFolder, SWT.NONE);
		tabItem.setControl(group);
		group.setText("停车场基本设置");
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		group.setBounds(0, 0, 675, 539);
		GridLayout gl_group = new GridLayout(3, false);
		gl_group.marginHeight = 0;
		group.setLayout(gl_group);

		Button button = new Button(group, SWT.CHECK);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.车位满是否允许临时车入场, button.getSelection() + "");
			}
		});
		button.setToolTipText("选中后，停车场车位满允许临时车进");
		button.setText("车位满是否允许临时车入场");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许临时车入场)));

		Button button_1 = new Button(group, SWT.CHECK);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.临时车入场是否确认, button_1.getSelection() + "");
			}
		});
		button_1.setToolTipText("选中后，临时车入场需要确认放行");
		button_1.setText("临时车入场是否需要确认");
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认)));

		Button button_2 = new Button(group, SWT.CHECK);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.临时车零收费是否自动出场, button_2.getSelection() + "");
			}
		});
		button_2.setToolTipText("选中后，收费0元自动放行");
		button_2.setText("临时车零收费是否自动出场");
		button_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_2.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车零收费是否自动出场)));

		Button button_31 = new Button(group, SWT.CHECK);
		button_31.setToolTipText("选中后允许使设备道闸在指定时间内有效，在修改设备处设置时间");
		button_31.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.允许设备限时, button_31.getSelection() + "");
				isRestart=true;
			}
		});
		button_31.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_31.setText("允许对设备进行限时工作");
		button_31.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.允许设备限时)));

		Button button_8 = new Button(group, SWT.CHECK);
		button_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.出场确认放行, button_8.getSelection() + "");
			}
		});
		button_8.setToolTipText("当选中时，出场收费放行会弹出确认框");
		button_8.setText("出场收费时是否需要确认");
		button_8.setSelection(false);
		button_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_8.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.出场确认放行)));

		Button button_5 = new Button(group, SWT.CHECK);
		button_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.是否允许无牌车进, button_5.getSelection() + "");
			}
		});
		button_5.setToolTipText("选中后，无牌车可以进入停车场");
		button_5.setText("是否允许无牌车进入停车场");
		button_5.setSelection(false);
		button_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_5.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.是否允许无牌车进)));

		Button button_32 = new Button(group, SWT.CHECK);
		button_32.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.访客车进场次数用完不能随便出, button_32.getSelection() + "");
			}
		});
		button_32.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_32.setText("访客车出场次数限制");
		button_32.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.访客车进场次数用完不能随便出)));

		Button button_37 = new Button(group, SWT.CHECK);
		button_37.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.出场免费确认放行, button_37.getSelection() + "");
			}
		});
		button_37.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_37.setText("出场免费时是否需要确认");
		button_37.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.出场免费确认放行)));

		Button button_19 = new Button(group, SWT.CHECK);
		button_19.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.临时车通道限制, button_19.getSelection() + "");
			}
		});
		button_19.setToolTipText("选中后临时车可以在固定车通道和储值车通道进出");
		button_19.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_19.setText("临时车不做通道限制");
		button_19.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车通道限制)));

		Button btnhttp = new Button(group, SWT.CHECK);
		btnhttp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.启动HTTP对外服务, btnhttp.getSelection() + "");
				isRestart=true;
			}
		});
		btnhttp.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		btnhttp.setText("启用HTTP对外服务");
		btnhttp.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启动HTTP对外服务)));

		Button button_10 = new Button(group, SWT.CHECK);
		button_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.自动识别出场车辆类型, button_10.getSelection() + "");
			}
		});
		button_10.setToolTipText("选中时，自动把黄牌车识别为大车，其他为小车");
		button_10.setText("自动识别出场车辆类型");
		button_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_10.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.自动识别出场车辆类型)));

		Button button_30 = new Button(group, SWT.CHECK);
		button_30.setToolTipText("选中后，监控界面会在每天晚上12点对所有摄像机下载固定车牌");
		button_30.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.自动下载车牌, button_30.getSelection() + "");
				isRestart=true;
			}
		});
		button_30.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_30.setText("自动下载用户车牌到设备");
		button_30.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.自动下载车牌)));

		Button button_23 = new Button(group, SWT.CHECK);
		button_23.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.保存遥控开闸记录, button_23.getSelection() + "");
				isRestart=true;
			}
		});
		button_23.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_23.setText("保存遥控开闸记录");
		
		Button button_39 = new Button(group, SWT.CHECK);
		button_39.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.收费放行打印小票, button_39.getSelection() + "");
			}
		});
		button_39.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_39.setText("收费放行打印小票");
		button_39.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.收费放行打印小票)));
		new Label(group, SWT.NONE);
		

		Composite composite_2 = new Composite(group, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		composite_2.setLayout(new GridLayout(4, false));

		Label label = new Label(composite_2, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("同一车牌识别间隔");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		text = new Text(composite_2, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			String s = text.getText();

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					String text2 = text.getText();
					if (!StrUtil.isEmpty(text2)) {
						Integer.valueOf(text2);
						s = text2;
						mapSystemSetting.put(SystemSettingTypeEnum.同一车牌识别间隔, text2);
					}
				} catch (NumberFormatException e1) {
					text.setText(s);
				}
			}
		});
		text.setText(mapSystemSetting.get(SystemSettingTypeEnum.同一车牌识别间隔));
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 82;
		text.setLayoutData(gd_text);

		Label label_1 = new Label(composite_2, SWT.NONE);
		label_1.setText("秒");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		Label label2 = new Label(composite_2, SWT.NONE);
		GridData gd_label2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label2.widthHint = 50;
		label2.setLayoutData(gd_label2);

		Composite composite_4 = new Composite(group, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		composite_4.setLayout(new GridLayout(5, false));

		Button button_11 = new Button(composite_4, SWT.CHECK);
		button_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.左下监控, button_11.getSelection() + "");
			}
		});
		button_11.setToolTipText("选中开启监控界面左下视频监控");
		button_11.setText("左下监控");
		button_11.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.左下监控)));
		button_11.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Button button_12 = new Button(composite_4, SWT.CHECK);
		button_12.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.右下监控, button_12.getSelection() + "");
			}
		});
		button_12.setToolTipText("选中开启监控界面右下视频监控");
		button_12.setText("右下监控");
		button_12.setSelection(false);
		button_12.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_12.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.右下监控)));

		Button button_13 = new Button(composite_4, SWT.CHECK);
		button_13.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.进场允许修改车牌, button_13.getSelection() + "");
			}
		});
		button_13.setText("允许修改进场车牌");
		button_13.setSelection(false);
		button_13.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_13.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.进场允许修改车牌)));

		Button button_14 = new Button(composite_4, SWT.CHECK);
		button_14.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.进场允许手动入场, button_14.getSelection() + "");
			}
		});
		button_14.setText("允许手动入场");
		button_14.setSelection(false);
		button_14.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_14.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.进场允许手动入场)));

		Button button_29 = new Button(composite_4, SWT.CHECK);
		button_29.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.停车场重复计费, button_29.getSelection() + "");
			}
		});
		button_29.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_29.setText("停车场重复收费");
		button_29.setToolTipText("选中后,选中后车辆每次出场都会收费，不会累计收费到了一天最大收费就不收费了");
		button_29.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.停车场重复计费)));

		Composite composite_5 = new Composite(group, SWT.NONE);
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		composite_5.setLayout(new GridLayout(5, false));

		Label label_6 = new Label(composite_5, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("数据库备份位置");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		text_setting_dataBaseSave = new Text(composite_5, SWT.BORDER);
		text_setting_dataBaseSave.setText(mapSystemSetting.get(SystemSettingTypeEnum.数据库备份位置));
		text_setting_dataBaseSave.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_setting_dataBaseSave.setEditable(false);
		GridData gd_text_setting_dataBaseSave = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_setting_dataBaseSave.widthHint = 239;
		text_setting_dataBaseSave.setLayoutData(gd_text_setting_dataBaseSave);

		Button button_15 = new Button(composite_5, SWT.NONE);
		button_15.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String open = getPresenter().getDatabaseFilePath();
				if (StrUtil.isEmpty(open)) {
					return;
				}
				text_setting_dataBaseSave.setText(open);
				mapSystemSetting.put(SystemSettingTypeEnum.数据库备份位置, open);
			}
		});
		button_15.setText("...");

		Button button_16 = new Button(composite_5, SWT.NONE);
		button_16.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().backup(text_setting_dataBaseSave.getText());
			}
		});
		button_16.setText("备份");
		button_16.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Button button_17 = new Button(composite_5, SWT.NONE);
		button_17.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().restoreDataBase(text_setting_dataBaseSave.getText());
			}
		});
		button_17.setText("还原");
		button_17.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Composite composite_6 = new Composite(group, SWT.NONE);
		composite_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		composite_6.setLayout(new GridLayout(3, false));

		Label label_7 = new Label(composite_6, SWT.NONE);
		GridData gd_label_7 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_label_7.widthHint = 135;
		label_7.setLayoutData(gd_label_7);
		label_7.setText("抓拍图片存放位置");
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		text_setting_imgSave = new Text(composite_6, SWT.BORDER);
		text_setting_imgSave.setText(string);
		text_setting_imgSave.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_setting_imgSave.setEditable(false);
		GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_3.widthHint = 241;
		text_setting_imgSave.setLayoutData(gd_text_3);

		Button button_18 = new Button(composite_6, SWT.NONE);
		button_18.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getShell(), SWT.SINGLE);
				String open = directoryDialog.open();
				if (StrUtil.isEmpty(open)) {
					return;
				}
				text_setting_imgSave.setText(open);
				CarparkFileUtils.writeObject(cLIENT_IMAGE_SAVE_FILE_PATH, open);
			}
		});
		button_18.setText("...");

		Composite composite_7 = new Composite(group, SWT.NONE);
		composite_7.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		composite_7.setLayout(new GridLayout(4, false));

		Button btn_imgSaveMonth = new Button(composite_7, SWT.CHECK);
		btn_imgSaveMonth.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.是否自动删除图片, btn_imgSaveMonth.getSelection() + "");
				text_setting_imgSaveDays.setEditable(btn_imgSaveMonth.getSelection());
			}
		});
		btn_imgSaveMonth.setToolTipText("选中之后，表示会自动删除照片");
		btn_imgSaveMonth.setText("保存多少天的照片");
		btn_imgSaveMonth.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.是否自动删除图片)));
		btn_imgSaveMonth.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		text_setting_imgSaveDays = new Text(composite_7, SWT.BORDER);
		text_setting_imgSaveDays.addKeyListener(new KeyAdapter() {
			String s = text_setting_imgSaveDays.getText();

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					String text2 = text_setting_imgSaveDays.getText();
					if (!StrUtil.isEmpty(text2)) {
						Integer.valueOf(text2);
						s = text2;
						mapSystemSetting.put(SystemSettingTypeEnum.图片保存多少天, text2);
					}
				} catch (NumberFormatException e1) {
					text_setting_imgSaveDays.setText(s);
				}
			}
		});
		text_setting_imgSaveDays.setEditable(btn_imgSaveMonth.getSelection());
		text_setting_imgSaveDays.setText(mapSystemSetting.get(SystemSettingTypeEnum.图片保存多少天));
		text_setting_imgSaveDays.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_4.widthHint = 67;
		text_setting_imgSaveDays.setLayoutData(gd_text_4);
		Label label3 = new Label(composite_7, SWT.NONE);
		GridData gd_label3 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label3.widthHint = 50;
		label3.setLayoutData(gd_label3);

		Button btnCheckButton_1 = new Button(composite_7, SWT.CHECK);
		btnCheckButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.启用车牌报送, btnCheckButton_1.getSelection() + "");
			}
		});
		btnCheckButton_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnCheckButton_1.setText("启用车牌报送");
		btnCheckButton_1.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用车牌报送)));

		Composite composite_1 = new Composite(group, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));

		Label lblNewLabel_3 = new Label(composite_1, SWT.NONE);
		lblNewLabel_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("车位数显示方式");

		ComboViewer comboViewer_slot = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo_slot = comboViewer_slot.getCombo();
		combo_slot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("=============" + combo_slot.getSelectionIndex());
				mapSystemSetting.put(SystemSettingTypeEnum.车位数显示方式, "" + combo_slot.getSelectionIndex());
			}
		});
		combo_slot.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo_slot = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo_slot.widthHint = 200;
		combo_slot.setLayoutData(gd_combo_slot);
		comboViewer_slot.setContentProvider(new ArrayContentProvider());
		comboViewer_slot.setLabelProvider(new LabelProvider());
		comboViewer_slot.setInput(new String[] { "显示临时车剩余车位", "显示固定车剩余车位", "显示总剩余车位", "实时临时车位", "实时固定车位", "实时总车位数", "本地实时车位" });
		combo_slot.select(Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位数显示方式)));

		Composite composite_9 = new Composite(group, SWT.NONE);
		composite_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		composite_9.setLayout(new GridLayout(4, false));

		Label lblNewLabel_2 = new Label(composite_9, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_2.setText("免费原因");

		ComboViewer comboViewer = new ComboViewer(composite_9, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(mapSystemSetting.get(SystemSettingTypeEnum.免费原因).split(","));
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 200;
		combo.setLayoutData(gd_combo);
		combo.select(0);

		Button btnNewButton = new Button(composite_9, SWT.NONE);
		btnNewButton.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String reason = getPresenter().setFreeReson();
				if (StrUtil.isEmpty(reason)) {
					return;
				}
				String string2 = mapSystemSetting.get(SystemSettingTypeEnum.免费原因);
				if (string2.indexOf(reason) > -1) {
					return;
				}
				combo.add(reason);

				if (!StrUtil.isEmpty(string2)) {
					string2 += "," + reason;
				}
				mapSystemSetting.put(SystemSettingTypeEnum.免费原因, string2);
			}
		});
		btnNewButton.setText("添加");

		Button btnNewButton_1 = new Button(composite_9, SWT.NONE);
		btnNewButton_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String reason = combo.getText();
				if (reason.equals("其他原因")) {
					return;
				}
				combo.remove(combo.getSelectionIndex());
				combo.select(0);
				String string2 = mapSystemSetting.get(SystemSettingTypeEnum.免费原因);
				String replaceAll = string2.replaceAll("," + reason, "");
				mapSystemSetting.put(SystemSettingTypeEnum.免费原因, replaceAll);
			}
		});
		btnNewButton_1.setText("删除");

		TabItem tbtmC = new TabItem(tabFolder, SWT.NONE);
		tbtmC.setText("固定车设置");

		Group group_3 = new Group(tabFolder, SWT.NONE);
		tbtmC.setControl(group_3);
		group_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.BOLD));
		group_3.setLayout(new GridLayout(1, false));
		group_3.setText("固定车设置");

		Composite composite_14 = new Composite(group_3, SWT.NONE);
		composite_14.setLayout(new GridLayout(3, false));
		composite_14.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Button button_3 = new Button(composite_14, SWT.CHECK);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.车位满是否允许免费车入场, button_3.getSelection() + "");
			}
		});
		button_3.setToolTipText("选中后，停车场车位满允许固定免费车进");
		button_3.setText("车位满是否允许固定车入场");
		button_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_3.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许免费车入场)));

		Button button_4 = new Button(composite_14, SWT.CHECK);
		button_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.固定车入场是否确认, button_4.getSelection() + "");
			}
		});
		button_4.setToolTipText("选中后，固定车入场需要确认放行");
		button_4.setText("固定车入场是否需要确认");
		button_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_4.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认)));

		Button button_7 = new Button(composite_14, SWT.CHECK);
		button_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.固定车出场确认, button_7.getSelection() + "");
			}
		});
		button_7.setToolTipText("选中后，固定车出场场需要确认放行");
		button_7.setText("固定车出场是否需要确认");
		button_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_7.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认)));

		Button button_9 = new Button(composite_14, SWT.CHECK);
		button_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.固定车车位满作临时车计费, button_9.getSelection() + "");
			}
		});
		button_9.setToolTipText("选择之后，固定用户车位停满后再进车就会当作临时车计费，否则固定车车位满就不允许进入");
		button_9.setText("固定车车位满作临时车计费");
		button_9.setSelection(false);
		button_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_9.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车车位满作临时车计费)));

		Button button_24 = new Button(composite_14, SWT.CHECK);
		button_24.setToolTipText("选择后固定车到期后作临时车计费，否则到期后不允许进入。默认选中");
		button_24.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_24.setText("固定车到期后作临时车计费");
		button_24.setSelection(Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期变临时车)));
		button_24.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = button_24.getSelection();
				mapSystemSetting.put(SystemSettingTypeEnum.固定车到期变临时车, selection + "");
			}
		});

		Button button_26 = new Button(composite_14, SWT.CHECK);
		button_26.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selection = button_26.getSelection();
				mapSystemSetting.put(SystemSettingTypeEnum.固定车到期所属停车场限制, selection + "");
			}
		});
		button_26.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_26.setText("固定车到期停车场限制");
		button_26.setToolTipText("固定车到期后是否允许进入所属停车场，勾选后允许随意进出所属停车场");
		button_26.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车到期所属停车场限制)));

		Button button_6 = new Button(composite_14, SWT.CHECK);
		button_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.车位满是否允许储值车入场, button_6.getSelection() + "");
			}
		});
		button_6.setToolTipText("选中后，停车场车位满允许固定储值车进");
		button_6.setText("车位满是否允许储值车入场");
		button_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_6.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位满是否允许储值车入场)));

		Button button_33 = new Button(composite_14, SWT.CHECK);
		button_33.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_33.setText("固定车转临时车弹窗提示");

		Button button_28 = new Button(composite_14, SWT.CHECK);
		button_28.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.固定车到期提醒, button_28.getSelection() + "");
			}
		});
		button_28.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_28.setText("固定车到期提醒");
		button_28.setToolTipText("选择后，固定车即将到期或到期后会在管理界面提醒");
		button_28.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车到期提醒)));

		Composite composite_11 = new Composite(group_3, SWT.NONE);
		composite_11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_11.setLayout(new GridLayout(2, false));

		Label label_plateMachSize = new Label(composite_11, SWT.NONE);
		label_plateMachSize.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_plateMachSize.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_plateMachSize.setText("固定车放行需连续正确匹配车牌字符数量");

		Combo combo_1 = new Combo(composite_11, SWT.READ_ONLY);
		GridData gd_combo_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo_1.widthHint = 50;
		combo_1.setLayoutData(gd_combo_1);
		combo_1.setItems(new String[] { "6", "7" });
		combo_1.setText(mapSystemSetting.get(SystemSettingTypeEnum.固定车车牌匹配字符数));
		
		Composite composite_15 = new Composite(group_3, SWT.NONE);
		composite_15.setLayout(new GridLayout(3, false));
		composite_15.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
				Button button_carparkChangeCar = new Button(composite_15, SWT.CHECK);
				button_carparkChangeCar.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean selection = button_carparkChangeCar.getSelection();
						mapSystemSetting.put(SystemSettingTypeEnum.绑定车辆允许场内换车, selection + "");
						text_carparkChangeCarTime.setEnabled(selection);
					}
				});
				button_carparkChangeCar.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
				button_carparkChangeCar.setText("允许场内换车");
				button_carparkChangeCar.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.绑定车辆允许场内换车)));
				
						text_carparkChangeCarTime = new Text(composite_15, SWT.BORDER);
						text_carparkChangeCarTime.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
						text_carparkChangeCarTime.setEnabled(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.绑定车辆允许场内换车)));
						text_carparkChangeCarTime.setText(mapSystemSetting.get(SystemSettingTypeEnum.绑定车辆场内换车时间));
								
										Label label_10 = new Label(composite_15, SWT.NONE);
										label_10.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
										label_10.setText("分");
						text_carparkChangeCarTime.addModifyListener(new ModifyListener() {
							@Override
							public void modifyText(ModifyEvent e) {
								try {
									String text2 = text_carparkChangeCarTime.getText();
									Integer.valueOf(text2);
									mapSystemSetting.put(SystemSettingTypeEnum.绑定车辆场内换车时间, text2);
								} catch (NumberFormatException e1) {

								}
							}
						});

		Group group_1 = new Group(group_3, SWT.NONE);
		group_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		group_1.setLayout(new GridLayout(4, false));
		group_1.setText("储值车设置");

		Label lblNewLabel = new Label(group_1, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("提醒金额");

		text_5 = new Text(group_1, SWT.BORDER);
		text_5.addKeyListener(new KeyAdapter() {
			String s = text_5.getText();

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					String text2 = text_5.getText();
					if (!StrUtil.isEmpty(text2)) {
						Float.valueOf(text2);
						s = text2;
						mapSystemSetting.put(SystemSettingTypeEnum.储值车提醒金额, text2);
					}
				} catch (NumberFormatException e1) {
					text_5.setText(s);
				}
			}
		});
		text_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_5 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_5.widthHint = 100;
		text_5.setLayoutData(gd_text_5);
		text_5.setText(mapSystemSetting.get(SystemSettingTypeEnum.储值车提醒金额));

		Label lblNewLabel_1 = new Label(group_1, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("进出场限制金额");

		text_6 = new Text(group_1, SWT.BORDER);
		text_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_6 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_6.widthHint = 100;
		text_6.setLayoutData(gd_text_6);
		text_6.setText(mapSystemSetting.get(SystemSettingTypeEnum.储值车进出场限制金额));

		group_childCarparkSetting = new Group(group_3, SWT.NONE);
		group_childCarparkSetting.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		group_childCarparkSetting.setLayout(new GridLayout(3, false));
		group_childCarparkSetting.setText("子停车场设置");

		Button button_27 = new Button(group_childCarparkSetting, SWT.CHECK);
		button_27.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.固定车非所属停车场停留收费, button_27.getSelection() + "");
			}
		});
		button_27.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_27.setToolTipText("选中后固定车在其他停车场停留超时后会收费");
		button_27.setText("固定车非所属停车场停留时间");
		button_27.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.固定车非所属停车场停留收费)));

		text_2 = new Text(group_childCarparkSetting, SWT.BORDER);
		text_2.addKeyListener(new KeyAdapter() {
			String s = "";

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					String text2 = text_2.getText();
					Integer.valueOf(text2);
					s = text2;
					mapSystemSetting.put(SystemSettingTypeEnum.固定车非所属停车场停留时间, s);
				} catch (NumberFormatException e1) {
					text_2.setText(s);
				}
			}
		});
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_2.widthHint = 50;
		text_2.setLayoutData(gd_text_2);
		text_2.setText(mapSystemSetting.get(SystemSettingTypeEnum.固定车非所属停车场停留时间));

		Label label_8 = new Label(group_childCarparkSetting, SWT.NONE);
		label_8.setText("分钟");
		text_6.addKeyListener(new KeyAdapter() {
			String s = text_6.getText();

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					String text2 = text_6.getText();
					if (!StrUtil.isEmpty(text2)) {
						Float.valueOf(text2);
						s = text2;
						mapSystemSetting.put(SystemSettingTypeEnum.储值车进出场限制金额, text2);
					}
				} catch (NumberFormatException e1) {
					text_6.setText(s);
				}
			}
		});
		combo_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.固定车车牌匹配字符数, combo_1.getText());
			}
		});

		TabItem tabItem_2 = new TabItem(tabFolder, SWT.NONE);
		tabItem_2.setText("在线支付设置");

		Group grpApp = new Group(tabFolder, SWT.NONE);
		tabItem_2.setControl(grpApp);
		grpApp.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		grpApp.setLayout(new GridLayout(3, false));
		grpApp.setText("APP服务");

		Button btncjlapp = new Button(grpApp, SWT.CHECK);
		btncjlapp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.启用CJLAPP支付, btncjlapp.getSelection() + "");
			}
		});
		btncjlapp.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btncjlapp.setText("启用在线支付");
		btncjlapp.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用CJLAPP支付)));

		Button button_34 = new Button(grpApp, SWT.CHECK);
		button_34.setToolTipText("无牌车进场会在显示屏显示二维码");
		button_34.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.无车牌时使用二维码进出场, button_34.getSelection() + "");
			}
		});
		button_34.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_34.setText("无牌车扫码进出场(需要一体机)");
		button_34.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.无车牌时使用二维码进出场)));

		Button button_35 = new Button(grpApp, SWT.CHECK);
		button_35.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.优先使用云平台计费, button_35.getSelection() + "");
			}
		});
		button_35.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_35.setText("优先使用云平台计费");
		button_35.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.优先使用云平台计费)));

		Button button_36 = new Button(grpApp, SWT.CHECK);
		button_36.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.监控界面提示网络故障, button_36.getSelection() + "");
			}
		});
		button_36.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_36.setText("监控端提示外网故障");
		button_36.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.监控界面提示网络故障)));

		Button button_38 = new Button(grpApp, SWT.CHECK);
		button_38.setToolTipText("出场时在出口展示二维码");
		button_38.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.使用二维码缴费, button_38.getSelection() + "");
			}
		});
		button_38.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_38.setText("使用二维码缴费(需要一体机)");
		button_38.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.使用二维码缴费)));
		
		Button button_40 = new Button(grpApp, SWT.CHECK);
		button_40.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.使用设备二维码, button_40.getSelection()+"");
			}
		});
		button_40.setToolTipText("设备编号必须更云平台上设备序列号对应");
		button_40.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_40.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.使用设备二维码)));
		button_40.setText("启用设备二维码");

		Composite composite_12 = new Composite(grpApp, SWT.NONE);
		GridLayout gl_composite_12 = new GridLayout(3, false);
		gl_composite_12.marginHeight = 0;
		gl_composite_12.marginWidth = 0;
		composite_12.setLayout(gl_composite_12);
		composite_12.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_11 = new Label(composite_12, SWT.NONE);
		label_11.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_11.setText("自动检测缴费间隔");

		text_3 = new Text(composite_12, SWT.BORDER);
		text_3.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String text2 = text_3.getText();
				try {
					Integer.valueOf(text2);
					mapSystemSetting.put(SystemSettingTypeEnum.出场时检测云平台缴费间隔, text2);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		text_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_3.setText(mapSystemSetting.get(SystemSettingTypeEnum.出场时检测云平台缴费间隔));
		GridData gd_text_31 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text_31.widthHint = 36;
		text_3.setLayoutData(gd_text_31);

		Label label_12 = new Label(composite_12, SWT.NONE);
		label_12.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_12.setText("秒");

		Composite composite_13 = new Composite(grpApp, SWT.NONE);
		GridLayout gl_composite_13 = new GridLayout(3, false);
		gl_composite_13.marginWidth = 0;
		gl_composite_13.marginHeight = 0;
		composite_13.setLayout(gl_composite_13);
		composite_13.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_13 = new Label(composite_13, SWT.NONE);
		label_13.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_13.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_13.setText("自动检测缴费时长");

		text_4 = new Text(composite_13, SWT.BORDER);
		text_4.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_4.setText("120");
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_4.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String text2 = text_4.getText();
				try {
					Integer.valueOf(text2);
					mapSystemSetting.put(SystemSettingTypeEnum.出场时等待云平台缴费超时时长, text2);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		text_4.setText(mapSystemSetting.get(SystemSettingTypeEnum.出场时等待云平台缴费超时时长));

		Label label_14 = new Label(composite_13, SWT.NONE);
		label_14.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_14.setText("秒");
		new Label(grpApp, SWT.NONE);
		
		Button btn_lianjielvdi = new Button(grpApp, SWT.CHECK);
		btn_lianjielvdi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.上传数据到绿地平台, btn_lianjielvdi.getSelection()+"");
			}
		});
		btn_lianjielvdi.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btn_lianjielvdi.setText("连接绿地云平台");
		btn_lianjielvdi.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.上传数据到绿地平台)));
		new Label(grpApp, SWT.NONE);
		new Label(grpApp, SWT.NONE);

		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("双摄像头设置");

		Group group_4 = new Group(tabFolder, SWT.NONE);
		tabItem_1.setControl(group_4);
		group_4.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		group_4.setLayout(new GridLayout(3, false));
		group_4.setText("双摄像头设置");

		Label label_2 = new Label(group_4, SWT.NONE);
		label_2.setText("双摄像头等待间隔");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		text_1 = new Text(group_4, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_1.widthHint = 80;
		text_1.setLayoutData(gd_text_1);

		text_1.setToolTipText("0表示无双摄像头");
		text_1.setText(mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔));
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Label label_3 = new Label(group_4, SWT.NONE);
		label_3.setText("毫秒");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Label label_15 = new Label(group_4, SWT.NONE);
		label_15.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_15.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_15.setText("双摄像头忽略间隔");

		text_8 = new Text(group_4, SWT.BORDER);
		text_8.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		GridData gd_text_8 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_8.widthHint = 80;
		text_8.setLayoutData(gd_text_8);
		text_8.setText(mapSystemSetting.get(SystemSettingTypeEnum.双摄像头忽略间隔));
		text_8.addKeyListener(new KeyAdapter() {
			String s = text_8.getText();

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					String text2 = text_8.getText();
					if (!StrUtil.isEmpty(text2)) {
						Integer.valueOf(text2);
						s = text2;
						mapSystemSetting.put(SystemSettingTypeEnum.双摄像头忽略间隔, text2);
					}
				} catch (NumberFormatException e1) {
					text_8.setText(s);
				}
			}
		});

		Label label_16 = new Label(group_4, SWT.NONE);
		label_16.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_16.setText("毫秒");

		TabItem tabItem_3 = new TabItem(tabFolder, SWT.NONE);
		tabItem_3.setText("集中收费设置");

		Group group_2 = new Group(tabFolder, SWT.NONE);
		tabItem_3.setControl(group_2);
		group_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		group_2.setText("集中收费设置");
		group_2.setLayout(new GridLayout(3, false));

		Button btnCheckButton = new Button(group_2, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.启用集中收费, btnCheckButton.getSelection() + "");
				text_7.setEditable(btnCheckButton.getSelection());
			}
		});
		btnCheckButton.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnCheckButton.setText("启用集中收费");
		btnCheckButton.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用集中收费)));
		new Label(group_2, SWT.NONE);
		new Label(group_2, SWT.NONE);

		Label label_4 = new Label(group_2, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_4.setText("允许延迟出场时间");

		text_7 = new Text(group_2, SWT.BORDER);
		text_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_7 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_7.widthHint = 100;
		text_7.setLayoutData(gd_text_7);
		text_7.setText(mapSystemSetting.get(SystemSettingTypeEnum.集中收费延迟出场时间));
		text_7.setEditable(btnCheckButton.getSelection());
		text_7.addKeyListener(new KeyAdapter() {
			String s = text_7.getText();

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					String text2 = text_7.getText();
					if (!StrUtil.isEmpty(text2)) {
						Integer.valueOf(text2);
						s = text2;
						mapSystemSetting.put(SystemSettingTypeEnum.集中收费延迟出场时间, text2);
					}
				} catch (NumberFormatException e1) {
					text_7.setText(s);
				}
			}

		});

		Label label_5 = new Label(group_2, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setText("分");
		
		text_1.addKeyListener(new KeyAdapter() {
			String s = text_1.getText();

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					String text2 = text_1.getText();
					if (!StrUtil.isEmpty(text2)) {
						Integer.valueOf(text2);
						s = text2;
						mapSystemSetting.put(SystemSettingTypeEnum.双摄像头识别间隔, text2);
					}
				} catch (NumberFormatException e1) {
					text_1.setText(s);
				}
			}
		});

		Composite composite_8 = new Composite(composite, SWT.NONE);
		composite_8.setLayout(new GridLayout(4, false));

		Button button_20 = new Button(composite_8, SWT.NONE);
		button_20.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().setHoliday();
			}
		});
		button_20.setText("节假日设置");
		button_20.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Button button_21 = new Button(composite_8, SWT.NONE);
		button_21.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().clearAllHistory();
			}
		});
		button_21.setToolTipText("清除进出场记录，清除充值、归账记录");
		button_21.setText("清除记录");
		button_21.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Button button_22 = new Button(composite_8, SWT.NONE);
		button_22.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().cleanCarWithIn();
			}
		});
		button_22.setText("清理场内车");
		button_22.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Button button_25 = new Button(composite_8, SWT.NONE);
		button_25.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().downloadPlate();
			}
		});
		button_25.setToolTipText("将固定车的车牌下载到设备");
		button_25.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_25.setText("车牌下载");
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		listComposite = new Composite(sashForm, SWT.NONE);
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm.setWeights(new int[] { 447, 164 });
		scrolledComposite.getVerticalBar().setIncrement(20);
	}

	public SettingView(Composite c, int style, Map<SystemSettingTypeEnum, String> mapSystemSetting) {
		super(c, style);
		this.mapSystemSetting = mapSystemSetting;
		createView();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public SettingPresenter getPresenter() {
		return (SettingPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
}
