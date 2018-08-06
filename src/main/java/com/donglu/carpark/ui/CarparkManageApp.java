package com.donglu.carpark.ui;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;

import com.donglu.carpark.ui.common.AbstractApp;
import com.dongluhitec.card.common.ui.uitl.JFaceUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkModuleEnum;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.google.common.collect.Maps;
import com.google.inject.Inject;



public class CarparkManageApp extends AbstractApp{
	protected Shell shell;
	@Inject
	private CarparkManagePresenter presenter;
	
	private Map<SystemSettingTypeEnum, String> mapSystemSetting=Maps.newHashMap();
	
	Map<SingleCarparkModuleEnum, TabItem> mapModuleToItem=new HashMap<>();
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void open() {
		try {
			long nanoTime = System.nanoTime();
			Display display = Display.getDefault();
			shell = new Shell();
			shell.setSize(1112, 621);
			init();
			createContents();
			shell.open();
			shell.setMaximized(true);
			
			shell.layout();
			System.out.println("界面创建==="+(System.nanoTime()-nanoTime));
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
	}
	
	@Override
	public void openAsyncExec() {

		try {
			long nanoTime = System.nanoTime();
			Display display = Display.getDefault();
			shell = new Shell();
			shell.setSize(896, 621);
			shell.setEnabled(false);
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					init();
					createContents();
					shell.layout();
					shell.setEnabled(true);
				}
			});
			
			shell.open();
			shell.setMaximized(true);
			shell.layout();
			System.out.println("界面创建==="+(System.nanoTime()-nanoTime));
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			System.exit(0);
		}
	
	}
	

	private void init() {
		presenter.setView(this);
		
		for (SystemSettingTypeEnum t : SystemSettingTypeEnum.values()) {
			mapSystemSetting.put(t, t.getDefaultValue());
		}
		presenter.init();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		String dbServerIp = CarparkClientConfig.getInstance().getDbServerIp();
		shell.setText("停车场管理界面("+dbServerIp+")");
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		shell.setImage(JFaceUtil.getImage("carpark_16"));
		shell.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				presenter.systemExit();
			}
		});
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		presenter.getMainPresenter().go(composite);
	}



	public Map<SystemSettingTypeEnum, String> getMapSystemSetting() {
		return mapSystemSetting;
	}

	@Override
	public boolean isOpen() {
		return false;
	}


	@Override
	public Shell getShell() {
		return this.shell;
	}
	
}
