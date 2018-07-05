package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.joda.time.DateTime;
import org.eclipse.swt.layout.GridData;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.util.concurrent.RateLimiter;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;

import java.util.Date;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;


public class CountFlowView extends Composite implements View{
	private static final String TEMP_TEMP2_PNG = "temp/temp2.png";

	private static final String TEMP_TEMP1_PNG = "temp/temp1.png";

	private static final String TEMP_TEMP_PNG = "temp/temp.png";

	private Presenter presenter;
	
	private RateLimiter rateLimiter = RateLimiter.create(2);

	private CLabel lbl_one;

	private CLabel lbl_two;

	private CLabel lbl_three;

	private static Image image;
	
	private static Image image1;

	private static Image image2;

	private TabFolder tabFolder;

	public CountFlowView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				disposeImage();
			}
		});
		Group group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(9, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		group.setText("图形统计");
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("开始时间");
		
		DateChooserCombo dcc_start = new DateChooserCombo(group, SWT.BORDER);
		dcc_start.setValue(new DateTime().minusMonths(1).toDate());
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setText("结束时间");
		
		DateChooserCombo dcc_end = new DateChooserCombo(group, SWT.BORDER);
		dcc_end.setValue(new Date());
		
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("方式");
		
		ComboViewer comboViewer = new ComboViewer(group, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[]{"天流量","月流量","小时流量"});
		combo.select(0);
		
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.setText("统计");
		
		Button btnNewButton_1 = new Button(group, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabItem[] selection = tabFolder.getSelection();
				if (StrUtil.isEmpty(selection)) {
					return;
				}
				TabItem tabItem = selection[0];
				String img = (String) tabItem.getData("img");
				getPresenter().saveImage(img);
			}
		});
		btnNewButton_1.setText("保存");
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setImage();
			}
		});
		button.setText("刷新");
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!rateLimiter.tryAcquire()) {
					return;
				}
				Date value = StrUtil.getTodayTopTime(dcc_start.getValue());
				Date value2 = StrUtil.getTodayBottomTime(dcc_end.getValue());
				getPresenter().countFlows(value,value2,combo.getText());
			}
		});
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tabFolder = new TabFolder(composite_1, SWT.NONE);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("单口流量");
		tabItem.setData("img",TEMP_TEMP_PNG);
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		lbl_one = new CLabel(composite, SWT.NONE);
		lbl_one.setText("");
		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("进出口流量");
		tabItem_1.setData("img",TEMP_TEMP1_PNG);
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_2);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		lbl_two = new CLabel(composite_2, SWT.NONE);
		lbl_two.setText("");
		
		TabItem tabItem_2 = new TabItem(tabFolder, SWT.NONE);
		tabItem_2.setText("总流量");
		tabItem_2.setData("img",TEMP_TEMP2_PNG);
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tabItem_2.setControl(composite_3);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		lbl_three = new CLabel(composite_3, SWT.NONE);
		lbl_three.setText("");
		lbl_one.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (image!=null&&!image.isDisposed()) {
					Rectangle bounds = lbl_one.getBounds();
					e.gc.drawImage(image, 0, 0, image.getImageData().width, image.getImageData().height, 0, 0, bounds.width, bounds.height);
				}
			}
		});
		lbl_two.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (image1!=null&&!image1.isDisposed()) {
					Rectangle bounds = lbl_one.getBounds();
					e.gc.drawImage(image1, 0, 0, image1.getImageData().width, image1.getImageData().height, 0, 0, bounds.width, bounds.height);
				}
			}
		});
		lbl_three.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				if (image2!=null&&!image2.isDisposed()) {
					Rectangle bounds = lbl_one.getBounds();
					e.gc.drawImage(image2, 0, 0, image2.getImageData().width, image2.getImageData().height, 0, 0, bounds.width, bounds.height);
				}
			}
		});
	}


	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public CountFlowPresenter getPresenter() {
		return (CountFlowPresenter) presenter;
	}
	public void setImage(){
		disposeImage();
		image = new Image(getDisplay(), TEMP_TEMP_PNG);
		image1 = new Image(getDisplay(), TEMP_TEMP1_PNG);
		image2 = new Image(getDisplay(), TEMP_TEMP2_PNG);
		lbl_one.redraw();
		lbl_two.redraw();
		lbl_three.redraw();
		
	}


	/**
	 * 
	 */
	public void disposeImage() {
		if (image!=null) {
			image.dispose();
		}
		if (image1!=null) {
			image.dispose();
		}
		if (image2!=null) {
			image.dispose();
		}
	}
	public Rectangle getImageSize(){
		Rectangle bounds = lbl_one.getBounds();
		if (bounds.width<=0) {
			bounds = lbl_two.getBounds();
			if (bounds.width<=0) {
				bounds = lbl_three.getBounds();
			}
		}
		return bounds;
	}
	
}
