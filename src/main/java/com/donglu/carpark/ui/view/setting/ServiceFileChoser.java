package com.donglu.carpark.ui.view.setting;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;

import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.SettingService;
import com.donglu.carpark.ui.Login;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.graphics.Image;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;

public class ServiceFileChoser extends Dialog {
	private CarparkDatabaseServiceProvider sp;
	SettingService settingService;
	private CommonUIFacility commonui;
	
	private class TreeContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			List<Object> list=(List<Object>) inputElement;
			return list.toArray();
		}
		public Object[] getChildren(Object parentElement) {
			if (parentElement.getClass().equals(String.class)) {
				List<File> serverChildFiles = settingService.getServerChildFiles(null);
				Object[] array = serverChildFiles.toArray();
				return array;
			}
			else if(parentElement.getClass().equals(File.class)){
				File file = (File) parentElement;
				List<File> serverChildFiles = settingService.getServerChildFiles(file.toString());
				return serverChildFiles.toArray();
			}
			return new Object[0];
		}
		public Object getParent(Object element) {
			return null;
		}
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}
	private class ViewerLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			if (element instanceof File) {
				File f=(File) element;
				if (f.isDirectory()) {
					return JFaceUtil.getImage("addRoot_16");
				}
			}else if(element instanceof String){
				return JFaceUtil.getImage("consumption_control_close_16");
			}
			return super.getImage(element);
		}
		public String getText(Object element) {
			String text = super.getText(element);
			int lastIndexOf = text.lastIndexOf("\\");
			if (text.length()>3) {
				return text.substring(lastIndexOf+1);
			}
			return text;
		}
	}

	protected String result;
	protected Shell shell;
	private Text txtCarparkbak;
	private TreeViewer treeViewer;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ServiceFileChoser(Shell parent, int style) {
		super(parent, style);
		sp=Login.injector.getInstance(CarparkDatabaseServiceProvider.class);
		settingService=sp.getSettingService();
		commonui=Login.injector.getInstance(CommonUIFacility.class);
		setText("选择文件");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public String open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 300);
		shell.setText("文件选择");
		shell.setLayout(new GridLayout(1, false));
		
		treeViewer = new TreeViewer(shell, SWT.BORDER);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					TreeSelection selection = (TreeSelection) treeViewer.getSelection();
					Object firstElement = selection.getFirstElement();
					if (firstElement instanceof File) {
						File f=(File) firstElement;
						if (!f.isDirectory()) {
							txtCarparkbak.setText(f.toString().substring(f.toString().lastIndexOf("\\")+1));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new ViewerLabelProvider());
		treeViewer.setInput(Arrays.asList("计算机"));
		
		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(4, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		txtCarparkbak = new Text(composite, SWT.BORDER);
		txtCarparkbak.setText("carpark.bak");
		txtCarparkbak.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button_2 = new Button(composite, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				File f=getSelectedFile();
				if (f==null) {
					return;
				}
				String input = commonui.input("提示", "请输入新建文件夹名称");
				if (StrUtil.isEmpty(input)) {
					return;
				}
				String filePath=f.toString();
				if (!f.isDirectory()) {
					int lastIndexOf = filePath.lastIndexOf("\\");
					filePath=filePath.substring(0, lastIndexOf);
				}
				String path = filePath+"\\"+input;
				System.out.println(path);
				settingService.createServerDirectory(path);
				File element = new File(filePath);
				treeViewer.refresh(element);
				treeViewer.expandToLevel(element, 1);
				treeViewer.setSelection(new TreeSelection(new TreePath(new Object[]{new File(path)})));
			}
		});
		button_2.setText("新建文件夹");
		
		Button button = new Button(composite, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = txtCarparkbak.getText();
				int indexOf = text.indexOf(".bak");
				if (indexOf<=-1) {
					commonui.info("提示", "文件后缀名必须为.bak");
					return;
				}
				TreeSelection selection = (TreeSelection) treeViewer.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof String) {
					commonui.info("提示", "请选择一个文件");
					return;
				}
				String filePath=firstElement.toString();
				if (firstElement instanceof File) {
					File f=(File) firstElement;
					if (!f.isDirectory()) {
						filePath=f.toString().substring(0,f.toString().lastIndexOf("\\"));
					}
				}
				result=filePath+"\\"+txtCarparkbak.getText();
				shell.close();
			}
		});
		button.setText("确定");
		
		Button button_1 = new Button(composite, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		button_1.setText("取消");

	}

	protected File getSelectedFile() {
		TreeSelection selection = (TreeSelection) treeViewer.getSelection();
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof File) {
			File f=(File) firstElement;
			return f;
		}
		return null;
	}
}
