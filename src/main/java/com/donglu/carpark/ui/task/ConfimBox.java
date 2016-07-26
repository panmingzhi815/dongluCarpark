package com.donglu.carpark.ui.task;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ConfimBox{
	protected Boolean result;
	protected Shell shell;
	ConfimDialog confimDialog;
	private String title;
	private String message;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ConfimBox(String title,String message) {
		this.title = title;
		this.message = message;
	}

	public ConfimBox(String message) {
		this.title ="确认提示";
		this.message = message;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Boolean open() {
		try {
			createContents();
			int i=0;
			while (result==null&&i<600) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
				if (i>=600) {
					if (confimDialog!=null) {
						confimDialog.close();
					}
					result=false;
				}
			}
			return result;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		Runnable runnable = new Runnable() {
			public void run() {
				confimDialog = new ConfimDialog(title, message);
				result = confimDialog.open();
			}
		};
		Display.getDefault().asyncExec(runnable);
	}
}
