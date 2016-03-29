package com.donglu.carpark.ui.common;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.donglu.carpark.util.CarparkUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;

public class ImageDialog extends Dialog {

	protected Object result;
	protected Shell shell;
	String img;
	static Image image;
	private CLabel lbl_image;

	/**
	 * 
	 * @param img 进出场记录的图片字段属性
	 */
	public ImageDialog(String img) {
		super(Display.getDefault().getActiveShell());
		setText("图片显示");
		this.img=img;
	}
	/**
	 * 
	 * @param image swt图片
	 */
	private ImageDialog(Image image) {
		super(Display.getDefault().getActiveShell());
		setText("图片显示");
		ImageDialog.image=image;
	}
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		shell.setMaximized(true);
		shell.addShellListener(new ShellAdapter() {

			@Override
			public void shellClosed(ShellEvent e) {
				System.out.println("shell can be close");
				if (!shell.isDisposed()) {
					shell.dispose();
				}
			}
			
		});
		setImage(img);
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
		shell = new Shell(getParent(), SWT.DIALOG_TRIM);
		shell.setText(getText());
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		lbl_image = new CLabel(shell, SWT.NONE);

	}
	private void setImage(String img) {
		CarparkUtils.setBackgroundImage(CarparkUtils.getImageByte(img), lbl_image, img);
	}
	public static void main(String[] args) {
		ImageDialog d=new ImageDialog("");
		d.open();
	}
}
