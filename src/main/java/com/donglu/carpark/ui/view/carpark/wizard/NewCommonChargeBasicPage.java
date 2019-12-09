package com.donglu.carpark.ui.view.carpark.wizard;

import java.util.*;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.donglu.carpark.util.CarparkUtils;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkAcrossDayTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationPrice;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationStandard;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkDurationTypeEnum;
import com.dongluhitec.card.domain.db.singlecarpark.CarparkHolidayTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;
import com.google.common.base.Strings;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * Created with IntelliJ IDEA. User: panmingzhi Date: 13-11-19 Time: 下午4:14 To change this template use File | Settings | File Templates.
 */
public class NewCommonChargeBasicPage extends WizardPage {
	private DataBindingContext m_bindingContext;

	private final NewCommonChargeModel model;
	private Composite container;
	private TabFolder tf;
	private DateTime startTime;
	private DateTime endTime;

	private final Map<String, Boolean> mapAutoCreate=new HashMap<String, Boolean>();
	
	private Composite composite_1;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_freeTime;
	private ComboViewer comboViewer;
	private ComboViewer comboViewer_1;
	private ComboViewer comboViewer_2;
	private ComboViewer comboViewer_3;
	private ComboViewer comboViewer_carType;
	private Button button_6;
	private Text text_4;
	private Button button_1;
	private Combo combo_3;
	private Button button;
	private Combo combo_1;

	public NewCommonChargeBasicPage(NewCommonChargeModel model) {
		super("wizardPage");
		this.model = model;
		setDescription("请输入收费标准详细参数");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.BORDER);
		setControl(container);
		container.setLayout(new GridLayout(1, false));

		composite_1 = new Composite(container, SWT.NONE);
		composite_1.setLayout(new GridLayout(7, false));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		Label label_1 = new Label(composite_1, SWT.RIGHT);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("收  费  编  码");

		text = new Text(composite_1, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 87;
		text.setLayoutData(gd_text);
		new Label(composite_1, SWT.NONE);

		Label label_2 = new Label(composite_1, SWT.RIGHT);
		GridData gd_label_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_label_2.widthHint = 90;
		label_2.setLayoutData(gd_label_2);
		label_2.setText("收  费  名  称");

		text_1 = new Text(composite_1, SWT.BORDER);
		GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_1.widthHint = 88;
		text_1.setLayoutData(gd_text_1);

		Label label_3 = new Label(composite_1, SWT.RIGHT);
		GridData gd_label_3 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_label_3.widthHint = 120;
		label_3.setLayoutData(gd_label_3);
		label_3.setText("跨   天    标   准");

		comboViewer = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setInput(CarparkDurationTypeEnum.values());
		model.setCarparkDurationTypeEnum(CarparkDurationTypeEnum.进场时长);

		Label label_4 = new Label(composite_1, SWT.RIGHT);
		label_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_4.setText("一天最大收费");

		text_2 = new Text(composite_1, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		Label label_13 = new Label(composite_1, SWT.NONE);
		GridData gd_label_13 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label_13.widthHint = 25;
		label_13.setLayoutData(gd_label_13);
		label_13.setText("元");

		Label label_11 = new Label(composite_1, SWT.RIGHT);
		label_11.setText("车  辆  类  型");
		label_11.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		comboViewer_carType = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo_4 = comboViewer_carType.getCombo();
		combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboViewer_carType.setContentProvider(new ArrayContentProvider());
		comboViewer_carType.setLabelProvider(new LabelProvider());
		comboViewer_carType.setInput(model.getCarparkCarTypeList());
		if (model.getCarparkCarType() == null) {
			model.setCarparkCarType(model.getCarparkCarTypeList().get(1));
		}
		button = new Button(composite_1, SWT.CHECK | SWT.RIGHT);
		button.addSelectionListener(new SelectionAdapter() {
			CarparkHolidayTypeEnum type = null;

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (button.getSelection()) {
					if (!StrUtil.isEmpty(type)) {
						model.setCarparkHolidayTypeEnum(type);
					}
				} else {
					type = model.getCarparkHolidayTypeEnum();
					model.setCarparkHolidayTypeEnum(CarparkHolidayTypeEnum.工作日);
				}
			}
		});
		button.setToolTipText("勾选后允许修改收费标准");
		GridData gd_button = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button.widthHint = 90;
		button.setLayoutData(gd_button);
		button.setText("工 作 日 类 型");

		comboViewer_1 = new ComboViewer(composite_1, SWT.READ_ONLY);
		combo_1 = comboViewer_1.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboViewer_1.setContentProvider(new ArrayContentProvider());
		comboViewer_1.setLabelProvider(new LabelProvider());
		comboViewer_1.setInput(CarparkHolidayTypeEnum.values());

		button_1 = new Button(composite_1, SWT.CHECK | SWT.RIGHT);
		button_1.addSelectionListener(new SelectionAdapter() {
			Boolean acrossDayIsFree = false;
			private int freeTime = 0;
			private String freeTimeEnable = "是";

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (button_1.getSelection()) {
					model.setAcrossDayIsFree(acrossDayIsFree);
					model.setFreeTime(freeTime);
					model.setFreeTimeEnable(freeTimeEnable);
				} else {
					freeTimeEnable = model.getFreeTimeEnable();
					freeTime = model.getFreeTime();
					acrossDayIsFree = model.getAcrossDayIsFree();
					model.setFreeTimeEnable("是");
					model.setFreeTime(0);
					model.setAcrossDayIsFree(false);
				}
			}
		});
		GridData gd_button_1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button_1.widthHint = 75;
		button_1.setLayoutData(gd_button_1);
		button_1.setText("免 费 时 长");

