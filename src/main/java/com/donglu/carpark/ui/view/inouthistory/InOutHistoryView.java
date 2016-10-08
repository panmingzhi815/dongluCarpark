package com.donglu.carpark.ui.view.inouthistory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ConstUtil;
import com.dongluhitec.card.domain.db.singlecarpark.SystemUserTypeEnum;
import com.google.common.util.concurrent.RateLimiter;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkCarpark;
import com.dongluhitec.card.domain.db.singlecarpark.SingleCarparkSystemUser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.widgets.DateTime;


public class InOutHistoryView extends Composite implements View{
	private Presenter presenter;
	private Composite listComposite;
	private Text text_plateNO;
	private Text text_userName;
	private Combo combo_carType;
	private Combo combo_inorout;
	private Text text_inDevice;
	private Text text_outDevice;
	private Text text_returnAccount;
	private InOutHistoryModel model;
	private ComboViewer comboViewer;
	private ComboViewer comboViewer_1;
	
	private RateLimiter rateLimiter = RateLimiter.create(2);
	private Text text_1;
	private DateChooserCombo dateChooserCombo_outstart;
	private DateChooserCombo dateChooserCombo_outend;
	private DateTime dateTime_outstart;
	private DateTime dateTime_outend;

	public InOutHistoryView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		
		Group group = new Group(scrolledComposite, SWT.NONE);
		group.setText("查询");
		group.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridLayout gl_group = new GridLayout(13, false);
		gl_group.verticalSpacing = 10;
		gl_group.horizontalSpacing = 15;
		group.setLayout(gl_group);
		
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("车      牌");
		label.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_plateNO = new Text(group, SWT.BORDER);
		text_plateNO.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 71;
		text_plateNO.setLayoutData(gd_text);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("用      户");
		label_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		text_userName = new Text(group, SWT.BORDER);
		text_userName.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_userName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("进场时间");
		label_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Composite composite = new Composite(group, SWT.NONE);
		GridLayout gl_composite = new GridLayout(5, false);
		gl_composite.marginWidth = 0;
		gl_composite.verticalSpacing = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 0;
		composite.setLayout(gl_composite);
		
