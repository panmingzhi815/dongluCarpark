package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.List;
import org.eclipse.wb.swt.SWTResourceManager;

public class FreeReasonView extends Composite implements View{
	private Presenter presenter;
	private Text text;
	private SingleCarparkInOutHistory model;
	private String reasons;

	public FreeReasonView(Composite parent, int style) {
		super(parent, style);
		createContent();
	}

	/**
	 * 
	 */
	public void createContent() {
		setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		GridData gd_composite = new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1);
		gd_composite.heightHint = 115;
		composite.setLayoutData(gd_composite);
		
		Label label = new Label(composite, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("原因");
		
		ComboViewer comboViewer = new ComboViewer(composite, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(reasons.split(","));
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_combo.widthHint = 179;
		combo.setLayoutData(gd_combo);
		combo.select(0);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		label_1.setText("备注");
		
		text = new Text(composite, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text.heightHint = 67;
		text.setLayoutData(gd_text);
	}

	public FreeReasonView(Composite c, int style, String reasons) {
		super(c, style);
		this.reasons=reasons;
		createContent();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public CarInPresenter getPresenter() {
		return (CarInPresenter) presenter;
	}

	public void setModel(SingleCarparkInOutHistory model) {
		this.model=model;
	}

	public void setReasons(String reasons) {
		this.reasons = reasons;
	}
}