		text_freeTime = new Text(composite_1, SWT.BORDER);
		GridData gd_text_3 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text_3.widthHint = 88;
		text_freeTime.setLayoutData(gd_text_3);

		Label label_14 = new Label(composite_1, SWT.NONE);
		label_14.setText("分");

		Label label_8 = new Label(composite_1, SWT.RIGHT);
		label_8.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_8.setText("免费时长收费");

		comboViewer_3 = new ComboViewer(composite_1, SWT.READ_ONLY);
		combo_3 = comboViewer_3.getCombo();
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboViewer_3.setContentProvider(new ArrayContentProvider());
		comboViewer_3.setLabelProvider(new LabelProvider());
		comboViewer_3.setInput(new String[] { "是", "否" });

		button_6 = new Button(composite_1, SWT.CHECK | SWT.RIGHT);
		GridData gd_button_6 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_button_6.widthHint = 90;
		button_6.setLayoutData(gd_button_6);
		button_6.setText("跨天继续免费");
		new Label(composite_1, SWT.NONE);

		Label label_7 = new Label(composite_1, SWT.RIGHT);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("跨天计费时长");

		comboViewer_2 = new ComboViewer(composite_1, SWT.READ_ONLY);
		Combo combo_2 = comboViewer_2.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboViewer_2.setContentProvider(new ArrayContentProvider());
		comboViewer_2.setLabelProvider(new LabelProvider());
		comboViewer_2.setInput(CarparkAcrossDayTypeEnum.values());
		new Label(composite_1, SWT.NONE);

		Label label = new Label(composite_1, SWT.RIGHT);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("跨天额外收费");

		text_4 = new Text(composite_1, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(composite_1, SWT.NONE);
		new Label(composite_1, SWT.NONE);

		Label label_12 = new Label(composite_1, SWT.RIGHT);
		label_12.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_12.setText("收费时段设置");

		Composite composite = new Composite(composite_1, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		composite.setLayout(new GridLayout(4, false));

		startTime = new DateTime(composite, SWT.BORDER | SWT.TIME);
		startTime.setSeconds(0);
		startTime.setMinutes(0);
		startTime.setHours(0);

		endTime = new DateTime(composite, SWT.BORDER | SWT.TIME);
		endTime.setSeconds(0);
		endTime.setMinutes(0);
		endTime.setHours(0);

		Button button_2 = new Button(composite, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CarparkDurationStandard carparkDurationStandard = new CarparkDurationStandard();
//				Date sTime = StrUtil.parseDateTime(new org.joda.time.DateTime(1900, 1, 1, startTime.getHours(), startTime.getMinutes()).toString(StrUtil.DATETIME_PATTERN));
//				Date eTime = StrUtil.parseDateTime(new org.joda.time.DateTime(1900, 1, 1, endTime.getHours(), endTime.getMinutes()).toString(StrUtil.DATETIME_PATTERN));
				Date sTime =new Date(1900, 1, 1, startTime.getHours(), startTime.getMinutes());
				Date eTime=new Date(1900, 1, 1, endTime.getHours(), endTime.getMinutes());
				carparkDurationStandard.setStartTime(sTime);
				carparkDurationStandard.setEndTime(eTime);

				for (int i = 1; i <= 24; i++) {
					CarparkDurationPrice carparkDurationPrice = new CarparkDurationPrice();
					carparkDurationPrice.setDurationLength(i);
					carparkDurationPrice.setDurationLengthPrice(0);
					carparkDurationStandard.addCarparkDurationPriceList(carparkDurationPrice);
				}
				createDurationTab(Arrays.asList(carparkDurationStandard));
			}
		});
		button_2.setText("添加时段");

