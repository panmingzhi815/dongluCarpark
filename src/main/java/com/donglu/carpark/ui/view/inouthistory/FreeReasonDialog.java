package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.ui.util.FileUtils;
import com.dongluhitec.card.ui.util.WidgetUtil;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.wb.swt.SWTResourceManager;

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
		shell = new Shell(getParent(),SWT.ON_TOP|SWT.CLOSE);
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
		lblNewLabel_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		lblNewLabel_1.setText("原因");
		
		Combo combo = new Combo(composite, SWT.READ_ONLY);
		combo.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 131;
		combo.setLayoutData(gd_combo);
		combo.setItems(reasons);
		Object readObject = FileUtils.readObject("lastCarFreeReason");
		if (readObject!=null) {
			combo.setText(readObject+"");
		}else{
			combo.select(0);
		}
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblNewLabel.setText("详情");
		
		text = new Text(composite, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.heightHint = 61;
		gd_text.widthHint = 66;
		text.setLayoutData(gd_text);
		new Label(composite, SWT.NONE);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button button = new Button(composite_1, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=combo.getText()+"-"+text.getText();
				FileUtils.writeObject("lastCarFreeReason", combo.getText());
				close();
			}
		});
		button.setText("确认");
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				result=null;
				close();
			}
		});
		button_1.setText("取消");
		Display display = shell.getDisplay();
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					System.out.println("free reason  "+event.keyCode);
					if (event.keyCode==13||event.keyCode==StrUtil.SMAIL_KEY_ENTER) {
						result=combo.getText()+"-"+text.getText();
						FileUtils.writeObject("lastCarFreeReason", combo.getText());
						close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		display.addFilter(SWT.KeyUp, listener);
		display.setData("freeReasoKeyListener", listener);
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				Listener data = (Listener) display.getData("freeReasoKeyListener");
				if (data!=null) {
					display.removeFilter(SWT.KeyUp, data);
				}
			}
		});
	}

	/**
	 * 
	 */
	public void close() {
		try {
			shell.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
