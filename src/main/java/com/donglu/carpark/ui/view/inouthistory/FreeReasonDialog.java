package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.WidgetUtil;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

public class FreeReasonDialog extends Dialog {

	protected String result=null;
	protected Shell shell;
	private Text text;
	private String[] reasons;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FreeReasonDialog(String[] reasons) {
		super(Display.getCurrent().getActiveShell(), Display.getCurrent().getActiveShell().getStyle());
		this.reasons = reasons;
		setText("免费原因确认");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
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
		shell = new Shell(getParent(), getStyle());
		shell.setSize(364, 280);
		shell.setText(getText());
		shell.setLayout(new GridLayout(1, false));
		WidgetUtil.center(shell);
		shell.setImage(JFaceUtil.getImage("consumption_24"));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gd_composite.heightHint = 151;
		gd_composite.widthHint = 199;
		composite.setLayoutData(gd_composite);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText("原因");
		
		Combo combo = new Combo(composite, SWT.READ_ONLY);
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 131;
		combo.setLayoutData(gd_combo);
		combo.setItems(reasons);
		combo.select(0);
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel.setText("详情");
		
		text = new Text(composite, SWT.BORDER);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode!=StrUtil.BIG_KEY_ENTER&&e.keyCode!=StrUtil.SMAIL_KEY_ENTER) {
					return;
				}
				result=combo.getText()+"-"+text.getText();
				shell.close();
			}
		});
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.heightHint = 61;
		gd_text.widthHint = 66;
		text.setLayoutData(gd_text);
		new Label(composite, SWT.NONE);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=combo.getText()+"-"+text.getText();
				shell.close();
			}
		});
		button.setText("确认");
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=null;
				shell.close();
			}
		});
		button_1.setText("取消");
	}
}
