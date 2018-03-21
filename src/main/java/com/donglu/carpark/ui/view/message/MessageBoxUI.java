package com.donglu.carpark.ui.view.message;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class MessageBoxUI {

	protected Shell shell;
	private String title;
	private String msg;
	private String[] buttons=new String[]{"确    认","取    消"};
	private int stayTime;
	
	int result=0;
	private boolean waitReturn;
	private Timer timer;
	private Point location;
	
	public MessageBoxUI(String title,String msg,String[] buttons,int stayTime,boolean waitReturn,Point location) {
		this.title = title;
		this.msg = msg;
		this.buttons = buttons;
		this.stayTime = stayTime;
		this.waitReturn = waitReturn;
		this.location = location;
	}

	public MessageBoxUI() {
	}

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			MessageBoxUI window = new MessageBoxUI();
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
		if (waitReturn) {
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			} 
		}
		return result;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.CLOSE | SWT.MIN|SWT.ON_TOP);
		shell.setSize(450, 245);
		shell.setText(title);
		shell.setLayout(new GridLayout(1, false));
		shell.setImage(JFaceUtil.getImage("carpark_32"));
		if (location==null) {
			Rectangle clientArea = shell.getDisplay().getClientArea();
			int down = clientArea.width - shell.getSize().x;
			int left = clientArea.height - shell.getSize().y;
			shell.setLocation(down, left);
		}else{
			shell.setLocation(location);
		}
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		Label lbl_msg = new Label(composite, SWT.WRAP);
		lbl_msg.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.BOLD));
		lbl_msg.setText(msg);
		lbl_msg.setAlignment(SWT.CENTER);
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.marginWidth = 15;
		gl_composite_1.horizontalSpacing = 15;
		composite_1.setLayout(gl_composite_1);
		GridData gd_composite_1 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_composite_1.exclude = false;
		if (StrUtil.isEmpty(buttons)) {
			gd_composite_1.exclude =true;
		}
		composite_1.setLayoutData(gd_composite_1);
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (timer!=null) {
					timer.cancel();
				}
			}
		});
		
		if (!StrUtil.isEmpty(buttons)) {
			for (int i = 0; i < buttons.length; i++) {
				String string = buttons[i];
				int size = i;
				Button button = new Button(composite_1, SWT.NONE);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						result = size;
					}
				});
				button.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
				button.setText(string);
			} 
		}
		if (stayTime>0) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					result=-1;
					if (!shell.isDisposed()) {
						shell.getDisplay().asyncExec(new Runnable() {
							public void run() {
								shell.close();
							}
						});
					}
				}
			}, stayTime);
		}

	}

	public void close() {
		shell.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (!shell.isDisposed()) {
					shell.close();
				} 
			}
		});
	}
}
