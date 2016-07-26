package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.dongluhitec.card.ui.util.WidgetUtil;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ShowDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Presenter presenter;
	protected boolean haveButon=true;

	
	public ShowDialog(String name) {
		super(Display.getCurrent().getActiveShell(), SWT.NONE);
		shell = new Shell(getParent(),getParent().getStyle());
		shell.setSize(300, 300);
		setText(name);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		WidgetUtil.center(shell);
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		presenter.go(composite_2);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gridData.exclude = !haveButon;
		composite_1.setLayoutData(gridData);
		composite_1.setLayout(new GridLayout(2, false));
		
		Button btnNewButton = new Button(composite_1, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=presenter.getModel();
				shell.close();
			}
		});
		btnNewButton.setText("确定");
		
		Button btnNewButton_1 = new Button(composite_1, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=null;
				shell.close();
			}
		});
		btnNewButton_1.setText("取消");

	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	public void setSize(int width,int height ){
		shell.setSize(width, height);
	}

	public void setHaveButon(boolean haveButon) {
		this.haveButon = haveButon;
	}
}
