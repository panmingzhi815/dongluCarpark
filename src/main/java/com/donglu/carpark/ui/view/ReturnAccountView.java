package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkReturnAccount;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;

public class ReturnAccountView extends Composite implements View{
	private Text text_1;
	private Presenter presenter;
	private Composite listComposite;
	private ComboViewer comboViewer;
	private List<SingleCarparkSystemUser> list;

	public ReturnAccountView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Group group = new Group(this, SWT.NONE);
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		group.setText("查询");
		group.setLayout(new GridLayout(10, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("操作员");
		
		comboViewer = new ComboViewer(group, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 55;
		combo.setLayoutData(gd_combo);
		Font font = SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("归账人");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("时间");
		
		DateChooserCombo dateChooserCombo = new DateChooserCombo(group, SWT.BORDER | SWT.READ_ONLY);
		dateChooserCombo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo.setValue(new org.joda.time.DateTime(new Date()).minusDays(1).toDate());
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("终止时间");
		
		DateChooserCombo dateChooserCombo_1 = new DateChooserCombo(group, SWT.BORDER | SWT.READ_ONLY);
		dateChooserCombo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateChooserCombo_1.setValue(new Date());
		Button button = new Button(group, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SingleCarparkSystemUser singleCarparkSystemUser = list.get(comboViewer.getCombo().getSelectionIndex());
				String operaName="";
				if (!StrUtil.isEmpty(singleCarparkSystemUser.getId())) {
					operaName=singleCarparkSystemUser.getUserName();
				}
				getPresenter().search(operaName,text_1.getText(),dateChooserCombo.getValue(),dateChooserCombo_1.getValue());
			}
		});
		button.setText("查询");
		
		Button button_1 = new Button(group, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().export();
			}
		});
		button_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_1.setText("导出");
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public ReturnAccountPresenter getPresenter() {
		return (ReturnAccountPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}
	public void setCombo(final List<SingleCarparkSystemUser> list){
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				comboViewer.setInput(list);
				ReturnAccountView.this.list = list;
				comboViewer.getCombo().select(0);
			}
		});
	}
}
