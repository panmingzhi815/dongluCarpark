package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.task.CarInTask;
import com.donglu.carpark.util.CarparkUtils;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.CLabel;

public class InCheckDetailView extends Composite implements View{
	private Presenter presenter;
	private CarparkMainModel model;

	public InCheckDetailView(Composite parent, int style) {
		super(parent, style);
		createContent();
	}

	public InCheckDetailView(Composite c, int style, CarparkMainModel model) {
		super(c, style);
		this.model=model;
		createContent();
	}

	/**
	 * 
	 */
	public void createContent() {
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		for (String s:model.getMapInCheck().keySet()) {
			CarInTask carInTask = model.getMapInCheck().get(s);
			TabItem tbtmbdw = new TabItem(tabFolder, SWT.NONE);
			tbtmbdw.setText(s);
			Composite composite = new Composite(tabFolder, SWT.NONE);
			tbtmbdw.setControl(composite);
			composite.setLayout(new GridLayout(1, false));
			Composite composite_1 = new Composite(composite, SWT.NONE);
			composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			composite_1.setLayout(new GridLayout(4, false));
			Label lblNewLabel = new Label(composite_1, SWT.NONE);
			lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
			lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblNewLabel.setText("车牌");
			Text text = new Text(composite_1, SWT.BORDER);
			text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
			GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_text.widthHint = 90;
			text.setLayoutData(gd_text);
			text.setText(s);
			Button btnNewButton = new Button(composite_1, SWT.NONE);
			btnNewButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean equals = text.getText().equals(s);
					TabItem[] selection = tabFolder.getSelection();
					TabItem x = selection[0];
					boolean result=getPresenter().carIn(carInTask,!equals,text.getText());
					if (result) {
						x.dispose();
					}
				}
			});
			btnNewButton.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
			btnNewButton.setText("确定");
			
			Button button = new Button(composite_1, SWT.NONE);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					TabItem[] selection = tabFolder.getSelection();
					TabItem x = selection[0];
					model.getMapInCheck().remove(s);
					if (model.getMapInCheck().keySet().size()<1) {
						getShell().dispose();
					}
					x.dispose();
				}
			});
			button.setText("取消");
			Composite composite_2 = new Composite(composite, SWT.NONE);
			composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
			composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			CLabel label = new CLabel(composite_2, SWT.NONE);
			label.setAlignment(SWT.CENTER);
//			ImgUtil.setBackgroundImage(label,carInTask.getBigImage());
			CarparkUtils.setBackgroundImage(carInTask.getBigImage(), label,carInTask.getBigImgSavePath());
		}
	}


	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public InCheckDetailPresenter getPresenter() {
		return (InCheckDetailPresenter) presenter;
	}

}
