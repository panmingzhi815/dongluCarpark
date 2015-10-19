package com.donglu.carpark.server.imgserver;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.server.CarparkHardwareGuiceModule;
import com.donglu.carpark.server.CarparkServerConfig;
import com.donglu.carpark.server.ServerUI;
import com.donglu.carpark.server.servlet.ImageUploadServlet;
import com.donglu.carpark.server.servlet.ServerServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.CarparkLocalVMServiceProvider;
import com.dongluhitec.card.blservice.HardwareFacility;
import com.dongluhitec.card.common.ui.CommonUIGuiceModule;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.exception.DongluAppException;
import com.dongluhitec.card.domain.util.StrUtil;
import com.dongluhitec.card.hardware.util.HardwareFacilityImpl;
import com.dongluhitec.card.server.ServerUtil;
import com.dongluhitec.card.ui.util.FileUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.wb.swt.SWTResourceManager;

public class ImageServerUI {

	public static final String IMAGE_SAVE_DIRECTORY = "directory";
	protected Shell shell;
	private Text text;

	private Server server;
	@Inject
	private ServerUI ui;

	private final Provider<ImageUploadServlet> imageServletProvider = new Provider<ImageUploadServlet>() {
		@Override
		public ImageUploadServlet get() {
			return new ImageUploadServlet();
		}
	};

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Injector createInjector = Guice.createInjector(new CommonUIGuiceModule());
			ImageServerUI window = createInjector.getInstance(ImageServerUI.class);
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
		shell.setImage(JFaceUtil.getImage("carpark_16"));
		
		Button btnTest = new Button(shell, SWT.NONE);
		btnTest.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ui.open();
			}
		});
		btnTest.setText("配    置");
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		System.exit(0);
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(522, 86);
		shell.setText("图片服务器");
		shell.setLayout(new GridLayout(5, false));

		Label label = new Label(shell, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("图片保存路径");

		text = new Text(shell, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text.setEditable(false);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 214;
		text.setLayoutData(gd_text);
		Object readObject = FileUtils.readObject(IMAGE_SAVE_DIRECTORY);
		text.setText(readObject==null?"":(String) readObject);
		Button button = new Button(shell, SWT.NONE);
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog=new DirectoryDialog(shell,SWT.SINGLE);
				String open = directoryDialog.open();
				FileUtils.writeObject(IMAGE_SAVE_DIRECTORY, open);
				if (StrUtil.isEmpty(open)) {
					return;
				}
				text.setText(open);
			}
		});
		button.setText("...");

		Button btnStart = new Button(shell, SWT.NONE);
		btnStart.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		btnStart.setData("type", "start");
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String data = (String) btnStart.getData("type");
				if (data.equals("start")) {
					startServer();
					btnStart.setText("退    出");
					btnStart.setData("type", "stop");
				}
				if (data.equals("stop")) {
					System.exit(0);
				}
			}
		});
		btnStart.setText("启    动");

	}
	
	public Image getImage(final byte[] smallImage, Label insmallimg, Shell shell) {

		ByteArrayInputStream stream = null;
		try {
			stream = new ByteArrayInputStream(smallImage);
			Image img = new Image(shell.getDisplay(), stream);
			Rectangle rectangle = insmallimg.getBounds();
			ImageData data = img.getImageData().scaledTo(rectangle.width, rectangle.height);
			ImageDescriptor createFromImageData = ImageDescriptor.createFromImageData(data);
			Image createImg = createFromImageData.createImage();
			img.dispose();
			img = null;
			insmallimg.setText("");
			return createImg;
		} catch (Exception e) {
			throw new DongluAppException("图片转换错误", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void startServer() {
		try {
			this.server = new Server(8899);
			ServletHandler servletHandler = new ServletHandler();
			server.setHandler(servletHandler);
			ServerUtil.startServlet("/carparkImage/*", servletHandler, imageServletProvider);
			Provider<ServerServlet> serverServlet = new Provider<ServerServlet>() {
				@Override
				public ServerServlet get() {
					return new ServerServlet();
				}
			};
			ServerUtil.startServlet("/server/*", servletHandler, serverServlet);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
