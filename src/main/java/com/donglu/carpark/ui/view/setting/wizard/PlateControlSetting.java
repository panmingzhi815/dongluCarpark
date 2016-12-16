package com.donglu.carpark.ui.view.setting.wizard;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.util.CarparkUtils;
import com.donglu.carpark.util.TextUtils;
import com.dongluhitec.card.blservice.DatabaseServiceProvider;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.List;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class PlateControlSetting {

	protected Shell shell;
	private CarparkDatabaseServiceProvider sp;
	private Text txt_plate;
	private ListViewer listViewer;
	java.util.List<String> list = Arrays.asList("陕G12345","陕G12345");
	private Button btn_dan;
	private Button btn_duo;
	private CommonUIFacility commonui;
	
	public PlateControlSetting(CarparkDatabaseServiceProvider sp,CommonUIFacility commonui) {
		this.sp = sp;
		this.commonui = commonui;
	}
	public PlateControlSetting() {
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			PlateControlSetting window = new PlateControlSetting();
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
		shell = new Shell();
		shell.setSize(543, 440);
		shell.setText("车牌控制设置");
		shell.setLayout(new GridLayout(1, false));
		
		Group group = new Group(shell, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		group.setText("等待进入车牌");
		
		Composite composite_1 = new Composite(group, SWT.NONE);
		composite_1.setLayout(new GridLayout(4, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		txt_plate = new Text(composite_1, SWT.BORDER);
		GridData gd_txtbbbbbb = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtbbbbbb.widthHint = 100;
		txt_plate.setLayoutData(gd_txtbbbbbb);
		TextUtils.createPlateNOAutoCompleteField(txt_plate);
		
		Button button_2 = new Button(composite_1, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = txt_plate.getText();
				if (StrUtil.isEmpty(text)) {
					return;
				}
				boolean confirm = commonui.confirm("提示", "确认将车牌["+text+"]添加到等待放行列表中？");
				if (!confirm) {
					return;
				}
				sp.getCarparkService().addWillInPlate(text);
				init();
			}
		});
		button_2.setText("添加");
		
		Button button_3 = new Button(composite_1, SWT.NONE);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StructuredSelection selection = (StructuredSelection) listViewer.getSelection();
				java.util.List<String> list2 = selection.toList();
				boolean confirm = commonui.confirm("提示", "确认删除选中的"+list2.size()+"个等待进入车牌");
				if (!confirm) {
					return;
				}
				for (String string : list2) {
					sp.getCarparkService().deleteWillInPlate(string);
				}
				init();
			}
		});
		button_3.setText("删除");
		
		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save();
			}
		});
		button.setText("保存");
		
		Composite composite_2 = new Composite(group, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		listViewer = new ListViewer(composite_2, SWT.BORDER | SWT.V_SCROLL);
		listViewer.setContentProvider(new ArrayContentProvider());
		listViewer.setLabelProvider(new LabelProvider());
		
		List list = listViewer.getList();
		
		Group group_1 = new Group(shell, SWT.NONE);
		group_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		group_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group_1.setText("限行设置");
		
		Composite composite = new Composite(group_1, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		
		btn_dan = new Button(composite, SWT.RADIO);
		btn_dan.setText("单号限行");
		
		btn_duo = new Button(composite, SWT.RADIO);
		btn_duo.setText("双号限行");
		init();
	}

	protected void save() {
		sp.getCarparkService().setPlateControlStatus(btn_dan.getSelection()?true:false);
	}
	private void init() {
		// true 为单
		boolean controlSetting = getControlSetting();
		btn_dan.setSelection(controlSetting);
		btn_duo.setSelection(!controlSetting);
		list=sp.getCarparkService().getWillInPlate();
		if (list==null) {
			list=new ArrayList<>();
		}
		listViewer.setInput(list);
	}
	/**
	 * 
	 */
	public boolean getControlSetting() {
		Map<Boolean, Date> map = sp.getCarparkService().getPlateControlStatus();
		if (map==null) {
			return true;
		}else{
			Date date = map.get(true);
			Date nowTime = new Date();
			if (date!=null) {
				if (StrUtil.formatDate(date).equals(StrUtil.formatDate(nowTime))) {
					return true;
				}
				int day = CarparkUtils.countTime(StrUtil.getTodayTopTime(date), StrUtil.getTodayBottomTime(nowTime), TimeUnit.DAYS);
				if (day%2>0) {
					return false;
				}else{
					return true;
				}
			}
			date = map.get(false);
			if (date!=null) {
				if (StrUtil.formatDate(date).equals(StrUtil.formatDate(nowTime))) {
					return false;
				}
				int day = CarparkUtils.countTime(StrUtil.getTodayTopTime(date), StrUtil.getTodayBottomTime(nowTime), TimeUnit.DAYS);
				if (day%2>0) {
					return true;
				}else{
					return false;
				}
			}
		}
		return true;
	}
}
