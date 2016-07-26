package com.donglu.carpark.ui.view.offline;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.google.common.util.concurrent.RateLimiter;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.swt.custom.ScrolledComposite;


public class CarparkOffLineHistoryView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_plateNO;
	private CDateTime dt_end;
	
	private RateLimiter rateLimiter = RateLimiter.create(2);

	public CarparkOffLineHistoryView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Group group = new Group(scrolledComposite, SWT.NONE);
		group.setText("查询");
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridLayout gl_group = new GridLayout(7, false);
		gl_group.verticalSpacing = 10;
		gl_group.horizontalSpacing = 15;
		group.setLayout(gl_group);
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车牌");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_plateNO = new Text(group, SWT.BORDER);
		text_plateNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 71;
		text_plateNO.setLayoutData(gd_text);
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("开始时间");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		CDateTime dateTime = new CDateTime(group, CDT.BORDER);
		dateTime.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime.setPattern("yyyy-MM-dd HH:mm");
		dateTime.setSelection(new org.joda.time.DateTime().minusHours(1).toDate());
		
		Label label_6 = new Label(group, SWT.NONE);
		label_6.setText("结束时间");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		dt_end = new CDateTime(group, CDT.BORDER);
		dt_end.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dt_end.setPattern("yyyy-MM-dd HH:mm\r\n");
		dt_end.setSelection(new Date());
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				dateTime.getSelection();
				getPresenter().search(text_plateNO.getText(), dateTime.getSelection(), dt_end.getSelection());
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.setText("查询");
		scrolledComposite.setContent(group);
		scrolledComposite.setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public CarparkOffLineHistoryPresenter getPresenter() {
		return (CarparkOffLineHistoryPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
}
