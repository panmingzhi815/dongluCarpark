package com.donglu.carpark.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.donglu.carpark.ui.common.AbstractApp;
import com.donglu.carpark.ui.common.Presenter;

import org.eclipse.swt.layout.FillLayout;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class ShowApp extends AbstractApp{

	protected Shell shell;
	
	private Presenter presenter;
	
	private List<Presenter> listPresenter=new ArrayList<>();
	
	boolean maximized = true;

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
		shell.setMaximized(maximized);
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		for (Presenter presenter : listPresenter) {
			TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
			tbtmNewItem.setText(presenter.getTitle());
			
			Composite composite = new Composite(tabFolder, SWT.NONE);
			tbtmNewItem.setControl(composite);
			composite.setLayout(new FillLayout(SWT.HORIZONTAL));
			presenter.go(composite);
		}
		tabFolder.setSelection(0);
	}

	public Presenter getPresenter() {
		return presenter;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		if(!listPresenter.contains(presenter)){
			listPresenter.add(presenter);
		}
	}

	@Override
	public boolean isOpen() {
		
		return shell!=null&&!shell.isDisposed();
	}

	@Override
	public void focus() {
		
		shell.setFocus();
	}

	public Shell getShell() {
		return shell;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}
	@Override
	public void close() {
		if (!shell.isDisposed()) {
			shell.close();
		}
	}
}
