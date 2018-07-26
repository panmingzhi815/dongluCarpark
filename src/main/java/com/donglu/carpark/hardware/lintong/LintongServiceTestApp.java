package com.donglu.carpark.hardware.lintong;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class LintongServiceTestApp {

	protected Shell shell;
	private Text txtbdw;
	private Text text_1;
	private Text text_2;
	LintongServiceImpl lintongService = new LintongServiceImpl();
	private Text txtGbk;
	private Button button_1;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LintongServiceTestApp window = new LintongServiceTestApp();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("临潼油库接口测试");
		
		Label label = new Label(shell, SWT.NONE);
		label.setBounds(31, 25, 61, 17);
		label.setText("车牌：");
		
		txtbdw = new Text(shell, SWT.BORDER);
		txtbdw.setText("粤BD021W");
		txtbdw.setBounds(98, 19, 129, 23);
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setBounds(31, 55, 61, 17);
		label_1.setText("检验时间");
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(98, 52, 129, 23);
		
		Label label_2 = new Label(shell, SWT.NONE);
		label_2.setBounds(31, 90, 61, 17);
		label_2.setText("输出字节");
		
		text_2 = new Text(shell, SWT.BORDER);
		text_2.setText("32");
		text_2.setBounds(98, 84, 73, 23);
		
		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LintongServiceImpl.charsetName=txtGbk.getText();
				LintongServiceImpl.LINTONG_RETURN_SIZE=Integer.valueOf(text_2.getText());
				Date plate = lintongService.getInDateByPlate(txtbdw.getText());
				text_1.setText(StrUtil.formatDateTime(plate));
			}
		});
		button.setBounds(98, 126, 80, 27);
		button.setText("测试");
		
		txtGbk = new Text(shell, SWT.BORDER);
		txtGbk.setText("GBK");
		txtGbk.setBounds(233, 19, 73, 23);
		
		button_1 = new Button(shell, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				lintongService.deletePlate(txtbdw.getText());
			}
		});
		button_1.setBounds(184, 126, 80, 27);
		button_1.setText("删除");
		
	}
}
