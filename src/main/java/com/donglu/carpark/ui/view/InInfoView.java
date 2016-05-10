package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.donglu.carpark.util.ConstUtil;
import com.donglu.carpark.util.ImageUtils;
import com.donglu.carpark.util.TextUtils;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;

public class InInfoView extends Composite implements View{
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	private Presenter presenter;
	private CarparkMainModel model;
	private Text text_plateno;
	private Text text_time;
	private Button btn_check;
	private CLabel lbl_smallImg;
	private CLabel lbl_bigImg;

	public InInfoView(Composite parent, int style) {
		super(parent, style);
		createView();
	}

	/**
	 * 
	 */
	public void createView() {
		setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite_20 = new Composite(this, SWT.NONE);
		GridLayout gl_composite_20 = new GridLayout(1, false);
		gl_composite_20.verticalSpacing = 0;
		gl_composite_20.marginWidth = 0;
		gl_composite_20.marginHeight = 0;
		gl_composite_20.horizontalSpacing = 0;
		composite_20.setLayout(gl_composite_20);

		Composite composite_21 = new Composite(composite_20, SWT.NONE);
		GridData gd_composite_21 = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_composite_21.heightHint = 75;
		gd_composite_21.widthHint = 419;
		composite_21.setLayoutData(gd_composite_21);
		composite_21.setLayout(new GridLayout(3, false));

		Composite composite_22 = new Composite(composite_21, SWT.NONE);
		GridData gd_composite_22 = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_composite_22.widthHint = 140;
		composite_22.setLayoutData(gd_composite_22);
		composite_22.setLayout(new GridLayout(2, false));

		Label label_7 = new Label(composite_22, SWT.NONE);
		label_7.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_7.setText("车牌号码");
		label_7.setForeground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_FOREGROUND));
		label_7.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		label_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));

		text_plateno = new Text(composite_22, SWT.BORDER);
		text_plateno.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				String text = text_plateno.getText();
				if (text_plateno.getEditable()&&!StrUtil.isEmpty(text)) {
					text_plateno.setText(text.split("-")[0]);
				}
			}
		});
		text_plateno.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_plateno.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_plateno.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_plateno.setData(ConstUtil.NO_CHANGE_FOCUS, "true");
		TextUtils.createPlateNOAutoCompleteField(text_plateno);
		Label label_14 = new Label(composite_22, SWT.NONE);
		label_14.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_14.setText("进场时间");
		label_14.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_time = new Text(composite_22, SWT.BORDER);
		text_time.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_time.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_time.setEditable(false);
		text_time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite composite_23 = new Composite(composite_21, SWT.NONE);
		composite_23.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		composite_23.setLayout(new GridLayout(1, false));

		btn_check = new Button(composite_23, SWT.NONE);
		btn_check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String inShowPlateNO = model.getInShowPlateNO();
				getPresenter().check(inShowPlateNO);
			}
		});
		GridData gd_btn_check = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btn_check.exclude = false;
		Map<SystemSettingTypeEnum, String> mapSystemSetting = model.getMapSystemSetting();
		if (Boolean
				.valueOf(mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认) == null ? SystemSettingTypeEnum.临时车入场是否确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.临时车入场是否确认))
				|| Boolean.valueOf(
						mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认) == null ? SystemSettingTypeEnum.固定车入场是否确认.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.固定车入场是否确认))
				|| !Boolean.valueOf(
						mapSystemSetting.get(SystemSettingTypeEnum.是否允许无牌车进) == null ? SystemSettingTypeEnum.是否允许无牌车进.getDefaultValue() : mapSystemSetting.get(SystemSettingTypeEnum.是否允许无牌车进))) {
			
		} else {
			gd_btn_check.exclude = true;
		}
		btn_check.setLayoutData(gd_btn_check);
		btn_check.setText("入场确认");
		btn_check.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		btn_check.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));

		Composite composite_24 = new Composite(composite_21, SWT.BORDER);
		GridData gd_composite_24 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_24.widthHint = 120;
		composite_24.setLayoutData(gd_composite_24);
		composite_24.setLayout(new FillLayout(SWT.HORIZONTAL));

		lbl_smallImg = new CLabel(composite_24, SWT.NONE);
		lbl_smallImg.setToolTipText("进场小图");
		lbl_smallImg.setText("进场车牌");
		lbl_smallImg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lbl_smallImg.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.BOLD));
		lbl_smallImg.setAlignment(SWT.CENTER);

		Composite composite_25 = new Composite(composite_20, SWT.BORDER);
		composite_25.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_25.setLayout(new FillLayout(SWT.HORIZONTAL));

		lbl_bigImg = new CLabel(composite_25, SWT.NONE);
		lbl_bigImg.setToolTipText("进场大图");
		lbl_bigImg.setText("入场车牌");
		lbl_bigImg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lbl_bigImg.setFont(SWTResourceManager.getFont("微软雅黑", 23, SWT.BOLD));
		lbl_bigImg.setAlignment(SWT.CENTER);
		lbl_bigImg.setData("imageType", "big");
		m_bindingContext = initDataBindings();
	}

	public InInfoView(Composite c, int style, CarparkMainModel model) {
		super(c, style);
		this.model=model;
		createView();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public InInfoPresenter getPresenter() {
		return (InInfoPresenter) presenter;
	}

	public CLabel getLbl_smallImg() {
		return lbl_smallImg;
	}

	public CLabel getLbl_bigImg() {
		return lbl_bigImg;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue observeTextText_timeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_time);
		IObservableValue inShowTimeModelObserveValue = BeanProperties.value("inShowTime").observe(model);
		bindingContext.bindValue(observeTextText_timeObserveWidget, inShowTimeModelObserveValue, null, null);
		//
		IObservableValue observeTextText_platenoObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_plateno);
		IObservableValue inShowPlateNOModelObserveValue = BeanProperties.value("inShowPlateNO").observe(model);
		bindingContext.bindValue(observeTextText_platenoObserveWidget, inShowPlateNOModelObserveValue, null, null);
		//
		IObservableValue observeEditableText_platenoObserveWidget = WidgetProperties.editable().observe(text_plateno);
		IObservableValue inCheckClickModelObserveValue = BeanProperties.value("inCheckClick").observe(model);
		bindingContext.bindValue(observeEditableText_platenoObserveWidget, inCheckClickModelObserveValue, null, null);
		//
		IObservableValue observeEnabledBtn_checkObserveWidget = WidgetProperties.enabled().observe(btn_check);
		bindingContext.bindValue(observeEnabledBtn_checkObserveWidget, inCheckClickModelObserveValue, null, null);
		//
		ImageUtils.bindImageWithBig(model, "inShowBigImg", "inBigImageName", lbl_bigImg);
		ImageUtils.bindImageWithBig(model, "inShowSmallImg", lbl_smallImg);
		return bindingContext;
	}
}
