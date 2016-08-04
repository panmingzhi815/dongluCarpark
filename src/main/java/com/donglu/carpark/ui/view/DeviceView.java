package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.util.concurrent.RateLimiter;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class DeviceView extends Composite implements View{
	private Presenter presenter;
	private CTabFolder tabFolder;
	private RateLimiter rateLimiter = RateLimiter.create(1);
	private ToolItem addToolItem;

	private ToolItem editToolItem;

	private ToolItem delToolItem;
	private ToolItem toolItem_in_openDoor;
	private ToolItem toolItem_in_closeDoor;
	private ToolItem toolItem_in_fleet;
	private ToolItem toolItem_testDevice;

	public DeviceView(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tabFolder = new CTabFolder(this, SWT.BORDER);
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				System.out.println(tabFolder.getSelection());
//				model.setSelectTabSelect(tabFolder.getSelection());
			}
			
		});
		Composite control3 = new Composite(tabFolder, SWT.NONE);
		GridLayout layout3 = new GridLayout(2,false);
		layout3.marginHeight = 0;
		layout3.marginWidth = 0;
		control3.setLayout(layout3);
		Label label = new Label(control3, SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 50;
		gd_label.heightHint = 25;
		label.setLayoutData(gd_label);
		
		ToolBar toolBar3 = new ToolBar(control3, SWT.FLAT | SWT.NONE);
		ToolItem toolItem_in_photograph3 = new ToolItem(toolBar3, SWT.NONE);
		toolItem_in_photograph3.setText("拍照");
		toolItem_in_photograph3.setToolTipText("手动抓拍");
		toolItem_in_photograph3.setSelection(true);
		toolItem_in_photograph3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				CTabItem selection = tabFolder.getSelection();
				if (StrUtil.isEmpty(selection)) {
					return;
				}
				getPresenter().handPhotograph(getPresenter().getModel().getMapDeviceTabItem().get(selection));
			}
		});
		toolItem_in_openDoor = new ToolItem(toolBar3, SWT.NONE);
		toolItem_in_openDoor.setText("抬杆");
		
		toolItem_in_openDoor.setToolTipText("手动抬杆");
		toolItem_in_openDoor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!rateLimiter.tryAcquire()) {
					return;
				}
				getPresenter().openDoor();
			}
		});
		
		toolItem_in_closeDoor = new ToolItem(toolBar3, SWT.NONE);
		toolItem_in_closeDoor.setText("落杆");
		
		toolItem_in_closeDoor.setToolTipText("手动落杆");
		toolItem_in_closeDoor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!rateLimiter.tryAcquire()) {
					return;
				}
				getPresenter().closeDoor();
			}
		});
//		toolItem_testDevice = new ToolItem(toolBar3, SWT.NONE);
//		toolItem_testDevice.setText("检测");
//		
//		toolItem_testDevice.setToolTipText("检测设备是否正常");
//		toolItem_testDevice.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//				if (!rateLimiter.tryAcquire()) {
//					return;
//				}
//				getShell().setEnabled(false);
//				getPresenter().testDevice();
//				getShell().setEnabled(true);
//			}
//		});
//		toolItem_in_fleet = new ToolItem(toolBar3, SWT.NONE);
//		toolItem_in_fleet.setText("车队");
//		
//		toolItem_in_fleet.setToolTipText("启动车队");
//		toolItem_in_fleet.addSelectionListener(new SelectionAdapter() {
//			boolean isopen=true;
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//
//				if (!rateLimiter.tryAcquire()) {
//					return;
//				}
//				getPresenter().fleet(isopen);
//				if (isopen) {
//					toolItem_in_fleet.setText("停止");
//					toolItem_in_fleet.setToolTipText("停止车队");
//				}else{
//					toolItem_in_fleet.setText("车队");
//					toolItem_in_fleet.setToolTipText("启动车队");
//				}
//				isopen=!isopen;
//			}
//		});
		
		

		addToolItem = new ToolItem(toolBar3, SWT.NONE);
		addToolItem.setText("添加");
		addToolItem.setToolTipText("添加设备");
		addToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().addDevice(tabFolder);
			}
		});
		editToolItem = new ToolItem(toolBar3, SWT.NONE);
		editToolItem.setText("修改");
		editToolItem.setToolTipText("修改设备");
		editToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().editDevice(tabFolder);
			}
		});

		delToolItem = new ToolItem(toolBar3, SWT.NONE);
		delToolItem.setText("删除");
		delToolItem.setToolTipText("删除设备");

		delToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().deleteDevice(tabFolder.getSelection());
			}
		});
		tabFolder.setTopRight(control3);
		
		
		
		tabFolder.setFont(SWTResourceManager.getFont("微软雅黑", 14, SWT.BOLD));
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}
	public CTabFolder getTabFolder() {
		return tabFolder;
	}

	@Override
	public DevicePresenter getPresenter() {
		return (DevicePresenter) presenter;
	}

	public void initDevices(List<SingleCarparkDevice> listDevice) {
		if (StrUtil.isEmpty(listDevice)) {
			return;
		}
		Date date=new Date();
		for (SingleCarparkDevice d : listDevice) {
			CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
			tabItem.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
			String name = d.getName() == null ? d.getIp() : d.getName();
			tabItem.setText(name);
			final Composite composite = new Composite(tabFolder,SWT.EMBEDDED);
			tabItem.setControl(composite);
			composite.setLayout(new FillLayout());
			getDisplay().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					getPresenter().createRightCamera(d.getIp(), composite);
				}
			});
			getPresenter().getModel().getMapDeviceTabItem().put(tabItem, d.getIp());
			getPresenter().getModel().getMapIpToTabItem().put(d.getIp(),tabItem);
			tabItem.addDisposeListener(new DisposeListener() {
				
				@Override
				public void widgetDisposed(DisposeEvent e) {
					composite.dispose();
				}
			});
			tabItem.setImage(JFaceUtil.getImage("deviceStatus_16"));
			tabItem.setToolTipText("正在使用");
			getPresenter().getPresenter().checkDeviceControlTimeStatus(date,d);
		}
		tabFolder.setSelection(0);
		if (getPresenter().getType().equals("进口")) {
			toolItem_in_openDoor.setToolTipText("手动抬杆F2");
			toolItem_in_closeDoor.setToolTipText("手动落杆F1");
			tabFolder.setToolTipText("进口设备");
		}if (getPresenter().getType().equals("出口")) {
			toolItem_in_openDoor.setToolTipText("手动抬杆F4");
			toolItem_in_closeDoor.setToolTipText("手动落杆F3");
			tabFolder.setToolTipText("出口设备");
		}
	}

	public void controlItem(Boolean dispose) {
		if (dispose) {
			addToolItem.dispose();
			editToolItem.dispose();
			delToolItem.dispose();
		}
		
	}
}
