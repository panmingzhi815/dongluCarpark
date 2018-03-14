package com.donglu.carpark.ui.view.user;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class UserRemindMessageBox {

	protected Shell shell;
	int result=0;
	private String msg;
	private String title;
	private int stayTime=0;
	private Timer timer;
	private String[] buttons=new String[]{"查看","稍后提醒","我知道了"};
	
	public UserRemindMessageBox(String msg) {
		this.msg = msg;
		this.title ="提示";
	}
	public UserRemindMessageBox(String title,String msg,int stayTime,String[] buttons) {
		this.title = title;
		this.msg = msg;
		this.stayTime = stayTime;
		this.buttons = buttons;
	}
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UserRemindMessageBox window = new UserRemindMessageBox(null);
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public int open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.CLOSE|SWT.ON_TOP);
		shell.setImage(JFaceUtil.getImage("attendanceplan_72"));
		shell.setSize(360, 235);
		shell.setText(title);
		shell.setLayout(new GridLayout(1, false));
		Rectangle clientArea = shell.getDisplay().getClientArea();
		int down=clientArea.width-360;
		int left=clientArea.height-235;
		shell.setLocation(down, left);
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		Label lblNewLabel = new Label(composite, SWT.WRAP);
		lblNewLabel.setAlignment(SWT.CENTER);
		if (msg!=null) {
			lblNewLabel.setText(msg);
		}
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=1;
				shell.dispose();
			}
		});
		button.setText("查看");
		
		Button btnNewButton = new Button(composite_1, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=2;
				shell.dispose();
			}
		});
		btnNewButton.setText("稍后提醒");
		
		Button btnNewButton_1 = new Button(composite_1, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=3;
				shell.dispose();
			}
		});
		btnNewButton_1.setText("我知道了");
		if (stayTime>0) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (shell.isDisposed()) {
						return;
					}
					
				}
			}, stayTime);
		}
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (timer!=null) {
					timer.cancel();
				}
			}
		});
	}
	public void close() {
		result=0;
		if (!shell.isDisposed()) {
			shell.dispose();
		}
	}
}
