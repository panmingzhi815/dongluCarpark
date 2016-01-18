package com.donglu.carpark.ui.view.carpark;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.ToolItem;

public class CarparkView extends Composite implements View{
	private Presenter presenter;
	private Table table;

	public CarparkView(Composite parent, int style) {
		super(parent, style);
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
		toolItem.setText("添加主停车场");
		
		ToolItem toolItem_1 = new ToolItem(toolBar, SWT.NONE);
		toolItem_1.setText("添加子停车场");
		
		ToolItem toolItem_2 = new ToolItem(toolBar, SWT.NONE);
		toolItem_2.setText("删除");
		
		ToolItem toolItem_3 = new ToolItem(toolBar, SWT.NONE);
		toolItem_3.setText("修改");
		
		ToolItem toolItem_4 = new ToolItem(toolBar, SWT.NONE);
		toolItem_4.setText("刷新");
		
		TreeViewer treeViewer = new TreeViewer(composite_1, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Composite composite_2 = new Composite(sashForm, SWT.NONE);
		composite_2.setLayout(new GridLayout(2, false));
		
		Label label_1 = new Label(composite_2, SWT.NONE);
		label_1.setText("收费设置");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		ToolBar toolBar_1 = new ToolBar(composite_2, SWT.FLAT | SWT.RIGHT);
		toolBar_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		ToolItem toolItem_5 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_5.setToolTipText("添加临时收费设置");
		toolItem_5.setText("添加临时收费设置");
		
		ToolItem toolItem_6 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_6.setToolTipText("添加固定收费设置");
		toolItem_6.setText("添加固定收费设置");
		
		ToolItem toolItem_7 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_7.setText("删除");
		
		ToolItem toolItem_8 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_8.setToolTipText("修改");
		toolItem_8.setText("修改");
		
		ToolItem toolItem_9 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_9.setToolTipText("启用临时收费设置");
		toolItem_9.setText("启用");
		
		ToolItem toolItem_10 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_10.setToolTipText("禁用停车场收费设置");
		toolItem_10.setText("禁用");
		
		ToolItem toolItem_11 = new ToolItem(toolBar_1, SWT.NONE);
		toolItem_11.setText("刷新");
		
		TableViewer tableViewer = new TableViewer(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.RIGHT);
		TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setWidth(101);
		tableColumn.setText("编码");
		tableColumn.setResizable(false);
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
		tableColumn_1.setWidth(150);
		tableColumn_1.setText("名称");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_2 = tableViewerColumn_2.getColumn();
		tableColumn_2.setWidth(114);
		tableColumn_2.setText("收费类型");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_3 = tableViewerColumn_3.getColumn();
		tableColumn_3.setWidth(100);
		tableColumn_3.setText("车辆类型");
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_4 = tableViewerColumn_4.getColumn();
		tableColumn_4.setWidth(100);
		tableColumn_4.setText("节假日类型");
		
		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tableColumn_5 = tableViewerColumn_5.getColumn();
		tableColumn_5.setWidth(100);
		tableColumn_5.setText("是否启用");
		sashForm.setWeights(new int[] {1, 1, 1, 1});
		Font font = SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public CarparkPresenter getPresenter() {
		return (CarparkPresenter) presenter;
	}
}
