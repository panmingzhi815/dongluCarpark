package com.donglu.carpark.ui.view.setting;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.donglu.carpark.util.InjectorUtil;
import com.donglu.carpark.util.aliyun.AliyunSmsUtil;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SmsSettingComposite extends Composite {

	private CommonUIFacility commonui;
	private Map<SystemSettingTypeEnum, String> map;

	private Text txt_accessKeyId;
	private Text txt_accessKeySecret;
	private Text txt_signName;
	private Text txt_temp;
	private Button btn_isStart;

	public SmsSettingComposite(Composite parent, int style, Map<SystemSettingTypeEnum, String> map) {
		this(parent, style);
		this.commonui = InjectorUtil.getInstance(CommonUIFacility.class);
		this.map = map;
		btn_isStart.setSelection(Boolean.valueOf(map.get(SystemSettingTypeEnum.启动短信发送服务)));
		txt_accessKeyId.setText(map.get(SystemSettingTypeEnum.短信服务appid));
		txt_accessKeySecret.setText(map.get(SystemSettingTypeEnum.短信服务appsecret));
		txt_signName.setText(map.get(SystemSettingTypeEnum.短信签名));
		txt_temp.setText(map.get(SystemSettingTypeEnum.短信模板));
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SmsSettingComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		new Label(this, SWT.NONE);

		btn_isStart = new Button(this, SWT.CHECK);
		btn_isStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				map.put(SystemSettingTypeEnum.启动短信发送服务, btn_isStart.getSelection() + "");
			}
		});
		btn_isStart.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btn_isStart.setText("启用短信服务");

		Label lblAppid = new Label(this, SWT.NONE);
		lblAppid.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblAppid.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAppid.setText("AccessKeyId");

		txt_accessKeyId = new Text(this, SWT.BORDER);
		txt_accessKeyId.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 300;
		txt_accessKeyId.setLayoutData(gd_text);
		txt_accessKeyId.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				map.put(SystemSettingTypeEnum.短信服务appid, txt_accessKeyId.getText());
			}
		});

		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("AccessKeySecret");

		txt_accessKeySecret = new Text(this, SWT.BORDER);
		txt_accessKeySecret.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_accessKeySecret.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txt_accessKeySecret.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				map.put(SystemSettingTypeEnum.短信服务appsecret, txt_accessKeySecret.getText());
			}
		});

		Label label = new Label(this, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("短信签名");

		txt_signName = new Text(this, SWT.BORDER);
		txt_signName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_signName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		txt_signName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				map.put(SystemSettingTypeEnum.短信签名, txt_signName.getText());
			}
		});

		Label label_1 = new Label(this, SWT.NONE);
		label_1.setVisible(false);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("短信模板");

		txt_temp = new Text(this, SWT.BORDER);
		txt_temp.setVisible(false);
		txt_temp.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		txt_temp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txt_temp.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				map.put(SystemSettingTypeEnum.短信模板, txt_temp.getText());
			}
		});

		new Label(this, SWT.NONE);

		Button button_1 = new Button(this, SWT.NONE);
		button_1.setVisible(false);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 你的accessKeyId
				final String accessKeyId = "LTAIXZ4qJGagzqIv";
				// 你的accessKeySecret
				final String accessKeySecret = "90poU2cJhvavnoUAnkfvGExNZYBBgS";
				String signName = "东云智联";
				String templateCode = "SMS_150735052";
				JSONObject jo = new JSONObject();
				jo.put("code", 12345);

				try {
					String sendSms = AliyunSmsUtil.sendSms(accessKeyId, accessKeySecret, signName, templateCode, "13537630413", jo.toJSONString());
					JSONObject result = JSON.parseObject(sendSms);
					if ("OK".equals(result.getString("Code"))) {
						commonui.info("提示", "发送成功");
						return;
					}
					commonui.info("提示", "发送失败," + result.getString("Message"));
				} catch (Exception e1) {
					e1.printStackTrace();
					commonui.info("提示", "发送失败," + e1.getMessage());
				}
			}
		});
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.setText("测试");

	}

	@Override
	protected void checkSubclass() {
	}
}
