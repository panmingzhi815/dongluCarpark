package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.CarparkMainApp;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkDevice;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.util.concurrent.RateLimiter;

import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Display;

public class DeviceView extends Composite implements View{
	private Presenter presenter;
	private CarparkMainModel model;
	private CTabFolder tabFolder;
	private RateLimiter rateLimiter = RateLimiter.create(1);
	private ToolItem addToolItem;

	private ToolItem editToolItem;

	private ToolItem delToolItem;

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
		GridLayout layout3 = new GridLayout();
		layout3.marginHeight = 0;
		layout3.marginWidth = 0;
		control3.setLayout(layout3);
		ToolBar toolBar3 = new ToolBar(control3, SWT.NONE);
		ToolItem toolItem_in_photograph3 = new ToolItem(toolBar3, SWT.NONE);
		toolItem_in_photograph3.setText("拍照");
		toolItem_in_photograph3.setToolTipText("手动抓拍");
		toolItem_in_photograph3.setSelection(true);
		toolItem_in_photograph3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println(rateLimiter.tryAcquire());
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				CTabItem selection = tabFolder.getSelection();
				if (StrUtil.isEmpty(selection)) {
					return;
				}
				getPresenter().handPhotograph(CarparkMainApp.mapDeviceTabItem.get(selection));
			}
		});
		ToolItem toolItem_in_openDoor3 = new ToolItem(toolBar3, SWT.NONE);
		toolItem_in_openDoor3.setText("抬杆");
		toolItem_in_openDoor3.setToolTipText("手动抬杆");
		toolItem_in_openDoor3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (!rateLimiter.tryAcquire()) {
					return;
				}
				getPresenter().openDoor();
			}
		});

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

	public DeviceView(Composite c, int style, CarparkMainModel model) {
		this(c, style);
		this.model=model;
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
		for (SingleCarparkDevice d : listDevice) {
			CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
			tabItem.setFont(SWTResourceManager.getFont("微软雅黑", 15, SWT.NORMAL));
			tabItem.setText(d.getName() == null ? d.getIp() : d.getName());
			final Composite composite = new Composite(tabFolder, SWT.BORDER | SWT.EMBEDDED);
			tabItem.setControl(composite);
			composite.setLayout(new FillLayout());
			getPresenter().createRightCamera(d.getIp(), composite);
			CarparkMainApp.mapDeviceTabItem.put(tabItem, d.getIp());
			tabItem.addDisposeListener(new DisposeListener() {
				
				public void widgetDisposed(DisposeEvent e) {
					composite.dispose();
				}
			});
		}
		tabFolder.setSelection(0);
	}

	public void controlItem(Boolean dispose) {
		if (dispose) {
			addToolItem.dispose();
			editToolItem.dispose();
			delToolItem.dispose();
		}
		
	}
}
