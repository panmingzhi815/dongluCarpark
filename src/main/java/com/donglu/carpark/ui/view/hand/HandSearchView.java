package com.donglu.carpark.ui.view.hand;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ImageUtils;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkInOutHistory;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.widgets.Text;

public class HandSearchView extends Composite implements View{
	private Presenter presenter;
	SearchErrorCarModel model;
	private Table table;
	private Table table_1;
	private TableViewer tableViewer;
	private TableViewer tableViewer_1;
	private Label lbl_bigImg;
	boolean search=false;
	private Text txt_plate;
	public HandSearchView(Composite parent, int style,SearchErrorCarModel model) {
		super(parent, style);
		this.model=model;
		setLayout(new GridLayout(2, false));
		
		Composite composite_3 = new Composite(this, SWT.NONE);
		composite_3.setLayout(new GridLayout(3, false));
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label = new Label(composite_3, SWT.NONE);
		label.setText("车牌");
		
		txt_plate = new Text(composite_3, SWT.BORDER);
		GridData gd_txt_plate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txt_plate.widthHint = 108;
		txt_plate.setLayoutData(gd_txt_plate);
		
		Button button = new Button(composite_3, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setPlateNo(txt_plate.getText());
				getPresenter().search(true);
			}
		});
		button.setText("模糊查找");
		new Label(this, SWT.NONE);
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = tabFolder.getSelection()[0].getText();
				if (text.equals("无牌车")) {
					model.setInOrOut(false);
				}else{
					model.setInOrOut(true);
				}
				
			}
		});
		GridData gd_tabFolder = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_tabFolder.widthHint = 252;
		tabFolder.setLayoutData(gd_tabFolder);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("相似车");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_1);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				model.setNoPlateNoSelect(null);
					SingleCarparkInOutHistory select = model.getHavePlateNoSelect();
					lbl_bigImg.setImage(ImageUtils.getImage(ImageUtils.getImageByte(select.getBigImg()),lbl_bigImg, getShell()));
					model.setBigImg(ImageUtils.getImageByte(model.getSaveBigImg()));
					model.setSmallImg(ImageUtils.getImageByte(model.getSaveSmallImg()));
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(83);
		tableColumn.setText("车牌");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
		tableColumn_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search=!search;
				getPresenter().Order(search);
			}
		});
		tableColumn_1.setWidth(148);
		tableColumn_1.setText("进场时间");
		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("无牌车");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_2);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tableViewer_1 = new TableViewer(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_1 = tableViewer_1.getTable();
		table_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				model.setHavePlateNoSelect(null);
				SingleCarparkInOutHistory select = model.getNoPlateNoSelect();
				model.setBigImg(ImageUtils.getImageByte(model.getSaveBigImg()));
				model.setSmallImg(ImageUtils.getImageByte(model.getSaveSmallImg()));
				lbl_bigImg.setImage(ImageUtils.getImage(ImageUtils.getImageByte(select.getBigImg()),lbl_bigImg, getShell()));
			}
		});
		table_1.setLinesVisible(true);
		table_1.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer_1, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn_2.getColumn();
		tblclmnNewColumn.setAlignment(SWT.CENTER);
		tblclmnNewColumn.setWidth(206);
		tblclmnNewColumn.setText("进场时间");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		lbl_bigImg = new Label(composite, SWT.NONE);
		lbl_bigImg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lbl_bigImg.setText("大图片");
		initDataBindings();
	}
	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public HandSearchPresenter getPresenter() {
		return (HandSearchPresenter) presenter;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap[] observeMaps = BeansObservables.observeMaps(listContentProvider.getKnownElements(), SingleCarparkInOutHistory.class, new String[]{"plateNo", "inTimeLabel"});
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMaps));
		tableViewer.setContentProvider(listContentProvider);
		//
		IObservableList havePlateNoListModelObserveList = BeanProperties.list("havePlateNoList").observe(model);
		tableViewer.setInput(havePlateNoListModelObserveList);
		//
		ObservableListContentProvider listContentProvider_1 = new ObservableListContentProvider();
		IObservableMap observeMap = BeansObservables.observeMap(listContentProvider_1.getKnownElements(), SingleCarparkInOutHistory.class, "inTimeLabel");
		tableViewer_1.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		tableViewer_1.setContentProvider(listContentProvider_1);
		//
		IObservableList noPlateNoListModelObserveList = BeanProperties.list("noPlateNoList").observe(model);
		tableViewer_1.setInput(noPlateNoListModelObserveList);
		//
		IObservableValue observeSingleSelectionTableViewer = ViewerProperties.singleSelection().observe(tableViewer);
		IObservableValue havePlateNoSelectModelObserveValue = BeanProperties.value("havePlateNoSelect").observe(model);
		bindingContext.bindValue(observeSingleSelectionTableViewer, havePlateNoSelectModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionTableViewer_1 = ViewerProperties.singleSelection().observe(tableViewer_1);
		IObservableValue noPlateNoSelectModelObserveValue = BeanProperties.value("noPlateNoSelect").observe(model);
		bindingContext.bindValue(observeSingleSelectionTableViewer_1, noPlateNoSelectModelObserveValue, null, null);
		//
		IObservableValue observeTextTxt_plateObserveWidget = WidgetProperties.text(SWT.Modify).observe(txt_plate);
		IObservableValue plateNoModelObserveValue = BeanProperties.value("plateNo").observe(model);
		bindingContext.bindValue(observeTextTxt_plateObserveWidget, plateNoModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
