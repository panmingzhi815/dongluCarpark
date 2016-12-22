package com.donglu.carpark.hardware.bx;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class AnKangBXScreenTestApp {

	protected Shell shell;
	private Text text;
	private Text txtg;
	private AnKangBXScreenServiceImpl service;
	private Button button_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			AnKangBXScreenTestApp window = new AnKangBXScreenTestApp();
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
		shell.setSize(450, 300);
		
		text = new Text(shell, SWT.BORDER);
		text.setText("172.16.1.232");
		text.setBounds(15, 60, 124, 23);
		
		Button button = new Button(shell, SWT.NONE);
		button.setBounds(15, 27, 80, 27);
		button.setText("初始化");
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				service.sendPlateNO(0, text.getText(), txtg.getText(), true);
			}
		});
		btnNewButton.setBounds(15, 141, 80, 27);
		btnNewButton.setText("发送车牌");
		
		txtg = new Text(shell, SWT.BORDER);
		txtg.setText("陕G12345");
		txtg.setBounds(15, 99, 124, 23);
		
		button_1 = new Button(shell, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				service.setPlateControlStatus(false);
				service.sendPosition(0, text.getText(), 0);
			}
		});
		button_1.setBounds(117, 141, 80, 27);
		button_1.setText("更新屏幕");
		init();
	}

	private void init() {
		service = new AnKangBXScreenServiceImpl();
		service.init(0);
		service.setPlateControlStatus(false);
		service.setWillInPlate(Arrays.asList("陕GZ6606","陕GZ6605"));
	}
}
