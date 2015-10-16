package com.donglu.carpark.server.imgserver;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.server.servlet.ImageServlet;
import com.dongluhitec.card.server.ServerUtil;
import com.google.inject.Provider;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ImageServerUI {

	protected Shell shell;
	private Text text;

	private Server server;

	private final Provider<ImageServlet> imageServletProvider = new Provider<ImageServlet>() {
		@Override
		public ImageServlet get() {

			return new ImageServlet();
		}
	};

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ImageServerUI window = new ImageServerUI();
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
		shell.setSize(370, 300);
		shell.setText("图片服务器");
		shell.setLayout(new GridLayout(4, false));

		Label label = new Label(shell, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("保存路径");

		text = new Text(shell, SWT.BORDER);
		text.setEditable(false);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 214;
		text.setLayoutData(gd_text);

		Button button = new Button(shell, SWT.NONE);
		button.setText("...");

		Button button_1 = new Button(shell, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				startServer();
			}
		});
		button_1.setText("启动");
		new Label(shell, SWT.NONE);
		
		Button button_2 = new Button(shell, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				test();
			}
		});
		button_2.setText("测试");
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

	}

	protected void test() {
		try {
			byte[] readAllBytes = Files.readAllBytes(Paths.get("D:/20150925131528451_粤BD021W_small.jpg"));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	protected void startServer() {
		try {
			this.server = new Server(8889);
			ServletHandler servletHandler = new ServletHandler();
			server.setHandler(servletHandler);
			ServerUtil.startServlet("/carparkImage/*", servletHandler, imageServletProvider);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
