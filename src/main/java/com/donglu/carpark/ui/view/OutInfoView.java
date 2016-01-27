package com.donglu.carpark.ui.view;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

import com.donglu.carpark.model.CarparkMainModel;
import com.donglu.carpark.ui.CarparkMainApp;
import com.donglu.carpark.ui.common.ImageDialog;
import com.donglu.carpark.ui.common.Presenter;
import com.donglu.carpark.ui.common.View;
import com.dongluhitec.card.common.ui.CommonUIFacility;
import com.dongluhitec.card.domain.db.singlecarpark.SystemSettingTypeEnum;
import com.dongluhitec.card.domain.util.StrUtil;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class OutInfoView extends Composite implements View{
	@SuppressWarnings("unused")
	private DataBindingContext m_bindingContext;
	private Presenter presenter;
	private CarparkMainModel model;
	private CommonUIFacility commonui;
	private Text text_plateno;
	private Text text_time;
	private Button btn_check;
	private CLabel lbl_smallImg;
	private CLabel lbl_bigImg;
	private Button button;

	public OutInfoView(Composite parent, int style) {
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
		text_plateno.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_plateno.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_plateno.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_plateno.setTextLimit(8);
		Label label_14 = new Label(composite_22, SWT.NONE);
		label_14.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_14.setText("出场时间");
		label_14.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));

		text_time = new Text(composite_22, SWT.BORDER);
		text_time.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		text_time.setFont(SWTResourceManager.getFont("微软雅黑", 11, SWT.BOLD));
		text_time.setEditable(false);
		text_time.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite composite_23 = new Composite(composite_21, SWT.NONE);
		composite_23.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_composite_23 = new GridLayout(1, false);
		gl_composite_23.verticalSpacing = 2;
		gl_composite_23.marginHeight = 2;
		gl_composite_23.horizontalSpacing = 0;
		composite_23.setLayout(gl_composite_23);

		btn_check = new Button(composite_23, SWT.NONE);
		btn_check.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String outShowPlateNO = model.getOutShowPlateNO();
				if (StrUtil.isEmpty(outShowPlateNO)) {
					return;
				}
				if (outShowPlateNO.length() > 8) {
					commonui.info("车牌错误", "请输入正确的车牌");
					return;
				}
				model.setOutCheckClick(false);
			}
		});
		GridData gd_btn_check = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btn_check.exclude = false;
		if (!Boolean.valueOf(CarparkMainApp.mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认) == null ? SystemSettingTypeEnum.固定车出场确认.getDefaultValue() : CarparkMainApp.mapSystemSetting.get(SystemSettingTypeEnum.固定车出场确认))) {
			gd_btn_check.exclude = true;
		}
		btn_check.setLayoutData(gd_btn_check);
		btn_check.setText("出场确认");
		btn_check.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		btn_check.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		
		button = new Button(composite_23, SWT.NONE);
		button.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (StrUtil.isEmpty(text_plateno.getText())) {
					commonui.info("提示", "请先输入车牌");
					return;
				}
				model.setBtnClick(false);
				model.setDisContinue(true);
				String data = model.getSearchPlateNo();
				String bigImg = model.getSearchBigImage();
				String smallImg = model.getSearchSmallImage();
				getPresenter().showManualSearch(data, bigImg, smallImg);
			}
		});
		button.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD));
		button.setText("人工查找");

		Composite composite_24 = new Composite(composite_21, SWT.BORDER);
		GridData gd_composite_24 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_24.widthHint = 120;
		composite_24.setLayoutData(gd_composite_24);
		composite_24.setLayout(new FillLayout(SWT.HORIZONTAL));

		lbl_smallImg = new CLabel(composite_24, SWT.NONE);
		lbl_smallImg.setText("出场车牌");
		lbl_smallImg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lbl_smallImg.setFont(SWTResourceManager.getFont("微软雅黑", 13, SWT.BOLD));
		lbl_smallImg.setAlignment(SWT.CENTER);

		Composite composite_25 = new Composite(composite_20, SWT.BORDER);
		composite_25.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_25.setLayout(new FillLayout(SWT.HORIZONTAL));

		lbl_bigImg = new CLabel(composite_25, SWT.NONE);
		lbl_bigImg.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Image lastImage = (Image)lbl_bigImg.getData("lastImage");
				ImageDialog imageDialog = new ImageDialog(lastImage);
				imageDialog.open();
			}
		});
		lbl_bigImg.setText("出场车牌");
		lbl_bigImg.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		lbl_bigImg.setFont(SWTResourceManager.getFont("微软雅黑", 23, SWT.BOLD));
		lbl_bigImg.setAlignment(SWT.CENTER);
		m_bindingContext = initDataBindings();
	}

	public OutInfoView(Composite c, int style, CarparkMainModel model,CommonUIFacility commonui) {
		super(c, style);
		this.model=model;
		this.commonui=commonui;
		createView();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
	}

	@Override
	public OutInfoPresenter getPresenter() {
		return (OutInfoPresenter) presenter;
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
		IObservableValue observeEnabledBtn_checkObserveWidget = WidgetProperties.enabled().observe(btn_check);
		IObservableValue inCheckClickModelObserveValue = BeanProperties.value("inCheckClick").observe(model);
		bindingContext.bindValue(observeEnabledBtn_checkObserveWidget, inCheckClickModelObserveValue, null, null);
		//
		IObservableValue observeEditableText_platenoObserveWidget = WidgetProperties.editable().observe(text_plateno);
		IObservableValue outPlateNOEditableModelObserveValue = BeanProperties.value("outPlateNOEditable").observe(model);
		bindingContext.bindValue(observeEditableText_platenoObserveWidget, outPlateNOEditableModelObserveValue, null, null);
		//
		IObservableValue observeTextText_platenoObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_plateno);
		IObservableValue outShowPlateNOModelObserveValue = BeanProperties.value("outShowPlateNO").observe(model);
		bindingContext.bindValue(observeTextText_platenoObserveWidget, outShowPlateNOModelObserveValue, null, null);
		//
		IObservableValue observeTextText_timeObserveWidget = WidgetProperties.text(SWT.Modify).observe(text_time);
		IObservableValue outShowTimeModelObserveValue = BeanProperties.value("outShowTime").observe(model);
		bindingContext.bindValue(observeTextText_timeObserveWidget, outShowTimeModelObserveValue, null, null);
		//
		IObservableValue observeEnabledButtonObserveWidget = WidgetProperties.enabled().observe(button);
		IObservableValue handSearchModelObserveValue = BeanProperties.value("handSearch").observe(model);
		bindingContext.bindValue(observeEnabledButtonObserveWidget, handSearchModelObserveValue, null, null);
		//
		return bindingContext;
	}
}
