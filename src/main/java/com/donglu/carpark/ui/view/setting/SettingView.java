package com.donglu.carpark.ui.view.setting;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.CarparkClientConfig;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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

		ScrolledComposite scrolledComposite = new ScrolledComposite(sashForm, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Group group = new Group(composite, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setText("停车场基本设置");
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		group.setBounds(0, 0, 675, 539);
		group.setLayout(new GridLayout(3, false));

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

		Button button_3 = new Button(group, SWT.CHECK);
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

		Button button_4 = new Button(group, SWT.CHECK);
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

		Button button_6 = new Button(group, SWT.CHECK);
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

		Button button_7 = new Button(group, SWT.CHECK);
		button_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.固定车出场确认, button_7.getSelection() + "");
			}
		});
		button_7.setToolTipText("选中后，固定车出场场需要确认放行");
		button_7.setText("固定车出场是否需要确认");
		button_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_7.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.左下监控)));

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

		Button button_9 = new Button(group, SWT.CHECK);
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
		
		Button button_24 = new Button(group, SWT.CHECK);
		button_24.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.固定车到期变临时车, button_24.getSelection()+"");
			}
		});
		button_24.setToolTipText("选择后固定车到期后作临时车计费，否则到期后不允许进入。默认选中");
		button_24.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_24.setText("固定车到期后作临时车计费");
		button_24.setSelection(Boolean.valueOf(CarparkUtils.getSettingValue(mapSystemSetting, SystemSettingTypeEnum.固定车到期变临时车)));
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);

		Composite composite_2 = new Composite(group, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		composite_2.setLayout(new GridLayout(3, false));

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

		Composite composite_3 = new Composite(group, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		composite_3.setLayout(new GridLayout(3, false));

		Label label_2 = new Label(composite_3, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("双摄像头等待间隔");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		text_1 = new Text(composite_3, SWT.BORDER);

		text_1.setToolTipText("0表示无双摄像头");
		text_1.setText(mapSystemSetting.get(SystemSettingTypeEnum.双摄像头识别间隔));
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text_1.widthHint = 82;
		text_1.setLayoutData(gd_text_1);
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

		Label label_3 = new Label(composite_3, SWT.NONE);
		label_3.setText("毫秒");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		Composite composite_4 = new Composite(group, SWT.NONE);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		composite_4.setLayout(new GridLayout(4, false));

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
				mapSystemSetting.put(SystemSettingTypeEnum.进场允许手动入场, button_13.getSelection() + "");
			}
		});
		button_14.setText("允许手动入场");
		button_14.setSelection(false);
		button_14.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_14.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.进场允许手动入场)));

		Composite composite_5 = new Composite(group, SWT.NONE);
		GridData gd_composite_5 = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
		String dbServerIp=CarparkClientConfig.getInstance().getDbServerIp();
		if (dbServerIp.equals("localhost")||dbServerIp.equals("127.0.0.1")||dbServerIp.equals(StrUtil.getHostIp())) {
			
		}else
		gd_composite_5.exclude = true;
		composite_5.setLayoutData(gd_composite_5);
		composite_5.setLayout(new GridLayout(5, false));

		Label label_6 = new Label(composite_5, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("数据库备份位置");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));

		text_setting_dataBaseSave = new Text(composite_5, SWT.BORDER);
		text_setting_dataBaseSave.setText(mapSystemSetting.get(SystemSettingTypeEnum.数据库备份位置));
		text_setting_dataBaseSave.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_setting_dataBaseSave.setEditable(false);
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_2.widthHint = 239;
		text_setting_dataBaseSave.setLayoutData(gd_text_2);

		Button button_15 = new Button(composite_5, SWT.NONE);
		button_15.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.SINGLE);
				fileDialog.setText("请选择路径");
				String open = fileDialog.open();
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
		String readObject = (String) CarparkFileUtils.readObject(cLIENT_IMAGE_SAVE_FILE_PATH);
		String string = readObject == null ? System.getProperty("user.dir") + "/img" : readObject;
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
		composite_7.setLayout(new GridLayout(2, false));

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
		
		Composite composite_1 = new Composite(group, SWT.NONE);
		composite_1.setLayout(new GridLayout(4, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
		
		Button btnCheckButton_1 = new Button(composite_1, SWT.CHECK);
		btnCheckButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				mapSystemSetting.put(SystemSettingTypeEnum.启用车牌报送, btnCheckButton_1.getSelection()+"");
			}
		});
		btnCheckButton_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnCheckButton_1.setText("启用车牌报送");
		btnCheckButton_1.setSelection(Boolean.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.启用车牌报送)));
		
		Label lblNewLabel_2 = new Label(composite_1, SWT.NONE);
		GridData gd_lblNewLabel_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel_2.widthHint = 90;
		lblNewLabel_2.setLayoutData(gd_lblNewLabel_2);
		
		Label lblNewLabel_3 = new Label(composite_1, SWT.NONE);
		lblNewLabel_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("车位数显示方式");
		
		ComboViewer comboViewer = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("============="+combo.getSelectionIndex());
				mapSystemSetting.put(SystemSettingTypeEnum.车位数显示方式, ""+combo.getSelectionIndex());
			}
		});
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 200;
		combo.setLayoutData(gd_combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[]{"显示临时车剩余车位","显示固定车剩余车位","显示总剩余车位"});
		combo.select(Integer.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.车位数显示方式)));

		Group group_1 = new Group(composite, SWT.NONE);
		group_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		group_1.setLayout(new GridLayout(4, false));
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
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

		Group group_2 = new Group(composite, SWT.NONE);
		group_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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

		Button button_23 = new Button(composite, SWT.NONE);
		button_23.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().saveAll(mapSystemSetting);
			}
		});
		button_23.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		button_23.setText("保存设置");
		button_23.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		button_23.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		listComposite = new Composite(sashForm, SWT.NONE);
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm.setWeights(new int[] { 685, 210 });
	}

	public SettingView(Composite c, int style, Map<SystemSettingTypeEnum, String> mapSystemSetting) {
		super(c, style);
		this.mapSystemSetting=mapSystemSetting;
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
