package com.donglu.carpark.ui.monitor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.DeviceVoiceTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDeviceVoice;
import com.dongluhitec.card.ui.util.FileUtils;
import com.dongluhitec.card.ui.util.WidgetUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.wb.swt.SWTResourceManager;

public class MonitorSettingApp {
	private CarparkMainModel model;
	private CommonUIFacility commonui;
	private CarparkDatabaseServiceProvider sp;
	
	

	protected Shell shell;
	private Text text;
	private Text text_1;

	
	public MonitorSettingApp(CarparkMainModel model, CommonUIFacility commonui, CarparkDatabaseServiceProvider sp) {
		this.model = model;
		this.commonui = commonui;
		this.sp = sp;
	}




	/**
	 * @wbp.parser.entryPoint
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

	protected void createContents() {
		shell = new Shell();
		shell.setSize(699, 612);
		shell.setText("参数设置");
		shell.setLayout(new GridLayout(1, false));
		WidgetUtil.center(shell);
		
		Group group = new Group(shell, SWT.NONE);
		group.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setText("参数设置");
		
		Composite composite = new Composite(group, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite.widthHint = 453;
		composite.setLayoutData(gd_composite);
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label.setText("监控端图片存放位置：");
		
		text = new Text(composite, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text.setEnabled(false);
		text.setText(model.getClientImageSavePath());
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.widthHint = 246;
		text.setLayoutData(gd_text);
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeClientImageSavePath();
			}
		});
		btnNewButton.setText("选择文件夹");
		
		Group group_1 = new Group(shell, SWT.NONE);
		group_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		group_1.setLayout(new GridLayout(1, false));
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group_1.setText("语音设置");
		
		Composite composite_1 = new Composite(group_1, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_1.setLayout(new GridLayout(3, false));
		
		ComboViewer comboViewer = new ComboViewer(composite_1, SWT.READ_ONLY);
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection s=(StructuredSelection) event.getSelection();
				DeviceVoiceTypeEnum dv=(DeviceVoiceTypeEnum) s.getFirstElement();
				System.out.println(dv);
				if(dv==null){
					return;
				}
				SingleCarparkDeviceVoice voice = model.getMapVoice().get(dv);
				if(text_1==null){
					return;
				}
				text_1.setText(voice.getContent());
			}
		});
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setInput(DeviceVoiceTypeEnum.values());
		comboViewer.setSelection(new StructuredSelection(DeviceVoiceTypeEnum.values()[0]));
		Combo combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 180;
		combo.setLayoutData(gd_combo);
		
		text_1 = new Text(composite_1, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_1.setText(model.getMapVoice().get(DeviceVoiceTypeEnum.values()[0]).getContent());
		
		Button button = new Button(composite_1, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					StructuredSelection s = (StructuredSelection) comboViewer.getSelection();
					DeviceVoiceTypeEnum dv = (DeviceVoiceTypeEnum) s.getFirstElement();
					SingleCarparkDeviceVoice voice = model.getMapVoice().get(dv);
					voice.setContent(text_1.getText());
					sp.getCarparkService().saveDeviceVoice(Arrays.asList(voice));
					commonui.info("提示", "保存成功！");
				} catch (Exception e2) {
					// TODO: 有异常？
				}
			}
		});
		button.setText("保存语音");
		
		initScreen();
	}
	
	private void initScreen(){
		Map<String, String> mapMonitorConfig=(Map<String, String>) CarparkFileUtils.readObject("mapMonitorConfig");
		if(mapMonitorConfig==null){
			return;
		}
	}



	/**
	 * 
	 */
	public void focus() {
		shell.setFocus();
	}
	/**
	 * 修改客户端图片存放位置
	 */
	public void changeClientImageSavePath() {
		try {
			DirectoryDialog dd=new DirectoryDialog(shell);
			String open = dd.open();
			if(open==null){
				return;
			}
			String newPath="";
			newPath=open+"\\img";
			newPath=newPath.replace("\\\\", "\\");
			if(newPath.equals(text.getText())){
				return;
			}
			boolean confirm = commonui.confirm("提示", "确认将图片保存路径:["+text.getText()+"]改为:["+newPath+"]吗？");
			if (!confirm) {
				return;
			}
			model.setClientImageSavePath(open);
			FileUtils.writeObject(ConstUtil.CLIENT_IMAGE_SAVE_FILE_PATH, open);
			text.setText(newPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
