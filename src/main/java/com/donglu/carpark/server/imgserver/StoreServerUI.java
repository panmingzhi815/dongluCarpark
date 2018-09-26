package com.donglu.carpark.server.imgserver;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.util.CarparkFileUtils;
import com.donglu.carpark.util.ConstUtil;

public class StoreServerUI {

	protected Shell shell;
	private Text txtHttpstore;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			StoreServerUI window = new StoreServerUI();
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
		shell = new Shell(SWT.CLOSE|SWT.ON_TOP);
		shell.setSize(378, 174);
		shell.setText("商铺网络服务器配置");
		shell.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		composite.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setText("地址");
		
		txtHttpstore = new Text(composite, SWT.BORDER);
		txtHttpstore.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_txtHttpstore = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtHttpstore.widthHint = 280;
		txtHttpstore.setLayoutData(gd_txtHttpstore);
		new Label(composite, SWT.NONE);
		String readObject = (String) CarparkFileUtils.readObject(ConstUtil.STORE_SERVER_PATH);
		txtHttpstore.setText(readObject==null?"":readObject);
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				save();
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnNewButton.setText("保存");
	}

	protected void save() {
		CarparkFileUtils.writeObject(ConstUtil.STORE_SERVER_PATH, txtHttpstore.getText());
	}
}
