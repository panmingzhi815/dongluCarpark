package com.donglu.carpark.ui.view.carpark;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.wb.rcp.databinding.BeansListObservableFactory;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import org.eclipse.wb.rcp.databinding.TreeBeanAdvisor;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.wb.rcp.databinding.TreeObservableLabelProvider;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewerProperties;

public class CarparkView extends Composite implements View{
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	private Presenter presenter;
	private TreeViewer treeViewer;
	private CarparkModel carparkModel;
	private Composite listComsite;

	public CarparkView(Composite parent, int style,CarparkModel carparkModel) {
		super(parent, style);
		this.carparkModel=carparkModel;
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		
		Composite composite_1 = new Composite(sashForm, SWT.BORDER);
		composite_1.setLayout(new GridLayout(2, false));
		
		Label label = new Label(composite_1, SWT.NONE);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.widthHint = 88;
		label.setLayoutData(gd_label);
		label.setText("停车场设置");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		ToolBar toolBar = new ToolBar(composite_1, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().addCarpark();
			}
		});
		toolItem.setText("添加主停车场");
		
		ToolItem toolItem_1 = new ToolItem(toolBar, SWT.NONE);
		toolItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().addChildCapark();
			}
		});
		toolItem_1.setText("添加子停车场");
		
		ToolItem toolItem_2 = new ToolItem(toolBar, SWT.NONE);
		toolItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().deleteCarpark();
			}
		});
		toolItem_2.setText("删除");
		
		ToolItem toolItem_3 = new ToolItem(toolBar, SWT.NONE);
		toolItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().editCarpark();
			}
		});
		toolItem_3.setText("修改");
		
		ToolItem toolItem_4 = new ToolItem(toolBar, SWT.NONE);
		toolItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().refreshCarpark();
			}
		});
		toolItem_4.setText("刷新");
		
		treeViewer = new TreeViewer(composite_1, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().refreshCharges();
			}
		});
		tree.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		listComsite = new Composite(sashForm, SWT.NONE);
		listComsite.setLayout(new FillLayout(SWT.HORIZONTAL));
		sashForm.setWeights(new int[] {1, 1});
		m_bindingContext = initDataBindings();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public CarparkPresenter getPresenter() {
		return (CarparkPresenter) presenter;
	}

	public void expandAllCarpark() {
		if(getShell() == null || getShell().isDisposed()){
			return;
		}
		getShell().getDisplay().asyncExec(()->{
			treeViewer.expandAll();
		});
		
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		BeansListObservableFactory treeObservableFactory = new BeansListObservableFactory(SingleCarparkCarpark.class, "childs");
		TreeBeanAdvisor treeAdvisor = new TreeBeanAdvisor(SingleCarparkCarpark.class, "parent", "childs", null);
		ObservableListTreeContentProvider treeContentProvider = new ObservableListTreeContentProvider(treeObservableFactory, treeAdvisor);
		treeViewer.setLabelProvider(new TreeObservableLabelProvider(treeContentProvider.getKnownElements(), SingleCarparkCarpark.class, "labelString", null));
		treeViewer.setContentProvider(treeContentProvider);
		//
		IObservableList listCarparkCarparkModelObserveList = BeanProperties.list("listCarpark").observe(carparkModel);
		treeViewer.setInput(listCarparkCarparkModelObserveList);
		//
		IObservableValue observeSingleSelectionTreeViewer = ViewerProperties.singleSelection().observe(treeViewer);
		IObservableValue carparkCarparkModelObserveValue = BeanProperties.value("carpark").observe(carparkModel);
		bindingContext.bindValue(observeSingleSelectionTreeViewer, carparkCarparkModelObserveValue, null, null);
		//
		return bindingContext;
	}

	public Composite getListComsite() {
		return listComsite;
	}
}
