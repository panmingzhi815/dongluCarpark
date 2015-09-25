package com.donglu.carpark;

import java.awt.Canvas;
import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import com.donglu.carpark.wizard.AddDeviceModel;
import com.donglu.carpark.wizard.AddDeviceWizard;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.exception.DongluAppException;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.device.WebCameraDevice;
import com.dongluhitec.card.hardware.xinluwei.XinlutongCallback.XinlutongResult;
import com.dongluhitec.card.hardware.xinluwei.XinlutongJNA;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class CarparkMainApp {
	
	private Logger LOGGER = LoggerFactory.getLogger(CarparkMainApp.class);

	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text txtPanmingzhi;
	private Text text_4;
	private Text text_5;
	private Text text_6;
	private Text text_3;
	private Text txta;
	private Text text_8;
	private Text text_9;
	private Text text_10;
	private Text text_11;
	private Text text_12;
	private Text text_13;
	private Text txtinplateNo;
	private Text text_in_time;
	private Text txtoutplateNo;
	private Text text_out_time;
	
	@Inject
	private WebCameraDevice webCameraDevice;
	
	@Inject
	private XinlutongJNA xinlutongJNA;
	@Inject
	private CommonUIFacility commonui;
	@Inject
	private CarparkMainPresenter presenter;
	private Composite northCamera;
	private Composite southCamera;
	private CLabel inbigimg;
	private CLabel insmallimg;
	private CLabel outsmallImage;
	private CLabel outbigImage;
	
	
	AtomicInteger plateNoTotal=new AtomicInteger(0);

	private EmbeddedMediaPlayer createPlayLeft;

	private EmbeddedMediaPlayer createPlayRight;
	
	//保存设备的进出口信息
	Map<String, String> mapDeviceType=Maps.newHashMap();

	private CTabFolder tabInFolder;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CarparkMainApp window = new CarparkMainApp();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public CarparkMainApp() {
		
	}
	/**
	 * Open the window.
	 */
	public void open() {
		String property = System.getProperty("userType");
		if (StrUtil.isEmpty(property)) {
			System.exit(0);
		}
		Display display = Display.getDefault();
		createContents();
		shell.setMaximized(true);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		commonui.confirm("提示", "车牌抓拍数："+plateNoTotal.intValue(),new Shell());
		System.exit(0);
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setMinimumSize(new Point(1024, 768));
		shell.setSize(450, 300);
		shell.setText("停车场监控-1.0.0.1");
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.verticalSpacing = 2;
		gl_shell.marginWidth = 2;
		gl_shell.marginHeight = 2;
		gl_shell.horizontalSpacing = 2;
		shell.setLayout(gl_shell);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite.verticalIndent = 5;
		composite.setLayoutData(gd_composite);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		FillLayout fl_composite_1 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_1.spacing = 5;
		composite_1.setLayout(fl_composite_1);
		
		tabInFolder = new CTabFolder(composite_1, SWT.BORDER | SWT.FLAT);
		tabInFolder.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.BOLD));
		Composite control = new Composite(tabInFolder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		ToolBar toolBar = new ToolBar(control,SWT.NONE);
		ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
		toolItem.setText("拍照");
		ToolItem toolItem2 = new ToolItem(toolBar, SWT.NONE);
		toolItem2.setText("抬杆");
		ToolItem addInToolItem = new ToolItem(toolBar, SWT.NONE);
		addInToolItem.setText("添加");
		addInToolItem.setToolTipText("添加进口设备");
		
		ToolItem editInToolItem = new ToolItem(toolBar, SWT.NONE);
		editInToolItem.setText("修改");
		editInToolItem.setToolTipText("修改进口设备");
		editInToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem selection = tabInFolder.getSelection();
				String text2 = selection.getText();
				AddDeviceModel model=new AddDeviceModel();
				model.setName(text2);
				AddDeviceWizard v=new AddDeviceWizard(model);
				AddDeviceModel showWizard = (AddDeviceModel) commonui.showWizard(v);
				selection.setText(showWizard.getName());
			}
		});
		
		
		ToolItem delInToolItem = new ToolItem(toolBar, SWT.NONE);
		delInToolItem.setText("删除");
		delInToolItem.setToolTipText("删除进口设备");
		addInToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				CTabItem tabItem = new CTabItem(tabInFolder, SWT.NONE);
				tabItem.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
				tabItem.setText("dsad");
				Composite composite = new Composite(tabInFolder, SWT.BORDER|SWT.EMBEDDED);
				tabItem.setControl(composite);
				composite.setLayout(new FillLayout());
				createLeftCamera("192.168.1.138", composite);
			}
		});
		delInToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean confirm = commonui.confirm("确定提示", "确定删除所选设备");
				if (confirm) {
					tabInFolder.getSelection().dispose();
				}
			}
		});

		tabInFolder.setTopRight(control);
		tabInFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tabItem = new CTabItem(tabInFolder, SWT.NONE);
		tabItem.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
		tabItem.setText("南门入场视频");
		
		southCamera = new Composite(tabInFolder, SWT.BORDER|SWT.EMBEDDED);
		tabItem.setControl(southCamera);
		southCamera.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		CTabFolder tabFolder_1 = new CTabFolder(composite_1, SWT.BORDER | SWT.FLAT);
		Composite control2 = new Composite(tabFolder_1, SWT.NONE);
		GridLayout layout2 = new GridLayout();
		layout2.marginHeight = 0;
		layout2.marginWidth = 0;
		control2.setLayout(layout2);
		ToolBar toolBar2 = new ToolBar(control2,SWT.NONE);
		ToolItem toolItem3 = new ToolItem(toolBar2, SWT.NONE);
		toolItem3.setText("拍照");
		ToolItem toolItem4 = new ToolItem(toolBar2, SWT.NONE);
		toolItem4.setText("抬杆");

		tabFolder_1.setTopRight(control2);
		tabFolder_1.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.BOLD));
		tabFolder_1.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tabItem_2 = new CTabItem(tabFolder_1, SWT.NONE);
		tabItem_2.setText("南门出场视频");
		
		northCamera = new Composite(tabFolder_1, SWT.BORDER|SWT.EMBEDDED);
		tabItem_2.setControl(northCamera);
		northCamera.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new GridLayout(1, false));
		
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite_5 = new Composite(composite_3, SWT.NONE);
		composite_5.setLayout(new GridLayout(2, false));
		
		Composite composite_9 = new Composite(composite_5, SWT.NONE);
		composite_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		composite_9.setLayout(new GridLayout(2, false));
		composite_9.setBounds(0, 0, 64, 64);
		
		Label lblNewLabel_3 = new Label(composite_9, SWT.NONE);
		lblNewLabel_3.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setBounds(0, 0, 61, 17);
		lblNewLabel_3.setText("车牌号码");
		
		txtinplateNo = new Text(composite_9, SWT.BORDER);
		txtinplateNo.setEditable(false);
		txtinplateNo.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		txtinplateNo.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		txtinplateNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_4 = new Label(composite_9, SWT.NONE);
		lblNewLabel_4.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		lblNewLabel_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setText("入场时间");
		
		text_in_time = new Text(composite_9, SWT.BORDER);
		text_in_time.setEditable(false);
		text_in_time.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_in_time.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_in_time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite_10 = new Composite(composite_5, SWT.BORDER);
		GridData gd_composite_10 = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_composite_10.widthHint = 120;
		composite_10.setLayoutData(gd_composite_10);
		composite_10.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_10.setBounds(0, 0, 64, 64);
		
		insmallimg = new CLabel(composite_10, SWT.NONE);
		insmallimg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		insmallimg.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.BOLD));
		insmallimg.setAlignment(SWT.CENTER);
		insmallimg.setText("入场车牌");
		
		Composite composite_6 = new Composite(composite_3, SWT.NONE);
		composite_6.setLayout(new GridLayout(2, false));
		
		Composite composite_12 = new Composite(composite_6, SWT.NONE);
		composite_12.setLayout(new GridLayout(2, false));
		composite_12.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Label label_15 = new Label(composite_12, SWT.NONE);
		label_15.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_15.setText("车牌号码");
		label_15.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		
		txtoutplateNo = new Text(composite_12, SWT.BORDER);
		txtoutplateNo.setEditable(false);
		txtoutplateNo.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		txtoutplateNo.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		txtoutplateNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_16 = new Label(composite_12, SWT.NONE);
		label_16.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_16.setText("出场时间");
		label_16.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		
		text_out_time = new Text(composite_12, SWT.BORDER);
		text_out_time.setEditable(false);
		text_out_time.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_out_time.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_out_time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite composite_11 = new Composite(composite_6, SWT.BORDER);
		composite_11.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_composite_11 = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_composite_11.widthHint = 120;
		composite_11.setLayoutData(gd_composite_11);
		
		outsmallImage = new CLabel(composite_11, SWT.NONE);
		outsmallImage.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		outsmallImage.setAlignment(SWT.CENTER);
		outsmallImage.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.BOLD));
		outsmallImage.setText("出场车牌");
		
		Composite composite_4 = new Composite(composite_2, SWT.NONE);
		FillLayout fl_composite_4 = new FillLayout(SWT.HORIZONTAL);
		fl_composite_4.spacing = 6;
		composite_4.setLayout(fl_composite_4);
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite_7 = new Composite(composite_4, SWT.BORDER);
		composite_7.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		inbigimg = new CLabel(composite_7, SWT.NONE);
		inbigimg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		inbigimg.setFont(SWTResourceManager.getFont("微软雅黑", 23, SWT.BOLD));
		inbigimg.setAlignment(SWT.CENTER);
		inbigimg.setText("入场车牌");
		
		Composite composite_8 = new Composite(composite_4, SWT.BORDER);
		composite_8.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		outbigImage = new CLabel(composite_8, SWT.NONE);
		outbigImage.setText("入场车牌");
		outbigImage.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		outbigImage.setFont(SWTResourceManager.getFont("微软雅黑", 23, SWT.BOLD));
		outbigImage.setAlignment(SWT.CENTER);
		
		Group group = new Group(shell, SWT.SHADOW_IN);
		group.setFont(SWTResourceManager.getFont("微软雅黑", 5, SWT.NORMAL));
		GridLayout gl_group = new GridLayout(2, false);
		gl_group.verticalSpacing = 8;
		group.setLayout(gl_group);
		GridData gd_group = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
		gd_group.widthHint = 250;
		group.setLayoutData(gd_group);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("剩余车位数");
		
		text = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		text.setEditable(false);
		text.setText("1000");
		text.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("时租车位数");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_1 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_1.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_1.setText("1000");
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_1.setEditable(false);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("月租车位数");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_2 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_2.setText("1000");
		text_2.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_2.setEditable(false);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("当前值班");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		txtPanmingzhi = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		txtPanmingzhi.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		txtPanmingzhi.setText("panmingzhi");
		txtPanmingzhi.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		txtPanmingzhi.setEditable(false);
		txtPanmingzhi.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("上班时间");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_4 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_4.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_4.setText("2015-8-15 12:30:20");
		text_4.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_4.setEditable(false);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("收费金额");
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_5 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		text_5.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_5.setText("1000");
		text_5.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_5.setEditable(false);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_5 = new Label(group, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("免费金额");
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_6 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_6.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		text_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		text_6.setText("1000");
		text_6.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_6.setEditable(false);
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_1 = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lblNewLabel_1.setText("New Label");
		
		Label label_6 = new Label(group, SWT.NONE);
		label_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_6.setText("车牌号码");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		txta = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		txta.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		txta.setText("京A23456");
		txta.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		txta.setEditable(false);
		txta.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_7 = new Label(group, SWT.NONE);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("用户名称");
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_3 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_3.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_3.setText("李大钊");
		text_3.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_3.setEditable(false);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_8 = new Label(group, SWT.NONE);
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("用户类型");
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_8 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_8.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_8.setText("月卡");
		text_8.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_8.setEditable(false);
		text_8.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_9 = new Label(group, SWT.NONE);
		label_9.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_9.setText("入场时间");
		label_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_9 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_9.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_9.setText("2015-8-15 12:30:20");
		text_9.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_9.setEditable(false);
		text_9.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_10 = new Label(group, SWT.NONE);
		label_10.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_10.setText("出场时间");
		label_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_10 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_10.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_10.setText("2015-8-15 14:50:20");
		text_10.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_10.setEditable(false);
		text_10.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_11 = new Label(group, SWT.NONE);
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_11.setText("停车时间");
		label_11.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_11 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_11.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_11.setText("2:20:00");
		text_11.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_11.setEditable(false);
		text_11.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_12 = new Label(group, SWT.NONE);
		label_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_12.setText("应收金额");
		label_12.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_12 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_12.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_12.setText("20.0");
		text_12.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_12.setEditable(false);
		text_12.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_13 = new Label(group, SWT.NONE);
		label_13.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_13.setText("实收金额");
		label_13.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.BOLD));
		
		text_13 = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		text_13.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_13.setText("20.0");
		text_13.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_13.setEditable(false);
		text_13.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		GridData gd_btnNewButton = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		gd_btnNewButton.widthHint = 120;
		btnNewButton.setLayoutData(gd_btnNewButton);
		btnNewButton.setText("收费放行(F11)");
		
		Button btnf = new Button(group, SWT.NONE);
		btnf.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		GridData gd_btnf = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		gd_btnf.widthHint = 120;
		btnf.setLayoutData(gd_btnf);
		btnf.setText("免费放行(F12)");
		
		Button btnf_1 = new Button(group, SWT.NONE);
		btnf_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createPlayLeft.release();
				createLeftCamera("192.168.1.139",southCamera);
			}
		});
		btnf_1.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		GridData gd_btnf_1 = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		gd_btnf_1.widthHint = 120;
		btnf_1.setLayoutData(gd_btnf_1);
		btnf_1.setText("换班(F7)");
		
		Button btnf_2 = new Button(group, SWT.NONE);
		btnf_2.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		GridData gd_btnf_2 = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		gd_btnf_2.widthHint = 120;
		btnf_2.setLayoutData(gd_btnf_2);
		btnf_2.setText("归账(F8)");
		
		Button btnf_3 = new Button(group, SWT.NONE);
		btnf_3.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		GridData gd_btnf_3 = new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1);
		gd_btnf_3.widthHint = 120;
		btnf_3.setLayoutData(gd_btnf_3);
		btnf_3.setText("浏览记录(F9)");
		new Label(group, SWT.NONE);
		new Label(group, SWT.NONE);
		addCamera();
		addXinlutongJNA();
		tabInFolder.setSelection(0);
		tabFolder_1.setSelection(0);
	}

	private void addXinlutongJNA() {
		XinlutongResult xinlutongResult = new XinlutongResult() {
			public void invok(final String ip, int channel,final String plateNO,final byte[] bigImage, final byte[] smallImage) {
				Date date = new Date();
				String folder=StrUtil.formatDate(date, "yyyyMMddHH");
				String fileName=StrUtil.formatDate(date, "yyyyMMddHHmmssSSS");
				saveImage(folder,fileName+"_"+plateNO+"_big.jpg",bigImage);
				saveImage(folder,fileName+"_"+plateNO+"_small.jpg",smallImage);
				final String dateString=StrUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
				if (ip.equals("192.168.1.139")) {
					new Thread(new Runnable() {
						public void run() {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									if(outsmallImage.getBackgroundImage() != null){
										LOGGER.info(dateString+ip+"小图片销毁图片");
										outsmallImage.getBackgroundImage().dispose();
									}
									if(outbigImage.getBackgroundImage() != null){
										LOGGER.info(dateString+ip+"大图片销毁图片");
										outbigImage.getBackgroundImage().dispose();
									}
									Image newImg = getImage(smallImage,insmallimg,shell);
									outsmallImage.setText("");
									outsmallImage.setBackgroundImage(newImg);
									Image bigImg = getImage(bigImage,inbigimg,shell);
									outbigImage.setText("");
									outbigImage.setBackgroundImage(bigImg);
									txtoutplateNo.setText(plateNO);
									text_out_time.setText(dateString);
									plateNoTotal.addAndGet(1);
								}
							});
						}
						}).start();
				}else if(ip.equals("192.168.1.138")){
					new Thread(new Runnable() {
					public void run() {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								if(insmallimg.getBackgroundImage() != null){
									LOGGER.info(dateString+ip+"小图片销毁图片");
									insmallimg.getBackgroundImage().dispose();
								}
								if(inbigimg.getImage() != null){
									LOGGER.info(dateString+ip+"大图片销毁图片");
									inbigimg.getBackgroundImage().dispose();
								}
								Image newImg = getImage(smallImage,insmallimg,shell);
								insmallimg.setText("");
								insmallimg.setBackgroundImage(newImg);
								Image bigImg = getImage(bigImage,inbigimg,shell);
								inbigimg.setText("");
								inbigimg.setBackgroundImage(bigImg);
								txtinplateNo.setText(plateNO);
								text_in_time.setText(dateString);
								plateNoTotal.addAndGet(1);
							}
						});
					}
					}).start();
				}
				
			}
		};
		xinlutongJNA.openEx("192.168.1.138", xinlutongResult);
		xinlutongJNA.openEx("192.168.1.139", xinlutongResult);
	}

	protected void saveImage(String f, String fileName, byte[] bigImage) {
		String fl="img/"+f;
		try {
			File file = new File(fl);
			if (!file.exists()&&!file.isDirectory()) {
				file.mkdir();
			}
			Files.write(bigImage, new File(fl+"/"+fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void addCamera() {
		createLeftCamera("192.168.1.138",southCamera);
		
		createRightCamera("192.168.1.139",northCamera);
	}

	/**
	 * @param ip 
	 * @param northCamera 
	 * 
	 */
	public void createRightCamera(String ip, Composite northCamera) {
		Frame new_Frame1 = SWT_AWT.new_Frame(northCamera);
		Canvas canvas1 = new Canvas();
        new_Frame1.add(canvas1);
        new_Frame1.pack();
        new_Frame1.setVisible(true);
        final String url="rtsp://"+ip+":554/h264ESVideoTest";
		createPlayRight = webCameraDevice.createPlay(new_Frame1, url);
		createPlayRight.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				new Runnable() {
					public void run() {
						while (!mediaPlayer.isPlaying()) {
							LOGGER.info("设备连接{}已断开",url);
							mediaPlayer.release();
							mediaPlayer.playMedia(url);
							Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
						}
					}
				}.run();
			}
		});
		
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				createPlayRight.release();
			}
		});
	}

	/**
	 * @param ip 
	 * @param southCamera 
	 * 
	 */
	public void createLeftCamera(String ip, Composite southCamera) {
		Frame new_Frame = SWT_AWT.new_Frame(southCamera);
		Canvas canvas = new Canvas();
        new_Frame.add(canvas);
        new_Frame.pack();
        new_Frame.setVisible(true);
        final String url="rtsp://"+ip+":554/h264ESVideoTest";
		createPlayLeft = webCameraDevice.createPlay(new_Frame, url);
		createPlayLeft.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(final MediaPlayer mediaPlayer) {
				new Runnable() {
					public void run() {
						while (!mediaPlayer.isPlaying()) {
							LOGGER.info("设备连接{}已断开",url);
							mediaPlayer.release();
							mediaPlayer.playMedia(url);
							Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);
							System.out.println("mediaPlayer.isPlaying()=="+mediaPlayer.isPlaying());
						}
					}
				}.run();
			}
			@Override
			public void error(MediaPlayer mediaPlayer) {
				
			}
		});
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				createPlayLeft.release();
			}
		});
	}

	
	public Image getImage(final byte[] smallImage, CLabel insmallimg, Shell shell) {
		try(ByteArrayInputStream stream = new ByteArrayInputStream(smallImage)) {
			Image img = new Image(shell.getDisplay(), stream);
			Rectangle rectangle = insmallimg.getBounds();
			ImageData id = img.getImageData().scaledTo(rectangle.width, rectangle.height);
			Image newImg = new Image(shell.getDisplay(), id);
			img.dispose();
			LOGGER.info("getImage dispose");
			return newImg;
		} catch (Exception e) {
			throw new DongluAppException("图片转换错误", e);
		}
	}
}
