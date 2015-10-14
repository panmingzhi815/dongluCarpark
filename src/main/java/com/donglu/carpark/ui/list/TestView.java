package com.donglu.carpark.ui.list;

import org.eclipse.swt.widgets.Composite;

import com.dongluhitec.card.common.ui.Presenter;
import com.dongluhitec.card.common.ui.WidgetContainer;
import com.dongluhitec.card.common.ui.control.AbstractViewer;
import com.dongluhitec.card.common.ui.impl.SWTContainer;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;

public class TestView extends AbstractViewer {
	private Text text;
	private Text text_1;
	private Composite composite;

	public TestView(Composite container, int none) {
		super(container, none);
		setLayout(new GridLayout(1, false));
		
		Group group = new Group(this, SWT.NONE);
		group.setText("查询");
		group.setLayout(new GridLayout(9, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("操作员");
		
		text = new Text(group, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("归账人");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("时间");
		
		DateTime dateTime = new DateTime(group, SWT.BORDER);
		dateTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("终止时间");
		
		DateTime dateTime_1 = new DateTime(group, SWT.BORDER);
		dateTime_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Button button = new Button(group, SWT.NONE);
		button.setText("查询");
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}
	public TestView(WidgetContainer container) {
		this(((SWTContainer) container).getContainer(), container.getWidgetStyle());
		container.add(getWidget());
	}
	public WidgetContainer getListContainer() {
		return SWTContainer.getInstance(composite);
	}
	
	
}
