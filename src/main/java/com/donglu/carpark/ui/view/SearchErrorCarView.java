package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.model.SearchErrorCarModel;
import com.donglu.carpark.server.imgserver.FileuploadSend;
import com.donglu.carpark.ui.CarparkClientConfig;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.io.Files;

import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
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
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;

public class SearchErrorCarView extends Composite implements View{
	private DataBindingContext m_bindingContext;
	private Presenter presenter;
	SearchErrorCarModel model;
	Map<SystemSettingTypeEnum, String> mapSystemSetting;
	private Table table;
	private Table table_1;
	private TableViewer tableViewer;
	private TableViewer tableViewer_1;
	private Label lbl_bigImg;
	private Label lblbbbbbb;
	boolean search=false;
	private Button button;
	public SearchErrorCarView(Composite parent, int style,SearchErrorCarModel model) {
		super(parent, style);
		this.model=model;
		setLayout(new GridLayout(2, false));
		
		lblbbbbbb = new Label(this, SWT.NONE);
		lblbbbbbb.setText("粤BBBBBB");
		
		button = new Button(this, SWT.CHECK);
		button.setText("使用进场车牌为准");
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = tabFolder.getSelection()[0].getText();
				if (text.equals("相似车")) {
					model.setNoPlateNoSelect(null);
				}else{
					model.setHavePlateNoSelect(null);
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
				if (StrUtil.isEmpty(model.getHavePlateNoSelect())) {
					SingleCarparkInOutHistory select = model.getNoPlateNoSelect();
					model.setBigImg(getByte(model.getSaveBigImg()));
					model.setSmallImg(getByte(model.getSaveSmallImg()));
					lbl_bigImg.setImage(getImage(getByte(select.getBigImg()),lbl_bigImg));
				}else{
					SingleCarparkInOutHistory select = model.getHavePlateNoSelect();
					lbl_bigImg.setImage(getImage(getByte(select.getBigImg()),lbl_bigImg));
					model.setBigImg(getByte(model.getSaveBigImg()));
					model.setSmallImg(getByte(model.getSaveSmallImg()));
				}
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
				getPresenter().search(search);
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
		Font font = SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL);
		m_bindingContext = initDataBindings();
	}

	protected byte[] getByte(String img) {
		try {
			byte[] image;
			File file=new File(mapSystemSetting.get(SystemSettingTypeEnum.图片保存位置)+"/img/"+img);
			if (file.exists()) {
				image=Files.toByteArray(file);
			}else{
				String substring = img.substring(img.lastIndexOf("/")+1);
				String actionUrl = "http://"+CarparkClientConfig.getInstance().getDbServerIp()+":8899";
				image = FileuploadSend.download(actionUrl, substring);
			}
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected Image getImage(byte[] image, Label lbl) {
		if (image==null) {
			return null;
		}
		ByteArrayInputStream stream = null;
		try {
			stream = new ByteArrayInputStream(image);
			Image newImg = new Image(getShell().getDisplay(), stream);
			Rectangle rectangle = lbl.getBounds();
			ImageData data = newImg.getImageData().scaledTo(rectangle.width, rectangle.height);
			ImageDescriptor createFromImageData = ImageDescriptor.createFromImageData(data);
			Image createImg = createFromImageData.createImage();
			newImg.dispose();
			newImg = null;
			lbl.setText("");
			return createImg;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			if (stream!=null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public SearchErrorCarPresenter getPresenter() {
		return (SearchErrorCarPresenter) presenter;
	}
	public void setSystemSetting(Map<SystemSettingTypeEnum, String> mapSystemSetting) {
		this.mapSystemSetting=mapSystemSetting;
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
		IObservableValue observeTextLabelObserveWidget = WidgetProperties.text().observe(lblbbbbbb);
		IObservableValue plateNoModelObserveValue = BeanProperties.value("plateNo").observe(model);
		bindingContext.bindValue(observeTextLabelObserveWidget, plateNoModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButtonObserveWidget = WidgetProperties.selection().observe(button);
		IObservableValue inOrOutModelObserveValue = BeanProperties.value("inOrOut").observe(model);
		bindingContext.bindValue(observeSelectionButtonObserveWidget, inOrOutModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
