package com.donglu.carpark.ui.view.inouthistory.feecount;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractView;
import com.donglu.carpark.ui.common.Presenter;
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
import org.eclipse.swt.widgets.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FeeCountViewer extends AbstractView {
	private Text text;
	private Table table;

	public FeeCountViewer(Composite parent) {
		super(parent);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("收费员统计");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayout(new GridLayout(10, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label = new Label(composite_1, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label.setText("用户名");
		
		text = new Text(composite_1, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		if (ConstUtil.getLevel()==SystemUserTypeEnum.操作员.getLevel()) {
			text.setText(ConstUtil.getUserName());
			text.setEditable(false);
		}
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setText("时间");
		
		DateChooserCombo dateChooserCombo_start = new DateChooserCombo(composite_1, SWT.BORDER);
		dateChooserCombo_start.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		dateChooserCombo_start.setValue(StrUtil.getMonthTopTime(new Date()));
		
		Label label_2 = new Label(composite_1, SWT.NONE);
		label_2.setText("-");
		
		DateChooserCombo dateChooserCombo_end = new DateChooserCombo(composite_1, SWT.BORDER);
		dateChooserCombo_end.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		
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
					List<Object[]> list = getPresenter().feeCount(dateChooserCombo_start.getValue(),dateChooserCombo_end.getValue(),combo.getSelectionIndex(),text.getText());
					TableColumn[] columns = table.getColumns();
					for (TableColumn tableColumn : columns) {
						tableColumn.dispose();
					}
					Map<String,Map<String,Double[]>> map=new HashMap<>();
					Set<String> setName=new HashSet<>();
					for (Object[] objects : list) {
						if (objects[0]==null) {
							continue;
						}
						String name = String.valueOf(objects[0]);
						String date = String.valueOf(objects[1]);
						Double should=objects[2]==null?0:(Double) objects[2];
						Double fact=objects[3]==null?0:(Double) objects[3];
						Double online=objects[4]==null?0:(Double) objects[4];
						Double free=objects[5]==null?0:(Double) objects[5];
						Map<String,Double[]> mapMoney=map.getOrDefault(date, new HashMap<>());
						mapMoney.put(name, new Double[] {should,fact-online,online,free});
						map.put(date, mapMoney);
						setName.add(name);
					}
					List<String> listName=new ArrayList<>(setName);
					ArrayList<String> timelist = new ArrayList<>(map.keySet());
					timelist.sort((s1,s2)->s1.compareTo(s2));
					listName.sort((s1,s2)->s1.compareTo(s2));
					TableColumn column = new TableColumn(table, SWT.NONE);
					column.setText("时间");
					column.setWidth(100);
					for (String name : listName) {
						TableColumn column1 = new TableColumn(table, SWT.NONE);
						column1.setWidth(100);
						column1.setText(name+"现金");
						Font font2 = table.getFont();
						TableColumn column2 = new TableColumn(table, SWT.NONE);
						column2.setWidth(100);
						column2.setText(name+"网上");
					}
					TableColumn columnheji = new TableColumn(table, SWT.NONE);
					columnheji.setText("现金合计");
					columnheji.setWidth(100);
					TableColumn columnwheji = new TableColumn(table, SWT.NONE);
					columnwheji.setText("网上合计");
					columnwheji.setWidth(100);
					TableColumn columntheji = new TableColumn(table, SWT.NONE);
					columntheji.setText("合计");
					columntheji.setWidth(100);
					
					Map<String,Double[]> mapTotal=new HashMap<>();
					double total=0;
					double total1=0;
					for (String string : timelist) {
						Map<String, Double[]> map2 = map.get(string);
						TableItem item = new TableItem(table, 0);
						int index=0;
						item.setText(index++, string);
						double utotal=0;
						double wtotal=0;
						for (String string2 : listName) {
							Double[] dt=mapTotal.getOrDefault(string2, new Double[2] );
							double factTotal=dt[0]==null?0:dt[0];
							double onlineTotel=dt[1]==null?0:dt[1];
							Double[] doubles = map2.get(string2);
							if (doubles==null) {
								doubles=new Double[] {0d,0d,0d,0d};
							}
							factTotal+=doubles[1];
							onlineTotel+=doubles[2];
							utotal+=doubles[1];
							wtotal+=doubles[2];
							mapTotal.put(string2, new Double[] {factTotal,onlineTotel});
							item.setText(index++, String.valueOf(doubles[1]));
							item.setText(index++, String.valueOf(doubles[2]));
						}
						total+=utotal;
						total1+=wtotal;
						item.setText(index++, String.valueOf(utotal));
						item.setText(index++, String.valueOf(wtotal));
						item.setText(index++, String.valueOf(utotal+wtotal));
					}
					TableItem item = new TableItem(table, 0);
					int index=0;
					item.setText(index++, "总计");
					for (String string2 : listName) {
						Double[] dt=mapTotal.getOrDefault(string2, new Double[2] );
						double factTotal=dt[0]==null?0:dt[0];
						double onlineTotel=dt[1]==null?0:dt[1];
						item.setText(index++, String.valueOf(factTotal));
						item.setText(index++, String.valueOf(onlineTotel));
					}
					item.setText(index++, String.valueOf(total));
					item.setText(index++, String.valueOf(total1));
					item.setText(index++, String.valueOf(total+total1));
					table.layout();
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
	@Override
	public FeeCountPresenter getPresenter() {
		return (FeeCountPresenter) super.getPresenter();
	}
}