		DateChooserCombo dateChooserCombo_instart = new DateChooserCombo(composite, SWT.BORDER);
		dateChooserCombo_instart.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		DateTime dateTime_instart = new DateTime(composite, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime_instart.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime_instart.setHours(0);
		dateTime_instart.setMinutes(0);
		
		Label label_9 = new Label(composite, SWT.NONE);
		label_9.setText("-");
		label_9.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		DateChooserCombo dateChooserCombo_inend = new DateChooserCombo(composite, SWT.BORDER);
		dateChooserCombo_inend.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		DateTime dateTime_inend = new DateTime(composite, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime_inend.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime_inend.setHours(23);
		dateTime_inend.setMinutes(59);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("进场设备");
		
		text_inDevice = new Text(group, SWT.BORDER);
		text_inDevice.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_inDevice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_3.setText("操  作  员");
		label_3.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		
		comboViewer_1 = new ComboViewer(group, SWT.READ_ONLY);
		Combo combo_1 = comboViewer_1.getCombo();
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		
		combo_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_7 = new Label(group, SWT.NONE);
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("停  车  场");
		
		comboViewer = new ComboViewer(group, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_combo.widthHint = 62;
		combo.setLayoutData(gd_combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		Button button = new Button(group, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				float shouldMoney=0;
				try {
					shouldMoney=Float.valueOf(text_1.getText());
				} catch (NumberFormatException e2) {
				}
				try {
					if (!rateLimiter.tryAcquire()) {
						return;
					}
					SingleCarparkCarpark singleCarparkCarpark = model.getListCarpark().get(comboViewer.getCombo().getSelectionIndex());
					String text = combo_1.getText();
					if (text.equals("全部")) {
						text=null;
					}
					Date inStart=null;
					Date inEnd=null;
					Date outStart=null;
					Date outEnd=null;
					
					Date value = dateChooserCombo_instart.getValue();
					if (value!=null) {
						int hours = dateTime_instart.getHours();
						int minutes = dateTime_instart.getMinutes();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(value);
						calendar.set(Calendar.HOUR_OF_DAY, hours);
						calendar.set(Calendar.MINUTE, minutes);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						inStart=calendar.getTime();
					}
					value = dateChooserCombo_inend.getValue();
					if (value!=null) {
						int hours = dateTime_inend.getHours();
						int minutes = dateTime_inend.getMinutes();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(value);
						calendar.set(Calendar.HOUR_OF_DAY, hours);
						calendar.set(Calendar.MINUTE, minutes);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						inEnd=calendar.getTime();
					}
					value = dateChooserCombo_outstart.getValue();
					if (value!=null) {
						int hours = dateTime_outstart.getHours();
						int minutes = dateTime_outstart.getMinutes();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(value);
						calendar.set(Calendar.HOUR_OF_DAY, hours);
						calendar.set(Calendar.MINUTE, minutes);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						outStart=calendar.getTime();
					}
					value = dateChooserCombo_outend.getValue();
					if (value!=null) {
						int hours = dateTime_outend.getHours();
						int minutes = dateTime_outend.getMinutes();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(value);
						calendar.set(Calendar.HOUR_OF_DAY, hours);
						calendar.set(Calendar.MINUTE, minutes);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);
						outEnd=calendar.getTime();
					}
					getPresenter().search(text_plateNO.getText(),text_userName.getText(),inStart,inEnd,outStart,outEnd,
							text,combo_carType.getText(),combo_inorout.getText(),text_inDevice.getText(),
							text_outDevice.getText(),text_returnAccount.getText(),singleCarparkCarpark,shouldMoney);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		button.setText("查询");
		button.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("车辆类型");
		label_4.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		ComboViewer comboViewer_carType = new ComboViewer(group, SWT.READ_ONLY);
		comboViewer_carType.setContentProvider(new ArrayContentProvider());
		comboViewer_carType.setLabelProvider(new LabelProvider());
		comboViewer_carType.setInput(new String[]{"全部","临时车","固定车"});
		combo_carType = comboViewer_carType.getCombo();
		combo_carType.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_carType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_carType.select(0);
		
		Label label_5 = new Label(group, SWT.NONE);
		label_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_5.setText("是否出场");
		label_5.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		ComboViewer comboViewer_inout = new ComboViewer(group, SWT.READ_ONLY);
		comboViewer_inout.setContentProvider(new ArrayContentProvider());
		comboViewer_inout.setLabelProvider(new LabelProvider());
		comboViewer_inout.setInput(new String[]{"全部","是","否"});
		combo_inorout = comboViewer_inout.getCombo();
		combo_inorout.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		combo_inorout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		combo_inorout.select(0);
		
		Label label_6 = new Label(group, SWT.NONE);
		label_6.setText("出场时间");
		label_6.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		Composite composite_1 = new Composite(group, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(5, false);
		gl_composite_1.horizontalSpacing = 0;
		gl_composite_1.marginHeight = 0;
		gl_composite_1.verticalSpacing = 0;
		gl_composite_1.marginWidth = 0;
		composite_1.setLayout(gl_composite_1);
		
		dateChooserCombo_outstart = new DateChooserCombo(composite_1, SWT.BORDER);
		dateChooserCombo_outstart.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		dateTime_outstart = new DateTime(composite_1, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime_outstart.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime_outstart.setHours(0);
		dateTime_outstart.setMinutes(0);
		
		Label label_10 = new Label(composite_1, SWT.NONE);
		label_10.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_10.setText("-");
		
		dateChooserCombo_outend = new DateChooserCombo(composite_1, SWT.BORDER);
		dateChooserCombo_outend.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		
		dateTime_outend = new DateTime(composite_1, SWT.BORDER | SWT.TIME | SWT.SHORT);
		dateTime_outend.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		dateTime_outend.setHours(23);
		dateTime_outend.setMinutes(59);
		
		Label lblNewLabel_1 = new Label(group, SWT.NONE);
		lblNewLabel_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("出场设备");
		
		text_outDevice = new Text(group, SWT.BORDER);
		text_outDevice.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_outDevice.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		Label lblNewLabel_2 = new Label(group, SWT.NONE);
		lblNewLabel_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		lblNewLabel_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("归账编号");
		
		text_returnAccount = new Text(group, SWT.BORDER);
		text_returnAccount.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_returnAccount.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Label label_8 = new Label(group, SWT.NONE);
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		label_8.setText("应收金额");
		
		text_1 = new Text(group, SWT.BORDER);
		text_1.setToolTipText("查询大于或等于输入金额的数据");
		text_1.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		Button button_2 = new Button(group, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getPresenter().exportSearch();
			}
		});
		button_2.setFont(SWTResourceManager.getFont("微软雅黑", 12, SWT.NORMAL));
		button_2.setText("导出");
		scrolledComposite.setContent(group);
		scrolledComposite.setMinSize(group.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		listComposite = new Composite(this, SWT.NONE);
		listComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
	}

	public InOutHistoryView(Composite c, int style, InOutHistoryModel model) {
		this(c, style);
		this.model=model;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public InOutHistoryPresenter getPresenter() {
		return (InOutHistoryPresenter) presenter;
	}

	public Composite getListComposite() {
		return listComposite;
	}

	public void setCarparkList(List<SingleCarparkCarpark> findAllCarpark) {
		Display.getDefault().asyncExec(()->{
			comboViewer.setInput(findAllCarpark);
			comboViewer.getCombo().select(0);
		});
	}

	public void setComboValue(List<SingleCarparkSystemUser> findAll) {
		comboViewer_1.setInput(findAll);
		comboViewer_1.getCombo().select(0);
		if (ConstUtil.getLevel()<2) {
			comboViewer_1.getCombo().setEnabled(false);
		}
	}
}
