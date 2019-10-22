package com.donglu.carpark.ui.view.inouthistory.feecount;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractView;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.DateTime;

public class OnlineFeeCountViewer extends AbstractView {
	private Table table;

	public OnlineFeeCountViewer(Composite parent) {
		super(parent);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("网上统计");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(10, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_1.setText("时间");
		
		DateChooserCombo dateChooserCombo_start = new DateChooserCombo(composite_1, SWT.BORDER);
		dateChooserCombo_start.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateChooserCombo_start.setValue(StrUtil.getMonthTopTime(new Date()));
		
		DateTime dateTime_start = new DateTime(composite_1, SWT.BORDER | SWT.TIME | SWT.LONG);
		dateTime_start.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateTime_start.setTime(0, 0, 0);
		
		Label label_2 = new Label(composite_1, SWT.NONE);
		label_2.setText("-");
		
		DateChooserCombo dateChooserCombo_end = new DateChooserCombo(composite_1, SWT.BORDER);
		dateChooserCombo_end.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		
		DateTime dateTime_end = new DateTime(composite_1, SWT.BORDER | SWT.TIME | SWT.LONG);
		dateTime_end.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateTime_end.setTime(23, 59, 59);
		
		Label label_3 = new Label(composite_1, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("类型");
		
		Combo combo = new Combo(composite_1, SWT.NONE);
		combo.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		combo.setItems(new String[] {"日报表", "月报表", "年报表"});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo.select(0);
		
		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table.removeAll();
				try {
					getPresenter().feeCount(getTime(dateChooserCombo_start,dateTime_start),getTime(dateChooserCombo_end,dateTime_end),combo.getSelectionIndex());
					TableColumn[] columns = table.getColumns();
					for (TableColumn tableColumn : columns) {
						tableColumn.dispose();
					}
					List<List<Object>> tableData = getPresenter().getTableData();
					if (StrUtil.isEmpty(tableData)) {
						return;
					}
					List<Object> list2 = tableData.get(0);
					for (Object object : list2) {
						TableColumn column = new TableColumn(table, SWT.NONE);
						column.setText(String.valueOf(object));
						column.setWidth(100);
					}
					if (tableData.size()<2) {
						return;
					}
					for (int i = 1; i < tableData.size(); i++) {
						List<Object> list3 = tableData.get(i);
						TableItem item = new TableItem(table, SWT.NONE);
						List<String> collect = list3.stream().map(s->String.valueOf(s)).collect(Collectors.toList());
						item.setText(collect.toArray(new String[collect.size()]));
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button.setText("统计");
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().importData();
			}
		});
		button_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button_1.setText("导出");
		
		Composite composite_2 = new Composite(composite, SWT.NONE);
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		table = new Table(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		table.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	protected Date getTime(DateChooserCombo dateChooserCombo_start, DateTime dateTime) {
		return new org.joda.time.DateTime(dateChooserCombo_start.getValue()).withTime(dateTime.getHours(), dateTime.getMinutes()
				, dateTime.getSeconds(), 0).toDate();
	}
	@Override
	public OnlineFeeCountPresenter getPresenter() {
		return (OnlineFeeCountPresenter) super.getPresenter();
	}
	
}
