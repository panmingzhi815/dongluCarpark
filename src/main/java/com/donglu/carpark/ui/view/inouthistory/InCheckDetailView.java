package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.ui.task.CarInTask;
import com.donglu.carpark.util.ImageUtils;
import com.donglu.carpark.util.TextUtils;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.CLabel;

public class InCheckDetailView extends Composite implements View{
	private Presenter presenter;
	private CarparkMainModel model;
	private static Map<TabItem, CarInTask> mapItemWithTask=new HashMap<>();
	private static Map<TabItem, CLabel> mapItemWithLabel=new HashMap<>();

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
			mapItemWithTask.put(tbtmbdw, carInTask);
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
			text.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.keyCode==StrUtil.BIG_KEY_ENTER) {
						if (text.getText().length()<3) {
							return;
						}
						onSuccess(tabFolder, s, carInTask, text);
					}
				}
				
			});
			TextUtils.createPlateNOAutoCompleteField(text);
			GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			gd_text.widthHint = 90;
			text.setLayoutData(gd_text);
			text.setText(s);
			Button btnNewButton = new Button(composite_1, SWT.NONE);
			btnNewButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					onSuccess(tabFolder, s, carInTask, text);
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
					x.dispose();
					if (model.getMapInCheck().keySet().size()<1) {
						getShell().dispose();
					}
				}
			});
			button.setText("取消");
			Composite composite_2 = new Composite(composite, SWT.NONE);
			composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
			composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			CLabel label = new CLabel(composite_2, SWT.NONE);
			label.setAlignment(SWT.CENTER);
//			ImgUtil.setBackgroundImage(label,carInTask.getBigImage());
			ImageUtils.setBackgroundImage(carInTask.getBigImage(), label,carInTask.getBigImgSavePath());
			mapItemWithLabel.put(tbtmbdw, label);
		}
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TabItem[] selection = tabFolder.getSelection();
				if (StrUtil.isEmpty(selection)) {
					return;
				}
				TabItem tabItem = selection[0];
				CarInTask carInTask=mapItemWithTask.get(tabItem);
				ImageUtils.setBackgroundImage(carInTask.getBigImage(), mapItemWithLabel.get(tabItem),carInTask.getBigImgSavePath());
			}
		});
	}


	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public InCheckDetailPresenter getPresenter() {
		return (InCheckDetailPresenter) presenter;
	}

	/**
	 * @param tabFolder
	 * @param s
	 * @param carInTask
	 * @param text
	 */
	public void onSuccess(TabFolder tabFolder, String s, CarInTask carInTask, Text text) {
		String text2 = text.getText();
		boolean equals = text2.equals(s);
		TabItem[] selection = tabFolder.getSelection();
		TabItem x = selection[0];
		boolean result=getPresenter().carIn(carInTask,!equals,text2);
		if (result) {
			model.getMapInCheck().remove(s);
			mapItemWithTask.remove(x);
			mapItemWithLabel.remove(x);
			x.dispose();
		}
		if (model.getMapInCheck().keySet().size()<1) {
			getShell().dispose();
		}
	}

}
