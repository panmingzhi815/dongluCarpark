package com.donglu.carpark.ui.view.user;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkMonthlyCharge;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.custom.ScrolledComposite;

public class UserView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_name;
	private Text text_plateNO;
	private Text text_willOverdue;
	private Combo combo_monthlyCharges;
	private Text text_address;

	public UserView(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Group group = new Group(scrolledComposite, SWT.NONE);
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		group.setLayout(new GridLayout(13, false));
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("姓名");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_name = new Text(group, SWT.BORDER);
		text_name.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("车牌");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_plateNO = new Text(group, SWT.BORDER);
		text_plateNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_plateNO.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_5 = new Label(group, SWT.NONE);
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("住址");
		
		text_address = new Text(group, SWT.BORDER);
		text_address.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_address.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("收费标准");
		
		combo_monthlyCharges = new Combo(group, SWT.READ_ONLY);
		combo_monthlyCharges.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo_monthlyCharges = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo_monthlyCharges.widthHint = 80;
		combo_monthlyCharges.setLayoutData(gd_combo_monthlyCharges);
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("即将过期天数");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_willOverdue = new Text(group, SWT.BORDER);
		text_willOverdue.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_willOverdue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("是否过期");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		ComboViewer comboViewer = new ComboViewer(group, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(new String[]{"全部","是","否"});
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 66;
		combo.setLayoutData(gd_combo);
		
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					String text = text_willOverdue.getText();
					int parseInt=0;
					try {
						parseInt = Integer.parseInt(text);
					} catch (NumberFormatException e1) {
					}
					
					String text2 = combo.getText();
					if (text2.equals("全部")) {
						text2=null;
					}
					getPresenter().search(text_name.getText(), text_plateNO.getText(),text_address.getText(),combo_monthlyCharges.getSelectionIndex(),parseInt,text2);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setText("查询");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		scrolledComposite.setContent(group);
		scrolledComposite.setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public UserPresenter getPresenter() {
		return (UserPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}

	public void setMonthlyCharges(List<SingleCarparkMonthlyCharge> listMonthlyCharge) {
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				combo_monthlyCharges.add("全部");
				for (SingleCarparkMonthlyCharge singleCarparkMonthlyCharge : listMonthlyCharge) {
					SingleCarparkCarpark carpark = singleCarparkMonthlyCharge.getCarpark();
					String name=singleCarparkMonthlyCharge.getChargeName()+"-"+carpark.getName();
					combo_monthlyCharges.add(name);
				}
				combo_monthlyCharges.select(0);
			}
		});
	}
}
