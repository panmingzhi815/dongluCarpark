package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ToolItem;

import com.dongluhitec.card.domain.db.DomainObject;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyUserPayHistory;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.databinding.viewers.ViewerProperties;

public abstract class AbstractListView<T> extends Composite{
	protected ListPresenter<T> presenter;
	private DataBindingContext m_bindingContext;
	private Table table;
	private Model model=new Model();
	private TableViewer tableViewer;
	
	String[] columnProperties;
	String[] nameProperties;
	int[] columnLenths;
	Class tClass;
	private Label lbl_tableTitle;
	private Label lbl_nowCount;
	private Label lbl_allcount;
	
	public class Model extends DomainObject{
		private List<T> list=new ArrayList<>();
		private List<T> selected=new ArrayList<>();
		Integer countSearch=0;
		Integer countSearchAll=0;
		public List<T> getList() {
			return list;
		}

		public void setList(List<T> list) {
			this.list = list;
			if (pcs != null)
				pcs.firePropertyChange("list", null, null);
		}

		public Integer getCountSearch() {
			return countSearch;
		}

		public void setCountSearch(Integer countSearch) {
			this.countSearch = countSearch;
			if (pcs != null)
				pcs.firePropertyChange("countSearch", null, null);
		}

		public Integer getCountSearchAll() {
			return countSearchAll;
		}

		public void setCountSearchAll(Integer countSearchAll) {
			this.countSearchAll = countSearchAll;
			if (pcs != null)
				pcs.firePropertyChange("countSearchAll", null, null);
		}

		public void AddList(List<T> list2) {
			this.list.addAll(list2);
			if (pcs != null)
				pcs.firePropertyChange("list", null, null);
			
		}

		public List<T> getSelected() {
			return selected;
		}

		public void setSelected(List<T> selected) {
			this.selected = selected;
			if (pcs != null)
				pcs.firePropertyChange("selected", null, null);
		}
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public AbstractListView(Composite parent, int style,Class<T> tClass,String[] columnProperties,String[] nameProperties,int[] columnLenths) {
		super(parent, style);
//		,Class<T> tClass,String[] columnProperties,String[] nameProperties,int[] columnLenths
		this.tClass=tClass;
		this.columnProperties=columnProperties;
		this.nameProperties=nameProperties;
		this.columnLenths=columnLenths;
		setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		composite.setLayout(new GridLayout(3, false));
		
		lbl_tableTitle = new Label(composite, SWT.NONE);
		lbl_tableTitle.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lbl_tableTitle.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lbl_tableTitle.setText("");
		
		ToolBar toolBar_menu = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		toolBar_menu.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		createMenuBarToolItem(toolBar_menu);
		
		ToolBar toolBar_refresh = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		
		createRefreshBarToolItem(toolBar_refresh);
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table = tableViewer.getTable();
		table.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				presenter.mouseDoubleClick(model.selected);
			}
		});
		
		for (int i = 0; i < columnProperties.length; i++) {
			
			TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
			tblclmnNewColumn.setWidth(columnLenths[i]);
			tblclmnNewColumn.setText(nameProperties[i]);
			
		}
		Composite composite_2 = new Composite(this, SWT.NONE);
		composite_2.setLayout(new GridLayout(4, false));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		lbl_nowCount = new Label(composite_2, SWT.NONE);
		lbl_nowCount.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lbl_nowCount.setAlignment(SWT.RIGHT);
		GridData gd_lbl_nowCount = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_nowCount.widthHint = 56;
		lbl_nowCount.setLayoutData(gd_lbl_nowCount);
		lbl_nowCount.setText("0000000");
		
		Label lblNewLabel_2 = new Label(composite_2, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_2.setText("/");
		
		lbl_allcount = new Label(composite_2, SWT.NONE);
		lbl_allcount.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_lbl_allcount = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_allcount.widthHint = 56;
		lbl_allcount.setLayoutData(gd_lbl_allcount);
		lbl_allcount.setText("0000000");
		
		
		Button btn_more = new Button(composite_2, SWT.NONE);
		btn_more.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btn_more.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchMore();
			}
		});
		btn_more.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btn_more.setText("更多");
		m_bindingContext = initDataBindings();
	}

	protected void createMenuBarToolItem(ToolBar toolBar_menu) {
		ToolItem toolItem_add = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.add();
			}
		});
		toolItem_add.setText("添加");
		
		ToolItem toolItem_delete = new ToolItem(toolBar_menu, SWT.NONE);
		toolItem_delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.delete(model.getSelected());
			}
		});
		toolItem_delete.setText("删除");
	}

	protected void createRefreshBarToolItem(ToolBar toolBar_refresh) {
		ToolItem tltm_refresh = new ToolItem(toolBar_refresh, SWT.NONE);
		tltm_refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				presenter.refresh();
			}
		});
		tltm_refresh.setText("刷新");
	}
	
	protected void searchMore(){}
	protected void refresh(){}
	protected void add(){}
	protected void delete(){}
	
	public Model getModel() {
		return model;
	}

	public void setTableTitle(String tableTitle) {
		lbl_tableTitle.setText(tableTitle);
	}
	public void  sss(){
		
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextLbl_nowCountObserveWidget = WidgetProperties.text().observe(lbl_nowCount);
		IObservableValue countSearchModelObserveValue = BeanProperties.value("countSearch").observe(model);
		bindingContext.bindValue(observeTextLbl_nowCountObserveWidget, countSearchModelObserveValue, null, null);
		//
		IObservableValue observeTextLbl_allcountObserveWidget = WidgetProperties.text().observe(lbl_allcount);
		IObservableValue countSearchAllModelObserveValue = BeanProperties.value("countSearchAll").observe(model);
		bindingContext.bindValue(observeTextLbl_allcountObserveWidget, countSearchAllModelObserveValue, null, null);
		//
		IObservableList observeMultiSelectionTableViewer = ViewerProperties.multipleSelection().observe(tableViewer);
		IObservableList selectedModelObserveList = BeanProperties.list("selected").observe(model);
		bindingContext.bindList(observeMultiSelectionTableViewer, selectedModelObserveList, null, null);
		//
		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
		IObservableMap[] observeMap = BeansObservables.observeMaps(listContentProvider.getKnownElements(), tClass,columnProperties);
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
		tableViewer.setContentProvider(listContentProvider);
		//
		IObservableList listModelObserveList = BeanProperties.list("list").observe(model);
		tableViewer.setInput(listModelObserveList);
		return bindingContext;
	}

	public Presenter getPresenter() {
		return presenter;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = (ListPresenter<T>) presenter;
	}

	public String[] getColumnProperties() {
		return columnProperties;
	}

	public String[] getNameProperties() {
		return nameProperties;
	}
}
