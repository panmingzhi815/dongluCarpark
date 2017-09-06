package com.donglu.carpark.ui.keybord;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.ui.keybord.KeySetting.KeyReleaseTypeEnum;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SWTKeySettingApp {

	protected Shell shell;
	private KeySetting keySetting;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			SWTKeySettingApp window = new SWTKeySettingApp();
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
		shell = new Shell(SWT.CLOSE|SWT.ON_TOP);
		shell.setSize(450, 300);
		shell.setText("按键设置");
		shell.setLayout(new GridLayout(1, false));
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		
		initKeySetting(composite);
		
		
		Composite composite_1 = new Composite(shell, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		Button button_1 = new Button(composite_1, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (KeyReleaseTypeEnum keyReleaseTypeEnum : KeyReleaseTypeEnum.values()) {
					mapText.get(keyReleaseTypeEnum).setText(SWTKeyCode.getKeyText(keyReleaseTypeEnum.defaultKeyCode));
					keySetting.map.put(keyReleaseTypeEnum, keyReleaseTypeEnum.defaultKeyCode);
				}
			}
		});
		button_1.setText("恢复默认");
		
		Button button = new Button(composite_1, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				keySetting.write();
				shell.close();
			}
		});
		button.setText("保存");
		
	}
	Map<KeyReleaseTypeEnum, Text> mapText=new HashMap<>();
	private void initKeySetting(Composite composite) {
		keySetting = KeySetting.read();
		Map<KeyReleaseTypeEnum, Integer> map = keySetting.getMap();
		for (KeyReleaseTypeEnum keyReleaseTypeEnum : KeyReleaseTypeEnum.values()) {
			Label label = new Label(composite, SWT.NONE);
			label.setText(keyReleaseTypeEnum.toString());
			
			Text text = new Text(composite, SWT.BORDER);
			text.setEditable(false);
			GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
			gd_text.widthHint = 80;
			text.setLayoutData(gd_text);
			text.setText(SWTKeyCode.getKeyText(map.get(keyReleaseTypeEnum)));
			text.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					String string = SWTKeyCode.getKeyText(e.keyCode);
					if(string==null||string.contains("未知")||map.values().contains(e.keyCode)){
						text.setText(SWTKeyCode.getKeyText(map.get(keyReleaseTypeEnum)));
					}else{
						text.setText(string);
						map.put(keyReleaseTypeEnum, e.keyCode);
					}
				}
			});
			mapText.put(keyReleaseTypeEnum, text);
		}
	}
}
