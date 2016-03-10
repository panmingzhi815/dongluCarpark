package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ShowDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	protected Presenter presenter;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ShowDialog(Shell parent, int style,String name) {
		super(parent, style);
		setText(name);
	}

	public ShowDialog(String name) {
		super(Display.getCurrent().getActiveShell(), SWT.NONE);
		setText(name);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
//		shell.setMaximized(true);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return presenter.getModel();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(),SWT.CLOSE );
		shell.setSize(450, 300);
		shell.setText(getText());
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.go(composite);

	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

}
