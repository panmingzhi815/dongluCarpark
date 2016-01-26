package com.donglu.carpark.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.donglu.carpark.ui.common.AbstractApp;
import com.donglu.carpark.ui.common.Presenter;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ShowApp extends AbstractApp{

	protected Shell shell;
	
	private Presenter presenter;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ShowApp window = new ShowApp();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	@Override
	public void open() {
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
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(750, 600);
		shell.setText("浏览历史记录");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.go(composite);
	}

	public Presenter getPresenter() {
		return presenter;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public boolean isOpen() {
		
		return !shell.isDisposed();
	}

	@Override
	public void focus() {
		
		shell.setFocus();
	}

}
