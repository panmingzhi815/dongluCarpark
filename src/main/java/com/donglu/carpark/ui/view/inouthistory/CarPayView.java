package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;

public class CarPayView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_plateNO;

	public CarPayView(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);
		
		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridLayout gl_group = new GridLayout(8, false);
		gl_group.marginHeight = 0;
		group.setLayout(gl_group);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("车牌");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_plateNO = new Text(group, SWT.BORDER);
		text_plateNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_plateNO.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setText("缴费时间");
		
		DateChooserCombo dateChooserCombo = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setText("-");
		
		DateChooserCombo dateChooserCombo_1 = new DateChooserCombo(group, SWT.BORDER);
		dateChooserCombo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					getPresenter().search(text_plateNO.getText(),dateChooserCombo.getValue(),dateChooserCombo_1.getValue());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setText("查询");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button_1 = new Button(group, SWT.CHECK);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().startAutoRefresh(button_1.getSelection());
			}
		});
		button_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_1.setText("保持刷新");
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public CarPayPresenter getPresenter() {
		return (CarPayPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}

	public void setShellFocus() {
		listComposite.getDisplay().syncExec(new Runnable() {
			public void run() {
				int hWnd=getShell().handle;
				Rectangle r = getShell().getBounds();
				System.out.println(getShell().getBounds());
				int hForeWnd = OS.GetForegroundWindow();
				int dwForeID = OS.GetWindowThreadProcessId(hForeWnd, null);
				int dwCurID = OS.GetCurrentThreadId();
				OS.AttachThreadInput(dwCurID, dwForeID, true);
				int SW_SHOWNORMAL=1;
				OS.ShowWindow(hWnd, SW_SHOWNORMAL);
				int x = 0;
				int y = 0;
				int cx = 0;
				int cy = 0;
				OS.SetWindowPos(hWnd, OS.HWND_TOPMOST, x, y, cx, cy, OS.SWP_NOSIZE|OS.SWP_NOMOVE);
				OS.SetWindowPos(hWnd, OS.HWND_NOTOPMOST, x, y, cx, cy, OS.SWP_NOSIZE|OS.SWP_NOMOVE);
				OS.SetForegroundWindow(hWnd);
				OS.AttachThreadInput(dwCurID, dwForeID, false);
				getShell().setMaximized(getShell().getMaximized());
			}
		});
	}
}
