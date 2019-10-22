package com.donglu.carpark.ui.view.device;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractView;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.FillLayout;

public class DeviceView extends AbstractView {
	private Text text_1;
	private Composite composite;

	public DeviceView(Composite parent) {
		super(parent, parent.getStyle());
		setLayout(new GridLayout(1, false));
		
		Group group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label lblip = new Label(group, SWT.NONE);
		lblip.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblip.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblip.setText("岗亭IP");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_1.widthHint = 122;
		text_1.setLayoutData(gd_text_1);
		
		Button button = new Button(group, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("查询");
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	public Composite getComposite() {
		return composite;
	}
}
