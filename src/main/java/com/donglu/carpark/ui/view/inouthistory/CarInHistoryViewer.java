package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;

import com.donglu.carpark.ui.common.AbstractView;
import com.donglu.carpark.ui.common.Presenter;

import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CarInHistoryViewer extends AbstractView {

	private Composite composite_listView;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Combo combo_carType;
	private Button button;
	private Label lbl_msg;

	public CarInHistoryViewer(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Group group = new Group(this, SWT.NONE);
		group.setLayout(new GridLayout(11, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("车辆类型");
		
		combo_carType = new Combo(group, SWT.READ_ONLY);
		combo_carType.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		combo_carType.setItems(new String[] {"全部", "固定车", "临时车"});
		combo_carType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_carType.select(2);
		
		Label label = new Label(group, SWT.NONE);
		label.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("停留时间(分钟)");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_1.setText("60");
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_1.widthHint = 40;
		text_1.setLayoutData(gd_text_1);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("-");
		
		text_2 = new Text(group, SWT.BORDER);
		text_2.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text_2.setText("1440");
		GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_2.widthHint = 40;
		text_2.setLayoutData(gd_text_2);
		
		button = new Button(group, SWT.CHECK);
		button.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		button.setSelection(true);
		button.setText("保持更新");
		
		text = new Text(group, SWT.BORDER);
		text.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		text.setText("5");
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 36;
		text.setLayoutData(gd_text);
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		label_3.setText("秒");
		
		Button btnNewButton = new Button(group, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().setSetting(new int[]{combo_carType.getSelectionIndex(),Integer.valueOf(text_1.getText()),Integer.valueOf(text_2.getText()),button.getSelection()?0:1,Integer.valueOf(text.getText())});
			}
		});
		btnNewButton.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 12, SWT.NORMAL));
		btnNewButton.setText("刷新设置");
		new Label(group, SWT.NONE);
		
		composite_listView = new Composite(this, SWT.NONE);
		composite_listView.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_listView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		lbl_msg = new Label(composite, SWT.NONE);
		lbl_msg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}
	
	@Override
	public CarInHistoryPresenter getPresenter() {
		return (CarInHistoryPresenter) super.getPresenter();
	}

	public Composite getComposite_listView() {
		return composite_listView;
	}

	public void setSetting(int[] setting) {
		combo_carType.select(setting[0]);
		text_1.setText(setting[1]+"");
		text_2.setText(setting[2]+"");
		button.setSelection(setting[3]==0);
		text.setText(setting[4]+"");
	}
	public void setMsg(String msg){
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				lbl_msg.setText(msg);
			}
		});
	}
}
