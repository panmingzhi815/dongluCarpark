package com.donglu.carpark.ui.view.user;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkUser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class UserRemindMessageBox {

	protected Shell shell;
	private SingleCarparkUser user;
	int result=0;
	
	public UserRemindMessageBox(SingleCarparkUser user) {
		this.user=user;
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
		shell.setText("用户到期提醒");
		shell.setLayout(new GridLayout(1, false));
		Rectangle clientArea = shell.getDisplay().getClientArea();
		int down=clientArea.width-360;
		int left=clientArea.height-235;
		shell.setLocation(down, left);
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		CLabel lblNewLabel = new CLabel(composite, SWT.NONE);
		lblNewLabel.setAlignment(SWT.CENTER);
		if (user!=null) {
			lblNewLabel.setText("用户"+user+"即将过期");
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

	}
	public void close() {
		result=0;
		if (!shell.isDisposed()) {
			shell.dispose();
		}
	}
}
