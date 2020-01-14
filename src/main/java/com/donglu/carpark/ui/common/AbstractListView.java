package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ToolItem;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.DomainObject;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.beans.BeansObservables;

import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.databinding.viewers.ViewerProperties;

public abstract class AbstractListView<T> extends AbstractView {
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			Object fieldValueByName = CarparkUtils.getFieldValueByName(columnProperties[columnIndex], element);
			return fieldValueByName==null?"":fieldValueByName.toString();
		}
	}
	protected ListPresenter<T> presenter;
	private Table table;
	private Model model = new Model();
	private TableViewer tableViewer;

	String[] columnProperties;
	String[] nameProperties;
	int[] columnLenths;
	int[] aligns;
	Class<T> tClass;
	private Label lbl_tableTitle;
	private Label lbl_nowCount;
	private Label lbl_allcount;
	private Composite cmp_bottom;
	private TableViewerColumn[] tableViewerColumns;
	private DataBindingContext bindingContext;
	private Composite composite_tableViewer;
	private ViewerCell selectedCell;

	public class Model extends DomainObject {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6950475725572819624L;
		private List<T> list = new ArrayList<>();
		private List<T> selected = new ArrayList<>();
		Integer countSearch = 0;
		Integer countSearchAll = 0;

		public List<T> getList() {
			return list;
		}

		public void setList(List<T> list) {
			clearSort();
			this.list.clear();
			this.list.addAll(list);
			setCountSearch(this.list.size());
			fillTable();
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
			fillTable();

		}

		public List<T> getSelected() {
			return selected;
		}

		public void setSelected(List<T> selected) {
			this.selected = selected;
		}
	}
	
	public AbstractListView(Composite parent, int style, Class<T> tClass, String[] columnProperties, String[] nameProperties, int[] columnLenths, int[] aligns) {
		// ,Class<T> tClass,String[] columnProperties,String[] nameProperties,int[] columnLenths, int[] aligns
		this(parent, style);
		this.tClass = tClass;
		this.columnProperties = columnProperties;
		this.nameProperties = nameProperties;
		this.columnLenths = columnLenths;
		this.aligns = aligns;
		
		bindingContext = initDataBindings();
		createTable();
	}
	public void fillTable() {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				tableViewer.setInput(model.list);
				tableViewer.setSelection(new StructuredSelection(model.selected));
			}
		});
		
	}
	public void setTableColumn(String[] columnProperties, String[] nameProperties, int[] columnLenths, int[] aligns){
		this.columnProperties = columnProperties;
		this.nameProperties = nameProperties;
		this.columnLenths = columnLenths;
		this.aligns = aligns;
	}
	/**
	 *创建表格
	 */
	public void createTable() {
		if(tableViewer!=null){
			tableViewer.getTable().dispose();
		}
		tableViewer = new TableViewer(composite_tableViewer, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				try {
					IStructuredSelection selection = (IStructuredSelection) arg0.getSelection();
					if (selection.isEmpty()) {
						return;
					}
					List list = selection.toList();
					model.setSelected(list);
//					getPresenter().selected(list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
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
		Menu menu = new Menu(table);
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText("复制单元格内容");
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedCell!=null) {
					String text = selectedCell.getText();
					if (StrUtil.isEmpty(text)) {
						return;
					}
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
				}
			}
		});
		MenuItem menuItem_copyRow = new MenuItem(menu, SWT.NONE);
		menuItem_copyRow.setText("复制整行内容");
		menuItem_copyRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (selectedCell!=null) {
					int columnCount = selectedCell.getViewerRow().getColumnCount();
					StringBuffer sb=new StringBuffer();
					for (int i = 0; i < columnCount; i++) {
						ViewerCell cell = selectedCell.getViewerRow().getCell(i);
						sb.append(cell.getText());
						sb.append("\t");
					}
					String text = sb.toString();
					if (StrUtil.isEmpty(text)) {
						return;
					}
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
				}
			}
		});
		table.setMenu(menu);
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode==127) {
					presenter.delete(model.getSelected());
				}
			}
		});
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				selectedCell = tableViewer.getCell(new Point(e.x, e.y));
				menuItem_copyRow.setEnabled(selectedCell!=null&&!StrUtil.isEmpty(selectedCell.getText()));
			}
		});
		tableViewerColumns = new TableViewerColumn[columnProperties.length];
		for (int i = 0; i < columnProperties.length; i++) {
			TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			tableViewerColumns[i] = tableViewerColumn;
			TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
			tblclmnNewColumn.setWidth(columnLenths[i]);
			tblclmnNewColumn.setText(nameProperties[i]);
			if (aligns != null) {
				int j = aligns[i];
				if (j != 0) {
					tblclmnNewColumn.setAlignment(j);
				}
			}
			int num = i;
			tblclmnNewColumn.addSelectionListener(new SelectionAdapter() {
				boolean flag = false;
				@Override
				public void widgetSelected(SelectionEvent e) {
//					model.setList(CarparkUtils.sortObjectPropety(model.getList(), columnProperties[num], flag));
					try {
						tableViewer.setSorter(new TableSort(flag, columnProperties[num]));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					flag = !flag;
				}

			});
		}
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
//		initTableBind();
	}
	public void clearSort(){
		getDisplay().syncExec(new Runnable() {
			public void run() {
				tableViewer.setSorter(null);
			}
		});
	}
	/**
	 * @param parent
	 * @param style
	 * @param aligns
	 */
	private AbstractListView(Composite parent, int style) {
		super(parent, style);
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

		composite_tableViewer = new Composite(this, SWT.NONE);
		composite_tableViewer.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_tableViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		cmp_bottom = new Composite(this, SWT.NONE);
		cmp_bottom.setLayout(new GridLayout(5, false));
		cmp_bottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		lbl_nowCount = new Label(cmp_bottom, SWT.NONE);
		lbl_nowCount.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lbl_nowCount.setAlignment(SWT.RIGHT);
		GridData gd_lbl_nowCount = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_nowCount.widthHint = 66;
		lbl_nowCount.setLayoutData(gd_lbl_nowCount);
		lbl_nowCount.setText("0000000");

		Label lblNewLabel_2 = new Label(cmp_bottom, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_2.setText("/");

		lbl_allcount = new Label(cmp_bottom, SWT.NONE);
		lbl_allcount.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_lbl_allcount = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lbl_allcount.widthHint = 64;
		lbl_allcount.setLayoutData(gd_lbl_allcount);
		lbl_allcount.setText("0000000");
		Composite composite_3 = new Composite(cmp_bottom, SWT.NONE);
		composite_3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		createBottomComposite(composite_3);

		// Composite composite_4 = new Composite(composite_3, SWT.NONE);
		// composite_4.setLayout(new GridLayout(1, false));

		Button btn_more = new Button(cmp_bottom, SWT.NONE);
		btn_more.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btn_more.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				searchMore();
			}
		});
		btn_more.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btn_more.setText("更多");
	}

	public AbstractListView(Composite parent, int style, Class<T> class1, String[] columns, String[] strings2, int[] is) {
		this(parent, style, class1, columns, strings2, is, null);
	}

	public void setColumnWidth(int[] widths) {
		for (int i = 0; i < tableViewerColumns.length; i++) {
			TableViewerColumn j = tableViewerColumns[i];
			j.getColumn().setWidth(widths[i]);
		}
	}

	public void setShowMoreBtn(boolean show) {
		GridData layoutData2 = (GridData) cmp_bottom.getLayoutData();
		layoutData2.exclude = !show;
		cmp_bottom.setLayoutData(layoutData2);
		cmp_bottom.getParent().layout();
	}
	public void setShow(boolean top){
		Composite parent = lbl_tableTitle.getParent();
		GridData layoutData2 = (GridData) parent.getLayoutData();
		layoutData2.exclude = !top;
		parent.setLayoutData(layoutData2);
		parent.setVisible(top);
		parent.getParent().layout();
		
	}

	protected void createBottomComposite(Composite parent) {
		Composite composite_4 = new Composite(parent, SWT.NONE);
		composite_4.setLayout(new GridLayout(1, false));
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
				model.setSelected(new ArrayList<>());
				presenter.refresh();
			}
		});
		tltm_refresh.setText("刷新");
	}

	protected void searchMore() {
		getPresenter().loadMore();
	}

	protected void refresh() {
	}

	protected void add() {
	}

	protected void delete() {
	}

	public Model getModel() {
		return model;
	}

	public void setTableTitle(String tableTitle) {
		lbl_tableTitle.setText(tableTitle);
	}

	public void sss() {

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
		return bindingContext;
	}
	/**
	 * @param bindingContext
	 */
	public void initTableBind() {
//		IObservableList observeMultiSelectionTableViewer = ViewerProperties.multipleSelection().observe(tableViewer);
//		IObservableList selectedModelObserveList = BeanProperties.list("selected").observe(model);
//		bindingContext.bindList(observeMultiSelectionTableViewer, selectedModelObserveList, null, null);
//		//
//		ObservableListContentProvider listContentProvider = new ObservableListContentProvider();
//		IObservableMap[] observeMap = BeansObservables.observeMaps(listContentProvider.getKnownElements(), tClass, columnProperties);
//		tableViewer.setLabelProvider(new ObservableMapLabelProvider(observeMap));
//		tableViewer.setContentProvider(listContentProvider);
//		//
//		IObservableList listModelObserveList = BeanProperties.list("list").observe(model);
//		tableViewer.setInput(listModelObserveList);
	}

	public AbstractListPresenter<T> getPresenter() {
		return (AbstractListPresenter<T>) presenter;
	}

	@SuppressWarnings("unchecked")
	public void setPresenter(Presenter presenter) {
		this.presenter = (ListPresenter<T>) presenter;
	}

	public String[] getColumnProperties() {
		return columnProperties;
	}

	public String[] getNameProperties() {
		return nameProperties;
	}
	public TableViewer getTableViewer() {
		return tableViewer;
	}
}
