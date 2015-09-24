package com.donglu.carpark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * 测试TableViewer
 * @description {description}
 * @className TableViewTest
 * @author share
 * @date 2012-5-7 上午11:36:55
 */
public class TableViewerTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new TableViewerTest().creatTable();
	}
	
	private static Display display = new Display();
	private static Shell shell = new Shell(display);
	
	//tableViewer数据源
	private  List<Person> list = new ArrayList<Person>();
	private Table table;
	
	/**
	 * 创建一个TableViewer对象-->设置数据源/设置内容提供者/设置Table列标签内容提供者
	 *     -->为TableViewer对象的Table设置没一个列名
	 * 创建一个线程去读取Person对象提供给Table
	 * 创建一个模拟线程产生Person对象并添加到数据源中
	 */
	public void creatTable(){
		shell.setText("Test TableViewer");
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		shell.setLayout(tableColumnLayout);
		
		TableViewer tv = new TableViewer(shell,SWT.FULL_SELECTION);
		tv.setContentProvider(new TvContentProvider());
		tv.setInput(list);
		
		table = tv.getTable();
		table.setHeaderVisible(true);//设置表格头部是否可见
		table.setLinesVisible(true);//设置线条是否可见
		
		//设置Table的列
		TableColumn tcId = new TableColumn(table, SWT.NONE);
		tableColumnLayout.setColumnData(tcId, new ColumnPixelData(150,true,true));
		tcId.setText("id");
		
		TableColumn tcName = new TableColumn(table, SWT.NONE);
		tableColumnLayout.setColumnData(tcName, new ColumnPixelData(150,true,true));
		tcName.setText("名字");
		
		TableColumn tcSex = new TableColumn(table, SWT.NONE);
		tableColumnLayout.setColumnData(tcSex, new ColumnPixelData(150,true,true));
		tcSex.setText("性别");
		
		/**
		 * 给Table设置数据,模拟1秒添加一条
		 */
		new Thread(new Runnable() {
			final Display display = Display.getDefault();
			
			public void run() {
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					display.asyncExec(new Runnable() {
						public void run() {
							if(!list.isEmpty()){
								Person p = list.remove(0);
								//设置表格数据
								TableItem item = new TableItem(table, SWT.None);
								item.setText(p.toArray());
							}
						}
					});
				}
			}
		}).start();
		
		/**
		 * 模拟生产数据
		 */
		new Thread(new Runnable() {
			
			public void run() {
				while(true){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Person p = new Person();
					int id = new Random().nextInt(1000);
					p.setId(id+"");
					p.setName("我是"+id);
					p.setSex(id%2==0?"男":"女");
					list.add(p);
				}
			}
		}).start();
		
		shell.pack();
		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		
	}
	
	/***
	 * TableViewer内容提供者/就是为TableViewer提供数据集合
	 * @description {description}
	 * @className TvContentProvider
	 * @author Share
	 * @date 2012-5-7 上午11:33:17
	 */
	class TvContentProvider implements IStructuredContentProvider{

		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getElements(Object inputElement) {
			System.out.println(inputElement);
			if(inputElement instanceof List){
				return ((List) inputElement).toArray();
			}
			return new Object[0];
		}
		
	}
	
	/**
	 * Person entity对象
	 * @description {description}
	 * @className Person
	 * @author share
	 * @date 2012-5-7 上午11:36:29
	 */
	class Person {
		private String id;
		private String name;
		private String sex;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		public String getSex() {
			return sex;
		}
		public void setSex(String sex) {
			this.sex = sex;
		}
		
		public String[] toArray() {
			// TODO Auto-generated method stub
			return new String[]{this.id,this.name,this.sex};
		}
		
	}
}