		Button button_3 = new Button(composite, SWT.NONE);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (tf.getItemCount() == 0)
					return;
				TabItem tabItem = tf.getSelection()[0];
				tabItem.dispose();
			}
		});
		button_3.setText("删除时段");

		tf = new TabFolder(container, SWT.NONE);
		tf.setLayout(new FillLayout());
		tf.setLayoutData(new GridData(GridData.FILL_BOTH));
		createDurationTab(this.model.getCarparkDurationStandards());

		m_bindingContext = initDataBindings();
		WizardPageSupport.create(this, m_bindingContext);
		init();
	}

	/**
	 * 计算时间收费
	 * 
	 * @param startStepTime
	 *            起步时间
	 * @param startStepPrice
	 *            起步金额
	 * @param time
	 *            单位时长
	 * @param price
	 *            单位金额
	 * @param maxPrice
	 *            时段最大收费
	 * @param hour
	 *            时段时间
	 * @return
	 */
	protected List<DurationInfo> getDuratonPriceInfo(int startStepTime, float startStepPrice, int time, float price, float maxPrice, int hour) {
		ArrayList<DurationInfo> list = new ArrayList<>();
		int totalMinute = 60 * hour;
		if (totalMinute <= 0) {
			return list;
		}
		int nowMinute = 0;
		float nowPrice = 0;
		int freeTime = model.getFreeTime();
		if (freeTime > 0) {
			int flag = nowMinute;
			Integer fTime = freeTime;
			nowMinute += fTime;
			DurationInfo info = new DurationInfo();
			info.setTime(nowMinute);

			info.setName(CarparkUtils.getMinuteToTime(flag) + "-" + CarparkUtils.getMinuteToTime(nowMinute));
			info.setPrice(nowPrice);
			list.add(info);
		}
		if (startStepTime > 0) {
			int flag = nowMinute;
			nowMinute += (startStepTime - freeTime);
			nowPrice = startStepPrice;
			DurationInfo info = new DurationInfo();
			info.setTime(nowMinute);
			info.setName(CarparkUtils.getMinuteToTime(flag) + "-" + CarparkUtils.getMinuteToTime(nowMinute));
			info.setPrice(nowPrice);
			list.add(info);
		}
		if (time <= 0) {
			return list;
		}
		while (nowMinute < totalMinute) {
			int flag = nowMinute;
			nowMinute += time;
			nowPrice += price;
			if (maxPrice > 0 && nowPrice > maxPrice) {
				nowPrice = maxPrice;
			}
			DurationInfo info = new DurationInfo();
			info.setTime(nowMinute);
			info.setName(CarparkUtils.getMinuteToTime(flag) + "-" + CarparkUtils.getMinuteToTime(nowMinute));
			info.setPrice(nowPrice);
			list.add(info);
		}
		return list;
	}

	public List<CarparkDurationStandard> getDurationTable() {
		List<CarparkDurationStandard> carparkDurationStandards = new ArrayList<CarparkDurationStandard>();
		TabItem[] items = tf.getItems();
		for (TabItem tabItem : items) {
			CarparkDurationStandard carparkDurationStandard = new CarparkDurationStandard();
			String[] hourPoint = tabItem.getText().split("-");
			carparkDurationStandard.setStartTime(StrUtil.parse(hourPoint[0], "HH:mm"));
			carparkDurationStandard.setEndTime(StrUtil.parse(hourPoint[1], "HH:mm"));

			Composite cp = (Composite) tabItem.getControl();
			Control[] children = cp.getChildren();
			for (Control control : children) {
				Composite cp0 = (Composite) control;
				if (cp0.getData("key") == "composite_start_step") {
					Text startStepPrice = (Text) cp0.getData("startStepPrice");
					Text startStepTime = (Text) cp0.getData("startStepTime");
					Text maxPrice = (Text) cp0.getData("maxPrice");
					carparkDurationStandard.setStartStepPrice(Float.valueOf(startStepPrice.getText()));
					carparkDurationStandard.setStartStepTime(Integer.valueOf(startStepTime.getText()));
					carparkDurationStandard.setMaxPrice(Float.valueOf(maxPrice.getText()));

				}
				if (cp0.getData("key") == "composite_durationConfig") {
					Text unitDurationLength = (Text) cp0.getData("unitDurationLength");
					Text unitDurationPrice = (Text) cp0.getData("unitDurationPrice");
					Text crossDayUnitTimeLength = (Text) cp0.getData("crossDayUnitTimeLength");
					Text crossDayUnitPrice = (Text) cp0.getData("crossDayUnitPrice");
					// int data = (int) cp0.getData("priceSize");

					Object obj = cp0.getData("DurationId");
					carparkDurationStandard.setId(obj == null ? null : (Long) obj);
					Integer valueOf = Float.valueOf(unitDurationLength.getText()).intValue();
					carparkDurationStandard.setUnitDuration(valueOf);
					Float valueOf2 = Float.valueOf(unitDurationPrice.getText());
					carparkDurationStandard.setUnitPrice(valueOf2);
					carparkDurationStandard.setCrossDayUnitDuration(Integer.valueOf(crossDayUnitTimeLength.getText()));
					carparkDurationStandard.setCrossDayPrice(Float.valueOf(crossDayUnitPrice.getText()));
					carparkDurationStandard.setStandardCode(text.getText());
					// for (int i = 0; i < data; i++) {
					// CarparkDurationPrice carparkDurationPrice = new CarparkDurationPrice();
					// int durationLength = i+1;
					// carparkDurationPrice.setDurationLength(durationLength);
					// float durationLengthPrice = ((60*durationLength)/valueOf)*valueOf2;
					// if (carparkDurationStandard.getMaxPrice()!=null&&carparkDurationStandard.getMaxPrice()>0&&durationLengthPrice>carparkDurationStandard.getMaxPrice()) {
					// durationLengthPrice=carparkDurationStandard.getMaxPrice();
					// }
					// carparkDurationPrice.setDurationLengthPrice(durationLengthPrice);
					// carparkDurationStandard.addCarparkDurationPriceList(carparkDurationPrice);
					// }
				}
				if (cp0.getData("key") == "composite_durationPrice") {
					for (int i = 1; i <= 24; i++) {
						Object data = cp0.getData(i + "");
						if (data != null) {
							CarparkDurationPrice carparkDurationPrice = new CarparkDurationPrice();
							carparkDurationPrice.setDurationLength(i);
							carparkDurationPrice.setDurationLengthPrice(Float.valueOf(((Text) data).getText()));
							carparkDurationPrice.setStandardCode(text.getText());
							carparkDurationStandard.addCarparkDurationPriceList(carparkDurationPrice);
						}
					}
				}

				if (cp0.getData("key") == "table") {
					if (!mapAutoCreate.get(carparkDurationStandard.getStartEndLabel())) {
						Table table = (Table) cp0.getData("table");
						TableItem[] items2 = table.getItems();
						for (int i = 0; i < items2.length; i++) {
							TableItem tableItem = items2[i];
							String text2 = tableItem.getText(3);
							System.out.println("tableItem.getText(3)==" + text2);
							if (!StrUtil.isEmpty(text2)) {
								Float valueOf = Float.valueOf(text2);
								CarparkDurationPrice carparkDurationPrice = new CarparkDurationPrice();
								carparkDurationPrice.setDurationLength(i + 1);
								carparkDurationPrice.setDurationLengthPrice(valueOf);
								carparkDurationPrice.setStandardCode(text.getText());
								carparkDurationStandard.addCarparkDurationPriceList(carparkDurationPrice);
							}
						}
					}else{
						List<DurationInfo> duratonPriceInfo = getDuratonPriceInfo(carparkDurationStandard, carparkDurationStandard.getUnitDuration(), carparkDurationStandard.getUnitPrice(), carparkDurationStandard.getMaxPrice(), carparkDurationStandard.getCarparkDurationHoursSize(), true);
						for (int i = 0; i < duratonPriceInfo.size(); i++) {
							CarparkDurationPrice carparkDurationPrice = new CarparkDurationPrice();
							carparkDurationPrice.setDurationLength(i + 1);
							carparkDurationPrice.setDurationLengthPrice(duratonPriceInfo.get(i).getDayDurationPrice());
							carparkDurationPrice.setStandardCode(text.getText());
							carparkDurationStandard.addCarparkDurationPriceList(carparkDurationPrice);
						}
					}
				}
			}
			carparkDurationStandards.add(carparkDurationStandard);

		}
		return carparkDurationStandards;
	}

	public void createDurationTab(List<CarparkDurationStandard> durationStandards) {
		for (CarparkDurationStandard carparkDurationStandard : durationStandards) {
			TabItem tabItem_1 = new TabItem(tf, SWT.NONE);
			String startEndLabel = carparkDurationStandard.getStartEndLabel();
			tabItem_1.setText(startEndLabel);
			mapAutoCreate.put(startEndLabel, true);
			Composite composite_2 = new Composite(tf, SWT.NONE);
			GridLayout gl_composite_2 = new GridLayout(1, false);
			gl_composite_2.verticalSpacing = 0;
			composite_2.setLayout(gl_composite_2);
			tabItem_1.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					mapAutoCreate.remove(startEndLabel);
				}
			});

			Composite composite_3 = new Composite(composite_2, SWT.NONE);
			RowLayout rl_composite_3 = new RowLayout(SWT.HORIZONTAL);
			rl_composite_3.center = true;
			composite_3.setLayout(rl_composite_3);
			composite_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			composite_3.setData("key", "composite_start_step");

			Label lblNewLabel = new Label(composite_3, SWT.NONE);
			lblNewLabel.setText("起步时长");

			Text text_startStepTime = new Text(composite_3, SWT.BORDER);
			text_startStepTime.setLayoutData(new RowData(50, SWT.DEFAULT));
			text_startStepTime.setText(carparkDurationStandard.getStartStepTime() == null ? "0" : carparkDurationStandard.getStartStepTime() + "");
			composite_3.setData("startStepTime", text_startStepTime);
			Label lblNewLabel_3 = new Label(composite_3, SWT.NONE);
			lblNewLabel_3.setText("(分)");

			Label lblNewLabel_1 = new Label(composite_3, SWT.NONE);
			lblNewLabel_1.setText("   起步金额");

			Text text_startStepPrice = new Text(composite_3, SWT.BORDER);
			text_startStepPrice.setLayoutData(new RowData(50, SWT.DEFAULT));
			text_startStepPrice.setText(carparkDurationStandard.getStartStepPrice() == null ? "0" : carparkDurationStandard.getStartStepPrice() + "");
			composite_3.setData("startStepPrice", text_startStepPrice);
			Label lblNewLabel_4 = new Label(composite_3, SWT.NONE);
			lblNewLabel_4.setText("(元)");

			Label lblNewLabel_2 = new Label(composite_3, SWT.NONE);
			lblNewLabel_2.setText("   时段最大收费");

			Text text_mapPrice = new Text(composite_3, SWT.BORDER);
			text_mapPrice.setLayoutData(new RowData(50, SWT.DEFAULT));
			text_mapPrice.setText(carparkDurationStandard.getMaxPrice() == null ? "0" : carparkDurationStandard.getMaxPrice() + "");
			composite_3.setData("maxPrice", text_mapPrice);
			Label label_5 = new Label(composite_3, SWT.NONE);
			label_5.setText("(元)");

			Composite composite_durationConfig = new Composite(composite_2, SWT.NONE);
			RowLayout rl_composite_4 = new RowLayout(SWT.HORIZONTAL);
			rl_composite_4.center = true;
			composite_durationConfig.setLayout(rl_composite_4);
			composite_durationConfig.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			composite_durationConfig.setData("key", "composite_durationConfig");
			composite_durationConfig.setData("DurationId", carparkDurationStandard.getId());
			composite_durationConfig.setData("priceSize", carparkDurationStandard.getCarparkDurationPriceList().size());
			Label lblNewLabel_5 = new Label(composite_durationConfig, SWT.NONE);
			lblNewLabel_5.setText("单位时长");

			Text unitDurationLength = new Text(composite_durationConfig, SWT.BORDER);
			unitDurationLength.setLayoutData(new RowData(50, SWT.DEFAULT));
			unitDurationLength.setText(carparkDurationStandard.getUnitDuration() == null ? "0" : carparkDurationStandard.getUnitDuration() + "");
			composite_durationConfig.setData("unitDurationLength", unitDurationLength);
			Label lblNewLabel_6 = new Label(composite_durationConfig, SWT.NONE);
			lblNewLabel_6.setText("(分)");

			Label lblNewLabel_7 = new Label(composite_durationConfig, SWT.NONE);
			lblNewLabel_7.setText("   单位金额");

			Text unitDurationPrice = new Text(composite_durationConfig, SWT.BORDER);
			unitDurationPrice.setLayoutData(new RowData(50, SWT.DEFAULT));
			unitDurationPrice.setText(carparkDurationStandard.getUnitPrice() == null ? "0" : carparkDurationStandard.getUnitPrice() + "");
			composite_durationConfig.setData("unitDurationPrice", unitDurationPrice);
			Label lblNewLabel_8 = new Label(composite_durationConfig, SWT.NONE);
			lblNewLabel_8.setText("(元)");

			Label lblNewLabel_11 = new Label(composite_durationConfig, SWT.NONE);
			lblNewLabel_11.setText("   跨天单位时长");

			Text crossDayUnitTimeLength = new Text(composite_durationConfig, SWT.BORDER);
			crossDayUnitTimeLength.setLayoutData(new RowData(50, SWT.DEFAULT));
			crossDayUnitTimeLength.setText(carparkDurationStandard.getCrossDayUnitDuration() == null ? "0" : carparkDurationStandard.getCrossDayUnitDuration() + "");
			composite_durationConfig.setData("crossDayUnitTimeLength", crossDayUnitTimeLength);
			Label lblNewLabel_9 = new Label(composite_durationConfig, SWT.NONE);
			lblNewLabel_9.setText("(分)");

			Label lblNewLabel_10 = new Label(composite_durationConfig, SWT.NONE);
			lblNewLabel_10.setText("   跨天单位金额");

			Text crossDayUnitPrice = new Text(composite_durationConfig, SWT.BORDER);
			crossDayUnitPrice.setLayoutData(new RowData(50, SWT.DEFAULT));
			crossDayUnitPrice.setText(carparkDurationStandard.getCrossDayPrice() == null ? "0" : carparkDurationStandard.getCrossDayPrice() + "");
			composite_durationConfig.setData("crossDayUnitPrice", crossDayUnitPrice);
			Label lblNewLabel_12 = new Label(composite_durationConfig, SWT.NONE);
			lblNewLabel_12.setText("(元)");

			Button button_4 = new Button(composite_durationConfig, SWT.NONE);

			button_4.setText("生成");

			Composite composite_6 = new Composite(composite_2, SWT.NONE);
			composite_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			GridLayout gl_composite_6 = new GridLayout(2, false);
			gl_composite_6.verticalSpacing = 0;
			gl_composite_6.marginWidth = 0;
			gl_composite_6.marginHeight = 0;
			composite_6.setLayout(gl_composite_6);

			Label lblNewLabel_14 = new Label(composite_6, SWT.SEPARATOR | SWT.HORIZONTAL);
			lblNewLabel_14.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			Label lblNewLabel_15 = new Label(composite_6, SWT.SHADOW_IN);

			lblNewLabel_15.setText("︾");
			lblNewLabel_15.setBounds(0, 0, 61, 17);

			Composite composite_5 = new Composite(composite_2, SWT.NONE);
			composite_5.setLayout(new FillLayout(SWT.HORIZONTAL));
			composite_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

			TableViewer tableViewer = new TableViewer(composite_5, SWT.BORDER | SWT.FULL_SELECTION);
			Table table = tableViewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			composite_5.setData("key", "table");
			composite_5.setData("table", table);
			table.setData("carparkDurationStandard", carparkDurationStandard);
			TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			TableColumn tableColumn = tableViewerColumn.getColumn();
			tableColumn.setWidth(134);
			tableColumn.setText("停车时长");

			TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
			TableColumn tableColumn_1 = tableViewerColumn_1.getColumn();
			tableColumn_1.setWidth(156);
			tableColumn_1.setText("收费金额");

			TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
			TableColumn tableColumn_2 = tableViewerColumn_2.getColumn();
			tableColumn_2.setWidth(156);
			tableColumn_2.setText("时段时长");

			TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
			TableColumn tableColumn_3 = tableViewerColumn_3.getColumn();
			tableColumn_3.setWidth(156);
			tableColumn_3.setText("时段收费");

			button_4.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					mapAutoCreate.put(startEndLabel, false);
					initHourPrice(text_startStepTime, text_startStepPrice, text_mapPrice, unitDurationLength, unitDurationPrice, table, carparkDurationStandard.getCarparkDurationHoursSize(), true);
					table.setVisible(true);
				}
			});
			lblNewLabel_15.addMouseListener(new MouseAdapter() {
				boolean flag = false;

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					table.setVisible(flag);
					flag = !flag;
					if (flag) {
						lblNewLabel_15.setText("︾");
					} else {
						lblNewLabel_15.setText("︽");
					}
				}
			});
			initHourPrice(text_startStepTime, text_startStepPrice, text_mapPrice, unitDurationLength, unitDurationPrice, table, carparkDurationStandard.getCarparkDurationHoursSize(), false);
			tabItem_1.setControl(composite_2);
			tf.setSelection(tabItem_1);
		}
	}

	private void init() {
		if (Strings.isNullOrEmpty(this.model.getFreeTimeEnable())) {
			this.model.setFreeTimeEnable("是");
		}
		if (this.model.getCarparkDurationTypeEnum() == null) {
			this.model.setCarparkDurationTypeEnum(CarparkDurationTypeEnum.自然天);
		}
		if (this.model.getCarparkHolidayTypeEnum() == null) {
			this.model.setCarparkHolidayTypeEnum(CarparkHolidayTypeEnum.工作日);
		}
		if (this.model.getCarparkCarType() == null && this.model.getCarparkCarTypeList().isEmpty() == false) {
			this.model.setCarparkCarType(this.model.getCarparkCarTypeList().get(0));
		}
		if (this.model.getCarparkAcrossDayTypeEnum() == null) {
			this.model.setCarparkAcrossDayTypeEnum(CarparkAcrossDayTypeEnum.当天时长);
		}
		if (this.model.getOnedayMaxCharge() == 0) {
			this.model.setOnedayMaxCharge(0);
		}
		if (model.getCarparkHolidayTypeEnum().equals(CarparkHolidayTypeEnum.非工作日)) {
			model.setCheckHolidayType(true);
		}
		if (model.getFreeTime() > 0) {
			model.setCheckFreeTime(true);
		}
		if (model.getAcrossDayPrice() == null) {
			model.setAcrossDayPrice(0F);
		}
	}

	@Override
	public NewCommonChargeWizard getWizard() {
		return (NewCommonChargeWizard) super.getWizard();
	}

	/**
	 * @param text_startStepTime
	 * @param text_startStepPrice
	 * @param text_mapPrice
	 * @param text_unitDurationTime
	 * @param text_unitDurationPrice
	 * @param table
	 * @param hourSize
	 * @param isNew
	 */
	private void initHourPrice(Text text_startStepTime, Text text_startStepPrice, Text text_mapPrice, Text text_unitDurationTime, Text text_unitDurationPrice, Table table, int hourSize,
			boolean isNew) {
		TableItem[] items = table.getItems();
		for (TableItem tableItem : items) {
			tableItem.dispose();
		}
		Integer startsStepTime = Integer.valueOf(text_startStepTime.getText());
		Float startsStepPrice = Float.valueOf(text_startStepPrice.getText());
		Integer time = Integer.valueOf(text_unitDurationTime.getText());
		Float price = Float.valueOf(text_unitDurationPrice.getText());
		Float maxPrice = Float.valueOf(text_mapPrice.getText());
		CarparkDurationStandard data = (CarparkDurationStandard) table.getData("carparkDurationStandard");
		List<DurationInfo> list = getDuratonPriceInfo(startsStepTime, startsStepPrice, time, price, maxPrice, hourSize);
		List<DurationInfo> listDay = getDuratonPriceInfo(data, time, price, maxPrice, hourSize, isNew);
		for (int i = 0; i < list.size(); i++) {
			DurationInfo info = list.get(i);
			TableItem ti = new TableItem(table, SWT.NONE);
			String[] strings = new String[4];
			strings[0] = info.getName();
			strings[1] = info.getPrice() + "";
			ti.setText(strings);
		}
		for (int i = 0; i < listDay.size(); i++) {
			if (listDay.size() > i) {
				TableItem item = null;
				if (table.getItemCount()<=i) {
					item=new TableItem(table, 0);
				}else{
					item = table.getItem(i);
				}
				DurationInfo durationInfo = listDay.get(i);
				String s = durationInfo.getDayDurationName();
				String p = durationInfo.getDayDurationPrice() + "";
				item.setText(2, s);
				item.setText(3, p);
				Text t = new Text(table, 0);
				TableEditor edit = new TableEditor(table);
				edit.grabHorizontal = true;
				t.setText(p);
				edit.setEditor(t, item, 3);
				t.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent e) {
						mapAutoCreate.put(data.getStartEndLabel(),false);
						edit.getItem().setText(3, t.getText());
					}
				});
				item.addDisposeListener(new DisposeListener() {
					@Override
					public void widgetDisposed(DisposeEvent e) {
						t.dispose();
						edit.dispose();
					}
				});
				item.addListener(SWT.SELECTED, new Listener() {

					@Override
					public void handleEvent(Event event) {
						System.out.println(t.getText());
					}
				});
			}
		}
	}

	private List<DurationInfo> getDuratonPriceInfo(CarparkDurationStandard data, Integer time, Float price, Float maxPrice, int hour, boolean isNew) {
		ArrayList<DurationInfo> list = new ArrayList<>();
		int totalMinute = 60 * hour;
		if (totalMinute <= 0 || time == 0) {
			return list;
		}
		int nowMinute = 0;
		float nowPrice = 0;
		for (int i = 0; i < totalMinute / time; i++) {
			int flag = nowMinute;
			DurationInfo info = new DurationInfo();
			nowMinute += time;
			nowPrice += price;
			info.setTime(nowMinute);
			info.setDayDurationName(CarparkUtils.getMinuteToTime(flag) + "-" + CarparkUtils.getMinuteToTime(nowMinute));
			float dayDurationPrice = nowPrice;
			if (maxPrice > 0) {
				dayDurationPrice = nowPrice > maxPrice ? maxPrice : nowPrice;
			}
			info.setDayDurationPrice(dayDurationPrice);
			if (data.getId() != null && !isNew) {
				List<CarparkDurationPrice> carparkDurationPriceList = data.getCarparkDurationPriceList();
				if (!StrUtil.isEmpty(carparkDurationPriceList)) {
					info.setDayDurationPrice(carparkDurationPriceList.get(i).getDurationLengthPrice());
				}
			}
			list.add(info);
		}
		return list;
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextTextObserveWidget_1 = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue codeModelObserveValue = BeanProperties.value("code").observe(model);
		bindingContext.bindValue(observeTextTextObserveWidget_1, codeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_1ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_1);
		IObservableValue nameModelObserveValue = BeanProperties.value("name").observe(model);
		bindingContext.bindValue(observeTextText_1ObserveWidget, nameModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer = ViewerProperties.singleSelection().observe(comboViewer);
		IObservableValue observe_durationType_model = BeansObservables.observeValue(model, "carparkDurationTypeEnum");
		bindingContext.bindValue(observeSingleSelectionComboViewer, observe_durationType_model, null, null);
		//
		IObservableValue observeTextText_2ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_2);
		IObservableValue onedayMaxChargeModelObserveValue = BeanProperties.value("onedayMaxCharge").observe(model);
		bindingContext.bindValue(observeTextText_2ObserveWidget, onedayMaxChargeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_3ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_freeTime);
		IObservableValue freeTimeModelObserveValue = BeanProperties.value("freeTime").observe(model);
		bindingContext.bindValue(observeTextText_3ObserveWidget, freeTimeModelObserveValue, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_1 = ViewerProperties.singleSelection().observe(comboViewer_1);
		IObservableValue observe_holidayType_model = BeansObservables.observeValue(model, "carparkHolidayTypeEnum");
		bindingContext.bindValue(observeSingleSelectionComboViewer_1, observe_holidayType_model, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_2 = ViewerProperties.singleSelection().observe(comboViewer_2);
		IObservableValue observe_crossDayChargeType_model = BeansObservables.observeValue(model, "carparkAcrossDayTypeEnum");
		bindingContext.bindValue(observeSingleSelectionComboViewer_2, observe_crossDayChargeType_model, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_3 = ViewerProperties.singleSelection().observe(comboViewer_3);
		IObservableValue observe_enableCharge_model = BeansObservables.observeValue(model, "freeTimeEnable");
		bindingContext.bindValue(observeSingleSelectionComboViewer_3, observe_enableCharge_model, null, null);
		//
		IObservableValue observeSingleSelectionComboViewer_4 = ViewerProperties.singleSelection().observe(comboViewer_carType);
		IObservableValue observe_carType_model = BeansObservables.observeValue(model, "carparkCarType");
		bindingContext.bindValue(observeSingleSelectionComboViewer_4, observe_carType_model, null, null);
		//
		IObservableValue observeSelectionButton_6ObserveWidget = WidgetProperties.selection().observe(button_6);
		IObservableValue acrossDayIsFreeModelObserveValue = BeanProperties.value("acrossDayIsFree").observe(model);
		bindingContext.bindValue(observeSelectionButton_6ObserveWidget, acrossDayIsFreeModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButton_1ObserveWidget = WidgetProperties.selection().observe(button_1);
		IObservableValue checkFreeTimeModelObserveValue = BeanProperties.value("checkFreeTime").observe(model);
		bindingContext.bindValue(observeSelectionButton_1ObserveWidget, checkFreeTimeModelObserveValue, null, null);
		//
		IObservableValue observeEditableText_freeTimeObserveWidget = WidgetProperties.editable().observe(text_freeTime);
		bindingContext.bindValue(observeEditableText_freeTimeObserveWidget, checkFreeTimeModelObserveValue, null, null);
		//
		IObservableValue observeEnabledCombo_3ObserveWidget = WidgetProperties.enabled().observe(combo_3);
		bindingContext.bindValue(observeEnabledCombo_3ObserveWidget, checkFreeTimeModelObserveValue, null, null);
		//
		IObservableValue observeEnabledButton_6ObserveWidget = WidgetProperties.enabled().observe(button_6);
		bindingContext.bindValue(observeEnabledButton_6ObserveWidget, checkFreeTimeModelObserveValue, null, null);
		//
		IObservableValue observeSelectionButtonObserveWidget = WidgetProperties.selection().observe(button);
		IObservableValue checkHolidayTypeModelObserveValue = BeanProperties.value("checkHolidayType").observe(model);
		bindingContext.bindValue(observeSelectionButtonObserveWidget, checkHolidayTypeModelObserveValue, null, null);
		//
		IObservableValue observeEnabledCombo_1ObserveWidget = WidgetProperties.enabled().observe(combo_1);
		bindingContext.bindValue(observeEnabledCombo_1ObserveWidget, checkHolidayTypeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_4ObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_4);
		IObservableValue acrossDayPriceModelObserveValue = BeanProperties.value("acrossDayPrice").observe(model);
		bindingContext.bindValue(observeTextText_4ObserveWidget, acrossDayPriceModelObserveValue, null, null);
		//
		return bindingContext;
	}

	public Map<String, Boolean> getMapAutoCreate() {
		return mapAutoCreate;
	}
}
