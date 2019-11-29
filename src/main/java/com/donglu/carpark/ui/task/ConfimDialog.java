package com.donglu.carpark.ui.task;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.wb.swt.SWTResourceManager;

public class ConfimDialog extends Dialog {

	protected Boolean result=false;
	protected Shell shell;
	private String message;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ConfimDialog(String title,String message) {
		super(new Shell(), SWT.ON_TOP|SWT.CLOSE);
		this.message = message;
		setText(title);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Boolean open() {
		try {
			createContents();
			shell.open();
			shell.layout();
			WidgetUtil.center(shell);
			Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			return result;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
//		shell = new Shell(getParent(), getStyle());
		shell = new Shell();
		shell.setSize(600, 167);
		shell.setImage(JFaceUtil.getImage("carpark_32"));
		shell.setText(getText());
		GridLayout gl_shell = new GridLayout(1, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.horizontalSpacing = 0;
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		CLabel label = new CLabel(composite, SWT.CENTER);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setAlignment(SWT.CENTER);
		label.setText(message);
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		
		Button button = new Button(composite_1, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=true;
				shell.close();
			}
		});
		button.setText("确  认");
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		GridData gd_label_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_1.widthHint = 20;
		label_1.setLayoutData(gd_label_1);
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=false;
				shell.close();
			}
		});
		button_1.setText("取  消");
		button.setFocus();
	}

	public void close() {
		Runnable runnable = new Runnable() {
			public void run() {
				if (!shell.isDisposed()) {
					shell.close();
				}
			}
		};
		shell.getDisplay().asyncExec(runnable);
	}
}
